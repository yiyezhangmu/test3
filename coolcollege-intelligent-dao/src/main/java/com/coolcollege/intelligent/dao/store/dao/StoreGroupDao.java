package com.coolcollege.intelligent.dao.store.dao;

import com.coolcollege.intelligent.dao.store.StoreGroupMapper;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * 门店分组
 * @author zhangnan
 * @date 2022-05-10 14:56
 */
@Repository
public class StoreGroupDao {

    @Resource
    private StoreGroupMapper storeGroupMapper;

    /**
     * 根据来源查询分组
     * @param eid
     * @param source
     * @return
     */
    public List<StoreGroupDO> selectGroupBySource(String eid, String source) {
        if(StringUtils.isAnyBlank(eid, source)) {
            return Lists.newArrayList();
        }
        return storeGroupMapper.selectGroupBySource(eid, source);
    }

    /**
     * 根据ids删除分组
     * @param eid
     * @param groupIds
     */
    public void deleteByIds(String eid, List<String> groupIds) {
        if(StringUtils.isBlank(eid) || CollectionUtils.isEmpty(groupIds)) {
            return;
        }
        storeGroupMapper.batchDeleteStoreGroup(eid, groupIds);
    }

    /**
     * 批量新增
     * @param eid
     * @param insertGroups
     */
    public void batchInsertGroup(String eid, List<StoreGroupDO> insertGroups) {
        if(StringUtils.isBlank(eid) || CollectionUtils.isEmpty(insertGroups)) {
            return;
        }
        storeGroupMapper.batchInsertGroup(eid, insertGroups);
    }

    /**
     * 批量更新
     * @param eid
     * @param updateGroups
     */
    public void batchUpdateGroup(String eid, List<StoreGroupDO> updateGroups) {
        if(StringUtils.isBlank(eid) || CollectionUtils.isEmpty(updateGroups)) {
            return;
        }
        storeGroupMapper.batchUpdateGroup(eid, updateGroups);
    }
}
