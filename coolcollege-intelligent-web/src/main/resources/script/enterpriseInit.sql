#职位添加;
DELETE from sys_role_${enterpriseId} WHERE id in (20000000, 30000000, 40000000, 50000000, 60000000, 70000000, 80000000);
INSERT INTO sys_role_${enterpriseId} (
    `id`,
    `role_name`,
    `is_internal`,
    `role_auth`,
    `source`,
    `position_type`,
    `app_menu`,
    `priority`,
    `create_time`,
    `role_enum`
)
VALUES
(20000000, '管理员', 1, 'all', 'create', 'store_outside', '[{\"key\":\"offine\",\"name\":\"线下巡店\",\"checked\":true},{\"key\":\"online\",\"name\":\"线上巡店\",\"checked\":true},{\"key\":\"record\",\"name\":\"巡店记录\",\"checked\":true},{\"key\":\"display\",\"name\":\"标准陈列\",\"checked\":true},{\"key\":\"map\",\"name\":\"门店地图\",\"checked\":true},{\"key\":\"operate\",\"name\":\"运营看板\",\"checked\":true},{\"key\":\"social\",\"name\":\"圈子\",\"checked\":true},{\"key\":\"person\",\"name\":\"人员管理\",\"checked\":true},{\"key\":\"customer\",\"name\":\"客流分析\",\"checked\":true},{\"key\":\"marketing\",\"name\":\"顾客分析\",\"checked\":true},{\"key\":\"visitRecord\",\"name\":\"到访记录\",\"checked\":true}]', 1, now(), 'master'),
(80000000, '子管理员', 1, 'personal', 'create', 'store_outside', '[{\"key\":\"offine\",\"name\":\"线下巡店\",\"checked\":true},{\"key\":\"online\",\"name\":\"线上巡店\",\"checked\":true},{\"key\":\"record\",\"name\":\"巡店记录\",\"checked\":true},{\"key\":\"display\",\"name\":\"标准陈列\",\"checked\":true},{\"key\":\"map\",\"name\":\"门店地图\",\"checked\":true},{\"key\":\"operate\",\"name\":\"运营看板\",\"checked\":true},{\"key\":\"social\",\"name\":\"圈子\",\"checked\":true},{\"key\":\"person\",\"name\":\"人员管理\",\"checked\":true},{\"key\":\"customer\",\"name\":\"客流分析\",\"checked\":true},{\"key\":\"marketing\",\"name\":\"顾客分析\",\"checked\":true},{\"key\":\"visitRecord\",\"name\":\"到访记录\",\"checked\":true}]', 2, now(), 'sub_master'),
(30000000, '未分配', 1, 'personal', 'create', 'store_outside', '[{\"key\":\"offine\",\"name\":\"线下巡店\",\"checked\":true},{\"key\":\"online\",\"name\":\"线上巡店\",\"checked\":true},{\"key\":\"record\",\"name\":\"巡店记录\",\"checked\":true},{\"key\":\"display\",\"name\":\"标准陈列\",\"checked\":true},{\"key\":\"map\",\"name\":\"门店地图\",\"checked\":false},{\"key\":\"operate\",\"name\":\"运营看板\",\"checked\":false},{\"key\":\"social\",\"name\":\"圈子\",\"checked\":false},{\"key\":\"person\",\"name\":\"人员管理\",\"checked\":false},{\"key\":\"customer\",\"name\":\"客流分析\",\"checked\":false},{\"key\":\"marketing\",\"name\":\"顾客分析\",\"checked\":false},{\"key\":\"visitRecord\",\"name\":\"到访记录\",\"checked\":false}]', 99999999, now(), 'employee'),
(40000000, '部门负责人', 1, 'personal', 'create', 'store_inside', NULL, 10, now(), 'dept_leader'),
(50000000, '店长', 0, 'personal', 'create', 'store_inside', '[{\"key\":\"offine\",\"name\":\"线下巡店\",\"checked\":true},{\"key\":\"online\",\"name\":\"线上巡店\",\"checked\":true},{\"key\":\"record\",\"name\":\"巡店记录\",\"checked\":true},{\"key\":\"display\",\"name\":\"标准陈列\",\"checked\":true},{\"key\":\"map\",\"name\":\"门店地图\",\"checked\":false},{\"key\":\"operate\",\"name\":\"运营看板\",\"checked\":false},{\"key\":\"social\",\"name\":\"圈子\",\"checked\":false},{\"key\":\"person\",\"name\":\"人员管理\",\"checked\":false},{\"key\":\"customer\",\"name\":\"客流分析\",\"checked\":false},{\"key\":\"marketing\",\"name\":\"顾客分析\",\"checked\":false},{\"key\":\"visitRecord\",\"name\":\"到访记录\",\"checked\":false}]', 3, now(), 'shopowner'),
(60000000, '运营', 0, 'personal', 'create', 'store_inside', '[{\"key\":\"offine\",\"name\":\"线下巡店\",\"checked\":true},{\"key\":\"online\",\"name\":\"线上巡店\",\"checked\":true},{\"key\":\"record\",\"name\":\"巡店记录\",\"checked\":true},{\"key\":\"display\",\"name\":\"标准陈列\",\"checked\":true},{\"key\":\"map\",\"name\":\"门店地图\",\"checked\":false},{\"key\":\"operate\",\"name\":\"运营看板\",\"checked\":false},{\"key\":\"social\",\"name\":\"圈子\",\"checked\":false},{\"key\":\"person\",\"name\":\"人员管理\",\"checked\":false},{\"key\":\"customer\",\"name\":\"客流分析\",\"checked\":false},{\"key\":\"marketing\",\"name\":\"顾客分析\",\"checked\":false},{\"key\":\"visitRecord\",\"name\":\"到访记录\",\"checked\":false}]', 4, now(), 'operator'),
(70000000, '店员', 0, 'personal', 'create', 'store_inside', '[{\"key\":\"offine\",\"name\":\"线下巡店\",\"checked\":true},{\"key\":\"online\",\"name\":\"线上巡店\",\"checked\":true},{\"key\":\"record\",\"name\":\"巡店记录\",\"checked\":true},{\"key\":\"display\",\"name\":\"标准陈列\",\"checked\":true},{\"key\":\"map\",\"name\":\"门店地图\",\"checked\":false},{\"key\":\"operate\",\"name\":\"运营看板\",\"checked\":false},{\"key\":\"social\",\"name\":\"圈子\",\"checked\":false},{\"key\":\"person\",\"name\":\"人员管理\",\"checked\":false},{\"key\":\"customer\",\"name\":\"客流分析\",\"checked\":false},{\"key\":\"marketing\",\"name\":\"顾客分析\",\"checked\":false},{\"key\":\"visitRecord\",\"name\":\"到访记录\",\"checked\":false}]', 5, now(), 'clerk');

#设备场景初始化;
delete from store_scene_${enterpriseId} where id in (1,2,3);
INSERT INTO store_scene_${enterpriseId} (`id`, `name`, create_time, update_time,scene_type) VALUES
(1, '其他', now(), now(),'nothing'),
(2, '店外客流', now(), now(),'store_in_out'),
(3, '进店客流', now(), now(),'store_in');

#业绩分类;
DELETE from achievement_type_${enterpriseId} WHERE id < 4;
INSERT INTO `achievement_type_${enterpriseId}`(
    `id`,
    `create_time`,
    `create_user_id`,
    `create_user_name`,
    `update_user_id`,
    `update_user_name`,
    `name`
)
VALUES
(1, NOW(), 'system', 'system', 'system', 'system', '男鞋'),
(2, NOW(), 'system', 'system', 'system', 'system', '女装'),
(3, NOW(), 'system', 'system', 'system', 'system', '童装');

#业绩模板;
DELETE from achievement_formwork_${enterpriseId} WHERE id < 3;
INSERT INTO achievement_formwork_${enterpriseId}(
    `id`,
    `create_time`,
    `create_id`,
    `create_name`,
    `update_id`,
    `update_name`,
    `name`,
    `type`,
    `status`
)
VALUES
(1, NOW(), 'system', 'system', 'system', 'system', '日常销量提报', 'normal', 1),
(2, NOW(), 'system', 'system', 'system', 'system', '订货会销量提报', 'normal', 1);

#业绩模板类型映射;
DELETE from achievement_formwork_mapping_${enterpriseId} WHERE id < 3;
INSERT INTO achievement_formwork_mapping_${enterpriseId} (
    `id`,
    `formwork_id`,
    `type_id`,
    `status`
)
VALUES
(1, 1, 2, 1),
(2, 2, 2, 1);


#业绩目标;
DELETE from achievement_target_${enterpriseId} WHERE id = 1;
INSERT INTO achievement_target_${enterpriseId} (
    `id`,
    `create_time`,
    `store_id`,
    `store_name`,
    `region_id`,
    `region_path`,
    `achievement_year`,
    `year_achievement_target`,
    `create_user_id`,
    `create_user_name`,
    `update_user_id`,
    `update_user_name`,
    `store_num`
)
VALUES
(1, NOW(), 'default_store_id', '测试门店', 1, '/1/', 2022, 204000.00, 'system', 'system', 'system', 'system', NULL);


#业绩目标详情;
DELETE from achievement_target_detail_${enterpriseId} WHERE id < 13;
INSERT INTO achievement_target_detail_${enterpriseId} (
    `id`,
    `create_time`,
    `edit_time`,
    `target_id`,
    `store_id`,
    `store_name`,
    `region_id`,
    `region_path`,
    `time_type`,
    `begin_date`,
    `end_date`,
    `achievement_year`,
    `achievement_target`,
    `create_user_id`,
    `create_user_name`,
    `update_user_id`,
    `update_user_name`,
    `store_num`
)
VALUES
(1,now(),now(),1,'default_store_id','测试门店',1,'/1/','month', CONCAT(YEAR(NOW()),'-01-01'),last_day(CONCAT(YEAR(NOW()), '-01-01')),YEAR(NOW()),20000.00,'system','system','system','system',NULL),
(2,now(),now(),1,'default_store_id','测试门店',1,'/1/','month', CONCAT(YEAR(NOW()),'-02-01'),last_day(CONCAT(YEAR(NOW()), '-02-01')),YEAR(NOW()),18000.00,'system','system','system','system',NULL),
(3,now(),now(),1,'default_store_id','测试门店',1,'/1/','month', CONCAT(YEAR(NOW()),'-03-01'),last_day(CONCAT(YEAR(NOW()), '-03-01')),YEAR(NOW()),16000.00,'system','system','system','system',NULL),
(4,now(),now(),1,'default_store_id','测试门店',1,'/1/','month', CONCAT(YEAR(NOW()),'-04-01'),last_day(CONCAT(YEAR(NOW()), '-04-01')),YEAR(NOW()),16000.00,'system','system','system','system',NULL),
(5,now(),now(),1,'default_store_id','测试门店',1,'/1/','month', CONCAT(YEAR(NOW()),'-05-01'),last_day(CONCAT(YEAR(NOW()), '-05-01')),YEAR(NOW()),17000.00,'system','system','system','system',NULL),
(6,now(),now(),1,'default_store_id','测试门店',1,'/1/','month', CONCAT(YEAR(NOW()),'-06-01'),last_day(CONCAT(YEAR(NOW()), '-06-01')),YEAR(NOW()),15000.00,'system','system','system','system',NULL),
(7,now(),now(),1,'default_store_id','测试门店',1,'/1/','month',CONCAT(YEAR(NOW()),'-07-01'),last_day(CONCAT(YEAR(NOW()), '-07-01')),YEAR(NOW()),15000.00,'system','system','system','system',NULL),
(8,now(),now(),1,'default_store_id','测试门店',1,'/1/','month',CONCAT(YEAR(NOW()),'-08-01'),last_day(CONCAT(YEAR(NOW()), '-08-01')),YEAR(NOW()),18000.00,'system','system','system','system',NULL),
(9,now(),now(),1,'default_store_id','测试门店',1,'/1/','month',CONCAT(YEAR(NOW()),'-09-01'),last_day(CONCAT(YEAR(NOW()), '-09-01')),YEAR(NOW()),18000.00,'system','system','system','system',NULL),
(10,now(),now(),1,'default_store_id','测试门店',1,'/1/','month',CONCAT(YEAR(NOW()),'-10-01'),last_day(CONCAT(YEAR(NOW()), '-10-01')),YEAR(NOW()),17000.00,'system','system','system','system',NULL),
(11,now(),now(),1,'default_store_id','测试门店',1,'/1/','month',CONCAT(YEAR(NOW()),'-11-01'),last_day(CONCAT(YEAR(NOW()), '-11-01')),YEAR(NOW()),16000.00,'system','system','system','system',NULL),
(12,now(),now(),1,'default_store_id','测试门店',1,'/1/','month',CONCAT(YEAR(NOW()),'-12-01'),last_day(CONCAT(YEAR(NOW()), '-12-01')),YEAR(NOW()),18000.00,'system','system','system','system',
 NULL);

#业绩明细;
DELETE from achievement_detail_${enterpriseId} WHERE id < 3;
INSERT INTO `achievement_detail_${enterpriseId}` (
    `id`,
    `create_time`,
    `produce_time`,
    `edit_time`,
    `store_id`,
    `store_name`,
    `region_id`,
    `region_path`,
    `achievement_type_id`,
    `achievement_amount`,
    `create_user_id`,
    `create_user_name`,
    `produce_user_id`,
    `produce_user_name`,
    `deleted`,
    `achievement_formwork_id`,
    `achievement_formwork_type`
)
VALUES
(1,now(),date_format(now(),'%y-%m-%d'),now(),'default_store_id','测试门店',1,'/1/',2,300.00,'system','system','system','system',0,1,'normal'),
(2,now(),date_format(now(),'%y-%m-%d'),now(),'default_store_id','测试门店',1,'/1/',2,880.00,'system','system','system','system',0,2,'normal');



#初始化3个根区域以及测试门店对应的区域;
DELETE from region_${enterpriseId} WHERE id IN (-3);
INSERT INTO `region_${enterpriseId}` (
    `id`,
    `name`,
    `parent_id`,
    `create_time`,
    `create_name`,
    `region_type`,
    `region_path`,
    `deleted`,
    `store_num`,
    `store_id`
)
VALUES
(-3, '测试门店', 1, UNIX_TIMESTAMP(NOW())*1000, 'system', 'store', '/1/', 0, 0, 'default_store_id');

#绑定开通人与测试门店的区域权限;
DELETE from user_auth_mapping_${enterpriseId} WHERE id = 1;
INSERT INTO `user_auth_mapping_${enterpriseId}` (
    `id`,
    `user_id`,
    `mapping_id`,
    `type`,
    `source`,
    `create_id`,
    `create_time`
)
VALUES
(1, #{userId}, '-3', 'region', 'create', 'system', UNIX_TIMESTAMP(NOW())*1000);

#初始化测试门店;
DELETE from store_${enterpriseId} WHERE id = 1;
INSERT INTO `store_${enterpriseId}` (
    `id`,
    `store_id`,
    `store_name`,
    `region_id`,
    `store_address`,
    `location_address`,
    `is_lock`,
    `longitude_latitude`,
    `longitude`,
    `latitude`,
    `is_delete`,
    `create_time`,
    `create_name`,
    `region_path`,
    `has_camera`,
    `store_status`,
    `address_point`
)
VALUES
(1,'default_store_id','测试门店',1,'北京市东城区东华门街道天安门','北京市东城区东华门街道天安门','not_locked','116.397451,39.909187','116.397451','39.909187','effective',UNIX_TIMESTAMP(NOW())*1000,'system','/1/-3/',1,'open',ST_GeomFromText('POINT(116.397451 39.909187)'));

#初始化门店分组;
DELETE from store_group_${enterpriseId} WHERE id = 1;
INSERT INTO `store_group_${enterpriseId}` (
    `id`,
    `group_id`,
    `group_name`,
    `create_time`,
    `create_user`
)
VALUES
(1, #{groupId}, '默认分组', UNIX_TIMESTAMP(NOW())* 1000, 'system');

#绑定测试门店致默认分组;
DELETE from store_group_mapping_${enterpriseId} WHERE id = 1;
INSERT INTO `store_group_mapping_${enterpriseId}` (
    `id`,
    `store_id`,
    `group_id`,
    `create_time`,
    `create_user`
)
VALUES
(1, 'default_store_id', #{groupId}, UNIX_TIMESTAMP(NOW())* 1000, 'system');

#初始化sop文档;
DELETE from task_sop_${enterpriseId} WHERE id < 4;
INSERT INTO `task_sop_${enterpriseId}` (
    `id`,
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
    `visible_role_name`
)
VALUES
(1,'陈列管理2022.pptx','https://oss-cool.coolstore.cn/doc/sop/display-management.pptx','pdf','pdf','system','system',NOW(),NULL,NULL,NOW(),0,'','','',''),
(2,'巡店管理2022.pptx','https://oss-cool.coolstore.cn/doc/sop/patrol-management.pptx','pdf','pdf','system','system',NOW(),NULL,NULL,NOW(),0,'','','',''),
(3,'门店管理2022pptx.pptx','https://oss-cool.coolstore.cn/doc/sop/system-store.pptx','pdf','pdf','system','system',NOW(),NULL,NULL,NOW(),0,'','','','');


#初始化角色菜单权限;
DELETE from sys_role_menu_v2_${enterpriseId};
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 585, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 589, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 600, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 33, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 37, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 634, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 666, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 611, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 577, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 26, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 29, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 18, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 12, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 605, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 585, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 589, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 600, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 33, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 37, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 634, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 666, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 611, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 577, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 26, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 29, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 18, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 12, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 605, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 585, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 589, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 600, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 33, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 37, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 634, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 666, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 611, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 577, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 26, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 29, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 18, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 12, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 605, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 585, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 589, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 600, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 33, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 37, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 634, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 666, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 611, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 577, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 26, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 29, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 18, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 12, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 605, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2020, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2020, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2020, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2020, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2020, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2021, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2021, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2021, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2021, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2021, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2022, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2022, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2022, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2022, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2022, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2023, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2023, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2023, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2023, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 2023, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4002, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4006, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4016, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4017, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4022, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4023, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4015, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4631, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4632, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4024, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5084, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5085, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5086, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5087, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5088, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5089, 30000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4002, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4006, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4016, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4017, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4023, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4015, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4631, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4632, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4024, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4022, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5084, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5085, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5086, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5087, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5088, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5089, 40000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4002, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4006, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4016, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4017, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4023, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4015, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4631, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4632, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4024, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4022, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5084, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5085, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5086, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5087, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5088, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5089, 50000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4002, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4006, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4016, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4017, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4023, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4015, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4631, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4632, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4024, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4022, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5084, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5085, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5086, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5087, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5088, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5089, 60000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4002, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4006, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4016, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4017, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4023, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4015, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4631, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4632, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4024, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4022, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5084, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5085, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5086, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5087, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5088, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 5089, 70000000, 'NEW_APP');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4385, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4362, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4383, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4356, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4357, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4470, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4469, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4359, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4358, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4360, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4361, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4362, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4363, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4364, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4365, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4366, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4367, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4368, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4369, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4370, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4384, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4385, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4468, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4374, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4379, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4541, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4543, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4545, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4547, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4549, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4551, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4554, 60000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4385, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4362, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4383, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4356, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4357, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4470, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4469, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4359, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4358, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4360, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4361, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4362, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4363, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4364, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4365, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4366, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4367, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4368, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4369, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4370, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4384, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4385, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4468, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4374, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4379, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4541, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4543, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4545, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4547, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4549, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4551, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4554, 30000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4385, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4362, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4383, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4356, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4357, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4470, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4469, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4359, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4358, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4360, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4361, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4362, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4363, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4364, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4365, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4366, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4367, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4368, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4369, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4370, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4384, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4385, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4468, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4374, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4379, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4541, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4543, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4545, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4547, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4549, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4551, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4554, 50000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4385, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4362, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4383, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4356, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4357, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4470, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4469, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4359, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4358, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4360, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4361, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4362, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4363, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4364, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4365, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4366, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4367, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4368, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4369, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4370, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4384, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4385, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4468, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4374, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4379, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4541, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4543, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4545, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4547, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4549, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4551, 70000000, 'PC');
INSERT INTO `sys_role_menu_v2_${enterpriseId}`( `menu_id`, `role_id`, `platform`) VALUES ( 4554, 70000000, 'PC');

-- 检查项分类
delete from `tb_meta_column_category_${enterpriseId}` where id < 4;
INSERT INTO `tb_meta_column_category_${enterpriseId}`(`id`, `category_name`, `order_num`, `is_default`, `create_id`) VALUES (1, '日常检查', 0, 0, 'system');
INSERT INTO `tb_meta_column_category_${enterpriseId}`(`id`, `category_name`, `order_num`, `is_default`, `create_id`) VALUES (2, 'AI检查项', 0, 1, 'system');
INSERT INTO `tb_meta_column_category_${enterpriseId}`(`id`, `category_name`, `order_num`, `is_default`, `create_id`) VALUES (3, '其他', 0, 1, 'system');

-- 快速检查项
delete from `tb_meta_quick_column_${enterpriseId}` where id < 17;
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`question_handler_name`,`create_user`) VALUES (1, '开门打烊', '正常开门营业，无推迟现象，无提前闭店现象', 'position', '50000000', '', 1,  NULL, 0, NULL, 0.6, 1, 1.00, 1.00,'店长','system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`question_handler_name`,`create_user`) VALUES (2, '晨会', '晨会是否按照流程进行', 'position', '50000000', '', 1, NULL, 0, NULL, 0.6, 1, 1.00, 1.00,'店长','system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`question_handler_name`,`create_user`) VALUES (3, '货架卫生', '货架无杂物乱放行为',  'position', '50000000', '', 1,  NULL, 0, NULL, 0.6, 1, 1.00, 1.00,'店长','system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`question_handler_name`,`create_user`) VALUES (4, '价签', '价签：一物一签，无手工涂改，标签字迹清晰无破损、变色现象',  'position', '50000000', '', 1,  NULL, 0, NULL, 0.6, 1, 1.00, 1.00,'店长','system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`question_handler_name`,`create_user`) VALUES (5, '陈列', '商品陈列：商品陈列是否整齐，不凌乱',  'position', '50000000', '', 1,  NULL, 0, NULL, 0.6, 1, 1.00, 1.00,'店长','system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`question_handler_name`,`create_user`) VALUES (6, '空缺货位', '空缺货位：门店陈列位货品空缺货位',  'position', '50000000', '', 1,  NULL, 0, NULL, 0.6, 1, 1.00, 1.00,'店长','system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`question_handler_name`,`create_user`) VALUES (7, '环境卫生', '地面：门店内外地面干净，无垃圾纸屑、无死角',  'position', '50000000', '', 1,  NULL, 0, NULL, 0.6, 1, 1.00, 1.00,'店长','system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`question_handler_name`,`create_user`) VALUES (8, '培训', '每日日训记录完整，必须有培训具体内容', 'position', '50000000', '', 1,  NULL, 0, NULL, 0.6, 1, 1.00, 1.00,'店长','system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`create_user`) VALUES (9, '厨师帽', '厨师帽',  NULL, NULL, 'position', 10,  1, 6, 'hat', 0.6, 2, 0.00, 0.00,'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`create_user`) VALUES (10, '口罩', '口罩',  NULL, NULL, 'position', 10,  1, 6, 'mask', 0.6, 2, 0.00, 0.00,'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`create_user`) VALUES (11, '厨师服', '厨师服',  NULL, NULL, 'position', 10,  1, 6, 'uniform', 0.6, 2, 0.00, 0.00,'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`create_user`) VALUES (12, '老鼠', '老鼠',  NULL, NULL, 'position', 10,  1, 6, 'mouse', 0.6, 2, 0.00, 0.00,'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`create_user`) VALUES (13, '垃圾桶满溢', '垃圾桶满溢',  NULL, NULL, 'position', 10,  1, 6, 'trash', 0.6, 2, 0.00, 0.00,'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`create_user`) VALUES (14, '趴桌睡觉', '趴桌睡觉',  NULL, NULL, 'position', 10,  1, 6, 'sleep', 0.6, 2, 0.00, 0.00,'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`create_user`) VALUES (15, '玩手机', '玩手机',  NULL, NULL, 'position', 10,  1, 6, 'mobile', 0.6, 2, 0.00, 0.00,'system');
INSERT INTO `tb_meta_quick_column_${enterpriseId}`(`id`, `column_name`, `description`,  `question_handler_type`, `question_handler_id`, `question_cc_type`, `support_score`, `store_scene_id`, `column_type`, `ai_type`, `threshold`, `category_id`, `min_score`, `max_score`,`create_user`) VALUES (16, '吸烟', '吸烟',  NULL, NULL, 'position', 10,  1, 6, 'smoking', 0.6, 2, 0.00, 0.00,'system');


update `tb_meta_quick_column_${enterpriseId}` set create_user = 'system', create_user_name = 'system', edit_user_name = 'system' where id < 11;

-- 快速检查项结果项
delete from `tb_meta_quick_column_result_${enterpriseId}` where id < 49;
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (1, 1, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (2, 1, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (3, 1, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (4, 2, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (5, 2, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (6, 2, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (7, 3, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (8, 3, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (9, 3, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (10, 4, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (11, 4, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (12, 4, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (13, 5, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (14, 5, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (15, 5, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (16, 6, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (17, 6, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (18, 6, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (19, 7, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (20, 7, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (21, 7, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (22, 8, '合格', 1.00, 1.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (23, 8, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (24, 8, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (25, 9, '合格', 10.00, 0.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (26, 9, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (27, 9, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (28, 10, '合格', 10.00, 0.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (29, 10, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (30, 10, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (31, 11, '合格', 10.00, 0.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (32, 11, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (33, 11, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (34, 12, '合格', 10.00, 0.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (35, 12, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (36, 12, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (37, 13, '合格', 10.00, 0.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (38, 13, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (39, 13, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (40, 14, '合格', 10.00, 0.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (41, 14, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (42, 14, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (43, 15, '合格', 10.00, 0.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (44, 15, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (45, 15, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (46, 16, '合格', 10.00, 0.00, 0.00, 'PASS', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (47, 16, '不合格', 0.00, 0.00, 0.00, 'FAIL', 'ignore', 'system');
INSERT INTO `tb_meta_quick_column_result_${enterpriseId}`(`id`, `meta_quick_column_id`, `result_name`, `max_score`, `min_score`, `default_money`, `mapping_result`, `description`, `create_user_id`) VALUES (48, 16, '不适用', 0.00, 0.00, 0.00, 'INAPPLICABLE', 'ignore', 'system');

-- sop表
delete from `tb_meta_table_${enterpriseId}` where id < 4;
INSERT INTO `tb_meta_table_${enterpriseId}`(`id`, `table_name`, `create_user_id`, `create_user_name`, `support_score`, `locked`, `active`, `table_type`, `edit_user_id`, `edit_user_name`, `level_rule`, `level_info`, `category_name_list`, `order_num`, `total_score`, `table_property`,`sop_path`) VALUES (1, '基础运营', 'system', 'system', 0, 1, 1, 'STANDARD', 'system', 'system', 'SCORING_RATE', '{\"levelList\":[{\"keyName\":\"excellent\",\"percent\":90,\"qualifiedNum\":6},{\"keyName\":\"good\",\"percent\":80,\"qualifiedNum\":4},{\"keyName\":\"eligible\",\"percent\":60,\"qualifiedNum\":2},{\"keyName\":\"disqualification\",\"percent\":0,\"qualifiedNum\":0}],\"open\":true}', '[\"日常检查\"]', 1, 0.00, 0,'/-1/-2/');
INSERT INTO `tb_meta_table_${enterpriseId}`(`id`, `table_name`, `create_user_id`, `create_user_name`, `support_score`, `locked`, `active`, `table_type`, `edit_user_id`, `edit_user_name`, `level_rule`, `level_info`, `category_name_list`, `order_num`, `total_score`, `table_property`) VALUES (2, '全国门店月陈列反馈检查表', 'system', 'system', 1, 1, 1, 'TB_DISPLAY', NULL, NULL, NULL, NULL, NULL, 2, 0.00, 0);
INSERT INTO `tb_meta_table_${enterpriseId}`(`id`, `table_name`, `create_user_id`, `create_user_name`, `support_score`, `locked`, `active`, `table_type`, `edit_user_id`, `edit_user_name`, `level_rule`, `level_info`, `category_name_list`, `order_num`, `total_score`, `table_property`) VALUES (3, '华北区元旦促销陈列检查', 'system', 'system', 1, 0, 1, 'TB_DISPLAY', NULL, NULL, NULL, NULL, NULL, 3, 0.00, 1);
INSERT INTO `tb_meta_table_${enterpriseId}`(`id`, `table_name`, `create_user_id`, `create_user_name`, `sop_type`, `sop_path`,`no_applicable_rule`, `table_property`) VALUES (-2, '默认分组', 'system', 'system', 'leaf','/-1/','0','-1');

-- sop表中检查项
delete from `tb_meta_sta_table_column_${enterpriseId}` where id < 4;
INSERT INTO `tb_meta_sta_table_column_${enterpriseId}`(`id`, `category_name`, `meta_table_id`, `column_name`, `description`, `question_handler_type`, `question_handler_id`, `create_user_id`, `support_score`, `level`, `quick_column_id`, `threshold`) VALUES (1, '日常检查', 1, '开门打烊', '正常开门营业，无推迟现象，无提前闭店现象', 'position', '50000000', 'system', 1.00, 'general', 0, 0.4);
INSERT INTO `tb_meta_sta_table_column_${enterpriseId}`(`id`, `category_name`, `meta_table_id`, `column_name`, `description`, `question_handler_type`, `question_handler_id`, `create_user_id`, `support_score`, `level`, `quick_column_id`, `threshold`) VALUES (2, '日常检查', 1, '晨会', '晨会是否按照流程进行', 'position', '50000000', 'system', 1.00, 'general', 0, 0.4);
INSERT INTO `tb_meta_sta_table_column_${enterpriseId}`(`id`, `category_name`, `meta_table_id`, `column_name`, `description`, `question_handler_type`, `question_handler_id`, `create_user_id`, `support_score`, `level`, `quick_column_id`, `threshold`) VALUES (3, '日常检查', 1, '货架卫生', '货架无杂物乱放行为', 'position', '50000000', 'system', 1.00, 'general', 0, 0.4);

delete from `tb_meta_column_result_${enterpriseId}` where id < 10;
-- sop表中检查项的结果项
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`, `result_name`, `mapping_result`, `description`, `max_score`, `min_score`) VALUES (1, 1, 1, 'system', '合格', 'PASS', 'ignore', 1.00, 1.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`, `result_name`, `mapping_result`, `description`, `max_score`, `min_score`) VALUES (2, 1, 1, 'system', '不合格', 'FAIL', 'ignore', 0.00, 0.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`, `result_name`, `mapping_result`, `description`, `max_score`, `min_score`) VALUES (3, 1, 1, 'system', '不适用', 'INAPPLICABLE', 'ignore', 0.00, 0.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`, `result_name`, `mapping_result`, `description`, `max_score`, `min_score`) VALUES (4, 1, 2, 'system', '合格', 'PASS', 'ignore', 1.00, 1.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`, `result_name`, `mapping_result`, `description`, `max_score`, `min_score`) VALUES (5, 1, 2, 'system', '不合格', 'FAIL', 'ignore', 0.00, 0.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`, `result_name`, `mapping_result`, `description`, `max_score`, `min_score`) VALUES (6, 1, 2, 'system', '不适用', 'INAPPLICABLE', 'ignore', 0.00, 0.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`, `result_name`, `mapping_result`, `description`, `max_score`, `min_score`) VALUES (7, 1, 3, 'system', '合格', 'PASS', 'ignore', 1.00, 1.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`, `result_name`, `mapping_result`, `description`, `max_score`, `min_score`) VALUES (8, 1, 3, 'system', '不合格', 'FAIL', 'ignore', 0.00, 0.00);
INSERT INTO `tb_meta_column_result_${enterpriseId}`(`id`, `meta_table_id`, `meta_column_id`, `create_user_id`, `result_name`, `mapping_result`, `description`, `max_score`, `min_score`) VALUES (9, 1, 3, 'system', '不适用', 'INAPPLICABLE', 'ignore', 0.00, 0.00);



#1.1 标准检查表对应检查项信息;
DELETE from tb_meta_display_table_column_${enterpriseId} WHERE id IN (1,2,3);
INSERT `tb_meta_display_table_column_${enterpriseId}` (
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
	(1,NOW(),NOW(),2,'全景照片','system',1,'https://oss-cool.coolstore.cn/pic/6ea450e02f8044e98b3fdf91463a46ef.png','店铺需将双十一物料都陈列好后，拍下清晰的全景照片',0,'system','','',0,10,0),
	(2,NOW(),NOW(),2,'货架A标准图','system',2,'https://oss-cool.coolstore.cn/pic/655c4ef1461d4442acb231c2fe8ddd13.png','请按照标准图上传货架A照片',0,'system','','',0,10,0),
	(3,NOW(),NOW(),2,'门头标准图','system',3,'https://oss-cool.coolstore.cn/pic/0b7649071e71489aafd864b5e527f386.png','请按照标准图上传门头照片',0,'system','','',0,10,0);

#2.1陈列高级检查表对应检查项;
DELETE from tb_meta_display_table_column_${enterpriseId} WHERE id IN (4,5,6,7,8);
INSERT INTO `tb_meta_display_table_column_${enterpriseId}`(
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
    `score`,
    `check_type`
)
VALUES
(4,now(),now(),3,'整洁度','system',1,'','',0,'system',	'',	'',40,0),
(5,now(),now(),3,'氛围','system',2,'','',0,'system','','',30,0),
(6,now(),now(),3,'灯光','system',3,'','',0,'system','','',30,0),
(7,now(),now(),3,'门头','system',1,'https://oss-cool.coolstore.cn/pic/0b7649071e71489aafd864b5e527f386.png','',0,'system','','',40,1),
(8,now(),now(),3,'橱窗','system',2,'https://oss-cool.coolstore.cn/pic/6ea450e02f8044e98b3fdf91463a46ef.png','',0,'system','','',30,1);

#3.陈列快速检查项;
DELETE from tb_meta_display_quick_column_${enterpriseId} WHERE id < 4;
INSERT INTO `tb_meta_display_quick_column_${enterpriseId}` (
    `id`,
    `create_time`,
    `edit_time`,
    `column_name`,
    `create_user_id`,
    `create_user_name`,
    `deleted`,
    `score`,
    `standard_pic`,
    `description`,
    `check_type`
)
VALUES
(1, now(), now(), '卫生', 'system', 'system', 0, 10, '', '', 0),
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
