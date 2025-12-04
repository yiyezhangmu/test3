package com.coolcollege.intelligent.model.storework.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 店务提交、点评消息发送体
 * @author wxp
 */
@Data
public class StoreWorkSubmitCommentMsgData implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 企业id
     */
    private String enterpriseId;

    private String type;

    private Long dataColumnId;

    private Long dataTableId;

    private Long storeWorkId;
    /**
     * 实际点评人
     */
    private String actualCommentUserId;

    /**
     * 点评来源于AI
     */
    private Boolean fromAi;
}
