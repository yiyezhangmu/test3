package com.coolcollege.intelligent.model.video.platform.yushi.response;

import lombok.Data;


/**
 * 抓拍图片返回值
 * @author byd
 */
@Data
public class YonghuiDeviceCaptureResponse {

    /**
     * 抓拍后的图片路径
     */
    private String url;
    /**
     * 过期时间，UTC时间戳
     */
    private Long expireTime;
    
}
