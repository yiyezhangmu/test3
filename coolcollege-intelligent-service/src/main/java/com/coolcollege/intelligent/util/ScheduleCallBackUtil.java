package com.coolcollege.intelligent.util;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.model.enums.ScheduleCallBackEnum;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleCallBackRequest;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;

/**
 * @author 邵凌志
 * @date 2020/7/16 14:39
 */
public class ScheduleCallBackUtil {

    public static ScheduleCallBackRequest getCallBack(String action, String type) {
        ScheduleCallBackRequest callBackRequest = new ScheduleCallBackRequest();
        callBackRequest.setType(type);
        if (Objects.equals(type, ScheduleCallBackEnum.api.getValue())) {
            JSONObject apiObj = new JSONObject();
            apiObj.put("url", action);
            callBackRequest.setAction(apiObj.toJSONString());
        } else {
            callBackRequest.setAction(action);
        }
        return callBackRequest;
    }

    /**
     * 构建请求header
     *
     * @return Map
     */
    public static Map<String, String> buildHeaderMap() {
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put("app", "DIGITAL_STORE");
        headerMap.put("Content-Type", "application/json");
        return headerMap;
    }
}
