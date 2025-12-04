package com.coolcollege.intelligent.model.device.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author suzhuhong
 * @Date 2022/12/16 14:12
 * @Version 1.0
 */
@Data
public class DeviceSummaryListDTO {


    private String avatar;
    private String storeId;
    @Excel(name = "门店名称", width = 20, orderNum = "1")
    private String storeName;
    @Excel(name = "所属区域", width = 20, orderNum = "2")
    private String allRegionName;
    @Excel(name = "设备在线率", width = 20, orderNum = "3", numFormat = "#.##%")
    private BigDecimal ipcDeviceOnlineRate;
    @Excel(name = "设备总数", width = 20, orderNum = "4")
    private Integer ipcTotal;
    @Excel(name = "设备在线", width = 20, orderNum = "5")
    private Integer ipcDeviceOnlineNum;
    @Excel(name = "设备离线", width = 20, orderNum = "6")
    private Integer ipcDeviceOfflineNum;

}
