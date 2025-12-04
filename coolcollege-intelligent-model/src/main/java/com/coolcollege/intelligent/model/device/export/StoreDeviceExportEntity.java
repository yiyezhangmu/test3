package com.coolcollege.intelligent.model.device.export;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class StoreDeviceExportEntity {
    //门店id
    @Excel(name = "门店id(必填)")
    private String storeId;
    @Excel(name = "门店名称")
    private String storeName;
    @Excel(name = "设备名称(必填)")
    private String deviceName;
    @Excel(name = "设备id(必填)")
    private String deviceId;
    @Excel(name = "(必填)设备场景(店外客流、店内客流、试衣间客流、其他)")
    private String scene;
    @Excel(name = "备注")
    private String remake;

}
