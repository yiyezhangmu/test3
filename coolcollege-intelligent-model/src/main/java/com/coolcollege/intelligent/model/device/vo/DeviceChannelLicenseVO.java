package com.coolcollege.intelligent.model.device.vo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Data
public class DeviceChannelLicenseVO {

    /**
     * 通道id
     */
    private String id;

    /**
     * 通道号
     */
    private Integer channelNo;

    /**
     * 设备国标编号
     */
    private String deviceCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 通道序列号
     */
    private String channelSerial;//页面没有？


    /**
     * 状态
     */
    private String status;

    /**
     * 绑定时间
     */
    private Date bindingTime;

    public String getChannelSerial() {
        if (StringUtils.isNotBlank(channelSerial) && channelSerial.contains(":")) {
            int index = channelSerial.indexOf(':');
            if (index != -1) {
                String result = channelSerial.substring(index + 1);
                return  result;
            }
        }
        return channelSerial;
    }
}
