package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseStatusEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskStoreDao;
import com.coolcollege.intelligent.facade.UnifyTaskFcade;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enums.TaskCycleEnum;
import com.coolcollege.intelligent.model.enums.TaskRunRuleEnum;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.enums.UnifyTaskLoopDateEnum;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskPersonService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zhangchenbiao
 * @FileName: SchedulerJobListener
 * @Description: 定时任务回调
 * @date 2023-02-23 10:22
 */
@Slf4j
@Service
public class SchedulerJobListener implements MessageListener {

    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private EnterpriseService enterpriseService;
    @Autowired
    private UnifyTaskFcade unifyTaskFcade;
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private UnifyTaskPersonService unifyTaskPersonService;
    @Resource
    private TaskStoreDao taskStoreDao;
    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("定时任务回调mq:{}", text);
        String lockKey = "SchedulerJobListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);
        if(lock){
            JSONObject jsonObject = JSONObject.parseObject(text);
            String enterpriseId = jsonObject.getString("enterpriseId");
            String taskId = jsonObject.getString("taskId");
            if(StringUtils.isAnyBlank(enterpriseId, taskId)){
                log.info("SchedulerJobListener message:{}", text);
                return Action.CommitMessage;
            }
            DataSourceHelper.reset();
            EnterpriseDO enterpriseDO = enterpriseService.selectById(enterpriseId);
            if(Objects.isNull(enterpriseDO)){
                log.info("企业信息不存在：{}", enterpriseId);
                return Action.CommitMessage;
            }
            if(EnterpriseStatusEnum.NORMAL.getCode() != enterpriseDO.getStatus()){
                log.info("enterprise-id：{}，企业状态异常：{}， 任务生成失败:{}", enterpriseId, EnterpriseStatusEnum.getMessage(enterpriseDO.getStatus()), taskId);
                return Action.CommitMessage;
            }
            EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
            if(Objects.isNull(enterpriseConfigDO)){
                log.info("企业信息不存在， enterpriseConfigDO 为空：{}", enterpriseId);
                return Action.CommitMessage;
            }
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
            //判断是否生成任务
            ResponseResult TRUE = getResponseResult(enterpriseId, taskId);
            if (TRUE != null) {
                return Action.CommitMessage;
            }
            unifyTaskFcade.schedulerTask(enterpriseId, Long.valueOf(taskId), enterpriseConfigDO.getDbName(),null, false);
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
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
}
