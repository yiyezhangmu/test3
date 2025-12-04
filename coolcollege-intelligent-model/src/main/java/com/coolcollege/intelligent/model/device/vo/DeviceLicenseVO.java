package com.coolcollege.intelligent.model.device.vo;

import lombok.Data;

import java.util.Date;

@Data
public class DeviceLicenseVO {

    private String id;

    /**
     * 设备类型，1:IPC,2:NVR
     */
    private String type;

    /**
     * 平台分配国标设备编码
     */
    private String deviceCode;

    /**
     * 设备序列号
     */
    private String deviceSerial;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 占用状态
     */
    private String status;

    /**
     * 申请批次
     */
    private String batchId;


    private String channelIds;

    /**
     * 通道数
     */
    private Integer channelNum;

    /**
     * 是否被萤石新设备使用
     */
    private String useByNew;

    /**
     * 申请时间
     */
    private Date applyTime;

    private String applyUser;
    private String createName;

}
