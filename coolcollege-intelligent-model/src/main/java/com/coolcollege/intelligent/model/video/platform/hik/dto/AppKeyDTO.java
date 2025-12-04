package com.coolcollege.intelligent.model.video.platform.hik.dto;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/12/27 14:51
 * @Version 1.0
 */
@Data
public class AppKeyDTO {

    private String appKey;

    private String token;

    public AppKeyDTO(String appKey, String token) {
        this.appKey = appKey;
        this.token = token;
    }

    public AppKeyDTO() {
    }
}
