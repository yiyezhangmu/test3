package com.coolcollege.intelligent.service.system;

import com.coolcollege.intelligent.common.enums.PlatFormTypeEnum;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiAddRoleDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.RoleDetailVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserRole;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.system.SysRoleQueryDTO;
import com.coolcollege.intelligent.model.system.dto.RoleBaseDetailDTO;
import com.coolcollege.intelligent.model.system.dto.RoleDTO;
import com.coolcollege.intelligent.model.system.dto.UserDTO;
import com.coolcollege.intelligent.model.system.request.SysRoleModifyAuthRequest;
import com.coolcollege.intelligent.model.system.request.SysRoleModifyBaseRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


public interface SysRoleService {
    /**
     * 查询所有的角色
     * @param enterpriseId
     * @return
     */
    List<RoleDTO> getRoles(String enterpriseId, String roleName,String positionType,Integer pageNum, Integer pageSize);

    /**
     * 添加角色信息
     * @param enterpriseId
     * @param roleName
     */
    Long addSystemRoles( String enterpriseId, String roleName,String positionType,List<Long> appMenuIdList, Integer priority);
    /**
     * 角色基本信息
     * @param enterpriseId
     * @param roleId
     * @return
     */
    RoleBaseDetailDTO detailSystemRole(String enterpriseId, Long roleId);

    /**
     * 角色基本信息
     * @param enterpriseId
     * @param roleId
     * @return
     */
    RoleBaseDetailDTO detailSystemRoleNew(String enterpriseId, Long roleId);

    List<String> getRoleIdByUserId(String eid,String userId);

    /**
     * 角色下的人员信息
     * @param enterpriseId
     * @param roleId
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<UserDTO> detailUserSystemRole(String enterpriseId, Long roleId, String userName,Integer pageNum, Integer pageSize, Boolean active);

    /**
     * 更改角色信息
     * @param enterpriseId
     * @param request
     * @return
     */
    Boolean modifyRole(String enterpriseId, SysRoleModifyAuthRequest request , PlatFormTypeEnum appType);

    /**
     * 更改角色信息（名称和类型）
     * @param enterpriseId
     * @param request
     * @return
     */
    Boolean modifyRoleBase(String enterpriseId, SysRoleModifyBaseRequest request );




    /**
     * 删除角色权限信息
     * @param enterpriseId
     * @param roleId
     * @return
     */
    Boolean  deleteRoles(String enterpriseId,Long roleId);

    /**
     * 批量删除角色
     * @param enterpriseId
     * @param roleIdList
     * @return
     */
    Boolean  batchDeleteRoles(String enterpriseId, String userId, List<Long> roleIdList, Boolean enableDingSync);
    /**
     * 获取角色下用户列表
     * @param enterpriseId
     * @param roleId
     * @param userName
     * @return
     */
    Map<String ,Object> getPersonsByRole(String enterpriseId, Long roleId,String userName,Integer pageNum,Integer pageSize);

    /**
     * 给指定角色添加用户
     * @param enterpriseId
     * @param sysRoleQueryDTO
     * @param delAll 是否删除全部关联关系  默认  false
     * @return
     */
    Boolean addPersonToUser  (String enterpriseId,SysRoleQueryDTO sysRoleQueryDTO, boolean delAll);

    /**
     * 删除人员角色信息
     * @param eid
     * @param userIds
     * @param delAll
     */
    void delRolesByUserIds(String eid, List<String> userIds, boolean delAll);
    
    Integer  countRoleByPerson(String eid,String userId);

    /**
     * 删除角色下的用户信息
     * @param enterpriseId
     * @param roleId
     * @param userIdList
     * @return
     */
    Boolean deletePersonToUser(String enterpriseId,Long roleId,List<String> userIdList);

    /**
     * 插入或更新角色
     * @param enterpriseId
     * @param role
     * @return
     */
    Boolean insertOrUpdateRole(String enterpriseId, SysRoleDO role);

    /**
     * 批量插入角色信息
     * @param enterpriseId
     * @param roles
     * @return
     */
    Boolean insertBatchRoles(String enterpriseId, List<SysRoleDO> roles);

    /**
     * 批量插入用户角色
     * @param eid
     * @param userRole
     * @return
     */
    Boolean insertBatchUserRole(String eid, List<EnterpriseUserRole> userRole);

    /**
     * 删除同步角色的关联关系
     * @param eid
     * @param userId
     * @return
     */
    Boolean deleteSyncRoleRelate(String eid, String userId);

    /**
     * 获取同步的角色列表
     * @param eid
     * @return
     */
    Object getSyncRoles(String eid);

    Boolean checkIsAdmin(String enterpriseId, String userId);

    Boolean checkIsAdminAndSubAdmin(String enterpriseId, String userId);

    /**
     * 根据岗位来源和角色名称获取角色
     * @param eid 企业id
     * @param roleName 角色名称
     * @param source 岗位来源
     * @return: java.util.List<com.coolcollege.intelligent.model.system.SysRoleDO>
     * @Author: xugangkun
     * @Date: 2021/3/22 16:11
     */
    List<SysRoleDO> selectByRoleNameAndSource(String eid, String roleName, String source);

    /**
     * 获得除未分配以外的角色中，排序值最大的角色(优先级最低)
     * @param eid 企业id
     * @return: java.lang.Integer
     * @Author: xugangkun
     * @Date: 2021/3/26 13:59
     */
    Integer getNormalRoleMaxPriority(String eid);

    /**
     * 初始化默认角色的排序值
     * @param eid
     * @throws
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/29 15:27
     */
    void initDefaultRolePriority(String eid);

    SysRoleDO getRoleByRoleEnum(String eid,String roleEnum);

    Long getRoleIdByRoleEnum(String eid,String roleEnum);

    /**
     * 给新增角色初始化移动端菜单
     * @param eid
     * @param roleId
     * @return
     */
    Boolean initMenuWhenSyncRole(String eid, Long roleId);

    /**
     * 获取角色信息
     * @param eid
     * @param ids
     * @return
     */
    List<SysRoleDO> getRoleByRoleIds(String eid, List<Long> ids);


    Boolean fixData(String enterpriseId,List<String> enterpriseIds,Long menuId);

    RoleDetailVO insertOrUpdateSysRole(String enterpriseId, OpenApiAddRoleDTO param);

    List<SysRoleDO> getRoleIdByThirdUniqueIds(String enterpriseId, List<String> thirdUniqueIds);

    Boolean updateThirdUniqueIds(String enterpriseId, MultipartFile file);

    Integer deleteRoleWithoutUsers(String enterpriseId, Boolean isDeleteDefault);
}
