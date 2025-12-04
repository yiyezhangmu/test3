package com.coolcollege.intelligent.dao.store;

import com.coolcollege.intelligent.model.store.StoreDeviceMappingDO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author shoul
 */
@Repository
public class StoreDeviceMappingDao {

    @Resource
    private StoreDeviceMappingMapper storeDeviceMappingMapper;

    public List<StoreDeviceMappingDO> getDeviceMapping(String enterpriseId, List<String> storeIds, List<String> dingIds) {
        return storeDeviceMappingMapper.getSyncDeviceMapping(enterpriseId, storeIds, dingIds);
    }


}