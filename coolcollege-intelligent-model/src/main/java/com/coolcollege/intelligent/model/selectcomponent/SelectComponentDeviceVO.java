package com.coolcollege.intelligent.model.selectcomponent;

import lombok.Data;

import java.util.List;

/**
 * @desc: 选人组件中设备信息的返回
 * @author: xuanfeng
 * @date: 2021-10-27 15:07
 */
@Data
public class SelectComponentDeviceVO {
    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型，b1：b1,video:摄像头
     */
    private String type;
}
