-- 创建路径信息表
CREATE TABLE IF NOT EXISTS `imooc_coupon_data`.`coupon_path` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '路径ID, 自增主键',
  `path_pattern` varchar(200) NOT NULL DEFAULT '' COMMENT '路径模式	',
  `http_method` varchar(20) NOT NULL DEFAULT '' COMMENT 'http请求类型',
  `path_name` varchar(50) NOT NULL DEFAULT '' COMMENT '路径描述',
  `service_name` varchar(50) NOT NULL DEFAULT '' COMMENT '服务名',
  `op_mode` varchar(20) NOT NULL DEFAULT '' COMMENT '操作类型, READ/WRITE',
  PRIMARY KEY (`id`),
  KEY `idx_path_pattern` (`path_pattern`),
  KEY `idx_servivce_name` (`service_name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='路径信息表';

-- 创建用户角色表
CREATE TABLE IF NOT EXISTS `imooc_coupon_data`.`coupon_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色ID, 自增主键',
  `role_name` varchar(128) NOT NULL DEFAULT '' COMMENT '角色名称',
  `role_tag` varchar(128) NOT NULL DEFAULT '' COMMENT '角色TAG标识',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='用户角色表';

-- 创建 Role 与 Path 的映射关系表
CREATE TABLE IF NOT EXISTS `imooc_coupon_data`.`coupon_role_path_mapping` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `role_id` int(11) NOT NULL DEFAULT '0' COMMENT '角色ID',
  `path_id` int(11) NOT NULL DEFAULT '0' COMMENT '路径ID',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_path_id` (`path_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='角色路径映射表';

-- 创建 User 与 Role 的映射关系表
CREATE TABLE IF NOT EXISTS `imooc_coupon_data`.`coupon_user_role_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `role_id` int(11) NOT NULL DEFAULT '0' COMMENT '角色ID',
  PRIMARY KEY (`id`),
  KEY `key_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='用户角色关系映射表';
