package com.imooc.coupon.ExecutorManager;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.executor.ExecuteManager;
import com.imooc.distribution.constant.CouponCategory;
import com.imooc.distribution.constant.GoodType;
import com.imooc.distribution.vo.CouponTemplateSDK;
import com.imooc.distribution.vo.GoodInfo;
import com.imooc.distribution.vo.SettlementInfo;
import com.imooc.distribution.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ExecutorManagerTest {

    private Long fakeUserId = 10001L;

    @Autowired
    private ExecuteManager executeManager;

    @Test
    public void testComputeRule() throws Exception {
        //满减优惠券测试
//        log.info("ManJian Coupon Executor Test !");
//        SettlementInfo info=fakeManJianCouponSettlement();
//        SettlementInfo rule = executeManager.computeRule(info);
//        log.info("{}",rule.getCost());
//        log.info("{}",rule.getCouponAndTemplateInfo().size());
//        log.info("{}",rule.getCouponAndTemplateInfo());

        //折扣优惠券测试
//        log.info("ZheKou Coupon Executor Test !");
//        SettlementInfo settlementInfo = fakeZheKouCouponSettlement();
//        SettlementInfo rule = executeManager.computeRule(settlementInfo);
//        log.info("{}", rule.getCost());
//        log.info("{}", rule.getCouponAndTemplateInfo().size());
//        log.info("{}", rule.getCouponAndTemplateInfo());

        //立减优惠券测试
//        log.info("LiJian Coupon Executor Test !");
//        SettlementInfo settlementInfo = fakeLiJianCouponSettlement();
//        SettlementInfo rule = executeManager.computeRule(settlementInfo);
//        log.info("{}", rule.getCost());
//        log.info("{}", rule.getCouponAndTemplateInfo().size());
//        log.info("{}", rule.getCouponAndTemplateInfo());

        //满减 + 折扣 优惠券测试
        log.info("LiJian Coupon Executor Test !");
        SettlementInfo settlementInfo = fakeManJianAndZheKouCouponSettlement();
        SettlementInfo rule = executeManager.computeRule(settlementInfo);
        log.info("{}", rule.getCost());
        log.info("{}", rule.getCouponAndTemplateInfo().size());
        log.info("{}", rule.getCouponAndTemplateInfo());

    }

    private SettlementInfo fakeManJianCouponSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setCost(0.0);
        info.setEmploy(false);

        GoodInfo goodInfo = new GoodInfo();
        goodInfo.setCount(1);
        goodInfo.setPrice(50.55);
        goodInfo.setType(GoodType.WUYU.getCode());

        GoodInfo goodInfo1 = new GoodInfo();
        goodInfo1.setCount(1);
        goodInfo1.setPrice(52.55);
        goodInfo1.setType(GoodType.WUYU.getCode());

        info.setGoodInfos(Arrays.asList(goodInfo, goodInfo1));

        SettlementInfo.CouponAndTemplateInfo templateInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        templateInfo.setId(1);
        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(1);
        templateSDK.setCategory(CouponCategory.MANJIAN.getCode());
        templateSDK.setKey("100120190801");
        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(20, 200));
        rule.setUsage(new TemplateRule.Usage("湖南", "长沙",
                JSON.toJSONString(Arrays.asList(GoodType.WUYU.getCode(), GoodType.SHENGXIAN.getCode()))));
        templateSDK.setRule(rule);
        templateInfo.setTemplateSDK(templateSDK);
        info.setCouponAndTemplateInfo(Collections.singletonList(templateInfo));
        return info;
    }

    private SettlementInfo fakeZheKouCouponSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setCost(0.0);
        info.setEmploy(false);

        GoodInfo goodInfo = new GoodInfo();
        goodInfo.setCount(2);
        goodInfo.setPrice(50.55);
        goodInfo.setType(GoodType.WUYU.getCode());

        GoodInfo goodInfo1 = new GoodInfo();
        goodInfo1.setCount(4);
        goodInfo1.setPrice(52.55);
        goodInfo1.setType(GoodType.WUYU.getCode());

        info.setGoodInfos(Arrays.asList(goodInfo, goodInfo1));

        SettlementInfo.CouponAndTemplateInfo templateInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        templateInfo.setId(1);
        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(1);
        templateSDK.setCategory(CouponCategory.ZHEKOU.getCode());
        templateSDK.setKey("100120190801");
        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(85, 1));
        rule.setUsage(new TemplateRule.Usage("湖南", "长沙",
                JSON.toJSONString(Arrays.asList(GoodType.SHENGXIAN.getCode()))));
        templateSDK.setRule(rule);
        templateInfo.setTemplateSDK(templateSDK);
        info.setCouponAndTemplateInfo(Collections.singletonList(templateInfo));
        return info;
    }

    private SettlementInfo fakeLiJianCouponSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setCost(0.0);
        info.setEmploy(false);

        GoodInfo goodInfo = new GoodInfo();
        goodInfo.setCount(2);
        goodInfo.setPrice(50.55);
        goodInfo.setType(GoodType.WUYU.getCode());

        GoodInfo goodInfo1 = new GoodInfo();
        goodInfo1.setCount(4);
        goodInfo1.setPrice(52.55);
        goodInfo1.setType(GoodType.WUYU.getCode());

        info.setGoodInfos(Arrays.asList(goodInfo, goodInfo1));

        SettlementInfo.CouponAndTemplateInfo templateInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        templateInfo.setId(1);
        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(1);
        templateSDK.setCategory(CouponCategory.LIJIAN.getCode());
        templateSDK.setKey("100120190801");
        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(50, 1));
        rule.setUsage(new TemplateRule.Usage("湖南", "长沙",
                JSON.toJSONString(Arrays.asList(GoodType.WUYU.getCode(),
                        GoodType.SHENGXIAN.getCode()))));
        templateSDK.setRule(rule);
        templateInfo.setTemplateSDK(templateSDK);
        info.setCouponAndTemplateInfo(Collections.singletonList(templateInfo));
        return info;
    }

    private SettlementInfo fakeManJianAndZheKouCouponSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setCost(0.0);
        info.setEmploy(false);

        GoodInfo goodInfo = new GoodInfo();
        goodInfo.setCount(2);
        goodInfo.setPrice(50.55);
        goodInfo.setType(GoodType.WUYU.getCode());

        GoodInfo goodInfo1 = new GoodInfo();
        goodInfo1.setCount(4);
        goodInfo1.setPrice(52.55);
        goodInfo1.setType(GoodType.WUYU.getCode());

        info.setGoodInfos(Arrays.asList(goodInfo, goodInfo1));

        // 满减优惠券
        SettlementInfo.CouponAndTemplateInfo manjianInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        manjianInfo.setId(1);

        CouponTemplateSDK manjianTemplate = new CouponTemplateSDK();
        manjianTemplate.setId(1);
        manjianTemplate.setCategory(CouponCategory.MANJIAN.getCode());
        manjianTemplate.setKey("100120190712");

        TemplateRule manjianRule = new TemplateRule();
        manjianRule.setDiscount(new TemplateRule.Discount(20, 199));
        manjianRule.setUsage(new TemplateRule.Usage("安徽省", "桐城市",
                JSON.toJSONString(Arrays.asList(
                        GoodType.WUYU.getCode(),
                        GoodType.JIAJU.getCode()
                ))));
        manjianRule.setWeight(JSON.toJSONString(Collections.emptyList()));
        manjianTemplate.setRule(manjianRule);
        manjianInfo.setTemplateSDK(manjianTemplate);

        // 折扣优惠券
        SettlementInfo.CouponAndTemplateInfo zhekouInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        zhekouInfo.setId(1);

        CouponTemplateSDK zhekouTemplate = new CouponTemplateSDK();
        zhekouTemplate.setId(2);
        zhekouTemplate.setCategory(CouponCategory.ZHEKOU.getCode());
        zhekouTemplate.setKey("100220190712");

        TemplateRule zhekouRule = new TemplateRule();
        zhekouRule.setDiscount(new TemplateRule.Discount(85, 1));
        zhekouRule.setUsage(new TemplateRule.Usage("安徽省", "桐城市",
                JSON.toJSONString(Arrays.asList(
                        GoodType.WUYU.getCode(),
                        GoodType.JIAJU.getCode()
                ))));
        zhekouRule.setWeight(JSON.toJSONString(
                Collections.singletonList("1001201907120001")
        ));
        zhekouTemplate.setRule(zhekouRule);
        zhekouInfo.setTemplateSDK(zhekouTemplate);

        info.setCouponAndTemplateInfo(Arrays.asList(
                manjianInfo, zhekouInfo
        ));
        return info;
    }
}
