package com.imooc.coupon.executor.imple;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.distribution.constant.CouponCategory;
import com.imooc.distribution.vo.GoodInfo;
import com.imooc.distribution.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 满减 + 折扣 优惠券规则结算执行器
 */
@Component
@Slf4j
public class ManJianZheKouExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * 规则类型定义
     *
     * @return
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN_ZHEKOU;
    }

    /**
     * 校验商品类型和优惠券是否匹配
     * 需要注意:
     * 1.这里实现的时 满减 +折扣优惠券的校验
     * 2.如果想要实现多类优惠券 则需要把所有商品类型包含在内 , 即差集为null
     *
     * @param settle {@link SettlementInfo} 用户传递的计算信息
     * @return
     */
    @SuppressWarnings("all")
    @Override
    protected boolean isGoodsTypeSatisfy(SettlementInfo settle) {
        log.debug("ManJian And zheKou Is Match Not Null");
        List<Integer> goodsType = settle.getGoodInfos().stream()
                .map(GoodInfo::getType).collect(Collectors.toList());
        List<Integer> templateGoodsType = new ArrayList<>();

        settle.getCouponAndTemplateInfo().forEach(ct -> {
            templateGoodsType.addAll(
                    JSON.parseObject(ct.getTemplateSDK()
                            .getRule().getUsage().getGoodsType(), List.class));
        });

        //如果想要使用多品类的优惠券, 则必须要所有的商品类型都包含在里面, 即差集为null
        //CollectionUtils.subtract(A,B) A-B=null
        return CollectionUtils.isEmpty(
                CollectionUtils.subtract(goodsType, templateGoodsType)
        );
    }

    /**
     * 优惠券规则计算
     *
     * @param settle {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的优惠券信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settle) {
        double goodsSum = retain2Decimal(goodsCostSum(
                settle.getGoodInfos()
        ));

        //商品类型校验
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settle, goodsSum
        );
        if (null != probability) {
            log.debug("ManJian And ZheKou Template Is Not Match To GoodsType");
            return probability;
        }

        SettlementInfo.CouponAndTemplateInfo manJian = null;
        SettlementInfo.CouponAndTemplateInfo zheKou = null;

        for (SettlementInfo.CouponAndTemplateInfo ct :
                settle.getCouponAndTemplateInfo()) {
            if (CouponCategory.of(ct.getTemplateSDK().getCategory())
                    == CouponCategory.MANJIAN) {
                manJian = ct;
            } else {
                zheKou = ct;
            }
        }
        assert null != manJian;
        assert null != zheKou;

        //当前的折扣优惠券和满减券如果不能一起使用,清空优惠券,返回商品总价
        if (!isTemplateCanShard(manJian, zheKou)) {
            log.debug("Current Manjian and Zhekou Can Not Shared");
            settle.setCouponAndTemplateInfo(Collections.emptyList());
            settle.setCost(goodsSum);
            return settle;
        }

        //可以一起使用
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = new ArrayList<>();
        double manJianBase = (double) manJian.getTemplateSDK().getRule().getDiscount().getBase();
        double manJianQuota = (double) manJian.getTemplateSDK().getRule().getDiscount().getQuota();

        //最终价格
        double targetSum = goodsSum;

        //先计算满减
        if (targetSum > manJianBase) {
            targetSum -= manJianQuota;
            ctInfos.add(manJian);
        }

        //在计算折扣
        double zheKouQuota = (double) zheKou.getTemplateSDK().getRule().getDiscount().getQuota();
        targetSum *= zheKouQuota * (1.0 / 100);
        ctInfos.add(zheKou);

        settle.setCouponAndTemplateInfo(ctInfos);
        settle.setCost(retain2Decimal(
                targetSum > minCost() ? targetSum : minCost()
        ));

        log.debug("Use ManJian And ZheKou Coupon Make Goods Cost From {} To {}",
                goodsSum, settle.getCost());

        return settle;
    }

    /**
     * 当前的两张优惠券可不可以一起使用
     * 即校验 TemplateRule 中的weight-优惠券的唯一编码 key 是否满足条件
     *
     * @param manJian
     * @param zheKou
     * @return
     */
    @SuppressWarnings("all")
    private boolean isTemplateCanShard(SettlementInfo.CouponAndTemplateInfo manJian,
                                       SettlementInfo.CouponAndTemplateInfo zheKou) {
        String manJianKey = manJian.getTemplateSDK().getKey()
                + String.format("%04d", manJian.getId());
        String zheKouKey = zheKou.getTemplateSDK().getKey()
                + String.format("%%04d", zheKou.getId());

        List<String> allSharedKeysForManjian = new ArrayList<>();
        allSharedKeysForManjian.add(manJianKey);
        allSharedKeysForManjian.addAll(JSON.parseObject(
                manJian.getTemplateSDK().getRule().getWeight(),
                List.class
        ));

        List<String> allSharedKeysForZhekou = new ArrayList<>();
        allSharedKeysForZhekou.add(zheKouKey);
        allSharedKeysForZhekou.addAll(JSON.parseObject(
                zheKou.getTemplateSDK().getRule().getWeight(),
                List.class
        ));

        return CollectionUtils.isSubCollection(
                Arrays.asList(manJianKey, zheKouKey), allSharedKeysForManjian
        ) || CollectionUtils.isSubCollection(
                Arrays.asList(manJianKey, zheKouKey), allSharedKeysForZhekou
        );
    }

}