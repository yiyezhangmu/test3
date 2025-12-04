package com.coolcollege.intelligent.common.enums;

/**
 * 统一消息类型
 * @author byd
 */

public enum MsgUniteDataTypeEnum {

    /**
     * 菜单类型
     */
    PATROL_RECORD_EXPORT("patrol_record_export","导出巡店记录"),
    EXPORT_TEMP_RES_RECORD_EXPORT("export_temp_res_record_export","导出标准检查表"),
    EXPORT_TEMP_RES_DEF_RECORD_EXPORT("export_temp_res_def_record_export","导出自定义检查表"),
    DING_DEPT_USER_SYNC_ALL("ding_dept_user_sync_all","钉钉同步全部用户"),
    EXPORT_BASE_DETAIL_RECORD_EXPORT("export_base_detail_record_export","导基础详情表导出记录"),
    ACHIEVEMENT_DETAIL_EXPORT("export_achievement_detail_export","导出业绩详情"),
    PATROL_STORE_TASK_REPORT_EXPORT("patrol_store_task_report_export","巡店任务报表"),
    TASK_QUESTION_REPORT("task_question_report","导出工单报表"),
    EXPORT_TASK_STAGE_LIST_RECORD("export_task_stage_list_record","巡店记录数据导出"),
    EXPORT_TASK_STAGE_LIST_RECORD_DETAIL("export_task_stage_list_record_detail","巡店记录明细数据导出"),
    EXPORT_FILE_COMMON("export_file_common","统一导出"),
    DISPLAY_HAS_EXPORT("display_has_export","陈列导出带图片"),
    TB_QUESTION_RECORD("tb_question_record","导出问题工单"),
    SUB_QUESTION_DETAIL("sub_question_detail","工单详情表"),
    REGION_STORE_QUESTION_REPORT("region_store_question_report","区域门店工单报表"),
    QUESTION_LIST("question_list","工单列表"),
    PATROL_STORE_DETAIL("patrol_store_detail","巡店明细"),
    STOREWORK_STORE_STATISTICS("storework_store_statistics","门店统计"),
    STOREWORK_REGION_STATISTICS("storework_region_statistics","区域统计"),
    STOREWORK_DAY_STATISTICS("storework_day_statistics","日报表统计"),

    STOREWORK_STORERECORD_LIST("storework_storerecord_list","店务记录"),
    STOREWORK_STORERECORD_DETAIL_LIST("storework_storerecord_detail_list","店务记录明细"),
    STOREWORK_TABLE_LIST("storework_table_list","检查表记录"),
    STOREWORK_COLUMN_LIST("storework_column_list","检查项记录"),
    DEVICE_LIST("device_list","设备列表"),
    DEVICE_SUMMARY_LIST("device_summary_list","门店设备汇总列表"),
    REGION_EXECUTIVE_LIST_REPORT("region_executive_list_report","区域执行力"),
    SUPERVISION_DATA_EXPORT("supervision_data_export","督导助手数据导出"),
    SUPERVISION_DATA_STORE_EXPORT("supervision_data_store_export","督导助手按门店数据导出"),
    SUPERVISION_DATA_DETAIL_EXPORT("supervision_data_detail_export","督导助手任务明细导出"),
    CONFIDENCE_FEEDBACK_EXPORT("confidence_feedback_export","督导助手任务明细导出"),
    ACTIVITY_USER("activity_user","活动人员列表导出"),
    ACTIVITY_COMMENT("activity_comment","活动评论导出"),
    WEEKLY_NEWSPAPER_LIST("weekly_newspaper_list","周报列表导出"),
    PATROL_STORE_REVIEW_LIST_EXPORT("patrol_store_review_list_export","线上稽核记录"),

    EXTERNAL_USER_LIST("external_user_list","外部用户列表"),
    EXTERNAL_REGION_LIST("external_region_list","外部组织架构列表"),
    EXPORT_CHECK_LIST("export_check_list","稽核概览列表"),
    EXPORT_CHECK_DETAIL_LIST("export_check_detail_list","稽核详情列表"),
    EXPORT_CHECK_ANALYZE_LIST("export_check_analyze_list","稽核分析列表"),
    ;

    private String code;
    private String msg;

    MsgUniteDataTypeEnum(String code, String msg){
        this.code=code;
        this.msg=msg;
    }

    public static MsgUniteDataTypeEnum getByCode(String code){
        for (MsgUniteDataTypeEnum value : MsgUniteDataTypeEnum.values()) {
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
