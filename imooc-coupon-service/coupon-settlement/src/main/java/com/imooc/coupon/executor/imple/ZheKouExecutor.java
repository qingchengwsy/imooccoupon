package com.imooc.coupon.executor.imple;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.distribution.vo.CouponTemplateSDK;
import com.imooc.distribution.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 折扣优惠券结算规则执行器
 */
@Component
@Slf4j
public class ZheKouExecutor extends AbstractExecutor implements RuleExecutor {


    /**
     * 规则类型标记
     *
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.ZHEKOU;
    }

    /**
     * 优惠券规则计算
     *
     * @param settle {@link SettlementInfo} 包含了选择的优惠券
     * @return
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settle) {
        //计算商品总价
        double goodsSun = retain2Decimal(
                goodsCostSum(settle.getGoodInfos())
        );

        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settle, goodsSun
        );

        if (null != probability) {
            log.debug("ZheKou Template Is Not Match To GoodsType");
            return probability;
        }

        //折扣优惠券可以直接使用,没有门槛
        CouponTemplateSDK templateSDK = settle.getCouponAndTemplateInfo().get(0)
                .getTemplateSDK();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        //计算使用优惠券之后的价格
        settle.setCost(retain2Decimal(goodsSun * (quota * 1.0 / 100)) > minCost() ?
                        retain2Decimal(goodsSun * (quota * 1.0 / 100)) : minCost()
        );
        log.debug("Use ZheKou Coupon Make Goods  Cost From {} To {}",
                goodsSun, settle.getCost());
        return settle;
    }
}
