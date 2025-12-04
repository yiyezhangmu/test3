package com.coolcollege.intelligent.controller.boss.manage;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * 企业留资管理    2、获取配置（根据企业id、用户id判断是否弹框）  3、设置全局配置 4、设置指定企业
 * 5、获取用户token和用户信息的config接口
 * @author ：wxp
 * @date ：2022/8/17 11:06
 */
@Api(tags = "boss企业留资管理")
@RestController
@RequestMapping({"/boss/manage/enterpriseOpenLeaveInfo"})
public class BossEnterpriseOpenLeaveInfoController {

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;

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

    @ApiOperation("飞书企微留资配置")
    @GetMapping("/fsAndQwRetainCapital")
    public ResponseResult fsAndQwRetainCapital(@RequestParam(value = "appType") String appType,
                                               @RequestParam(value = "status") boolean status) {
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigDao.selectByAppType(appType);
        for (EnterpriseConfigDO enterpriseConfigDO : enterpriseConfigDOS) {
            String enterpriseId = enterpriseConfigDO.getEnterpriseId();
            if (status){
                redisUtilPool.hashSet(RedisConstant.LEAVE_ENTERPRISE, enterpriseId, enterpriseId, 7 * 24 * 60 * 60);
            }else {
                redisUtilPool.hashDel(RedisConstant.LEAVE_ENTERPRISE, enterpriseId);
            }
            redisUtilPool.setString("fsAndQwRetainCapital_"+appType, JSONObject.toJSONString(status));
        }
        return ResponseResult.success(Boolean.TRUE);
    }

    @ApiOperation("飞书企微留资明细")
    @GetMapping("/fsAndQwRetainCapital/detail")
    public ResponseResult fsAndQwRetainCapitalDetail() {
        String fsAndQwRetainCapital_qw2 = redisUtilPool.getString("fsAndQwRetainCapital_qw2");
        String fsAndQwRetainCapital_fei_shu = redisUtilPool.getString("fsAndQwRetainCapital_fei_shu");
        HashMap<String, String> result = new HashMap<>();
        result.put("qw2",fsAndQwRetainCapital_qw2);
        result.put("fei_shu",fsAndQwRetainCapital_fei_shu);
        return ResponseResult.success(result);
    }


}
