package com.coolcollege.intelligent.model.scheduler.request;

import lombok.Data;

/**
 * ScheduleCallBackRequest
 * @author 首亮
 */
@Data
public class ScheduleCallBackRequest {

    /**
     * 请求类型api,script,mq
     */
    private String type;

    private String action;

}
