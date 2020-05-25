package com.imooc.distribution.feign.hystrix;

import com.imooc.distribution.vo.CommonResponse;
import com.imooc.distribution.vo.CouponTemplateSDK;
import com.imooc.distribution.feign.TemplateClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 优惠券模板 Feign 熔断降级策略
 */

@Slf4j
@Component
public class TemplateClientHystrix implements TemplateClient {

    /**
     * 查找所有了用的优惠券模板
     *
     * @return
     */
    @Override
    public CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate() {
        log.error("[eureka-client-distribution-template] findAllUsableTemplate " +
                "request error");
        return new CommonResponse<>(
                -1,
                "[eureka-client-distribution-template] request error",
                Collections.emptyList()
        );
    }

    /**
     * 获取模板 ids 到CouponTemplateSDK的映射
     *
     * @param ids
     * @return
     */
    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(Collection<Integer> ids) {
        log.error("[eureka-client-distribution-template] findIds2TemplateSDK " +
                "request error");
        return new CommonResponse<>(
                -1,
                "[eureka-client-distribution-template] request error",
                new HashMap<>()
        );
    }
}
