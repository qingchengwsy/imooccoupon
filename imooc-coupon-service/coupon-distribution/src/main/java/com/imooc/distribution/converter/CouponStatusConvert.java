package com.imooc.distribution.converter;

import com.imooc.distribution.constant.CouponStatus;
import com.imooc.distribution.pojo.Coupon;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * 优惠券状态转换器
 */
@Convert
public class CouponStatusConvert implements AttributeConverter<CouponStatus,Integer> {
    @Override
    public Integer convertToDatabaseColumn(CouponStatus status) {
        return status.getCode();
    }

    @Override
    public CouponStatus convertToEntityAttribute(Integer code) {
        return CouponStatus.of(code);
    }
}
