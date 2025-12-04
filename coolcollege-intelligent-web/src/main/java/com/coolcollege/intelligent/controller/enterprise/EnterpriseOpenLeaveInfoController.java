package com.coolcollege.intelligent.controller.enterprise;

import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseOpenLeaveInfoDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseOpenLeaveInfoService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业留资管理    2、获取配置（根据企业id、用户id判断是否弹框）  3、设置全局配置 4、设置指定企业
 * 5、获取用户token和用户信息的config接口
 * @author ：wxp
 * @date ：2022/8/17 11:06
 */
@Api(tags = "企业留资管理")
@RestController
@RequestMapping("/v3/enterprise/enterpriseOpenLeaveInfo")
public class EnterpriseOpenLeaveInfoController {

    @Autowired
    private EnterpriseOpenLeaveInfoService enterpriseOpenLeaveInfoService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    public EnterpriseConfigService enterpriseConfigService;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @ApiOperation("企业留资管理-用户授权留资")
    @GetMapping("/{enterprise-id}/add")
    public ResponseResult add(@PathVariable("enterprise-id") String enterpriseId,
                              @RequestParam(value = "authCode", required = true) String authCode) {
        DataSourceHelper.reset();
        CurrentUser user = UserHolder.getUser();
        enterpriseOpenLeaveInfoService.saveEnterpriseOpenLeaveInfo(enterpriseId, authCode, user);
        return ResponseResult.success(Boolean.TRUE);
    }

    @ApiOperation("飞书企微企业留资管理-用户授权留资")
    @GetMapping("/{enterprise-id}/addByFsAndQw")
    public ResponseResult addByFsAndQw(@PathVariable("enterprise-id") String enterpriseId,
                                       @RequestParam(value = "phoneNum", required = false) String phoneNum,
                                       @RequestParam String smsCode) {
        DataSourceHelper.reset();
        CurrentUser user = UserHolder.getUser();
        enterpriseOpenLeaveInfoService.saveEnterpriseOpenLeaveInfoByFsAndQw(enterpriseId, phoneNum, user,smsCode);
        return ResponseResult.success(Boolean.TRUE);
    }

    @ApiOperation("企业留资管理-用户授权留资不需要登录")
    @GetMapping("/leaveWithNoLogin")
    public ResponseResult leaveWithNoLogin(@RequestParam(value = "corpId", required = true) String corpId,
                              @RequestParam(value = "appType", required = true) String appType,
                              @RequestParam(value = "authCode", required = true) String authCode) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByCorpId(corpId, appType);
        if(enterpriseConfig == null){
            return ResponseResult.success(false);
        }
        enterpriseOpenLeaveInfoService.saveEnterpriseOpenLeaveInfo(enterpriseConfig.getEnterpriseId(), authCode, null);
        return ResponseResult.success(Boolean.TRUE);
    }

    @ApiOperation("判断企业用户是否需要弹框授权留资  返回true表示需要留资，否则不需要")
    @GetMapping("/{enterprise-id}/checkUserLeaveInfo")
    public ResponseResult<Boolean> checkUserLeaveInfo(@PathVariable("enterprise-id") String enterpriseId){
        DataSourceHelper.reset();
        EnterpriseDO enterpriseDO = enterpriseService.selectById(enterpriseId);
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(enterpriseOpenLeaveInfoService.checkUserLeaveInfo(enterpriseId, user, enterpriseDO, enterpriseDO.getAppType()));
    }

    @ApiOperation("判断门店通企业用户是否需要弹框授权留资  返回true表示需要留资，否则不需要")
    @GetMapping("/checkUserLeaveInfoWithNoLogin")
    public ResponseResult<Boolean> checkUserLeaveInfoWithNoLogin(@RequestParam(value = "corpId", required = true) String corpId,
                                                      @RequestParam(value = "appType", required = true) String appType){
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByCorpId(corpId, appType);
        if(enterpriseConfig == null){
            return ResponseResult.success(false);
        }
        return ResponseResult.success(enterpriseOpenLeaveInfoService.checkUserLeaveInfo(enterpriseConfig.getEnterpriseId(),null, null, appType));
    }

    @ApiOperation("企业留资管理-留资开关")
    @GetMapping("/setLeaveOpen")
    public ResponseResult setLeaveOpen(@RequestParam("leaveOpen") Boolean leaveOpen){
        if(leaveOpen){
            redisUtilPool.setString(RedisConstant.LEAVE_OPEN, String.valueOf(leaveOpen));
        }else {
            redisUtilPool.delKey(RedisConstant.LEAVE_OPEN);
        }
        return ResponseResult.success(Boolean.TRUE);
    }

    @ApiOperation("企业留资管理-设置留资企业")
    @GetMapping("/setLeaveEnterprise")
    public ResponseResult setLeaveEnterprise(@RequestParam(value = "enterpriseIds") List<String> enterpriseIds) {
        for (String enterpriseId : enterpriseIds) {
            redisUtilPool.hashSet(RedisConstant.LEAVE_ENTERPRISE, enterpriseId, enterpriseId, 7 * 24 * 60 * 60);
        }
        return ResponseResult.success(Boolean.TRUE);
    }

    @ApiOperation("企业留资管理-移除留资企业")
    @GetMapping("/removeLeaveEnterprise")
    public ResponseResult removeLeaveEnterprise(@RequestParam(value = "enterpriseIds") List<String> enterpriseIds) {
        for (String enterpriseId : enterpriseIds) {
            redisUtilPool.hashDel(RedisConstant.LEAVE_ENTERPRISE, enterpriseId);
        }
        return ResponseResult.success(Boolean.TRUE);
    }

}
