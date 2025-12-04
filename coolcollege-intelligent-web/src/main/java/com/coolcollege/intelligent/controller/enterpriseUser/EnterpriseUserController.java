package com.coolcollege.intelligent.controller.enterpriseUser;

import cn.hutool.core.date.DatePattern;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.FileUtil;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.dto.*;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseDetailUserVO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.UserInfoExportRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.user.EnterpriseUserAuthCopyDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.requestBody.user.EnterpriseUserRequestBody;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户
 * Created by ydw on 2020/6/16.
 * @author wch
 */
@RestController
@RequestMapping({"/v2/enterprises/{enterprise-id}/users", "/v3/enterprises/{enterprise-id}/users"})
@BaseResponse
@Slf4j
public class EnterpriseUserController {

    @Autowired
    public EnterpriseUserService enterpriseUserService;
    @Resource
    private EnterpriseService enterpriseService;
    @Autowired
    private EnterpriseSettingService enterpriseSettingService;
    @Autowired
    private EnterpriseConfigService enterpriesConfigService;
    @Autowired
    private ExportUtil exportUtil;
    @Autowired
    private RedisUtilPool redisUtilPool;

    public static final String ADDRESS_BOOK_TITLE = "填写须知:" +
            "\n <1>请勿对本excel表中员工姓名、工号、部门、岗位进行修改，只需修改自定义字段；" +
            "\n <2>员工UserID为必填项，也是识别学员身份的唯一标识；" +
            "\n <3>操作方法：保留需要导入的学员，删除不需要导入的学员，保存后进行导入；";
    public static final String SHEET_NAME = "员工通讯录";
    public static final String FILE_NAME = "员工通讯录.xlsx";

    /**
     * 查询用户详情
     *
     * @param eid
     * @param userId
     * @return
     */
    @GetMapping(path = "/{user-id}/query")
    public EnterpriseDetailUserVO getUserDetail(@PathVariable(value = "enterprise-id", required = true) String eid,
                                                @PathVariable(value = "user-id", required = true) String userId) {
        DataSourceHelper.changeToMy();
        return enterpriseUserService.getFullDetail(eid, userId);
    }

    /**
     * 查询用户列表
     *
     * @param eid
     * @param userName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(path = "/list")
    public ResponseResult getUserList(@PathVariable(value = "enterprise-id", required = true) String eid,
                              @RequestParam(name = "user_name", required = false, defaultValue = "") String userName,
                              @RequestParam(name = "page_num", required = false, defaultValue = "1") Integer pageNum,
                              @RequestParam(name = "page_size", required = false, defaultValue = "20") Integer pageSize) {
        DataSourceHelper.changeToMy();
        EnterpriseUserQueryDTO enterpriseUserQueryDTO = new EnterpriseUserQueryDTO();
        enterpriseUserQueryDTO.setUserName(userName);
        enterpriseUserQueryDTO.setPage_num(pageNum);
        enterpriseUserQueryDTO.setPage_size(pageSize);
        return ResponseResult.success(enterpriseUserService.getUserList(eid, enterpriseUserQueryDTO));
    }

    /**
     * 查询用户列表（无权限控制）
     * @param eid
     * @param userName
     * @param deptId
     * @param roleId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(path = "/dept/list")
    public ResponseResult getUserDeptList(@PathVariable(value = "enterprise-id", required = true) String eid,
                                          @RequestParam(name = "user_name", required = false) String userName,
                                          @RequestParam(name = "dept_id", required = false) String deptId,
                                          @RequestParam(name = "role_id", required = false) Long roleId,
                                          @RequestParam(name = "order_by", required = false) String orderBy,
                                          @RequestParam(name = "mobile", required = false) String mobile,
                                          @RequestParam(name = "order_rule", required = false, defaultValue = "asc") String orderRule,
                                          @RequestParam(name = "user_status", required = false) Integer userStatus,
                                          @RequestParam(name = "userType", required = false) Integer userType,
                                          @RequestParam(name = "page_num", required = false, defaultValue = "1") Integer pageNum,
                                          @RequestParam(name = "page_size", required = false, defaultValue = "10") Integer pageSize,
                                          @RequestParam(name = "isQueryByName", required = false, defaultValue = "true") Boolean isQueryByName) {
        DataSourceHelper.changeToMy();
        List<EnterpriseUserDTO> deptUserList = enterpriseUserService.getDeptUserList(eid, userName, deptId, orderBy, orderRule, roleId, userStatus, pageNum, pageSize, isQueryByName, mobile, userType);
        if(CollectionUtils.isNotEmpty(deptUserList)){
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>(deptUserList)));
        }else {
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>()));

        }
    }


    /**
     * 查询人员列表（多条件）
     * @param eid
     * @param userName
     * @param deptId
     * @param roleId
     * @param orderBy
     * @param orderRule
     * @param userStatus
     * @param pageNum
     * @param pageSize
     * @param jobNumber
     * @return
     */
    @GetMapping(path = "/dept/userList")
    public ResponseResult<PageInfo<EnterpriseUserDTO>> getUserList(@PathVariable(value = "enterprise-id", required = true) String eid,
                                          @RequestParam(name = "user_name", required = false) String userName,
                                          @RequestParam(name = "dept_id", required = false) String deptId,
                                          @RequestParam(name = "role_id", required = false) Long roleId,
                                          @RequestParam(name = "order_by", required = false) String orderBy,
                                          @RequestParam(name = "order_rule", required = false, defaultValue = "asc") String orderRule,
                                          @RequestParam(name = "user_status", required = false) Integer userStatus,
                                           @RequestParam(name = "userType", required = false) Integer userType,
                                          @RequestParam(name = "page_num", required = false, defaultValue = "1") Integer pageNum,
                                          @RequestParam(name = "page_size", required = false, defaultValue = "10") Integer pageSize,
                                          @RequestParam(name = "job_number", required = false) String jobNumber,
                                          @RequestParam(name = "region_id", required = false) String regionId,
                                          @RequestParam(name = "mobile", required = false) String mobile,
                                          @RequestParam(name = "has_page", required = false,defaultValue = "true") Boolean hasPage) {
        DataSourceHelper.changeToMy();
        List<EnterpriseUserDTO> deptUserList = enterpriseUserService.listUser(eid, userName, deptId, orderBy, orderRule, roleId, userStatus, pageNum, pageSize, jobNumber,regionId,hasPage, mobile, userType);
        if(CollectionUtils.isNotEmpty(deptUserList)){
            return ResponseResult.success(new PageInfo<>(deptUserList));
        }else {
            return ResponseResult.success(new PageInfo<>());

        }
    }





    /**
     * 更新用户信息
     *
     * @param eid
     * @param userRequestBody
     * @return
     */
    @PostMapping(path = "/update")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_UPDATE, operateDesc = "更新用户信息")
    @SysLog(func = "编辑", opModule = OpModuleEnum.ENTERPRISE_USER, opType = OpTypeEnum.EDIT)
    public Boolean updateUserMobile(@PathVariable(value = "enterprise-id", required = true) String eid,
                                   @RequestBody @Validated EnterpriseUserRequestBody userRequestBody) {
        DataSourceHelper.reset();
        Boolean enableDingSync = Objects.equals(enterpriseSettingService.getEnterpriseSettingVOByEid(eid).getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN) ||
                Objects.equals(enterpriseSettingService.getEnterpriseSettingVOByEid(eid).getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD);
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return enterpriseUserService.updateDetailUser(eid,userRequestBody, enableDingSync,user);
    }

    /**
     * 导出通讯录
     *
     * @param enterpriseId
     * @param response
     */
    @GetMapping(path = "/export_address_book")
    public void exportAddressBook(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                  HttpServletResponse response) {
        DataSourceHelper.changeToMy();
        List<AddressBookUserDTO> users = enterpriseUserService.getAddressBookUsers(enterpriseId);

        FileUtil.exportBigDataExcel(users, ADDRESS_BOOK_TITLE, SHEET_NAME, AddressBookUserDTO.class, FILE_NAME, response);
    }

    /**
     * 导入通讯录
     *
     * @param enterpriseId
     * @param file
     * @return
     */
    @PostMapping(path = "/import_address_book")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_ADD, operateDesc = "导入用户信息")
    public Object importAddressBook(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                    MultipartFile file) {
        DataSourceHelper.changeToMy();
        ExcelReader reader = null;
        try {
            reader = ExcelUtil.getReader(file.getInputStream());
        } catch (IOException e) {
            log.error("read file error:", e);
        }
        assert reader != null;
        List<Map<String, Object>> dataMapList = reader.read(1, 2, Integer.MAX_VALUE);

        return enterpriseUserService.importAddressBook(enterpriseId, dataMapList, file.getOriginalFilename());

    }



    /**
     * 删除用户
     * @param enterpriseId
     * @param userId
     * @return
     */
    @GetMapping("/deleteEnterpriseUser")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除用户")
    public Object deleteEnterpriseUser(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                       @RequestParam(value = "userId",required = false) String userId){
        DataSourceHelper.changeToMy();
        return enterpriseUserService.deleteEnterpriseUser(enterpriseId,userId);

    }

    /**
     * 冻结账户
     * @param enterpriseId
     * @param userId
     * @return
     */
    @GetMapping("/freezeEnterpriseUser")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_UPDATE, operateDesc = "冻结账户")
    public Object freezeEnterpriseUser(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                       @RequestParam(value = "userId",required = false) String userId){
        return null;
    }

    /**
     * 根据多个用户id批量获取用户列表
     * @param enterpriseId
     * @param userIds
     * @return
     */
    @GetMapping("/getUserList")
    public Object getUserList(@PathVariable(value = "enterprise-id",required = false)String enterpriseId,
                              @RequestParam(value = "userIds",required = false)String userIds){
        DataSourceHelper.changeToMy();
        ArrayList<String> strings = Lists.newArrayList(userIds.split(","));
        return enterpriseUserService.selectUsersByUserIds(enterpriseId,strings);
    }

    /**
     * 获取用户基本信息和部门信息
     * @param enterpriseId
     * @param userId
     * @return
     */
    @GetMapping("/userDeptInfo")
    public UserDeptDTO getUserAndUserDeptInfo(@PathVariable(value = "enterprise-id",required = false)String enterpriseId,
                                              String userId) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        UserDeptDTO result = enterpriseUserService.getUserDeptByUserId(enterpriseId, user);
        DataSourceHelper.reset();
        EnterpriseDO enterpriseDO = enterpriseService.selectById(enterpriseId);
        result.setEnterpriseName(enterpriseDO.getName());
        result.setIsNeedImproveUserInfo(enterpriseUserService.getUserIsNeedImproveUserInfo(result.getUnionid(), result.getMobile(), enterpriseId));
        if(StringUtils.isBlank(result.getMobile())){
            result.setMobile(user.getMobile());
        }
        if(StringUtils.isBlank(result.getEmail())){
            result.setEmail(user.getEmail());
        }
        return result;
    }

    /**
     * 获取选人组件用户信息
     * @param enterpriseId
     * @param userId
     * @return
     */
    @GetMapping("selectUserInfo")
    public SelectUserInfoDTO getUserInfo(@PathVariable(value = "enterprise-id",required = false)String enterpriseId,
                                         String userId) {
        return enterpriseUserService.selectUserInfo(enterpriseId, userId);
    }

    /**
     * pc端区域报表导出
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("userInfoExport")
    @Deprecated
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "设置-组织架构-成员管理")
    public ResponseResult statisticsRegionExport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                 @RequestBody UserInfoExportRequest request) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_USER_INFO);
        request.setEnterpriseId(enterpriseId);
        request.setUser(UserHolder.getUser());
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }

    @PostMapping("/externalUserInfoExport")
    @SysLog(func = "外部用户导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "设置-组织架构-成员管理")
    public ResponseResult externalUserInfoExport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                 @RequestBody UserInfoExportRequest request) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_USER_INFO);
        request.setEnterpriseId(enterpriseId);
        request.setUser(UserHolder.getUser());
        DataSourceHelper.changeToMy();
        ImportTaskDO importTaskDO = enterpriseUserService.externalUserInfoExport(request);
        return ResponseResult.success(importTaskDO);
    }

    /**
     * 更新用户信息
     *
     * @param eid
     * @param enterpriseUserAuthCopyDTO
     * @return
     */
    @PostMapping(path = "/copyUserAuth")
    public ResponseResult copyUserAuth(@PathVariable(value = "enterprise-id") String eid,
                                   @RequestBody @Validated EnterpriseUserAuthCopyDTO enterpriseUserAuthCopyDTO) {
        if (Constants.BATCH_INSERT_COUNT <= enterpriseUserAuthCopyDTO.getUserIds().size()) {
            return ResponseResult.fail(ErrorCodeEnum.USER_MAX_NUM);
        }
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        enterpriseUserService.copyUserAuth(eid, enterpriseUserAuthCopyDTO, user);
        return ResponseResult.success();
    }

    @GetMapping(path = "/getIsFirstLogin")
    public ResponseResult getIsFirstLogin(@PathVariable(value = "enterprise-id") String eid,
                                          @RequestParam(value = "loginWay")String loginWay) {
        DataSourceHelper.reset();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(enterpriseUserService.getIsFirstLogin(eid,user,loginWay));
    }

    @GetMapping("/setFirstLogin")
    public ResponseResult<Boolean> setFirstLogin(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                 @RequestParam(value = "loginWay")String loginWay) {

        String key = RedisConstant.FIRST_LOGIN +  enterpriseId + "_" + UserHolder.getUser().getUserId() + "_" + loginWay;
        //31天过期
        redisUtilPool.setString(key, DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN),3600 * 24 * 31);
        return ResponseResult.success(true);
    }

    @ApiOperation("管理员获取组织架构模块是否第一次登录")
    @GetMapping(path = "/getOrgModuleIsFirstLogin")
    public ResponseResult getOrgModuleIsFirstLogin(@PathVariable(value = "enterprise-id") String eid) {
        DataSourceHelper.reset();
        CurrentUser user = UserHolder.getUser();
        String key = MessageFormat.format(RedisConstant.OrgModuleFirstLogin, eid, user.getUserId());
        String value = redisUtilPool.getString(key);
        boolean result = StringUtils.isBlank(value);
        if(result){
            redisUtilPool.setString(key, DateUtils.convertDateTimeToString(LocalDateTime.now()));
        }
        return ResponseResult.success(result);
    }

    @GetMapping("/clearTokenByUserId")
    public ResponseResult clearTokenByUserId(@PathVariable(value = "enterprise-id") String eid, @RequestParam("userId") String userId){
        enterpriseUserService.clearTokenByUserId(eid, userId);
        return ResponseResult.success();
    }

    @GetMapping("/clearTokenByRoleId")
    public ResponseResult clearTokenByRoleId(@PathVariable(value = "enterprise-id") String eid, @RequestParam("roleId") Long roleId){
        DataSourceHelper.changeToMy();
        enterpriseUserService.clearTokenByRoleId(eid, roleId);
        return ResponseResult.success();
    }
}
