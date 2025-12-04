package com.coolcollege.intelligent.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.enums.ResponseCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.util.ParamFormatUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.*;

/**
 * @Description 统一响应体处理器
 * @author Aaron
 * @date 2019/12/20
 */
@ControllerAdvice(annotations = BaseResponse.class)
@Slf4j
public class ResponseResultHandlerAdvice implements ResponseBodyAdvice{

    /**
     * 拦截配置
     * @param returnType
     * @param converterType
     * @return boolean
     */
    @Override
    public boolean supports(MethodParameter returnType, Class converterType){
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response){
    	String urlPath=request.getURI().getPath();
        if(body instanceof ResponseResult){
            Object data = ((ResponseResult) body).getData();
            if (data instanceof List) {
                ((ResponseResult) body).setData(formatList(data,urlPath));
                return body;
            }
            ((ResponseResult) body).setData(formatObject(data,urlPath));
            return body;
        }
        if(body instanceof String){
            return JSON.toJSONString(new ResponseResult<>(ResponseCodeEnum.SUCCESS.getCode(), ResponseCodeEnum.SUCCESS.getMessage(), formatObject(body,urlPath)));
        }
        return new ResponseResult(ResponseCodeEnum.SUCCESS.getCode(), ResponseCodeEnum.SUCCESS.getMessage(), formatObject(body,urlPath));
    }


    /**
     * 规范队列
     * @Description 规范队列
     * @param data
     * @return Object
     */
    public Object formatList(Object data,String urlPath) {
    	if(urlPath!=null && urlPath.indexOf("/v3/")>=0) {
    		return data;
    	}
        List<Object> list = Lists.newArrayList();
        for (Object dataChild : (List) data) {
            Object dataNew = formatObject(dataChild,urlPath);
            if (dataNew != null) {
                list.add(dataNew);
            }
        }
        return list;
    }


    /**
     * 规范对象
     * @Description 规范对象
     * @param data
     * @return Object
     * @throws Exception
     */
    public Object formatObject(Object data,String urlPath) {
    	if(urlPath!=null && urlPath.indexOf("/v3/")>=0) {
    		return data;
    	}
        if(Objects.isNull(data)){
            return null;
        }
        if (data instanceof List) {
            if(ObjectUtils.isEmpty(data)){
                return new ArrayList();
            }
            return formatList(data,urlPath);
        }
        JSONObject json;
        try {
            json = (JSONObject) JSONObject.toJSON(data);
        } catch (Exception e) {
            return data;
        }
        JSONObject jsonNew = new JSONObject();
        if (json == null) {
            return null;
        }
        Set<String> jsonKeys = json.keySet();
        Iterator<String> it = jsonKeys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            String newKey = ParamFormatUtil.HumpToUnderline(key);
            if (StringUtils.isEmpty(json.get(key))) {
                jsonNew.put(newKey, json.get(key));
                continue;
            }
            if (json.get(key) instanceof List) {
                Object o = formatList(json.get(key),urlPath);
                jsonNew.put(newKey, o);
            } else {
                Object o = formatObject(json.get(key),urlPath);
//                if (o instanceof Boolean) {
//                    o = o.toString();
//                }
                jsonNew.put(newKey, o);
            }
        }
        return jsonNew;
    }


}
