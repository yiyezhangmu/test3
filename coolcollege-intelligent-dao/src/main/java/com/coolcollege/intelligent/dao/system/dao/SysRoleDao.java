package com.coolcollege.intelligent.dao.system.dao;

import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.enterprise.dto.UserRoleDTO;
import com.coolcollege.intelligent.model.impoetexcel.dto.RoleImportDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangnan
 * @description:
 * @date 2022/4/18 10:57 PM
 */
@Repository
public class SysRoleDao {

    @Resource
    private SysRoleMapper sysRoleMapper;

    /**
     * 根据角色id列表查询
     * @param enterpriseId
     * @param roleIds
     * @return
     */
    public List<SysRoleDO> selectRoleByRoleIds(String enterpriseId, List<Long> roleIds) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(roleIds)) {
            return Lists.newArrayList();
        }
        return sysRoleMapper.getRoleByRoleIds(enterpriseId, roleIds);
    }

    /**
     * 根据角色id列表查询
     * @param enterpriseId
     * @param userIdList
     * @return
     */
    public List<UserRoleDTO> getUserRoleNameByUserIdList(String enterpriseId, List<String> userIdList) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(userIdList)) {
            return Lists.newArrayList();
        }
        return sysRoleMapper.getUserRoleNameByUserIdList(enterpriseId, userIdList);
    }

    public Map<String, Long> getRoleIdByThirdUniqueIds(String enterpriseId, List<String> thirdUniqueIds) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(thirdUniqueIds)) {
            return Maps.newHashMap();
        }
        List<SysRoleDO> roleList = sysRoleMapper.getRoleIdByThirdUniqueIds(enterpriseId, thirdUniqueIds);
        return roleList.stream().collect(Collectors.toMap(k->k.getThirdUniqueId(), v->v.getId(), (k1, k2) -> k1));
    }

    public void addRole(String enterpriseId, List<SysRoleDO> roleList){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(roleList)){
            return;
        }
        sysRoleMapper.addRole(enterpriseId, roleList);
    }

    public void insertOrUpdateRole(String enterpriseId, SysRoleDO role){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(role)){
            return;
        }
        sysRoleMapper.insertOrUpdateRole(enterpriseId, role);
    }

    public Map<Long, String> getRoleNameMap(String enterpriseId, List<Long> roleIds){
        List<SysRoleDO> sysRoleDOS = selectRoleByRoleIds(enterpriseId, roleIds);
        if(CollectionUtils.isEmpty(sysRoleDOS)){
            return Maps.newHashMap();
        }
        return sysRoleDOS.stream().collect(Collectors.toMap(k->k.getId(), v->v.getRoleName(), (k1, k2)->k1));
    }

    public Boolean deleteRoles(String enterpriseId, Long roleId){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(roleId)){
            return false;
        }
        return sysRoleMapper.deleteRoles(enterpriseId, roleId);
    }

    public List<Long> getNoUserSyncRoleIds(String enterpriseId){
        if(StringUtils.isBlank(enterpriseId)){
            return Lists.newArrayList();
        }
        return sysRoleMapper.getNoUserSyncRoleIds(enterpriseId);
    }

    public Integer deleteByRoleIds(String enterpriseId, List<Long> roleIds){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(roleIds)){
            return 0;
        }
        return sysRoleMapper.deleteByRoleIds(enterpriseId, roleIds);
    }

    public Integer getLastedPriority(String enterpriseId) {
        Integer lastedPriority = sysRoleMapper.getLastedPriority(enterpriseId);
        return lastedPriority == null ? 1 : lastedPriority;
    }

    public void updateThirdUniqueIds(String enterpriseId, List<RoleImportDTO> roleImportDTOList) {
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(roleImportDTOList)) {
            return;
        }
        sysRoleMapper.updateThirdUniqueIds(enterpriseId, roleImportDTOList);
    }

    public Integer deleteRoleWithoutUsers(String enterpriseId, Boolean isDeleteDefault) {
        return sysRoleMapper.deleteRoleWithoutUsers(enterpriseId, isDeleteDefault);
    }
}
