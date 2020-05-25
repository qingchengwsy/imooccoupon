package com.imooc.distribution.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>忽略统一响应注解定义</h1>
 */

@Target({ElementType.TYPE,ElementType.METHOD})  //将注解标注在什么类型上面(类,方法)
@Retention(RetentionPolicy.RUNTIME)  //运行时起作用
public @interface IgnoreResponseAdvice {
}
