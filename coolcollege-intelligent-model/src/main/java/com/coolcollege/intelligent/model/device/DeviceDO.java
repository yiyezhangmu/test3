package com.coolcollege.intelligent.model.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName DeviceDO
 * @Description 用一句话描述什么
 * @author 首亮
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDO {
    /**
     * 自增ID
     */
    private Long id;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("设备名称")
    private String deviceName;

    @ApiModelProperty("设备类型，b1：b1,video:摄像头")
    private String type;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("创建人")
    private String createName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty("更新人Id")
    private String updateName;

    @ApiModelProperty("是否绑定门店，true 为绑定 ，false为默认值 未绑定")
    private Boolean bindStatus;

    @ApiModelProperty("设备状态:offline离线online 在线 默认在线")
    private String deviceStatus;

    private String deviceScene;

    @ApiModelProperty("设备来源")
    private String resource;

    /**
     * 虚拟设备Id
     */
    private String dataSourceId;
    /**
     * 是否有子设备
     */
    private Boolean hasChildDevice;
    /**
     * 是否支持云台
     */
    private Boolean hasPtz;
    /**
     * 绑定门店Id
     */
    private String bindStoreId;

    private String bindStoreIds;
    /**
     * 绑定时间
     */
    private Long bindTime;
    /**
     * 门店路径
     */
    private String regionPath;
    /**
     * 门店场景id
     */
    private Long storeSceneId;


    /**
     * 是否支持抓图 0-不支持 1-支持
     */
    private Integer supportCapture;

    /**
     * 是否支持客流分析 0-不支持 1-支持
     */
    private Boolean supportPassenger;
    /**
     * 是否开启客流分析
     */
    private Boolean enablePassenger;

    private String accountType;

    //设备型号,ISAPI: DS-2XD8747 萤石: C4X
    private String model;

    /**
     * 扩展信息
     */
    private String extendInfo;

    public static class ExtendInfoField {
        /**
         * 检查项描述是否必填
         */
        public static final String USERNAME = "username";

        /**
         * 检查项自动工单有效期
         */
        public static final String PASSWORD = "password";

        /**
         * 设备能力集
         */
        public static final String DEVICE_CAPACITY = "deviceCapacity";
    }

}
