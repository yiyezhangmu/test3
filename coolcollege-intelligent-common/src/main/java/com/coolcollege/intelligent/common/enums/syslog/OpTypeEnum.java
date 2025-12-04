package com.coolcollege.intelligent.common.enums.syslog;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * describe: 操作类型
 *
 * @author wangff
 * @date 2025/1/20
 */
@Getter
@AllArgsConstructor
public enum OpTypeEnum {
    DEFAULT("默认", null),

    INSERT("新增", null),
    EDIT("编辑", null),
    BATCH_EDIT("编辑", "批量编辑"),
    DELETE("删除", null),
    BATCH_DELETE("删除", "批量删除"),
    BATCH_MOVE("批量移动", "批量移动"),
    REMIND("催办", null),
    STOP("停止", null),
    REALLOCATE("重新分配", null),
    TOP_OR_NOT("置顶/取消置顶", null),
    BATCH_ARCHIVE("批量归档", "批量归档"),
    ARCHIVE("归档", null),
    RECOVERY("恢复使用", null),
    INSERT_GROUP("新增", "新增分组/分类"),
    UPDATE_GROUP("编辑", "编辑分组/分类"),
    DELETE_GROUP("删除", "删除分组/分类"),
    LOGIN("登录", null),
    FREEZE("冻结/解冻", null),
    EDIT_USER_STATUS("修改人事状态", null),

    // 导入导出
    IMPORT("导入", null),
    EXPORT("导出", null),

    // 巡店SOP
    SOP_COLUMN_BATCH_AUTH_SETTING("编辑", "批量配置权限"),

    // 巡店SOP档案库
	SOP_ARCHIVES_COLUMN_DELETE("删除", "检查项删除"),
	SOP_ARCHIVES_TABLE_DELETE("删除", "检查表删除"),

    // 巡店任务
    PATROL_STORE_TASK_BATCH_DELETE("删除", "批量删除"),
    PATROL_STORE_TASK_REMIND("催办", "巡店任务父任务催办"),
    PATROL_STORE_TASK_STORE_REMIND("催办", "巡店任务门店任务催办"),
    INSERT_BY_PERSON("新建", "按人任务新建"),
	// 工单
	QUESTION_RECORD_DELETE("删除", "删除子工单"),
    BATCH_REMIND("催办", "批量催办"),
	// 区域门店
	INSERT_PERSON("新增", "新增人员"),

	// 职位管理
	EDIT_DATA_FUNC_AUTH("编辑", "数据可见范围权限/功能权限"),
	EDIT_HOME_TEMPLATE("编辑", "首页模板"),
	CONFIG_PERSON("编辑", "配置人员"),
	REMOVE_PERSON("编辑", "移除人员"),

    // 设备集成
	DEVICE_AUTHORIZATION("授权", null),
	DEVICE_SYNC("同步", null),
    DEVICE_REFRESH("刷新", null),

    DEVICE_FORMAT("格式化", null),

    ;

    /**
     * 操作类型
     */
    private final String type;
    /**
     * 描述
     */
    private final String msg;

}
