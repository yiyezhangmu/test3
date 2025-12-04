package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.facade.dto.openApi.OpenApiUserQueryDTO;
import com.coolcollege.intelligent.facade.dto.user.EnterpriseUserAllFacadeDTO;
import com.coolcollege.intelligent.model.department.dto.DeptUserTreeDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shoul
 */
@Mapper
public interface EnterpriseUserMapper {

    /**
     * 获取企业人员名字
     * @param enterpriseId
     * @param userId
     * @return
     */
    String selectActiveNameByUserId(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId);

    /**
     * 获取用户详情（平台库）
     * @param userId
     * @throws
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO
     * @Author: xugangkun
     * @Date: 2021/3/27 16:13
     */
    EnterpriseUserDO selectConfigUserByUserId(@Param("userId") String userId);

    /**
     * 获取用户详情（平台库）
     * @param userId
     * @throws
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO
     * @Author: xugangkun
     * @Date: 2021/3/27 16:13
     */
    EnterpriseUserDO selectConfigUserByUserIdIgnoreActive(@Param("userId") String userId);

    /**
     * 获取用户列表（平台库）
     * @param userIds
     * @throws
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO
     * @Author: xugangkun
     * @Date: 2021/3/27 16:13
     */
    List<EnterpriseUserDO> selectConfigUserByUserIds(@Param("userIds") String userIds);

    /**
     * 获取用户详情（平台库）
     * @param unionid
     * @throws
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO
     * @Author: xugangkun
     * @Date: 2021/6/21 10:13
     */
    EnterpriseUserDO selectConfigUserByUnionid(@Param("unionid") String unionid);

    String selectNameIgnoreActiveByUserId(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId);

    /**
     * 获取企业人员
     * @param enterpriseId
     * @param userId
     * @return
     */
    EnterpriseUserDO selectByUserId(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId);

    /**
     * 获取企业人员
     * @param enterpriseId
     * @param id
     * @return
     */
    EnterpriseUserDO selectById(@Param("enterpriseId") String enterpriseId, @Param("id") String id);

    /**
     * 获取企业人员(无视激活状态)
     * @param enterpriseId
     * @param userId
     * @return
     */
    EnterpriseUserDO selectByUserIdIgnoreActive(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId);

    /**
     * 批量插入平台人员
     * @param users
     */
    void batchInsertPlatformUsers(@Param("list") List<EnterpriseUserDO> users);

    /**
     * 批量插入或更新
     * @param users
     * @param eid
     */
    void batchInsertOrUpdate(@Param("list") List<EnterpriseUserDO> users, @Param("eid") String eid);

    /**
     * 保存企业用户
     * @param eid
     * @param entity
     * @return: void
     * @Author: xugangkun
     */
    void insertEnterpriseUser(@Param("eid") String eid, @Param("entity") EnterpriseUserDO entity);

    /**
     * 更新企业用户信息(企业库)
     * @Param:
     * @param eid
     * @param enterpriseUserDO
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/24 14:22
     */
    void updateEnterpriseUser(@Param("eid") String eid,@Param("enterpriseUserDO") EnterpriseUserDO enterpriseUserDO);
    /**
     * 覆盖更新企业用户信息(企业库)
     * @Param:
     * @param eid
     * @param enterpriseUserDO
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/24 14:22
     */
    void overwriteUpdateEnterpriseUser(@Param("eid") String eid,@Param("enterpriseUserDO") EnterpriseUserDO enterpriseUserDO);

    void clearThirdOaUniqueFlag(@Param("eid") String eid,@Param("userId") String userId);

    /**
     * 更新企业用户信息(平台)
     * @param enterpriseUserDO
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/24 14:22
     */
    void updateConfigEnterpriseUser(@Param("enterpriseUserDO") EnterpriseUserDO enterpriseUserDO);

    int getNumByJobnumber(@Param("eid") String eid, @Param("jobNumber") String jobNumber, @Param("userId") String excludeUserId);

    /**
     * 获取全部企业人员
     * @param ed
     * @return
     */
    List<EnterpriseUserDO> selectAllList(@Param("eid") String ed);

    /**
     * 获取全部企业已激活人员Id
     * @param eid
     * @return
     */
    List<String> selectAllUserIdsByActive(@Param("eid") String eid, @Param("active") Boolean active);

    List<String> selectPlatformAllUserIds();

    /**
     * 获取全部企业人员
     * @param ed
     * @return
     */
    List<EnterpriseUserDO> selectAllUser(@Param("eid") String ed);

    /**
     * 获取全部企业人员
     * @param ed
     * @return
     */
    List<EnterpriseUserDO> selectUserByEid(@Param("eid") String ed, @Param("regionId") Long regionId);

    /**
     * 获取部门下的人员列表
     * @param ed
     * @return
     */
    List<String> selectUserByRegionId(@Param("eid") String ed, @Param("regionId") String regionId);


    /**
     * 获取部门下的人员列表
     * @param ed
     * @return
     */
    List<String> selectUserByDepartmentId(@Param("eid") String ed, @Param("departmentId") String departmentId);

    /**
     * 获取全部企业人员id
     * @param eid
     * @return
     */
    List<String> selectAllUserIds(@Param("eid") String eid);

    /**
     * 按类型获取用户
     * @param eid
     * @param userType
     * @return
     */
    List<String> selectAllUserIdByUserType(@Param("eid") String eid, @Param("userType") Integer userType);

    /**
     * 获取指定节点的ID 包括下级用户ID
     * @param eid
     * @return  指定
     */
    List<SyncEnterpriseUserDTO> selectSpecifyNodeUserIds(@Param("eid") String eid, @Param("dingDeptId") String dingDeptId);

    /**
     * 常用人员列表
     * @param eid
     * @return
     */
    List<SelectUserDTO> selectRecentUserList(@Param("eid") String eid, @Param("userIds") List<String> userIds);

    /**
     * 根基钉钉corpId删除企业人员
     * @param eid
     * @param usersIds
     */
    void deleteEnterpriseUsersByDingUserIds(@Param("eid") String eid, @Param("list") List<String> usersIds);

    /**
     * 获取用户
     * @param eid
     * @param userIds
     * @return
     */
    List<String> selectUsersByDingUserIds(@Param("eid") String eid, @Param("userIds") List<String> userIds);


    /**
     * 获取用户
     * @param eid
     * @param userIds
     * @return
     */
    List<String> selectUserNamesByUserIds(@Param("eid") String eid, @Param("userIds") List<String> userIds);

    /**
     * 根据部门id获取用户
     * @param eid
     * @param deptId
     * @return
     */

    /**
     * 根据用户id数组获取用户
     * @param eid
     * @param userIds
     * @return
     */
    List<EnterpriseUserDO> selectUsersByUserIds(@Param("eid") String eid, @Param("userIds") List<String> userIds);

    /**
     * 根据用户id数组获取用户
     * @param eid
     * @param userIds
     * @return
     */
    List<EnterpriseUserDO> selectActiveUsersByUserIds(@Param("eid") String eid, @Param("userIds") List<String> userIds);

    /**
     * 根据用户id查询
     * @param eid
     * @param ids
     * @return
     */
    List<String> selectUserIdsByIds(@Param("eid") String eid, @Param("ids") List<String> ids);

    /**
     * 模糊查询名称在UserId列表中
     * @param eid
     * @param userIds
     * @param userName
     * @return
     */
    List<EnterpriseUserDO> fuzzyUsersByUserIdsAndUserName(@Param("eid") String eid,
                                                          @Param("userIds") List<String> userIds,
                                                          @Param("userName") String userName,
                                                          @Param("userStatus") Integer userStatus);

    /**
     * 根究唯一id获取用户(平台库)
     * @param unionidList
     * @return
     */
    List<EnterpriseUserDTO> selectUsersIdByUnionid(@Param("list") List<String> unionidList);

    /**
     *  根究userId获取用户(平台库)
     * @param userId
     * @return
     */
    EnterpriseUserDTO selectUsersIdByUserId(@Param("userId") String userId);


    /**
     *  更新用户mobile (平台库)
     * @param userId
     * @return
     */
    void updateUserMobile(@Param("userId") String userId,
                                            @Param("mobile") String mobile);

    /**
     * 分页查询
     * @param enterpriseId
     * @param deptIdList
     * @param keyword
     * @param userIds
     * @return
     */
    List<EnterpriseUserDTO> getDepUsersByPage(@Param("eid") String enterpriseId,
                                        @Param("deptIdList") List<String>  deptIdList,
                                        @Param("keyword") String keyword,
                                        @Param("userIds")List<String> userIds);


    /**
     * 获取用户部门
     * @param enterpriseId
     * @param userType
     * @return
     */
    List<EnterpriseUserDO> getUsersDepartments(@Param("eid") String enterpriseId, @Param("userType") String userType);

    /**
     * 获取用户详情
     * @param enterpriseId
     * @param userId
     * @return
     */
    EnterpriseUserDTO getUserDetail(@Param("eid") String enterpriseId, @Param("userId") String userId);

    /**
     * 批量插入用户电话
     * @param enterpriseId
     * @param users
     * @return
     */
    Long batchUpdateUserMobile(@Param("eid") String enterpriseId, @Param("list") List<EnterpriseUserDO> users);

    /**
     * 批量更新导入人员
     * @param enterpriseId
     * @param users
     * @return
     */
    Integer batchUpdateImportUser(@Param("eid") String enterpriseId, @Param("list") List<EnterpriseUserDO> users);

    /**
     * 非覆盖批量更新导入人员
     * @param enterpriseId
     * @param users
     * @return
     */
    Integer nonOverwriteUpdateImportUser(@Param("eid") String enterpriseId, @Param("list") List<EnterpriseUserDO> users);

    /**
     * 获取地址
     * @param enterpriseId
     * @return
     */
    List<AddressBookUserDTO> getAddressBookUsers(@Param("eid") String enterpriseId);


    /**
     * 根据管理员获取用户
     * @param enterpriseId
     * @param isAdmin
     * @return
     */
    List<EnterpriseUserDO>  getUserByAdmin(@Param("eip")String enterpriseId,@Param("isAdmin") Boolean isAdmin);

    /**
     * 批量删除用户
     * @param enterpriseId
     * @param userIds
     * @return
     */
    Long batchDeleteUserIds(@Param("eid")String enterpriseId,@Param("list")List<String> userIds);

    /**
     * 批量删除用户（平台库）
     * @param userIds
     * @return: java.lang.Long
     * @Author: xugangkun
     * @Date: 2021/3/25 17:25
     */
    Long batchDeleteUserIdsConfig(@Param("list")List<String> userIds);


    List<DeptUserTreeDTO> getDeptUser(@Param("eid")String enterpriseId, @Param("userType") String userType);

    List<EnterpriseUserDTO> fuzzyUsersByDepartment(@Param("eid") String eid,
                                                   @Param("deptId") String deptId,
                                                   @Param("roleId") Long roleId,
                                                   @Param("orderBy") String orderBy,
                                                   @Param("orderRule") String orderRule,
                                                   @Param("userName") String userName,
                                                   @Param("jobNumber") String jobnumber,
                                                   @Param("userStatus") Integer userStatus,
                                                   @Param("userIdList") List<String> userIdList,
                                                   @Param("regionId") String regionId,
                                                   @Param("mobile") String mobile,
                                                   @Param("userType") Integer userType);

    Long fuzzyUsersByDepartmentCOUNT(@Param("eid") String eid,
                                                   @Param("deptId") String deptId,
                                                   @Param("roleId") Long roleId,
                                                   @Param("orderBy") String orderBy,
                                                   @Param("orderRule") String orderRule,
                                                   @Param("userName") String userName,
                                                   @Param("jobNumber") String jobnumber,
                                                   @Param("userStatus") Integer userStatus,
                                                   @Param("userIdList") List<String> userIdList,
                                                   @Param("regionId") String regionId,
                                                   @Param("mobile") String mobile,
                                                   @Param("userType") Integer userType);


    List<EnterpriseUserDTO> fuzzyUsersByNotRole(@Param("eid") String eid,
                                                @Param("deptId") String deptId,
                                                @Param("orderBy") String orderBy,
                                                @Param("orderRule") String orderRule,
                                                @Param("userName") String userName,
                                                @Param("jobNumber") String jobnumber,
                                                @Param("userStatus") Integer userStatus,
                                                @Param("userIdList") List<String> userIdList,
                                                @Param("regionId") String regionId,
                                                @Param("mobile") String mobile,
                                                @Param("userType") Integer userType);

    Long fuzzyUsersByNotRoleCOUNT(@Param("eid") String eid,
                                                @Param("deptId") String deptId,
                                                @Param("orderBy") String orderBy,
                                                @Param("orderRule") String orderRule,
                                                @Param("userName") String userName,
                                                @Param("jobNumber") String jobnumber,
                                                @Param("userStatus") Integer userStatus,
                                                @Param("userIdList") List<String> userIdList,
                                                @Param("regionId") String regionId,
                                                @Param("mobile") String mobile,
                                                @Param("userType") Integer userType);

    int updateUserToMainAdmin(@Param("eid")String eid, @Param("userIds") List<String> userIds);

    List<EnterpriseUserDO> getMainAdmin(@Param("eid")String eid);

    List<EnterpriseUserDTO> getUserDetailList(@Param("eid")String eid, @Param("userIdList") List<String> useIdList);

    /**
     * 获取部门-人员映射
     * @param eid
     * @return
     */
//    List<DeptUserDTO> getDeptUserList(@Param("eid")String eid, @Param("userType") String userType);

    List<String> selectUserAll(@Param("eid")String eid);

    /**
     * 获取所有的用户
     * @param eid
     * @return
     */
    List<EnterpriseUserDO> selectEnterpriseUserAll(@Param("eid")String eid);
    /**
     * 计算全企业人数
     * @param eid
     * @return
     */
    Integer countUserAll(@Param("eid")String eid);

    Integer countUserByNotRole(@Param("eid") String eid,
                         @Param("deptId") String deptId,
                         @Param("userName") String userName,
                         @Param("jobNumber") String jobnumber,
                         @Param("userStatus") Integer userStatus,
                         @Param("regionId") String regionId);

    Integer countUserByRole(@Param("eid") String eid,
                               @Param("deptId") String deptId,
                              @Param("roleId") Long roleId,
                               @Param("userName") String userName,
                               @Param("jobNumber") String jobnumber,
                               @Param("userStatus") Integer userStatus,
                               @Param("regionId") String regionId);
    /**
     * 获取用户详情
     * @param enterpriseId
     * @param unionId
     * @return
     */
    EnterpriseUserDTO getUserDetailByUnionId(@Param("eid") String enterpriseId, @Param("unionId") String unionId);

    /**
     * 更新人员主管理员状态
     * @param eid
     */
    void updateUserMainAdmin(@Param("eid") String eid, @Param("userId") String userId);

    List<String> selectUserIdsByUserList(@Param("eid") String enterpriseId, @Param("userIdList") List<String> userIdList);

    List<EnterpriseUserDO> selectUserRegionIdsByUserList(@Param("eid") String enterpriseId, @Param("userIdList") List<String> userIdList, @Param("filterActive") Boolean filterActive);

    /**
     * 根据手机号获取平台用户
     * @param mobile
     * @return
     */
    List<EnterpriseUserDO> getPlatformUserByMobile(@Param("mobile")String mobile);

    EnterpriseUserDO getPlatformUserByUnionid(@Param("unionid")String unionid);

    /**
     * 获取企业库用户信息
     * @param enterpriseId
     * @param unionid
     * @return
     */
    EnterpriseUserDO selectByUnionid(@Param("enterpriseId") String enterpriseId, @Param("unionid") String unionid);

    List<EnterpriseUserDO> selectByUnionids(@Param("enterpriseId") String enterpriseId, @Param("unionids") List<String> unionids);


    Integer updateConfigEnterpriseUserByUnionId(@Param("enterpriseUserDO") EnterpriseUserDO enterpriseUserDO);

    Integer updateConfigEnterpriseUserList(@Param("enterpriseUserList") List<EnterpriseUserDO> enterpriseUserList);

    /**
     * 获取用户
     * @param enterpriseId
     * @param mobile
     * @return
     */
    EnterpriseUserDO getEnterpriseUserByMobile(@Param("enterpriseId") String enterpriseId, @Param("mobile") String mobile, @Param("excludeUnionid")String excludeUnionid);

    EnterpriseUserDO getUserByMobile(@Param("enterpriseId") String enterpriseId, @Param("mobile") String mobile, @Param("excludeUserId")String excludeUserId);

    EnterpriseUserDO getUserByThirdOaUniqueFlag(@Param("enterpriseId") String enterpriseId, @Param("thirdOaUniqueFlag") String thirdOaUniqueFlag, @Param("excludeUserId")String excludeUserId);


    EnterpriseUserDO getEnterpriseUserByThirdOaUniqueFlag(@Param("enterpriseId") String enterpriseId, @Param("thirdOaUniqueFlag") String thirdOaUniqueFlag, @Param("excludeUnionid")String excludeUnionid);

    /**
     * 批量更新用户状态
     * @param enterpriseId
     * @param unionids
     * @return
     */
    Integer batchUpdateUserStatusByUnionid(@Param("enterpriseId")String enterpriseId, @Param("unionids")List<String> unionids, @Param("userStatus")Integer userStatus);

    /**
     * 根据手机号修改密码
     * @param mobile
     * @param password
     * @return
     */
    Integer modifyPasswordByMobile(@Param("mobile")String mobile, @Param("password")String password);

    /**
     * 获取企业用户状态
     * @param enterpriseId
     * @return
     */
    List<EnterpriseUserDO> getEnterpriseUserStatus(@Param("enterpriseId")String enterpriseId);

    /**
     * 根据用户类型和手机号获得平台用户
     * @param appType
     * @param mobile
     * @author: xugangkun
     * @return com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO
     * @date: 2021/7/21 16:24
     */
    EnterpriseUserDO selectConfigUserByAppTypeAndMobile(@Param("appType") String appType, @Param("mobile") String mobile);

    /**
     * 获取用户数量
     * @param eid
     * @return
     */
    Integer getActiveUserCount(@Param("eid")String eid);

    /**
     * 获取用户数量通过状态
     * @param eid
     * @return
     */
    Integer getUserCountByActive(@Param("eid")String eid, @Param("active")Boolean active);

    /**
     * 根据工号获取企业库用户
     * @author chenyupeng
     * @date 2021/8/16
     * @param enterpriseId
     * @param jobnumber
     * @return com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO
     */
    EnterpriseUserDO selectByJobnumber(@Param("enterpriseId") String enterpriseId, @Param("jobnumber") String jobnumber);

    EnterpriseUserDO selectByThirdOaUniqueFlag(@Param("enterpriseId") String enterpriseId, @Param("thirdOaUniqueFlag") String thirdOaUniqueFlag);

    /**
     * 企业库根据unionid获取userid
     * @param enterpriseId
     * @param unionids
     * @return
     */
    List<String> getUserIdsByUnionIds(@Param("enterpriseId") String enterpriseId, @Param("unionids")List<String> unionids);

    /**
     * 根据关键字搜索人员信息
     * @param eid
     * @param keyword
     * @param userStatus
     * @return
     */
    List<EnterpriseUserDO> selectUserByKeyword(@Param("eid") String eid, @Param("keyword") String keyword,
                                               @Param("userStatus") Integer userStatus,
                                               @Param("userIdList") List<String> userIdList,
                                               @Param("active") Boolean active);

    /**
     * 筛选userIds里的正常用户
     * @param eid
     * @param userIds
     * @param userStatus
     * @return
     */
    List<String> selectByUserIdsAndStatus(@Param("eid") String eid, @Param("userIds") List<String> userIds,
                                          @Param("userStatus") Integer userStatus);

    /**
     * 获取用户信息通过状态和userIDs
     * @param eid
     * @param userIds
     * @param userStatus
     * @return
     */
    List<EnterpriseUserDO> selectUsersByStatusAndUserIds(@Param("eid") String eid, @Param("userIds") List<String> userIds,
                                                         @Param("userStatus") Integer userStatus,
                                                         @Param("active") Boolean active);

    List<EnterpriseUserDO> selectUsersByStatusAndUserIdsForMyj(@Param("eid") String eid, @Param("userIds") List<String> userIds,
                                                         @Param("userStatus") Integer userStatus,
                                                         @Param("active") Boolean active);


    List<EnterpriseUserDO> listByUserIdIgnoreActive(@Param("enterpriseId") String enterpriseId, @Param("userIds") List<String> userIds);

    /**
     * 批量更新用户部门集合
     * @param eid
     * @param oldFullRegionPath
     * @param newFullRegionPath
     * @return
     */
    Boolean batchUpdateUserRegionIds(@Param("eid") String eid,
                                     @Param("oldFullRegionPath") String oldFullRegionPath,
                                     @Param("newFullRegionPath") String newFullRegionPath,
                                     @Param("keyNode") String keyNode);

    /**
     * 批量更新不同用户 不同的userRegionids
     * @param eid
     * @param enterpriseUserDOList
     * @return
     */
    Boolean batchUpdateDiffUserDiffRegionIds(@Param("eid") String eid,
                                             @Param("list") List<EnterpriseUserDO> enterpriseUserDOList);

    Integer countUserByGroupId(@Param("eid") String eid,
                               @Param("groupId") String groupId);

    // 查询分组下的用户
    List<EnterpriseUserDTO> listUserByGroupId(@Param("eid") String eid,
                                                   @Param("groupId") String groupId,
                                                   @Param("userName") String userName,
                                                   @Param("userIdList") List<String> userIdList);

    List<String> getUserIdsByRegionIdList(@Param("eid") String eid, @Param("regionIdList") List<String> regionIdList);

    List<UserGroupByRegionDTO> listUserIdByRegionIdList(@Param("eid") String eid, @Param("regionIdList") List<String> regionIdList);

    List<String> listUserIdByDepartmentIdList(@Param("eid") String eid, @Param("departmentIdList") List<String> departmentIdList);


    List<EnterpriseUserSingleDTO> usersByUserIdList(@Param("eid") String eid, @Param("userIds") List<String> userIds);


    List<EnterpriseUserDO> selectIgnoreDeletedUsersByUserIds(@Param("eid") String eid, @Param("userIds") List<String> userIds);


    List<EnterpriseUserAllFacadeDTO> getUsersByPage(@Param("eid") String enterpriseId,
                                                    @Param("name") String name);
    /**
     * 获取数量
     * @param eid
     * @param userIds
     * @param regionIds
     * @return
     */
    Integer getUserCountByUserIdOrRegionIds(@Param("eid") String eid, @Param("userIds") List<String> userIds, @Param("regionIds") List<String> regionIds);



    List<EnterpriseUserDO> getUsersByRoleIds(@Param("enterpriseId") String enterpriseId,
                                             @Param("roleIdsByComp") List<Long> roleIdsByComp);

    List<String> selectUserIdsByUserIdOrJobNumber(@Param("eid") String eid,
                                                  @Param("importType") String importType,
                                                  @Param("userId") String userId,
                                                  @Param("jobNumber") String jobNumber);

    /**
     * 用于DEMO企业卡片发送，其他用途禁止使用
     * @param eid
     * @return
     */
    List<EnterpriseUserDO> getAllUser(@Param("eid") String eid);

    List<EnterpriseUserDO> selectUserByRegionPaths(@Param("eid")String enterpriseId, @Param("regionPaths")List<String> regionPath);

    Integer batchInsertUsers(@Param("eid")String enterpriseId, @Param("list") List<EnterpriseUserDO> users);

    String selectMaxNum(@Param("eid") String eid);


    EnterpriseUserDO getUserInfoByMobile(@Param("eid")String enterpriseId, @Param("mobile")String mobile);

    /**
     * 用户是否设置密码
     * @param unionid 唯一标识
     * @return 是否设置了密码
     */
    Boolean hasPassword(@Param("unionid") String unionid);

    /**
     * 列表查询
     * @param enterpriseId 企业id
     * @param param 用户查询DTO
     * @return 实体列表
     */
    List<EnterpriseUserDO> getList(@Param("enterpriseId") String enterpriseId, @Param("param") OpenApiUserQueryDTO param);
}
