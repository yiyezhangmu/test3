package com.coolcollege.intelligent.controller.patrolstore;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.page.PageBaseRequest;
import com.coolcollege.intelligent.model.page.PageRequest;
import com.coolcollege.intelligent.model.patrolstore.request.*;
import com.coolcollege.intelligent.model.patrolstore.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.patrolstore.PatrolPlanService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.PARAMS_VALIDATE_ERROR;

/**
 * @author zhangchenbiao
 * @FileName: PatrolPlanController
 * @Description:
 * @date 2024-09-04 11:34
 */
@Api(tags = "行事历")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/patrol/plan")
public class PatrolPlanController {

    @Resource
    private PatrolPlanService patrolPlanService;

    @ApiOperation("计划列表")
    @PostMapping("/page")
    public ResponseResult<PageInfo<PatrolPlanPageVO>> getPatrolPlanPage(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody PatrolPlanPageRequest param){
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        param.setUserId(currentUser.getUserId());
        return ResponseResult.success(patrolPlanService.getPatrolPlanPage(enterpriseId, param));
    }

    @ApiOperation("创建计划")
    @PostMapping("/add")
    public ResponseResult<Long> addPatrolPlan(@PathVariable(value = "enterprise-id") String enterpriseId,
                                              @Validated @RequestBody AddPatrolPlanRequest param,
                                              BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseResult.fail(PARAMS_VALIDATE_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(patrolPlanService.addPatrolPlan(enterpriseId, param, currentUser));
    }

    @ApiOperation("编辑计划")
    @PostMapping("/update")
    public ResponseResult<Boolean> updatePatrolPlan(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                    @Validated @RequestBody UpdatePatrolPlanRequest param,
                                                    BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseResult.fail(PARAMS_VALIDATE_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(patrolPlanService.updatePatrolPlan(enterpriseId, currentUser, param));
    }

    @ApiOperation("填写备注")
    @PostMapping("/updateRemark")
    public ResponseResult<Boolean> updatePatrolPlanRemark(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                          @Validated @RequestBody UpdatePatrolPlanRemarkRequest param,
                                                          BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseResult.fail(PARAMS_VALIDATE_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(patrolPlanService.updatePatrolPlanRemark(enterpriseId, currentUser, param));
    }

    @ApiOperation("获取计划详情")
    @GetMapping("/detail")
    public ResponseResult<PatrolPlanDetailVO> getPatrolPlanDetail(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestParam("planId") Long planId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolPlanService.getPatrolPlanDetail(enterpriseId, planId));
    }

    @ApiOperation("删除计划")
    @PostMapping("/delete")
    public ResponseResult<Integer> deletePatrolPlan(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                    @Validated @RequestBody DeletePatrolPlanRequest param,
                                                    BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseResult.fail(PARAMS_VALIDATE_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        return ResponseResult.success(patrolPlanService.deletePatrolPlan(enterpriseId, userId, param.getPlanId()));
    }

    @ApiOperation("获取我的计划详情")
    @GetMapping("/getMyPatrolPlanMonthDetail")
    public ResponseResult<PatrolPlanDetailVO> getMyPatrolPlanMonthDetail(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestParam("planMonth") String planMonth){
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        return ResponseResult.success(patrolPlanService.getMyPatrolPlanMonthDetail(enterpriseId, userId, planMonth));
    }

    @ApiOperation("获取我的计划列表")
    @PostMapping("/getMyPatrolPlanList")
    public ResponseResult<PageInfo<PatrolPlanPageVO>> getMyPatrolPlanList(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody PageBaseRequest param){
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        return ResponseResult.success(patrolPlanService.getMyPatrolPlanList(enterpriseId, userId, param));
    }

    @ApiOperation("获取我管辖的门店列表 限制100")
    @GetMapping("/getMyAuthStoreList")
    public ResponseResult<List<PatrolPlanAuthStoreVO>> getMyAuthStoreList(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestParam("metaTableId") Long metaTableId, String storeStatus,
                                                                          String storeName){
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        List<String> storeStatusList = null;
        if (StringUtils.isNotBlank(storeStatus)) {
            storeStatusList = Arrays.asList(storeStatus.split(","));
        }
        return ResponseResult.success(patrolPlanService.getMyAuthStoreList(enterpriseId, userId, metaTableId, storeStatusList, storeName));
    }

    @ApiOperation("获取行事历待办")
    @PostMapping("/getPatrolPlanToDo")
    public ResponseResult<PageInfo<PatrolPlanPageVO>> getPatrolPlanToDo(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody PageRequest param){
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        return ResponseResult.success(patrolPlanService.getPatrolPlanToDo(enterpriseId, userId, param));
    }

    @ApiOperation("行事历审批")
    @PostMapping("/auditPatrolPlan")
    public ResponseResult<Integer> auditPatrolPlan(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody @Validated AuditPatrolPlanRequest param){
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        return ResponseResult.success(patrolPlanService.auditPatrolPlan(enterpriseId, userId, param));
    }

    @ApiOperation("获取流程记录")
    @GetMapping("/getPatrolPlanProcess")
    public ResponseResult<List<PatrolPlanDealHistoryVO>> getPatrolPlanProcess(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestParam("planId") Long planId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolPlanService.getPatrolPlanProcess(enterpriseId, planId));
    }

    @ApiOperation("获取当天（本月）的待办巡店任务")
    @PostMapping("/getPatrolRecordToDo")
    public ResponseResult<PageInfo<PatrolRecordPageVO>> getPatrolRecordToDo(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody PatrolRecordTodoRequest param){
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        return ResponseResult.success(patrolPlanService.getPatrolRecordToDo(enterpriseId, userId, param));
    }

    @ApiOperation("行事历列表导出")
    @PostMapping("/exportPatrolPlan")
    public ResponseResult<ImportTaskDO> exportPatrolPlan(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody PatrolPlanPageRequest param){
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(patrolPlanService.exportPatrolPlan(enterpriseId, param, currentUser.getUserId(), currentUser.getDbName()));
    }

    @ApiOperation("行事历明细导出")
    @PostMapping("/exportPatrolPlanDetail")
    public ResponseResult<ImportTaskDO> exportPatrolPlanDetail(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody PatrolPlanPageRequest param){
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(patrolPlanService.exportPatrolPlanDetail(enterpriseId, param, currentUser.getUserId(), currentUser.getDbName()));
    }
}
