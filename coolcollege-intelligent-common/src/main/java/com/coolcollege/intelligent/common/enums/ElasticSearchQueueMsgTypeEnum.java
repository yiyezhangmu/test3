package com.coolcollege.intelligent.common.enums;

/**
 * ElasticSearch队列消息类型
 * @author Admin
 */

public enum ElasticSearchQueueMsgTypeEnum {

    /**
     * 检查表
     */
    TB_DATA_TABLE_COLUMN("tb_data_table_column", "检查表", "hz_{0}_tb_data_table_column"),

    /**
     * 巡店记录
     */
    TB_PATROL_STORE_RECORD("tb_patrol_store_record", "巡店记录", "hz_{0}_store_index_tb_patrol_store_record"),

    /**
     * 门店子任务
     */
    UNIFY_TASK_STORE("unify_task_store", "门店子任务", "hz_{0}_unify_task_store")
    ;

    private String code;
    private String msg;
    private String indexName;

    /**
     * 聚合查询支持的最大数量
     */
    public static final int AGGS_TERMS_MAX_SIZE = 2000;


    ElasticSearchQueueMsgTypeEnum(String code, String msg, String indexName) {
        this.code = code;
        this.msg = msg;
        this.indexName = indexName;
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

    public String getIndexName() {
        return indexName;
    }
}
