package com.imooc.coupon.executor;

import com.alibaba.fastjson.JSON;
import com.imooc.distribution.vo.GoodInfo;
import com.imooc.distribution.vo.SettlementInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 规则执行器抽象类
 */
public abstract class AbstractExecutor {

    /**
     * 校验商品类型和优惠券类型是否匹配
     * 需要注意:
     * 1. 这里实现的单品类优惠券的校验,多品类优惠券重写此方法
     * 2. 商品只需要有一个优惠券要求的商品类型去匹配就可以
     *
     * @param settle
     * @return
     */
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settle) {
        List<Integer> goodsType = settle.getGoodInfos().stream()
                .map(GoodInfo::getType).collect(Collectors.toList());
        List<Integer> templateGoodsType = JSON.parseObject(settle.getCouponAndTemplateInfo().get(0)
                .getTemplateSDK().getRule().getUsage().getGoodsType(), List.class);

        //存在交集即可 CollectionUtils.intersection(A,B)
        return CollectionUtils.isNotEmpty(
                CollectionUtils.intersection(goodsType, templateGoodsType));
    }

    /**
     * 处理商品类型与优惠券不匹配的情况
     *
     * @param settle   {@link SettlementInfo} 用户传递的结算消息
     * @param goodsSum 商品总价
     * @return {@link} 已经修改的结算信息
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(
            SettlementInfo settle, double goodsSum) {
        boolean isGoodsTypeSatisfy = isGoodsTypeSatisfy(settle);

        //当商品类型不满足时, 直接返回总价,并清空优惠券
        if (!isGoodsTypeSatisfy) {
            settle.setCost(goodsSum);
            settle.setCouponAndTemplateInfo(Collections.emptyList());
            return settle;
        }
        return null;
    }

    /**
     * 商品总价
     *
     * @param goodInfos
     * @return
     */
    protected double goodsCostSum(List<GoodInfo> goodInfos) {
        return goodInfos.stream().mapToDouble(
                g -> g.getPrice() * g.getCount()
        ).sum();
    }

    /**
     * 保留两位小数
     *
     * @param value
     * @return
     */
    protected double retain2Decimal(double value) {
        return new BigDecimal(value)
                .setScale(2, BigDecimal.ROUND_HALF_UP)  //四舍五入
                .doubleValue();
    }

    /**
     * 最小支付费用
     *
     * @return
     */
    protected double minCost() {
        return 0.1;
    }
}
