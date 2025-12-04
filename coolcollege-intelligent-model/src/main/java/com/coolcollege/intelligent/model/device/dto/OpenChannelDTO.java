package com.coolcollege.intelligent.model.device.dto;

import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.video.platform.yingshi.DeviceCapacityDTO;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

import static com.coolcollege.intelligent.common.constant.Constants.DEFAULT_STORE_ID;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/06
 */
@Data
public class OpenChannelDTO {

    private String parentDeviceId;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 通道号
     */
    private String channelNo;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 云台控制
     */
    private Boolean hasPtz;

    /**
     * 通道状态
     */
    private String status;

    /**
     * 来源
     */
    private String source;

    /**
     * 通道ID
     */
    private String channelId;
    
    /**
     * 是否支持抓拍
     */
    private Integer supportCapture;

    /**
     * 通道能力集
     */
    private DeviceCapacityDTO deviceCapacity;


    public static DeviceChannelDO mapDeviceChannelDO(DeviceDO deviceDO, OpenChannelDTO channel) {
        Date date = new Date();
        DeviceChannelDO deviceChannelDO = new DeviceChannelDO();
        deviceChannelDO.setParentDeviceId(channel.getParentDeviceId());
        deviceChannelDO.setDeviceId(channel.getDeviceId());
        deviceChannelDO.setChannelName(channel.getChannelName());
        deviceChannelDO.setChannelNo(channel.getChannelNo());
        deviceChannelDO.setCreateTime(date);
        deviceChannelDO.setUpdateTime(date);
        deviceChannelDO.setStatus(channel.getStatus());
        deviceChannelDO.setHasPtz(channel.getHasPtz());
        deviceChannelDO.setSupportCapture(Objects.nonNull(channel.getSupportCapture()) ? channel.getSupportCapture() : deviceDO.getSupportCapture());
        deviceChannelDO.setSupportPassenger(deviceDO.getSupportPassenger());
        deviceChannelDO.setStoreSceneId(DEFAULT_STORE_ID);
        return deviceChannelDO;
    }
}
