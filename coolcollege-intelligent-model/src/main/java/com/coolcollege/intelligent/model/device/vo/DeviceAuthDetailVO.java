package com.coolcollege.intelligent.model.device.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeviceAuthDetailVO {

    @ApiModelProperty("授权平台列表")
    private List<DeviceAuthAppVO> appList;

    @ApiModelProperty("授权记录列表")
    private List<DeviceAuthRecordVO> recordList;

    public DeviceAuthDetailVO(List<DeviceAuthAppVO> appList, List<DeviceAuthRecordVO> recordList) {
        this.appList = appList;
        this.recordList = recordList;
    }
}
