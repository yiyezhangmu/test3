package com.coolcollege.intelligent.controller.patrolstore;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.patrolstore.param.PatrolStoreBatchSubmitTableParam;
import com.coolcollege.intelligent.model.patrolstore.param.PatrolStoreSubmitTableParam;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreCheckService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author byd
 * @date 2024-09-05 14:28
 */
@Api(tags = "巡店稽核")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/patrolStore/patrolStoreCheck")
@ErrorHelper
@Slf4j
public class PatrolStoreCheckController {

    @Autowired
    private PatrolStoreCheckService patrolStoreCheckService;

    @ApiOperation("稽核巡店内容提交")
    @PostMapping(path = "/storeCheckSubmit")
    public ResponseResult storeCheckSubmit(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                           @RequestBody @Valid PatrolStoreSubmitTableParam param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreCheckService.storeCheckSubmit(enterpriseId, param, UserHolder.getUser().getUserId()));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "checkType", value = "1：大区稽核 2:战区稽核", dataType = "Integer", required = true),
    })
    @ApiOperation("是否可以稽核")
    @GetMapping(path = "/canCheck")
    public ResponseResult canCheck(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                   @RequestParam(required = true) Integer checkType) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreCheckService.canCheck(enterpriseId, UserHolder.getUser().getUserId(), checkType));
    }


    @ApiOperation("稽核巡店内容批量提交")
    @PostMapping(path = "/storeCheckBatchSubmit")
    public ResponseResult storeCheckBatchSubmit(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                @RequestBody @Valid PatrolStoreBatchSubmitTableParam param) {
        DataSourceHelper.changeToMy();
        param.getDataTableParamList().forEach(patrolStoreSubmitTableParam ->
                patrolStoreCheckService.storeCheckSubmit(enterpriseId, patrolStoreSubmitTableParam, UserHolder.getUser().getUserId()));
        return ResponseResult.success();
    }
}
