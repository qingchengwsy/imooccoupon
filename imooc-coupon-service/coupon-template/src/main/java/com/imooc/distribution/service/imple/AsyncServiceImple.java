package com.imooc.distribution.service.imple;

import com.google.common.base.Stopwatch;
import com.imooc.distribution.constant.Constant;
import com.imooc.distribution.dao.CouponTemplateDao;
import com.imooc.distribution.pojo.CouponTemplate;
import com.imooc.distribution.service.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 异步服务接口实现
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class AsyncServiceImple implements AsyncService {

    @Autowired
    private CouponTemplateDao dao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 根据模板异步创建优惠券码
     *
     * @param template {@link CouponTemplate} 模板实体
     */
    @Async("getAsyncExecutor")
    public void asyncConstructCouponByTemplate(CouponTemplate template) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Set<String> result = buildCouponCode(template);
        /* imooc_coupon_template_code_1*/
        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, template.getId().toString());
        log.info("Push Coupon To Redis: {}",redisTemplate.opsForList().rightPushAll(redisKey, result));

        template.setAvailable(true);
        dao.save(template);

        stopwatch.stop();
        log.info("Construct Coupon By Template Cost: {}", stopwatch.elapsed(TimeUnit.MILLISECONDS));

        //TODO 发送短信或者邮件通知优惠券模板可用
        log.info("CouponTemplate({}) Is Available!", template.getId());
    }

    /**
     * 构建优惠券码
     * 优惠券码(对应每一张优惠券, 18位)
     * 前四位: 产品线 + 类型
     * 中间六位: 日期随机(190101)
     * 后八位: 0-9 随机数组成
     *
     * @param template {@link CouponTemplate} 实体类
     * @return Set<String> 与 template.count 相同个数的优惠券码
     */
    private Set<String> buildCouponCode(CouponTemplate template) {
        Stopwatch stopwatch = Stopwatch.createStarted(); //计时器
        Set<String> result = new HashSet<>(template.getCount());
        //前四位
        String prifix4 = template.getProductLine().getCode().toString()
                + template.getCategory().getCode();
        String date = new SimpleDateFormat("yyMMdd").format(template.getCreateTime());
        for (int i = 0; i != template.getCount(); ++i) {
            result.add(prifix4 + buildCouponCodeSuffix14(date));
        }
        //Set保证不允许重复,   需要进行判断   for自增,while还要求个数.size(),效率更快
        while (result.size() < template.getCount()) {
            result.add(prifix4 + buildCouponCodeSuffix14(date));
        }
        assert result.size() == template.getCount();
        stopwatch.stop();
        log.info("Build Coupon Code Cost: {}ms",
                stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }

    /**
     * 构造优惠券码的后14位
     *
     * @param date 创建优惠券的日期
     * @return 14 位优惠券码
     */
    private String buildCouponCodeSuffix14(String date) {
        char[] bases = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        //中间六位
        //date.chars() 把date转为IntStream数据流, mapToObj x->y 转变
        List<Character> chars = date.chars().mapToObj(e -> (char) e).collect(Collectors.toList()); //转为char集合
        Collections.shuffle(chars);  //洗牌算法
        String mid6 = chars.stream().map(Object::toString).collect(Collectors.joining()); //在转会String
        //后八位,  第一位不为零
        String suffix8 = RandomStringUtils.random(1, bases)
                + RandomStringUtils.randomNumeric(7);
        return mid6 + suffix8;
    }
}
