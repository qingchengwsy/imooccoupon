package com.imooc.distribution.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 结算信息对象定义
 * 包含:
 * 1. userId
 * 2. 商品信息(列表)
 * 3. 优惠券列表
 * 4. 结算结果金额
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SettlementInfo {

    /*用户id*/
    private Long userId;

    /*商品信息列表*/
    private List<GoodInfo> goodInfos;

    /*是否使结算生效, 即核销*/
    private Boolean employ;

    /*优惠券列表*/
    private List<CouponAndTemplateInfo> couponAndTemplateInfo;

    /*结算金额*/
    private Double cost;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class CouponAndTemplateInfo {

        /*Coupon的主键*/
        private Integer id;

        /*优惠券对应的模板*/
        private CouponTemplateSDK templateSDK;

    }

}
