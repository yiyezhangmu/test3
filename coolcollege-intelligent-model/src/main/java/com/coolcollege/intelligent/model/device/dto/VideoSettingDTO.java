package com.coolcollege.intelligent.model.device.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 企业设备配置DTO
 * </p>
 *
 * @author wangff
 * @since 2025/5/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoSettingDTO {
    /**
     * appKey
     */
    private String appKey;

    /**
     * appSecret
     */
    private String appSecret;

    /**
     * uuid
     */
    private String uuid;
}
