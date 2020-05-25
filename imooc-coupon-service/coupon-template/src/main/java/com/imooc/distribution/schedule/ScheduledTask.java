package com.imooc.distribution.schedule;

import com.imooc.distribution.dao.CouponTemplateDao;
import com.imooc.distribution.pojo.CouponTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 定时清理已过期的优惠券模板
 */

@Service
@Slf4j
public class ScheduledTask {

    private final CouponTemplateDao couponTemplateDao;

    @Autowired
    public ScheduledTask(CouponTemplateDao couponTemplateDao) {
        this.couponTemplateDao = couponTemplateDao;
    }

    @Scheduled(fixedRate =  60 * 60 * 1000)
    public void offLineCouponTemplate() {
        log.info("Start To Expire CouponTemplate");
        //获取Expire =false的优惠券模板
        List<CouponTemplate> couponTemplateExpired =
                couponTemplateDao.findAllByExpired(false);
        //判断couponTemplateExpired==null
        if (CollectionUtils.isEmpty(couponTemplateExpired)) {
            log.info("Done To Expire CouponTemplate");
            return;
        }
        Date cur = new Date();
        List<CouponTemplate> couponTemplates = new
                ArrayList<>(couponTemplateExpired.size());
        couponTemplateExpired.forEach(template -> {
            //根据优惠券中的过期规则判断是过期
            if (template.getRule().getExpiration().getDeadLine() < cur.getTime()) {
                template.setExpired(true);
                couponTemplates.add(template);
            }
        });
        if (CollectionUtils.isNotEmpty(couponTemplates)) {
            log.info("Expire CouponTemplate Num: {}",
                    couponTemplateDao.saveAll(couponTemplates));
        }
        log.info("Done To Expire CouponTemplate");
    }
}
