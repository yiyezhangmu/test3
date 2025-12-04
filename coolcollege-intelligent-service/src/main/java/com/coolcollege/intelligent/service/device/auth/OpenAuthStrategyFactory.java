package com.coolcollege.intelligent.service.device.auth;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceAuthAppEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.model.device.EnterpriseAuthDeviceDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备授权策略工厂
 */
@Component
public class OpenAuthStrategyFactory {

    private final Map<String, OpenAuthService> strategies = new ConcurrentHashMap<>();

    @Autowired
    public OpenAuthStrategyFactory(Map<String, OpenAuthService> strategyMap) {
        strategies.putAll(strategyMap);
    }

    /**
     * 根据平台类型获取对应的授权服务
     * @param appId 平台类型
     * @return 对应的 OpenAuthService 实现
     */
    public OpenAuthService getStrategy(String appId) {
        DeviceAuthAppEnum appEnum = DeviceAuthAppEnum.getByAppId(appId);
        if (appEnum == null) {
            throw new ServiceException(ErrorCodeEnum.NOT_AUTH);
        }
        return strategies.get(appEnum.getServiceName());
    }

    public Boolean authDevice(List<EnterpriseAuthDeviceDO> authDeviceList, String appId) {
        return getStrategy(appId).authDevice(authDeviceList);
    }

    public Boolean authDevice(EnterpriseAuthDeviceDO authDevice, String appId) {
        return getStrategy(appId).authDevice(authDevice);
    }

    public Boolean cancelDevice(EnterpriseAuthDeviceDO authDevice, String appId) {
        return getStrategy(appId).cancelDevice(authDevice);
    }

    public Boolean cancelDevice(List<EnterpriseAuthDeviceDO> authDeviceList, String appId) {
        return getStrategy(appId).cancelDevice(authDeviceList);
    }


}