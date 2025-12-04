package com.coolcollege.intelligent.dao.device.dao;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Collections;

/**
 * @author zhangnan
 * @date 2022-06-28 15:12
 */
@Repository
public class DeviceDao {

    @Resource
    private DeviceMapper deviceMapper;

    public Integer count(String enterpriseId) {
        if(StringUtils.isBlank(enterpriseId)) {
            return Constants.ZERO;
        }
        return deviceMapper.count(enterpriseId);
    }

    public Integer countDeviceByStoreId(String enterpriseId, String deviceType, String storeId){
        return deviceMapper.countDeviceByStoreId(enterpriseId, deviceType, storeId);
    }

    public DeviceDO getDeviceByDeviceId(String enterpriseId, String deviceId) {
        return deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
    }

    public List<DeviceDO> getDeviceByDeviceIdList(String enterpriseId, List<String> deviceIdList){
        if(CollectionUtils.isEmpty(deviceIdList)){
            return Lists.newArrayList();
        }
        return deviceMapper.getDeviceByDeviceIdList(enterpriseId, deviceIdList);
    }

    /**
     * 根据门店id查询设备
     * @param enterpriseId 企业id
     * @param storeIds 门店id列表
     * @param storeSceneIds 场景id列表
     * @param resourceList 设备来源列表
     * @return 设备列表
     */
    public List<DeviceDO> getDeviceByStoreIds(String enterpriseId, List<String> storeIds, List<Long> storeSceneIds, List<String> resourceList) {
        if (CollectionUtils.isEmpty(storeIds)) {
            return Collections.emptyList();
        }
        return deviceMapper.getByStoreIds(enterpriseId, storeIds, storeSceneIds, resourceList);
    }
}
