package com.coolcollege.intelligent.model.device.response.tplink;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TpDeleteDeviceResponse {

    private int error_code;

    private List<FailDeleteDevice> failList;

    @Data
    public static class FailDeleteDevice{

        @ApiModelProperty("17位设备码")
        private String qrCode;

        @ApiModelProperty("设备mac地址")
        private String mac;

        @ApiModelProperty("视频子设备通道编号")
        private String channelId;

        @ApiModelProperty("网络子设备mac地址")
        private String childMac;

        @ApiModelProperty("错误码")
        private int error_code;
    }

}
