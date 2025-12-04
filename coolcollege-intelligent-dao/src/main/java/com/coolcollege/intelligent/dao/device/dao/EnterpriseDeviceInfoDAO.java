package com.coolcollege.intelligent.dao.device.dao;

import com.coolcollege.intelligent.dao.device.EnterpriseDeviceInfoMapper;
import com.coolcollege.intelligent.model.device.EnterpriseDeviceInfoDO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class EnterpriseDeviceInfoDAO {

    @Resource
    private EnterpriseDeviceInfoMapper enterpriseDeviceInfoMapper;
    public void batchInsertOrUpdate(List<EnterpriseDeviceInfoDO> recordList) {
        if(CollectionUtils.isEmpty(recordList)){
            return;
        }
        enterpriseDeviceInfoMapper.batchInsertOrUpdate(recordList);
    }

    public void batchInsertOrUpdateV2(List<EnterpriseDeviceInfoDO> recordList) {
        if(CollectionUtils.isEmpty(recordList)){
            return;
        }
        enterpriseDeviceInfoMapper.batchInsertOrUpdateV2(recordList);
    }

    public List<String> getEnterpriseIdsByDeviceId(String deviceId) {
        return enterpriseDeviceInfoMapper.getEnterpriseIdsByDeviceId(deviceId);
    }

    public List<EnterpriseDeviceInfoDO> getEnterpriseIdsByDeviceIdV2(String enterpriseId, String parentDeviceId) {
        return enterpriseDeviceInfoMapper.getEnterpriseIdsByDeviceIdV2(enterpriseId, parentDeviceId);
    }

}
