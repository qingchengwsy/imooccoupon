package com.imooc.distribution.converter;

import com.alibaba.fastjson.JSON;
import com.imooc.distribution.vo.TemplateRule;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * 优惠券规则对象定义转换器
 */
@Convert
public class CouponTemplateRuleConvert implements AttributeConverter<TemplateRule,String> {
    @Override
    public String convertToDatabaseColumn(TemplateRule rule) {
        return JSON.toJSONString(rule);
    }

    @Override
    public TemplateRule convertToEntityAttribute(String rule) {
        return JSON.parseObject(rule,TemplateRule.class);
    }
}
