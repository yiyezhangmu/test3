package com.coolcollege.intelligent.model.device.gb28181;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备通道
 *
 * @author twc
 * @date 2024/09/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Channel {

    private String deviceCode;

    private String deviceId;

    private String belongToDeviceCode;

    private String channelNo;

    private String channelName;

    private String randomDeviceCode;


    private String channelSerial;//结果

    private Integer status;//0-离线 1-在线



}
