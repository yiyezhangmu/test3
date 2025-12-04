package com.coolcollege.intelligent.model.device.dto;

import com.coolcollege.intelligent.common.enums.device.DeviceAuthStatusEnum;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.EnterpriseAuthDeviceDO;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DeviceAuthDTO {

    @NotBlank
    @ApiModelProperty("第三方门店Id")
    private String thirdStoreId;

    @NotBlank
    @ApiModelProperty("appId")
    private String appId;

    @NotBlank
    @ApiModelProperty("设备ID")
    private String deviceId;

    @ApiModelProperty("通道号")
    private String channelNo;

    @NotNull
    @ApiModelProperty("授权截止时间")
    private Date authEndTime;

    public static EnterpriseAuthDeviceDO convertAuthToDO(String enterpriseId, String storeId, DeviceAuthDTO param, DeviceDO device) {
        EnterpriseAuthDeviceDO result = new EnterpriseAuthDeviceDO();
        result.setEnterpriseId(enterpriseId);
        result.setStoreId(storeId);
        result.setThirdStoreId(param.getThirdStoreId());
        result.setDeviceId(param.getDeviceId());
        result.setDeviceName(param.getDeviceId());
        result.setChannelNo(param.getChannelNo());
        result.setAuthStatus(DeviceAuthStatusEnum.AUTH.getCode());
        result.setDeviceStatus(device.getDeviceStatus());
        result.setAuthStartTime(new java.util.Date());
        result.setAuthEndTime(param.getAuthEndTime());
        result.setAppId(param.getAppId());
        result.setCreateTime(new java.util.Date());
        result.setUpdateTime(new java.util.Date());
        result.setDeviceName(device.getDeviceName());
        return result;
    }
}
