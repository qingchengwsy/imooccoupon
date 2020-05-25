package com.imooc.distribution.feign;

import com.imooc.distribution.vo.CommonResponse;
import com.imooc.distribution.vo.CouponTemplateSDK;
import com.imooc.distribution.feign.hystrix.TemplateClientHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模板微服务 feign 接口定义
 */
@FeignClient(value = "eureka-client-distribution-template",
        fallback = TemplateClientHystrix.class)
public interface TemplateClient {

    /**
     * 查找所有可用的优惠券模板
     *
     * @return
     */
    @RequestMapping(value = "/coupon-template/template/sdk/all",
            method = RequestMethod.GET)
    CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate();

    /**
     * 获取优惠券模板 ids 到CouponTemplateSDK的映射
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/coupon-template/template/sdk/infos",
            method = RequestMethod.GET)
    CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(
            @RequestParam("ids") Collection<Integer> ids);
}
