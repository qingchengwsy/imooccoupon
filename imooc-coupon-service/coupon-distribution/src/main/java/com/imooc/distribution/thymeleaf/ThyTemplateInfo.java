package com.imooc.distribution.thymeleaf;

import com.alibaba.fastjson.JSON;
import com.imooc.distribution.constant.CouponCategory;
import com.imooc.distribution.constant.GoodType;
import com.imooc.distribution.constant.PeriodType;
import com.imooc.distribution.constant.ProductLine;
import com.imooc.distribution.vo.CouponTemplateSDK;
import com.imooc.distribution.vo.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>优惠券模板详情</h1>
 * Created by Qinyi.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class ThyTemplateInfo {

    // 列表展示

    /** 用户 id (不是创建优惠券模板的用户, 是当前的查看用户) */
    private Long userId;

    /** 自增主键 */
    private Integer id;

    /** 优惠券名称 */
    private String name;

    /** 优惠券描述 */
    private String desc;

    /** 优惠券分类 */
    private String category;

    /** 产品线 */
    private String productLine;

    // 详情展示

    /** 过期规则描述 */
    private String expiration;

    /** 折扣规则描述 */
    private String discount;

    /** 使用条件描述 */
    private String usage;

    static ThyTemplateInfo to(CouponTemplateSDK template) {

        ThyTemplateInfo info = new ThyTemplateInfo();
        info.setId(template.getId());
        info.setName(template.getName());
        info.setDesc(template.getDesc());
        info.setCategory(CouponCategory.of(template.getCategory()).getDescription());
        info.setProductLine(ProductLine.of(template.getProductLine()).getDescription());

        info.setExpiration(buildExpiration(template.getRule().getExpiration()));
        info.setDiscount(buildDiscount(template.getRule().getDiscount()));
        info.setUsage(buildUsage(template.getRule().getUsage()));

        return info;
    }

    /**
     * <h2>过期规则描述</h2>
     * */
    private static String buildExpiration(TemplateRule.Expiration expiration) {

        return PeriodType.of(expiration.getPeriod()).getDescription()
                + ", 有效间隔: "
                + expiration.getGap()
                + ", 优惠券模板过期日期: "
                + new SimpleDateFormat("yyyy-MM-dd").format(new Date(expiration.getDeadLine()));
    }

    /**
     * <h2>折扣规则描述</h2>
     * */
    private static String buildDiscount(TemplateRule.Discount discount) {

        return "基准: " + discount.getBase() + ", " + "额度: " + discount.getQuota();
    }

    /**
     * <h2>使用条件描述</h2>
     * */
    @SuppressWarnings("all")
    private static String buildUsage(TemplateRule.Usage usage) {

        List<Integer> goodTypesI = JSON.parseObject(usage.getGoodsType(), List.class);
        List<String> goodsTypes = goodTypesI
                .stream()
                .map(g -> GoodType.of(g))
                .map(g -> g.getDescription())
                .collect(Collectors.toList());

        return "省份: " + usage.getProvince() + ", 城市: " + usage.getCity() + ", 允许的商品类型: "
                + goodsTypes.stream().collect(Collectors.joining(", "));
    }
}
