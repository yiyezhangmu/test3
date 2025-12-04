package com.coolcollege.intelligent.model.aliyun.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 邵凌志
 * @date 2021/1/15 14:35
 */
@Data
@NoArgsConstructor
public class AliyunVdsTraceDTO {

    private String deviceId;

    private String storeId;

    private String storeName;

    private String startTime;

    private String endTime;

    private String sourceImage;

    private String targetImage;

    public AliyunVdsTraceDTO(String deviceId, String startTime, String endTime, String sourceImage, String targetImage) {
        this.deviceId = deviceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sourceImage = sourceImage;
        this.targetImage = targetImage;
    }
}
