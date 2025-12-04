package com.coolcollege.intelligent.model.video.platform;

import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/03/30
 */
@Data
public class EnterpriseVideoDO {
    //id
    private Long id;
    //企业Id
    private String enterpriseId;
    //设备Id
    private String deviceId;

    /**
     * 设备通道号
     */
    private String channelNo;

    //设备名称
    private String deviceName;
    //设备类型
    private String model;
    //设备状态
    private String status;
    //具有防护能力的设备布撤防状态：0-睡眠，8-在家，16-外出，普通IPC布撤防状态：0-撤防，1-布防
    private String defence;
    //是否加密
    private Integer isEncrypt;
    //告警声音模式：0-短叫，1-长叫，2-静音
    private Integer alarmSoundMode;
    //设备下线是否通知：0-不通知 1-通知
    private Integer offlineNotify;
    //设备大类
    private String category;
    //网络类型，如有线连接wire
    private String netType;
    //信号强度(%)
    private String signal;
    //用户ID 数智门店用户ID
    private String userId;
    //授权APP中的名称
    private String userName;
    //授权APP中的ID
    private String deviceTrustId;
    //创建时间
    private Date createTime;
    //修改时间
    private Date updateTime;
    /**
     * 是否支持抓图 0-不支持 1-支持
     */
    private int supportCapture;

    /**
     * 是否支持客流
     */
    private Boolean supportPassenger;

    /**
     * 云类型
     */
    private String yunType;
    /**
     * 通道号能力集
     */
    private String channelAbility;

    /**
     * 授权能力集
     */
    private String shareFunctions;

    /**
     *""：设备属于自己；"share"：通过乐橙app共享给此用户
     */
    private String shareStatus;

    /**
     * 云存储状态
     */
    private String csStatus;
    /**
     * 乐橙授权账号
     */
    private String phone;

    
    private Boolean isDelete;




}
