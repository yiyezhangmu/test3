package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 * 设备信息VO
 * </p>
 *
 * @author wangff
 * @since 2025/4/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceVO {
    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 通道列表
     */
    private List<DeviceChannelVO> channelList;
}
