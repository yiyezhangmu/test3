package com.coolcollege.intelligent.service.authentication.impl;

import com.alibaba.fastjson.JSONArray;
import com.coolcollege.intelligent.dao.authentication.AuthenticationMapper;
import com.coolcollege.intelligent.model.authentication.AuthenticationDO;
import com.coolcollege.intelligent.service.authentication.AuthenticationService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色基本操作
 * 
 * @ClassName AuthenticationServiceImpl
 * @Description 角色基本操作
 * @author Aaron
 */
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Resource
    private AuthenticationMapper authenticationMapper;

    /**
     * 角色查询
     * 
     * @Description 角色查询
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     */
    @Override
    public List<AuthenticationDO> queryRoles(String enterpriseId, Map<String, Object> map) {

        DataSourceHelper.changeToMy();
        List<AuthenticationDO> authenticationDOList = authenticationMapper.queryRoles(enterpriseId, map);
        // 部门名称查询
        if (CollectionUtils.isEmpty(authenticationDOList)) {
            return Lists.newArrayList();
        }
        authenticationDOList.iterator().forEachRemaining(p -> {
            List<String> departmentNames = authenticationMapper.queryDepartment(enterpriseId,
                JSONArray.parseArray(p.getDepartment(), Integer.class));
            String departmentName = String.join(",", departmentNames);
            p.setDepartment(departmentName);
        });
        return authenticationDOList;
    }

    @Override
    public String getDeptName(String enterpriseId, String department) {
        List<String> departmentNames =
            authenticationMapper.queryDepartment(enterpriseId, JSONArray.parseArray(department, Integer.class));
        String departmentName = String.join(",", departmentNames);
        if (StringUtils.isNotBlank(departmentName)) {
            departmentName = departmentName.replaceAll("\\[", "").replaceAll("]", "");
        }
        return departmentName;
    }

    /**
     * 角色更新
     * 
     * @Description 角色更新
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     */
    @Override
    public int updateRoles(String enterpriseId, Map<String, Object> map) {
        return authenticationMapper.updateRoles(enterpriseId, map);
    }

    /**
     * 角色批量更新
     * 
     * @Description 角色批量更新
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     */
    @Override
    public int batchUpdateRoles(String enterpriseId, Map<String, Object> map) {
        return authenticationMapper.batchUpdateRoles(enterpriseId, map);
    }

    /**
     * 员工查询
     * 
     * @Description 员工查询
     * @param map
     * @return map
     * @throws Exception
     */
    @Override
    public List<AuthenticationDO> queryEmployee(String enterpriseId, Map<String, Object> map) {
        List<AuthenticationDO> authenticationDOS = authenticationMapper.queryEmployee(enterpriseId, map);
        // 部门名称查询
        if (CollectionUtils.isEmpty(authenticationDOS)) {
            return Lists.newArrayList();
        }
        authenticationDOS.iterator().forEachRemaining(p -> {
            List<String> departmentNames = authenticationMapper.queryDepartment(enterpriseId,
                JSONArray.parseArray(p.getDepartment(), Integer.class));
            String departmentName = String.join(",", departmentNames);
            p.setDepartment(departmentName);
        });
        return authenticationDOS;
    }

    /**
     * 角色批量更新非管理员
     * 
     * @Description 角色批量更新非管理员
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     */
    @Override
    public int batchUpdateUser(String enterpriseId, Map<String, Object> map) {
        return authenticationMapper.batchUpdateUser(enterpriseId, map);
    }

}
