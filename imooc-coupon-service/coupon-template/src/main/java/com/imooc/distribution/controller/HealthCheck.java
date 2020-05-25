package com.imooc.distribution.controller;

import com.imooc.distribution.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 健康检查接口
 */
@Slf4j
@RestController
public class HealthCheck {

    /*服务发现客户端*/
    private final DiscoveryClient client;

    /*服务注册接口, 提供了获取服务 id 的方法*/
    private final Registration registration;

    @Autowired
    public HealthCheck(DiscoveryClient client, Registration registration) {
        this.client = client;
        this.registration = registration;
    }

    /**
     * 健康检查接口
     * 127.0.0.1:7001/distribution-template/health
     * 127.0.0.1:9000/imooc/coupon-template/distribution-template/health
     * @return
     */
    @GetMapping("/health")
    public String health() {
        log.debug("view health api");
        return "CouponTemplate IS Ok";
    }

    /**
     * 异常测试接口
     * 127.0.0.1:7001/distribution-template/exception
     * 127.0.0.1:9000/imooc/coupon-template/distribution-template/exception
     * @return
     * @throws CouponException
     */
    @GetMapping("/exception")
    public String exception() throws CouponException {
        log.debug("view exception api");
        throw new CouponException("CouponTemplate Has Some Problem");
    }

    /**
     * 获取Eureka server 上的微服务元信息
     * 127.0.0.1:7001/distribution-template/info
     * 127.0.0.1:9000/imooc/coupon-template/distribution-template/info
     * @return
     */
    @GetMapping("/info")
    public List<Map<String, Object>> info() {
        /*大约需要两分钟才能获取到注册信息*/
        List<ServiceInstance> instances = client.getInstances(registration.getServiceId());
        List<Map<String, Object>> maps = new ArrayList<>(instances.size());
        instances.forEach(i -> {
            Map<String, Object> map = new HashMap<>();
            map.put("serviceId", i.getServiceId());
            map.put("instanceId", i.getInstanceId());
            map.put("port", i.getPort());
            maps.add(map);
        });
        return maps;
    }

}
