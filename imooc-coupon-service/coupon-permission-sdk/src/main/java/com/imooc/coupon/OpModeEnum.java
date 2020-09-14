package com.imooc.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作模式的枚举定义
 */
@Getter
@AllArgsConstructor
public enum OpModeEnum {

    READ("读"),
    WRITE("写");

    private String mode;
}
