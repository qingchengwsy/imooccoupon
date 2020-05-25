package com.imooc.distribution.controller;

import com.imooc.distribution.annotation.IgnoreResponseAdvice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Ribbon 应用Controller
 */
@Slf4j
@RestController
@IgnoreResponseAdvice
public class RibbonController {

    private final RestTemplate restTemplate;

    @Autowired
    public RibbonController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public TemplateInfo getTemplateInfo() {
        String infoUrl = "http://eureka-client-distribution-template/distribution-template/info";
        return restTemplate.getForEntity(infoUrl, TemplateInfo.class).getBody();
    }


    /**
     * 模板微服务元信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TemplateInfo {
        private Integer code;
        private String message;
        private List<Map<String, Object>> data;
    }
}
