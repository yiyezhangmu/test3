package com.coolcollege.intelligent.service.video.manager.Impl;

import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.video.platform.yingshi.DeviceCapacityDTO;
import com.coolcollege.intelligent.service.video.YingshiDeviceService;
import com.coolcollege.intelligent.service.video.manager.YingshiDeviceManager;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.YunTypeEnum;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2021/9/2 17:52
 * @Version 1.0
 */
@Service
public class YingshiDeviceManagerImpl implements YingshiDeviceManager {
    @Autowired
    private YingshiDeviceService yingshiDeviceService;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private DeviceChannelMapper deviceChannelMapper;


    @Override
    public Boolean syncInventoryData() {
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigMapper.selectEnterpriseConfigAll();
        for (EnterpriseConfigDO enterpriseConfigDO:enterpriseConfigList) {
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
            //查询该企业的设备
            List<DeviceDO> deviceDOS = deviceMapper.selectAllDevice(enterpriseConfigDO.getEnterpriseId(), DeviceTypeEnum.DEVICE_VIDEO.getCode());
            //萤石云token
            String yingshiToken = yingshiDeviceService.getRedisToken(enterpriseConfigDO.getEnterpriseId());
            //筛选萤石云的设备
            deviceDOS = deviceDOS.stream().filter(deviceDO -> deviceDO.getResource().equals(YunTypeEnum.YINGSHIYUN.getCode())).collect(Collectors.toList());
            if (deviceDOS.size()==0){
                continue;
            }
            List<DeviceDO> deviceList = ListUtils.emptyIfNull(deviceDOS).stream()
                    .map(data -> {
                        DeviceCapacityDTO yingshiDeviceCapaticy = yingshiDeviceService.getYingshiDeviceCapaticy(data.getDeviceId(), yingshiToken);
                        data.setSupportCapture(yingshiDeviceCapaticy.getSupportCapture());
                        return data;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            //默认是不支持抓拍。。只需要修改具有抓拍的设备和子设备  筛选出supportCapture = 1的设备 将device表中supportCapture改为1, 筛选出这些设备中有子设备的
            List<DeviceDO> supportCapturedeviceList = deviceList.stream().
                    filter(data -> data.getSupportCapture() == 1).collect(Collectors.toList());
            //更新支持抓拍的设备
            if (supportCapturedeviceList!=null&&supportCapturedeviceList.size()!=0){
                deviceMapper.updateDeviceByDeviceId(enterpriseConfigDO.getEnterpriseId(),supportCapturedeviceList);
            }
            //筛选有子设备的设备
            List<DeviceDO> HasChildDeviceList = supportCapturedeviceList.stream().
                    filter(data -> data.getHasChildDevice() != null && data.getHasChildDevice()).collect(Collectors.toList());

            List<String> deviceIdList = HasChildDeviceList.stream().map(DeviceDO::getDeviceId).collect(Collectors.toList());
            if (HasChildDeviceList!=null&HasChildDeviceList.size()!=0){
                deviceChannelMapper.updateDeviceChannalByDeviceId(enterpriseConfigDO.getEnterpriseId(),deviceIdList);
            }
        }
        return true;
    }
}
