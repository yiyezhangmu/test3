package com.coolcollege.intelligent.model.device.vo;

import com.coolcollege.intelligent.model.device.DeviceDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: DeviceInfoVO
 * @Description:设备信息
 * @date 2022-12-15 13:52
 */
@Data
public class DeviceInfoVO {

    @ApiModelProperty("设备ID")
    private String deviceId;

    @ApiModelProperty(name = "设备名称")
    private String deviceName;

    @ApiModelProperty("门店Id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("设备类型，b1：b1，video：摄像头")
    private String deviceType;

    @ApiModelProperty("来源 阿里云:ali, 宇视云:yushi, 萤石云:yingshi, 萤石国标:yingshi_gb, 云橙:imou, 云视通:yunshitong, 海康云眸:hikcloud")
    private String source;

    @ApiModelProperty("门店场景")
    private String scene;

    @ApiModelProperty("设备状态:offline离线, online 在线")
    private String deviceStatus;

    @ApiModelProperty("是否绑定了门店（0未绑定，1绑定）默认值0")
    private Boolean bindStatus;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("是否有子设备")
    private Boolean hasChildDevice;

    @ApiModelProperty("是否支持云台")
    private Boolean hasPtz;

    @ApiModelProperty("关联时间")
    private String bindTime;

    @ApiModelProperty("门店场景id")
    private Long storeSceneId;

    @ApiModelProperty("门店场景名称")
    private String storeSceneName;

    @ApiModelProperty("是否支持客流分析")
    private Boolean supportPassenger;

    @ApiModelProperty("是否开启客流分析")
    private Boolean enablePassenger;

    @ApiModelProperty("通道")
    private List<DeviceChannelVO> channelList;


    public static DeviceInfoVO convertVO(DeviceDO device){
        DeviceInfoVO result = new DeviceInfoVO();
        result.setDeviceId(device.getDeviceId());
        result.setDeviceName(device.getDeviceName());
        result.setStoreId(device.getBindStoreId());
        result.setDeviceType(device.getType());
        result.setBindStatus(device.getBindStatus());
        result.setDeviceStatus(device.getDeviceStatus());
        result.setScene(device.getDeviceScene());
        result.setSource(device.getResource());
        result.setRemark(device.getRemark());
        result.setStoreSceneId(device.getStoreSceneId());
        result.setHasPtz(device.getHasPtz()==null?false:device.getHasPtz());
        result.setHasChildDevice(device.getHasChildDevice()==null?false:device.getHasChildDevice());
        result.setSupportPassenger(device.getSupportPassenger()==null?false:device.getSupportPassenger());
        result.setEnablePassenger(device.getEnablePassenger()==null?false:device.getEnablePassenger());
        return result;
    }

}
