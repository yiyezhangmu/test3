package com.coolcollege.intelligent.model.elasticSearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2021/8/11 14:42
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticSearchQueueMsg<T> implements Serializable {
    private static final long serialVersionUID = 14551645645416515L;

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * ElasticSearchQueueMsgTypeEnum
     * 消息类型
     */
    private String msgType;

    /**
     * 数据来源类型
     */
    private String dataSourceType;

    /**
     * 消息数据体（json结构的array字符串）
     */
    private T data;

    /**
     * 模糊搜索字段 需要拼接在searchContent中的字段
     */
    private List<String> searchFields;
}
