package com.coolcollege.intelligent.service.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.model.enums.ScheduleCallBackEnum;
import com.coolcollege.intelligent.model.scheduler.request.SchedulerAddRequest;

/**
 * @author shuchang.wei
 * @date 2021/4/25 9:59
 */
public interface ScheduleService {
    /**
     * 删除定时任务
     *
     * @param enterpriseId 企业id
     * @param scheduleId   定时任务id
     * @return
     */
    Boolean deleteSchedule(String enterpriseId, String scheduleId);

    /**
     * 新增定时任务
     *
     * @param enterpriseId 企业id
     * @param callBackUrl  回调地址
     * @param callBackType 回调方式
     * @param request      新增请求
     * @return 调度Id
     */
    JSONObject addSchedule(String enterpriseId, String callBackUrl, ScheduleCallBackEnum callBackType, String request);


}
