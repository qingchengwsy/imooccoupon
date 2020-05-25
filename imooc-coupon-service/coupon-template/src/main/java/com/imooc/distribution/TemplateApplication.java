package com.imooc.distribution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <h1>模板微服务启动入口</h1>
 */

@EnableScheduling    // 允许启动定时任务
@EnableJpaAuditing   // 启用 Spring Data JPA 审计功能，自动填充或更新实体中的CreateDate
@EnableDiscoveryClient  // 标识当前的应用是 EurekaClient
@SpringBootApplication
public class TemplateApplication {
    public static void main(String[] args) {
        SpringApplication.run(TemplateApplication.class,args);
    }
}
