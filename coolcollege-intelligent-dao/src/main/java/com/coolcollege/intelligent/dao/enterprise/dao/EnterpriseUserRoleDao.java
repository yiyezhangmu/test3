package com.coolcollege.intelligent.dao.enterprise.dao;

import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.system.dao.SysRoleDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserRole;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserRoleDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangnan
 * @date 2022-04-14 18:38
 */
@Repository
public class EnterpriseUserRoleDao {

    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;

    /**
     * 根据角色id查询用户id列表
     * @param enterpriseId
     * @param roleIds
     * @return
     */
    public List<String> selectUserIdsByRoleIdList(String enterpriseId, List<Long> roleIds) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(roleIds)) {
            return Lists.newArrayList();
        }
        return enterpriseUserRoleMapper.selectUserIdsByRoleIdList(enterpriseId, roleIds);
    }

    public Map<String,List<Long>> selectByRoleIdList(String enterpriseId, List<Long> roleIds) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(roleIds)) {
            return new HashMap<>();
        }
        List<EnterpriseUserRoleDTO> byRoleIds = enterpriseUserRoleMapper.getByRoleIds(enterpriseId, roleIds);
        return  byRoleIds.stream().collect(Collectors.groupingBy(EnterpriseUserRoleDTO::getUserId,Collectors.mapping(EnterpriseUserRoleDTO::getRoleId,Collectors.toList())));
    }

    public List<String> selectUserIdsByRole(String enterpriseId, Role role) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(role)) {
            return Lists.newArrayList();
        }
        return enterpriseUserRoleMapper.selectUserIdsByRoleIdList(enterpriseId, Arrays.asList(Long.valueOf(role.getId())));
    }

    public List<String> getUserIdsByRoleIds(String enterpriseId, List<String> roleIds, List<String> userIds){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(roleIds) || CollectionUtils.isEmpty(userIds)){
            return Lists.newArrayList();
        }
        return enterpriseUserRoleMapper.getUserIdsByRoleIds(enterpriseId, roleIds, userIds);
    }

    public List<Long> getUserRoleIds(String enterpriseId, String userId){
        if(StringUtils.isAnyBlank(enterpriseId, userId)){
            return Lists.newArrayList();
        }
        return enterpriseUserRoleMapper.getUserRoleIds(enterpriseId, userId);
    }
    public Map<String,List<Long>> getUserRoleIds(String enterpriseId, List<String> userId){
        if(StringUtils.isAnyBlank(enterpriseId)||CollectionUtils.isEmpty(userId)){
            return new HashMap<>();
        }
        List<EnterpriseUserRoleDTO> enterpriseUserRoles = enterpriseUserRoleMapper.selectByUserIdsList(enterpriseId, userId);
        return enterpriseUserRoles.stream().collect(Collectors.groupingBy(EnterpriseUserRoleDTO::getUserId,Collectors.mapping(EnterpriseUserRoleDTO::getRoleId,Collectors.toList())));
    }

    public Boolean checkIsAdmin(String enterpriseId, String userId) {

        // 1.取出所有用户角色
        // 2.匹配是否有管理员角色
        List<SysRoleDO> sysRoleDOList = sysRoleMapper.listRoleByUserId(enterpriseId, userId);
        return ListUtils.emptyIfNull(sysRoleDOList)
                .stream()
                .anyMatch(role-> StringUtils.equals(Role.MASTER.getRoleEnum(),role.getRoleEnum()));
    }

    public List<String> getUserIdsByRoleIdList(String enterpriseId, List<Long> roleIdList) {
        return enterpriseUserRoleMapper.getUserIdsByRoleIdList(enterpriseId,roleIdList);
    }

    public void deleteByRoleId(String enterpriseId, String roleId){
        if(StringUtils.isAnyBlank(enterpriseId, roleId)){
            return;
        }
        enterpriseUserRoleMapper.deleteByRoleId(enterpriseId, roleId);
    }
}
