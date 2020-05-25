package com.imooc.distribution.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponTemplateSDK {

    /*优惠券id*/
    private Integer id;

    /*优惠券名称*/
    private String name;

    /*优惠券logo*/
    private String logo;

    /*优惠券详情*/
    private String desc;

    /*优惠券分类*/
    private String category;

    /*产品线*/
    private Integer productLine;

    /*优惠券编码*/
    private String key;

    /*目标用户*/
    private Integer target;

    /*优惠券规则*/
    private TemplateRule rule;
}
