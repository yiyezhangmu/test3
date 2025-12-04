package com.coolcollege.intelligent.controller.device;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dto.EnterpriseConfigDTO;
import com.coolcollege.intelligent.model.device.request.JfyCaptureCallbackRequest;
import com.coolcollege.intelligent.model.device.request.TPLinkNoticePushRequest;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolcollege.intelligent.service.video.openapi.VideoServiceApi;
import com.coolcollege.intelligent.service.video.openapi.impl.JfyOpenServiceImpl;
import com.coolcollege.intelligent.service.video.openapi.impl.TPVideoOpenServiceImpl;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@Api(tags = "设备授权")
@RestController
@RequestMapping({"/v3/device/callback/{enterprise-id}"})
public class DeviceCallBackController {

    @Autowired
    private EnterpriseConfigApiService enterpriseConfigApiService;
    @Autowired
    private VideoServiceApi videoServiceApi;
    @Autowired
    private DeviceService deviceService;

    @PostMapping("/jfy/capturePicture/{deviceId}")
    public ResponseResult capturePicture(@PathVariable("enterprise-id") String enterpriseId, @RequestParam("deviceId") String deviceId, @RequestBody JfyCaptureCallbackRequest request) {
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        if(StringUtils.isBlank(dbName)){
            return ResponseResult.fail(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        DataSourceHelper.changeToSpecificDataSource(dbName);
        videoServiceApi.getTaskFiles(enterpriseId, deviceId, request.getTaskId());
        return ResponseResult.success();
    }

    @PostMapping(value = "/tpLink/eventPush")
    public ResponseResult tpLinkEventPush(@PathVariable("enterprise-id") String enterpriseId, @RequestBody String requestBodyStr, HttpServletRequest request) {
        log.info("tpLinkPush requestBodyStr:{}", requestBodyStr);
        String headerSign = request.getHeader("Signature");
        try {
            String calSign = hmac256ToHexStr(TPVideoOpenServiceImpl.SK.getBytes(StandardCharsets.UTF_8), requestBodyStr);
            if (!Objects.equals(headerSign, calSign)) {
                log.info("验签不通过");
            }
            TPLinkNoticePushRequest jsonObject = JSONObject.parseObject(requestBodyStr, TPLinkNoticePushRequest.class);
            String qrCode = jsonObject.getParentQrCode();
            if(StringUtils.isBlank(qrCode)){
                qrCode = jsonObject.getQrCode();
            }
            EnterpriseConfigDTO enterpriseConfig = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
            if(Objects.isNull(enterpriseConfig)){
                return ResponseResult.success();
            }
            deviceService.callbackUpdateDeviceStatus(enterpriseId, enterpriseConfig.getDbName(), qrCode);
        } catch (Exception e) {
            log.error("hmac256ToHexStr exception occur, e:", e);
            // 结束方法
        }
        return ResponseResult.success();
    }

    public static String hmac256ToHexStr(byte[] key, String msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
        mac.init(secretKeySpec);
        return DatatypeConverter.printHexBinary(mac.doFinal(msg.getBytes(StandardCharsets.UTF_8))).toLowerCase();
    }
}
