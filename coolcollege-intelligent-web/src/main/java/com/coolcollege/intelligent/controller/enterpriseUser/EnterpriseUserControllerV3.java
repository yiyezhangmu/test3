package com.coolcollege.intelligent.controller.enterpriseUser;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.EncryptUtil;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.model.user.*;
import com.coolcollege.intelligent.model.user.dto.UserIDDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @FileName: UserController
 * @Description: 用户相关接口
 * @date 2021-07-20 10:02
 */
@RestController
@ErrorHelper
@Slf4j
@RequestMapping("/v3/enterpriseUser")
public class EnterpriseUserControllerV3 {

    @Resource
    private EnterpriseUserService enterpriseUserService;
    @Resource
    private RedisUtilPool redisUtilPool;

    /**
     * 完善用户信息
     * @return
     */
    @PostMapping("/improveUserInfo")
    public ResponseResult improveUserInfo(@Validated @RequestBody ImproveUserInfoDTO param){
        return enterpriseUserService.improveUserInfo(param, UserHolder.getUser());
    }

    /**
     * 修改密码
     * @param param
     * @return
     */
    @PostMapping("/modifyPassword")
    public ResponseResult modifyPassword(@Validated @RequestBody ModifyPasswordDTO param){
        CurrentUser user = UserHolder.getUser();
        if(!param.getMobile().equals(user.getMobile())){
            return ResponseResult.fail(ErrorCodeEnum.MOBILE_NOT_MATCH);
        }
        return enterpriseUserService.modifyPassword(param, user.getUnionid());
    }


    @PostMapping("/initPassword")
    public ResponseResult<String> initPassword(@Validated @RequestBody InitPasswordDTO param){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return enterpriseUserService.initPassword(param, user);
    }

    @ApiOperation("根据原密码修改密码")
    @PostMapping("/modifyPasswordByOld")
    public ResponseResult modifyPasswordByOld(@Validated @RequestBody ModifyPasswordByOldDTO param) {
        CurrentUser user = UserHolder.getUser();
        if(!param.getMobile().equals(user.getMobile())){
            return ResponseResult.fail(ErrorCodeEnum.MOBILE_NOT_MATCH);
        }
        DataSourceHelper.reset();
        return enterpriseUserService.modifyPasswordByOriginalPassword(param, user.getUnionid());
    }

    @ApiOperation("校验原密码是否正确")
    @PostMapping("/verifyPassword")
    public ResponseResult verifyOriginalPassword(@Validated @RequestBody PasswordVerifyDTO param) {
        CurrentUser user = UserHolder.getUser();
        if (!param.getMobile().equals(user.getMobile())) {
            return ResponseResult.fail(ErrorCodeEnum.MOBILE_NOT_MATCH2);
        }
        DataSourceHelper.reset();
        return enterpriseUserService.verifyOriginalPassword(param.getPassword(), user.getUnionid());
    }

    /**
     * 忘记密码
     * @param param
     * @return
     */
    @PostMapping("/forgetPassword")
    public ResponseResult forgetPassword(@Validated @RequestBody ModifyPasswordDTO param){
        return enterpriseUserService.forgetPassword(param);
    }


    /**
     * 新增用户
     * @param param
     * @return
     */
    @PostMapping("/addUser")
    @SysLog(func = "添加用户", opModule = OpModuleEnum.ENTERPRISE_USER, opType = OpTypeEnum.INSERT)
    public ResponseResult addUser(@Validated @RequestBody UserAddDTO param){
        CurrentUser user = UserHolder.getUser();
        return enterpriseUserService.addUser(param, user.getEnterpriseId(), user.getDbName(), user);
    }


    @PostMapping("/deleteUser")
    public ResponseResult deleteUser(@RequestBody UserIDDTO param){
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        return enterpriseUserService.deleteUser(user.getEnterpriseId(), param.getUserIds(), user.getUserId());
    }

    /**
     * 批量修改用户状态
     * @param param
     * @return
     */
    @PostMapping("/batchUpdateUserStatus")
    @SysLog(func = "冻结/解冻", opModule = OpModuleEnum.ENTERPRISE_USER, opType = OpTypeEnum.FREEZE)
    public ResponseResult<Boolean> batchUpdateUserStatus(@Validated @RequestBody BatchUserStatusDTO param){
        CurrentUser user = UserHolder.getUser();
        return enterpriseUserService.batchUpdateUserStatus(param, user.getEnterpriseId(), user.getDbName());
    }

    @PostMapping("/batchUpdateUserRegion")
    @SysLog(func = "批量移动", opModule = OpModuleEnum.ENTERPRISE_USER, opType = OpTypeEnum.BATCH_MOVE)
    public ResponseResult<Boolean> batchUpdateUserRegion(@Validated @RequestBody BatchUserRegionMappingDTO param){
        CurrentUser user = UserHolder.getUser();
        return enterpriseUserService.batchUpdateUserRegion(param, user.getEnterpriseId(), user);
    }

    /**
     * 更新个人中心信息
     * @param param
     * @return
     */
    @PostMapping("/updateUserCenterInfo")
    public ResponseResult updateUserCenterInfo(@RequestBody UpdateUserCenterDTO param){
        CurrentUser user = UserHolder.getUser();
        return enterpriseUserService.updateUserCenterInfo(param, user);
    }

    /**
     * 修改手机号
     * @param param
     * @return
     */
    @PostMapping("/modifyUserMobile")
    public ResponseResult modifyUserMobile(@Validated @RequestBody ModifyUserMobileDTO param){
        CurrentUser user = UserHolder.getUser();
        return enterpriseUserService.modifyUserMobile(param, user);
    }

    /**
     * 邀请用户注册
     * @param param
     * @return
     */
    @PostMapping("/inviteRegister")
    public ResponseResult inviteRegister(@Validated @RequestBody InviteUserRegisterDTO param){
        return enterpriseUserService.inviteRegister(param);
    }

    /**
     * 获取邀请链接key
     * @return
     */
    @GetMapping("/getInviteUrlKey")
    public ResponseResult getInviteUrlKey(){
        String inviteKey = UUIDUtils.get32UUID();
        String unionid = UserHolder.getUser().getUnionid();
        redisUtilPool.setString(inviteKey, unionid, 24 * 60 * 60);
        return ResponseResult.success(inviteKey);
    }

    /**
     * 检查邀请链接是否有效
     * @param inviteKey
     * @return
     */
    @GetMapping("/checkInviteUrlKey")
    public ResponseResult checkInviteUrlKey(@RequestParam("inviteKey")String inviteKey){
        String inviteValue = redisUtilPool.getString(inviteKey);
        boolean isUse = StringUtils.isNotBlank(inviteValue);
        return ResponseResult.success(isUse);
    }

    @GetMapping("/getUserByToken")
    public ResponseResult<CurrentUser> getUserByToken(@RequestParam("accessToken")String accessToken){
        String user = redisUtilPool.getString(RedisConstant.ACCESS_TOKEN_PREFIX + accessToken);
        CurrentUser currentUser = JSONObject.parseObject(user, CurrentUser.class);
        return ResponseResult.success(currentUser);
    }

    @GetMapping("/getUserByMobile")
    public ResponseResult<Boolean> getUserByMobile(@RequestParam("mobile")String mobile){
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseUserService.getUserByMobile(mobile));
    }

    /**
     * 跳转不二家获取我们的免密token
     * @return
     */
    @GetMapping("/b2zg/getThirdTicket")
    public ResponseResult<Map<String, String>> getThirdTicket() {
        CurrentUser user = UserHolder.getUser();
        StringBuffer sb = new StringBuffer();
        //用户唯一标识id
        sb.append("userId=" + user.getUserId() + "").append("&");
        //企业唯一标识enterpriseId
        sb.append("cropId=" + user.getDingCorpId() + "").append("&");
        //应用的appType
        sb.append("appType=" + user.getAppType() + "").append("&");
        //加密时间
        sb.append("createTime=" + System.currentTimeMillis() + "");
        //加密
        String ticket = EncryptUtil.aesEncryp(sb.toString(), EncryptUtil.oaB2gnMd5());
        Map<String, String> result = new HashMap<>(16);
        result.put("thirdToken", ticket);
        return ResponseResult.success(result);
    }
}
