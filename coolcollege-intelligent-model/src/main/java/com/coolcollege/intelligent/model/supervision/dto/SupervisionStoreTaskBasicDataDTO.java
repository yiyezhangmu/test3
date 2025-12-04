package com.coolcollege.intelligent.model.supervision.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2023/4/17 17:12
 * @Version 1.0
 */
@Data
public class SupervisionStoreTaskBasicDataDTO {

    private Long supervisionTaskId;

    /**
     * 总数
     */
    private Long count;
    /**
     * 过滤取消的总数
     */
    private Long filterCancelCount;

    private Long handleNum;

    private Long completeNum;

    private Long approveNum;

    private Long handleOverTimeNum;

    private Long cancelNum;

    private Date maxSubmitTime;

    private Date maxCompleteTime;

}
