package com.coolcollege.intelligent.controller.video;


import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.device.request.TPLinkNoticePushRequest;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolcollege.intelligent.service.video.openapi.impl.TPVideoOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@RestController
public class TpLinkController {

    @Resource
    private DeviceService deviceService;
    @Resource
    private TPVideoOpenServiceImpl tPVideoOpenServiceImpl;

    @PostMapping(value = "/device/open/setAppMsgPushConfig")
    public ResponseResult setAppMsgPushConfig(@RequestParam("enterpriseId")String enterpriseId, @RequestParam("callbackUrl") String callbackUrl) {
        tPVideoOpenServiceImpl.setAppMsgPushConfig(enterpriseId, callbackUrl);
        return ResponseResult.success();
    }

    @PostMapping(value = "/device/open/setAppMsgPushSk")
    public ResponseResult setAppMsgPushSk(@RequestParam("enterpriseId")String enterpriseId) {
        tPVideoOpenServiceImpl.setAppMsgPushSk(enterpriseId, TPVideoOpenServiceImpl.SK);
        return ResponseResult.success();
    }

    @PostMapping(value = "/device/open/getAppMsgPushSk")
    public ResponseResult getAppMsgPushSk(@RequestParam("enterpriseId")String enterpriseId) {
        return ResponseResult.success(tPVideoOpenServiceImpl.getAppMsgPushSk(enterpriseId));
    }

    @PostMapping(value = "/device/open/getAppMsgPushConfig")
    public ResponseResult getAppMsgPushConfig(@RequestParam("enterpriseId")String enterpriseId) {
        return ResponseResult.success(tPVideoOpenServiceImpl.getAppMsgPushConfig(enterpriseId));
    }


    public static String hmac256ToHexStr(byte[] key, String msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
        mac.init(secretKeySpec);
        return DatatypeConverter.printHexBinary(mac.doFinal(msg.getBytes(StandardCharsets.UTF_8))).toLowerCase();
    }

}
