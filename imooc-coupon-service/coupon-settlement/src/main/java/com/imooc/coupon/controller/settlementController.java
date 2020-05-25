package com.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.executor.ExecuteManager;
import com.imooc.distribution.exception.CouponException;
import com.imooc.distribution.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 优惠券结算 controller
 */

@Slf4j
@RestController
public class settlementController {

    /*优惠券规则执行管理器*/
    private final ExecuteManager executeManager;

    @Autowired
    public settlementController(ExecuteManager executeManager) {
        this.executeManager = executeManager;
    }

    /**
     * 优惠券结算
     * @param settlement
     * @return
     */
    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlement)
            throws CouponException {
        log.info("settlement: {}",JSON.toJSONString(settlement));
        return executeManager.computeRule(settlement);
    }
}
