package com.coolcollege.intelligent.dao.usergroup.dao;

import com.coolcollege.intelligent.dao.usergroup.EnterpriseUserGroupMapper;
import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户分组
 * @author wxp
 * @date 2022-12-29 14:56
 */
@Repository
public class EnterpriseUserGroupDao {

    @Resource
    private EnterpriseUserGroupMapper enterpriseUserGroupMapper;

    public int insertSelective(EnterpriseUserGroupDO record, String enterpriseId){
        return enterpriseUserGroupMapper.insertSelective(record,enterpriseId);
    }

    public int updateByPrimaryKeySelective(EnterpriseUserGroupDO record, String enterpriseId){
        return enterpriseUserGroupMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    public int updateByGroupId(EnterpriseUserGroupDO record, String enterpriseId){
        return enterpriseUserGroupMapper.updateByGroupId(record,enterpriseId);
    }

    public int countByGroupName(String enterpriseId, String groupName, String groupId){
        return  enterpriseUserGroupMapper.countByGroupName(enterpriseId, groupName, groupId);
    }

    public void deleteByGroupIdList(String enterpriseId, List<String> groupIdList) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(groupIdList)) {
            return;
        }
        enterpriseUserGroupMapper.deleteByGroupIdList(enterpriseId, groupIdList);
    }

    public List<EnterpriseUserGroupDO> listUserGroup(String enterpriseId, String groupName){
        return  enterpriseUserGroupMapper.listUserGroup(enterpriseId, groupName);
    }

    public EnterpriseUserGroupDO getByGroupId(String enterpriseId, String groupId){
        return  enterpriseUserGroupMapper.getByGroupId(enterpriseId, groupId);
    }

    public List<EnterpriseUserGroupDO> listByGroupIdList(String enterpriseId, List<String> groupIdList){
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(groupIdList)) {
            return new ArrayList<>();
        }
        return  enterpriseUserGroupMapper.listByGroupIdList(enterpriseId, groupIdList);
    }


}
