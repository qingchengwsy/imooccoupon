package com.imooc.distribution.converter;

import com.imooc.distribution.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * 优惠券分类枚举属性转换器
 * AttributeConverter<X,Y>
 *     x: 实体属性类型
 *     Y: 数据库字段类型
 */
@Convert
public class CouponCategoryConvert implements AttributeConverter<CouponCategory,String> {

    /**
     * 将实体属性X转换为Y存储到数据库,插入或者更新时执行
     * @param couponCategory
     * @return
     */
    public String convertToDatabaseColumn(CouponCategory couponCategory) {
        return couponCategory.getCode();
    }

    /**
     * 将数据库中的字段Y转换为实体属性X,在查询操作时执行
     * @param code
     * @return
     */
    public CouponCategory convertToEntityAttribute(String code) {
        return CouponCategory.of(code);
    }
}
