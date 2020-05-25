package com.imooc.distribution.dao;

import com.imooc.distribution.constant.CouponStatus;
import com.imooc.distribution.pojo.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * distribution dao层接口定义
 */

public interface CouponDao extends JpaRepository<Coupon, Integer> {

    /**
     * 根据userId + 状态查找优惠券记录
     * where userId= ... and status=
     * @param userId
     * @param status
     * @return
     */
    List<Coupon> findByUserIdAndStatus(Long userId, CouponStatus status);
}
