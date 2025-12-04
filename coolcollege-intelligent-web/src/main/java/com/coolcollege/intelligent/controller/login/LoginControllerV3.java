package com.coolcollege.intelligent.controller.login;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.model.enums.LoginTypeEnum;
import com.coolcollege.intelligent.model.login.SwitchEnterpriseDO;
import com.coolcollege.intelligent.model.login.UserLoginDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.login.strategy.LoginStrategy;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Admin
 * @date 2021-07-16 10:09
 */
@RestController
@ErrorHelper
@Slf4j
@RequestMapping("/v3/login")
public class LoginControllerV3 {

    @Resource
    private RedisUtilPool redisUtilPool;

    /**
     * 用户登录
     * @param param
     * @return
     */
    @PostMapping("/accountLogin")
    public ResponseResult accountLogin(@RequestBody UserLoginDTO param){
        return SpringContextUtil.getBean(param.getLoginType().getClazzName(), LoginStrategy.class).login(param);
    }

    /**
     * 切换企业登录
     * @return
     */
    @PostMapping("/switchLoginEnterprise")
    @SysLog(func = "登录", opModule = OpModuleEnum.LOGIN, opType = OpTypeEnum.LOGIN)
    public ResponseResult switchLoginEnterprise(@RequestBody SwitchEnterpriseDO param){
        CurrentUser user = UserHolder.getUser();
        return SpringContextUtil.getBean(LoginTypeEnum.DEFAULT_TYPE.getClazzName(), LoginStrategy.class).switchLoginEnterprise(param, user.getMobile());
    }

    @GetMapping("/getAllEnterprise")
    public ResponseResult getAllEnterprise(){
        CurrentUser user = UserHolder.getUser();
        return SpringContextUtil.getBean(LoginTypeEnum.DEFAULT_TYPE.getClazzName(), LoginStrategy.class).getCurrentLoginUserEnterpriseList(user.getMobile());
    }

    @GetMapping("/logout")
    public ResponseResult logout(){
        CurrentUser currentUser = UserHolder.getUser();
        String accessToken = currentUser.getAccessToken();
        String key = RedisConstant.ACCESS_TOKEN_PREFIX + accessToken;
        redisUtilPool.delKey(key);
        return ResponseResult.success(true);
    }

    /**
     * 用户登录
     * @param param
     * @return
     */
    @PostMapping("/switchAccountLogin")
    public ResponseResult switchAccountLogin(@RequestBody UserLoginDTO param){
        DataSourceHelper.changeToMy();
        return SpringContextUtil.getBean(param.getLoginType().getClazzName(), LoginStrategy.class).login(param);
    }

}
