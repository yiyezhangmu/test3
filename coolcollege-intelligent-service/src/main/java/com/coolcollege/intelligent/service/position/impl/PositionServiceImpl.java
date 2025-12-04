package com.coolcollege.intelligent.service.position.impl;

import com.alibaba.fastjson.JSONArray;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.UserRoleDTO;
import com.coolcollege.intelligent.model.position.dto.PositionDTO;
import com.coolcollege.intelligent.model.position.queryDto.PositionQueryDTO;
import com.coolcollege.intelligent.model.system.dto.RoleDTO;
import com.coolcollege.intelligent.model.system.dto.RoleUserDTO;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.service.position.PositionService;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName PositionServiceImpl
 * @Description 岗位
 */
@Slf4j
@Service(value = "positionService")
public class PositionServiceImpl implements PositionService {

    @Autowired
    @Lazy
    private SysDepartmentService sysDepartmentService;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    public List<PositionDTO> getPositionList(String enterpriseId, PositionQueryDTO positionQueryDTO) {

        List<RoleDTO> sysRoleDos = sysRoleMapper.fuzzyRole(enterpriseId, positionQueryDTO.getKeyword(),null);
        if(CollectionUtils.isEmpty(sysRoleDos)){
            return Collections.EMPTY_LIST;
        }
        List<Long> roleIdList=  ListUtils.emptyIfNull(sysRoleDos)
                .stream()
                .map(RoleDTO::getId)
                .collect(Collectors.toList());

        List<RoleUserDTO> roleUserDTOList = sysRoleMapper.selectRoleUser(enterpriseId, roleIdList);
        Map<Long, Integer> userCountMap = ListUtils.emptyIfNull(roleUserDTOList).stream()
                .filter(a -> a.getRoleId() != null && a.getUserCount() != null)
                .collect(Collectors.toMap(RoleUserDTO::getRoleId, RoleUserDTO::getUserCount, (a, b) -> a));

        return ListUtils.emptyIfNull(sysRoleDos).stream()
                .map(data->{
                    PositionDTO positionDTO =new PositionDTO();
                    positionDTO.setName(data.getRoleName());
                    positionDTO.setId(data.getId());
                    positionDTO.setPositionId(data.getId().toString());
                    positionDTO.setSource(data.getSource());
                    if(MapUtils.isNotEmpty(userCountMap)){
                        Integer userCount = userCountMap.get(data.getId());
                        if(userCount==null){
                            positionDTO.setUserCount(0L);
                        }else {
                            positionDTO.setUserCount(userCount.longValue());
                        }
                    } else {
                        positionDTO.setUserCount(0L);
                    }
                    return positionDTO;
                }).collect(Collectors.toList());
    }

    @Override
    public List<RoleDTO> getPositionPageList(String enterpriseId, PositionQueryDTO positionQueryDTO) {
        PageHelper.startPage(positionQueryDTO.getPage_num(),positionQueryDTO.getPage_size());
        List<RoleDTO> sysRoleDos = sysRoleMapper.fuzzyRole(enterpriseId, positionQueryDTO.getKeyword(),null);
        if(CollectionUtils.isEmpty(sysRoleDos)){
            return sysRoleDos;
        }
        List<Long> roleIdList=  ListUtils.emptyIfNull(sysRoleDos)
                .stream()
                .map(RoleDTO::getId)
                .collect(Collectors.toList());

        List<RoleUserDTO> roleUserDTOList = sysRoleMapper.selectRoleUser(enterpriseId, roleIdList);
        Map<Long, Integer> userCountMap = ListUtils.emptyIfNull(roleUserDTOList).stream()
                .filter(a -> a.getRoleId() != null && a.getUserCount() != null)
                .collect(Collectors.toMap(RoleUserDTO::getRoleId, RoleUserDTO::getUserCount, (a, b) -> a));

         ListUtils.emptyIfNull(sysRoleDos).stream()
                .forEach(data->{
                    if(MapUtils.isNotEmpty(userCountMap)){
                        Integer userCount = userCountMap.get(data.getId());
                        if(userCount==null){
                            data.setUserCount(0);
                        }else {
                            data.setUserCount(userCount);
                        }
                    } else {
                        data.setUserCount(0);
                    }
                });
         return sysRoleDos;
    }


    @Override
    public void setOtherInfoForUsers(String enterpriseId, List<? extends EnterpriseUserDTO> users) {

        if (CollectionUtils.isNotEmpty(users)) {
            List<String> userIdList = users.stream()
                    .map(EnterpriseUserDTO::getUserId)
                    .collect(Collectors.toList());
            List<SysDepartmentDO> sysDepartmentDOList = sysDepartmentService.selectAllDepts(enterpriseId);
            List<UserRoleDTO> userRoleDOTList = sysRoleMapper.userAndRolesByUserId(enterpriseId, userIdList);
            Map<String, UserRoleDTO> userRoleMap = ListUtils.emptyIfNull(userRoleDOTList)
                    .stream()
                    .collect(Collectors.toMap(UserRoleDTO::getUserId, a -> a, (b, c) -> c));
            users.forEach(s -> {
                setDepNames(s, sysDepartmentDOList);
                setRole(s,userRoleMap );
            });
        }
    }


    /**
     * 设置部门集合
     *
     * @param userDTO
     * @param sysDepartmentDOS
     */
    private void setDepNames(EnterpriseUserDTO userDTO, List<SysDepartmentDO> sysDepartmentDOS) {
        String department = userDTO.getDepartment();
        if (StringUtils.isEmpty(department)) {
            return;
        }
        Set<Long> deps = Sets.newHashSet(JSONArray.parseArray(department, Long.class));
        userDTO.setDepartmentNames(sysDepartmentDOS.stream()
                .filter(s -> deps.contains(s.getId())).map(SysDepartmentDO::getName).collect(Collectors.toList()));
    }

    /**
     * 给用户赋角色信息
     */
    private void  setRole(EnterpriseUserDTO userDTO, Map<String, UserRoleDTO> userRoleMap ){

        String userId = userDTO.getUserId();
        UserRoleDTO userRoleDTO = userRoleMap.get(userId);
        if(userRoleDTO!=null){
            userDTO.setRole(userRoleDTO.getRoleName());
            String roleAuth = userRoleDTO.getRoleAuth();
            AuthRoleEnum byCode = AuthRoleEnum.getByCode(roleAuth);
            userDTO.setRoleAuthName(byCode.getMsg());
            userDTO.setRoleAuth(byCode.getCode());
        }
    }

}
