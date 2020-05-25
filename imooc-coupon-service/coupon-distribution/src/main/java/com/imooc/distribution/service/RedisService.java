package com.imooc.distribution.service;

import com.imooc.distribution.exception.CouponException;
import com.imooc.distribution.pojo.Coupon;

import java.util.List;

/**
 * Redis 相关操作服务接口定义
 * 1.用户的三个状态优惠券 Cache操作
 * 2.优惠券模板生成的优惠券码 Cache操作
 */

public interface RedisService {


    /**
     * 根据userId和缓存状态找到缓存的优惠券数据
     *
     * @param userId 用户Id
     * @param status 缓存状态 {@link com.imooc.distribution.constant.CouponStatus}
     * @return {@link Coupon} 注意,可能返回null,代表从没有过记录
     */
    List<Coupon> getCacheCoupons(Long userId, Integer status);


    /**
     * 保存空的优惠券列表到缓存中  (避免缓存穿透)
     *
     * @param userId 用户id
     * @param status 优惠券状态列表
     */
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);

    /**
     * 尝试从Cache中获取优惠券码
     *
     * @param templateId 优惠券模板主键
     * @return 优惠券码
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);

    /**
     * 将优惠券保存到Cache中
     *
     * @param userId  用户id
     * @param coupons {@link Coupon}
     * @param status  优惠券状态
     * @return 保存成功个数
     */
    Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status)throws CouponException;
}
