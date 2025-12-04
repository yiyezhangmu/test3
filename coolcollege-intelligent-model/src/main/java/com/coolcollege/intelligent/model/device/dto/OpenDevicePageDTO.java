package com.coolcollege.intelligent.model.device.dto;

import com.coolcollege.intelligent.common.enums.device.DeviceSceneEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.model.device.DeviceDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

import static com.coolcollege.intelligent.common.constant.Constants.DEFAULT_STORE_ID;

/**
 *  外部第三方设备列表
 */
@Data
public class OpenDevicePageDTO {

    private String deviceSerial;
    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    private Boolean hasChildDevice;

    private String source;

    private String deviceStatus;

    private String channelStatus;

    @ApiModelProperty("门店编码")
    private String storeCode;

    private String useStoreId;

    /**
     * 是否支持云台控制
     */
    private Boolean hasPtz;

    /**
     * 杰峰云每个设备都有账密
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    public static DeviceDO convertDO(OpenDevicePageDTO param, String userId, OpenDeviceDTO deviceDetail){
        if(Objects.isNull(param)){
            return null;
        }
        DeviceDO deviceDO = new DeviceDO();
        deviceDO.setDeviceId(param.getDeviceId());
        deviceDO.setDeviceName(param.getDeviceName());
        deviceDO.setDeviceScene(DeviceSceneEnum.OTHER.getCode());
        deviceDO.setDeviceStatus(deviceDetail.getDeviceStatus());
        deviceDO.setResource(param.getSource());
        deviceDO.setSupportCapture(0);
        deviceDO.setStoreSceneId(DEFAULT_STORE_ID);
        deviceDO.setSupportPassenger(false);
        deviceDO.setType(DeviceTypeEnum.DEVICE_VIDEO.getCode());
        deviceDO.setHasPtz(false);
        deviceDO.setCreateName(userId);
        deviceDO.setCreateTime(System.currentTimeMillis());
        deviceDO.setHasChildDevice(param.getHasChildDevice());
        deviceDO.setDataSourceId(null);
        return deviceDO;
    }

}
