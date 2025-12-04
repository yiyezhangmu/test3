package com.coolcollege.intelligent.model.scheduler.request;

import lombok.Data;

/**
 * ScheduleCallBackRequest
 * @author 首亮
 */
@Data
public class ScheduleJobRequest {

    /**
     * 请求类型api,script,mq
     */
    private String scheduleId;

    private String action;

    private String eid;

}
