package com.coolcollege.intelligent.controller.boss.manage;

import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.IpUtil;
import com.coolcollege.intelligent.dao.boss.dao.BossUserLoginRecordDao;
import com.coolcollege.intelligent.model.boss.BossUserLoginRecordDO;
import com.coolcollege.intelligent.model.system.BossUserDO;
import com.coolcollege.intelligent.model.system.dto.BossLoginUserDTO;
import com.coolcollege.intelligent.model.system.request.SysLoginRequest;
import com.coolcollege.intelligent.model.userholder.BossUserHolder;
import com.coolcollege.intelligent.service.boss.BossUserService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Date;

/**
 * 登录管理
 *
 * @author byd
 * @date 2021-01-29 14:10
 */
@RestController
@RequestMapping("/boss/manage/login")
@BaseResponse
@Slf4j
public class BossLoginController {

    @Autowired
    private BossUserService bossUserService;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Autowired
    private BossUserLoginRecordDao bossUserLoginRecordDao;

    /**
     * 管理用户登录
     *
     * @param sysLoginRequest
     * @return
     */
    @PostMapping(value = "/loginIn")
    public ResponseResult loginOn(HttpServletRequest request,
                                  @RequestBody SysLoginRequest sysLoginRequest) {
        DataSourceHelper.reset();
        String password = sysLoginRequest.getPassword();
        String username = sysLoginRequest.getUsername();
        String lockKey = MessageFormat.format(RedisConstant.BOSS_ACCOUNT_LOGIN, LocalDate.now(), username);
        String errorCount = redisUtilPool.getString(lockKey);
        if(StringUtils.isNotBlank(errorCount) && Integer.valueOf(errorCount) >= 5){
            return ResponseResult.fail(ErrorCodeEnum.PASSWORD_ERROR_MAX_COUNT, errorCount);
        }
        if (StringUtils.isBlank(username)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "用户名不能为空");
        }
        if (StringUtils.isBlank(password)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "密码不能为空");
        }
        BossUserDO bossUserDO = bossUserService.getUserByUsername(username);

        if (bossUserDO == null) {
            throw new ServiceException(ErrorCodeEnum.ACCOUNT_NOT_EXIST);
        }
        //加上key
        password = MD5Util.md5(password + Constants.BOSS_PASSWORD_KEY);
        if (!password.equals(bossUserDO.getPassword())) {
            Long errorNum = redisUtilPool.incrby(lockKey, 1);
            if(errorNum == 1){
                redisUtilPool.expire(lockKey, 24 * 60 * 60);
            }
            throw new ServiceException(ErrorCodeEnum.ACCOUNT_NOT_EXIST);
        }

        if (bossUserDO.getStatus() == 1) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "用户已冻结，无法登录");
        }

        BossLoginUserDTO userLoginDTO = new BossLoginUserDTO();
        userLoginDTO.setId(bossUserDO.getId());
        userLoginDTO.setName(bossUserDO.getName());
        userLoginDTO.setUsername(bossUserDO.getUsername());
        userLoginDTO.setAvatar(bossUserDO.getAvatar());
        userLoginDTO.setUserId(bossUserDO.getUserId());

        try {
            //生成令牌
            RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
            String token = randomNumberGenerator.nextBytes().toHex();
            userLoginDTO.setToken(token);
            redisUtilPool.setString(Constants.BOSS_LOGIN_USER_KEY + token, JSON.toJSONString(userLoginDTO), Constants.ACTION_TOKEN_EXPIRE);
            //记录登陆信息：username,ip
            recordLoginDate(userLoginDTO, request);
            return ResponseResult.success(userLoginDTO);
        } catch (Exception e) {
            log.error("登录失败:", e);
        }
        throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "登录失败");
    }

    private void recordLoginDate(BossLoginUserDTO userLoginDTO, HttpServletRequest request) {
        try {
            BossUserLoginRecordDO loginRecordDO = new BossUserLoginRecordDO();
            loginRecordDO.setLoginTime(new Date());
            loginRecordDO.setUsername(userLoginDTO.getUsername());
            loginRecordDO.setName(userLoginDTO.getName());
            loginRecordDO.setIp(IpUtil.getIpAddr(request));
            bossUserLoginRecordDao.insertSelective(loginRecordDO);
        } catch (Exception e) {
            log.error("recordLoginDate error", e);
        }

    }

    /**
     * 推出登录
     *
     * @return
     */
    @PostMapping(value = "/loginOut")
    public ResponseResult loginOn() {
        BossLoginUserDTO sysUserLoginDTO = BossUserHolder.getUser();
        redisUtilPool.delKey(Constants.BOSS_LOGIN_USER_KEY + sysUserLoginDTO.getToken());
        return ResponseResult.success(null);
    }
    
    
    public static void main(String[] args) {
        System.out.println(MD5Util.md5("b20210706!a" + Constants.BOSS_PASSWORD_KEY));
    }

}
