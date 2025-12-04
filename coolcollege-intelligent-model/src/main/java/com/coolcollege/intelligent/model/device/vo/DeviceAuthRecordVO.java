package com.coolcollege.intelligent.model.device.vo;

import com.coolcollege.intelligent.common.enums.device.DeviceAuthAppEnum;
import com.coolcollege.intelligent.model.device.EnterpriseAuthDeviceDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class DeviceAuthRecordVO {

    @ApiModelProperty("设备ID")
    private String deviceId;

    @ApiModelProperty("设备名称")
    private String deviceName;

    @ApiModelProperty("通道号")
    private String channelNo;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("门店Id")
    private String storeId;

    @ApiModelProperty("第三方门店id")
    private String thirdStoreId;

    @ApiModelProperty("授权状态 0未授权, 1授权中, 2取消授权")
    private Integer authStatus;

    @ApiModelProperty("授权开始时间")
    private Date authStartTime;

    @ApiModelProperty("授权结束时间")
    private Date authEndTime;

    @ApiModelProperty("取消授权时间")
    private Date cancelAuthTime;

    @ApiModelProperty("授权平台")
    private String appName;

    public static List<DeviceAuthRecordVO> convertList(List<EnterpriseAuthDeviceDO> recordList) {
        if (CollectionUtils.isEmpty(recordList)) {
            return Lists.newArrayList();
        }
        List<DeviceAuthRecordVO> resultList = new ArrayList<>();
        for (EnterpriseAuthDeviceDO recordDO : recordList) {
            DeviceAuthRecordVO recordVO = new DeviceAuthRecordVO();
            recordVO.setDeviceId(recordDO.getDeviceId());
            recordVO.setDeviceName(recordDO.getDeviceName());
            recordVO.setChannelNo(recordDO.getChannelNo());
            recordVO.setStoreId(recordDO.getStoreId());
            recordVO.setAuthStatus(recordDO.getAuthStatus());
            recordVO.setAuthStartTime(recordDO.getAuthStartTime());
            recordVO.setAuthEndTime(recordDO.getAuthEndTime());
            recordVO.setCancelAuthTime(recordDO.getCancelAuthTime());
            recordVO.setThirdStoreId(recordDO.getThirdStoreId());
            DeviceAuthAppEnum appEnum = DeviceAuthAppEnum.getByAppId(recordDO.getAppId());
            String appName = appEnum != null ? appEnum.getAppName() : "";
            recordVO.setAppName(appName);
            resultList.add(recordVO);
        }
        return resultList;
    }
}
