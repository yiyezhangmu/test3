package com.coolcollege.intelligent.service.schedule.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.http.HttpRequest;
import com.coolcollege.intelligent.model.enums.ScheduleCallBackEnum;
import com.coolcollege.intelligent.service.schedule.ScheduleService;
import com.coolcollege.intelligent.util.ScheduleCallBackUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author shuchang.wei
 * @date 2021/4/25 10:02
 */
@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {
    @Value("${scheduler.api.url}")
    private String schedulerApiUrl;

    @Override
    public Boolean deleteSchedule(String enterpriseId, String scheduleId) {
        if(log.isInfoEnabled()){
            log.info("删除调度器，开始调用定时器enterpriseId={},scheduleId={}", enterpriseId, scheduleId);
        }
        String coolStoreResult = HttpRequest.sendDelete(schedulerApiUrl + "/v2/" + enterpriseId + "/schedulers/" + scheduleId, "", ScheduleCallBackUtil.buildHeaderMap());
        String coolcollegeResult = HttpRequest.sendDelete("https://schedulerapi.coolcollege.cn/v2/" + enterpriseId + "/schedulers/" + scheduleId, "", ScheduleCallBackUtil.buildHeaderMap());
        log.info("deleteSchedule  enterpriseId:{} scheduleId:{} coolStoreResult:{}",enterpriseId, scheduleId, coolStoreResult);
        log.info("deleteSchedule  enterpriseId:{} scheduleId:{} coolcollegeResult:{}",enterpriseId, scheduleId, coolcollegeResult);
        JSONObject coolStoreResultSchedule = JSONObject.parseObject(coolStoreResult);
        JSONObject coolcollegeSchedule = JSONObject.parseObject(coolcollegeResult);
        boolean coolStoreDelete = !Objects.isNull(coolStoreResultSchedule) && coolStoreResultSchedule.getBoolean("deleted") != null && coolStoreResultSchedule.getBoolean("deleted");
        boolean coolcollegeDelete = !Objects.isNull(coolcollegeSchedule) && coolcollegeSchedule.getBoolean("deleted") != null && coolcollegeSchedule.getBoolean("deleted");
        return coolStoreDelete || coolcollegeDelete;
    }

    @Override
    public JSONObject addSchedule(String enterpriseId, String callBackUrl, ScheduleCallBackEnum callBackType, String request) {
        if(log.isInfoEnabled()){
            log.info("新增调度器，开始调用定时器enterpriseId={},开始调用参数={}", enterpriseId , request);
        }
        String schedule = HttpRequest.sendPost(schedulerApiUrl + "/v2/" + enterpriseId + "/schedulers", request, ScheduleCallBackUtil.buildHeaderMap());
        JSONObject jsonObjectSchedule = JSONObject.parseObject(schedule);
        if(log.isInfoEnabled()){
            log.info("新增调度器，结束调用定时器enterpriseId={},返回结果={}", enterpriseId, jsonObjectSchedule);
        }
        return jsonObjectSchedule;
    }
}
