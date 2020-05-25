package com.imooc.distribution.converter;

import com.imooc.distribution.constant.DistributeTarget;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * 优惠券分发目标枚举转换器
 */
@Convert
public class CouponTargetConvert implements AttributeConverter<DistributeTarget,Integer> {
    @Override
    public Integer convertToDatabaseColumn(DistributeTarget distributeTarget) {
        return distributeTarget.getCode();
    }

    @Override
    public DistributeTarget convertToEntityAttribute(Integer code) {
        return DistributeTarget.of(code);
    }
}
