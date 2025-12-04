package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/31 16:24
 */
@Data
public class UnifyParentUser {

    private Long subTaskId;

    private Long unifyTaskId;

    private String userId;

    private Long loopCount;

    private String nodeNo;

    private Long subBeginTime;

    private Long subEndTime;

    private String taskType;
}
