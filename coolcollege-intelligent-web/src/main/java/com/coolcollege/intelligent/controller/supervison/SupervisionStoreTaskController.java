package com.coolcollege.intelligent.controller.supervison;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiUpdateSupervisionTaskDTO;
import com.coolcollege.intelligent.model.common.IdDTO;
import com.coolcollege.intelligent.model.common.IdListDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.supervision.request.*;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionApproveCountVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionApproveDataVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionStoreTaskDetailVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionStoreTaskVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.supervison.SupervisionApproveService;
import com.coolcollege.intelligent.service.supervison.SupervisionStoreTaskService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/27 20:07
 * @Version 1.0
 */
@Api(tags = "督导助手门店任务表")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/supervision/storeTask/")
@ErrorHelper
@Slf4j
public class SupervisionStoreTaskController {

    @Autowired
    SupervisionStoreTaskService supervisionStoreTaskService;
    @Resource
    EnterpriseConfigService enterpriseConfigService;

    @Resource
    SupervisionApproveService supervisionApproveService;


    @ApiOperation("督导子任务取消")
    @GetMapping(path = "/taskCancel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "父任务ID", required = false),
            @ApiImplicitParam(name = "supervisionTaskId", value = "子任务ID", required = false),
            @ApiImplicitParam(name = "id", value = "门店任务ID", required = false)
    })
    public ResponseResult<Boolean> taskCancel(@PathVariable(value = "enterprise-id") String enterpriseId,
                                              @RequestParam(name = "parentId",required = false)Long parentId,
                                              @RequestParam(name = "supervisionTaskId",required = false)Long supervisionTaskId,
                                              @RequestParam(name = "id",required = false)Long id) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return ResponseResult.success(supervisionStoreTaskService.storeTaskCancel(enterpriseId, parentId,supervisionTaskId,id,enterpriseConfigDO));
    }


    @ApiOperation("移动端去提交跳转门店列表")
    @GetMapping(path = "/getSupervisionStoreList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "按人任务ID 移动端去提交跳转门店列表传值 按门店列表查询门店不需要传", required = false),
            @ApiImplicitParam(name = "taskStatus", value = "门店任务状态", required = false),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = false),
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = false)
    })
    public ResponseResult<PageInfo<SupervisionStoreTaskDetailVO>> getSupervisionStoreList(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                             @RequestParam(name = "taskId",required = false)Long taskId,
                                                                             @RequestParam(name = "taskStatus",required = false) SupervisionSubTaskStatusEnum taskStatus,
                                                                             @RequestParam(name = "handleOverTimeStatus",required = false) Integer handleOverTimeStatus,
                                                                             @RequestParam(name = "storeName",required = false) String storeName,
                                                                             @RequestParam(name = "pageSize",required = false,defaultValue = "10")Integer pageSize,
                                                                             @RequestParam(name = "pageNum",required = false,defaultValue = "1")Integer pageNum) {
        DataSourceHelper.reset();
        CurrentUser user = UserHolder.getUser();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return ResponseResult.success(supervisionStoreTaskService.getSupervisionStoreList(enterpriseId,taskId,user.getUserId(),taskStatus,pageSize,pageNum,handleOverTimeStatus,storeName));
    }


    @ApiOperation("我的任务 门店列表")
    @PostMapping("/listMySupervisionStoreTask")
    public ResponseResult<PageInfo<SupervisionStoreTaskVO>> listMySupervisionStoreTask(@PathVariable("enterprise-id") String enterpriseId,
                                                                                  @RequestBody SupervisionStoreTaskQueryRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        request.setSupervisionUserId(user.getUserId());
        return ResponseResult.success(supervisionStoreTaskService.listMySupervisionStoreTask(enterpriseId, request));
    }


    @ApiOperation("督导任务按门店-详情")
    @GetMapping(path = "/storeTaskDetail")
    public ResponseResult<SupervisionStoreTaskVO> storeTaskDetail(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                   @RequestParam(value = "id") Long id) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(supervisionStoreTaskService.getSupervisionStoreTaskDetail(enterpriseId, id));
    }


    @ApiOperation("门店列表中间页面")
    @GetMapping(path = "/supervisionStoreList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = false),
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = false)
    })
    public ResponseResult<PageInfo<SupervisionStoreTaskDetailVO>> supervisionStoreList(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                             @RequestParam(name = "pageSize",required = false,defaultValue = "10")Integer pageSize,
                                                                             @RequestParam(name = "pageNum",required = false,defaultValue = "1")Integer pageNum) {
        DataSourceHelper.reset();
        CurrentUser user = UserHolder.getUser();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return ResponseResult.success(supervisionStoreTaskService.supervisionStoreList(enterpriseId,user.getUserId(),pageSize,pageNum));
    }


    @ApiOperation("沪上回调门店任务")
    @PostMapping(path = "/batchUpdate")
    public ResponseResult batchUpdateSupervisionTaskStatus(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                           @RequestBody @Valid OpenApiUpdateSupervisionTaskDTO dto) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        supervisionStoreTaskService.batchUpdateSupervisionStoreTaskStatus(enterpriseId, dto);
        return ResponseResult.success(true);
    }

    @ApiOperation("门店任务确认完成")
    @GetMapping(path = "/storeTaskConfirmFinish")
    public ResponseResult confirmSupervisionTask(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                 @RequestParam Long id) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionStoreTaskService.confirmSupervisionTask(enterpriseId,id,user));
    }

    @ApiOperation("门店任务确认完成")
    @PostMapping(path = "/storeTaskBatchConfirmFinish")
    public ResponseResult storeTaskBatchConfirmFinish(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                 @RequestBody @Validated IdListDTO idListDTO) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionStoreTaskService.storeTaskBatchConfirmFinish(enterpriseId,idListDTO.getIdList(),user));
    }


    @ApiOperation("执行人任务转交")
    @PostMapping("/supervisionTransfer")
    public ResponseResult<Boolean> supervisionTransfer(@PathVariable("enterprise-id") String enterpriseId,
                                                       @RequestBody SupervisionTransferRequest request) {
        DataSourceHelper.changeToMy();
        supervisionStoreTaskService.supervisionTransfer(enterpriseId, request, UserHolder.getUser());
        return ResponseResult.success(Boolean.TRUE);
    }


    @ApiOperation("查看是否有审批数据")
    @GetMapping(path = "/getApproveData")
    public ResponseResult<Boolean> getApproveData(@PathVariable(value = "enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionApproveService.getApproveData(enterpriseId,user));
    }


    @ApiOperation("查看按人按店需要审批的数量")
    @GetMapping(path = "/getApproveCount")
    public ResponseResult<SupervisionApproveCountVO> getApproveCount(@PathVariable(value = "enterprise-id") String enterpriseId,@RequestParam String taskName) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionApproveService.getApproveCount(enterpriseId,user,taskName));
    }


    @ApiOperation("查询按人任务审批数据")
    @GetMapping(path = "/getSupervisionTaskApproveData")
    public ResponseResult<PageInfo<SupervisionApproveDataVO>> getSupervisionTaskApproveData(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                                            @RequestParam(name = "taskName",required = false) String taskName,
                                                                                            @RequestParam(name = "pageSize",required = false,defaultValue = "10")Integer pageSize,
                                                                                            @RequestParam(name = "pageNum",required = false,defaultValue = "1")Integer pageNum) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionApproveService.getSupervisionTaskApproveData(enterpriseId,taskName,"person",user,pageSize,pageNum));
    }


    @ApiOperation("查询按店任务审批数据")
    @GetMapping(path = "/getSupervisionStoreTaskApproveData")
    public ResponseResult<PageInfo<SupervisionApproveDataVO>> getSupervisionStoreTaskApproveData(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                                                 @RequestParam(name = "taskName",required = false) String taskName,
                                                                                                 @RequestParam(name = "pageSize",required = false,defaultValue = "10")Integer pageSize,
                                                                                                 @RequestParam(name = "pageNum",required = false,defaultValue = "1")Integer pageNum) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionApproveService.getSupervisionStoreTaskApproveData(enterpriseId,taskName,"store",user,pageSize,pageNum));
    }

    @ApiOperation("审批任务转交")
    @PostMapping("/supervisionApproveTaskTransfer")
    public ResponseResult<Boolean> supervisionApproveTaskTransfer(@PathVariable("enterprise-id") String enterpriseId,
                                                                  @RequestBody SupervisionApproveTaskTransferRequest request) {
        DataSourceHelper.changeToMy();
        supervisionStoreTaskService.supervisionApproveTaskTransfer(enterpriseId, request, UserHolder.getUser());
        return ResponseResult.success(Boolean.TRUE);
    }

    @ApiOperation("任务审批")
    @PostMapping("/supervisionApprove")
    public ResponseResult<Boolean> supervisionApprove(@PathVariable("enterprise-id") String enterpriseId,
                                                      @RequestBody SupervisionApproveRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionStoreTaskService.supervisionApprove(enterpriseId,request,user));
    }

    @ApiOperation("待审批获取催办人员(催办)")
    @PostMapping(path = "/getSupervisionApproveUserList")
    public ResponseResult<List<PersonDTO>> getSupervisionApproveUserList(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                         @RequestBody SupervisionApproveUserRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionStoreTaskService.getSupervisionApproveUserList(enterpriseId, request, user));
    }
}
