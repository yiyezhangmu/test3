package com.coolcollege.intelligent.controller.supervison;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiUpdateSupervisionTaskDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.store.dto.SingleStoreDTO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionReassignDTO;
import com.coolcollege.intelligent.model.supervision.request.*;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionReassignStoreVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionTaskVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.fileUpload.OssClientService;
import com.coolcollege.intelligent.service.supervison.SupervisionTaskService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * @author
 * @date 2023-02-01 14:15
 */
@Api(tags = "督导助手")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/supervision")
@ErrorHelper
@Slf4j
public class SupervisionTaskController {

    @Resource
    SupervisionTaskService supervisionTaskService;
    @Resource
    private EnterpriseConfigService enterpriseConfigService;
    @Resource
    OssClientService ossClientService;

    @ApiOperation("我的任务")
    @PostMapping("/listMySupervisionTask")
    public ResponseResult<PageInfo<SupervisionTaskVO>> listMySupervisionTask(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestBody SupervisionTaskQueryRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        request.setSupervisionUserId(user.getUserId());
        return ResponseResult.success(supervisionTaskService.listMySupervisionTask(enterpriseId, request));
    }

    @ApiOperation("督导任务-详情")
    @GetMapping(path = "/detail")
    public ResponseResult<SupervisionTaskVO> getSupervisionTaskDetail(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                      @RequestParam(value = "supervisionTaskId") Long supervisionTaskId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(supervisionTaskService.getSupervisionTaskDetail(enterpriseId, supervisionTaskId));
    }

    @ApiOperation("提交固定表单")
    @PostMapping(path = "/submitForm")
    public ResponseResult submitSupervisionTask(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                @RequestBody @Valid SupervisionTaskHandleRequest request) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionTaskService.submitSupervisionTask(enterpriseId, request, user, enterpriseConfigDO));
    }

    @ApiOperation("提交自定义表单")
    @PostMapping(path = "/submitFormByFormId")
    public ResponseResult submitFormByFormId(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                @RequestBody @Valid SupervisionDefDataRequest request) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionTaskService.submitSupervisionTaskByFormId(enterpriseId, request, user, enterpriseConfigDO));
    }


    @ApiOperation("确认完成")
    @PostMapping(path = "/confirmFinish")
    public ResponseResult confirmSupervisionTask(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                @RequestBody SupervisionTaskHandleRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionTaskService.confirmSupervisionTask(enterpriseId, request, user));
    }


    @ApiOperation("沪上回调")
    @PostMapping(path = "/batchUpdate")
    public ResponseResult batchUpdateSupervisionTaskStatus(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                     @RequestBody @Valid OpenApiUpdateSupervisionTaskDTO dto) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        supervisionTaskService.batchUpdateSupervisionTaskStatus(enterpriseId, dto);
        return ResponseResult.success(true);
    }


    @ApiOperation("督导子任务取消")
    @GetMapping(path = "/taskCancel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "父任务ID", required = false),
            @ApiImplicitParam(name = "id", value = "子任务ID", required = false)
    })
    public ResponseResult<Boolean> taskCancel(@PathVariable(value = "enterprise-id") String enterpriseId,
                                              @RequestParam(name = "taskId",required = false)Long taskId,
                                              @RequestParam(name = "id",required = false)Long id) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return ResponseResult.success(supervisionTaskService.taskCancel(enterpriseId, taskId,id,enterpriseConfigDO));
    }



    @ApiOperation("重新分配")
    @PostMapping("/supervisionReassign")
    public ResponseResult<Boolean> supervisionReassign(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                       @RequestBody SupervisionReassignDTO supervisionReassignDTO){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionTaskService.supervisionReassign(enterpriseId,user,supervisionReassignDTO));
    }


    @ApiOperation("重新分配门店列表")
    @GetMapping("/getSupervisionReassignStore")
    public ResponseResult<PageInfo<SupervisionReassignStoreVO>> getSupervisionReassignStore(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                                            @RequestParam(name = "taskId",required = false)Long taskId,
                                                                                            @RequestParam(name = "storeName",required = false)String storeName,
                                                                                            @RequestParam(name = "pageSize",required = false)Integer pageSize,
                                                                                            @RequestParam(name = "pageNum",required = false)Integer pageNum){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(supervisionTaskService.getSupervisionReassignStore(enterpriseId,taskId,storeName,pageSize,pageNum));
    }



    @GetMapping("/previewFileUrl")
    public ResponseResult detail(@PathVariable("enterprise-id") String enterpriseId,
                                 @RequestParam("fileUrl") String fileUrl) {
        return ResponseResult.success(ossClientService.getPreviewUrl(fileUrl));
    }
}
