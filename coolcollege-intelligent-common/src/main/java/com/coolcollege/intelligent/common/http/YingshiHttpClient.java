package com.coolcollege.intelligent.common.http;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import java.util.Map;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/08/17
 */
@Slf4j
public class YingshiHttpClient {
    private final static Integer OK=200;

    public static String post(String url, Map<String, String> map ){
        String resultStr = CoolHttpClient.sendPostFormRequest(url, map);
        if(StringUtils.isBlank(resultStr)){
            log.error("http yingshi error! url={},map={},result={}",url,map,resultStr);
            throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000000);
        }
        JSONObject yunResult = JSONObject.parseObject(resultStr);
        if ( !OK.equals(yunResult.getInteger("code"))) {
            Integer code = yunResult.getInteger("code");
            //不足5位前面补0
            String formatCode = String.format("%05d", code);
            String yingshiCode="71"+formatCode;
            ErrorCodeEnum byCode = ErrorCodeEnum.getByCode(Integer.valueOf(yingshiCode));
            if(byCode!=null){
                log.error("http yingshi error! url={},map={},result={}",url,map,resultStr);
                throw new ServiceException(byCode);
            }
            log.error("http yingshi error! url={},map={},result={}",url,map,resultStr);
            throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000000);
        }
        return resultStr;
    }

    public static String postForm(String url, Map<String, String> headers, Map<String, Object> form) {
        String resultStr = HttpUtil.createPost(url).addHeaders(headers).form(form).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).execute().body();
        log.info("yingshi api postForm url:{}, headers:{}, form:{}, result:{}", url, headers, form, resultStr);
        verifyResult(resultStr);
        return resultStr;
    }

    public static String putForm(String url, Map<String, String> headers, Map<String, Object> form) {
        String resultStr = HttpRequest.put(url).addHeaders(headers).form(form).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).execute().body();
        log.info("yingshi api putForm url:{}, headers:{}, form:{}, result:{}", url, headers, form, resultStr);
        verifyResult(resultStr);
        return resultStr;
    }

    public static String get(String url, Map<String, String> headers) {
        String resultStr = HttpUtil.createGet(url).addHeaders(headers).execute().body();
        log.info("yingshi api get url:{}, headers:{}, result:{}", url, headers, resultStr);
        verifyResult(resultStr);
        return resultStr;
    }

    public static void verifyResult(String resultStr) {
        if (StringUtils.isBlank(resultStr)) {
            throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000000);
        }
        JSONObject yunResult;
        try {
            yunResult = JSONObject.parseObject(resultStr);
        } catch (Exception e) {
            throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000000);
        }
        Integer code = yunResult.getInteger("code");
        String message = null;
        if (yunResult.containsKey("meta")) {
            JSONObject meta = yunResult.getJSONObject("meta");
            if (meta.containsKey("code")) {
                code = meta.getInteger("code");
            }
            if (meta.containsKey("message")) {
                message = meta.getString("message");
            }
        }
        if (!OK.equals(code)) {
            //不足5位前面补0
            String formatCode = String.format("%05d", code);
            String yingshiCode = "71" + formatCode;
            ErrorCodeEnum byCode = ErrorCodeEnum.getByCode(Integer.valueOf(yingshiCode));
            if (byCode != null) {
                throw new ServiceException(byCode);
            }
            if(StringUtils.isNotBlank(message)){
                throw new ServiceException(ErrorCodeEnum.ERROR, message);
            }
            throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000000);
        }
    }
}
