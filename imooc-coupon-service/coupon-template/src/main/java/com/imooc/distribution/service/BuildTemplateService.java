package com.imooc.distribution.service;

import com.imooc.distribution.exception.CouponException;
import com.imooc.distribution.pojo.CouponTemplate;
import com.imooc.distribution.vo.TemplateRequest;

/**
 * 构建优惠券模板接口定义
 */
public interface BuildTemplateService {

    /**
     * 创建优惠券模板
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     * @throws CouponException
     */
    CouponTemplate buildTemplate(TemplateRequest request)throws CouponException;
}
