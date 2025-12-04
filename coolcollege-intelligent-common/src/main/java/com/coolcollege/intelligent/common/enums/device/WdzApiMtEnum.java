package com.coolcollege.intelligent.common.enums.device;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * describe: 万店掌接口类型
 *
 * @author wangff
 * @date 2024/10/17
 */
@Getter
@AllArgsConstructor
public enum WdzApiMtEnum {

    LOGIN("open.shopweb.security.mobileLogin", "v1", "登录"),
    
    DEVICE_STATES("open.shopweb.device.getDeviceStates", "v2", "设备状态（非NVR设备）"),

    DEVICE_LIST("open.shopweb.device.getVideoDeviceList", "v1", "设备列表"),

    MEDIA_PLAY("open.shopweb.device.startNewMediaPlay", "v4", "实时播放"),

    PAST_PLAY("open.shopweb.device.startPlayRecReq", "v3", "视频回放"),

    PTZ_CTRL("open.shopweb.device.ptzCtrlAll", "v2", "云台控制设备"),

    CAPTURE("open.shopweb.device.snapshot", "v1", "实时视频抓拍"),

    DOWNLOAD_VIDEO("open.device.public.device.v2.downloadVideo", "v3", "录像视频下载"),
    ;

    /**
     * 接口名称
     */
    private String mt;
    
    /**
     * 接口版本
     */
    private String version;

    /**
     * 接口描述
     */
    private String msg;
}
