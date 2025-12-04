package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/09/17
 */
@Mapper
public interface EnterpriseUserDepartmentMapper {

    /**
     * 保存
     * @param eid
     * @param entity
     * @return: void
     * @Author: xugangkun
     */
    void save(@Param("eid")String eid, @Param("entity") EnterpriseUserDepartmentDO entity);

    int batchInsert(@Param("eid")String eid, @Param("deptUsers") List<EnterpriseUserDepartmentDO> deptUsers);

    int deleteMapping(@Param("eid")String eid, @Param("userIds") List<String> userIds);

    List<EnterpriseUserDepartmentDO> selectEnterpriseUserDepartmentByUserList(@Param("eid")String eid,
                                                                            @Param("list")List<String> userId);

    List<EnterpriseUserDepartmentDO> selectEnterpriseUserDepartmentByDeptList(@Param("eid")String eid,
                                                                                @Param("list")List<String> deptIdList);

    /**
     * 获取人员与部门、部门与父部门的列表
     * @param eid
     * @param userIds
     * @return
     */
    List<ManualDeptUserDTO> getDeptParentAndIdForUser(@Param("eid") String eid, @Param("userIds") List<String> userIds,
                                                      @Param("deptIds") List<String> deptIds);

    /**
     * 获取人员
     * @param eid
     * @param userIds
     * @return
     */
    UserDeptRoleDTO getUserDeptAndRole(@Param("eid") String eid, @Param("userIds") List<String> userIds);

    /**
     * 根据用户id获取用户和对应部门列表
     * @param eid
     * @param userId
     * @return
     */
    UserDeptDTO getUserDeptByUserId(@Param("eid") String eid, @Param("userId") String userId);

    /**
     * 根据userId获得用户部门关联表
     * @param eid
     * @param userId
     * @return: java.util.List<com.coolcollege.intelligent.model.enterprise.EnterpriseUserDepartmentDO>
     * @Author: xugangkun
     * @Date: 2021/3/26 10:03
     */
    List<EnterpriseUserDepartmentDO> selectUserDeptByUserId(@Param("eid")String eid, @Param("userId")String userId);

    /**
     * 获取用户部门权限
     * @param eid
     * @param userId
     * @return
     */
    List<EnterpriseUserDepartmentDO> selectUserDeptAuthByUserId(@Param("eid")String eid, @Param("userId")String userId);

    /**
     * 根据主键列表删除映关系
     * @param eid
     * @param ids
     * @throws
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/25 14:10
     */
    void deleteByIdList(@Param("eid") String eid, @Param("list") List<Integer> ids);

    /**
     * 根据userId获得主键列表
     * @param eid
     * @param userId
     * @return
     */
    List<Integer> getIdsByUserId(@Param("eid") String eid, @Param("userId") String userId);


    List<Integer> getIdsByUserIds(@Param("eid") String eid, @Param("userIds") List<String> userIds);

    /**
     * 部门统计人数
     * @param eid
     * @param departmentIds
     * @return
     */
    List<EnterpriseUserDepartmentDO> getUserDepartments(@Param("eid") String eid, @Param("departmentIds") List<String> departmentIds);

    /**
     *  根据部门id获取部门和人员的关系列表
     * @param eid
     * @param deptId
     * @return
     */
    List<EnterpriseUserDepartmentDO> getUserIdsByDeptId(@Param("eid") String eid, @Param("deptId") String deptId);

    /**
     * 删除用户部门权限数据
     * @param eid
     * @param userIds
     * @return
     */
    Integer deleteUserDepartmentAuth(@Param("eid")String eid, @Param("userIds") List<String> userIds);

    /**
     * 根据用户的id 获取映射关系部门的id
     * @param eid
     * @param userIds
     * @return
     */
    List<EnterpriseUserDepartmentDO> selectDeptIdByUserIds(@Param("eid")String eid, @Param("userIds") List<String> userIds);

    /**
     * 根据用户的id 获取映射关系权限部门的id
     * @param eid
     * @param userIds
     * @return
     */
    List<EnterpriseUserDepartmentDO> selectDeptAuthByUserIds(@Param("eid")String eid, @Param("userIds") List<String> userIds);
}
