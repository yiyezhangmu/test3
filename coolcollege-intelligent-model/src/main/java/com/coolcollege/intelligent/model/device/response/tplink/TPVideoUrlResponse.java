package com.coolcollege.intelligent.model.device.response.tplink;

import lombok.Data;

@Data
public class TPVideoUrlResponse {

    /**
     * 平台sdk预览、回放的地址信息，将此传递给sdk组件以播放设备画面
     */
    private String sdkStreamUrl;
}
