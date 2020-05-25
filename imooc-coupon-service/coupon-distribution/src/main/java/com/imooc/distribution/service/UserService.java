package com.imooc.distribution.service;

import com.imooc.distribution.exception.CouponException;
import com.imooc.distribution.vo.CouponTemplateSDK;
import com.imooc.distribution.vo.SettlementInfo;
import com.imooc.distribution.pojo.Coupon;
import com.imooc.distribution.vo.AcquireTemplateRequest;

import java.util.List;

/**
 * 用户服务相关接口定义
 * 1.用户三类状态优惠券信息展示服务
 * 2.查看用户当前可领取的优惠券模板    distribution-template
 * 3.用户领取优惠券服务
 * 4.用户消费优惠券服务  -distribution-settlement 微服务配合实现
 */

public interface UserService {

    /**
     * 根据用户 id 和状态查询优惠券记录
     *
     * @param userId 用户id
     * @param status 优惠券状态
     * @return {@link Coupon} s
     */
    List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException;

    /**
     * 根据用户id 查找当前可以领取的优惠券模板
     *
     * @param userId 用户id
     * @return {@link CouponTemplateSDK}
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException;

    /**
     * 用户领取优惠券
     *
     * @param request {@link AcquireTemplateRequest}
     * @return {@link Coupon}
     */
    Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException;

    /**
     * 结算(核销)优惠券
     *
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     * @throws CouponException
     */
    SettlementInfo settlement(SettlementInfo info) throws CouponException;
}

