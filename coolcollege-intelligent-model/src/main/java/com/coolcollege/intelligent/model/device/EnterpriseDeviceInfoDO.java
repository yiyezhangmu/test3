package com.coolcollege.intelligent.model.device;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 * @author   zhangchenbiao
 * @date   2025-04-28 07:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseDeviceInfoDO implements Serializable {
    @ApiModelProperty("自增ID")
    private Long id;

    @ApiModelProperty("企业Id")
    private String enterpriseId;

    @ApiModelProperty("设备id device_type=nvr_ipc时是parent_device_id_channel_no")
    private String deviceId;

    @ApiModelProperty("父设备Id")
    private String parentDeviceId;

    @ApiModelProperty("通道号")
    private String channelNo;

    @ApiModelProperty("设备类型 nvr、ipc、nvr_ipc")
    private String deviceType;

    @ApiModelProperty("云类型: ali 阿里云，yushi 宇视,yingshi 萤石云， other 其他")
    private String yunType;

    @ApiModelProperty("账号类型 私有账号 private, 平台账号platform")
    private String accountType;

    @ApiModelProperty("设备状态:离线offline 在线online")
    private String deviceStatus;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    public static List<EnterpriseDeviceInfoDO> convertEnterpriseDeviceInfo(String enterpriseId, List<DeviceDO> deviceList, List<DeviceChannelDO> channelList){
        List<EnterpriseDeviceInfoDO> resultList = new ArrayList<>();
        if(CollectionUtils.isEmpty(deviceList)){
            return null;
        }
        for (DeviceDO deviceDO : deviceList) {
            EnterpriseDeviceInfoDO enterpriseDeviceInfoDO = new EnterpriseDeviceInfoDO();
            enterpriseDeviceInfoDO.setEnterpriseId(enterpriseId);
            enterpriseDeviceInfoDO.setDeviceId(deviceDO.getDeviceId());
            enterpriseDeviceInfoDO.setDeviceType(deviceDO.getHasChildDevice() ? "nvr":"ipc");
            enterpriseDeviceInfoDO.setYunType(deviceDO.getResource());
            enterpriseDeviceInfoDO.setAccountType(deviceDO.getAccountType());
            enterpriseDeviceInfoDO.setDeviceStatus(deviceDO.getDeviceStatus());
            resultList.add(enterpriseDeviceInfoDO);
        }
        Map<String, DeviceDO> deviceMap = ListUtils.emptyIfNull(deviceList).stream().collect(Collectors.toMap(DeviceDO::getDeviceId, deviceDO -> deviceDO, (v1, v2) -> v1));
        for (DeviceChannelDO deviceChannelDO : ListUtils.emptyIfNull(channelList)) {
            DeviceDO device = deviceMap.get(deviceChannelDO.getParentDeviceId());
            EnterpriseDeviceInfoDO enterpriseDeviceInfoDO = new EnterpriseDeviceInfoDO();
            enterpriseDeviceInfoDO.setEnterpriseId(enterpriseId);
            enterpriseDeviceInfoDO.setDeviceId(deviceChannelDO.getParentDeviceId() + Constants.UNDERLINE + deviceChannelDO.getChannelNo());
            enterpriseDeviceInfoDO.setDeviceType("nvr_ipc");
            enterpriseDeviceInfoDO.setYunType(device.getResource());
            enterpriseDeviceInfoDO.setAccountType(device.getAccountType());
            enterpriseDeviceInfoDO.setDeviceStatus(deviceChannelDO.getStatus());
            resultList.add(enterpriseDeviceInfoDO);
        }
        return resultList;
    }

    public static List<EnterpriseDeviceInfoDO> convertEnterpriseDeviceInfo(String enterpriseId, DeviceDO device, List<DeviceChannelDO> channelList){
        List<EnterpriseDeviceInfoDO> resultList = new ArrayList<>();
        if(Objects.isNull(device)){
            return null;
        }
        EnterpriseDeviceInfoDO enterpriseDeviceInfoDO = new EnterpriseDeviceInfoDO();
        enterpriseDeviceInfoDO.setEnterpriseId(enterpriseId);
        enterpriseDeviceInfoDO.setDeviceId(device.getDeviceId());
        enterpriseDeviceInfoDO.setDeviceType(device.getHasChildDevice() ? "nvr":"ipc");
        enterpriseDeviceInfoDO.setYunType(device.getResource());
        enterpriseDeviceInfoDO.setAccountType(device.getAccountType());
        enterpriseDeviceInfoDO.setDeviceStatus(device.getDeviceStatus());
        resultList.add(enterpriseDeviceInfoDO);
        for (DeviceChannelDO deviceChannelDO : ListUtils.emptyIfNull(channelList)) {
            EnterpriseDeviceInfoDO channelDevice = new EnterpriseDeviceInfoDO();
            channelDevice.setEnterpriseId(enterpriseId);
            channelDevice.setDeviceId(deviceChannelDO.getParentDeviceId() + Constants.UNDERLINE + deviceChannelDO.getChannelNo());
            channelDevice.setDeviceType("nvr_ipc");
            channelDevice.setYunType(device.getResource());
            channelDevice.setAccountType(device.getAccountType());
            channelDevice.setDeviceStatus(deviceChannelDO.getStatus());
            resultList.add(channelDevice);
        }
        return resultList;
    }
}