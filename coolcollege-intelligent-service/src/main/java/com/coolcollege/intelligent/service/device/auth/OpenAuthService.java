package com.coolcollege.intelligent.service.device.auth;

import com.coolcollege.intelligent.model.device.EnterpriseAuthDeviceDO;

import java.util.List;

public interface OpenAuthService {

    /**
     * 设备授权
     * @param authDeviceList
     * @return
     */
    Boolean authDevice(List<EnterpriseAuthDeviceDO> authDeviceList);

    /**
     * 设备授权
     * @param authDevice
     * @return
     */
    Boolean authDevice(EnterpriseAuthDeviceDO authDevice);

    /**
     * 设备取消授权
     * @param authDevice
     * @return
     */
    Boolean cancelDevice(EnterpriseAuthDeviceDO authDevice);

    /**
     * 设备取消授权
     * @param authDeviceList
     * @return
     */
    Boolean cancelDevice(List<EnterpriseAuthDeviceDO> authDeviceList);

}
