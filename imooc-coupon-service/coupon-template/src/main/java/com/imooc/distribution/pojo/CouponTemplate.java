package com.imooc.distribution.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.distribution.constant.CouponCategory;
import com.imooc.distribution.constant.DistributeTarget;
import com.imooc.distribution.constant.ProductLine;
import com.imooc.distribution.converter.CouponCategoryConvert;
import com.imooc.distribution.converter.CouponProductLineConvert;
import com.imooc.distribution.converter.CouponTargetConvert;
import com.imooc.distribution.converter.CouponTemplateRuleConvert;
import com.imooc.distribution.serialize.CouponTemplateSerialize;
import com.imooc.distribution.vo.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 优惠券实体定义: 基础属性 + 规则属性
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon_template")
@JsonSerialize(using = CouponTemplateSerialize.class)
public class CouponTemplate implements Serializable {

    private static final long serialVersionUID = 468274751454313248L;
    /* 自增主键*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Integer id;

    /*是否是可用状态*/
    @Column(name = "available",nullable = false)
    private Boolean available;

    /*是否过期*/
    @Column(name = "expired",nullable = false)
    private Boolean expired;

    /*优惠券名称*/
    @Column(name = "name",nullable = false)
    private String name;

    /*优惠券logo*/
    @Column(name = "logo",nullable = false)
    private String logo;

    /*优惠券描述*/
    @Column(name = "intro",nullable = false)
    private String desc;

    /*优惠券分类*/
    @Column(name = "category",nullable = false)
    @Convert(converter =CouponCategoryConvert.class)
    private CouponCategory category;

    /*产品线*/
    @Column(name = "product_line",nullable = false)
    @Convert(converter = CouponProductLineConvert.class)
    private ProductLine productLine;

    /*总数*/
    @Column(name = "coupon_count",nullable =false)
    private Integer count;

    /*创建时间*/
    @CreatedDate
    @Column(name = "create_time",nullable = false)
    private Date createTime;

    /*创建用户*/
    @Column(name = "user_id",nullable = false)
    private Long userId;

    /*优惠券模板编码*/
    @Column(name = "template_key",nullable = false)
    private String key;

    /*目标用户*/
    @Column(name = "target",nullable = false)
    @Convert(converter = CouponTargetConvert.class)
    private DistributeTarget target;

    /*优惠券规则*/
    @Column(name = "rule",nullable = false)
    @Convert(converter = CouponTemplateRuleConvert.class)
    private TemplateRule rule;

    /**
     * 自定义构造函数
     */
    public CouponTemplate(String name,String logo,String desc,String catetory,
                          Integer productLine,Integer count,Long userId,
                          Integer target,TemplateRule rule){
        this.available=false;
        this.expired=false;
        this.name=name;
        this.logo=logo;
        this.desc=desc;
        this.category=CouponCategory.of(catetory);
        this.productLine=ProductLine.of(productLine);
        this.count=count;
        this.userId=userId;
        //优惠券唯一编码=4(产品线+类型) +8(日期: 20200913) +id(扩充四位)
        this.key=productLine.toString()+catetory +
                new SimpleDateFormat("yyyyMMdd").format(new Date());
        this.target=DistributeTarget.of(target);
        this.rule=rule;
    }
}
