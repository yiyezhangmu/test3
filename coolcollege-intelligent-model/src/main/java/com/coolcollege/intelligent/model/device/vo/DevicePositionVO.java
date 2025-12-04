package com.coolcollege.intelligent.model.device.vo;

import com.coolcollege.intelligent.model.device.DevicePositionDO;
import com.coolcollege.intelligent.model.device.dto.DevicePositionDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: DevicePositionVO
 * @Description:
 * @date 2022-12-15 15:51
 */
@Data
public class DevicePositionVO {

    @ApiModelProperty("预置位id")
    private Long id;

    @ApiModelProperty("设备Id")
    private String deviceId;

    @ApiModelProperty("通道号")
    private String channelNo;

    @ApiModelProperty("预制位名称")
    private String devicePositionName;

    @ApiModelProperty("预置位索引")
    private String positionIndex;


    public static DevicePositionVO convertVO(DevicePositionDO data) {
        DevicePositionVO result = new DevicePositionVO();
        result.setId(data.getId());
        result.setDeviceId(data.getDeviceId());
        result.setChannelNo(data.getChannelNo());
        result.setDevicePositionName(data.getDevicePositionName());
        result.setPositionIndex(data.getPositionIndex());
        return result;
    }

}
