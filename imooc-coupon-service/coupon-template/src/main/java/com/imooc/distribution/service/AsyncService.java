package com.imooc.distribution.service;

import com.imooc.distribution.pojo.CouponTemplate;

/**
 * 异步服务接口类
 */
public interface AsyncService {

    /**
     * 根据模板异步创建优惠券码
     * @param template {@link CouponTemplate} 模板实体
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);

}
