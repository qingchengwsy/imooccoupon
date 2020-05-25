package com.imooc.distribution.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.distribution.exception.CouponException;
import com.imooc.distribution.pojo.CouponTemplate;
import com.imooc.distribution.service.BuildTemplateService;
import com.imooc.distribution.service.TemplateBaseService;
import com.imooc.distribution.vo.CouponTemplateSDK;
import com.imooc.distribution.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *优惠券模板controller
 */
@Slf4j
@RestController
public class CouponTemplateController {

    private final BuildTemplateService buildTemplateService;

    private final TemplateBaseService templateBaseService;

    @Autowired
    public CouponTemplateController(BuildTemplateService buildTemplateService, TemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;

    }

    /**
     * 创建优惠券模板
     * 127.0.0.1:7001/distribution-template/template/build
     * 127.0.0.1:9000/imooc/coupon-template/distribution-template/template/build
     * @param request
     * @return
     * @throws CouponException
     */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request)
            throws CouponException {
        log.info("Build Template: {}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    /**
     * 根据id获取优惠券详情
     * 127.0.0.1:7001/distribution-template/template/info
     * 127.0.0.1:9000/imooc/coupon-template/distribution-template/template/info
     * @param id
     * @return
     * @throws CouponException
     */
    @GetMapping("/template/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id)
            throws CouponException {
        log.info("Build Template Info Is id :{}", id);
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * 查询所有可用优惠券模板
     * 127.0.0.1:7001/distribution-template/template/sdk/all
     * 127.0.0.1:9000/imooc/coupon-template/distribution-template/template/sdk/all
     * @return
     */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template.");
        return templateBaseService.findAllUsableTemplate();
    }

    /**
     * 获取模板 ids 到 CouponTemplateSDK的映射
     * 127.0.0.1:7001/distribution-template/template/sdk/infos
     * 127.0.0.1:9000/imooc/coupon-template/distribution-template/template/sdk/infos
     * @param ids
     * @return
     */
    @GetMapping("/template/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(
            @RequestParam("ids") Collection<Integer> ids) {
        log.info("findIds2TemplateSDK: {}", JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }
}
