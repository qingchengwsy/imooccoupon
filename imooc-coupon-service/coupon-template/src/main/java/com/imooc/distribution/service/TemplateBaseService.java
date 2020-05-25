package com.imooc.distribution.service;

import com.imooc.distribution.exception.CouponException;
import com.imooc.distribution.pojo.CouponTemplate;
import com.imooc.distribution.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模板基础(view,delete,update)服务定义
 */
public interface TemplateBaseService {

    /**
     * 根据优惠券模板 id 获取优惠券模板信息
     * @param id 模板id
     * @return {@link CouponTemplate} 优惠券实体
     * @throws CouponException
     */
    CouponTemplate buildTemplateInfo(Integer id)throws CouponException;

    /**
     * 查找所有可用的优惠券模板
     * @return {@link CouponTemplate} s
     */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * 获取模板 ids 到 CouponTemplateSDK的映射
     * @param ids 模板 ids
     * @return Map<key: ids, value: CouponTemplateSDK>
     */
    Map<Integer,CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
