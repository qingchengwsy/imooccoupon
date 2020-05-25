package com.imooc.coupon.executor.imple;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.distribution.vo.CouponTemplateSDK;
import com.imooc.distribution.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 立减优惠券结算规则执行器
 */
@Component
@Slf4j
public class LiJianExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * 规则类型定义
     *
     * @return
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.LIJIAN;
    }

    /**
     * 优惠券规则计算
     * @param settle {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的优惠券信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settle) {
        double goodsSum = retain2Decimal(goodsCostSum(
                settle.getGoodInfos()));

        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settle, goodsSum
        );

        if (null != probability) {
            log.debug("LiJian Template Is Not Match To GoodsType!");
            return probability;
        }

        //立减优惠券直接使用,没有门槛
        CouponTemplateSDK templateSDK = settle.getCouponAndTemplateInfo()
                .get(0).getTemplateSDK();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        //计算使用优惠券之后的价格
        settle.setCost(retain2Decimal(goodsSum - quota) > minCost() ?
                retain2Decimal(goodsSum - quota) : minCost()
        );
        log.debug("Use LiJian Coupon Make Goods Cost From {} To {}",
                goodsSum, settle.getCost());
        return settle;
    }
}
