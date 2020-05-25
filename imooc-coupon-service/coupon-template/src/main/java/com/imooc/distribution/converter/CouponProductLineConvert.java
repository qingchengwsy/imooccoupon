package com.imooc.distribution.converter;

import com.imooc.distribution.constant.ProductLine;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * 优惠券产品线枚举属性转换器
 */
@Convert
public class CouponProductLineConvert implements AttributeConverter<ProductLine,Integer> {
    @Override
    public Integer convertToDatabaseColumn(ProductLine productLine) {
        return productLine.getCode();
    }

    @Override
    public ProductLine convertToEntityAttribute(Integer code) {
        return ProductLine.of(code);
    }
}
