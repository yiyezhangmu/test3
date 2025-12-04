package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 按门店发送子任务统一消息发送体
 * @author wxp
 */
@Data
public class CombineUpcomingCancelData implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 门店id
     */
    private String dingCorpId;

    private String appType;

    private Long unifyTaskId;

    private Long loopCount;

    private String handleUserId;

}
