-- 创建 distribution 数据表
CREATE TABLE IF NOT EXISTS `imooc_coupon_data`.`coupon` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `template_id` int(11) NOT NULL DEFAULT '0' COMMENT '关联优惠券模板的主键',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '领取用户',
  `coupon_code` varchar(64) NOT NULL DEFAULT '' COMMENT '优惠券码',
  `assign_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '领取时间',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '优惠券的状态',
  PRIMARY KEY (`id`),
  KEY `idx_template_id` (`template_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='优惠券(用户领取的记录)';

-- 清空表数据
-- truncate distribution;