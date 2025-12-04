package com.coolcollege.intelligent.model.workFlow;

import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: WorkflowDataDTO
 * @Description:
 * @date 2024-01-29 10:09
 */
@Data
public class WorkflowDealDTO {

    private String enterpriseId;

    private String cid;

    private String bizCode;

    private Long cycleCount;

    private String nextNodeNo;

    private String beforeNodeNo;

    private String actionKey;

    private String turnFromUserId;

    private String turnToUserId;

    private Boolean endFlag;

    private String taskData;

    private Long subTaskId;

    private Long unifyTaskId;

    private String storeId;

    private Long loopCount;

    private String remark;

    private String createUserId;

    private String data;

    private String primaryKey;

}
