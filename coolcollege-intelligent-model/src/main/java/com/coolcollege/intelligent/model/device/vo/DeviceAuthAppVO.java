package com.coolcollege.intelligent.model.device.vo;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.device.DeviceAuthAppEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceAuthStatusEnum;
import com.coolcollege.intelligent.model.device.EnterpriseAuthDeviceDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class DeviceAuthAppVO {

    @ApiModelProperty("appId")
    private String appId;

    @ApiModelProperty("平台名称")
    private String appName;

    @ApiModelProperty("授权状态 0未授权, 1授权中, 2取消授权")
    private Integer authStatus;

    @ApiModelProperty("图标")
    private String appIcon;

    public static List<DeviceAuthAppVO> convertList(List<EnterpriseAuthDeviceDO> recordList){
        List<DeviceAuthAppVO> resultList = new ArrayList<>();
        if(CollectionUtils.isEmpty(recordList)){
            for (DeviceAuthAppEnum value : DeviceAuthAppEnum.values()) {
                if(value.isHidden()){
                    continue;
                }
                DeviceAuthAppVO appVO = new DeviceAuthAppVO();
                appVO.setAppId(value.getAppId());
                appVO.setAppName(value.getAppName());
                appVO.setAuthStatus(DeviceAuthStatusEnum.NO_AUTH.getCode());
                appVO.setAppIcon(value.getAppIcon());
                resultList.add(appVO);
            }
            return resultList;
        }
        Map<String, List<EnterpriseAuthDeviceDO>> appAuthRecordMap = recordList.stream().collect(Collectors.groupingBy(EnterpriseAuthDeviceDO::getAppId));
        for (DeviceAuthAppEnum value : DeviceAuthAppEnum.values()) {
            if(value.isHidden()){
                continue;
            }
            DeviceAuthAppVO appVO = new DeviceAuthAppVO();
            appVO.setAppId(value.getAppId());
            appVO.setAppName(value.getAppName());
            appVO.setAppIcon(value.getAppIcon());
            List<EnterpriseAuthDeviceDO> appAuthList = appAuthRecordMap.get(value.getAppId());
            if(CollectionUtils.isEmpty(appAuthList)){
                appVO.setAuthStatus(DeviceAuthStatusEnum.NO_AUTH.getCode());
            }else{
                //处理有没有授权中 但是已经过期了的情况
                List<EnterpriseAuthDeviceDO> expiredList = appAuthList.stream().filter(authDevice -> DeviceAuthStatusEnum.AUTH.getCode().equals(authDevice.getAuthStatus()))
                        .filter(authDevice -> authDevice.getAuthEndTime().before(new Date())).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(expiredList)){
                    appVO.setAuthStatus(DeviceAuthStatusEnum.NO_AUTH.getCode());
                }else{
                    EnterpriseAuthDeviceDO enterpriseAuthDeviceDO = appAuthList.get(0);
                    appVO.setAuthStatus(enterpriseAuthDeviceDO.getAuthStatus());
                }
            }
            resultList.add(appVO);
        }
        return resultList;
    }

    public static List<DeviceAuthAppVO> convertList(String deviceId, Map<String, DeviceAuthStatusEnum> statusEnumMap){
        List<DeviceAuthAppVO> resultList = new ArrayList<>();
        for (DeviceAuthAppEnum value : DeviceAuthAppEnum.values()) {
            if(value.isHidden()){
                continue;
            }
            DeviceAuthAppVO appVO = new DeviceAuthAppVO();
            appVO.setAppId(value.getAppId());
            appVO.setAppName(value.getAppName());
            DeviceAuthStatusEnum statusEnum = statusEnumMap.getOrDefault(deviceId + Constants.MOSAICS + value.getAppId(), DeviceAuthStatusEnum.NO_AUTH);
            appVO.setAuthStatus(statusEnum.getCode());
            appVO.setAppIcon(value.getAppIcon());
            resultList.add(appVO);
        }
        return resultList;
    }

}
