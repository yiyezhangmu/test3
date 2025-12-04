package com.coolcollege.intelligent.model.device.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DeviceLicenseDetailVO {

    /**
     * licenseId
     */
    private Long id;

    /**
     * SIP服务ID
     */
    private String sipId;

    /**
     * SIP服务域
     */
    private String sipArea;

    /**
     * SIP服务器域名
     */
    private String sipDomain;

    /**
     * SIP服务器域名
     */
    private String sipIp;

    private String localPort = "5060";

    /**
     * SIP服务器端口
     */
    private String sipPort;


    /*--------------------------------------------------------*/

    /**
     * 设备类型
     */
    private String type;

    private String name;//自定义名称

    /**
     * 设备国标ID
     */
    private String deviceId;

    /**
     * 设备国标code
     */
    private String deviceCode;

    /**
     * SIP用户名
     */
    private String sipUserName;//设备国标ID

    /**
     * 设备密码
     */
    private String password;//license

    /**
     * SIP用户认证ID
     */
    private String sipUserAuthId;//设备国标ID

    /**
     * 心跳周期
     */
    private Integer heartbeatCycle;

    /**
     * 注册有效期
     */
    private Integer registerValidity;

    /**
     * 最大心跳超时次数
     */
    private Integer heartbeatTimeout;

    /**
     * 备注
     */
    private String remark;

    /**
     * 通道数
     */
    private Integer channelNum;

    /**
     * 设备状态
     */
    private String deviceStatus;

    /**
     * 设备序列号
     */
    private String deviceSerial;//sipId:deviceId


    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 通道列表
     */
    private List<DeviceChannelLicenseVO> channelLicenseVOS;

}
