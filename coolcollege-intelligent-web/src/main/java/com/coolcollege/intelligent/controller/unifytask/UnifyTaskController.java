package com.coolcollege.intelligent.controller.unifytask;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.facade.UnifyTaskFcade;
import com.coolcollege.intelligent.model.achievement.dto.AchievementTaskRecordDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.store.dto.BasicsStoreDTO;
import com.coolcollege.intelligent.model.store.dto.StoreSignInMapDTO;
import com.coolcollege.intelligent.model.store.queryDto.NearbyStoreRequest;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskPersonDO;
import com.coolcollege.intelligent.model.unifytask.dto.*;
import com.coolcollege.intelligent.model.unifytask.query.*;
import com.coolcollege.intelligent.model.unifytask.request.BuildByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.request.GetMiddlePageDataByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.request.GetTaskByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.request.GetTaskDetailByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.vo.GetMiddlePageDataByPersonVO;
import com.coolcollege.intelligent.model.unifytask.vo.GetTaskByPersonVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.achievement.AchievementTaskRecordService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseStoreCheckSettingService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskDisplayService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskPersonService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.model.enums.TaskTypeEnum.PRODUCT_FEEDBACK;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/27 20:19
 */
@Api(tags = "任务相关")
@RestController
@RequestMapping({"/v2/enterprises/{enterprise-id}/unifytask",
        "/v3/enterprises/{enterprise-id}/unifytask"})
@BaseResponse
@Slf4j
public class UnifyTaskController {

    @Autowired
    private UnifyTaskDisplayService unifyTaskDisplayService;
    @Autowired
    private UnifyTaskService unifyTaskService;
    @Autowired
    @Lazy
    private UnifyTaskFcade unifyTaskFcade;
    @Resource
    private TaskParentMapper taskParentMapper;
    @Autowired
    private EnterpriseStoreCheckSettingService enterpriseStoreCheckSettingService;
    @Resource
    private EnterpriseConfigMapper configMapper;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Autowired
    private UnifyTaskPersonService unifyTaskPersonService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private AchievementTaskRecordService achievementTaskRecordService;

    /**
     * 创建任务
     * @param enterpriseId
     * @param task
     * @return
     */
    @PostMapping(path = "/build")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_ADD, operateDesc = "新增任务")
    @SysLog(func = "新增任务", opModule = OpModuleEnum.UNIFY_TASK, opType = OpTypeEnum.INSERT)
    public ResponseResult buildParentTask(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                           @RequestBody @Validated UnifyTaskBuildDTO task) {
        log.info("#build body is ={}", JSON.toJSONString(task));
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        long createTime = System.currentTimeMillis();
        if (Objects.nonNull(task.isOverdueTaskContinue()) && task.isOverdueTaskContinue() == true){
            task.setIsOperateOverdue(1);
        }else if (Objects.nonNull(task.isOverdueTaskContinue()) && task.isOverdueTaskContinue() == false){
            task.setIsOperateOverdue(0);
        }
        if(TaskTypeEnum.QUESTION_ORDER.getCode().equals(task.getTaskType())){
            throw new ServiceException(ErrorCodeEnum.TASK_QUESTION_CREATE_ERROR);
        }
        try {
            if (task.getTaskType().equals(PRODUCT_FEEDBACK.getCode())){
                List<GeneralDTO> generalDTOS = unifyTaskService.productDeal(enterpriseId, task);
                List<GeneralDTO> store = task.getStoreIds().stream().filter(o -> o.getType().equals("store")).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(store) && store.size()>0){
                    generalDTOS.addAll(store);
                }
                task.setStoreIds(generalDTOS);
            }
        }catch (Exception e){
            log.info(e.getMessage());
        }
        Long taskId = unifyTaskFcade.insertUnifyTask(enterpriseId, task, user, createTime);

        //判断是否可以提示
        return ResponseResult.success(unifyTaskService.sendTaskToDayJudge(enterpriseId, taskId));
    }


    /**
     * 任务补发
     * @param enterpriseId
     * @return
     */
    @PostMapping(path = "/reissue")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_ADD, operateDesc = "补发子任务")
    public ResponseResult reissueSubTask(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                         @RequestParam(value = "taskId", required = true) Long taskId,
                                         @RequestParam(value = "storeIds") String storeIds,
                                         @RequestParam(value = "loopCount", required = true) Long loopCount) {
        DataSourceHelper.changeToMy();
        //前端反馈没地方调用  2025-11-11先注释 一段时间后删除
        //unifyTaskService.reissueSubTask(enterpriseId, taskId, storeIds, loopCount);
        return ResponseResult.success(true);
    }

    /**
     * 定时任务  slb返回503问题 目前的方案就是 做好补漏   批量任务补发
     * @param enterpriseId
     * @return
     */
    @PostMapping(path = "/batchReissue")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_ADD, operateDesc = "批量补发父任务")
    public ResponseResult batchReissue(@PathVariable(value = "enterprise-id", required = true) String enterpriseId,
                                       @RequestParam(value = "taskIdStr", required = true) String taskIdStr) {

        log.info("#batchReissue enterpriseId={}, taskIdStr={}", enterpriseId , taskIdStr);
        // 切换数据源
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());

        List<String> taskIdList = StrUtil.splitTrim(taskIdStr, ",");
        for (String taskId : taskIdList) {
            ResponseResult TRUE = unifyTaskService.getResponseResult(enterpriseId, taskId);
            if (TRUE != null) {
                continue;
            }
            unifyTaskFcade.schedulerTask(enterpriseId, Long.valueOf(taskId), enterpriseConfigDO.getDbName(),null, false);
        }
        return ResponseResult.success(true);
    }

    /**
     * 父任务列表
     * @param enterpriseId
     * @param query
     * @return
     */
    @ApiOperation(value = "父任务列表", notes = "1.nodeType:我创建的create,我收到的/抄送我的cc,(新)我处理的approval")
    @PostMapping(path = "/parent/list")
    public ResponseResult getDisplayParent(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                           @RequestBody @Validated DisplayQuery query) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(unifyTaskDisplayService.getDisplayParent(enterpriseId, query, user));
    }


    /**
     * 父任务跳中间页
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping(path = "/parent/getParentMiddlePageData")
    public ResponseResult getParentMiddlePageData(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                           @RequestBody @Validated TbDisplayQuery query) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(unifyTaskDisplayService.getParentMiddlePageData(enterpriseId, query, user));
    }

    /**
     * 子任务列表
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping(path = "/sub/list")
    public ResponseResult getDisplaySub(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                        @RequestBody @Validated DisplayQuery query) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(unifyTaskDisplayService.getDisplaySub(enterpriseId, query, user));
    }

    /**
     *详情
     * @param enterpriseId
     * @param taskId
     * @return
     */
    @GetMapping(path = "/parent/detail")
    public ResponseResult getDisplayParentDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                              @RequestParam(value = "taskId") Long taskId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskDisplayService.getDisplayParentDetail(enterpriseId, taskId));
    }


    /**
     *详情
     * @param enterpriseId
     * @param taskId
     * @return
     */
    @GetMapping(path = "/parent/detail/storeScope/input")
    public ResponseResult<List<BasicsStoreDTO>> getStoreScopeInput(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                   @RequestParam(value = "taskId") Long taskId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskDisplayService.getStoreScopeInput(enterpriseId, taskId));
    }

    /**
     *详情
     * @param enterpriseId
     * @param query
     * @return
     */
    @GetMapping(path = "/sub/detail")
    public ResponseResult getDisplaySubDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                              SubQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskDisplayService.getDisplaySubDetail(enterpriseId, query.getSubTaskId(), UserHolder.getUser().getUserId(), query.getTaskStoreId()));
    }

    @GetMapping(path = "/getSubOperHistoryData")
    public ResponseResult getSubOperHistoryData(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                              SubQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskDisplayService.getSubOperHistoryData(enterpriseId, query.getSubTaskId()));
    }

    /**
     * 通过父任务id查详情
     * @param enterpriseId
     * @param taskId
     * @return
     */
    @GetMapping(path = "/sub/detailByParentId")
    public ResponseResult getDetailByParentId(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                              @RequestParam(value = "taskId") Long taskId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskDisplayService.getDetailByParentId(enterpriseId, taskId, UserHolder.getUser()));
    }

    @PostMapping(path = "/sub/detail/batch")
    public ResponseResult getDisplayBatchSubDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                   @RequestBody List<Long> subTaskIdList) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskDisplayService.getDisplayBatchSubDetail(enterpriseId, subTaskIdList, UserHolder.getUser().getUserId(), null));
    }

    @PostMapping(path = "/taskStore/detail/batch")
    public ResponseResult getDisplayBatchTaskStoreDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                   @RequestBody List<Long> taskStoreIdList) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskDisplayService.getDisplayBatchSubDetail(enterpriseId, null, UserHolder.getUser().getUserId(), taskStoreIdList));
    }

    /**
     * 父任务删除
     *
     * @param enterpriseId
     * @param unifyTaskIdList
     * @return
     */
    @ApiOperation("删除任务")
    @PostMapping(path = "/del/batch")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除任务")
    @SysLog(func = "删除任务", opModule = OpModuleEnum.UNIFY_TASK, opType = OpTypeEnum.DELETE, preprocess = true)
    public ResponseResult batchDel(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
        @RequestBody List<Long> unifyTaskIdList) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        unifyTaskService.batchDelUnifyTask(enterpriseId, unifyTaskIdList, enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType());
        return ResponseResult.success(true);
    }

    /**
     * 任务编辑
     * @param enterpriseId
     * @param task
     * @return
     */
    @PostMapping(path = "/change")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "编辑任务")
    @SysLog(func = "编辑任务", opModule = OpModuleEnum.UNIFY_TASK, opType = OpTypeEnum.EDIT)
    public ResponseResult changeParentTask(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          @RequestParam Long taskId,
                                          @RequestBody @Validated UnifyTaskBuildDTO task) {
        if(log.isInfoEnabled()){
            log.info("#change body is ={}", JSON.toJSONString(task));
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());;
        //是否直接返回
        TaskParentDO parentDO = unifyTaskService.changeUnifyTask(enterpriseId, taskId, task, user, enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType());

        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, taskId);
        if(taskParentDO != null){
            return ResponseResult.success(unifyTaskService.sendTaskToDayJudge(enterpriseId, taskId));
        }
        Long newTaskId = unifyTaskFcade.insertUnifyTask(enterpriseId, task, user, parentDO.getCreateTime());
        return ResponseResult.success(unifyTaskService.sendTaskToDayJudge(enterpriseId, newTaskId));
    }
    /**
     * 任务转交
     * @param enterpriseId
     * @param task
     * @return
     */
    @PostMapping(path = "/turn")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "转交任务")
    public ResponseResult turnTask(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                           @RequestBody @Validated UnifyTaskTurnDTO task) {
        log.info("#turn body is ={}", JSON.toJSONString(task));
        DataSourceHelper.changeToMy();
        unifyTaskService.turnTask(enterpriseId, task, UserHolder.getUser());
        return ResponseResult.success(true);
    }

    /**
     * 任务转交
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    @GetMapping(path = "/hasCheckTableAuth")
    public ResponseResult hasCheckTableAuth(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                   @RequestParam("unifyTaskId") Long unifyTaskId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskService.hasCheckTableAuth(enterpriseId, unifyTaskId, UserHolder.getUser().getUserId()));
    }

    /**
     * 批量新增任务
     * @param enterpriseId
     * @param buildDTO
     * @return
     */
    @PostMapping("batchBuild")
    public ResponseResult batchBuild(@PathVariable("enterprise-id") String enterpriseId, @RequestBody @Validated BatchBuildDTO buildDTO){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        long createTime = System.currentTimeMillis();
        buildDTO.getTaskList().forEach(data ->{
                    try{
                        if(TaskTypeEnum.QUESTION_ORDER.getCode().equals(data.getTaskType())){
                            throw new ServiceException(ErrorCodeEnum.TASK_QUESTION_CREATE_ERROR);
                        }
                        unifyTaskFcade.insertUnifyTask(enterpriseId, data, user, createTime);
                    }catch (Exception e){
                        log.error("创建任务失败,任务信息{}", JSONUtil.toJsonStr(data),e);
                    }
                }
        );
        return ResponseResult.success(Boolean.TRUE);

    }


    /**
     * 父任务列表(新)
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping(path = "/parent/taskList")
    public ResponseResult getParenTaskList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                           @RequestBody @Validated DisplayQuery query) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(unifyTaskDisplayService.getDisplayParent(enterpriseId, query, user));
    }

    /**
     * 父任务状态统计
     * @param enterpriseId
     * @return
     */
    @GetMapping(path = "/parent/count")
    public ResponseResult getParentCount(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                         @Param("taskType") String taskType) {
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO settingDO = enterpriseStoreCheckSettingService.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskService.getParentCount(enterpriseId, taskType, settingDO.getOverdueTaskContinue()));
    }

    /**
     * 查询巡店父任务列表
     * @param enterpriseId
     * @return
     */
    @PostMapping(path = "/parent/selectParentTaskList")
    public ResponseResult selectParentTaskList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                               @RequestBody TaskParentQuery taskParentQuery) {
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO settingDO = enterpriseStoreCheckSettingService.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToMy();
        taskParentQuery.setOverdueTaskContinue(settingDO.getOverdueTaskContinue());
        return ResponseResult.success(unifyTaskService.selectParentTaskList(enterpriseId, UserHolder.getUser().getUserId(), taskParentQuery));
    }

    /**
     * 发起今日任务
     * @param enterpriseId
     * @return
     */
    @PostMapping(path = "/parent/sendTodayTask")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_ADD, operateDesc = "发起今日任务")
    public ResponseResult sendTodayTask(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                        @RequestParam("taskId") Long taskId,
                                        @RequestParam(value = "overdueTaskContinue", required = false) String overdueTaskContinue) {
        DataSourceHelper.changeToMy();
        int flag = Constants.ZERO;
        if (Constants.TRUE.equals(overdueTaskContinue)){
            flag = Constants.ONE;
        }
        unifyTaskService.setSchedulerForOnce(enterpriseId, taskId, new Date(), flag);
        return ResponseResult.success(null);
    }

    /**
     * 阶段任务通知
     * @param enterpriseId
     * @return
     */
    @PostMapping(path = "/stage/notice")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "发送通知")
    public ResponseResult stageNotice(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                      @RequestParam("unifyTaskId") Long unifyTaskId,  @RequestParam("loopCount") Long loopCount) {
        DataSourceHelper.changeToMy();
        unifyTaskService.sendDingNotice(enterpriseId, unifyTaskId, loopCount);
        return ResponseResult.success(null);
    }


    /**
     * 门店任务转交
     * @param enterpriseId
     * @param task
     * @return
     */
    @PostMapping(path = "/turnStoreTask")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "转交门店任务")
    public ResponseResult turnStoreTask(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                   @RequestBody @Validated UnifyStoreTaskTurnDTO task) {
        log.info("#turn body is ={}", JSON.toJSONString(task));
        DataSourceHelper.changeToMy();
        unifyTaskService.turnStoreTask(enterpriseId, task, UserHolder.getUser());
        return ResponseResult.success(true);
    }

//    /**
//     * 门店任务批量转交
//     * @param enterpriseId
//     * @param tasks
//     * @return
//     */
//    @PostMapping(path = "/turnStoreTasks")
//    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "转交门店任务")
//    public ResponseResult turnStoreTasks(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
//                                        @RequestBody @Validated List<UnifyStoreTaskTurnDTO> tasks) {
//        log.info("#turn body is ={}", JSON.toJSONString(tasks));
//        DataSourceHelper.changeToMy();
//        unifyTaskService.turnStoreTasks(enterpriseId, tasks, UserHolder.getUser());
//        return ResponseResult.success(true);
//    }

    /**
     * 批量门店任务转交
     * @param enterpriseId
     * @param task
     * @return
     */
    @ApiOperation("批量门店任务转交")
    @PostMapping(path = "/batchTurnStoreTask")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "转交门店任务")
    public ResponseResult<List<UnifyStoreTaskBatchErrorDTO>> batchTurnStoreTask(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                        @RequestBody @Validated UnifyStoreTaskBatchTurnDTO task) {
        log.info("#batchTurnStoreTask body is ={}", JSON.toJSONString(task));
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskService.batchTurnStoreTask(enterpriseId, task, UserHolder.getUser()));
    }


    /**
     * 任务重新分配
     * @param enterpriseId
     * @param task
     * @return
     */
    @PostMapping(path = "/reallocateStoreTask")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "任务重新分配")
    @SysLog(func = "详情", subFunc = "重新分配", opModule = OpModuleEnum.UNIFY_TASK, opType = OpTypeEnum.REALLOCATE)
    public ResponseResult redistributeStoreTask(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                        @RequestBody @Validated ReallocateStoreTaskDTO task) {
        log.info("#reallocateStoreTask body is ={}", JSON.toJSONString(task));

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        unifyTaskService.reallocateStoreTask(enterpriseId, task, enterpriseConfigDO.getDingCorpId(), UserHolder.getUser(),enterpriseConfigDO.getAppType(), Boolean.FALSE);
        return ResponseResult.success(true);
    }


    /**
     * 批量任务重新分配
     * @param enterpriseId
     * @param task
     * @return
     */
    @ApiOperation("批量任务重新分配")
    @PostMapping(path = "/batchReallocateStoreTask")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "批量任务重新分配")
    @SysLog(func = "详情", subFunc = "重新分配", opModule = OpModuleEnum.UNIFY_TASK, opType = OpTypeEnum.REALLOCATE)
    public ResponseResult<List<UnifyStoreTaskBatchErrorDTO>> batchReallocateStoreTask(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                                      @RequestBody ReallocateStoreTaskListDTO task) {
        log.info("#batchReallocateStoreTask body is ={}", JSON.toJSONString(task));

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskService.batchReallocateStoreTask(enterpriseId, task, enterpriseConfigDO.getDingCorpId(), UserHolder.getUser(), enterpriseConfigDO.getAppType()));
    }


    /**
     * 设置分享参数key（7天有效)
     */
    @ApiOperation("设置分享参数key（7天有效)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessId", value = "业务id", required = true),
            @ApiImplicitParam(name = "key", value = "分享key", required = true)
    })
    @GetMapping(path = "/taskShareExpire")
    public ResponseResult<Boolean> recordInfoShare(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                   @RequestParam Long businessId, @RequestParam String key) {
        redisUtilPool.setString(UnifyTaskConstant.TASK_SHARE + key, String.valueOf(businessId), 7 * 24 * 60 * 60);
        return ResponseResult.success(true);
    }


    @ApiOperation(value = "按人任务-新增", notes = "注意：taskInfo是json字符串，不是json对象")
    @PostMapping(path = "/buildByPerson")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_ADD, operateDesc = "新增按人任务")
    @SysLog(func = "新增任务", opModule = OpModuleEnum.UNIFY_TASK, opType = OpTypeEnum.INSERT_BY_PERSON)
    public ResponseResult buildByPerson(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                         @RequestBody @Validated BuildByPersonRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        unifyTaskService.insertUnifyTaskByPerson(enterpriseId, user, request);
        return ResponseResult.success();
    }


    @ApiOperation("按人任务-分页查询任务")
    @GetMapping(path = "/getTaskParentByPerson")
    public ResponseResult<PageInfo<GetTaskByPersonVO>> getTaskParentByPerson(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                       GetTaskByPersonRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskService.getTaskByPerson(enterpriseId, request));
    }

    @ApiOperation("按人任务-查询任务详情")
    @GetMapping(path = "/getTaskParentDetailByPerson")
    public ResponseResult<GetTaskByPersonVO> getTaskParentDetailByPerson(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                   GetTaskDetailByPersonRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskService.getTaskDetailByPerson(enterpriseId, request));
    }

    @ApiOperation("按人任务-查询任务中间页")
    @GetMapping(path = "/getMiddlePageDataByPerson")
    public ResponseResult<PageInfo<GetMiddlePageDataByPersonVO>> getMiddlePageDataByPerson(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                                           GetMiddlePageDataByPersonRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskPersonService.getMiddlePageDataByPerson(enterpriseId, request));
    }

    @ApiOperation("按人任务-查询人员任务详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subTaskId", value = "子任务id", required = true, example = "0"),
    })
    @GetMapping(path = "/getTaskPersonDetail")
    public ResponseResult<GetMiddlePageDataByPersonVO> getTaskPersonDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                                 Long subTaskId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(unifyTaskPersonService.getTaskPersonDetail(enterpriseId, subTaskId));
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "subTaskId", value = "子任务id", required = true, example = "0"),
    })
    @ApiOperation("按人任务-查询计划巡检门店数量")
    @GetMapping(path = "/statisticsTaskPersonPatrol")
    public ResponseResult statisticsTaskPersonPatrol(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                     @Param("subTaskId") Long subTaskId) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(unifyTaskPersonService.statisticsTaskPersonPatrol(enterpriseId, subTaskId));
    }

    @ApiOperation("按人任务-附近门店")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subTaskId", value = "子任务id", required = true, example = "0"),
            @ApiImplicitParam(name = "storeName", value = "门店名称", required = true)
    })
    @GetMapping("/nearbyStores")
    public ResponseResult<List<StoreSignInMapDTO>> getSignInStoreListNew(@PathVariable("enterprise-id") String enterpriseId,
                                                                         @RequestParam("longitude") String longitude,
                                                                         @RequestParam("latitude") String latitude,
                                                                         @RequestParam(value = "queryDistance",defaultValue = "5") Double queryDistance,
                                                                         @Param("subTaskId") Long subTaskId, @Param("storeName") String storeName) {
        NearbyStoreRequest request =new NearbyStoreRequest();
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setQueryDistance(queryDistance);
        request.setStoreName(storeName);
        DataSourceHelper.changeToMy();
        UnifyTaskPersonDO unifyTaskPersonDO = unifyTaskPersonService.getTaskPersonBySubTaskId(enterpriseId, subTaskId);
        Boolean queryAll = false;
        if(unifyTaskPersonDO != null){
            TaskPersonTaskInfoDTO.ExecuteDemand executeDemand = JSONObject.parseObject(unifyTaskPersonDO.getExecuteDemand(), TaskPersonTaskInfoDTO.PatrolParam.class).getPatrolParam();
            if(StringUtils.isNotEmpty(executeDemand.getStoreRange()) && Constants.PATROL_STORE_RANGE.equals(executeDemand.getStoreRange())){
                queryAll = true;
            }
        }
        List<StoreSignInMapDTO> storeSignInMapDTOList = storeService.getSignInStoreMapListNew(enterpriseId,request, queryAll);
        storeSignInMapDTOList = storeSignInMapDTOList == null ? new ArrayList<>() : storeSignInMapDTOList;

        if(unifyTaskPersonDO != null){
            List<String> patroledStoreList = Lists.newArrayList();
            if(StringUtils.isNotBlank(unifyTaskPersonDO.getStoreIds())){
                patroledStoreList = Lists.newArrayList(StringUtils.split(unifyTaskPersonDO.getStoreIds(), Constants.COMMA));
            }
            List<String> finalPatroledStoreList = patroledStoreList;
            storeSignInMapDTOList.forEach(f -> {
                f.setHasPatroled(finalPatroledStoreList.contains(f.getStoreId()));
            });
        }
        return ResponseResult.success(storeSignInMapDTOList);
    }

    @ApiOperation("停止任务循环")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务id", required = true, example = "0"),
    })
    @PostMapping(path = "/stopTaskRun")
    public ResponseResult<Boolean> stopTaskRun(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                     @RequestParam("taskId") Long taskId) {
        DataSourceHelper.changeToMy();
        unifyTaskService.stopTaskRun(enterpriseId, taskId);
        return ResponseResult.success(null);
    }


    /**
     * 门店任务列表
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    @ApiOperation("新品和旧品门店任务列表")
    @PostMapping(path = "/achievement/storeTask/list")
    public ResponseResult<PageInfo<AchievementTaskRecordDTO>> list(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                         @RequestBody @Validated AchievementTaskStoreQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementTaskRecordService.achievementStoreTaskList(enterpriseId, query, UserHolder.getUser().getUserId()));
    }

    @ApiOperation("父任务任务催办")
    @PostMapping(path = "/taskReminder")
    @SysLog(func = "催办", opModule = OpModuleEnum.UNIFY_TASK, opType = OpTypeEnum.REMIND)
    public ResponseResult taskReminder(@PathVariable(value = "enterprise-id", required = false) String enterpriseId, @RequestBody @Validated ParentTaskReminderDTO param) {
        DataSourceHelper.changeToMy();
        unifyTaskService.taskReminder(enterpriseId, param);
        return ResponseResult.success();
    }

    @ApiOperation("刷新任务")
    @PostMapping("/refresh")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "刷新任务")
    public ResponseResult<Boolean> refreshTask(@PathVariable(value = "enterprise-id", required = false) String enterpriseId, Long taskId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);

        unifyTaskService.taskRefresh(enterpriseId, taskId, enterpriseConfigDO.getDbName());
        return ResponseResult.success(true);
    }

}
