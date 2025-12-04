package com.coolcollege.intelligent.common.enums.video;

/**
 * video status
 */
public enum UploadTypeEnum {

    /**
     * 检查项提交
     */
    TB_DATA_STA_TABLE_COLUMN(1),

    /**
     * 巡店记录总结
     */
    TB_PATROL_STORE_RECORD(2),

    /**
     * 工单创建
     */
    QUESTION_CREATE(3),

    /**
     *工单处理审核提交
     */
    QUESTION_SUMMIT(4),

    /**
     * 父工单创建
     */
    QUESTION_PARENT_CREATE(5),

    /**
     * 店务提交
     */
    STORE_WORK_SUBMIT(6),

    /**
     * 运营手册
     */
    TASK_SOP_ADD(7),

    DEVICE_CAPTURE(8),


    /**
     * 巡店自定义检查项提交
     */
    TB_DATA_DEF_TABLE_COLUMN(9),


    /**
     * 督导自定义检查项提交
     */
    SUPERVISION_DATA_DEF_TABLE_COLUMN(10),

    ACTIVITY_COMMENT(11),

    WEEKLY_NEWSPAPER_LIST(12),

    /**
     * 陈列检查项提交
     */
    TB_DISPLAY_TABLE_DATA_COLUMN(13),
    /**
     * 陈列检查内容提交
     */
    TB_DISPLAY_TABLE_DATA_CONTENT(14),

    /**
     * 云图库提交
     */
    STORE_CLOUD(15),
    
    /**
     * 食安稽核申诉
     */
    DATA_COLUMN_APPEAL(16),
    ;


    private final Integer value;

    UploadTypeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
