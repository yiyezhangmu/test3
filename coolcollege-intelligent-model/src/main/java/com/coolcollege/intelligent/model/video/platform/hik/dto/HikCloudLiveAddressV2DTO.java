package com.coolcollege.intelligent.model.video.platform.hik.dto;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/8/25 17:14
 * @Version 1.0
 */
@Data
public class HikCloudLiveAddressV2DTO {

    /**
     * 播放地址
     */
    private String url;

    private String id;

    private String expireTime;

}
