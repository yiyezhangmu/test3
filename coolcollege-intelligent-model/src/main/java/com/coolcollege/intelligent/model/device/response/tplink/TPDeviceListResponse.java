package com.coolcollege.intelligent.model.device.response.tplink;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TPDeviceListResponse {

    private int total;

    private List<TPDeviceListDetail> list;

    @Data
    public static class TPDeviceListDetail{

        @ApiModelProperty(value = "17位设备码")
        private String qrCode;

        @ApiModelProperty(value = "设备名称")
        private String deviceName;

        @ApiModelProperty(value = "设备类型")
        private String deviceType;

        @ApiModelProperty(value = "设备状态")
        private int deviceStatus;

        @ApiModelProperty(value = "0:一般类型设备 1:视频类型设备")
        private int openType;

        @ApiModelProperty(value = "0:受限关闭 1:适用中 2付费使用中")
        private int openStatus;

        @ApiModelProperty(value = "设备型号")
        private String deviceModel;

        @ApiModelProperty(value = "ip地址")
        private String ip;

        @ApiModelProperty(value = "mac地址")
        private String mac;

        @ApiModelProperty(value = "所属分组")
        private String regionName;

        @ApiModelProperty(value = "分组id")
        private String regionId;

        @ApiModelProperty(value = "父设备的mac地址")
        private String parentMac;

        @ApiModelProperty(value = "父设备的qrCode")
        private String parentQrCode;

        @ApiModelProperty(value = "通道id")
        private String channelId;

    }
}
