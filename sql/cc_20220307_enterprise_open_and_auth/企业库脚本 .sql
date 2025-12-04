
DROP TABLE IF EXISTS `user_region_mapping_45f92210375346858b6b6694967f44de`;
CREATE TABLE `user_region_mapping_45f92210375346858b6b6694967f44de` (
                                                                        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                                        `region_id` varchar(128) DEFAULT NULL COMMENT '映射主键（区域或者是门店）',
                                                                        `user_id` varchar(128) DEFAULT NULL COMMENT '用户ID',
                                                                        `create_id` varchar(128) DEFAULT NULL COMMENT '创建人',
                                                                        `create_time` bigint(64) DEFAULT NULL COMMENT '创建时间',
                                                                        `update_id` varchar(128) DEFAULT NULL COMMENT '更新人',
                                                                        `update_time` bigint(64) DEFAULT NULL COMMENT '更新时间',
                                                                        PRIMARY KEY (`id`),
                                                                        KEY `idx_user_id` (`user_id`) USING BTREE COMMENT '用户id索引',
                                                                        KEY `idx_region_id` (`region_id`) USING BTREE COMMENT '映射Id索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='人员部门映射表';

DROP TABLE IF EXISTS `subordinate_mapping_45f92210375346858b6b6694967f44de`;
CREATE TABLE `subordinate_mapping_45f92210375346858b6b6694967f44de` (
                                                                        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                                        `user_id` varchar(128) DEFAULT NULL COMMENT '用户ID',
                                                                        `region_id` varchar(128) DEFAULT NULL COMMENT '部门ID',
                                                                        `personal_id` varchar(128) DEFAULT NULL COMMENT '人员ID',
                                                                        `type` tinyint(4) DEFAULT '0'  COMMENT '0 下属 ， 1 直属上级',
                                                                        `create_id` varchar(128) DEFAULT NULL COMMENT '创建人',
                                                                        `create_time` bigint(64) DEFAULT NULL COMMENT '创建时间',
                                                                        `update_id` varchar(128) DEFAULT NULL COMMENT '更新人',
                                                                        `update_time` bigint(64) DEFAULT NULL COMMENT '更新时间',
                                                                        PRIMARY KEY (`id`),
                                                                        KEY `idx_user_id` (`user_id`) USING BTREE COMMENT '用户id索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='我的下属映射表';

ALTER TABLE `enterprise_user_45f92210375346858b6b6694967f44de`
    ADD COLUMN `subordinate_change` tinyint(4) DEFAULT '0'   COMMENT '下级是否变动，0 没变动，1 变动',
ADD COLUMN `user_region_ids` text  COMMENT '部门集合（region_ids）';


ALTER TABLE `region_45f92210375346858b6b6694967f44de`
    ADD COLUMN `unclassified_flag` tinyint(4) DEFAULT '0'   COMMENT '未分组标志 0 分组 1 未分组',
ADD COLUMN `order_num` int(10)  COMMENT '排序';


ALTER TABLE `sys_role_45f92210375346858b6b6694967f44de`
    ADD COLUMN `create_user` varchar(128) DEFAULT NULL COMMENT '创建人',
    ADD COLUMN `update_user` varchar(128) DEFAULT NULL COMMENT '更新人';

-- ## 修改新增是否是is_has_auth的字段
ALTER TABLE enterprise_user_department_45f92210375346858b6b6694967f44de ADD COLUMN is_has_auth tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是部门权限数据：0否，1是';