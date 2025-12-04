package com.coolcollege.intelligent.common.http;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/08/20
 */
@Slf4j
public class YushiHttpClient {
    private final static Integer OK=200;

    public static String post(String url, JSONObject json ,Map<String, String> header ){
        String resultStr = CoolHttpClient.sendPostJsonRequest(url, JSONUtil.toJsonStr(json), header);
        if(StringUtils.isBlank(resultStr)){
            log.error("http yushi error! url={},json={},result={}",url,json,resultStr);
            throw new ServiceException(ErrorCodeEnum.YUS_DEVICE_7300000);
        }
        JSONObject yunResult = JSONObject.parseObject(resultStr);
        if ( !OK.equals(yunResult.getInteger("code"))) {
            Integer code = yunResult.getInteger("code");
            //不足5位前面补0
            String formatCode = String.format("%05d", code);
            String yingshiCode="72"+formatCode;
            ErrorCodeEnum byCode = ErrorCodeEnum.getByCode(Integer.valueOf(yingshiCode));
            if(byCode!=null){
                log.error("http yushi error! url={},json={},result={}",url,json,resultStr);
                throw new ServiceException(byCode);
            }
            log.error("http yushi error! url={},json={},result={}",url,json,resultStr);
            throw new ServiceException(ErrorCodeEnum.YUS_DEVICE_7300000);
        }
        return resultStr;
    }

    public static String post(String url, JSONObject json ){
        String resultStr = CoolHttpClient.sendPostJsonRequest(url, JSONUtil.toJsonStr(json));
        if(StringUtils.isBlank(resultStr)){
            log.error("http yushi error! url={},json={},result={}",url,json,resultStr);
            throw new ServiceException(ErrorCodeEnum.YUS_DEVICE_7300000);
        }
        JSONObject yunResult = JSONObject.parseObject(resultStr);
        if ( !OK.equals(yunResult.getInteger("code"))) {
            Integer code = yunResult.getInteger("code");
            //不足5位前面补0
            String formatCode = String.format("%05d", code);
            String yingshiCode="72"+formatCode;
            ErrorCodeEnum byCode = ErrorCodeEnum.getByCode(Integer.valueOf(yingshiCode));
            if(byCode!=null){
                log.error("http yushi error! url={},json={},result={}",url,json,resultStr);
                throw new ServiceException(byCode);
            }
            log.error("http yushi error! url={},json={},result={}",url,json,resultStr);
            throw new ServiceException(ErrorCodeEnum.YUS_DEVICE_7300000);
        }
        return resultStr;
    }
}
