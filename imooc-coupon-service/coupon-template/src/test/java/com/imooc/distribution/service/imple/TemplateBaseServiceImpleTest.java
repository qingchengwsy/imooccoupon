package com.imooc.distribution.service.imple;

import com.alibaba.fastjson.JSON;
import com.imooc.distribution.exception.CouponException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TemplateBaseServiceImpleTest {

    @Autowired
    private TemplateBaseServiceImple templateBaseServiceImple;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    public void buildTemplateInfo() throws CouponException {
        System.out.println(JSON.toJSONString(templateBaseServiceImple.buildTemplateInfo(10)));
    }

    @Test
    public void findAllUsableTemplate() {
        System.out.println(JSON.toJSONString(templateBaseServiceImple.findAllUsableTemplate()));
    }

    @Test
    public void findIds2TemplateSDK() {
        System.out.println(JSON.toJSONString(templateBaseServiceImple.findIds2TemplateSDK(
                Arrays.asList(10,2,3))));
    }
}