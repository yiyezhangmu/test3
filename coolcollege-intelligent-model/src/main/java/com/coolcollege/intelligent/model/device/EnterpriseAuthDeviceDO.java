package com.coolcollege.intelligent.model.device;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.device.DeviceAuthStatusEnum;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 
 * @author   zhangchenbiao
 * @date   2025-07-01 03:43
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseAuthDeviceDO implements Serializable {
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

    @ApiModelProperty("设备状态")
    private String deviceStatus;

    @ApiModelProperty("授权状态 0未授权, 1授权中, 2取消授权")
    private Integer authStatus;

    @ApiModelProperty("授权开始时间")
    private Date authStartTime;

    @ApiModelProperty("授权结束时间")
    private Date authEndTime;

    @ApiModelProperty("取消授权时间")
    private Date cancelAuthTime;

    @ApiModelProperty("授权平台 美团:meituan、饿了么eleme")
    private String appId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    public static Map<String,  DeviceAuthStatusEnum> getAuthStatus(List<EnterpriseAuthDeviceDO> authDeviceList){
        if(CollectionUtils.isEmpty(authDeviceList)){
            return Maps.newHashMap();
        }
        Map<String, DeviceAuthStatusEnum> authStatusMap = Maps.newHashMap();
        Map<String, List<EnterpriseAuthDeviceDO>> deviceMap = authDeviceList.stream().collect(Collectors.groupingBy(o -> o.getDeviceId() + Constants.MOSAICS + o.getChannelNo() + Constants.MOSAICS + o.getAppId()));
        deviceMap.keySet().forEach(deviceId -> {
            List<EnterpriseAuthDeviceDO> authList = deviceMap.get(deviceId);
            if(CollectionUtils.isEmpty(authList)){
                authStatusMap.put(deviceId, DeviceAuthStatusEnum.NO_AUTH);
                return;
            }
            //处理有没有授权中 但是已经过期了的情况
            List<EnterpriseAuthDeviceDO> expiredList = authList.stream().filter(authDevice -> DeviceAuthStatusEnum.AUTH.getCode().equals(authDevice.getAuthStatus()))
                    .filter(authDevice -> authDevice.getAuthEndTime().before(new Date())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(expiredList)){
                authStatusMap.put(deviceId, DeviceAuthStatusEnum.NO_AUTH);
            }else{
                EnterpriseAuthDeviceDO enterpriseAuthDeviceDO = authList.get(0);
                authStatusMap.put(deviceId, DeviceAuthStatusEnum.getStatusEnumByCode(enterpriseAuthDeviceDO.getAuthStatus()));
            }
        });
        return authStatusMap;
    }
}