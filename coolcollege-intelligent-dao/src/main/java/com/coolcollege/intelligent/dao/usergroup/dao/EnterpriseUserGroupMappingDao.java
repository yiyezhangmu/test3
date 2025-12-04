package com.coolcollege.intelligent.dao.usergroup.dao;

import com.coolcollege.intelligent.dao.usergroup.EnterpriseUserGroupMappingMapper;
import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupMappingDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户分组映射
 * @author wxp
 * @date 2022-12-29 14:56
 */
@Repository
public class EnterpriseUserGroupMappingDao {

    @Resource
    private EnterpriseUserGroupMappingMapper enterpriseUserGroupMappingMapper;

    public void deleteUserGroupMappingByGroupId(String enterpriseId, String groupId) {
        if (StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(groupId)) {
            return;
        }
        enterpriseUserGroupMappingMapper.deleteUserGroupMappingByGroupId(enterpriseId, groupId);
    }

    public void batchInsertMapping(String enterpriseId, List<String> userIdList, String groupId) {
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(userIdList) || StringUtils.isBlank(groupId)) {
            return;
        }
        enterpriseUserGroupMappingMapper.batchInsertMapping(enterpriseId, userIdList, groupId);
    }

    public void deleteMappingByGroupIdList(String enterpriseId, String groupId, List<String> userIdList) {
        if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(groupId)) {
            return;
        }
        enterpriseUserGroupMappingMapper.deleteMappingByGroupIdList(enterpriseId, groupId, userIdList);
    }

    public void deleteMappingByUserIdList(String enterpriseId, List<String> userIdList) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(userIdList)) {
            return;
        }
        enterpriseUserGroupMappingMapper.deleteMappingByUserIdList(enterpriseId, userIdList);
    }


    public List<EnterpriseUserGroupMappingDO> listByGroupIdList(String enterpriseId, List<String> groupIdList){
        return enterpriseUserGroupMappingMapper.listByGroupIdList(enterpriseId, groupIdList);
    }

    public List<String> getUserIdsByGroupIdList(String enterpriseId, List<String> groupIdList){
        return enterpriseUserGroupMappingMapper.getUserIdsByGroupIdList(enterpriseId, groupIdList);
    }

    public List<EnterpriseUserGroupMappingDO> listByUserIdList(String enterpriseId, List<String> userIdList){
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        return enterpriseUserGroupMappingMapper.listByUserIdList(enterpriseId, userIdList);
    }

    public void batchInsertOrUpdateUserGroupMapping(String enterpriseId, List<EnterpriseUserGroupMappingDO> userGroupMappingDOList) {
        if (CollectionUtils.isEmpty(userGroupMappingDOList)) {
            return;
        }
        enterpriseUserGroupMappingMapper.batchInsertOrUpdateUserGroupMapping(enterpriseId, userGroupMappingDOList);
    }

    public void deleteMappingByIdList(String enterpriseId, List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }
        enterpriseUserGroupMappingMapper.deleteMappingByIdList(enterpriseId, idList);
    }

}
