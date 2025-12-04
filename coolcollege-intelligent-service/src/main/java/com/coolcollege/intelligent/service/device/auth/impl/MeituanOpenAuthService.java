package com.coolcollege.intelligent.service.device.auth.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.ObjectToJsonUtil;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.common.util.sign.HmacSHATool;
import com.coolcollege.intelligent.model.device.EnterpriseAuthDeviceDO;
import com.coolcollege.intelligent.model.device.request.MeiTuanDeviceAuthRequest;
import com.coolcollege.intelligent.model.device.request.MeiTuanDeviceUnBindRequest;
import com.coolcollege.intelligent.model.device.response.meituan.DeviceAuthResponse;
import com.coolcollege.intelligent.model.device.response.meituan.MTBaseResponse;
import com.coolcollege.intelligent.service.device.auth.OpenAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 美团设备授权服务实现
 */
@Slf4j
@Service("meituanOpenAuthService")
public class MeituanOpenAuthService implements OpenAuthService {

    @Value("${mei.tuan.url.prefix:null}")
    private String meiTuanUrlPrefix;

    @Value("${mei.tuan.appId:null}")
    private String appId;

    @Value("${mei.tuan.appSecret:null}")
    private String appSecret;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public Boolean authDevice(List<EnterpriseAuthDeviceDO> authDeviceList) {
        for (MeiTuanDeviceAuthRequest meiTuanDeviceAuthRequest : MeiTuanDeviceAuthRequest.convert(authDeviceList)) {
            bindDevice(meiTuanDeviceAuthRequest);
        }

        return false;
    }

    @Override
    public Boolean authDevice(EnterpriseAuthDeviceDO authDevice) {
        // 美团单个设备授权逻辑
        bindDevice(MeiTuanDeviceAuthRequest.convert(authDevice));
        return true;
    }

    @Override
    public Boolean cancelDevice(EnterpriseAuthDeviceDO authDevice) {
        MeiTuanDeviceUnBindRequest cancelDevice = MeiTuanDeviceUnBindRequest.convert(authDevice);
        unbindDevice(cancelDevice);
        return true;
    }

    @Override
    public Boolean cancelDevice(List<EnterpriseAuthDeviceDO> authDeviceList) {
        List<MeiTuanDeviceUnBindRequest> cancelDeviceList = MeiTuanDeviceUnBindRequest.convert(authDeviceList);
        for (MeiTuanDeviceUnBindRequest cancelDevice : cancelDeviceList) {
            unbindDevice(cancelDevice);
        }
        return false;
    }

    /**
     * 设备绑定
     * @param request
     * @return
     */
    public String bindDevice(MeiTuanDeviceAuthRequest request) {
        String url = "/foodsafety/api/auth/thirdparty/device/bind";
        DeviceAuthResponse response = sendPostRequest(url, request, DeviceAuthResponse.class);
        log.info("美团bindDevice: {}", JSONObject.toJSONString(response));
        return response.getBindId();
    }

    /**
     * 设备解绑
     * @param request
     */
    public void unbindDevice(MeiTuanDeviceUnBindRequest request) {
        String url = "/foodsafety/api/auth/thirdparty/device/unbind";
        String s = sendPostRequest(url, request, String.class);
    }

    public  <T> T sendPostRequest(String url, Object body, Class<T> responseType){
        String requestUrl = meiTuanUrlPrefix + url;
        String bodyStr = ObjectToJsonUtil.toJsonForSignature(body);
        MTBaseResponse tResponseEntity = null;
        int tryCount = 0;
        while ((Objects.isNull(tResponseEntity) || tResponseEntity.getCode() != 0) && tryCount < 3){
            long timestamp = System.currentTimeMillis();
            HttpHeaders headers = new HttpHeaders();
            headers.add("appId", appId);
            headers.add("version", "v1.0");
            headers.add("timestamp", String.valueOf(timestamp));
            headers.add("requestId", UUIDUtils.get32UUID());
            String signStr = appId + timestamp + "v1.0" + bodyStr;
            String signature = HmacSHATool.encodeHmacSHA256(signStr, appSecret);
            headers.add("signature", signature);
            HttpEntity<Object> entity = new HttpEntity<>(body, headers);
            log.info("sendPostRequest: url={}, entity={}", requestUrl, JSONObject.toJSONString(entity));
            tResponseEntity = restTemplate.postForObject(requestUrl, entity, MTBaseResponse.class);
            tryCount++;
        }
        if(tResponseEntity.getCode() != 0){
            log.error("sendPostResponse: tryCount:{}, response:{}", tryCount, JSONObject.toJSONString(tResponseEntity));
            throw new ServiceException(ErrorCodeEnum.ERROR, "美团接口调用错误，错误信息:" + tResponseEntity.getMsg());
        }
        log.info("sendPostResponse: tryCount:{}, response:{}", tryCount, JSONObject.toJSONString(tResponseEntity));
        return JSONObject.parseObject(JSONObject.toJSONString(tResponseEntity.getData()), responseType);
    }

}