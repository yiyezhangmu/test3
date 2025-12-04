DELETE
from sys_role_${enterpriseId};
DELETE
from region_${enterpriseId};
DELETE
from user_auth_mapping_${enterpriseId};
DELETE
from store_${enterpriseId};
DELETE
from store_group_${enterpriseId};
DELETE
from store_group_mapping_${enterpriseId};
DELETE
from sys_role_menu_v2_${enterpriseId};
DELETE
from enterprise_user_role_${enterpriseId};
DELETE
from enterprise_user_${enterpriseId};
#设备场景初始化;
delete
from store_scene_${enterpriseId}
where id in (1, 2, 3);
INSERT INTO store_scene_${enterpriseId} (`id`, `name`, create_time, update_time, scene_type)
VALUES (1, '其他', now(), now(), 'nothing'),
       (2, '店外客流', now(), now(), 'store_in_out'),
       (3, '进店客流', now(), now(), 'store_in');

#业绩分类;
DELETE
from achievement_type_${enterpriseId}
WHERE id < 4;
INSERT INTO `achievement_type_${enterpriseId}`(`id`,
                                               `create_time`,
                                               `create_user_id`,
                                               `create_user_name`,
                                               `update_user_id`,
                                               `update_user_name`,
                                               `name`)
VALUES (1, NOW(), 'system', 'system', 'system', 'system', '男鞋'),
       (2, NOW(), 'system', 'system', 'system', 'system', '女装'),
       (3, NOW(), 'system', 'system', 'system', 'system', '童装');

#业绩模板;
DELETE
from achievement_formwork_${enterpriseId}
WHERE id < 3;
INSERT INTO achievement_formwork_${enterpriseId}(`id`,
                                                 `create_time`,
                                                 `create_id`,
                                                 `create_name`,
                                                 `update_id`,
                                                 `update_name`,
                                                 `name`,
                                                 `type`,
                                                 `status`)
VALUES (1, NOW(), 'system', 'system', 'system', 'system', '日常销量提报', 'normal', 1),
       (2, NOW(), 'system', 'system', 'system', 'system', '订货会销量提报', 'normal', 1);

#业绩模板类型映射;
DELETE
from achievement_formwork_mapping_${enterpriseId}
WHERE id < 3;
INSERT INTO achievement_formwork_mapping_${enterpriseId} (`id`,
                                                          `formwork_id`,
                                                          `type_id`,
                                                          `status`)
VALUES (1, 1, 2, 1),
       (2, 2, 2, 1);

#初始化sop文档;
DELETE
from task_sop_${enterpriseId}
WHERE id < 4;
INSERT INTO `task_sop_${enterpriseId}` (`id`,
                                        `file_name`,
                                        `url`,
                                        `type`,
                                        `category`,
                                        `create_user_id`,
                                        `create_user`,
                                        `create_time`,
                                        `update_user_id`,
                                        `update_user`,
                                        `update_time`,
                                        `is_delete`,
                                        `visible_user`,
                                        `visible_role`,
                                        `visible_user_name`,
                                        `visible_role_name`)
VALUES (3, '基础数据部署-系统设置+门店管理20220420-20220424104519.pdf',
        'https://oss-cool.coolstore.cn/doc/store-management.pdf', 'pdf', 'pdf', 'system', 'system', NOW(),
        NULL, NULL, NOW(), 0, '', '', '', '');

-- 检查项分类
delete
from `tb_meta_column_category_${enterpriseId}`
where id < 4;
INSERT INTO `tb_meta_column_category_${enterpriseId}`(`id`, `category_name`, `order_num`, `is_default`, `create_id`)
VALUES (1, '日常检查', 0, 0, 'system');
INSERT INTO `tb_meta_column_category_${enterpriseId}`(`id`, `category_name`, `order_num`, `is_default`, `create_id`)
VALUES (2, 'AI检查项', 0, 1, 'system');
INSERT INTO `tb_meta_column_category_${enterpriseId}`(`id`, `category_name`, `order_num`, `is_default`, `create_id`)
VALUES (3, '其他', 0, 1, 'system');

-- 快速检查项
delete
from `tb_meta_quick_column_${enterpriseId}`;
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`, `question_handler_type`,
                                                   `question_handler_id`, `question_cc_type`, `support_score`,
                                                   `store_scene_id`, `column_type`, `ai_type`, `threshold`,
                                                   `category_id`, `min_score`, `max_score`, `create_user`)
VALUES (1, '开门打烊', '正常开门营业，无推迟现象，无提前闭店现象', NULL, NULL, '', 1, NULL, 0, NULL, 0.6, 1, 1.00, 1.00, 'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`, `question_handler_type`,
                                                   `question_handler_id`, `question_cc_type`, `support_score`,
                                                   `store_scene_id`, `column_type`, `ai_type`, `threshold`,
                                                   `category_id`, `min_score`, `max_score`, `create_user`)
VALUES (2, '晨会', '晨会是否按照流程进行', NULL, NULL, '', 1, NULL, 0, NULL, 0.6, 1, 1.00, 1.00, 'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`, `question_handler_type`,
                                                   `question_handler_id`, `question_cc_type`, `support_score`,
                                                   `store_scene_id`, `column_type`, `ai_type`, `threshold`,
                                                   `category_id`, `min_score`, `max_score`, `create_user`)
VALUES (3, '货架卫生', '货架无杂物乱放行为', NULL, NULL, '', 1, NULL, 0, NULL, 0.6, 1, 1.00, 1.00, 'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`, `question_handler_type`,
                                                   `question_handler_id`, `question_cc_type`, `support_score`,
                                                   `store_scene_id`, `column_type`, `ai_type`, `threshold`,
                                                   `category_id`, `min_score`, `max_score`, `create_user`)
VALUES (4, '价签', '价签：一物一签，无手工涂改，标签字迹清晰无破损、变色现象', NULL, NULL, '', 1, NULL, 0, NULL, 0.6, 1, 1.00, 1.00,
        'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`, `question_handler_type`,
                                                   `question_handler_id`, `question_cc_type`, `support_score`,
                                                   `store_scene_id`, `column_type`, `ai_type`, `threshold`,
                                                   `category_id`, `min_score`, `max_score`, `create_user`)
VALUES (5, '陈列', '商品陈列：商品陈列是否整齐，不凌乱', NULL, NULL, '', 1, NULL, 0, NULL, 0.6, 1, 1.00, 1.00, 'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`, `question_handler_type`,
                                                   `question_handler_id`, `question_cc_type`, `support_score`,
                                                   `store_scene_id`, `column_type`, `ai_type`, `threshold`,
                                                   `category_id`, `min_score`, `max_score`, `create_user`)
VALUES (6, '空缺货位', '空缺货位：门店陈列位货品空缺货位', NULL, NULL, '', 1, NULL, 0, NULL, 0.6, 1, 1.00, 1.00, 'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`, `question_handler_type`,
                                                   `question_handler_id`, `question_cc_type`, `support_score`,
                                                   `store_scene_id`, `column_type`, `ai_type`, `threshold`,
                                                   `category_id`, `min_score`, `max_score`, `create_user`)
VALUES (7, '环境卫生', '地面：门店内外地面干净，无垃圾纸屑、无死角', NULL, NULL, '', 1, NULL, 0, NULL, 0.6, 1, 1.00, 1.00, 'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`, `question_handler_type`,
                                                   `question_handler_id`, `question_cc_type`, `support_score`,
                                                   `store_scene_id`, `column_type`, `ai_type`, `threshold`,
                                                   `category_id`, `min_score`, `max_score`, `create_user`)
VALUES (8, '培训', '每日日训记录完整，必须有培训具体内容', NULL, NULL, '', 1, NULL, 0, NULL, 0.6, 1, 1.00, 1.00, 'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`, `question_handler_type`,
                                                   `question_handler_id`, `question_cc_type`, `support_score`,
                                                   `store_scene_id`, `column_type`, `ai_type`, `threshold`,
                                                   `category_id`, `min_score`, `max_score`, `create_user`)
VALUES (9, '帽子', '帽子', NULL, NULL, '', 10, 1, 6, 'hat', 0.6, 2, 0.00, 0.00, 'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`, `question_handler_type`,
                                                   `question_handler_id`, `question_cc_type`, `support_score`,
                                                   `store_scene_id`, `column_type`, `ai_type`, `threshold`,
                                                   `category_id`, `min_score`, `max_score`, `create_user`)
VALUES (10, '口罩', '口罩', NULL, NULL, '', 10, 1, 6, 'mask', 0.6, 2, 0.00, 0.00, 'system');


update `tb_meta_quick_column_${enterpriseId}`
set create_user = 'system', create_user_name = 'system', edit_user_name = 'system'
where id < 11;

-- 快速检查项结果项
delete
from `tb_meta_quick_column_result_${enterpriseId}`
where id < 31;
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (1, 1, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (2, 1, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (3, 1, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (4, 2, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (5, 2, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (6, 2, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (7, 3, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (8, 3, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (9, 3, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (10, 4, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (11, 4, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (12, 4, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (13, 5, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (14, 5, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (15, 5, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (16, 6, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (17, 6, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (18, 6, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (19, 7, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (20, 7, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (21, 7, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (22, 8, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (23, 8, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (24, 8, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (25, 9, '合格', 10.00, 0.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (26, 9, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (27, 9, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (28, 10, '合格', 10.00, 0.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (29, 10, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`,
                                                          `min_score`, `default_money`, `mapping_result`, `description`,
                                                          `create_user_id`)
VALUES (30, 10, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');


-- sop表
delete
from `tb_meta_table_${enterpriseId}`
where id < 4;
INSERT INTO `tb_meta_table_${enterpriseId}`(`id`, `table_name`, `create_user_id`, `create_user_name`, `support_score`,
                                            `locked`, `active`, `table_type`, `edit_user_id`, `edit_user_name`,
                                            `level_rule`, `level_info`, `category_name_list`, `order_num`,
                                            `total_score`, `table_property`)
VALUES (1, '基础运营', 'system', 'system', 0, 1, 1, 'STANDARD', 'system', 'system', 'SCORING_RATE', '{\"levelList\":[{\"keyName\":\"excellent\",\"percent\":90,\"qualifiedNum\":6},{\"keyName\":\"good\",\"percent\":80,\"qualifiedNum\":4},{\"keyName\":\"eligible\",\"percent\":60,\"qualifiedNum\":2},{\"keyName\":\"disqualification\",\"percent\":0,\"qualifiedNum\":0}],\"open\":true}', '[\"日常检查\"]', 1, 0.00, 0);
INSERT INTO `tb_meta_table_${enterpriseId}`(`id`, `table_name`, `create_user_id`, `create_user_name`, `support_score`, `locked`, `active`, `table_type`, `edit_user_id`, `edit_user_name`, `level_rule`, `level_info`, `category_name_list`, `order_num`, `total_score`, `table_property`) VALUES (2, '全国门店月陈列反馈检查表', 'system', 'system', 1, 1, 1, 'TB_DISPLAY', NULL, NULL, NULL, NULL, NULL, 2, 0.00, 0);
INSERT INTO `tb_meta_table_${enterpriseId}`(`id`, `table_name`, `create_user_id`, `create_user_name`, `support_score`,
                                            `locked`, `active`, `table_type`, `edit_user_id`, `edit_user_name`,
                                            `level_rule`, `level_info`, `category_name_list`, `order_num`,
                                            `total_score`, `table_property`)
VALUES (3, '华北区元旦促销陈列检查', 'system', 'system', 1, 0, 1, 'TB_DISPLAY', NULL, NULL, NULL, NULL, NULL, 3, 0.00, 1);


-- sop表中检查项
delete
from `tb_meta_sta_table_column_${enterpriseId}`;
INSERT INTO `tb_meta_sta_table_column_${enterpriseId}`(`id`, `category_name`, `meta_table_id`, `column_name`,
                                                       `description`, `question_handler_type`, `question_handler_id`,
                                                       `create_user_id`, `support_score`, `level`, `quick_column_id`,
                                                       `threshold`)
VALUES (1, '日常检查', 1, '开门打烊', '正常开门营业，无推迟现象，无提前闭店现象', 'position', NUll, 'system', 1.00, 'general', 0, 0.4);
INSERT INTO `tb_meta_sta_table_column_${enterpriseId}`(`id`, `category_name`, `meta_table_id`, `column_name`,
                                                       `description`, `question_handler_type`, `question_handler_id`,
                                                       `create_user_id`, `support_score`, `level`, `quick_column_id`,
                                                       `threshold`)
VALUES (2, '日常检查', 1, '晨会', '晨会是否按照流程进行', 'position', NUll, 'system', 1.00, 'general', 0, 0.4);
INSERT INTO `tb_meta_sta_table_column_${enterpriseId}`(`id`, `category_name`, `meta_table_id`, `column_name`,
                                                       `description`, `question_handler_type`, `question_handler_id`,
                                                       `create_user_id`, `support_score`, `level`, `quick_column_id`,
                                                       `threshold`)
VALUES (3, '日常检查', 1, '货架卫生', '货架无杂物乱放行为', 'position', NUll, 'system', 1.00, 'general', 0, 0.4);

delete
from `tb_meta_column_result_${enterpriseId}`
where id < 10;
-- sop表中检查项的结果项
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`,
                                                    `result_name`, `mapping_result`, `description`, `max_score`,
                                                    `min_score`)
VALUES (1, 1, 1, 'system', '合格', 'PASS', 'ignore', 1.00, 1.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`,
                                                    `result_name`, `mapping_result`, `description`, `max_score`,
                                                    `min_score`)
VALUES (2, 1, 1, 'system', '不合格', 'FAIL', 'ignore', 0.00, 0.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`,
                                                    `result_name`, `mapping_result`, `description`, `max_score`,
                                                    `min_score`)
VALUES (3, 1, 1, 'system', '不适用', 'INAPPLICABLE', 'ignore', 0.00, 0.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`,
                                                    `result_name`, `mapping_result`, `description`, `max_score`,
                                                    `min_score`)
VALUES (4, 1, 2, 'system', '合格', 'PASS', 'ignore', 1.00, 1.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`,
                                                    `result_name`, `mapping_result`, `description`, `max_score`,
                                                    `min_score`)
VALUES (5, 1, 2, 'system', '不合格', 'FAIL', 'ignore', 0.00, 0.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`,
                                                    `result_name`, `mapping_result`, `description`, `max_score`,
                                                    `min_score`)
VALUES (6, 1, 2, 'system', '不适用', 'INAPPLICABLE', 'ignore', 0.00, 0.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`,
                                                    `result_name`, `mapping_result`, `description`, `max_score`,
                                                    `min_score`)
VALUES (7, 1, 3, 'system', '合格', 'PASS', 'ignore', 1.00, 1.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`,
                                                    `result_name`, `mapping_result`, `description`, `max_score`,
                                                    `min_score`)
VALUES (8, 1, 3, 'system', '不合格', 'FAIL', 'ignore', 0.00, 0.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`,
                                                    `result_name`, `mapping_result`, `description`, `max_score`,
                                                    `min_score`)
VALUES (9, 1, 3, 'system', '不适用', 'INAPPLICABLE', 'ignore', 0.00, 0.00);



#1.1 标准检查表对应检查项信息;
DELETE
from tb_meta_display_table_column_${enterpriseId}
WHERE id IN (1, 2, 3);
INSERT
`tb_meta_display_table_column_${enterpriseId}` (
    `id`,
    `create_time`,
    `edit_time`,
    `meta_table_id`,
    `column_name`,
    `create_user_id`,
    `order_num`,
    `standard_pic`,
    `description`,
    `deleted`,
    `create_user_name`,
    `edit_user_id`,
    `edit_user_name`,
    `quick_column_id`,
    `score`,
    `check_type`
)
VALUES
    (1,NOW(),NOW(),2,'全景照片','system',1,'https://oss-cool.coolstore.cn/pic/6ea450e02f8044e98b3fdf91463a46ef.png','店铺需将双十一物料都陈列好后
，拍下清晰的全景照片',0,'system','','',0,10,0),
    (2,NOW(),NOW(),2,'货架A标准图','system',2,'https://oss-cool.coolstore.cn/pic/655c4ef1461d4442acb231c2fe8ddd13.png','请按照标准图上传货架A照片',0,'system','','',0,10,0),
    (3,NOW(),NOW(),2,'门头标准图','system',3,'https://oss-cool.coolstore.cn/pic/0b7649071e71489aafd864b5e527f386.png','请按照标准图上传门头照片',0,'system','','',0,10,0);

#2.1陈列高级检查表对应检查项;
DELETE
from tb_meta_display_table_column_${enterpriseId}
WHERE id IN (4, 5, 6, 7, 8);
INSERT INTO `tb_meta_display_table_column_${enterpriseId}`(`id`,
                                                           `create_time`,
                                                           `edit_time`,
                                                           `meta_table_id`,
                                                           `column_name`,
                                                           `create_user_id`,
                                                           `order_num`,
                                                           `standard_pic`,
                                                           `description`,
                                                           `deleted`,
                                                           `create_user_name`,
                                                           `edit_user_id`,
                                                           `edit_user_name`,
                                                           `score`,
                                                           `check_type`)
VALUES (4, now(), now(), 3, '整洁度', 'system', 1, '', '', 0, 'system', '', '', 40, 0),
       (5, now(), now(), 3, '氛围', 'system', 2, '', '', 0, 'system', '', '', 30, 0),
       (6, now(), now(), 3, '灯光', 'system', 3, '', '', 0, 'system', '', '', 30, 0),
       (7, now(), now(), 3, '门头', 'system', 1,
        'https://oss-cool.coolstore.cn/pic/0b7649071e71489aafd864b5e527f386.png', '', 0, 'system', '', '', 40,
        1),
       (8, now(), now(), 3, '橱窗', 'system', 2,
        'https://oss-cool.coolstore.cn/pic/6ea450e02f8044e98b3fdf91463a46ef.png', '', 0, 'system', '', '', 30,
        1);

#3.1陈列快速检查项;
DELETE
from tb_meta_display_quick_column_${enterpriseId}
WHERE id < 4;
INSERT INTO `tb_meta_display_quick_column_${enterpriseId}` (`id`,
                                                            `create_time`,
                                                            `edit_time`,
                                                            `column_name`,
                                                            `create_user_id`,
                                                            `create_user_name`,
                                                            `deleted`,
                                                            `score`,
                                                            `standard_pic`,
                                                            `description`,
                                                            `check_type`)
VALUES (1, now(), now(), '卫生', 'system', 'system', 0, 10, '', '', 0),
       (2, now(), now(), '整齐', 'system', 'system', 0, 10, '', '', 0),
       (3, now(), now(), '形象', 'system', 'system', 0, 10, '', '', 0);


#4.首页模板;
DELETE from home_template_${enterpriseId};
INSERT INTO `home_template_${enterpriseId}`(
     `id`,
     `template_name`,
     `template_description`,
     `is_default`, `deleted`,
     `pc_components_json`,
     `app_components_json`,
     `create_id`,
     `create_time`,
     `update_id`,
     `update_time`
)
VALUES
(1, '店外职位模版', '适用于督导、营运、区经等角色使用。', 1, 0, '{\"componentsJson\":{\"moduleList\":[{\"key\":\"ToDoList\",\"name\":\"我的待办\",\"visible\":true,\"dragable\":false,\"configurable\":false,\"id\":\"ToDoList1\"},{\"key\":\"CommonFunctions\",\"name\":\"常用功能\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"CommonFunctions1\"},{\"key\":\"DataOverview\",\"name\":\"数据概况\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"DataOverview1\"},{\"key\":\"WorkorderData\",\"name\":\"工单数据\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"WorkorderData1\"},{\"key\":\"RankingofUnqualifiedItems\",\"name\":\"不合格项排名\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"RankingofUnqualifiedItems1\"},{\"key\":\"AverageScoreOfVisitedStores\",\"name\":\"已巡门店平均得分\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"AverageScoreOfVisitedStores1\"},{\"key\":\"ExecutionRanking\",\"name\":\"巡店执行力排名\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"ExecutionRanking1\"},{\"key\":\"DisplayData\",\"name\":\"陈列数据\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"DisplayData1\"},{\"key\":\"TourData\",\"name\":\"巡店数据\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"TourData1\"},{\"key\":\"TourRecord\",\"name\":\"巡店记录\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"TourRecord1\"},{\"key\":\"StoreTaskOverview\",\"name\":\"店务概况\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"StoreTaskOverview1\"}]}}', '{\"componentsJson\":{\"moduleList\":[{\"key\":\"Banner\",\"name\":\"轮播图\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"Banner2\"},{\"key\":\"CommonFunctions\",\"name\":\"常用功能\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"CommonFunctions2\"},{\"key\":\"DataOverview\",\"name\":\"数据概况\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"DataOverview2\"},{\"key\":\"LicenseData\",\"name\":\"证照数据\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"LicenseData2\"},{\"key\":\"WorkorderData\",\"name\":\"工单数据\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"WorkorderData2\"},{\"key\":\"RankingofUnqualifiedItems\",\"name\":\"不合格项排名\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"RankingofUnqualifiedItems2\"},{\"key\":\"TourRecord\",\"name\":\"巡店记录\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"TourRecord2\"},{\"key\":\"ToDoList\",\"name\":\"我的待办\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"ToDoList2\"},{\"key\":\"TourData\",\"name\":\"巡店数据\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"TourData2\"},{\"key\":\"AverageScoreOfVisitedStores\",\"name\":\"已巡门店平均得分\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"AverageScoreOfVisitedStores2\"},{\"key\":\"ExecutionRanking\",\"name\":\"巡店执行力排名\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"ExecutionRanking2\"},{\"key\":\"DisplayData\",\"name\":\"陈列数据\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"DisplayData2\"},{\"key\":\"DayClear\",\"name\":\"日清\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"DayClear2\"},{\"key\":\"StoreTaskOverview\",\"name\":\"店务概况\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"StoreTaskOverview2\"}]}}', 'system', now(), 'system', now()),
(2, '店内职位模版', '适用于门店的店长店员角色使用。', 1, 0, '{\"componentsJson\":{\"moduleList\":[{\"key\":\"ToDoList\",\"name\":\"我的待办\",\"visible\":true,\"dragable\":false,\"configurable\":false,\"id\":\"ToDoList1\"},{\"key\":\"CommonFunctions\",\"name\":\"常用功能\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"CommonFunctions1\"},{\"key\":\"DataOverview\",\"name\":\"数据概况\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"DataOverview1\"},{\"key\":\"WorkorderData\",\"name\":\"工单数据\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"WorkorderData1\"},{\"key\":\"RankingofUnqualifiedItems\",\"name\":\"不合格项排名\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"RankingofUnqualifiedItems1\"},{\"key\":\"AverageScoreOfVisitedStores\",\"name\":\"已巡门店平均得分\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"AverageScoreOfVisitedStores1\"},{\"key\":\"ExecutionRanking\",\"name\":\"巡店执行力排名\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"ExecutionRanking1\"},{\"key\":\"DisplayData\",\"name\":\"陈列数据\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"DisplayData1\"},{\"key\":\"TourData\",\"name\":\"巡店数据\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"TourData1\"},{\"key\":\"TourRecord\",\"name\":\"巡店记录\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"TourRecord1\"},{\"key\":\"StoreTaskOverview\",\"name\":\"店务概况\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"StoreTaskOverview1\"}]}}', '{\"componentsJson\":{\"moduleList\":[{\"key\":\"Banner\",\"name\":\"轮播图\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"Banner2\"},{\"key\":\"CommonFunctions\",\"name\":\"常用功能\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"CommonFunctions2\"},{\"key\":\"ToDoList\",\"name\":\"我的待办\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"ToDoList2\"},{\"key\":\"DayClear\",\"name\":\"日清\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"DayClear2\"},{\"key\":\"TourData\",\"name\":\"巡店数据\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"TourData2\"},{\"key\":\"DataOverview\",\"name\":\"数据概况\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"DataOverview2\"},{\"key\":\"LicenseData\",\"name\":\"证照数据\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"LicenseData2\"},{\"key\":\"WorkorderData\",\"name\":\"工单数据\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"WorkorderData2\"},{\"key\":\"RankingofUnqualifiedItems\",\"name\":\"不合格项排名\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"RankingofUnqualifiedItems2\"},{\"key\":\"TourRecord\",\"name\":\"巡店记录\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"TourRecord2\"},{\"key\":\"AverageScoreOfVisitedStores\",\"name\":\"已巡门店平均得分\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"AverageScoreOfVisitedStores2\"},{\"key\":\"ExecutionRanking\",\"name\":\"巡店执行力排名\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"ExecutionRanking2\"},{\"key\":\"DisplayData\",\"name\":\"陈列数据\",\"visible\":false,\"dragable\":true,\"configurable\":true,\"id\":\"DisplayData2\"},{\"key\":\"StoreTaskOverview\",\"name\":\"店务概况\",\"visible\":true,\"dragable\":true,\"configurable\":true,\"id\":\"StoreTaskOverview2\"}]}}', 'system', now(), 'system', now());

#4.首页模板映射;
DELETE from home_template_role_mapping_${enterpriseId};
INSERT INTO `home_template_role_mapping_${enterpriseId}` ( `id`, `template_id`, `role_id`, `create_id`, `create_time`, `update_id`, `update_time` )
VALUES
	( 1, 1, 20000000, 'system', now(), 'system', now() ),
	( 2, 1, 80000000, 'system', now(), 'system', now() ),
	( 3, 1, 40000000, 'system', now(), 'system', now() ),
	( 4, 1, 60000000, 'system', now(), 'system', now() ),
	( 5, 2, 30000000, 'system', now(), 'system', now() ),
	( 6, 2, 50000000, 'system', now(), 'system', now() ),
	( 7, 2, 70000000, 'system', now(), 'system', now() );

INSERT ignore INTO `import_distinct_${enterpriseId}`(`id`, `file_type`, `unique_field`, `unique_name`) VALUES (1, 'store', 'storeName', '门店名称');
INSERT ignore INTO `import_distinct_${enterpriseId}`(`id`, `file_type`, `unique_field`, `unique_name`) VALUES (2, 'store', 'storeNum', '门店编号');
INSERT ignore INTO `import_distinct_${enterpriseId}`(`id`, `file_type`, `unique_field`, `unique_name`) VALUES (3, 'user', 'userName', '用户名称');
INSERT ignore INTO `import_distinct_${enterpriseId}`(`id`, `file_type`, `unique_field`, `unique_name`) VALUES (4, 'user', 'jobnumber', '企业工号');
INSERT ignore INTO `import_distinct_${enterpriseId}`(`id`, `file_type`, `unique_field`, `unique_name`) VALUES (5, 'user', 'mobile', '手机号');

truncate table enterprise_brand_${enterpriseId};
INSERT INTO enterprise_brand_${enterpriseId} (code, name, extend_info, status, create_user_id, create_user_name, update_user_id, update_user_name, init_status) VALUES
('ORIGINAL', '默认品牌', NULL, 0, 'a100000001', 'AI用户', 'a100000001', 'AI用户', 1);

