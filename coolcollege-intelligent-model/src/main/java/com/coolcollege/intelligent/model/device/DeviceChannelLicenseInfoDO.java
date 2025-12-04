package com.coolcollege.intelligent.model.device;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 平台分配国标通道授权
 * @author   wangff
 * @date   2025-08-11 04:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceChannelLicenseInfoDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("国标通道序列号")
    private String channelSerial;

    @ApiModelProperty("通道名称")
    private String channelName;

    @ApiModelProperty("通道")
    private String channelNo;

    @ApiModelProperty("通道所属设备国标编号")
    private String deviceCode;

    @ApiModelProperty("通道所属国标license")
    private Long licenseId;

    @ApiModelProperty("创建人")
    private String createName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String updateName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("绑定时间(申请时间)")
    private Date bindingTime;
}