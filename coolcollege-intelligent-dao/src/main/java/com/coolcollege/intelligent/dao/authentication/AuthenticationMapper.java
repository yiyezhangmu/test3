package com.coolcollege.intelligent.dao.authentication;

import com.coolcollege.intelligent.model.authentication.AuthenticationDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 角色基本操作
 * @ClassName  AuthenticationMapper
 * @Description 角色基本操作
 * @author Aaron
 */
@Mapper
public interface AuthenticationMapper {


    /**
     * 角色查询
     * @Description 角色查询
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     */
    List<AuthenticationDO> queryRoles(@Param("enterpriseId") String enterpriseId, @Param("map") Map<String, Object> map);


    /**
     * 角色更新
     * @Description 角色更新
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     */
    int updateRoles(@Param("enterpriseId") String enterpriseId, @Param("map") Map<String, Object> map);

    /**
     * 部门查询
     * @Description 部门查询
     * @param departments
     * @param enterpriseId
     * @return map
     * @throws Exception
     */
    List<String> queryDepartment(@Param("enterpriseId") String enterpriseId,@Param("departments") List<Integer> departments);

    /**
     * 角色批量更新
     * @Description 角色批量更新
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     */
    int batchUpdateRoles(@Param("enterpriseId") String enterpriseId, @Param("map") Map<String, Object> map);

    /**
     * 员工查询
     * @Description 员工查询
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     */
    List<AuthenticationDO> queryEmployee(@Param("enterpriseId") String enterpriseId, @Param("map") Map<String, Object> map);

    /**
     * 角色批量更新非管理员
     * @Description 角色批量更新非管理员
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     */
    int batchUpdateUser(@Param("enterpriseId") String enterpriseId, @Param("map") Map<String, Object> map);
}
