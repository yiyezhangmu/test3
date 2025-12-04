package com.coolcollege.intelligent.dao.store.dao;

import com.coolcollege.intelligent.dao.store.StoreGroupMappingMapper;
import com.coolcollege.intelligent.model.store.StoreGroupMappingDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * 门店分组映射
 * @author zhangnan
 * @date 2022-05-10 14:57
 */
@Repository
public class StoreGroupMappingDao {

    @Resource
    private StoreGroupMappingMapper storeGroupMappingMapper;

    /**
     * 根据分组ids删除
     * @param eid
     * @param groupIds
     */
    public void deleteByGroupIds(String eid, List<String> groupIds) {
        if(StringUtils.isBlank(eid) || CollectionUtils.isEmpty(groupIds)) {
            return;
        }
        storeGroupMappingMapper.batchDeleteMappingByGroupIdList(eid, groupIds);
    }

    /**
     * 批量新增
     * @param eid
     * @param mappingDOList
     */
    public void batchInsertGroupMapping(String eid, List<StoreGroupMappingDO> mappingDOList) {
        if(StringUtils.isBlank(eid) || CollectionUtils.isEmpty(mappingDOList)) {
            return;
        }
        storeGroupMappingMapper.insertGroupMappingList(eid, mappingDOList);
    }

    /**
     * 根据门店id列表和分组id删除
     * @param eid
     * @param groupId
     * @param storeDeptIdList
     * @author: xugangkun
     * @return void
     * @date: 2022/5/18 17:06
     */
    public void deleteByGroupIdAndStoreIdList(String eid, String groupId, List<String> storeDeptIdList) {
        if (StringUtils.isBlank(eid) || StringUtils.isBlank(groupId) || CollectionUtils.isEmpty(storeDeptIdList)) {
            return;
        }
        storeGroupMappingMapper.deleteByGroupIdAndStoreIdList(eid, groupId, storeDeptIdList);
    }

}
