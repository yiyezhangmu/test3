package com.coolcollege.intelligent.common.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.YunShiTongResponse;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: YunShiTongHttpClient
 * @Description: 云视通 请求封装
 * @date 2022-12-07 16:42
 */
@Slf4j
@Service
public class YunShiTongHttpClient {

    @Autowired
    private RestTemplate restTemplate;

    public <T> T postForObject(String url, Object request, Class<T> responseType) {
        log.info("postForObject start:url={},request={},responseType={}", url, JSONObject.toJSONString(request),
                responseType.getName());
        YunShiTongResponse result = null;
        try {
            result = restTemplate.postForObject(url, getHttpEntity(request, null), YunShiTongResponse.class);
            log.info("postForObject end:result={}", JSONObject.toJSONString(result));
        } catch (RestClientException e) {
            log.error("postForObject error:{}", e);
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
        if(Objects.nonNull(result) && result.getCode() == 1000){
            T resultData = JSONObject.parseObject(JSONObject.toJSONString(result.getData()), responseType);
            return resultData;
        }
        String errorMessage = StatusCode.getYunShiTongErrorMessage(result.getCode());
        throw new ServiceException(ErrorCodeEnum.ERROR, errorMessage);
    }

    public <T> T postForObject(String url, Object request, String accessToken, Class<T> responseType) {
        log.info("postForObject start:url={},request={},accessToken:{}", url, JSONObject.toJSONString(request), accessToken);
        YunShiTongResponse result = null;
        try {
            result = restTemplate.postForObject(url, getHttpEntity(request, accessToken), YunShiTongResponse.class);
        } catch (RestClientException e) {
            log.error("postForObject error:{}", e);
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
        log.info("result:{}", JSON.toJSONString(result));
        if(Objects.nonNull(result) && result.getCode() == 1000){
            T resultData = JSONObject.parseObject(JSONObject.toJSONString(result.getData()), responseType);
            return resultData;
        }
        if(Objects.nonNull(result) && result.getCode() == 1006){
            //token 过期
            throw new ServiceException(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR);
        }
        Integer statusCode = Objects.isNull(result) ? null : result.getCode();
        String errorMessage = StatusCode.getYunShiTongErrorMessage(statusCode);
        throw new ServiceException(ErrorCodeEnum.ERROR, errorMessage);
    }


    public HttpEntity getHttpEntity(Object object, String accessToken) {
        return new HttpEntity(object, this.getHeaders(accessToken));
    }

    private MultiValueMap<String, String> getHeaders(String accessToken) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap();
        headers.add("X-Token", accessToken);
        headers.add("Content-Type", "application/json");
        return headers;
    }

    public static class StatusCode{
        /**
         * 云视通状态码映射
         */
        public static final Map<Integer, String> YUNSHITONG_STATUS_CODE_MAP = new HashMap<>();

        static{
            // 新增的状态码
            YUNSHITONG_STATUS_CODE_MAP.put(429, "请求过于频繁");
            YUNSHITONG_STATUS_CODE_MAP.put(1000, "成功");
            YUNSHITONG_STATUS_CODE_MAP.put(1001, "参数格式错误");
            YUNSHITONG_STATUS_CODE_MAP.put(1002, "请求异常");
            YUNSHITONG_STATUS_CODE_MAP.put(1003, "appKey 或 appSecret 错误");
            YUNSHITONG_STATUS_CODE_MAP.put(1004, "租户已被冻结");
            YUNSHITONG_STATUS_CODE_MAP.put(1006, "AccessToken 异常或过期");
            YUNSHITONG_STATUS_CODE_MAP.put(1008, "内部服务异常");
            YUNSHITONG_STATUS_CODE_MAP.put(1011, "该租户已经生成的 ak sk 超过最大限制数");
            YUNSHITONG_STATUS_CODE_MAP.put(1012, "该 ak 生成的 token 数超过最大限制，默认一个 ak 一天可以生成 1000 个");
            YUNSHITONG_STATUS_CODE_MAP.put(1016, "租户不存在");
            YUNSHITONG_STATUS_CODE_MAP.put(1032, "平台服务已暂停，请及时充值并保证账户余额充足");
            YUNSHITONG_STATUS_CODE_MAP.put(2002, "设备不在线");
            YUNSHITONG_STATUS_CODE_MAP.put(2003, "设备校验码错误");
            YUNSHITONG_STATUS_CODE_MAP.put(2005, "设备已被添加");
            YUNSHITONG_STATUS_CODE_MAP.put(2006, "设备能力不支持");
            YUNSHITONG_STATUS_CODE_MAP.put(2007, "设备属于专属项目，无添加权限");
            YUNSHITONG_STATUS_CODE_MAP.put(2008, "设备不存在");
            YUNSHITONG_STATUS_CODE_MAP.put(2009, "视频通道不合法");
            YUNSHITONG_STATUS_CODE_MAP.put(2011, "sd 卡不存在");
            YUNSHITONG_STATUS_CODE_MAP.put(2012, "设备语音文件未设置");
            YUNSHITONG_STATUS_CODE_MAP.put(2013, "无有效设备");
            YUNSHITONG_STATUS_CODE_MAP.put(2014, "通道不在线");
            YUNSHITONG_STATUS_CODE_MAP.put(2017, "通道未启用");
            YUNSHITONG_STATUS_CODE_MAP.put(2018, "平台 ID 已存在");
            YUNSHITONG_STATUS_CODE_MAP.put(2019, "平台 ID 不存在");
            YUNSHITONG_STATUS_CODE_MAP.put(3002, "云台移动超时或异常");
            YUNSHITONG_STATUS_CODE_MAP.put(3003, "云台停止移动超时或异常");
            YUNSHITONG_STATUS_CODE_MAP.put(3004, "聚焦和光圈控制超时或异常");
            YUNSHITONG_STATUS_CODE_MAP.put(3005, "停止控制聚焦和光圈超时或异常");
            YUNSHITONG_STATUS_CODE_MAP.put(4001, "获取链接地址异常");
            YUNSHITONG_STATUS_CODE_MAP.put(4002, "获取语音对讲链接地址异常");
            YUNSHITONG_STATUS_CODE_MAP.put(4003, "本地资源不存在");
            YUNSHITONG_STATUS_CODE_MAP.put(4004, "streamId 不合法");
            YUNSHITONG_STATUS_CODE_MAP.put(5001, "已超过该套餐可用限额");
            YUNSHITONG_STATUS_CODE_MAP.put(5003, "套餐不存在");
            YUNSHITONG_STATUS_CODE_MAP.put(5004, "没有可用套餐");
            YUNSHITONG_STATUS_CODE_MAP.put(5005, "未开启云存设置");
            YUNSHITONG_STATUS_CODE_MAP.put(5006, "云端录像资源不存在");
            YUNSHITONG_STATUS_CODE_MAP.put(5007, "租户已欠费");
            YUNSHITONG_STATUS_CODE_MAP.put(6000, "布防时段超过限制");
            YUNSHITONG_STATUS_CODE_MAP.put(6001, "布防区域不合法");
            YUNSHITONG_STATUS_CODE_MAP.put(6002, "有人形检测的能力，不能操作移动检测，移动检测和人形检测互斥");
            YUNSHITONG_STATUS_CODE_MAP.put(6003, "无设备权限");
            YUNSHITONG_STATUS_CODE_MAP.put(6004, "三方code信息无效");
            YUNSHITONG_STATUS_CODE_MAP.put(6005, "appid不存在");
            YUNSHITONG_STATUS_CODE_MAP.put(6006, "授权回调地址不匹配");
            YUNSHITONG_STATUS_CODE_MAP.put(6007, "code生成错误");
            YUNSHITONG_STATUS_CODE_MAP.put(6008, "AppSecret信息错误");
            YUNSHITONG_STATUS_CODE_MAP.put(9005, "账户余额不足");
        }

        /**
         * 根据状态码获取新增的错误信息
         * @param statusCode 状态码
         * @return 错误信息，如果状态码不存在则返回"未知错误码"
         */
        public static String getYunShiTongErrorMessage(Integer statusCode) {
            return YUNSHITONG_STATUS_CODE_MAP.getOrDefault(statusCode, "调用云视通接口错误");
        }
    }

}
