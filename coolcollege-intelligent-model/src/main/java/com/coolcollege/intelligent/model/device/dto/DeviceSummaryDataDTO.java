package com.coolcollege.intelligent.model.device.dto;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/12/15 17:24
 * @Version 1.0
 */
@Data
public class DeviceSummaryDataDTO {

    private Integer ipcTotal;

    private Integer ipcDeviceOnlineNum;

    private Integer ipcDeviceOfflineNum;

    private int nvrTotal;

    private int nvrDeviceOnlineNum;

    private int nvrDeviceOfflineNum;



}
