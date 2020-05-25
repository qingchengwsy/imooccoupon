package com.imooc.distribution.vo;

import com.imooc.distribution.constant.PeriodType;
import com.imooc.distribution.constant.CouponStatus;
import com.imooc.distribution.pojo.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 优惠券模板分类工具实现
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponClassify {

    private List<Coupon> usable;

    private List<Coupon> used;

    private List<Coupon> expired;

    /**
     * 对当前优惠券进行分类
     *
     * @param coupons
     * @return
     */
    public static CouponClassify classify(List<Coupon> coupons) {

        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());

        coupons.forEach(c -> {
            Boolean isTimeExpired;
            Long curTime = new Date().getTime();
            //如果是固定如期
            if (c.getTemplateSDK().getRule().getExpiration().getPeriod().equals(
                    PeriodType.REGULAR.getCode()
            )) {
                isTimeExpired = c.getTemplateSDK().getRule().getExpiration().getDeadLine()
                        <= curTime;
            } else {
                isTimeExpired = DateUtils.addDays(
                        c.getAssignTime(),
                        c.getTemplateSDK().getRule().getExpiration().getGap()
                ).getTime() <= curTime;
            }
            if (c.getStatus() == CouponStatus.USABLE) {
                usable.add(c);
            } else if (c.getStatus() == CouponStatus.USED) {
                used.add(c);
            } else {
                expired.add(c);
            }
        });
        return new CouponClassify(usable, used, expired);
    }
}
