package com.imooc.coupon.executor.imple;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.distribution.vo.CouponTemplateSDK;
import com.imooc.distribution.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 满减优惠券结算规则执行器
 */

@Component
@Slf4j
public class ManJianExecutor extends AbstractExecutor implements RuleExecutor {


    /**
     * 规则类型标记
     *
     * @return {@link RuleExecutor}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN;
    }

    /**
     * 优惠券规则计算
     *
     * @param settle {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的优惠券信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settle) {
        //计算商品总价
        double goodsSum = retain2Decimal(
                goodsCostSum(settle.getGoodInfos()));

        //判断优惠券类型和商品类型是否匹配
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settle, goodsSum
        );

        //不匹配,没有优惠券,直接返回商品价格
        if (null != probability) {
            log.debug("ManJian Template Is Not Match To GoodsType");
            return probability;
        }

        //判断满减是否符合折扣标准
        CouponTemplateSDK templateSDK = settle.getCouponAndTemplateInfo()
                .get(0).getTemplateSDK();
        //基准(满多少才减)
        double base = (double)templateSDK.getRule().getDiscount().getBase();
        //额度  减多少
        double quota = (double)templateSDK.getRule().getDiscount().getQuota();

        //如果不符合标准 , 则直接返回商品总价
        if (goodsSum < base) {
            log.debug("Current Goods Cost Sum < ManJian Coupon Base!");
            settle.setCost(goodsSum);
            settle.setCouponAndTemplateInfo(Collections.emptyList());
            return settle;
        }
        //计算使用优惠券之后的价格 - 结算
        settle.setCost(retain2Decimal(
                (goodsSum - quota) > minCost() ? (goodsSum - quota) : minCost()
        ));
        log.debug("Use ManJian Coupon Make Goods Cost From {} To {}",
                goodsSum, settle.getCost());
        return settle;
    }
}
