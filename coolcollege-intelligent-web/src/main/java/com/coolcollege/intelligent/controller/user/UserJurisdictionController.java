package com.coolcollege.intelligent.controller.user;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.user.dto.DynamicRegionDTO;
import com.coolcollege.intelligent.model.user.dto.UserJurisdictionDTO;
import com.coolcollege.intelligent.service.user.UserJurisdictionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/7/19 13:53
 */
@Api(tags = "用户权限")
@RestController
@ErrorHelper
@RequestMapping("/v3/enterprises/userJurisdiction")
public class UserJurisdictionController {

    @Resource
    private UserJurisdictionService userJurisdictionService;
    @ApiOperation("更新所有企业所有人的管理门店")
    @GetMapping("/updateAllUserJurisdictionStore")
    public ResponseResult<Boolean> updateAllUserJurisdictionStore(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds){
        userJurisdictionService.updateAllUserJurisdictionStore(enterpriseIds);
        return ResponseResult.success(Boolean.TRUE);
    }

    @ApiOperation("更新指定企业人员的管理门店")
    @GetMapping("/updateUserJurisdictionStore")
    public ResponseResult<Boolean> updateUserJurisdictionStore(@RequestParam("enterpriseId") String eId){
        userJurisdictionService.updateUserJurisdictionStore(eId);
        return ResponseResult.success(Boolean.TRUE);
    }

    @ApiOperation("获取用户权限范围信息")
    @GetMapping("/checkUserJurisdiction")
    public ResponseResult<UserJurisdictionDTO> getUserJurisdiction(@RequestParam("enterpriseId") String eid, @RequestParam("userId") String userId){
        return ResponseResult.success(userJurisdictionService.getUserJurisdiction(eid,userId));

    }

    /**
     * 提供拼接动态参数的接口给前端
     * ?appParam=[{"appId":127572,"name":"管辖范围参数","value":["二级区域id","三级区域id"]}]
     * @param enterpriseId
     * @param userId
     * @return
     */
    @ApiOperation("获取动态区域参数")
    @GetMapping("/getDynamicRegionParam")
    public ResponseResult<List<DynamicRegionDTO>> getDynamicRegionParam(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("userId") String userId){
        return ResponseResult.success(userJurisdictionService.getDynamicRegionParam(enterpriseId, userId));
    }

}
