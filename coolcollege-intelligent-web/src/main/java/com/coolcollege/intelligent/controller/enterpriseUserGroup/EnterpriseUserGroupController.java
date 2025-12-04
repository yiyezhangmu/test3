package com.coolcollege.intelligent.controller.enterpriseUserGroup;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserMappingDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.usergroup.request.*;
import com.coolcollege.intelligent.model.usergroup.vo.UserGroupVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.UserPersonInfoService;
import com.coolcollege.intelligent.service.enterpriseUserGroup.EnterpriseUserGroupService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * @author wxp
 * @date 2022-12-29 14:15
 */
@Api(tags = "用户分组")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/userGroup")
@ErrorHelper
@Slf4j
public class EnterpriseUserGroupController {

    @Autowired
    private EnterpriseUserGroupService enterpriseUserGroupService;

    @Autowired
    private ExportUtil exportUtil;

    @Autowired
    private UserPersonInfoService userPersonInfoService;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    /**
     * 添加用户分组
     *
     * @param enterpriseId
     * @param userGroupAddRequest
     * @return
     */
    @ApiOperation(value = "新建")
    @PostMapping("/addUserGroup")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_ADD, operateDesc = "添加用户分组")
    public ResponseResult<Boolean> addUserGroup(@PathVariable("enterprise-id") String enterpriseId,
                                                @Valid @RequestBody UserGroupAddRequest userGroupAddRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(enterpriseUserGroupService.saveOrUpdateUserGroup(enterpriseId, userGroupAddRequest, UserHolder.getUser()));
    }

    /**
     * 更新用户分组
     *
     * @param enterpriseId
     * @param userGroupAddRequest
     * @return
     */
    @ApiOperation(value = "更新")
    @PostMapping("/updateUserGroup")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_UPDATE, operateDesc = "更新用户分组")
    public ResponseResult<Boolean> updateUserGroup(@PathVariable("enterprise-id") String enterpriseId,
                                                   @Valid @RequestBody UserGroupAddRequest userGroupAddRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(enterpriseUserGroupService.saveOrUpdateUserGroup(enterpriseId, userGroupAddRequest, UserHolder.getUser()));
    }

    /**
     * 批量删除分组
     */
    @ApiOperation(value = "批量删除")
    @PostMapping("/batchDeleteGroup")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_DELETE, operateDesc = "批量删除用户分组")
    public ResponseResult batchDeleteGroup(@PathVariable("enterprise-id") String enterpriseId,
                                           @Valid @RequestBody UserGroupDeleteRequest request) {
        DataSourceHelper.changeToMy();
        enterpriseUserGroupService.batchDeleteGroup(enterpriseId, request.getGroupId(), request.getUserIdList());
        return ResponseResult.success(true);
    }

    /**
     * 获取分组列表
     *
     * @param enterpriseId
     * @param groupName
     * @return
     */
    @ApiOperation("分组列表")
    @GetMapping("/listUserGroup")
    public ResponseResult<List<UserGroupVO>> listUserGroup(@PathVariable("enterprise-id") String enterpriseId,
                                                           @RequestParam(value = "groupName", required = false, defaultValue = "") String groupName) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(enterpriseUserGroupService.listUserGroup(enterpriseId, groupName, user));
    }

    @ApiOperation("分组详情")
    @GetMapping(path = "/getGroupInfo")
    public ResponseResult<UserGroupVO> getGroupInfo(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                    @RequestParam(value = "groupId") @NotNull String groupId) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(enterpriseUserGroupService.getGroupInfo(enterpriseId, groupId, user));
    }

    @ApiOperation("用户列表")
    @GetMapping(path = "/listUserByGroupId")
    public ResponseResult<PageInfo<EnterpriseUserDTO>> listUserByGroupId(@PathVariable(value = "enterprise-id", required = true) String enterpriseId,
                                                                         @RequestParam(name = "groupId", required = true) String groupId,
                                                                         @RequestParam(name = "userName", required = false) String userName,
                                                                         @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                                         @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(enterpriseUserGroupService.listUserByGroupId(enterpriseId, groupId, userName, pageNum, pageSize, currentUser));
    }

    @ApiOperation("导出分组下用户")
    @PostMapping("/groupUserExport")
    public ResponseResult groupUserExport(@PathVariable(value = "enterprise-id") String enterpriseId,
                                          @RequestBody UserGroupExportRequest request) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_GROUP_USER_INFO);
        request.setEnterpriseId(enterpriseId);
        request.setUser(UserHolder.getUser());
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }

    @ApiOperation(value = "配置用户")
    @PostMapping("/configUser")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_ADD, operateDesc = "配置用户")
    public ResponseResult<Boolean> configUser(@PathVariable("enterprise-id") String enterpriseId,
                                              @RequestBody UserGroupAddRequest userGroupAddRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(enterpriseUserGroupService.configUser(enterpriseId, userGroupAddRequest, UserHolder.getUser()));
    }


    @ApiOperation(value = "获取选择人员明细列表")
    @PostMapping("/personInfoList")
    public ResponseResult<List<String>> personInfoList(@PathVariable("enterprise-id") String enterpriseId,
                                                       @RequestBody UserDetailListRequest userDetailListRequest) {
        DataSourceHelper.changeToMy();
        String userId = userDetailListRequest.getCreateUserId();
        if (StringUtils.isNotBlank(userId)){
            EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserId(enterpriseId, userId);
            if (Objects.isNull(enterpriseUserDO)){
                userId = UserHolder.getUser().getUserId();
            }
        }
        if (StringUtils.isBlank(userId) ) {
            userId = UserHolder.getUser().getUserId();
        }
        return ResponseResult.success(userPersonInfoService.getUserNameList(enterpriseId, userDetailListRequest.getUsePersonInfo(),
                userDetailListRequest.getUseRange(), userId));
    }

    @ApiOperation(value = "批量移除")
    @PostMapping("/batchRemoveUser")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_DELETE, operateDesc = "批量移除")
    public ResponseResult<Boolean> batchRemoveUser(@PathVariable("enterprise-id") String enterpriseId,
                                                   @RequestBody UserGroupRemoveRequest userGroupRemoveRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(enterpriseUserGroupService.batchRemoveUser(enterpriseId, userGroupRemoveRequest, UserHolder.getUser()));
    }
}
