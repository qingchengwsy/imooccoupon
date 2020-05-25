package com.imooc.coupon.executor;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.distribution.constant.CouponCategory;
import com.imooc.distribution.exception.CouponException;
import com.imooc.distribution.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠券结算规则执行管理器
 * 即根据用户的请求(SettlementInfo)找到对应的 Executor 去做结算
 * BeanPostProcessor : Bean后置处理器
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class ExecuteManager implements BeanPostProcessor {

    /*规则执行映射*/
    private static Map<RuleFlag, RuleExecutor> executorIndex =
            new HashMap<>(RuleFlag.values().length);


    /**
     * 优惠券结算规则计算入口
     * 注意: 一定要保证传进来的优惠券个数>=1
     *
     * @param settlement
     * @return
     */
    public SettlementInfo computeRule(SettlementInfo settlement)
            throws CouponException {
        SettlementInfo result = null;

        //单类优惠券
        if (settlement.getCouponAndTemplateInfo().size() == 1) {
            CouponCategory category = CouponCategory.of(
                    settlement.getCouponAndTemplateInfo()
                            .get(0).getTemplateSDK().getCategory()
            );
            switch (category) {
                case MANJIAN:
                    result = executorIndex.get(RuleFlag.MANJIAN)
                            .computeRule(settlement);
                    break;
                case LIJIAN:
                    result = executorIndex.get(RuleFlag.LIJIAN)
                            .computeRule(settlement);
                    break;
                case ZHEKOU:
                    result = executorIndex.get(RuleFlag.ZHEKOU)
                            .computeRule(settlement);
                    break;
            }
        } else {

            //多品类优惠券
            List<CouponCategory> categories = new ArrayList<>(
                    settlement.getCouponAndTemplateInfo().size()
            );

            settlement.getCouponAndTemplateInfo().forEach(ct ->
                    categories.add(CouponCategory.of(
                            ct.getTemplateSDK().getCategory()
                    )));
            if (categories.size() != 2) {
                throw new CouponException("Not Support For More" +
                        "Template Categories");
            } else {
                if (categories.contains(CouponCategory.MANJIAN)
                        && categories.contains(CouponCategory.ZHEKOU)) {
                    result = executorIndex.get(RuleFlag.MANJIAN_ZHEKOU)
                            .computeRule(settlement);
                } else {
                    throw new CouponException("Not Support For Other" +
                            "Template Category");
                }
            }
        }
        return result;
    }


    /**
     * Bean初始化之前执行
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        if (!(bean instanceof RuleExecutor)) {
            return bean;
        }

        RuleExecutor executor = (RuleExecutor) bean;
        RuleFlag ruleFlag = executor.ruleConfig();

        //bean已经存在
        if (executorIndex.containsKey(ruleFlag)) {
            throw new IllegalStateException("There is already an executor " +
                    "for rule flag" + ruleFlag);
        }

        log.info("Load executor {} for rule flag"
                , executor.getClass(), ruleFlag);

        executorIndex.put(ruleFlag, executor);

        return null;
    }


    /**
     * Bean初始化之后执行
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }
}
