package com.imooc.distribution.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.distribution.exception.CouponException;
import com.imooc.distribution.vo.CouponTemplateSDK;
import com.imooc.distribution.vo.SettlementInfo;
import com.imooc.distribution.pojo.Coupon;
import com.imooc.distribution.service.UserService;
import com.imooc.distribution.vo.AcquireTemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserService Controller
 */

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据用户id和优惠券状态查询优惠券记录
     *
     * @param userId
     * @param status
     * @return
     */
    @GetMapping("/coupons")
    public List<Coupon> findCouponsByStatus(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "status") Integer status) throws CouponException {
        log.info("Find Coupons By Status:{},{}", userId, status);
        return userService.findCouponsByStatus(userId, status);
    }

    /**
     * 根据用户Id查找可以领取的优惠券模板
     *
     * @param userId
     * @return
     * @throws CouponException
     */
    @GetMapping("/template")
    public List<CouponTemplateSDK> findAvailableTemplate(
            @RequestParam(value = "userId") Long userId) throws CouponException {
        log.info("Find Available Template:{}", userId);
        return userService.findAvailableTemplate(userId);
    }

    /**
     * 用户领取优惠券
     *
     * @param request
     * @return
     * @throws CouponException
     */
    @PostMapping("/acquire/template")
    public Coupon acquireTemplate(@RequestBody AcquireTemplateRequest request)
            throws CouponException {
        log.info("Acquire Template:{}", JSON.toJSONString(request));
        return userService.acquireTemplate(request);
    }

    /**
     * 结算(核销)优惠券
     *
     * @param info
     * @return
     * @throws CouponException
     */
    @PostMapping("/settlement")
    public SettlementInfo settlement(@RequestBody SettlementInfo info) throws CouponException {

        log.info("Settlement:{}", JSON.toJSONString(info));
        return userService.settlement(info);
    }
}
