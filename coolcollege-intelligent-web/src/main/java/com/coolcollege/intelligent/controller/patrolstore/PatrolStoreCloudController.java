package com.coolcollege.intelligent.controller.patrolstore;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.patrolstore.request.AddPatrolStoreCloudRequest;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolStoreCloudVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreCloudService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: hu hu
 * @Date: 2024/11/27 13:57
 * @Description:
 */
@Api(tags = "云图库")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/patrolStoreCloud")
public class PatrolStoreCloudController {

    @Resource
    private PatrolStoreCloudService patrolStoreCloudService;

    @ApiOperation("新增或更新云图库")
    @PostMapping("/insertOrUpdate")
    public ResponseResult<Long> insertOrUpdate(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                   @Validated @RequestBody AddPatrolStoreCloudRequest param){
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(patrolStoreCloudService.insertOrUpdate(enterpriseId, param, currentUser));
    }

    @ApiOperation("获取云图库")
    @GetMapping("/getCloudByBusinessId")
    public ResponseResult<PatrolStoreCloudVO> getCloudByBusinessId(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                   @RequestParam("businessId") Long businessId){
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(patrolStoreCloudService.getCloudByBusinessId(enterpriseId, businessId, currentUser.getUserId()));
    }

    @ApiOperation("删除云图库")
    @GetMapping("/deleteCloud")
    public ResponseResult<Integer> deleteCloud(@PathVariable(value = "enterprise-id") String enterpriseId,
                                               @RequestParam("businessId") Long businessId){
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(patrolStoreCloudService.deleteCloud(enterpriseId, businessId, currentUser.getUserId()));
    }
}
