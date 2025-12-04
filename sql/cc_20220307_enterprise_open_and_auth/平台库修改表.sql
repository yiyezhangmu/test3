-- ## 配置表
ALTER TABLE `enterprise_settings`
    ADD COLUMN `sync_subordinate_change` tinyint(4) DEFAULT '0' COMMENT '下级变动是否继续同步下级';