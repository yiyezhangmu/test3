package com.coolcollege.intelligent.controller.video;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.device.YunshitongMsgTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.device.request.YunshitongNoticePushRequest;
import com.coolcollege.intelligent.service.device.DeviceService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 云视通（中维）前端控制器
 * </p>
 *
 * @author wangff
 * @since 2025/7/29
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class YunshitongController {
    private final DeviceService deviceService;

    @ApiOperation("云视通(中维)设备事件推送")
    @PostMapping("/device/open/yunshitong/eventPush")
    public ResponseResult yunshitongEventPush(@RequestBody YunshitongNoticePushRequest request) {
        log.info("yunshitong requestBodyStr:{}", JSONObject.toJSONString(request));
        try {
            YunshitongMsgTypeEnum msgType = YunshitongMsgTypeEnum.getByCode(request.getMessageType());
            if (YunshitongMsgTypeEnum.ONLINE.equals(msgType) || YunshitongMsgTypeEnum.OFFLINE.equals(msgType)) {
                deviceService.callbackUpdateDeviceStatus(request.getMessageData().getDeviceSn());
            }
        } catch (Exception e) {
            log.info("云视通设备推送事件解析失败", e);
        }
        return ResponseResult.success();
    }

}
