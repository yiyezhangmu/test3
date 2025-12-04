package com.coolcollege.intelligent.controller.video;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.enums.device.YingShiCloudMsgTypeEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.enums.device.YingShiMsgTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.device.YingShiCloudRecordingMessage;
import com.coolcollege.intelligent.model.device.YingShiWebHookMessage;
import com.coolcollege.intelligent.model.device.YingShiWebHookResponse;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolcollege.intelligent.service.inspection.AiInspectionCapturePictureService;
import com.coolcollege.intelligent.service.video.YingshiDeviceService;
import com.coolcollege.intelligent.service.video.manager.YingshiDeviceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/02
 */
@Slf4j
@RestController
public class YingshiyunController {
    @Autowired
    private YingshiDeviceService yingshiDeviceService;
    @Resource
    private DeviceService deviceService;

    @Resource
    private AiInspectionCapturePictureService aiInspectionCapturePictureService;
    @GetMapping("/v3/enterprises/{enterprise-id}/yingshiyun/createUrl")
    @SysLog(func = "授权", opModule = OpModuleEnum.SETTING_DEVICE_INTEGRATION, opType = OpTypeEnum.DEVICE_AUTHORIZATION)
    public ResponseResult createUrl(@PathVariable("enterprise-id") String eid,
                                    @RequestParam(value = "storeId", required = false) String storeId,
                                    @RequestParam(value = "userId", required = false) String userId) {
        return ResponseResult.success(yingshiDeviceService.createYingshiAuthUrl(eid, storeId, userId));
    }

    @PostMapping("/yingshiyun/webhook")
    public YingShiWebHookResponse yingShiWebHookWebhook(@RequestBody String message){
        log.info("yingshiyun callback,message= {}", message);
        YingShiWebHookMessage webHookMessage = JSON.parseObject(message, YingShiWebHookMessage.class);
        if(Objects.isNull(webHookMessage) || Objects.isNull(webHookMessage.getHeader())){
            return new YingShiWebHookResponse(null);
        }
        if(YingShiMsgTypeEnum.ONOFFLINE.getCode().equals(webHookMessage.getHeader().getType())){
            String deviceId = webHookMessage.getHeader().getDeviceId();
            boolean result = deviceService.callbackUpdateDeviceStatus(deviceId);
            if(result){
                return new YingShiWebHookResponse(webHookMessage.getHeader().getMessageId());
            }
        }else if(YingShiMsgTypeEnum.YS_OPEN_CLOUD.getCode().equals(webHookMessage.getHeader().getType())){
            JSONObject jsonObject = JSON.parseObject(message);
            String messageType = jsonObject.getJSONObject("body").getString("messageType");
            if(YingShiCloudMsgTypeEnum.VIDEO_FRAME_STATUS_CHANGE.getCode().equals(messageType)
                || YingShiCloudMsgTypeEnum.VIDEO_FRAME.getCode().equals(messageType)){
                //及时抽帧消息
                YingShiCloudRecordingMessage messageDTO = JSON.parseObject(message, YingShiCloudRecordingMessage.class);
                aiInspectionCapturePictureService.handelDeviceCaptureCallBack(messageDTO);
            }
        }
        return new YingShiWebHookResponse(null);
    }

}
