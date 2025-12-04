package com.coolcollege.intelligent.model.device.dto;

import com.coolcollege.intelligent.common.enums.device.DeviceSceneEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.video.platform.yingshi.DeviceCapacityDTO;
import lombok.Data;

import java.util.List;
import java.util.Objects;

import static com.coolcollege.intelligent.common.constant.Constants.DEFAULT_STORE_ID;

/**
 * @ClassName DeviceDTO
 * @Description 用一句话描述什么
 * @author 首亮
 */
@Data
public class OpenDeviceDTO {
    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    private Boolean hasChildDevice;

    private Boolean hasPtz;

    private String source;

    private String deviceStatus;

    private Integer supportCapture;

    private Boolean supportPassenger;

    private String dataSourceId;
    private String storeNo;

    /**
     * 设备能力集
     */
    private DeviceCapacityDTO deviceCapacity;

    private List<OpenChannelDTO> channelList;

    /**
     * 1、区分 ISAPI协议 和 萤石云客流协议的方式：
     * 基于设备型号 做区分；
     * ISAPI :DS-2XD8747；
     * 萤石：C4X
     * 设备同步的时候：确认后isapi设备还要绑定区域和模板、下发消息；
     */
    private String model;


    public static DeviceDO convertDO(OpenDeviceDTO param, String userId){
        if(Objects.isNull(param)){
            return null;
        }
        DeviceDO deviceDO = new DeviceDO();
        deviceDO.setDeviceId(param.getDeviceId());
        deviceDO.setDeviceName(param.getDeviceName());
        deviceDO.setDeviceScene(DeviceSceneEnum.OTHER.getCode());
        deviceDO.setDeviceStatus(param.getDeviceStatus());
        deviceDO.setResource(param.getSource());
        deviceDO.setSupportCapture(param.getSupportCapture());
        deviceDO.setStoreSceneId(DEFAULT_STORE_ID);
        deviceDO.setSupportPassenger(param.getSupportPassenger());
        deviceDO.setType(DeviceTypeEnum.DEVICE_VIDEO.getCode());
        deviceDO.setHasPtz(param.getHasPtz());
        deviceDO.setCreateName(userId);
        deviceDO.setCreateTime(System.currentTimeMillis());
        deviceDO.setHasChildDevice(param.getHasChildDevice());
        deviceDO.setDataSourceId(param.getDataSourceId());
        return deviceDO;
    }

    public static DeviceDO convertUpdateDO(OpenDeviceDTO param, String userId){
        if(Objects.isNull(param)){
            return null;
        }
        DeviceDO deviceDO = new DeviceDO();
        deviceDO.setDeviceId(param.getDeviceId());
        deviceDO.setDeviceName(param.getDeviceName());
        deviceDO.setDeviceStatus(param.getDeviceStatus());
        deviceDO.setSupportCapture(param.getSupportCapture());
        deviceDO.setSupportPassenger(param.getSupportPassenger());
        deviceDO.setHasPtz(param.getHasPtz());
        deviceDO.setHasChildDevice(param.getHasChildDevice());
        deviceDO.setDataSourceId(param.getDataSourceId());
        deviceDO.setUpdateName(userId);
        return deviceDO;
    }

}
