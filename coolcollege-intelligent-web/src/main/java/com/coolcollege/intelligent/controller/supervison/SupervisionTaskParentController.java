package com.coolcollege.intelligent.controller.supervison;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enums.SupervisionParentStatusEnum;
import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.supervision.dto.*;
import com.coolcollege.intelligent.model.supervision.request.AddSupervisionTaskParentRequest;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskParentRequest;
import com.coolcollege.intelligent.model.supervision.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.supervison.SupervisionTaskParentService;
import com.coolcollege.intelligent.service.supervison.open.HsStrategyCenterService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/2 11:24
 * @Version 1.0
 */
@Api(tags = "督导助手父任务定义")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/supervision/parent/")
@ErrorHelper
@Slf4j
public class SupervisionTaskParentController {

    @Resource
    SupervisionTaskParentService supervisionTaskParentService;

    @Resource
    EnterpriseConfigService enterpriseConfigService;

    @Resource
    private HsStrategyCenterService hsStrategyCenterService;



    @ApiOperation("新增督导助手父任务定义表")
    @PostMapping(path = "/addSupervisionTaskParent")
    public ResponseResult<Boolean> addSupervisionTaskParent(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                @RequestBody AddSupervisionTaskParentRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionTaskParentService.addSupervisionTaskParent(enterpriseId, user,request));
    }

    @ApiOperation("新增督导助手父任务定义表校验")
    @PostMapping(path = "/addSupervisionTaskParentCheck")
    public ResponseResult<Integer> addSupervisionTaskParentCheck(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                 @RequestBody AddSupervisionTaskParentRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionTaskParentService.checkSupervisionTaskParent(enterpriseId, user,request));
    }



    @ApiOperation("暂存督导助手父任务定义表")
    @PostMapping(path = "/stagingSupervisionTaskParent")
    public ResponseResult<Boolean> stagingSupervisionTaskParent(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                            @RequestBody AddSupervisionTaskParentRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionTaskParentService.stagingSupervisionTaskParent(enterpriseId, user,request));
    }

    @ApiOperation("查询暂存督导助手父任务定义表数据")
    @GetMapping(path = "/getStagingSupervisionTaskParent")
    public ResponseResult<SupervisionTaskParentDetailVO> getStagingSupervisionTaskParent(@PathVariable(value = "enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionTaskParentService.getStagingSupervisionTaskParent(enterpriseId, user));
    }

    @ApiOperation("任务列表")
    @PostMapping(path = "/getSupervisionTaskParentList")
    public ResponseResult<PageInfo<SupervisionTaskParentVO>> getSupervisionTaskParentList(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                                          @RequestBody SupervisionTaskParentRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(supervisionTaskParentService.getSupervisionTaskParentList(enterpriseId, request.getKeywords(),request.getStartTime(),request.getEndTime(),request.getStatusEnumList()
                ,request.getPageSize(),request.getPageNumber(),request.getSupervisionTaskPriorityEnumList(),request.getTaskGroupingList(),request.getTags()));
    }


    @ApiOperation("编辑任务")
    @PostMapping(path = "/editSupervisionTaskParent")
    public ResponseResult<Boolean> editSupervisionTaskParent(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                            @RequestBody AddSupervisionTaskParentRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionTaskParentService.editSupervisionTaskParent(enterpriseId, user,request));
    }


    @ApiOperation("任务取消")
    @GetMapping(path = "/taskCancel")
    public ResponseResult<Boolean> taskCancel(@PathVariable(value = "enterprise-id") String enterpriseId,
                                               @RequestParam(required = true)Long taskId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return ResponseResult.success(supervisionTaskParentService.taskCancel(enterpriseId, taskId,enterpriseConfigDO));
    }

    @ApiOperation("任务删除")
    @GetMapping(path = "/taskDel")
    public ResponseResult<Boolean> taskDel(@PathVariable(value = "enterprise-id") String enterpriseId,
                                              @RequestParam(required = true)Long taskId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return ResponseResult.success(supervisionTaskParentService.taskDel(enterpriseId, taskId,enterpriseConfigDO));
    }


    @ApiOperation("PC 按任务数据列表")
    @PostMapping(path = "/listSupervisionTaskByParentId")
    public ResponseResult<PageInfo<SupervisionTaskDataVO>> listSupervisionTaskByParentId(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                                         @RequestBody SupervisionDataTaskDTO supervisionDataTaskDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(supervisionTaskParentService.listSupervisionTaskByParentId(enterpriseId, supervisionDataTaskDTO.getParentId(),
                supervisionDataTaskDTO.getUserName(),supervisionDataTaskDTO.getCompleteStatusList(),supervisionDataTaskDTO.getPageSize(),supervisionDataTaskDTO.getPageNum(),supervisionDataTaskDTO.getHandleOverTimeStatus()));
    }

    @ApiOperation("PC 按门店数据列表")
    @PostMapping(path = "/listSupervisionStoreTaskByParentId")
    public ResponseResult<PageInfo<SupervisionStoreTaskDataVO>> listSupervisionStoreTaskByParentId(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                                                   @RequestBody SupervisionDataTaskDTO supervisionDataTaskDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(supervisionTaskParentService.listSupervisionStoreTaskByParentId(enterpriseId, supervisionDataTaskDTO.getParentId(),supervisionDataTaskDTO.getTaskId(),
                supervisionDataTaskDTO.getStoreIds(),supervisionDataTaskDTO.getRegionIds(),supervisionDataTaskDTO.getUserName(), supervisionDataTaskDTO.getCompleteStatusList(),supervisionDataTaskDTO.getPageSize(),
                supervisionDataTaskDTO.getPageNum(),supervisionDataTaskDTO.getHandleOverTimeStatus()));
    }


    @ApiOperation("PC 按门店数据列表导出")
    @PostMapping(path = "/listSupervisionStoreTaskByParentIdExport")
    public ResponseResult<ImportTaskDO> listSupervisionStoreTaskByParentIdExport(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                                 @RequestBody SupervisionDataTaskDTO supervisionDataTaskDTO) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionTaskParentService.listSupervisionStoreTaskByParentIdExport(enterpriseId, supervisionDataTaskDTO.getParentId(),
                supervisionDataTaskDTO.getStoreIds(),supervisionDataTaskDTO.getUserName(),supervisionDataTaskDTO.getCompleteStatusList(),user,supervisionDataTaskDTO.getTaskId(),supervisionDataTaskDTO.getRegionIds(),supervisionDataTaskDTO.getHandleOverTimeStatus()));
    }

    @ApiOperation("数据列表到导出")
    @PostMapping("/listSupervisionTaskByParentIdExport")
    public ResponseResult<ImportTaskDO> listSupervisionTaskByParentIdExport(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                            @RequestBody SupervisionDataTaskDTO supervisionDataTaskDTO){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionTaskParentService.listSupervisionTaskByParentIdExport(enterpriseId, supervisionDataTaskDTO.getParentId(),
                supervisionDataTaskDTO.getUserName(),supervisionDataTaskDTO.getCompleteStatusList(),user,supervisionDataTaskDTO.getHandleOverTimeStatus()));
    }


    @ApiOperation("督导任务-父详情")
    @GetMapping(path = "/detail")
    public ResponseResult<SupervisionTaskParentDetailVO> getSupervisionTaskParentDetail(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                                  @RequestParam(value = "parentId") Long parentId) {
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        return ResponseResult.success(supervisionTaskParentService.selectDetailById(enterpriseId, parentId,currentUser));
    }


    @ApiOperation("任务明细")
    @GetMapping(path = "/taskDetail")
    public ResponseResult<PageInfo<SupervisionStoreTaskDataVO>> taskDetail(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                            @RequestParam(value = "tbMetaTableId",required = true) Long tbMetaTableId,
                                                                            @RequestParam(value = "parentIds",required = false) List<Long> parentIds,
                                                                            @RequestParam(value = "startTimeDate",required = false) Long startTimeDate,
                                                                            @RequestParam(value = "endTimeDate",required = false) Long endTimeDate,
                                                                            @RequestParam(value = "type",required = true) String type,
                                                                            @RequestParam(value = "pageSize",required = true,defaultValue = "10") Integer pageSize,
                                                                            @RequestParam(value = "pageNum",required = true,defaultValue = "1") Integer pageNum) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(supervisionTaskParentService.taskDetail(enterpriseId, tbMetaTableId, parentIds, startTimeDate, endTimeDate,type,pageSize,pageNum));
    }


    @ApiOperation("任务明细导出")
    @GetMapping("/taskDetailExport")
    public ResponseResult<ImportTaskDO> taskDetailExport(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                         @RequestParam(value = "tbMetaTableId",required = true) Long tbMetaTableId,
                                                         @RequestParam(value = "parentIds",required = false) List<Long> parentIds,
                                                         @RequestParam(value = "startTimeDate",required = false) Long startTimeDate,
                                                         @RequestParam(value = "endTimeDate",required = false) Long endTimeDate,
                                                         @RequestParam(value = "type",required = true) String type){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(supervisionTaskParentService.taskDetailExport(enterpriseId,tbMetaTableId,parentIds,startTimeDate,endTimeDate,type,user));
    }

    @ApiOperation("执行记录")
    @GetMapping("/getSupervisionHistoryHandle")
    public ResponseResult<List<SupervisionHistoryHandleVO>> getSupervisionHistoryHandle(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                         @RequestParam(value = "taskId",required = true) Long taskId,
                                                         @RequestParam(value = "type",required = true) String type){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(supervisionTaskParentService.getSupervisionHistoryHandleVO(enterpriseId,taskId,type));
    }

    @ApiOperation("获取任务分组")
    @GetMapping("/getTaskGroups")
    public ResponseResult<List<TaskGroupDTO>> getTaskGroups(@PathVariable(value = "enterprise-id") String enterpriseId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(hsStrategyCenterService.getTaskGroups());
    }

    @ApiOperation("获取任务标签")
    @GetMapping("/getTaskLabels")
    public ResponseResult<List<TaskLabelDTO>> getTaskLabels(@PathVariable(value = "enterprise-id") String enterpriseId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(hsStrategyCenterService.getTaskLabels());
    }

    @ApiOperation("获取关联业务")
    @GetMapping("/getRelatedBusiness")
    public ResponseResult<List<RelatedBusinessDTO>> getRelatedBusiness(@PathVariable(value = "enterprise-id") String enterpriseId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(hsStrategyCenterService.getRelatedBusiness());
    }

    @ApiOperation("获取核验规则")
    @GetMapping("/getCheckRules")
    public ResponseResult<List<CheckRuleDTO>> getCheckRules(@PathVariable(value = "enterprise-id") String enterpriseId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(hsStrategyCenterService.getCheckRules());
    }

}
