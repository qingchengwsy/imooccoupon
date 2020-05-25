package com.imooc.distribution.vo;

import com.imooc.distribution.constant.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * <h1>优惠券规则对象定义<h1/>
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRule {

    //优惠券过期规则
    private Expiration expiration;

    //折扣
    private Discount discount;

    //每人最多领取几张的限制
    private Integer limitation;

    //使用范围: 地域+商品类型
    private Usage usage;

    //权重 ( 可以和那些优惠券重叠使用,同一类的优惠券一定不能叠加)list[] ,优惠券的唯一编码 key
    private String weight;

    /**
     * 校验
     */
    public Boolean validate() {
        return expiration.validate() && discount.validate()
                && limitation > 0 && usage.validate()
                && StringUtils.isNotEmpty(weight);
    }

    /**
     * 有限期限规则定义
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Expiration {

        //有效期规格,直接对应 PeriodType code字段
        private Integer period;

        //有效期间隔: 只对变动性有效期有效
        private Integer gap;

        //优惠券模板的失效日期,两类规则都有效
        private Long deadLine;

        boolean validate() {
            //最简化校验
            return null != PeriodType.of(period) && gap > 0 && deadLine > 0;
        }
    }

    /**
     * 折扣,需要和类型配合决定
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Discount {

        //额度: 满减(20), 折扣(85), 立减(10)
        private Integer quota;

        //基准,需要满多少才可以用
        private Integer base;

        boolean validate() {
            return quota > 0 && base > 0;
        }
    }

    /**
     * 使用范围
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {

        //省份
        private String province;

        //城市
        private String city;

        //商品类型: list[文娱,生鲜,家具,全品]
        private String goodsType;

        boolean validate() {
            return StringUtils.isNotEmpty(province)
                    && StringUtils.isNotEmpty(city)
                    && StringUtils.isNotEmpty(goodsType);
        }
    }
}
