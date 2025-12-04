package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 设备通道号信息VO
 * </p>
 *
 * @author wangff
 * @since 2025/4/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceChannelVO {
    /**
     * 通道号
     */
    private String channelNo;
    /**
     * 通道名称
     */
    private String channelName;
}
