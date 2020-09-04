package com.imooc.distribution.service.imple;

import com.alibaba.fastjson.JSON;
import com.imooc.distribution.constant.Constant;
import com.imooc.distribution.exception.CouponException;
import com.imooc.distribution.vo.CouponTemplateSDK;
import com.imooc.distribution.vo.GoodInfo;
import com.imooc.distribution.vo.SettlementInfo;
import com.imooc.distribution.constant.CouponStatus;
import com.imooc.distribution.dao.CouponDao;
import com.imooc.distribution.feign.SettlementClient;
import com.imooc.distribution.feign.TemplateClient;
import com.imooc.distribution.pojo.Coupon;
import com.imooc.distribution.service.RedisService;
import com.imooc.distribution.service.UserService;
import com.imooc.distribution.vo.AcquireTemplateRequest;
import com.imooc.distribution.vo.CouponClassify;
import com.imooc.distribution.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户服务相关接口实现
 */

@Slf4j
@Service
public class UserServiceImple implements UserService {

    /*Coupon Dao*/
    private final CouponDao couponDao;
    /*Redis 服务*/
    private final RedisService redisService;
    /*kafka 服务*/
    private final KafkaTemplate kafkaTemplate;
    /*模板微服务客户端*/
    private final TemplateClient templateClient;
    /*结算微服务客户端*/
    private final SettlementClient settlementClient;

    @Autowired
    public UserServiceImple(CouponDao couponDao, RedisService redisService, KafkaTemplate kafkaTemplate, TemplateClient templateClient, SettlementClient settlementClient) {
        this.couponDao = couponDao;
        this.redisService = redisService;
        this.kafkaTemplate = kafkaTemplate;
        this.templateClient = templateClient;
        this.settlementClient = settlementClient;
    }

    /**
     * 根据用户 id 和状态查询优惠券记录
     *
     * @param userId 用户id
     * @param status 优惠券状态
     * @return {@link Coupon} s
     */
    @Override
    public List<Coupon> findCouponsByStatus(Long userId, Integer status)
            throws CouponException {

        List<Coupon> curCache = redisService.getCacheCoupons(userId, status);
        List<Coupon> preTarget;

        if (CollectionUtils.isNotEmpty(curCache)) {
            log.debug("distribution cache is not empty: {},{} ", userId, status);
            preTarget = curCache;
        } else {
            log.debug("distribution cache is empty, get distribution from db: {},{}",
                    userId, status);
            List<Coupon> dbCoupons = couponDao.findAllByUserIdAndStatus(
                    userId, CouponStatus.of(status)
            );
            //如果数据库中没有记录,直接返回就可以了,Cache中已经加入了一个无效优惠券
            if (CollectionUtils.isEmpty(dbCoupons)) {
                log.debug("distribution is empty from db: {},{}",
                        userId, status);
                return dbCoupons;
            }

            //填充dbCoupon中的 templateSDK字段
            Map<Integer, CouponTemplateSDK> id2Template =
                    templateClient.findIds2TemplateSDK(
                            dbCoupons.stream().map(Coupon::getId)
                                    .collect(Collectors.toList())
                    ).getData();
            dbCoupons.forEach(e -> e.setTemplateSDK(
                    id2Template.get(e.getTemplateId()))
            );
            //数据库中存在记录
            preTarget = dbCoupons;
            //将记录写入Cache
            redisService.addCouponToCache(userId, preTarget, status);
        }

        //将无效优惠券剔除
        preTarget = preTarget.stream()
                .filter(c -> c.getId() != -1)
                .collect(Collectors.toList());
        //如果当前获得的是可用优惠券,需要对过期优惠券的延迟处理处理
        if (CouponStatus.of(status) == CouponStatus.USABLE) {
            CouponClassify classify = CouponClassify.classify(preTarget);
            //如果已过期不为空,需要延迟处理
            if (CollectionUtils.isNotEmpty(classify.getExpired())) {
                log.info("Add Expired Coupon to Cache findCouponByStatus:{},{}",
                        userId, status);
                redisService.addCouponToCache(
                        userId, classify.getExpired(),
                        CouponStatus.EXPIRED.getCode());
                //发送到kafka做异步处理
                kafkaTemplate.send(
                        Constant.TOPIC,
                        JSON.toJSONString(new CouponKafkaMessage(
                                CouponStatus.EXPIRED.getCode(),
                                classify.getExpired().stream()
                                        .map(Coupon::getId).collect(Collectors.toList())
                        ))
                );
            }
            return classify.getUsable();
        }
        return preTarget;
    }

    /**
     * 根据用户id 查找当前可以领取的优惠券模板
     *
     * @param userId 用户id
     * @return {@link CouponTemplateSDK}
     */
    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {

        Long curTime = new Date().getTime();
        List<CouponTemplateSDK> templateSDKS =
                templateClient.findAllUsableTemplate().getData();

        log.debug("Find All Template (From TemplateClient) count: {}", templateSDKS.size());

        //过滤过期的优惠券模板  //因为设置的定时任务是延迟过期策略
        templateSDKS = templateSDKS.stream().filter(
                t -> t.getRule().getExpiration().getDeadLine() > curTime
        ).collect(Collectors.toList());

        log.info("Find Usable Template Count:{}", templateSDKS.size());

        //key 是 TemplateId
        //value 中 left是 Template limitation ,right是优惠券模板
        Map<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template =
                new HashMap<>(templateSDKS.size());
        templateSDKS.forEach(t ->
                limit2Template.put(t.getId(),
                        Pair.of(t.getRule().getLimitation(), t)
                )
        );

        List<CouponTemplateSDK> result =
                new ArrayList<>(limit2Template.size());
        List<Coupon> userUsableCoupons =
                findCouponsByStatus(userId, CouponStatus.USABLE.getCode());

        log.debug("Current User has Usable Coupons:{}", userId,
                userUsableCoupons.size());

        //key是templateId
        Map<Integer, List<Coupon>> template2IdCoupons = userUsableCoupons
                .stream().collect(Collectors.groupingBy(Coupon::getTemplateId));

        //根据Template 的 Rule判断是否可用领取优惠券模板
        limit2Template.forEach((k, v) -> {
            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();

            if (template2IdCoupons.containsKey(k)
                    && template2IdCoupons.get(k).size() >= limitation) {
                return;
            }
            result.add(templateSDK);
        });

        return result;
    }

    /**
     * 用户领取优惠券
     * 1.从TemplateClient拿到对应的优惠券模板, 并检查是否过期
     * 2.根据limitation 判断用户可用领取
     * 3.save to db
     * 4.填充CouponTemplateSDK
     * 5.save to Cache
     *
     * @param request {@link AcquireTemplateRequest}
     * @return {@link Coupon}
     */
    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {
        Map<Integer, CouponTemplateSDK> id2Template =
                templateClient.findIds2TemplateSDK(
                        Collections.singletonList(request.getTemplateSDK().getId())
                ).getData();

        //判断优惠券模板是否存在
        if (id2Template.size() <= 0) {
            log.error("Can not Acquire Template From TemplateClient: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Can not Acquire Template From TemplateClient");
        }

        //用户是否可以领取这张优惠券
        List<Coupon> userUsableCoupons = findCouponsByStatus(
                request.getUserId(), CouponStatus.USABLE.getCode());
        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons.stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));

        if (templateId2Coupons.containsKey(request.getTemplateSDK().getId())
                && templateId2Coupons.get(request.getTemplateSDK().getId()).size() >=
                request.getTemplateSDK().getRule().getLimitation()) {
            log.error("Exceed Template Assign Limitation: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Exceed Template Assign Limitation");
        }

        //尝试去获取优惠券码
        String couponCode = redisService.tryToAcquireCouponCodeFromCache(
                request.getTemplateSDK().getId()
        );
        if (StringUtils.isEmpty(couponCode)) {
            log.error("couponCode is empty:{}",
                    request.getTemplateSDK().getId());
            throw new CouponException("couponCode is empty");
        }

        Coupon newCoupon = new Coupon(
                request.getTemplateSDK().getId(),
                request.getUserId(),
                couponCode,
                CouponStatus.USABLE
        );
        newCoupon = couponDao.save(newCoupon);

        //填充Coupon 对象的 TemplateSDK , 一定要注意在缓存之前放入
        newCoupon.setTemplateSDK(request.getTemplateSDK());

        //放入缓存中
        redisService.addCouponToCache(
                request.getUserId(),
                Collections.singletonList(newCoupon),
                CouponStatus.USABLE.getCode()
        );

        return newCoupon;
    }

    /**
     * 结算(核销)优惠券
     * 这里需要注意 ,规则相关处理需要由 Settlement 系统去做 ,当前系统仅仅做
     * 业务处理过程(校验过程)
     *
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     * @throws CouponException
     */
    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {
        //当没有传递优惠券时, 直接返回商品总价
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos =
                info.getCouponAndTemplateInfo();
        if (CollectionUtils.isEmpty(ctInfos)) {
            log.info("Empty Coupon for Settlement");
            double goodSum = 0.0;

            for (GoodInfo gi : info.getGoodInfos()) {
                goodSum += gi.getPrice() * gi.getCount();
            }

            //没有优惠券也就不存在优惠券核销,SettlementInfo 其他字段不需要修改
            info.setCost(retain2Decimals(goodSum));

        }

        //校验传递的优惠券是否是自己的
        List<Coupon> coupons = findCouponsByStatus(
                info.getUserId(), CouponStatus.USABLE.getCode()
        );
        Map<Integer, Coupon> id2Coupons = coupons.stream()
                .collect(Collectors.toMap(Coupon::getId, Function.identity()));

        if (MapUtils.isEmpty(id2Coupons) || !CollectionUtils.isSubCollection(
                ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId)
                        .collect(Collectors.toList()), id2Coupons.keySet()
        )) {
            log.info("{}", id2Coupons.keySet());
            log.info("{}", ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId)
                    .collect(Collectors.toList()));
            log.error("User Coupon has some Problem " +
                    ",It is Not SubCollection is Coupons!");
            throw new CouponException("User Coupon has some Problem " +
                    ",It is Not SubCollection is Coupons!");
        }

        log.debug("Current Settlement Coupons Is User's:{}", ctInfos.size());

        List<Coupon> settleCoupons = new ArrayList<>(ctInfos.size());
        ctInfos.forEach(i -> settleCoupons.add(id2Coupons.get(i.getId())));

        //通过结算微服务获取结算信息
        SettlementInfo processedInfo =
                settlementClient.computeRule(info).getData();

        if (processedInfo.getEmploy() && CollectionUtils.isNotEmpty(
                processedInfo.getCouponAndTemplateInfo()
        )) {
            log.info("Settle User Coupon:{}", info.getUserId(),
                    JSON.toJSONString(settleCoupons));
            //更新缓存
            redisService.addCouponToCache(
                    info.getUserId(),
                    settleCoupons,
                    CouponStatus.USED.getCode()
            );
            //更新db
            kafkaTemplate.send(
                    Constant.TOPIC,
                    JSON.toJSONString(new CouponKafkaMessage(
                            CouponStatus.USED.getCode(),
                            settleCoupons.stream().map(Coupon::getId)
                                    .collect(Collectors.toList())
                    ))
            );
        }
        return processedInfo;
    }

    /**
     * 保留两位小数
     *
     * @param value
     * @return
     */
    private double retain2Decimals(double value) {

        //BigDecimal.ROUND_HALF_UP 代表四舍五入
        return new BigDecimal(value)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }
}
