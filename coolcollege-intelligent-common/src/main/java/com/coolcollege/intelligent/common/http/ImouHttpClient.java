package com.coolcollege.intelligent.common.http;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2022/04/25
 */
@Slf4j
public class ImouHttpClient{
    private static final String SUCCESS="0";

    public static String post(String url, String jsonRequest ){
        log.info("http imou request! url={},map={}",url,jsonRequest);
        String resultStr = CoolHttpClient.sendPostJsonRequest(url, jsonRequest);
        log.info("http imou response! url={},map={},result={}",url,jsonRequest,resultStr);

        if(StringUtils.isBlank(resultStr)){
            log.error("http imou error! url={},map={},result={}",url,jsonRequest,resultStr);
            throw new ServiceException(ErrorCodeEnum.LECHENG_DEVICE_7400000);
        }
        JSONObject result = JSONObject.parseObject(resultStr);
        if ( !SUCCESS.equals(result.getJSONObject("result").getString("code"))) {
            String errorMsg = result.getJSONObject("result").getString("msg");
            throw new ServiceException(ErrorCodeEnum.LECHENG_DEVICE_7400001,errorMsg);
        }
        return result.getString("result");

    }
}
