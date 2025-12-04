/**
 * Alipay.com Inc. Copyright (c) 2004-2020 All Rights Reserved.
 */

package com.coolcollege.intelligent.controller.communication;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskStoreDao;
import com.coolcollege.intelligent.facade.SyncDeptFacade;
import com.coolcollege.intelligent.facade.UnifyTaskFcade;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enums.TaskCycleEnum;
import com.coolcollege.intelligent.model.enums.TaskRunRuleEnum;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.enums.UnifyTaskLoopDateEnum;
import com.coolcollege.intelligent.model.region.dto.PatrolStorePictureMsgDTO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskBuildDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.service.store.StoreOpenRuleService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskPersonService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.taobao.api.ApiException;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author 首亮
 * @version $Id: CommunicationController.java, v 0.1 2020年06月19日 14:26 首亮 Exp $
 */
@RestController
@RequestMapping("/v2/{enterprise-id}/communication")
@BaseResponse
@Slf4j
public class CommunicationController {

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;
    @Autowired
    private PatrolStoreService patrolStoreService;

    @Autowired
    @Lazy
    private UnifyTaskFcade unifyTaskFcade;

    @Autowired
    private SyncDeptFacade syncDeptFacade;
    @Resource
    private TaskParentMapper taskParentMapper;

    @Resource
    private TaskStoreDao taskStoreDao;
    @Resource
    private UnifyTaskPersonService unifyTaskPersonService;

    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private EnterpriseService enterpriseService;
    @Resource
    private StoreOpenRuleService storeOpenRuleService;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @RequestMapping(value = {"/unity_task_scheduler/{task-id}",
            "/unity_task_scheduler/{task-id}/{isOperateOverdue}"
    }, method = RequestMethod.POST)
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_ADD, operateDesc = "定时任务触发")
    public ResponseResult unityTaskScheduler(
            @PathVariable(value = "enterprise-id", required = false) String enterpriseId,
            @PathVariable(value = "task-id", required = false) String taskId,
            @PathVariable(value = "isOperateOverdue", required = false) String isOperateOverdue) {
        if(log.isInfoEnabled()){
            log.info("#unityTaskScheduler unity_task_scheduler: enterprise-id=" + enterpriseId + ";task-id=" + taskId);
        }
        // 切换数据源
        DataSourceHelper.reset();
        EnterpriseDO enterpriseDO = enterpriseService.selectById(enterpriseId);
        if(Objects.isNull(enterpriseDO)){
            log.info("企业信息不存在 enterprise-id     {}", enterpriseId);
            throw new ServiceException(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        if(EnterpriseStatusEnum.NORMAL.getCode() != enterpriseDO.getStatus()){
            log.info("企业信息不存在 enterprise-id     {}, 企业状态异常：{}， 任务生成失败:{}", enterpriseId, EnterpriseStatusEnum.getMessage(enterpriseDO.getStatus()), taskId);
            throw new ServiceException(ErrorCodeEnum.TASK_ENTERPRISE_STATUS_ERROR);
        }
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        if(Objects.isNull(enterpriseConfigDO)){
            log.info("企业信息不存在 enterprise-id     {}", enterpriseId);
            throw new ServiceException(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        ResponseResult TRUE = getResponseResult(enterpriseId, taskId);
        if (TRUE != null) {
            return TRUE;
        }
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, Long.valueOf(taskId));
        JSONObject taskInfoJson = JSON.parseObject(taskParentDO.getTaskInfo());
        isOperateOverdue = taskInfoJson.containsKey("isOperateOverdue") ?
                taskInfoJson.getString("isOperateOverdue") : isOperateOverdue;
        unifyTaskFcade.schedulerTask(enterpriseId, Long.valueOf(taskId), enterpriseConfigDO.getDbName(),isOperateOverdue, false);
        return ResponseResult.success(null);
    }

    private ResponseResult getResponseResult(String enterpriseId, String taskId) {
        TaskParentDO parentDO = taskParentMapper.selectTaskById(enterpriseId,Long.parseLong(taskId));
        if(parentDO == null){
            return ResponseResult.success(Boolean.TRUE);
        }
        if(TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(parentDO.getTaskType())) {
            Integer count = unifyTaskPersonService.countTodayByTaskId(enterpriseId, Long.parseLong(taskId));
            return count == 0 ? null : ResponseResult.success(Boolean.TRUE);
        }
        // 单次任务直接返回null
        String runRule = parentDO.getRunRule();
        Long now = System.currentTimeMillis();
        //1.判断结束时间
        if(now>parentDO.getEndTime()){
            if(log.isInfoEnabled()){
                log.info("该父任务已结束，enterpriseId = {},taskId = {}",enterpriseId,taskId);
            }
            return ResponseResult.success(Boolean.TRUE);
        }
        Long startTime =   DateUtil.getTodayTime(0);
        Long endTime =   DateUtil.getTodayTime(24);
        //2.判断今天是否已经生成过门店任务
        Integer count = taskStoreDao.countByUnifyTaskIdAndTime(enterpriseId,parentDO.getId(),startTime,endTime);
        if(!TaskTypeEnum.PATROL_STORE_AI.getCode().equals(parentDO.getTaskType()) && count>0){
            if(log.isInfoEnabled()){
                log.info("该父任务的当天门店任务已生成，enterpriseId = {},taskId = {}",enterpriseId,taskId);
            }
            return ResponseResult.success(Boolean.TRUE);
        }
        if(TaskRunRuleEnum.ONCE.getCode().equals(runRule)){
            return null;
        }
        String taskCycle = parentDO.getTaskCycle();
        if(TaskCycleEnum.DAY.getCode().equals(taskCycle)){
            return null;
        }
        String runDate = parentDO.getRunDate();
        if(StringUtils.isBlank(runDate)){
            if(log.isInfoEnabled()){
                log.info("该父任务循环方式为空，enterpriseId = {},taskId = {}",enterpriseId,taskId);
            }
            return ResponseResult.success(Boolean.TRUE);
        }
        String[] runDates = runDate.split(",");
        //3.判断是否日期内
        Boolean isContinue = true;
        if(TaskCycleEnum.WEEK.getCode().equals(taskCycle)){
            String s =  DateUtil.getWeek(now);
            // 数据库存的是7，后端是0
            if("0".equals(s)){
                s = "7";
            }
            String finalS = s;
            isContinue = Arrays.asList(runDates).stream().anyMatch(data -> data.equals(finalS));
        }else if(TaskCycleEnum.MONTH.getCode().equals(taskCycle)){
            String s =  DateUtil.getDate(now);
            isContinue = Arrays.asList(runDates).stream().anyMatch(data -> data.equals(s));
        }
        if(!isContinue){
            if(log.isInfoEnabled()){
                log.info("该任务不在执行时间内，enterpriseId = {},taskId = {}",enterpriseId,taskId);
            }
            return ResponseResult.success(Boolean.TRUE);
        }
        if(UnifyTaskLoopDateEnum.QUARTER.getCode().equals(taskCycle)){
            return  checkQuarterTask(enterpriseId, parentDO);
        }
        if(UnifyTaskLoopDateEnum.HOUR.getCode().equals(taskCycle)){
            Calendar calendar = Calendar.getInstance();
            String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            List<String> split = Arrays.asList(parentDO.getRunDate().split(Constants.COMMA));
            if(split.contains(hour)){
                return null;
            }
            return ResponseResult.success(Boolean.TRUE);
        }
        return null;
    }






    @PostMapping(path = "/patrol_store_give_up_scheduler/{business_id}/{token}")
//    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_ADD, operateDesc = "门店巡视定时任务触发")
    public ResponseResult patrolStoreGiveUpScheduler(
            @PathVariable(value = "enterprise-id", required = false) String enterpriseId,
            @PathVariable(value = "business_id", required = false) Long businessId,
            @PathVariable(value = "token", required = false) String token) {
        log.info("#unityTaskScheduler patrol_store_give_up_scheduler: enterprise-id=" + enterpriseId + ";business_id" + businessId + ";token=" + token);

        if(StringUtils.isBlank(token)){
            log.info("#unityTaskScheduler patrol_store_give_up_scheduler: token为空");
            return ResponseResult.fail(ErrorCodeEnum.FAIL.getCode(), "token不能为空");
        }

        String rightToken = MD5Util.md5(businessId + enterpriseId + businessId);

        if(!rightToken.equals(token)){
            log.info("#unityTaskScheduler patrol_store_give_up_scheduler: token验证不通过");
            return ResponseResult.fail(ErrorCodeEnum.FAIL.getCode(), "token验证不通过");
        }

        // 切换数据源
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        patrolStoreService.giveUp(enterpriseId, businessId);
        return ResponseResult.success(null);
    }

    @PostMapping(path = "/dingSyncAllTaskScheduler/{token}")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_ADD, operateDesc = "定时任务触发")
    public ResponseResult dingSyncAllTaskScheduler(
            @PathVariable(value = "enterprise-id", required = false) String enterpriseId,
            @PathVariable(value = "token", required = false) String token) throws ApiException {
        if(StringUtils.isBlank(token)){
            log.info("# dingSyncAllTaskScheduler: token为空");
            return ResponseResult.fail(ErrorCodeEnum.FAIL.getCode(), "token不能为空");
        }

        String rightToken = MD5Util.md5(enterpriseId);

        if(!rightToken.equals(token)){
            log.info("#dingSyncAllTaskScheduler: token验证不通过");
            return ResponseResult.fail(ErrorCodeEnum.FAIL.getCode(), "token验证不通过");
        }
        log.info("# dingSyncAllTaskScheduler: enterprise-id=" + enterpriseId + ";token=" + token);
        // 切换数据源
        DataSourceHelper.changeToMy();
        syncDeptFacade.sync(enterpriseId, Constants.SYSTEM_USER_NAME, Constants.SYSTEM_USER_ID);
        return ResponseResult.success(null);
    }

    @PostMapping(path = "/capturePicture/{business_id}")
    @OperateLog(operateModule = CommonConstant.Function.TASK, operateType = CommonConstant.LOG_ADD, operateDesc = "定时抓拍图片任务回调")
    public ResponseResult capturePicture(
            @PathVariable(value = "enterprise-id", required = false) String enterpriseId,
            @PathVariable(value = "business_id", required = false) Long businessId) {

        log.info("# capturePicture: enterprise-id=" + enterpriseId);
        // 切换数据源
        simpleMessageService.send(JSONObject.toJSONString(new PatrolStorePictureMsgDTO(enterpriseId, businessId, null, TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode())), RocketMqTagEnum.PATROL_STORE_CAPTURE_PICTURE_QUEUE);

        return ResponseResult.success(null);
    }

    @PostMapping(path = "/storeOpenRule/{ruleId}")
    public ResponseResult<Boolean> storeOpenRuleCallback(
            @PathVariable(value = "enterprise-id", required = false) String enterpriseId,
            @PathVariable(value = "ruleId", required = false) Long ruleId) {

        log.info("#storeOpenRuleCallback,unity_task_scheduler: enterprise-id=" + enterpriseId + ";ruleId=" + ruleId);
        // 切换数据源
        DataSourceHelper.reset();
        EnterpriseDO enterpriseDO = enterpriseService.selectById(enterpriseId);
        if (Objects.isNull(enterpriseDO)) {
            log.info("企业信息不存在 enterprise-id     {}", enterpriseId);
            throw new ServiceException(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        if (EnterpriseStatusEnum.NORMAL.getCode() != enterpriseDO.getStatus()) {
            log.info("企业信息不存在 enterprise-id     {}, 企业状态异常：{}， 任务生成失败:{}", enterpriseId, EnterpriseStatusEnum.getMessage(enterpriseDO.getStatus()), ruleId);
            throw new ServiceException(ErrorCodeEnum.TASK_ENTERPRISE_STATUS_ERROR);
        }
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        if (Objects.isNull(enterpriseConfigDO)) {
            log.info("storeOpenRule#enterprise-id：{}，企业信息不存在, enterpriseConfigDO", enterpriseId);
            throw new ServiceException(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        String lockKey = "storeOpenRule:" + enterpriseId + "_" + ruleId;
        boolean lock = redisUtilPool.setNxExpire(lockKey, ruleId.toString(), CommonConstant.NORMAL_LOCK_TIMES);
        if (lock) {
            try {
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                UnifyTaskBuildDTO unifyTaskBuildDTO = storeOpenRuleService.buildStoreRuleTaskDTO(enterpriseId, ruleId);
                if(unifyTaskBuildDTO == null){
                    log.info("unifyTaskBuildDTO is null");
                    return ResponseResult.success(null);
                }
                CurrentUser currentUser = new CurrentUser();
                currentUser.setUserId(unifyTaskBuildDTO.getUserId());
                currentUser.setName(unifyTaskBuildDTO.getUserName());
                unifyTaskFcade.insertUnifyTask(enterpriseId, unifyTaskBuildDTO, currentUser, System.currentTimeMillis());
            } catch (Exception e) {
                log.error("WorkHandoverListener#error", e);
            } finally {
                redisUtilPool.delKey(lockKey);
            }
        }
        return ResponseResult.success(null);
    }


    private ResponseResult checkQuarterTask(String enterpriseId, TaskParentDO parentDO) {
        Long now = System.currentTimeMillis();
        Long firstQuarterStartTime = DateUtils.convertStringToLong(parentDO.getRunDate()+ " " + parentDO.getCalendarTime() + ":00");
        //1.判断开始时间
        if(now < firstQuarterStartTime){
            if(log.isInfoEnabled()){
                log.info("该季度父任务未开始，enterpriseId = {},taskId = {}" ,enterpriseId ,parentDO.getId());
            }
            return ResponseResult.success(Boolean.TRUE);
        }
        // 相差季度数 =  相差月数/3
        int diffQuarter = DateUtils.getMonthDiff(new Date(), new Date(firstQuarterStartTime))/3;
        Date currentQuarterStartDate = org.apache.commons.lang3.time.DateUtils.addMonths(new Date(firstQuarterStartTime), 3 * diffQuarter);
        Date currentQuarterEndDate = org.apache.commons.lang3.time.DateUtils.addMonths(new Date(firstQuarterStartTime), 3 * (diffQuarter+1));
        Integer count = taskStoreDao.countByUnifyTaskIdAndTime(enterpriseId,parentDO.getId(),currentQuarterStartDate.getTime(),currentQuarterEndDate.getTime());
        if(count>0){
            if(log.isInfoEnabled()){
                log.info("该父任务的季度门店任务已生成，enterpriseId = {},taskId = {},firstQuarterStartTime = {},currentQuarterStartDate = {},currentQuarterEndDate = {}"
                        ,enterpriseId ,parentDO.getId(), firstQuarterStartTime, currentQuarterStartDate, currentQuarterEndDate);
            }
            return ResponseResult.success(Boolean.TRUE);
        }
        return  null;
    }

    public static void main(String[] args) {
        TaskParentDO parentDO = new TaskParentDO();
        parentDO.setRunDate("2022/03/18");
        parentDO.setCalendarTime("19:05");
        parentDO.setLoopCount(3L);

        Long firstQuarterStartTime = DateUtils.convertStringToLong(parentDO.getRunDate()+ " " + parentDO.getCalendarTime() + ":00");
        // 相差季度数 =  相差月数/3
        int diffQuarter = DateUtils.getMonthDiff(new Date(1658230009000L), new Date(firstQuarterStartTime))/3;
        Date currentQuarterStartDate = org.apache.commons.lang3.time.DateUtils.addMonths(new Date(firstQuarterStartTime), 3 * diffQuarter);
        Date currentQuarterEndDate = org.apache.commons.lang3.time.DateUtils.addMonths(new Date(firstQuarterStartTime), 3 * (diffQuarter+1));
       log.info("该父任务的季度门店任务已生成，taskId = {},firstQuarterStartTime = {},currentQuarterStartDate = {},currentQuarterEndDate = {}"
                ,parentDO.getId(), firstQuarterStartTime, currentQuarterStartDate, currentQuarterEndDate);

    }

}