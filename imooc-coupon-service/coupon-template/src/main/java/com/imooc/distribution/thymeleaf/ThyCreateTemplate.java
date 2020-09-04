package com.imooc.distribution.thymeleaf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h1>创建优惠券模板</h1>
 * Created by Qinyi.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class ThyCreateTemplate {

    /** 优惠券名称 */
    private String name;

    /** 优惠券 logo */
    private String logo;

    /** 优惠券描述 */
    private String desc;

    /** 优惠券分类 */
    private String category;

    /** 产品线 */
    private Integer productLine;

    /** 总数 */
    private Integer count;

    /** 创建用户 */
    private Long userId;

    /** 目标用户 */
    private Integer target;

    /** 有效期规则 */
    private Integer period;

    /** 有效间隔: 只对变动型有效期有效 */
    private Integer gap;

    /** 优惠券模板的失效日期: 两类规则都有效, 2019-12-01 */
    private String deadline = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    /** 额度(quota): 20（满减）、85%（折扣）、10（立减）*/
    private Integer quota;

    /** 基准(base，需要满多少): 199 */
    private Integer base;

    /** 每个人最多领几张的限制 */
    private Integer limitation;

    /** 省份 */
    private String province;

    /** 城市 */
    private String city;

    /** 商品类型, list[文娱(1)、生鲜(2)、家居(3)、其他(4)、全品类(0)] */
    private List<Integer> goodsType = new ArrayList<>();

    /** 权重(可以和哪些券叠加使用, 需要验证同一类的优惠券一定不能叠加): list[], 优惠券的唯一编码 */
    private String weight;
}
