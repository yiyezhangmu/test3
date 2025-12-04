package com.coolcollege.intelligent.common.enums.elasticSearch;

/**
 * @Author suzhuhong
 * @Date 2021/8/11 14:15
 * @Version 1.0
 */
public enum ElasticSearchQueueMsgTypeEnum {
    /**
     * 检查 表
     */
    TB_DATA_TABLE_COLUMN("tb_data_table_column", "检查表"),

    /**
     * 巡店记录
     */
    TB_PATROL_STORE_RECORD("tb_patrol_store_record", "巡店记录"),

    /**
     * 陈列任务记录
     */
    TB_DISPLAY_TABLE_RECORD("tb_display_table_record","陈列任务记录"),
    /**
     * 门店任务记录
     */
    UNIFY_TASK_STORE("unify_task_store","门店任务记录"),

    /**
     * 陈列记录检查项数据
     */
    TB_DISPLAY_TABLE_DATA_COLUMN("tb_display_table_data_column","陈列记录检查项数据");

    private String code;
    private String msg;

    ElasticSearchQueueMsgTypeEnum(String code, String msg){
        this.code=code;
        this.msg=msg;
    }

    public static ElasticSearchQueueMsgTypeEnum getByCode(String code){
        for (ElasticSearchQueueMsgTypeEnum value : ElasticSearchQueueMsgTypeEnum.values()) {
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
