package com.coolcollege.intelligent.model.device.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: DevicePackageVO
 * @Description:
 * @date 2022-12-19 10:54
 */
@Data
public class DevicePackageVO {

    @ApiModelProperty("套餐设备数量")
    private Integer limitDeviceCount;

    @ApiModelProperty("设备数量")
    private Integer deviceCount;

    public DevicePackageVO(Integer limitDeviceCount, Integer deviceCount) {
        this.limitDeviceCount = limitDeviceCount;
        this.deviceCount = deviceCount;
    }
}
