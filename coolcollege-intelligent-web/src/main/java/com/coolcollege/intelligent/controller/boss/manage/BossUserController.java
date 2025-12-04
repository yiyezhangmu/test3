package com.coolcollege.intelligent.controller.boss.manage;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.boss.BossUserMapper;
import com.coolcollege.intelligent.model.boss.BossUserStatusUpdateDTO;
import com.coolcollege.intelligent.model.boss.dto.BossUserChangePasswordDTO;
import com.coolcollege.intelligent.model.common.IdDTO;
import com.coolcollege.intelligent.model.system.BossUserDO;
import com.coolcollege.intelligent.service.boss.BossUserService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolcollege.intelligent.common.util.MD5Util;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;

/**
 * 用户管理
 *
 * @author byd
 * @date 2021-01-28 17:10
 */
@RestController
@RequestMapping("/boss/manage/bossUser")
@BaseResponse
@Slf4j
public class BossUserController {

    @Autowired
    private BossUserService bossUserService;

    @Resource
    private BossUserMapper bossUserMapper;



    /**
     * 查询用户
     *
     * @param pageSize
     * @param pageNumber
     * @param status
     * @return
     */
    @GetMapping(value = "/list")
    public ResponseResult list(@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                               @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
                               @RequestParam(value = "status", required = false) Integer status,
                               @RequestParam(value = "username", required = false) String username) {
        DataSourceHelper.reset();
        return ResponseResult.success(bossUserService.getList(pageSize, pageNumber, username, status));
    }

    /**
     * 插入
     *
     * @param bossUserDO
     * @return
     */
    @PostMapping(value = "/saveUser")
    public ResponseResult save(@RequestBody BossUserDO bossUserDO,
                               @RequestParam(value = "appType", required = false, defaultValue = "dingding2") String appType) {
        DataSourceHelper.reset();
        return ResponseResult.success(bossUserService.insertUser(bossUserDO, appType));
    }

    /**
     * 更新
     *
     * @param bossUserDO
     * @return
     */
    @PostMapping(value = "/updateUser")
    public ResponseResult updateUser(@RequestBody BossUserDO bossUserDO,
                                     @RequestParam(value = "appType", required = false, defaultValue = "dingding2") String appType) {
        DataSourceHelper.reset();
        return ResponseResult.success(bossUserService.updateUser(bossUserDO, appType));
    }

    /**
     * 详情
     *
     * @param userId
     * @return
     */
    @GetMapping(value = "/detail/{userId}")
    public ResponseResult detail(@PathVariable Long userId) {
        DataSourceHelper.reset();
        return ResponseResult.success(bossUserService.detail(userId));
    }


    @PostMapping(value = "/update/userStatus")
    public ResponseResult updateUserStatus(@RequestBody @Validated BossUserStatusUpdateDTO param) {
        DataSourceHelper.reset();
        return ResponseResult.success(bossUserService.updateUserStatus(param));
    }

    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestBody @Validated IdDTO param) {
        DataSourceHelper.reset();
        return ResponseResult.success(bossUserService.deleteById(param.getId()));
    }

    @GetMapping(value = "/username/check")
    public ResponseResult usernameCheck(@RequestParam("username") String username) {
        DataSourceHelper.reset();
        return ResponseResult.success(bossUserService.usernameCheck(username));
    }

    @ApiOperation("boss用户管理-判断用户是否需要重新设置密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名称", required = true)
    })
    @GetMapping(value = "/changePasswordJudge")
    public ResponseResult changePasswordJudge(@RequestParam(value = "username") String username,
                                                  HttpServletResponse response) {
        DataSourceHelper.reset();
        BossUserDO bossUserDO = bossUserService.getUserByUsername(username);
        if (bossUserDO == null) {
            throw new ServiceException(ErrorCodeEnum.USER_NON_EXISTENT);
        }
        Long day = DateUtils.dayBetween(bossUserDO.getUpdateTime(), new Date());
        if (Constants.MAX_CHANGE_PASSWORD_TIME < day.intValue()) {
            return ResponseResult.success(true);
        }
        return ResponseResult.success(false);
    }

    @ApiOperation("boss用户管理-修改用户密码")
    @PostMapping(value = "/changeLoginPassword")
    public ResponseResult changeLoginPassword(@RequestBody @Valid BossUserChangePasswordDTO passwordDTO,
                                                  HttpServletResponse response) {
        DataSourceHelper.reset();
        String oldPassword = passwordDTO.getOldPassword();

        BossUserDO bossUserDO = bossUserService.getUserByUsername(passwordDTO.getUsername());
        if (bossUserDO == null) {
            throw new ServiceException(ErrorCodeEnum.USER_NON_EXISTENT);
        }
        //加上key
        oldPassword = MD5Util.md5(oldPassword + Constants.BOSS_PASSWORD_KEY);

        if (!oldPassword.equals(bossUserDO.getPassword())) {
            throw new ServiceException(ErrorCodeEnum.PASSWORD_ERROR);
        }
        String newPassword = MD5Util.md5(passwordDTO.getNewPassword() + Constants.BOSS_PASSWORD_KEY);
        bossUserDO.setPassword(newPassword);
        bossUserMapper.updateByIdSelective(bossUserDO);
        return ResponseResult.success();
    }

}
