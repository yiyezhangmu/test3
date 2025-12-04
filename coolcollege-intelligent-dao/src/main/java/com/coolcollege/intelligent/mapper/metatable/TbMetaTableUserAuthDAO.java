package com.coolcollege.intelligent.mapper.metatable;

import com.coolcollege.intelligent.dao.metatable.TbMetaTableUserAuthMapper;
import com.coolcollege.intelligent.model.metatable.TbMetaTableUserAuthDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class TbMetaTableUserAuthDAO {

    @Resource
    private TbMetaTableUserAuthMapper tbMetaTableUserAuthMapper;

    /**
     * 批量新增或更新
     * @param enterpriseId
     * @param addList
     */
    public void batchAddOrUpdate(String enterpriseId, List<TbMetaTableUserAuthDO> addList){
        if (CollectionUtils.isEmpty(addList)){
            return;
        }
        tbMetaTableUserAuthMapper.batchAddOrUpdate(enterpriseId,addList);
    }

    /**
     * 根据业务id删除
     * @param enterpriseId 企业id
     * @param businessIds 业务id列表
     * @param businessType 业务类型
     */
    public void deleteByBusinessIds(String enterpriseId, List<String> businessIds, String businessType, List<String> filterUserIds) {
        if (CollectionUtils.isEmpty(businessIds)) {
            return ;
        }
        tbMetaTableUserAuthMapper.deleteByBusinessIds(enterpriseId, businessIds, businessType, filterUserIds);
    }

    public void deleteByBusinessIds(String enterpriseId, List<String> businessIds, String businessType) {
        if (CollectionUtils.isEmpty(businessIds)) {
            return ;
        }
        tbMetaTableUserAuthMapper.deleteByBusinessIds(enterpriseId, businessIds, businessType, null);
    }

    public List<TbMetaTableUserAuthDO> getUserAuthMetaTableList(String enterpriseId, String userId) {
        if (StringUtils.isAnyBlank(enterpriseId, userId)) {
            return new ArrayList<>();
        }
        return tbMetaTableUserAuthMapper.getUserAuthMetaTableList(enterpriseId, userId);
    }

    public List<TbMetaTableUserAuthDO> getUserAuthViewMetaTableList(String enterpriseId, String userId) {
        if (StringUtils.isAnyBlank(enterpriseId, userId)) {
            return new ArrayList<>();
        }
        return tbMetaTableUserAuthMapper.getUserAuthViewMetaTableList(enterpriseId, userId);
    }

    public List<String> getEditAuthTableIds(String enterpriseId, String userId, List<Long> metaTableIds) {
        if (StringUtils.isAnyBlank(enterpriseId, userId) || CollectionUtils.isEmpty(metaTableIds)) {
            return new ArrayList<>();
        }
        return tbMetaTableUserAuthMapper.getEditAuthTableIds(enterpriseId, userId, metaTableIds);
    }

    public List<TbMetaTableUserAuthDO> getTableAuth(String enterpriseId, String userId, List<Long> metaTableIds) {
        if (StringUtils.isAnyBlank(enterpriseId, userId) || CollectionUtils.isEmpty(metaTableIds)) {
            return new ArrayList<>();
        }
        return tbMetaTableUserAuthMapper.getTableAuth(enterpriseId, userId, metaTableIds);
    }
}
