package com.coolcollege.intelligent.model.store.dto;

import lombok.Data;

@Data
public class StoreDeviceDTO {

    /**
     * 门店id
     */
    String storeId;
    /**
     * 设备id
     */
    String deviceId;
    /**
     * 设备名称
     */
    String deviceName;

    /**
     * 设备类型
     */
    String deviceType;
    /**
     * 备注
     */
    String remark;
}
