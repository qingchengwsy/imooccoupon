package com.imooc.distribution.constant;

import io.netty.util.internal.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 用户优惠券的状态
 */
@Getter
@AllArgsConstructor
public enum CouponStatus {

    USABLE("可用的", 1),
    USED("已使用", 2),
    EXPIRED("过期的(未被使用)", 3);

    /*优惠卷状态描述信息*/
    private String description;

    /*优惠券状态*/
    private Integer code;

    /**
     * 根据code获取CouponStatus
     *
     * @param code
     * @return
     */
    public static CouponStatus of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() ->new  IllegalArgumentException(code + "not exists"));
    }
}
