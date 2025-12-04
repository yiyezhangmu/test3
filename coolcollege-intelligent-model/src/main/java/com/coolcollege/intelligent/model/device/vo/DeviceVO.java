package com.coolcollege.intelligent.model.device.vo;

import com.coolcollege.intelligent.model.device.DeviceDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/02/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceVO {

    @ApiModelProperty("自增ID")
    private Long id;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("设备名称")
    private String deviceName;

    @ApiModelProperty("设备类型，b1：b1,video:摄像头")
    private String type;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("创建人")
    private String createName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty("更新人Id")
    private String updateName;

    @ApiModelProperty("是否绑定门店，1为绑定 ，0为默认值 未绑定")
    private Boolean bindStatus;

    @ApiModelProperty("设备状态:offline离线online 在线 ")
    private String deviceStatus;

    private String scene;

    @ApiModelProperty("设备来源")
    private String resource;

    @ApiModelProperty("绑定时间")
    private Long bindTime;

    @ApiModelProperty("绑定门店")
    private String bindStoreId;


    public static DeviceVO convertVO(DeviceDO device){
        if(Objects.isNull(device)){
            return null;
        }
        DeviceVO deviceVO = new DeviceVO();
        deviceVO.setDeviceId(device.getDeviceId());
        deviceVO.setDeviceName(device.getDeviceName());
        deviceVO.setType(device.getType());
        deviceVO.setRemark(device.getRemark());
        deviceVO.setBindStatus(device.getBindStatus());
        deviceVO.setDeviceStatus(device.getDeviceStatus());
        deviceVO.setScene(device.getDeviceScene());
        deviceVO.setBindStoreId(device.getBindStoreId());
        deviceVO.setResource(device.getResource());
        return deviceVO;
    }




}
