package com.imooc.distribution.service.imple;

import com.alibaba.fastjson.JSON;
import com.imooc.distribution.constant.Constant;
import com.imooc.distribution.exception.CouponException;
import com.imooc.distribution.constant.CouponStatus;
import com.imooc.distribution.pojo.Coupon;
import com.imooc.distribution.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis相关接口操作实现
 */
@Service
@Slf4j
public class RedisServiceImple implements RedisService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisServiceImple(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 根据userId和缓存状态找到缓存的优惠券数据
     *
     * @param userId 用户Id
     * @param status 缓存状态 {@link com.imooc.distribution.constant.CouponStatus}
     * @return {@link Coupon} 注意,可能返回null,代表从没有过记录
     */
    @Override
    public List<Coupon> getCacheCoupons(Long userId, Integer status) {
        log.info("Get Coupons From Cache: {}, {}", userId, status);
        String redisKey = status2RedisKey(status, userId);
        List<String> values = redisTemplate.opsForHash().values(redisKey) //List<Object>
                .stream() //Stream<Object>
                .map(o -> Objects.toString(o, null)) //Stream(String)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(values)) {
            saveEmptyCouponListToCache(userId,
                    Collections.singletonList(status));
            return Collections.emptyList();
        }
        return values.stream().map(v -> JSON.parseObject(v, Coupon.class))
                .collect(Collectors.toList());
    }

    /**
     * 保存空的优惠券列表到缓存中  (避免缓存穿透)
     * 目的: 避免缓存穿透
     *
     * @param userId 用户id
     * @param status 优惠券状态列表
     */
    @Override
    @SuppressWarnings("all")
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save Empty List to Cache For User: {}, Status: {}",
                userId, JSON.toJSONString(status));

        //key 是Coupon_id , value是序列化的Coupon
        Map<String, String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));

        //用户优惠券缓存信息
        //KV
        //K: status -> redisKey
        //V: {coupon_id, 序列化的 distribution}


        //使用SessionCallback 把数据命令放入Redis 的pipeline
        //pipeline(可以让我们一次性执行多个命令,命令执行完后一次性全部返回给我们,Redis是单线程,执行一条返回一条)
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                status.forEach(s -> {
                    String redisKey = status2RedisKey(s, userId);
                    redisOperations.opsForHash().putAll(redisKey, invalidCouponMap);
                });
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
    }

    /**
     * 尝试从Cache中获取优惠券码
     *
     * @param templateId 优惠券模板主键
     * @return 优惠券码
     */
    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, templateId.toString());
        //优惠券码不存在顺序关系,left或者right没有关系
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);
        log.info("Acquire Coupon code: {},{},{}", templateId, redisKey, couponCode);
        return couponCode;
    }

    /**
     * 将优惠券保存到Cache中
     *
     * @param userId  用户id
     * @param coupons {@link Coupon}
     * @param status  优惠券状态
     * @return 保存成功个数
     */
    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status)
            throws CouponException {
        log.info("Add Coupon TO Cache: {},{},{}",
                userId, JSON.toJSONString(coupons), status);
        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus) {
            case USABLE:
                result = addCouponToCacheForUsable(userId, coupons);
                break;
            case USED:
                result = addCouponToCacheForUsed(userId, coupons);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpire(userId, coupons);
                break;
        }
        return result;
    }

    /**
     * 新增加优惠券到Cache中  USEABLE
     *
     * @param userId
     * @param coupons
     * @return
     */
    private Integer addCouponToCacheForUsable(Long userId, List<Coupon> coupons) {
        // 如果 status 是 USBALE, 代表是新增加的优惠券
        // 只会影响一个Cache ,USER_COUPON_USABLE
        log.debug("Add Coupon To Cache For Usable");
        Map<String, String> needCacheToUsable = new HashMap<>();
        coupons.forEach(coupon -> {
            needCacheToUsable.put(coupon.getId().toString(),
                    JSON.toJSONString(coupon));
        });
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.USER_COUPON_USABLE, userId);
        redisTemplate.opsForHash().putAll(redisKey, needCacheToUsable);
        log.info("Add {} Coupon To Cache :{},{}",
                needCacheToUsable.size(), userId, redisKey);

        //设置过期时间
        redisTemplate.expire(
                redisKey,
                getRandExpirationTime(1, 2),
                TimeUnit.SECONDS
        );
        return needCacheToUsable.size();
    }

    /**
     * 将已使用的优惠券加入到Cache中  USED
     *
     * @param userId
     * @param coupons
     * @return
     */
    @SuppressWarnings("all")
    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> coupons) throws CouponException {
        //如果status 是USED, 代表用户操作是使用当前的优惠券,影响到两个Cache
        //USED , USABLE
        Map<String, String> needCacheFOrUsed = new HashMap<>(coupons.size());

        String redisKeyUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        String redisKeyUsed = status2RedisKey(CouponStatus.USED.getCode(), userId);

        //获取当前用户可用的优惠券
        List<Coupon> usableCache = getCacheCoupons(userId, CouponStatus.USABLE.getCode());
        //当前可用的优惠券个数一个大于1  (因为之前没有优惠券信息我们会塞入一个无效优惠券)
        assert usableCache.size() > coupons.size();

        coupons.forEach(coupon -> {
            needCacheFOrUsed.put(
                    coupon.getId().toString(),
                    JSON.toJSONString(coupon)
            );
        });

        //校验当前优惠券中的参数是否与Cache中相匹配
        List<Integer> usableIds = usableCache.stream().map(Coupon::getId)
                .collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream().map(Coupon::getId)
                .collect(Collectors.toList());
        if (!CollectionUtils.isSubCollection(paramIds, usableIds)) { //isSubCollection(A,B) A->B 子集
            log.error("CurCoupon Is Not Equal ToCache:{},{},{}",
                    userId,
                    JSON.toJSONString(usableIds),
                    JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupon Is Not Equal ToCache!");
        }

        List<String> needCleanKey = paramIds.stream().map(e -> e.toString())
                .collect(Collectors.toList());
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //1.已使用的优惠券 Cache 缓存添加
                redisOperations.opsForHash().putAll(
                        redisKeyUsed, needCacheFOrUsed
                );
                //2.可用的优惠券 Cache 缓存清理
                redisOperations.opsForHash().delete(
                        redisKeyUsable, usableCache.toArray());
                //3.重置过期时间
                redisOperations.expire(
                        redisKeyUsable,
                        getRandExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                redisOperations.expire(
                        redisKeyUsed,
                        getRandExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                return null;
            }
        };
        log.info("Pipeline Exe Result:{}",
                JSON.toJSONString(sessionCallback));
        return coupons.size();
    }

    /**
     * 将已过期的优惠券加入 Cache 中 Expired
     *
     * @param userId
     * @param coupons
     * @return
     */
    @SuppressWarnings("all")
    private Integer addCouponToCacheForExpire(Long userId, List<Coupon> coupons)
            throws CouponException {
        //如果status 是Expire, 代表优惠券已过期
        //影响两个Cache USABLE , EXPIRE
        Map<String, String> needCacheForExpire = new HashMap<>(coupons.size());

        String redisUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );
        String redisExpire = status2RedisKey(
                CouponStatus.EXPIRED.getCode(), userId
        );

        List<Coupon> usableCache = getCacheCoupons(
                userId, CouponStatus.USABLE.getCode()
        );

        //当前可用优惠券一定大于Coupon
        assert usableCache.size() > coupons.size();

        coupons.forEach(e -> {
            needCacheForExpire.put(
                    e.getId().toString(),
                    JSON.toJSONString(e)
            );
        });
        //校验当前优惠券参数是否与Cache中相匹配
        List<Integer> usableIds = usableCache.stream()
                .map(Coupon::getId).collect(Collectors.toList());

        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());

        if (!CollectionUtils.isSubCollection(paramIds, usableIds)) {
            log.error("Coupon is not Equal ToCache:{},{},{}",
                    userId,
                    JSON.toJSONString(paramIds),
                    JSON.toJSONString(usableIds));
            throw new CouponException("Coupon is not ToCache");
        }

        List<String> needCleanKey = paramIds.stream()
                .map(e -> e.toString()).collect(Collectors.toList());

        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //1.已过期的优惠券 Expire 添加
                redisOperations.opsForHash().putAll(redisExpire, needCacheForExpire);

                //2.可用优惠券 Usable 清理
                redisOperations.opsForHash().delete(redisUsable, needCleanKey);

                //3.重置过期时间
                redisOperations.expire(
                        redisExpire,
                        getRandExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                redisOperations.expire(
                        redisUsable,
                        getRandExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}",
                redisTemplate.executePipelined(sessionCallback));

        return coupons.size();
    }

    /**
     * 根据status获取RedisKey
     *
     * @param status
     * @param userId
     * @return
     */
    private String status2RedisKey(Integer status, Long userId) {
        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus) {
            case USABLE:
                redisKey = String.format("%s%S",
                        Constant.RedisPrefix.USER_COUPON_USABLE, userId);
                break;
            case USED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_USED, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_EXPIRED, userId);
        }
        return redisKey;
    }

    /**
     * 获取一个随机的过期时间
     * 缓存雪崩: key 在同一时间失效
     *
     * @param min 最小小时数
     * @param max 最大小时数
     * @return 返回[min, max] 之间的随机秒数
     */
    private Long getRandExpirationTime(Integer min, Integer max) {
        return RandomUtils.nextLong(
                min * 60 * 60,
                max * 60 * 60
        );
    }
}
