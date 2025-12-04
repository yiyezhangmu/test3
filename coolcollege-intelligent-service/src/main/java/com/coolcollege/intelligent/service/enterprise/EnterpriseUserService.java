package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.common.enums.user.UserTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.*;
import com.coolcollege.intelligent.facade.dto.openApi.vo.OpenUserVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.UserInfoVO;
import com.coolcollege.intelligent.model.department.dto.DepartmentQueryDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.*;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseDetailUserVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseUserBossVO;
import com.coolcollege.intelligent.model.export.request.UserInfoExportRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.region.dto.AuthRegionStoreUserDTO;
import com.coolcollege.intelligent.model.user.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.requestBody.user.EnterpriseUserRequestBody;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EnterpriseUserService {

    /**
     * 获取用户详情（平台库）
     * @param userId
     * @throws
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO
     * @Author: xugangkun
     * @Date: 2021/3/27 16:13
     */
    EnterpriseUserDO selectConfigUserByUserId(String userId);

    /**
     * 获取用户详情（平台库）
     * @param unionid
     * @throws
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO
     * @Author: xugangkun
     * @Date: 2021/6/21 10:13
     */
    EnterpriseUserDO selectConfigUserByUnionid(String unionid);

    void setUsersInfo(String eid, List<EnterpriseUserRequest> users, Set<String> deptIdSet, Map<String, String> deptIdMap);

    /**
     * 批量插入平台用户
     *
     * @param deptUsers
     */
    void batchInsertPlatformUsers(List<EnterpriseUserDO> deptUsers);

    /**
     * 批量插入企业用户
     *
     * @param deptUsers
     * @param eid
     */
    void batchInsertOrUpdate(List<EnterpriseUserDO> deptUsers, String eid);

    /**
     * 保存企业用户
     * @param eid
     * @param entity
     * @return: void
     * @Author: xugangkun
     */
    Boolean insertEnterpriseUser(String eid, EnterpriseUserDO entity);

    /**
     * 查询企业用户
     *
     * @param eid
     * @return
     */
    List<EnterpriseUserDO> selectAllList(String eid);

    /**
     * 获得该企业所有用户(包括未激活)
     * @param eid
     * @return: java.util.List<com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO>
     * @Author: xugangkun
     * @Date: 2021/3/24 21:05
     */
    List<EnterpriseUserDO> selectAllUser(String eid);

    /**
     * 获得该企业所有用户的id
     * @param eid
     * @return: java.util.List<com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO>
     * @Author: xugangkun
     * @Date: 2021/3/24 21:05
     */
    List<String> selectAllUserId(String eid);

    /**
     * 根据用户类型获取所有用户
     * @param eid
     * @param userType
     * @return
     */
    List<String> selectAllUserIdByUserType(String eid, UserTypeEnum userType);

    /**
     * 获得该企业指定部门下所有用户的id
     * @param eid
     * @param dingDeptId
     * @return
     */
    List<SyncEnterpriseUserDTO> selectSpecifyNodeUserIds(String eid, String dingDeptId);

    /**
     * 删除企业用户
     *
     * @param userIds
     * @param eid
     */
    void deleteEnterpriseByUserIds(List<String> userIds, String eid);


    List<EnterpriseUserDO> selectUsersByUserIds(String eid, List<String> userIds);

    Object getDepUsersByPage(String enterpriseId, DepartmentQueryDTO departmentQueryDTO);

    /**
     * 修改用户手机或者人脸
     *
     * @param enterpriseId
     * @param users
     * @return
     */
    void batchUpdateUserMobile(String enterpriseId, List<EnterpriseUserDO> users);

    /**
     * 下载通讯录
     *
     * @param enterpriseId
     * @return
     */
    List<AddressBookUserDTO> getAddressBookUsers(String enterpriseId);

    /**
     * 导入通讯录
     *
     * @param enterpriseId
     * @param dataMapList
     * @param fileName
     * @return
     */
    Object importAddressBook(String enterpriseId, List<Map<String, Object>> dataMapList, String fileName);

    /**
     * 查询用户列表
     *
     * @param enterpriseId
     * @param enterpriseUserQueryDTO
     * @return
     */
    Object getUserList(String enterpriseId, EnterpriseUserQueryDTO enterpriseUserQueryDTO);

    /**
     * 设置用户角色信息
     * @param enterpriseId
     * @param enterpriseUserList
     * @author: xugangkun
     * @return void
     * @date: 2021/7/23 11:38
     */
    List<String> initUserRole(String enterpriseId, List<EnterpriseUserDTO> enterpriseUserList);

    /**
     *
     * @param enterpriseId
     * @param userName
     * @param deptId
     * @param orderBy
     * @param orderRule
     * @param roleId
     * @param userStatus
     * @param pageNum
     * @param pageSize
     * @param isQueryByName 是否根据名称查询  true 根据名称查询  false根据工号查询
     * @return
     */
    List<EnterpriseUserDTO> getDeptUserList(String enterpriseId, String userName,String deptId,
                                            String orderBy, String orderRule,
                                            Long roleId, Integer userStatus, Integer pageNum,Integer pageSize, Boolean isQueryByName, String mobile, Integer userType);

    /**
     * 获取用户的详细信息
     * @param enterpriseId
     * @param userId
     * @return
     */
    EnterpriseDetailUserVO getFullDetail(String enterpriseId, String userId);

    /**
     * 删除账户
     * @param enterpriseId
     * @param userId
     * @return
     */
    Boolean  deleteEnterpriseUser(String enterpriseId,String userId);

    /**
     * 冻结账户
     * @param enterpriseId
     * @param userId
     * @return
     */
    Boolean freezeEnterpriseUser(String enterpriseId,String userId);

    /**
     * 更新用户详细信息
     * @param userRequestBody
     * @return
     */
    Boolean updateDetailUser(String eid,EnterpriseUserRequestBody userRequestBody, Boolean enableDingSync,CurrentUser user);

    public String addUserNumBySongXia(String eid, String promoterUserId, String roleId);
    /**
     * 获取用户基本信息及部门
     * @param eid
     * @param user
     * @return
     */
    UserDeptDTO getUserDeptByUserId(String eid, CurrentUser user);

    void updateUserMainAdmin(String eid, String userId);

    /**
     * 获取选人组件的用户详情
     * @param eid
     * @param userId
     * @return
     */
    SelectUserInfoDTO selectUserInfo(String eid, String userId);

    /**
     * 更新用户的部门全路径
     * @param user
     * @param deptIdMap
     * @throws
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/27 10:27
     */
    void updateUserDeptPath(EnterpriseUserRequest user, Map<String, String> deptIdMap);

    /**
     * 获得主管理员user_id列表
     * @param eid
     * @return: java.util.List<com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO>
     * @Author: xugangkun
     * @Date: 2021/4/2 15:48
     */
    List<EnterpriseUserDO> getMainAdmin(String eid);

    /**
     * 计算全企业人数
     * @param eid
     * @return
     */
    Integer countUserAll(String eid);

    /**
     * 根据unionid 获取用户信息
     * @param enterpriseId
     * @param unionid
     * @return
     */
    EnterpriseUserDO selectByUnionid(String enterpriseId, String unionid);

    /**
     * 获取unionids
     * @param enterpriseId
     * @param unionids
     * @return
     */
    List<EnterpriseUserDO> selectByUnionids(String enterpriseId, List<String> unionids);

    /**
     * 完善用户信息
     * @param param
     * @param currentUser
     * @return
     */
    ResponseResult improveUserInfo(ImproveUserInfoDTO param, CurrentUser currentUser);

    /**
     * 修改密码
     * @param param
     * @param unionid
     * @return
     */
    ResponseResult modifyPassword(ModifyPasswordDTO param, String unionid);

    /**
     * 根据原密码修改密码
     * @param param 根据密码修改密码DTO
     * @param unionid 用户唯一标识
     * @return 统一返回结果
     */
    ResponseResult modifyPasswordByOriginalPassword(ModifyPasswordByOldDTO param, String unionid);

    /**
     * 校验原密码是否正确
     * @param originalPassword 原密码
     * @param unionid 用户唯一标识
     * @return 统一返回结果
     */
    ResponseResult verifyOriginalPassword(String originalPassword, String unionid);

    /**
     * 新增用户
     * @param param
     * @param enterpriseId
     * @param dbName
     * @return
     */
    ResponseResult addUser(UserAddDTO param, String enterpriseId, String dbName, CurrentUser user);

    /**
     * 新增用户
     * @param enterpriseId
     * @param param
     * @return
     */
    ResponseResult addUser(String enterpriseId, OpenApiAddUserDTO param);


    ResponseResult deleteUser(String enterpriseId, List<String> userIds, String operator);

    /**
     * @param param
     * @param enterpriseId
     * @param dbName
     * @return
     */
    ResponseResult<Boolean> batchUpdateUserStatus(BatchUserStatusDTO param, String enterpriseId, String dbName);

    /**
     * 是否需要完善用户信息
     * @param unionid
     * @param enterpriseId
     * @return
     */
    boolean getUserIsNeedImproveUserInfo(String unionid, String mobile, String enterpriseId);

    /**
     * 忘记密码
     * @param param
     * @return
     */
    ResponseResult forgetPassword(ModifyPasswordDTO param);

    /**
     * 修改用户个人中心信息
     * @param param
     * @param currentUser
     * @return
     */
    ResponseResult updateUserCenterInfo(UpdateUserCenterDTO param, CurrentUser currentUser);

    /**
     * 修改手机号
     * @param param
     * @param currentUser
     * @return
     */
    ResponseResult modifyUserMobile(ModifyUserMobileDTO param, CurrentUser currentUser);

    /**
     * 邀请注册
     * @param param
     * @return
     */
    ResponseResult inviteRegister(InviteUserRegisterDTO param);

    /**
     * 批量复制用户区域门店权限
     * @param eid
     * @param enterpriseUserAuthCopyDTO
     * @param user
     * @author: xugangkun
     * @return void
     * @date: 2021/10/12 15:32
     */
    void copyUserAuth(String eid, EnterpriseUserAuthCopyDTO enterpriseUserAuthCopyDTO, CurrentUser user);

    PageInfo<EnterpriseUserBossVO> getUserListNew(String enterpriseId, EnterpriseUserQueryDTO enterpriseUserQueryDTO);

    Boolean getIsFirstLogin(String enterpriseId,CurrentUser user, String loginWay);


    /**
     *
     * @param enterpriseId
     * @param userName
     * @param deptId
     * @param orderBy
     * @param orderRule
     * @param roleId
     * @param userStatus
     * @param pageNum
     * @param pageSize
     * @param jobNumber
     * @return
     */
    List<EnterpriseUserDTO> listUser(String enterpriseId, String userName,String deptId,
                                            String orderBy, String orderRule,
                                            Long roleId, Integer userStatus, Integer pageNum,Integer pageSize, String jobNumber,String regionId,Boolean hasPage, String mobile, Integer userType);

    /**
     * 全量更新人员所属部门
     * @param param
     * @param enterpriseId
     * @param currentUser
     * @return
     */
    ResponseResult batchUpdateUserRegion(BatchUserRegionMappingDTO param, String enterpriseId, CurrentUser currentUser);


    /**
     * 获取用户的部门 组成String字符串 格式 ,/1/,/1/2/3/,/1/2/4/,
     * @return
     */
    List<EnterpriseUserDO> getUserRegionPathListStr(String enterpriseId,List<String> userIds);

    /**
     * 获取用户的部门 组成String字符串 格式 ,/1/,/1/2/3/,/1/2/4/,补全用户表的user_region_ids字段
     * @param enterpriseId
     * @param userIds
     */
    void updateUserRegionPathList(String enterpriseId, List<String> userIds);

    /**
     * 根据用户ids查询用户，包含用户角色，所在部门
     * @param enterpriseId
     * @param userIds
     * @return
     */
    List<EnterpriseUserDTO> getUserByUserIds(String enterpriseId, List<String> userIds);

    /**
     * 获取用户id和姓名map
     * @param enterpriseId
     * @param userIds
     * @return
     */
    Map<String, String> getUserNameMap(String enterpriseId, List<String> userIds);

    /**
     * 获取用户信息
     * @return
     */
    OpenUserVO getOpenUserInfo(String enterpriseId, OpenApiUserDTO openApiUserDTO);

    /**
     * 获取用户信息
     * @return
     */
    void updateUserRole(String enterpriseId, OpenApiUserDTO openApiUserDTO);

    List<String> selectByUserIdsAndStatus(String eid, List<String> userIds, Integer userStatus);

    /**
     * 填充用户管辖下属
     * @param enterpriseId
     * @param userIdList
     * @return
     */
    Map<String, SubordinateUserRangeDTO> fillUserSubordinateNames(String enterpriseId, List<String> userIdList);

    /**
     * 获取人员所属部门
     * @param enterpriseId
     * @param userIdList
     * @return
     */
    Map<String, String> getUserRegion(String enterpriseId, List<String> userIdList);


    /**
     * 更新人员角色权限
     * @param enterpriseId
     * @param param
     */
    void updateUseRoleAndAuth(String corpId, String enterpriseId, OpenApiUpdateUserAuthDTO param);

    /**
     * 用户导出
     * @param param
     * @return
     */
    ImportTaskDO externalUserInfoExport(UserInfoExportRequest param);

    /**
     * 更新人员角色权限
     * @param enterpriseId
     * @param param
     */
    void updateUseRoleAndRegionAuth(String corpId, String enterpriseId, OpenApiUpdateUserRoleAndAuthDTO param);

    void updateUserRegionPath(String enteprirseId, String regionId);

    /**
     * 根据用户id清除用户token
     * @param eid 企业id
     * @param userId 用户id
     */
    void clearTokenByUserId(String eid, String userId);

    /**
     * 根据角色清除用户token
     * @param eid 企业id
     * @param roleId 角色id
     */
    void clearTokenByRoleId(String eid, Long roleId);

    /**
     * 分页查询
     * @param enterpriseId 企业id
     * @param param 用户查询DTO
     * @return 分页对象
     */
    PageDTO<UserInfoVO> getUserPage(String enterpriseId, OpenApiUserQueryDTO param);

    /**
     * 列表查询
     * @param enterpriseId 企业id
     * @param param 用户查询DTO
     * @return
     */
    List<UserInfoVO> getUserList(String enterpriseId, OpenApiUserQueryDTO param);

    /**
     * 初始化密码
     * @param param
     * @return
     */
    ResponseResult<String> initPassword(InitPasswordDTO param, CurrentUser user);

    void handleUserRegionMapping(String enterpriseId, List<String> newRegionIds, String userId, CurrentUser currentUser, EnterpriseUserDO userDO);

    void updateUserAuth(String eid, List<AuthRegionStoreUserDTO> authRegionStoreList, String userId, Boolean enableDingSync);

    Boolean getUserByMobile(String mobile);

}
