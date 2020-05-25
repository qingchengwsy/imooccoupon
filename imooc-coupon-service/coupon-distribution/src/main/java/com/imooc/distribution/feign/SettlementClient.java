package com.imooc.distribution.feign;

import com.imooc.distribution.exception.CouponException;
import com.imooc.distribution.vo.CommonResponse;
import com.imooc.distribution.vo.SettlementInfo;
import com.imooc.distribution.feign.hystrix.SettlementClientHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 优惠券结算微服务 Feign 接口定义
 */

@FeignClient(value = "eureka-client-coupon-settlement",
        fallback = SettlementClientHystrix.class)
public interface SettlementClient {

    /**
     * 优惠券规则计算
     * @param settlementInfo
     * @return
     * @throws CouponException
     */
    @RequestMapping(value = "/coupon-settlement/settlement/compute",
            method = RequestMethod.POST)
    CommonResponse<SettlementInfo> computeRule(
            @RequestBody SettlementInfo settlementInfo) throws CouponException;
}
