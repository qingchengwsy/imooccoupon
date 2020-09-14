-- coupon_role 表数据填充
INSERT INTO `coupon_role` VALUES (1, '管理员', 'ADMIN');
INSERT INTO `coupon_role` VALUES (2, '超级管理员', 'SUPER_ADMIN');
INSERT INTO `coupon_role` VALUES (3, '普通用户', 'CUSTOMER');

-- coupon_role_path_mapping 表数据填充
INSERT INTO `coupon_role_path_mapping` VALUES (1, 1, 1);
INSERT INTO `coupon_role_path_mapping` VALUES (2, 1, 2);
INSERT INTO `coupon_role_path_mapping` VALUES (3, 3, 2);

-- coupon_user_role_mapping 表数据填充
INSERT INTO `coupon_user_role_mapping` VALUES (1, 15, 1);
INSERT INTO `coupon_user_role_mapping` VALUES (2, 16, 3);
