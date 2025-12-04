package com.coolcollege.intelligent.service.authentication;

import com.coolcollege.intelligent.model.authentication.AuthenticationDO;

import java.util.List;
import java.util.Map;

/**
 * 角色基本操作
 * 
 * @ClassName AuthenticationService
 * @Description 角色基本操作
 * @author Aaron
 */
public interface AuthenticationService {

    /**
     * 角色查询
     * 
     * @Description 角色查询
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     */
    List<AuthenticationDO> queryRoles(String enterpriseId, Map<String, Object> map);

    /**
     * @Title: getDeptName @Description: 根据部门id获取部门名称 @param enterpriseId @param department 格式为[1,3123,22112] @return
     * java.lang.String @throws
     */
    String getDeptName(String enterpriseId, String department);

    /**
     * 角色更新
     * 
     * @Description 角色更新
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     */
    int updateRoles(String enterpriseId, Map<String, Object> map);

    /**
     * 角色批量更新
     * 
     * @Description 角色批量更新
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     */
    int batchUpdateRoles(String enterpriseId, Map<String, Object> map);

    /**
     * 员工查询
     * 
     * @Description 员工查询
     * @param map
     * @return map
     * @throws Exception
     */
    List<AuthenticationDO> queryEmployee(String enterpriseId, Map<String, Object> map);

    /**
     * 角色批量更新非管理员
     * 
     * @Description 角色批量更新非管理员
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     */
    int batchUpdateUser(String enterpriseId, Map<String, Object> map);
}
