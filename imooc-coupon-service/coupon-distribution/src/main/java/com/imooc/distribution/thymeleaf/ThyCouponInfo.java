package com.imooc.distribution.thymeleaf;

import com.imooc.distribution.pojo.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;

/**
 * <h1>用户优惠券信息</h1>
 * Created by Qinyi.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class ThyCouponInfo {

    private Integer id;

    /** 关联优惠券模板的主键 */
    private Integer templateId;

    /** 领取用户 */
    private Long userId;

    /** 优惠券码 */
    private String couponCode;

    /** 领取时间 */
    private String assignTime;

    /** 优惠券状态 */
    private String status;

    /**
     * <h2>优惠券实体转换为 ThyCouponInfo</h2>
     * */
    static ThyCouponInfo to(Coupon coupon) {

        ThyCouponInfo info = new ThyCouponInfo();
        info.setId(coupon.getId());
        info.setTemplateId(coupon.getTemplateId());
        info.setUserId(coupon.getUserId());
        info.setCouponCode(coupon.getCouponCode());
        info.setAssignTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(coupon.getAssignTime()));
        info.setStatus(coupon.getStatus().getDescription());

        return info;
    }
}
