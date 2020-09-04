package com.imooc.distribution.thymeleaf;

import com.alibaba.fastjson.JSON;

import com.imooc.distribution.constant.GoodType;
import com.imooc.distribution.constant.PeriodType;
import com.imooc.distribution.pojo.CouponTemplate;
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

    /** 自增主键 */
    private Integer id;

    /** 是否是可用状态 */
    private String available;

    /** 优惠券名称 */
    private String name;

    /** 优惠券描述 */
    private String desc;

    /** 优惠券分类 */
    private String category;

    /** 产品线 */
    private String productLine;

    /** 总数 */
    private Integer count;

    /** 创建时间 */
    private String createTime;

    /** 创建用户 */
    private Long userId;

    /** 优惠券模板的编码 */
    private String key;

    /** 目标用户 */
    private String target;

    // 详情展示

    /** 过期规则描述 */
    private String expiration;

    /** 折扣规则描述 */
    private String discount;

    /** 每个人最多领几张的限制 */
    private Integer limitation;

    /** 使用条件描述 */
    private String usage;

    /** 权重 */
    private String weight;

    @SuppressWarnings("all")
    static ThyTemplateInfo to(CouponTemplate template) {

        ThyTemplateInfo info = new ThyTemplateInfo();
        info.setId(template.getId());
        info.setAvailable(template.getAvailable() ? "可用" : "不可用");
        info.setName(template.getName());
        info.setDesc(template.getDesc());
        info.setCategory(template.getCategory().getDescription());
        info.setProductLine(template.getProductLine().getDescription());
        info.setCount(template.getCount());
        info.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(template.getCreateTime()));
        info.setUserId(template.getUserId());
        info.setKey(template.getKey() + String.format("%04d", template.getId()));
        info.setTarget(template.getTarget().getDescription());

        info.setExpiration(buildExpiration(template.getRule().getExpiration()));
        info.setDiscount(buildDiscount(template.getRule().getDiscount()));
        info.setLimitation(template.getRule().getLimitation());
        info.setUsage(buildUsage(template.getRule().getUsage()));
        info.setWeight(JSON.parseObject(template.getRule().getWeight(), List.class)
                .stream().collect(Collectors.joining(", ")).toString());

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
