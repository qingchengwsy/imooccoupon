package com.imooc.distribution.service.imple;

import com.alibaba.fastjson.JSON;
import com.imooc.distribution.constant.CouponCategory;
import com.imooc.distribution.constant.DistributeTarget;
import com.imooc.distribution.constant.PeriodType;
import com.imooc.distribution.constant.ProductLine;
import com.imooc.distribution.pojo.CouponTemplate;
import com.imooc.distribution.vo.TemplateRequest;
import com.imooc.distribution.vo.TemplateRule;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BuildTemlateServiceImpleTest {

    @Autowired
    private BuildTemlateServiceImple temlateServiceImple;

    @Test
    public void buildTemplate() throws Exception {
        CouponTemplate couponTemplate = this.temlateServiceImple.buildTemplate(templateRequest());
        Thread.sleep(5000);
    }

    private TemplateRequest templateRequest(){
        TemplateRequest templateRequest=new TemplateRequest();
        templateRequest.setName("优惠券模板-"+new Date().getTime());
        templateRequest.setLogo("http://www.imooc.com");
        templateRequest.setDesc("这是有张优惠券模板");
        templateRequest.setCategory(CouponCategory.MANJIAN.getCode());
        templateRequest.setProductLine(ProductLine.DABAO.getCode());
        templateRequest.setCount(10000);
        templateRequest.setUserId(10001L);
        templateRequest.setTarget(DistributeTarget.SINGLE.getCode());
        TemplateRule rule=new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(
                PeriodType.SHIFT.getCode(),1,
                DateUtils.addDays(new Date(),60).getTime()));
        rule.setDiscount(new TemplateRule.Discount(20,1));
        rule.setLimitation(1);
        rule.setUsage(new TemplateRule.Usage(
                "安徽省","桐城市", JSON.toJSONString(Arrays.asList("文娱,道具"))));
        rule.setWeight(JSON.toJSONString(Arrays.asList(Collections.EMPTY_LIST)));
        templateRequest.setRule(rule);
        return templateRequest;
    }
}