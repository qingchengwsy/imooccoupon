package com.imooc.distribution.vo;

import com.imooc.distribution.constant.CouponCategory;
import com.imooc.distribution.constant.DistributeTarget;
import com.imooc.distribution.constant.ProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 优惠券模板请求对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRequest {

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

    /*总数*/
    private Integer count;

    /*创建用户*/
    private Long userId;

    /*目标用户*/
    private Integer target;

    /*优惠券规则*/
    private TemplateRule rule;


    /**
     * 校验对象的合法性
     *
     * @return
     */
    public Boolean validate() {
        Boolean stringValid = StringUtils.isNotEmpty(name)
                && StringUtils.isNotEmpty(logo)
                && StringUtils.isNotEmpty(desc)
                && StringUtils.isNotEmpty(category);
        Boolean enumValid = null != CouponCategory.of(category)
                && null != ProductLine.of(productLine)
                && null != DistributeTarget.of(target);
        Boolean numValid = count > 0 && userId > 0;
        return stringValid && enumValid && numValid && rule.validate();
    }
}
