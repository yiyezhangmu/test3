package com.coolcollege.intelligent.dao.store.dao;

import com.coolcollege.intelligent.dao.store.StoreSignInfoMapper;
import com.coolcollege.intelligent.model.store.StoreSignInfoDO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author byd
 * @date 2023-05-18 14:20
 */
@Repository
public class StoreSignInfoDao {

    @Resource
    private StoreSignInfoMapper storeSignInfoMapper;

    public StoreSignInfoDO selectByStoreIdAndSignDate(String enterpriseId, String storeId, String signDate, String userId) {
        return storeSignInfoMapper.selectByStoreIdAndSignDate(enterpriseId, storeId, signDate, userId);
    }

    public StoreSignInfoDO selectByPrimaryKey(String enterpriseId, Long id) {
        return storeSignInfoMapper.selectByPrimaryKey(id, enterpriseId);
    }


    public void insertSelective(String enterpriseId, StoreSignInfoDO record) {
        storeSignInfoMapper.insertSelective(record, enterpriseId);
    }

    public void updateByPrimaryKeySelective(String enterpriseId, StoreSignInfoDO record) {
        storeSignInfoMapper.updateByPrimaryKeySelective(record, enterpriseId);
    }

    public List<StoreSignInfoDO> list(String enterpriseId, String beginDate, String endDate,List<String> regionWays,String storeName,List<String> userIdList){
        return storeSignInfoMapper.list(enterpriseId, beginDate, endDate,regionWays,storeName, userIdList);
    }
}
