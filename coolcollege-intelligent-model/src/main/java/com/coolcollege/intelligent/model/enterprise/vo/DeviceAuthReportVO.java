package com.coolcollege.intelligent.model.enterprise.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeviceAuthReportVO {

    private String enterpriseId;

    @Excel(name = "企业名称")
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    @Excel(name = "CSM")
    @ApiModelProperty(value = "csm")
    private String csm;

    @Excel(name = "总门店数")
    @ApiModelProperty(value = "总门店数")
    private Integer totalStoreNum;

    @Excel(name = "营业门店数")
    @ApiModelProperty(value = "营业门店数")
    private Integer totalOpenStoreNum;

    @Excel(name = "美团总授权设备数")
    @ApiModelProperty(value = "美团总授权设备数")
    private Integer meituanAuthDeviceNum;

    @Excel(name = "美团当前授权设备数")
    @ApiModelProperty(value = "美团当前授权设备数")
    private Integer meituanCurrentlyAuthDeviceNum;

    @Excel(name = "美团本月授权设备数")
    @ApiModelProperty(value = "美团本月授权设备数")
    private Integer meituanMonthlyAuthorizedDeviceNum;

    @Excel(name = "美团本月调用次数")
    @ApiModelProperty(value = "美团本月调用次数")
    private Integer meituanMonthlyCallNum;

    @Excel(name = "饿了么总授权设备数")
    @ApiModelProperty(value = "饿了么总授权设备数")
    private Integer elemeAuthDeviceNum;

    @Excel(name = "饿了么当前授权设备数")
    @ApiModelProperty(value = "饿了么当前授权设备数")
    private Integer elemeCurrentlyAuthDeviceNum;

    @Excel(name = "饿了么本月授权设备数")
    @ApiModelProperty(value = "饿了么本月授权设备数")
    private Integer elemeMonthlyAuthorizedDeviceNum;

    @Excel(name = "饿了么本月调用次数")
    @ApiModelProperty(value = "饿了么本月调用次数")
    private Integer elemeMonthlyCallNum;

}
