package com.imooc.distribution.feign.hystrix;

import com.imooc.distribution.exception.CouponException;
import com.imooc.distribution.vo.CommonResponse;
import com.imooc.distribution.vo.SettlementInfo;
import com.imooc.distribution.feign.SettlementClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 结算微服务 熔断策略实现
 */
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {

    /**
     * 优惠券规则计算
     *
     * @param settlementInfo
     * @return
     * @throws CouponException
     */
    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlementInfo) throws CouponException {
        log.error("[eureka-client-distribution-settlement] computeRule" +
                " request error");
        settlementInfo.setEmploy(false);
        settlementInfo.setCost(-1.0);
        return new CommonResponse<>(
                -1,
                "[eureka-client-distribution-settlement] request error",
                settlementInfo
        );
    }
}
