package com.coolcollege.intelligent.controller.system;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.PlatFormTypeEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.controller.system.request.SysRoleAddRequest;
import com.coolcollege.intelligent.controller.system.request.SysRoleUserDeleteRequest;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.system.SysRoleQueryDTO;
import com.coolcollege.intelligent.model.system.dto.RoleDTO;
import com.coolcollege.intelligent.model.system.dto.UserDTO;
import com.coolcollege.intelligent.model.system.request.RoleDeleteRequest;
import com.coolcollege.intelligent.model.system.request.SysRoleModifyAuthRequest;
import com.coolcollege.intelligent.model.system.request.SysRoleModifyBaseRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.SubordinateMappingService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.selectcomponent.SelectionComponentService;
import com.coolcollege.intelligent.service.system.SysMenuService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 系统角色
 *
 * @author wangchunhui
 */
@RestController
@Slf4j
@BaseResponse
@RequestMapping("/v2/system/{enterprise-id}/role")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private EnterpriseSettingService enterpriseSettingService;
    @Autowired
    private SelectionComponentService selectionComponentService;
    @Autowired
    private SubordinateMappingService subordinateMappingService;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    /**
     * 获取所有的角色名称
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping(path = "/getRoles")
    public ResponseResult<Map<String, Object>> getRoles(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                        @RequestParam(value = "position_type", required = false) String positionType,
                                                        @RequestParam(value = "role_name", required = false) String roleName,
                                                        @RequestParam(value = "page_num", required = false,defaultValue = "1") Integer pageNum ,
                                                        @RequestParam(value = "page_size", required = false,defaultValue = "10") Integer pageSize ) {

        List<RoleDTO> roles = sysRoleService.getRoles(enterpriseId, roleName, positionType, pageNum, pageSize);
        if(CollectionUtils.isNotEmpty(roles)){
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>(roles)));
        }else {
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>()));

        }
    }

    /**
     * 获取同步角色列表
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping("/sync_roles")
    public Object getSyncList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId) {
        DataSourceHelper.changeToMy();
        return sysRoleService.getSyncRoles(enterpriseId);
    }

    /**
     * 添加角色接口
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping(path = "/addSystemRoles")
    @OperateLog(operateModule = CommonConstant.Function.ROLE, operateType = CommonConstant.LOG_ADD, operateDesc = "添加角色")
    @SysLog(func = "添加", opModule = OpModuleEnum.SETTING_POSITION, opType = OpTypeEnum.INSERT)
    public ResponseResult addSystemRoles(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
        @RequestBody SysRoleAddRequest request) {
        return ResponseResult.success(sysRoleService.addSystemRoles(enterpriseId,  request.getRoleName(),
                request.getPositionType(), request.getAppMenuIdList(), request.getPriority()));
    }

    /**
     * 角色基本信息
     * @param enterpriseId
     * @param roleId
     * @return
     */
    @GetMapping(path = "/detail/base")
    public ResponseResult detailSystemRoles(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                  @RequestParam("role_id")Long roleId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(sysRoleService.detailSystemRole(enterpriseId,  roleId));
    }

    /**
     * 角色基本信息
     * @param enterpriseId
     * @param roleId
     * @return
     */
    @GetMapping(path = "/detail/baseNew")
    public ResponseResult detailSystemRolesNew(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                            @RequestParam("role_id")Long roleId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(sysRoleService.detailSystemRoleNew(enterpriseId,  roleId));
    }

    /**
     * 获角色下的人员列表
     * @param enterpriseId
     * @param roleId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(path = "/detail/user")
    public ResponseResult detailUserSystemRoles(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                @RequestParam(value = "role_id",required = false)Long roleId,
                                                @RequestParam(value = "user_name",required = false)String userName,
                                                @RequestParam(value = "page_num", required = false,defaultValue = "1") Integer pageNum ,
                                                @RequestParam(value = "page_size", required = false,defaultValue = "10") Integer pageSize,
                                                @RequestParam(value = "active",required = false)Boolean active) {
        DataSourceHelper.changeToMy();
        if(roleId==null){
            return ResponseResult.success(new ArrayList<>());
        }
        List<UserDTO> userDTOS = sysRoleService.detailUserSystemRole(enterpriseId, roleId,userName, pageNum, pageSize, active);
        //填充人员门店，区域，职位信息
        userDTOS = selectionComponentService.supplementSelectRoleUserQueryResult(enterpriseId, userDTOS);
        CurrentUser currentUser = UserHolder.getUser();
        Boolean haveAllSubordinateUser = subordinateMappingService.checkHaveAllSubordinateUser(enterpriseId, currentUser.getUserId());
        List<String> userSubordinateList = Lists.newArrayList();
        if(!haveAllSubordinateUser){
            userSubordinateList = subordinateMappingService.getSubordinateUserIdList(enterpriseId, currentUser.getUserId(),Boolean.TRUE);
        }
        List<String> finalUserSubordinateList = userSubordinateList;
        userDTOS.forEach(f -> {
            if(haveAllSubordinateUser){
                f.setSelectFlag(true);
            }else {
                f.setSelectFlag(finalUserSubordinateList.contains(f.getUserId()));
            }
        });
        if(CollectionUtils.isNotEmpty(userDTOS)){
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>(userDTOS)));
        }else {
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>()));
        }
    }

    /**
     * 获取某个角色下的权限(pc)
     *
     * @param enterpriseId
     * @param roleId
     * @return
     */
    @GetMapping("/getSysMenuList")
    public Object getSysMenuList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
        @RequestParam(value = "roleId", required = false) String roleId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(sysMenuService.getMenusByRole(enterpriseId, roleId));
    }
    @GetMapping("/getSysMenuList/app")
    public Object getAppSysMenuList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId) {
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(sysMenuService.getAppMenusByUser(enterpriseId, null, currentUser));
    }

    @GetMapping("/getAppMenuList")
    public Object getAppMenuList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                 @RequestParam(value = "roleId", required = false) String roleId) {
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(sysMenuService.getAppMenuList(enterpriseId, roleId, currentUser));
    }
    @GetMapping("/getSysMenuList/appNew")
    public Object getAppSysMenuNewList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId) {
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(sysMenuService.getAppMenusByUserNew(enterpriseId, null, currentUser));
    }


    /**
     * 修改角色的接口（兼容性问题  后期作废）
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("/modify")
    @OperateLog(operateModule = CommonConstant.Function.ROLE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "修改角色信息")
    public Boolean modifyRole(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
        @RequestBody @Validated SysRoleModifyAuthRequest request) {
        DataSourceHelper.changeToMy();
        return sysRoleService.modifyRole(enterpriseId, request, PlatFormTypeEnum.APP);
    }

    /**
     * 新的角色编辑
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("/update")
    @OperateLog(operateModule = CommonConstant.Function.ROLE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "修改角色信息")
    @SysLog(func = "编辑", opModule = OpModuleEnum.SETTING_POSITION, opType = OpTypeEnum.EDIT_DATA_FUNC_AUTH, preprocess = true)
    public Boolean updateRole(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                              @RequestBody @Validated SysRoleModifyAuthRequest request) {
        DataSourceHelper.changeToMy();
        return sysRoleService.modifyRole(enterpriseId, request, PlatFormTypeEnum.NEW_APP);
    }

    /**
     * 角色基本信息接口
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("/modify/base")
    @OperateLog(operateModule = CommonConstant.Function.ROLE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "更改角色信息(名称和类型)")
    @SysLog(func = "编辑", subFunc = "基础信息", opModule = OpModuleEnum.SETTING_POSITION, opType = OpTypeEnum.EDIT)
    public Boolean modifyRoleBase(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                              @RequestBody @Validated SysRoleModifyBaseRequest request) {
        DataSourceHelper.changeToMy();
        return sysRoleService.modifyRoleBase(enterpriseId, request);
    }



    /**
     * 根据角色id删除角色
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("/deleteRoles")
    @OperateLog(operateModule = CommonConstant.Function.ROLE, operateType = CommonConstant.LOG_DELETE, operateDesc = "根据角色id删除角色")
    @SysLog(func = "删除", opModule = OpModuleEnum.SETTING_POSITION, opType = OpTypeEnum.DELETE, preprocess = true)
    public Boolean deleteRoles(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
        @RequestBody RoleDeleteRequest request) {
        DataSourceHelper.reset();
        Boolean enableDingSync = Objects.equals(enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId).getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN) ||
                Objects.equals(enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId).getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD);
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        return sysRoleService.batchDeleteRoles(enterpriseId, userId, request.getRoleIdList(), enableDingSync);
    }

    /**
     * 获取角色下的用户信息
     *
     * @param enterpriseId
     * @param roleId
     * @param userName
     * @return
     */
    @GetMapping(path = "/getPersonsByRole")
    public Object getPersonsByRole(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
        @RequestParam(value = "roleId", required = false) Long roleId,
        @RequestParam(value = "userName", required = false) String userName,
        @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
        @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum) {

        return ResponseResult.success(sysRoleService.getPersonsByRole(enterpriseId, roleId, userName, pageNum, pageSize));
    }

    /**
     * 给角色添加用户
     *
     * @param enterpriseId
     * @param sysRoleQueryDTO
     * @return
     */
    @PostMapping(path = "/addPersonToUser")
    @OperateLog(operateModule = CommonConstant.Function.ROLE, operateType = CommonConstant.LOG_ADD, operateDesc = "给角色添加用户")
    @SysLog(func = "编辑", subFunc = "配置人员", opModule = OpModuleEnum.SETTING_POSITION, opType = OpTypeEnum.CONFIG_PERSON)
    public Object addPersonToUser(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
        @RequestBody(required = false) SysRoleQueryDTO sysRoleQueryDTO) {
        DataSourceHelper.changeToMy();

        return sysRoleService.addPersonToUser(enterpriseId, sysRoleQueryDTO, false);

    }

    /**
     * 删除角色下的用户信息
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("/deletePersonToUser")
    @OperateLog(operateModule = CommonConstant.Function.ROLE, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除角色下的用户信息")
    @SysLog(func = "编辑", subFunc = "移除人员", opModule = OpModuleEnum.SETTING_POSITION, opType = OpTypeEnum.REMOVE_PERSON)
    public Object deletePersonToUser(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                     @RequestBody SysRoleUserDeleteRequest request) {
        DataSourceHelper.changeToMy();
        return sysRoleService.deletePersonToUser(enterpriseId, request.getRoleId(), request.getUserIdList());

    }


    @PostMapping("/fixData")
    public Boolean fixData(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody(required = false) List<String> enterpriseIds,@RequestParam Long menuId) {
        return sysRoleService.fixData(enterpriseId,enterpriseIds,menuId);
    }

    @ApiOperation("根据职位名称更新职位第三方唯一id")
    @PostMapping(path = "/updateThirdUniqueIds")
    @OperateLog(operateModule = CommonConstant.Function.IMPORT, operateType = CommonConstant.LOG_ADD, operateDesc = "更新职位第三方唯一id")
    public ResponseResult updateThirdUniqueIds(@PathVariable(value = "enterprise-id") String enterpriseId, MultipartFile file) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(sysRoleService.updateThirdUniqueIds(enterpriseId, file));
    }
}
