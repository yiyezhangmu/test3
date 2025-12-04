package com.coolcollege.intelligent.controller.openApi;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceAuthAppEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.ObjectToJsonUtil;
import com.coolcollege.intelligent.common.util.sign.HmacSHATool;
import com.coolcollege.intelligent.model.device.dto.VideoDTO;
import com.coolcollege.intelligent.model.device.request.*;
import com.coolcollege.intelligent.model.device.vo.OpenDevicePageVO;
import com.coolcollege.intelligent.model.device.vo.OpenVideoUrlVO;
import com.coolcollege.intelligent.service.device.DeviceAuthService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class DeviceOpenController {

    @Resource
    private DeviceAuthService deviceAuthService;

    @PostMapping("/device/open/getLiveUrl")
    public ResponseResult<OpenVideoUrlVO> getLiveUrl(@Validated @RequestBody OpenDeviceVideoRequest request, HttpServletRequest httpRequest) {
        log.info("request:{}", JSONObject.toJSONString(request));
        if (!request.check()) {
            return ResponseResult.fail(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        String sign = httpRequest.getHeader("sign");
        String timestamp = httpRequest.getHeader("timestamp");
        String appId = httpRequest.getHeader("appId");
        String requestJson = ObjectToJsonUtil.toJsonForSignature(request);
        String signStr = appId + timestamp + requestJson;
        DeviceAuthAppEnum appEnum = DeviceAuthAppEnum.getByAppId(appId);
        if(appEnum == null){
            return ResponseResult.fail(ErrorCodeEnum.NOT_AUTH);
        }
        String signature = HmacSHATool.encodeHmacSHA256(signStr, appEnum.getAppSecret());
        log.info("appId:{},timestamp:{},sign:{},signature:{}", appId, timestamp, sign, signature);
        if(!signature.equals(sign)){
            return ResponseResult.fail(ErrorCodeEnum.SIGN_CHECK_ERROR);
        }
        if (appEnum.isHidden()){
            //默认饿了么
            appEnum = DeviceAuthAppEnum.eleme;
        }
        VideoDTO param = OpenDeviceVideoRequest.convert(request);
        param.setExpireTime(3600);
        param.setSliceType("ts");
        return ResponseResult.success(deviceAuthService.getLiveUrl(appEnum, param));
    }

    @PostMapping("/device/open/eleme/getLiveUrl")
    public ResponseResult<OpenVideoUrlVO> getElemeLiveUrl(@Validated @RequestBody ElemeOpenDeviceVideoRequest requestData, HttpServletRequest httpRequest) {
        log.info("request:{}", JSONObject.toJSONString(requestData));
        OpenDeviceVideoRequest request = requestData.getData();
        if (!request.check()) {
            return ResponseResult.fail(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        String sign = httpRequest.getHeader("sign");
        String timestamp = httpRequest.getHeader("timestamp");
        String appId = httpRequest.getHeader("appId");
        String requestJson = ObjectToJsonUtil.toJsonForSignature(requestData.getData());
        String signStr = appId + timestamp + requestJson;
        DeviceAuthAppEnum appEnum = DeviceAuthAppEnum.getByAppId(appId);
        if(appEnum == null){
            return ResponseResult.fail(ErrorCodeEnum.NOT_AUTH);
        }
        String signature = HmacSHATool.encodeHmacSHA256(signStr, appEnum.getAppSecret());
        log.info("appId:{},timestamp:{},sign:{}, 加签前signStr:{}, 计算得到的签名:signature:{}", appId, timestamp, sign, signStr, signature);
        if(!signature.equals(sign)){
            return ResponseResult.fail(ErrorCodeEnum.SIGN_CHECK_ERROR);
        }
        VideoDTO param = OpenDeviceVideoRequest.convert(request);
        param.setExpireTime(3600);
        return ResponseResult.success(deviceAuthService.getLiveUrl(appEnum, param));
    }


    @PostMapping("/device/open/eleme/notifyStoreOpen")
    public ResponseResult<Boolean> notifyStoreOpen(@Validated @RequestBody ElemeNotifyStoreOpenRequest requestData, HttpServletRequest httpRequest) {
        log.info("requestData:{}", JSONObject.toJSONString(requestData));
        ElemeStoreOpenRequest request = requestData.getData();
        String sign = httpRequest.getHeader("sign");
        String timestamp = httpRequest.getHeader("timestamp");
        String appId = httpRequest.getHeader("appId");
        String requestJson = ObjectToJsonUtil.toJsonForSignature(requestData.getData());
        String signStr = appId + timestamp + requestJson;
        DeviceAuthAppEnum appEnum = DeviceAuthAppEnum.getByAppId(appId);
        if(appEnum == null){
            return ResponseResult.fail(ErrorCodeEnum.NOT_AUTH);
        }
        String signature = HmacSHATool.encodeHmacSHA256(signStr, appEnum.getAppSecret());
        log.info("appId:{},timestamp:{},sign:{}, 加签前signStr:{}, 计算得到的签名:signature:{}", appId, timestamp, sign, signStr, signature);
        if(!signature.equals(sign)){
            return ResponseResult.fail(ErrorCodeEnum.SIGN_CHECK_ERROR);
        }
        deviceAuthService.storeOpenPushAuthDevice(appEnum, request);
        return ResponseResult.success(true);
    }


    @PostMapping("/device/open/getDevicePage")
    public ResponseResult<PageInfo<OpenDevicePageVO>> getDevicePage(@RequestBody OpenDevicePageRequest request, HttpServletRequest httpRequest) {
        log.info("request:{}", JSONObject.toJSONString(request));
        String sign = httpRequest.getHeader("sign");
        String timestamp = httpRequest.getHeader("timestamp");
        String appId = httpRequest.getHeader("appId");
        String requestJson = ObjectToJsonUtil.toJsonForSignature(request);
        String signStr = appId + timestamp + requestJson;
        DeviceAuthAppEnum appEnum = DeviceAuthAppEnum.getByAppId(appId);
        if(appEnum == null){
            return ResponseResult.fail(ErrorCodeEnum.NOT_AUTH);
        }
        String signature = HmacSHATool.encodeHmacSHA256(signStr, appEnum.getAppSecret());
        log.info("appId:{},timestamp:{},sign:{},signature:{}", appId, timestamp, sign, signature);
        if(!signature.equals(sign)){
            return ResponseResult.fail(ErrorCodeEnum.SIGN_CHECK_ERROR);
        }
        return ResponseResult.success(deviceAuthService.getDevicePage(request));
    }

    @GetMapping("/devicePush")
    public ResponseResult<Boolean> devicePush(@RequestParam("enterpriseId")String enterpriseId) {
        deviceAuthService.devicePush(enterpriseId);
        return ResponseResult.success(true);
    }

}