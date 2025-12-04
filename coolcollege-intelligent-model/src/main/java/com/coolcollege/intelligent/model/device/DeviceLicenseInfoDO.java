package com.coolcollege.intelligent.model.device;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 平台分配国标设备授权
 * @author   wangff
 * @date   2025-08-11 04:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceLicenseInfoDO implements Serializable {
    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("license类型（1:IPC,2:NVR）")
    private Integer type;

    @ApiModelProperty("所属批次id，弃用")
    private String batchId;

    @ApiModelProperty("平台分配的国标设备编号")
    private String deviceCode;

    @ApiModelProperty("平台分配的国标设备id")
    private String deviceId;

    @ApiModelProperty("设备的注册密码")
    private String license;

    @ApiModelProperty("授权是否被使用（0:未使用，1以使用）")
    private Integer status;

    @ApiModelProperty("申请时间")
    private Date applyTime;

    @ApiModelProperty("关联通道id")
    private String channelIds;

    @ApiModelProperty("是否被新设备使用（萤石云新设备可远程配置GB）0:未使用1:已使用")
    private Integer useByNew;

    @ApiModelProperty("申请人")
    private String applyUser;

    @ApiModelProperty("注册有效时长 秒")
    private Integer registerValidity;

    @ApiModelProperty("心跳检测周期 秒")
    private Integer heartbeatCycle;

    @ApiModelProperty("心跳最大超时次数")
    private Integer heartbeatTimeout;

    @ApiModelProperty("创建人")
    private String createName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String updateName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("名称（页面设备名称）")
    private String name;

    @ApiModelProperty("设备序列号")
    private String deviceSerial;

    @ApiModelProperty("萤石设备本身序列号")
    private String ysDeviceSerial;
}