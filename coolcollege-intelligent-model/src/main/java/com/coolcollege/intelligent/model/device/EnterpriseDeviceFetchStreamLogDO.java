package com.coolcollege.intelligent.model.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 
 * @author   zhangchenbiao
 * @date   2025-07-31 03:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseDeviceFetchStreamLogDO implements Serializable {
    @ApiModelProperty("自增ID")
    private Long id;

    @ApiModelProperty("企业Id")
    private String enterpriseId;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("第三方门店id")
    private String thirdStoreId;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("设备名称")
    private String deviceName;

    @ApiModelProperty("通道号")
    private String channelNo;

    @ApiModelProperty("授权平台 美团:meituan、饿了么eleme")
    private String appId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    public static EnterpriseDeviceFetchStreamLogDO convert(EnterpriseAuthDeviceDO param){
        if(Objects.isNull(param)){
            return null;
        }
        EnterpriseDeviceFetchStreamLogDO result = new EnterpriseDeviceFetchStreamLogDO();
        result.setEnterpriseId(param.getEnterpriseId());
        result.setStoreId(param.getStoreId());
        result.setThirdStoreId(param.getThirdStoreId());
        result.setDeviceId(param.getDeviceId());
        result.setDeviceName(param.getDeviceId());
        result.setChannelNo(param.getChannelNo());
        result.setAppId(param.getAppId());
        result.setCreateTime(new java.util.Date());
        return result;
    }
}