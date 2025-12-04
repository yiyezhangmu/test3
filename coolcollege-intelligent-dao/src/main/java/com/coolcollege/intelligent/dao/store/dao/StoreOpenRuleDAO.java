package com.coolcollege.intelligent.dao.store.dao;

import com.coolcollege.intelligent.dao.store.StoreOpenRuleMapper;
import com.coolcollege.intelligent.model.store.StoreOpenRuleDO;
import com.coolcollege.intelligent.model.store.dto.CountStoreRuleDTO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author byd
 * @date 2023-05-12 14:14
 */
@Repository
public class StoreOpenRuleDAO {

    @Resource
    private StoreOpenRuleMapper storeOpenRuleMapper;

    public List<StoreOpenRuleDO> list(String enterpriseId, String createUserid,String regionId,String newStoreTaskStatus,List<String> mappingId,
                                      String ruleName) {
        return storeOpenRuleMapper.list(enterpriseId, createUserid,regionId,newStoreTaskStatus,mappingId, ruleName);
    }

    public int insertSelective(String enterpriseId, StoreOpenRuleDO record) {
        return storeOpenRuleMapper.insertSelective(record, enterpriseId);
    }

    public int updateByPrimaryKeySelective(String enterpriseId, StoreOpenRuleDO record) {
        return storeOpenRuleMapper.updateByPrimaryKeySelective(record, enterpriseId);
    }

    public StoreOpenRuleDO selectByPrimaryKey(String enterpriseId, Long id) {
        return storeOpenRuleMapper.selectByPrimaryKey(id, enterpriseId);
    }

    public CountStoreRuleDTO count(String eid,String regionId,List<String> mappingId) {
        return storeOpenRuleMapper.count(eid,regionId,mappingId);
    }
}
