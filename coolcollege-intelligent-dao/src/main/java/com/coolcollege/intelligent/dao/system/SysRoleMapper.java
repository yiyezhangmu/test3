package com.coolcollege.intelligent.dao.system;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserRole;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.SelectUserRoleDTO;
import com.coolcollege.intelligent.model.enterprise.dto.UserRoleDTO;
import com.coolcollege.intelligent.model.enterprise.settings.B1RoleDTO;
import com.coolcollege.intelligent.model.impoetexcel.dto.RoleImportDTO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentUserRoleVO;
import com.coolcollege.intelligent.model.store.dto.StoreUserDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.system.VO.SysRoleVO;
import com.coolcollege.intelligent.model.system.dto.RoleDTO;
import com.coolcollege.intelligent.model.system.dto.RoleUserDTO;
import com.coolcollege.intelligent.model.system.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 角色
 *
 * @author shoul
 */
@Mapper
public interface SysRoleMapper {

    /**
     * 获取角色列表
     *
     * @param enterpriseId
     * @return
     */
    List<RoleDTO> fuzzyRole(@Param("enterpriseId") String enterpriseId,
                            @Param("roleName") String roleName,
                            @Param("positionType")String positionType);

    List<RoleDTO> fuzzyRoleBySource(@Param("enterpriseId") String enterpriseId,
                            @Param("roleName") String roleName,
                            @Param("source")String source);

    /**
     *
     * @param enterpriseId
     * @param roleIdList
     * @return
     */
    List<RoleUserDTO> selectRoleUser(@Param("enterpriseId") String enterpriseId,
                                     @Param("roleIdList") List<Long> roleIdList);

    List<RoleUserDTO> selectRoleUserByRoleIds(@Param("enterpriseId") String enterpriseId,
                                              @Param("roleIdList") List<Long> roleIdList);

    List<SysRoleVO> getRoleUserByRoleEnum(@Param("enterpriseId") String enterpriseId,
                                        @Param("roleEnum") String roleEnum,
                                        @Param("positionType") String positionType);

    List<SysRoleVO> getRoleUserByRoleAuth(@Param("enterpriseId") String enterpriseId,
                                          @Param("roleAuth") String roleAuth,
                                          @Param("positionType") String positionType);


    /**
     * 添加系统角色
     *
     * @param enterpriseId
     * @param sysRoleDO
     */
    void addSystemRole(@Param("eid") String enterpriseId,
                       @Param("sysRole") SysRoleDO sysRoleDO);

    /**
     * 根据用户角色查询角色下的用户人数
     *
     * @param roleId
     * @param enterpriseId
     * @return
     */
    Integer getPersonNumsByRoles(@Param("eid") String enterpriseId, @Param("roleId") Long roleId);


    /**
     * 修改角色信息
     * @param enterpriseId
     * @param roleDO
     * @return
     */
    int updateRole(@Param("eid") String enterpriseId,
                   @Param("roleDO")SysRoleDO roleDO);



    /**
     * 根据角色删除角色和权限之间的关系
     *
     * @param enterpriseId
     * @param roleId
     * @param menuIds
     * @return
     */
    Boolean deleteMenuByRoles(@Param("eip") String enterpriseId, @Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    Boolean batchDeleteMenuRole(@Param("eid") String enterpriseId, @Param("roleIdList") List<Long> roleIdList);
    /**
     * 删除角色信息
     *
     * @param enterpriseId
     * @param roleId
     * @return
     */
    Boolean deleteRoles(@Param("eip") String enterpriseId, @Param("roleId") Long roleId);

    Boolean batchDeleteRoles(@Param("eid") String enterpriseId, @Param("roleIdList") List<Long> roleIdList);


    /**
     * 动态给角色添加权限
     *
     * @param enterpriseId
     * @param roleId
     * @param menus
     * @return
     */
    Boolean addMenuByRole(@Param("eip") String enterpriseId, @Param("roleId") Long roleId,
                          @Param("menus") List<Long> menus,
                          @Param("platform")String platform);

    /**
     * 查询角色下的所有用户信息
     *
     * @param enterpriseId
     * @param roleIds
     * @param userName
     * @return
     */
    List<EnterpriseUserDTO> getPersonsByRole(@Param("eip") String enterpriseId, @Param("roleIds") List<Long> roleIds, @Param("userName") String userName);

    /**
     * 给角色批量添加人员
     *
     * @param roleId
     * @param userIds
     * @param enterpriseId
     * @return
     */
    Boolean addPersonToUser(@Param("eip") String enterpriseId, @Param("roleId") Long roleId, @Param("userIds") List<String> userIds, @Param("syncType") Integer syncType);

    /**
     * 查询所有用户和角色的关联关系
     *
     * @param enterpriseId
     * @return
     */
    List<EnterpriseUserRole> userAndRoles(@Param("eip") String enterpriseId);

    List<UserRoleDTO> userAndRolesByUserId(@Param("eip") String enterpriseId,
                                           @Param("userIdList") List<String> userIdList);

    List<StoreUserDTO> userAndPositionList(@Param("eid") String enterpriseId,
                                           @Param("userIdList") List<String> userIdList,
                                           @Param("userName") String userName,
                                           @Param("positionType") String positionType);

    List<StoreUserDTO> userAndPositionListDistinct(@Param("eid") String enterpriseId,
                                                   @Param("userIdList") List<String> userIdList,
                                                   @Param("userName") String userName,
                                                   @Param("positionType") String positionType);
    /**
     * 批量插入用户角色
     * @param enterpriseId
     * @param userRoles
     * @return
     */
    Boolean insertBatchUserRole(@Param("eid") String enterpriseId, @Param("userRoles") List<EnterpriseUserRole> userRoles);

    /**
     * 通过id查询所有用户和角色的关联关系
     *
     * @param enterpriseId
     * @param userId
     * @return
     */
    List<String> selectRolesByuserId(@Param("eip") String enterpriseId, @Param("userId") String userId);

    String getHighestPriorityRoleIdByUserId(@Param("eip") String enterpriseId, @Param("userId") String userId);

    /**
     * 根据角色名称查询是否存在
     *
     * @param enterpriseId
     * @param roleName
     * @return
     */
    List<SysRoleDO> getRolesByName(@Param("eip") String enterpriseId, @Param("roleName") String roleName);

    /**
     * 查询角色详情
     *
     * @param enterpriseId
     * @param roleId
     * @return
     */
    SysRoleDO getRole(@Param("eid") String enterpriseId, @Param("roleId") Long roleId);

    /**
     * roleEnum 不能为空
     * @param enterpriseId
     * @param roleEnum
     * @return
     */
    SysRoleDO getRoleByRoleEnum(@Param("eid") String enterpriseId, @Param("roleEnum") String roleEnum);

    /**
     * 批量查询角色详情
     * @param enterpriseId
     * @param roleIdList
     * @return
     */
    List<SysRoleDO> getRoleList(@Param("eid") String enterpriseId, @Param("roleIdList") List<Long> roleIdList);

    /**
     * 批量查询角色详情（包括钉钉角色）
     * @param enterpriseId
     * @param roleIdList
     * @return
     */
    List<SysRoleDO> getRoleByRoleIds(@Param("eid") String enterpriseId, @Param("roleIdList") List<Long> roleIdList);

    /**
     * 获取企业下的所有职位
     * @param enterpriseId
     * @return
     */
    List<SysRoleDO> getRoleByEid(@Param("eid") String enterpriseId);

    /**
     * 批量查询角色名称列表
     * @param enterpriseId
     * @param roleIdList
     * @return
     */
    List<String> getRoleNameList(@Param("eid") String enterpriseId, @Param("roleIdList") List<Long> roleIdList);

    List<UserDTO> getRoleUser(@Param("eid")String eid,
                              @Param("roleId")Long roleId,
                              @Param("userName")String userName,
                               @Param("active")Boolean active);


    /**
     * 删除角色下的用户信息
     *
     * @param enterpriseId
     * @param roleId
     * @param userIds
     * @return
     */
    Boolean deletePersonToUser(@Param("eip") String enterpriseId, @Param("roleId") Long roleId, @Param("userIds") List<String> userIds);


    /**
     *  查优先级最高的角色
     * @param enterpriseId
     * @param userId
     * @return
     */
    SysRoleDO getHighestPrioritySysRoleDoByUserId(@Param("eip") String enterpriseId, @Param("userId") String userId);


    /**
     * 根据用户id查询角色,包括钉钉同步角色
     * @param enterpriseId
     * @param userId
     * @throws
     * @return: com.coolcollege.intelligent.model.system.SysRoleDO
     * @Author: xugangkun
     * @Date: 2021/3/31 15:58
     */
    List<SysRoleDO> getSysRoleByUserId(@Param("eip") String enterpriseId, @Param("userId") String userId);

    List<SysRoleDO> listRoleByUserId(@Param("eip") String enterpriseId, @Param("userId") String userId);

    /**
   * 删除用户权限
   * @param enterpriseId
   * @param userIds
   * @return
   */
   Boolean deleteRolesByPerson(@Param("eip") String enterpriseId,@Param("userIds") List<String> userIds,
                               @Param("delAll") boolean delAll);
   
   
   Integer  countRoleByPerson(@Param("eip") String enterpriseId,@Param("userId") String userId);
   

    /**
     * 删除用户同步的角色关联关系
     * @param enterpriseId
     * @param userId
     * @return
     */
   Boolean deleteSyncRoleRelate(@Param("eid") String enterpriseId, @Param("userId") String userId);

    /**
     * 插入或更新角色
     * @param enterpriseId
     * @param role
     * @return
     */
   Boolean insertOrUpdateRole(@Param("eid") String enterpriseId, @Param("role") SysRoleDO role);

   List<Map<String, Object>> getSyncRoleList(@Param("eid") String enterpriseId);

    /**
     * 批量插入角色信息
     * @param enterpriseId
     * @param roles
     * @return
     */
   Boolean batchInsertOrUpdateRoles(@Param("eid") String enterpriseId, @Param("roles") List<SysRoleDO> roles);

    /**
     * 查询钉钉角色中的人员Id
     * @param eid
     * @param roleId
     * @return
     */
   List<String> selectUserByDingRole(@Param("eid") String eid,
                                     @Param("roleId") Long roleId);
    /**
     * 查询角色中的人员Id
     * @param eid
     * @param roleId
     * @return
     */
    Integer countUserByDingRole(@Param("eid") String eid,
                                      @Param("roleId") Long roleId);

    /**
     * 查询钉钉角色列表
     * @param eid
     * @return
     */
   List<B1RoleDTO> selectDingRoleCount(@Param("eid") String eid,
                                       @Param("roleName")String roleName);

    /**
     * 根据职位id获取人员列表
     * @param eid
     * @param positionIds
     * @return
     */
   List<String> getPositionUserIds(@Param("eid") String eid, @Param("positionIds") List<String> positionIds);

    /**
     * 根据人员id和职位id获取符合的用户id
     * @param eid
     * @param positionId
     * @param userIds
     * @return
     */
   List<UserDTO> getUserIdListByPositionId(@Param("eid") String eid, @Param("positionId") String positionId, @Param("userIds") List<String> userIds);

   List<EnterpriseUserRole> selectUserRoleBySourceAndUserId(@Param("eid") String eid,
                                                            @Param("source") String source ,
                                                            @Param("userIdList") List<String> userIdList);

   List<String> getUserIdListByRoleIdList(@Param("enterpriseId")String eid,@Param("roleIdList") List<Long> roleIdList);

    /**
     * 获取用户职位信息
     * @param eid
     * @param userId
     * @return
     */
    SelectUserRoleDTO selectUserRoleInfo(@Param("eid") String eid, @Param("userId") String userId);

    /**
     * 根据岗位来源获得角色列表
     * @param eid 企业id
     * @param source 岗位来源
     * @return: java.util.List<com.coolcollege.intelligent.model.system.SysRoleDO>
     * @Author: xugangkun
     * @Date: 2021/3/22 16:11
     */
    List<SysRoleDO> selectSysRoleBySource(@Param("eid") String eid, @Param("source") String source);

    /**
     * 根据岗位来源和角色名称获取角色
     * @param eid 企业id
     * @param roleName 角色名称
     * @param source 岗位来源
     * @return: java.util.List<com.coolcollege.intelligent.model.system.SysRoleDO>
     * @Author: xugangkun
     * @Date: 2021/3/22 16:11
     */
    List<SysRoleDO> selectByRoleNameAndSource(@Param("eid") String eid, @Param("roleName") String roleName, @Param("source") String source);

    /**
     * 获得最大的排序值
     * @param eid
     * @return: int
     * @Author: xugangkun
     * @Date: 2021/3/22 16:12
     */
    Integer getMaxSort(@Param("eid") String eid);

    /**
     * 获得最大的排序值
     * @param eid
     * @return: int
     * @Author: xugangkun
     * @Date: 2021/3/22 16:12
     */
    List<Integer> getNormalRoleMaxPriority(@Param("eid") String eid);

    /**
     * 根据id列表获得角色列表
     * @param eid
     * @param roleIdList
     * @throws
     * @return: java.util.List<com.coolcollege.intelligent.model.system.SysRoleDO>
     * @Author: xugangkun
     * @Date: 2021/3/29 15:44
     */
    List<SysRoleDO> selectRoleByIdList(@Param("eid") String eid, @Param("list") List<String> roleIdList);

    /**
     * 根据优先级获得角色列表
     * @param eid
     * @param priority
     * @return: java.util.List<com.coolcollege.intelligent.model.system.SysRoleDO>
     * @Author: xugangkun
     * @Date: 2021/3/29 15:44
     */
    List<SysRoleDO> selectRoleByPriority(@Param("eid") String eid, @Param("priority") Integer priority);

    List<SysRoleDO> getLimitRoleIds(@Param("eid") String eid, @Param("limit") int limit);

    SysRoleDO getAdminRole(@Param("eid") String eid);

    /**
     * 获取用户岗位信息
     * @param eid
     * @param userIds
     * @return
     */
    List<SelectComponentUserRoleVO> selectUserRoleByUserIds(@Param("eid") String eid, @Param("userIds") List<String> userIds);

    List<SysRoleDO> getRoleListPage(@Param("eid") String enterpriseId);


    /**
     * 获取用户岗位信息
     * @param eid
     * @param userIds
     * @return
     */
    List<UserRoleDTO> getUserRoleNameByUserIdList(@Param("eid") String eid, @Param("userIdList") List<String> userIdList);

    /**
     * 根据岗位来源和角色名称获取角色
     * @param eid 企业id
     * @param synDingRoleId 角色名称
     * @param source 岗位来源
     * @return: java.util.List<com.coolcollege.intelligent.model.system.SysRoleDO>
     * @Author: xugangkun
     * @Date: 2021/3/22 16:11
     */
    List<SysRoleDO> selectBySynDingRoleIdAndSource(@Param("eid") String eid, @Param("source") String source, @Param("synDingRoleId") Long synDingRoleId);

    /**
     * 根据name获取角色id
     * @param eid
     * @param roleNames
     * @return
     */
    List<SysRoleDO> getRoleIdByThirdUniqueIds(@Param("eid") String eid, @Param("thirdUniqueIds") List<String> thirdUniqueIds);

    Integer addRole(@Param("enterpriseId") String enterpriseId, @Param("roleList") List<SysRoleDO> roleList);

    /**
     * 获取没有用户的同步角色
     * @param enterpriseId
     * @return
     */
    List<Long> getNoUserSyncRoleIds(@Param("enterpriseId") String enterpriseId);

    /**
     * 角色删除
     * @param enterpriseId
     * @param roleIds
     * @return
     */
    Integer deleteByRoleIds(@Param("enterpriseId")String enterpriseId, @Param("roleIds")List<Long> roleIds);

    /**
     * 通过角色名称获取角色
     * @param enterpriseId
     * @param roleNameList
     * @return
     */
    List<SysRoleDO> getRoleByRoleNames(@Param("enterpriseId")String enterpriseId, @Param("roleNameList")List<String> roleNameList, @Param("source")String source);

    SysRoleDO getRoleIdByThirdUniqueId(@Param("enterpriseId") String enterpriseId, @Param("thirdUniqueId") String thirdUniqueId);

    Integer getLastedPriority(@Param("enterpriseId") String enterpriseId);

    void updateThirdUniqueIds(@Param("enterpriseId") String enterpriseId, @Param("roleImportDTOList") List<RoleImportDTO> roleImportDTOList);

    /**
     * 删除没用用户的职位
     * @param enterpriseId
     * @return
     */
    Integer deleteRoleWithoutUsers(@Param("enterpriseId") String enterpriseId, Boolean isDeleteDefault);
}
