package com.imooc.distribution.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AcquireTemplateRequest {

    /*用户id*/
    private Long userId;

    /*优惠券模板信息*/
    private CouponTemplateSDK templateSDK;
}
