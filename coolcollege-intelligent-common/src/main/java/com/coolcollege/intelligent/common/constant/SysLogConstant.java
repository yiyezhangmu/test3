package com.coolcollege.intelligent.common.constant;

/**
 * describe: 系统日志常量池
 *
 * @author wangff
 * @date 2025/1/21
 */
public class SysLogConstant {
    /**
     * 预处理结果
     */
    public static final String PREPROCESS_RESULT = "preprocessResult";

    public static final String INSERT = "新增了";
    public static final String UPDATE = "修改了";
    public static final String DELETE = "删除了";
    public static final String REMIND = "催办了";
    public static final String STOP = "停止了";
    public static final String REALLOCATE = "重新分配了";
    public static final String ARCHIVE = "归档了";
    public static final String BATCH_UPDATE = "批量修改了";
    public static final String REMOVE = "移除了";

    // 操作内容默认模板
    public static class Template {
        
        public static final String INSERT_TEMPLATE = "新增了{0}「{1}」";
        public static final String INSERT_TEMPLATE2 = "新增了{0}「{1}({2})」";
        public static final String UPDATE_TEMPLATE = "修改了{0}「{1}」";
        public static final String UPDATE_TEMPLATE2 = "修改了{0}「{1}({2})」";
        public static final String DELETE_TEMPLATE = "删除了{0}「{1}」";
        public static final String DELETE_TEMPLATE2 = "删除了{0}{1}";
        public static final String REMIND_TEMPLATE = "催办了{0}「{1}」";
        public static final String REMIND_TEMPLATE2 = "催办了{0}{1}";
        public static final String STOP_TEMPLATE = "停止了{0}「{1}」";
        public static final String ARCHIVE_TEMPLATE = "归档了{0}{1}";
        public static final String RECOVERY_TEMPLATE = "恢复使用了{0}{1}";
        public static final String RECOVERY_TEMPLATE2 = "恢复使用了{0}「{1}」";
        public static final String BATCH_ITEM_TEMPLATE = "「{0}({1})」";
        public static final String BATCH_ITEM_TEMPLATE2 = "「{0}」";
        public static final String COMMON_TEMPLATE = "{0}{1}";

        /**
         * 店务重新分配/催办模板
         */
        public static final String STORE_WORK_SPECIAL_TEMPLATE = "{0}门店「{1}({2})」「{3}」的{4}「{5}」";

        /**
         * 巡店SOP检查项
         */
        public static final String SOP_COLUMN_BATCH_AUTH_SETTING = "批量修改了检查项{0}的配置权限";

        /**
         * 任务批量模板
         */
        public static final String TASK_STORE_TEMPLATE = "{0}{1}任务「{2}({3})」中{4}的门店任务";

        /**
         * 工单
         */
        public static final String QUESTION_BATCH_REMIND_TEMPLATE = "批量催办了{0}的未完成工单";
        public static final String QUESTION_REMIND_TEMPLATE = "催办了「{0}({1})」的未完成工单";
        public static final String QUESTION_SUB_TEMPLATE = "{0}「{1}({2})」的子工单「{3}({4})」";
        public static final String QUESTION_SUB_ITEM_TEMPLATE = "「{0}({1})」的子工单「{2}({3})」";
        public static final String QUESTION_INSERT_TEMPLATE = "发起了工单「{0}({1})」";
        public static final String QUESTION_DELETE_TEMPLATE = "删除了「{0}({1})」及其所有子工单";

        /**
         * 表单巡店
         */
        public static final String PATROL_STORE_FORM_INSERT_TEMPLATE = "提交了「{0}({1})」的表单巡店，巡店记录ID为「{2}」";

        /**
         * 区域门店
         */
        public static final String REGION_STORE_TEMPLATE = "{0}成员{1}至{2}「{3}」";

        /**
         * 职位管理
         */
        public static final String POSITION_EDIT_TEMPLATE = "修改了职位「{0}({1})」的{2}";
        public static final String POSITION_EDIT_PERSON_TEMPLATE = "{0}职位「{1}({2})」的人员配置，{3}：{4}";

        /**
         * 设备集成
         */
        public static final String AUTHORIZATION_TEMPLATE = "授权了「{0}」";
        public static final String SYNC_DEVICE_TEMPLATE = "同步了「{0}」";
    }

}
