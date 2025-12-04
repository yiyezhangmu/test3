package com.coolcollege.intelligent.model.video.platform.yingshi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.YesOrNoEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2021/9/2 16:07
 * @Version 1.0
 */
@Data
public class DeviceCapacityDTO {


    private String deviceSerial;

    /**
     * 是否支持对讲: 0-不支持, 1-全双工, 3-半双工
     */
    private int supportTalk;

    /**
     * 是否支持封面抓图: 0-不支持, 1-支持
     */
    private int supportCapture;

    /**
     * 是否支持客流
     */
    private Integer supportFlowStatistics;

    /**
     * 是否支持存储格式化 0-不支持, 1-支持
     */
    private Integer supportDisk;

    /**
     * 是否支持云台上下转动 0-不支持, 1-支持
     */
    private Integer ptzTopBottom;

    /**
     * 是否支持云台左右转动 0-不支持, 1-支持
     */
    private Integer ptzLeftRight;

    /**
     * 是否支持云台缩放控制 0-不支持, 1-支持
     */
    private Integer ptzZoom;

    /**
     * 是否支持云台预置点 0-不支持, 1-支持
     */
    private Integer ptzPreset;

    /**
     * 是否支持自适应码流 0-不支持, 1-支持
     */
    private Integer supportAutoAdjust;

    /**
     * 是否支持蓝牙 0-不支持, 1-支持
     */
    private Integer supportBluetooth;


    public static DeviceCapacityDTO defaultDeviceCapacity() {
        DeviceCapacityDTO result = new DeviceCapacityDTO();
        result.setSupportTalk(0);
        result.setSupportCapture(0);
        result.setSupportFlowStatistics(0);
        result.setSupportDisk(0);
        result.setPtzTopBottom(0);
        result.setPtzLeftRight(0);
        result.setPtzZoom(0);
        result.setPtzPreset(0);
        result.setSupportAutoAdjust(0);
        result.setSupportBluetooth(0);
        return result;
    }

    public static DeviceCapacityDTO convertYingShiDeviceCapacity(JSONObject data) {
        DeviceCapacityDTO deviceCapacityDTO = defaultDeviceCapacity();
        if(Objects.nonNull(data)){
            Integer supportTalk = data.getInteger("support_talk");
            deviceCapacityDTO.setSupportTalk(Objects.nonNull(supportTalk) ? supportTalk : YesOrNoEnum.NO.getCode());
            Integer supportCapture = data.getInteger("support_capture");
            deviceCapacityDTO.setSupportCapture(Objects.nonNull(supportCapture) ? supportCapture : YesOrNoEnum.NO.getCode());
            Integer supportFlowStatistics = data.getInteger("support_flow_statistics");
            deviceCapacityDTO.setSupportFlowStatistics(Objects.nonNull(supportFlowStatistics) ? supportFlowStatistics : YesOrNoEnum.NO.getCode());
            Integer supportDisk = data.getInteger("support_disk");
            deviceCapacityDTO.setSupportDisk(Objects.nonNull(supportDisk) ? supportDisk : YesOrNoEnum.NO.getCode());
            Integer ptzTopBottom = data.getInteger("ptz_top_bottom");
            deviceCapacityDTO.setPtzTopBottom(Objects.nonNull(ptzTopBottom) ? ptzTopBottom : YesOrNoEnum.NO.getCode());
            Integer ptzLeftRight = data.getInteger("ptz_left_right");
            deviceCapacityDTO.setPtzLeftRight(Objects.nonNull(ptzLeftRight) ? ptzLeftRight : YesOrNoEnum.NO.getCode());
            Integer ptzZoom = data.getInteger("ptz_zoom");
            deviceCapacityDTO.setPtzZoom(Objects.nonNull(ptzZoom) ? ptzZoom : YesOrNoEnum.NO.getCode());
            Integer ptzPreset = data.getInteger("ptz_preset");
            deviceCapacityDTO.setPtzPreset(Objects.nonNull(ptzPreset) ? ptzPreset : YesOrNoEnum.NO.getCode());
            Integer supportAutoAdjust = data.getInteger("support_auto_adjust");
            deviceCapacityDTO.setSupportAutoAdjust(Objects.nonNull(supportAutoAdjust) ? supportAutoAdjust : YesOrNoEnum.NO.getCode());
            Integer supportBluetooth = data.getInteger("support_bluetooth");
            deviceCapacityDTO.setSupportBluetooth(Objects.nonNull(supportBluetooth) ? supportBluetooth : YesOrNoEnum.NO.getCode());
        }
        return deviceCapacityDTO;
    }


    public static DeviceCapacityDTO convertJFYDeviceCapacity(JSONObject data) {
        DeviceCapacityDTO deviceCapacityDTO = defaultDeviceCapacity();
        if(Objects.nonNull(data)){
            JSONObject otherFunction = data.getJSONObject("OtherFunction");
            JSONObject encodeFunction = data.getJSONObject("EncodeFunction");
            deviceCapacityDTO.setSupportDisk(YesOrNoEnum.YES.getCode());
            if(Objects.nonNull(encodeFunction)){
                deviceCapacityDTO.setSupportCapture(Boolean.TRUE.equals(encodeFunction.getBoolean("SnapStream"))? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode());
            }
            if(Objects.nonNull(otherFunction)){
                deviceCapacityDTO.setSupportTalk(Boolean.TRUE.equals(otherFunction.getBoolean("SupportTwoWayVoiceTalk"))? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode());
                deviceCapacityDTO.setPtzTopBottom(Boolean.TRUE.equals(otherFunction.getBoolean("SupportPTZDirectionVerticalControl"))? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode());
                deviceCapacityDTO.setPtzLeftRight(Boolean.TRUE.equals(otherFunction.getBoolean("SupportPTZDirectionHorizontalControl")) ? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode());
                deviceCapacityDTO.setSupportFlowStatistics(Boolean.TRUE.equals(otherFunction.getBoolean("SupportCustomerFlowCount")) ? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode());
            }
        }
        return deviceCapacityDTO;
    }


    public static DeviceCapacityDTO convertYunShiTongDeviceCapacity(JSONArray ability) {
        DeviceCapacityDTO deviceCapacityDTO = defaultDeviceCapacity();
        if(Objects.nonNull(ability) && !ability.isEmpty()){
            List<String> abilityList = ability.stream().map(String::valueOf).collect(Collectors.toList());
            deviceCapacityDTO.setSupportCapture(YesOrNoEnum.YES.getCode());
            deviceCapacityDTO.setSupportTalk(abilityList.contains("talk")? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode());
            deviceCapacityDTO.setPtzTopBottom(abilityList.contains("ptz")? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode());
            deviceCapacityDTO.setPtzLeftRight(abilityList.contains("ptz") ? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode());
        }
        return deviceCapacityDTO;
    }

    public static DeviceCapacityDTO convertHikCloudDeviceCapacity() {
        DeviceCapacityDTO deviceCapacityDTO = defaultDeviceCapacity();
        deviceCapacityDTO.setSupportCapture(YesOrNoEnum.YES.getCode());
        deviceCapacityDTO.setSupportTalk(YesOrNoEnum.YES.getCode());
        deviceCapacityDTO.setPtzTopBottom(YesOrNoEnum.YES.getCode());
        return deviceCapacityDTO;
    }

    public static DeviceCapacityDTO convertLeChengDeviceCapacity(String deviceAbility) {
        DeviceCapacityDTO deviceCapacityDTO = defaultDeviceCapacity();
        if(StringUtils.isNotBlank(deviceAbility)){
            List<String> abilityList = Arrays.stream(deviceAbility.split(Constants.COMMA)).distinct().collect(Collectors.toList());
            deviceCapacityDTO.setSupportCapture(YesOrNoEnum.YES.getCode());
            deviceCapacityDTO.setSupportTalk(abilityList.contains("AudioTalkV1") || abilityList.contains("AudioTalk")? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode());
            deviceCapacityDTO.setPtzTopBottom(abilityList.contains("PT1") || abilityList.contains("PT") || abilityList.contains("PTZ")? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode());
            deviceCapacityDTO.setPtzLeftRight(abilityList.contains("PT1") || abilityList.contains("PT") || abilityList.contains("PTZ")? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode());
            deviceCapacityDTO.setPtzZoom(abilityList.contains("PTZ") || abilityList.contains("ZoomFocus")? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode());
        }
        return deviceCapacityDTO;
    }

    public static DeviceCapacityDTO convertWDZDeviceCapacity() {
        DeviceCapacityDTO deviceCapacityDTO = defaultDeviceCapacity();
        deviceCapacityDTO.setSupportCapture(YesOrNoEnum.YES.getCode());
        return deviceCapacityDTO;
    }

    public static DeviceCapacityDTO convertTPDeviceCapacity(JSONObject capability, JSONObject talkResultJson) {
        DeviceCapacityDTO deviceCapacityDTO = defaultDeviceCapacity();
        if(Objects.nonNull(capability)){
            deviceCapacityDTO.setSupportCapture(YesOrNoEnum.YES.getCode());
            deviceCapacityDTO.setPtzTopBottom(hasAbility(capability.getJSONArray("position_pan_range")));
            deviceCapacityDTO.setPtzLeftRight(hasAbility(capability.getJSONArray("position_tilt_range")));
            deviceCapacityDTO.setPtzZoom(hasAbility(capability.getJSONArray("position_zoom_range")));
            Integer presetSupported = capability.getInteger("preset_supported");
            deviceCapacityDTO.setPtzPreset(Objects.nonNull(presetSupported) ? presetSupported : YesOrNoEnum.NO.getCode());
        }
        if(Objects.nonNull(talkResultJson)){
            Boolean talkSupport = talkResultJson.getBoolean("talkSupport");
            deviceCapacityDTO.setSupportTalk(Objects.nonNull(talkSupport) && talkSupport ? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode());
        }
        return deviceCapacityDTO;
    }

    public static int hasAbility(JSONArray positionPanRange) {
        if (positionPanRange == null || positionPanRange.size() != 2) {
            return YesOrNoEnum.NO.getCode();
        }
        return !("0.000000".equals(positionPanRange.getString(0)) && "0.000000".equals(positionPanRange.getString(1))) ? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode();
    }


}
