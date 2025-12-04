package com.coolcollege.intelligent.model.device.response.tplink;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.model.device.dto.OpenDeviceDTO;
import com.coolstore.base.enums.YunTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TpDeviceDetailResponse {

    @ApiModelProperty(value = "17位设备码")
    private String qrCode;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    @ApiModelProperty(value = "设备状态 0 离线 ,1 在线,2 重启中，3 升级中,4 配置中，5 同步中,6 等待升级")
    private int deviceStatus;

    @ApiModelProperty(value = "0:一般类型设备 1:视频类型设备")
    private int openType;

    @ApiModelProperty(value = "0:受限关闭 1:适用中 2付费使用中")
    private int openStatus;

    @ApiModelProperty(value = "设备型号")
    private String deviceModel;

    @ApiModelProperty(value = "所属项目id")
    private String projectId;

    @ApiModelProperty(value = "所属项目名称")
    private String projectName;

    @ApiModelProperty(value = "所属分组")
    private String regionId;

    @ApiModelProperty(value = "分组名称")
    private String regionName;

    @ApiModelProperty(value = "ip地址")
    private String ip;

    @ApiModelProperty(value = "mac地址")
    private String mac;

    @ApiModelProperty(value = "固件版本")
    private String firmwareVer;

    @ApiModelProperty(value = "硬件版本")
    private String hardwareVer;

    public static OpenDeviceDTO convert(TpDeviceDetailResponse response){
        if(response == null){
            return null;
        }
        OpenDeviceDTO result = new OpenDeviceDTO();
        result.setDeviceId(response.getQrCode());
        result.setDeviceName(response.getDeviceName());
        result.setDeviceStatus(Constants.INDEX_ONE.equals(response.getDeviceStatus())  ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
        result.setSource(YunTypeEnum.TP_LINK.getCode());
        result.setSupportCapture(Constants.ZERO);
        result.setSupportPassenger(Boolean.FALSE);
        return result;
    }

}
