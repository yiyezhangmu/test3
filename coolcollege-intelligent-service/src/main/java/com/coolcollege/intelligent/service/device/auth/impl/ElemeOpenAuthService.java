package com.coolcollege.intelligent.service.device.auth.impl;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.model.device.EnterpriseAuthDeviceDO;
import com.coolcollege.intelligent.service.device.auth.OpenAuthService;
import eleme.openapi.sdk.api.entity.shop.DeviceInfo;
import eleme.openapi.sdk.api.entity.shop.LicenseCheckDTO;
import eleme.openapi.sdk.api.entity.shop.LicenseDeviceInfo;
import eleme.openapi.sdk.api.entity.shop.OVideoDeviceDTO;
import eleme.openapi.sdk.api.exception.ServiceException;
import eleme.openapi.sdk.api.service.ShopService;
import eleme.openapi.sdk.config.Config;
import eleme.openapi.sdk.oauth.response.Token;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 饿了么设备授权服务实现
 */
@Slf4j
@Service("elemeOpenAuthService")
public class ElemeOpenAuthService implements OpenAuthService {

    private static ShopService shopService;

    @Value("${eleme.isSandbox:false}")
    private Boolean isSandbox;

    @Value("${eleme.appKey:null}")
    private String appKey;

    @Value("${eleme.appSecret:null}")
    private String appSecret;

    @Value("${eleme.supplyId:113}")
    private String supplyId;

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        Config config = new Config(isSandbox, appKey, appSecret);
        Token token = new Token();
        token.setAccessToken("");
        shopService = new ShopService(config, token);
    }

    @Override
    public Boolean authDevice(List<EnterpriseAuthDeviceDO> authDeviceList) {
        if(CollectionUtils.isEmpty(authDeviceList)){
            return false;
        }
        // 批量授权
        Map<String, List<EnterpriseAuthDeviceDO>> storeAuthDeviceMap = authDeviceList.stream().collect(Collectors.groupingBy(EnterpriseAuthDeviceDO::getThirdStoreId));
        storeAuthDeviceMap.forEach((thirdStoreId, storeDeviceList) -> {
            try {
                LicenseCheckDTO licenseCheckDTO = new LicenseCheckDTO();
                licenseCheckDTO.setSupplyId(supplyId);
                licenseCheckDTO.setLicenseNo(thirdStoreId);
                Boolean result = shopService.validateShopVideoStatusNew(licenseCheckDTO);
                log.info("门店{}是否已开通结果：{}", thirdStoreId, result);
                if(result){
                    LicenseDeviceInfo licenseDeviceInfo = new LicenseDeviceInfo();
                    licenseDeviceInfo.setSupplyId(supplyId);
                    licenseDeviceInfo.setLicenseNo(thirdStoreId);
                    licenseDeviceInfo.setOperateType("1");
                    List<DeviceInfo> deviceList = convertList(storeDeviceList);
                    licenseDeviceInfo.setDeviceList(deviceList);
                    Boolean b = shopService.syncShopVideoDevices(licenseDeviceInfo);
                    log.info("授权结果：{}", b);
                }
            } catch (ServiceException e) {
                log.error("授权失败：" + e.getMessage());
                throw new com.coolcollege.intelligent.common.exception.ServiceException(ErrorCodeEnum.ERROR, "饿了么接口调用报错：" + e.getMessage());
            }
        });
        try {
            List<OVideoDeviceDTO> openVideoDeviceList = convertOpenList(authDeviceList);
            for (OVideoDeviceDTO videoDevice : openVideoDeviceList) {
                //同步状态
                Boolean b = shopService.setVideoStatus(videoDevice);
                log.info("设备id:{},同步状态结果：{}", videoDevice.getDeviceSn(), b);
            }
        } catch (ServiceException e) {
            log.error("更新设备状态失败：" + e.getMessage());
        }
        return false;
    }

    @Override
    public Boolean authDevice(EnterpriseAuthDeviceDO authDevice) {
        // 批量授权
        try {
            LicenseCheckDTO licenseCheckDTO = new LicenseCheckDTO();
            licenseCheckDTO.setSupplyId(supplyId);
            licenseCheckDTO.setLicenseNo(authDevice.getThirdStoreId());
            Boolean result = shopService.validateShopVideoStatusNew(licenseCheckDTO);
            log.info("门店{}是否已开通结果：{}", authDevice.getThirdStoreId(), result);
            if(result){
                LicenseDeviceInfo licenseDeviceInfo = new LicenseDeviceInfo();
                licenseDeviceInfo.setSupplyId(supplyId);
                licenseDeviceInfo.setLicenseNo(authDevice.getThirdStoreId());
                licenseDeviceInfo.setOperateType("1");
                List<DeviceInfo> deviceList = convertList(Collections.singletonList(authDevice));
                licenseDeviceInfo.setDeviceList(deviceList);
                Boolean authResult = shopService.syncShopVideoDevices(licenseDeviceInfo);
                log.info("设备id:{}, 授权结果：{}", authDevice.getDeviceId(), authResult);
                List<OVideoDeviceDTO> openVideoDeviceList = convertOpenList(Collections.singletonList(authDevice));
                for (OVideoDeviceDTO videoDevice : openVideoDeviceList) {
                    //同步状态
                    Boolean b = shopService.setVideoStatus(videoDevice);
                    log.info("设备id:{},同步状态结果：{}", videoDevice.getDeviceSn(), b);
                }
            }
        } catch (ServiceException e) {
            log.error("授权失败：" + e.getMessage());
            throw new com.coolcollege.intelligent.common.exception.ServiceException(ErrorCodeEnum.ERROR, "饿了么接口调用报错：" + e.getMessage());
        }
        return true;
    }

    @Override
    public Boolean cancelDevice(EnterpriseAuthDeviceDO authDevice) {
        // 批量授权
        try {
            LicenseDeviceInfo licenseDeviceInfo = new LicenseDeviceInfo();
            licenseDeviceInfo.setSupplyId(supplyId);
            licenseDeviceInfo.setLicenseNo(authDevice.getThirdStoreId());
            licenseDeviceInfo.setOperateType("3");
            List<DeviceInfo> deviceList = convertList(Collections.singletonList(authDevice));
            licenseDeviceInfo.setDeviceList(deviceList);
            shopService.syncShopVideoDevices(licenseDeviceInfo);
        } catch (ServiceException e) {
            log.error("取消授权失败：" + e.getMessage());
            throw new com.coolcollege.intelligent.common.exception.ServiceException(ErrorCodeEnum.ERROR, "饿了么接口调用报错：" + e.getMessage());
        }
        return true;
    }

    @Override
    public Boolean cancelDevice(List<EnterpriseAuthDeviceDO> authDeviceList) {
        if(CollectionUtils.isEmpty(authDeviceList)){
            return false;
        }
        Map<String, List<EnterpriseAuthDeviceDO>> storeAuthDeviceMap = authDeviceList.stream().collect(Collectors.groupingBy(EnterpriseAuthDeviceDO::getThirdStoreId));
        storeAuthDeviceMap.forEach((storeId, storeDeviceList) -> {
            try {
                // 批量授权
                LicenseDeviceInfo licenseDeviceInfo = new LicenseDeviceInfo();
                licenseDeviceInfo.setSupplyId(supplyId);
                licenseDeviceInfo.setLicenseNo(storeId);
                licenseDeviceInfo.setOperateType("3");
                List<DeviceInfo> deviceList = convertList(storeDeviceList);
                licenseDeviceInfo.setDeviceList(deviceList);
                shopService.syncShopVideoDevices(licenseDeviceInfo);
            } catch (ServiceException e) {
                log.error("取消授权失败：" + e.getMessage());
                throw new com.coolcollege.intelligent.common.exception.ServiceException(ErrorCodeEnum.ERROR, "饿了么接口调用报错：" + e.getMessage());
            }
        });
        return false;
    }

    private List<DeviceInfo> convertList(List<EnterpriseAuthDeviceDO> authDeviceList) {
        List<DeviceInfo> deviceList = new ArrayList<>();
        for (EnterpriseAuthDeviceDO authDevice : authDeviceList) {
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setDeviceSn(authDevice.getDeviceId());
            deviceInfo.setDeviceName(authDevice.getDeviceName());
            deviceInfo.setSubType(Integer.valueOf(authDevice.getChannelNo()));
            deviceInfo.setOutChannel(authDevice.getChannelNo());
            deviceList.add(deviceInfo);
        }
        return deviceList;
    }

    private List<OVideoDeviceDTO> convertOpenList(List<EnterpriseAuthDeviceDO> authDeviceList) {
        List<OVideoDeviceDTO> deviceList = new ArrayList<>();
        for (EnterpriseAuthDeviceDO authDevice : authDeviceList) {
            OVideoDeviceDTO deviceInfo = new OVideoDeviceDTO();
            deviceInfo.setSupplyId(supplyId);
            deviceInfo.setDeviceSn(authDevice.getDeviceId());
            deviceInfo.setDeviceStatus(DeviceStatusEnum.ONLINE.getCode().equals(authDevice.getDeviceStatus()) ? "ONLINE" : "OFFLINE");
            deviceInfo.setStatusChangeTime(System.currentTimeMillis());
            deviceInfo.setOutChannel(authDevice.getChannelNo());
            deviceInfo.setDeviceStatusChangeReason(null);
            deviceList.add(deviceInfo);
        }
        return deviceList;
    }
}