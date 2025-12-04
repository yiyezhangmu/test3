package com.coolcollege.intelligent.service.unifytask.resolve;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.unifytask.dto.CombineUpcomingCancelData;
import com.coolcollege.intelligent.model.unifytask.dto.TaskReissueDTO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskSubVO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskParentDao;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskStoreDao;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskSubDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.util.TaskCacheManager;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: TaskResolveService
 * @Description:
 * @date 2025-01-07 10:53
 */
@Slf4j
@Service
public class TaskResolveService {

    @Resource
    private TaskResolveFactory taskResolveFactory;
    @Resource
    protected EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;
    @Resource
    private TaskStoreDao taskStoreDao;
    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;
    @Resource
    private TaskParentDao taskParentDao;
    @Resource
    protected TaskSubDao taskSubDao;
    @Resource
    protected StoreDao storeDao;
    @Resource
    protected UnifyTaskService unifyTaskService;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    protected UnifyTaskStoreService unifyTaskStoreService;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Autowired
    @Lazy
    private JmsTaskService jmsTaskService;
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    /**
     * 任务分解
     * @param enterpriseId
     * @param taskStore
     */
    public void taskResolve(String enterpriseId, TaskStoreDO taskStore, boolean isRefresh) {
        String requestId = MDC.get(Constants.REQUEST_ID);
        MDC.put(Constants.REQUEST_ID, requestId + "_" + taskStore.getStoreId());
        log.info("任务分解 开始 enterpriseId:{}, unifyTaskId:{}, storeId:{}, loopCount:{}", enterpriseId, taskStore.getUnifyTaskId(), taskStore.getStoreId(), taskStore.getLoopCount());
        long startTime = System.currentTimeMillis();
        TaskTypeEnum taskType = TaskTypeEnum.getByCode(taskStore.getTaskType());
        ITaskResolve taskResolve = taskResolveFactory.getTaskResolve(taskType);
        Long unifyTaskId = taskStore.getUnifyTaskId(), loopCount = taskStore.getLoopCount();
        String storeId = taskStore.getStoreId();
        if(Objects.isNull(unifyTaskId) || Objects.isNull(loopCount) || StringUtils.isBlank(storeId)){
            log.info("任务分解 门店任务关键字段不存在, taskStore:{}", JSONObject.toJSONString(taskStore));
            return;
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = TaskCacheManager.getEnterpriseConfig(enterpriseId, taskStore.getUnifyTaskId(), () -> enterpriseConfigDao.getEnterpriseConfig(enterpriseId));
        if(Objects.isNull(enterpriseConfig)){
            log.info("任务分解 企业信息不存在");
            return;
        }
        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        //新增门店任务子任务逻辑
        TaskParentDO taskParent = taskParentDao.selectById(enterpriseId, taskStore.getUnifyTaskId());
        if(Objects.isNull(taskParent)){
            log.error("任务分解 父任务不存在,unifyTaskId={},storeId={},loopCount={}", unifyTaskId, storeId, loopCount);
            return;
        }
        boolean isFilterStoreTask = taskResolve.isFilterStoreTask(enterpriseId, taskParent, taskStore);
        if(isFilterStoreTask){
            log.info("任务分解 任务不生成");
            return;
        }
        long step2Time = System.currentTimeMillis();
        log.info("任务分解耗时 : 1----> {}秒", (step2Time - startTime)/1000);
        Object businessData = taskResolve.getBusinessData(enterpriseId, taskStore.getUnifyTaskId(), taskStore.getStoreId(), taskStore.getLoopCount());
        log.info("任务分解耗时 : 2----> {}秒", (System.currentTimeMillis() - step2Time)/1000);
        long step3Time = System.currentTimeMillis();
        TaskStoreDO taskStoreDetail = taskStoreDao.getTaskStoreDetail(enterpriseId, unifyTaskId, storeId, loopCount);
        if(Objects.nonNull(businessData) && Objects.isNull(taskStoreDetail)){
            //门店任务为空  业务数据不为空 走补发
            log.info("任务分解 门店任务被删除 不做补发");
            return;
        }
        if(Objects.nonNull(taskStoreDetail)){
            log.info("任务分解 补发");
            TaskReissueDTO taskReissueDTO = reissueResolve(enterpriseId, taskStoreDetail, taskStore, enterpriseStoreCheckSetting);
            Map<String, Object> reissueMap = taskResolve.taskReissue(enterpriseId, taskStoreDetail, taskParent, taskReissueDTO, isRefresh);
            sendReissueNotice(enterpriseId, reissueMap, taskParent, taskStoreDetail, enterpriseConfig, taskReissueDTO);
            return;
        }
        log.info("任务分解 首次新增");
        List<TaskSubDO> subTaskList = taskResolve.createTaskStoreAndSubTask(enterpriseId, taskParent, taskStore, enterpriseStoreCheckSetting);
        log.info("任务分解耗时 : 3----> {}秒", (System.currentTimeMillis() - step3Time)/1000);
        unifyTaskService.sendNotice(enterpriseId, taskStore, taskParent, subTaskList, enterpriseStoreCheckSetting.getTaskCcRemind());
    }

    /**
     * 任务补发消息通知
     * <p> 待办取消、新增抄送人通知
     * <p> 新增人员的待办通知
     */
    private void sendReissueNotice(String enterpriseId, Map<String, Object> reissueMap, TaskParentDO parentDO, TaskStoreDO taskStoreDO, EnterpriseConfigDO configDO, TaskReissueDTO taskReissueDTO) {
        try {
            List<Long> removeSubTaskIdList = (List<Long>) reissueMap.get(Constants.REMOVE_SUB_TASK_ID_LIST);
            List<String> removeUserIdList = (List<String>) reissueMap.get(Constants.REMOVE_USER_ID_LIST);
            // 取消待办
            if (TaskTypeEnum.isCombineNoticeTypes(taskStoreDO.getTaskType()) && CollectionUtil.isNotEmpty(removeUserIdList)) {
                unifyTaskService.cancelCombineUpcoming(enterpriseId, parentDO.getId(), taskStoreDO.getLoopCount(), taskStoreDO.getStoreId(), taskStoreDO.getNodeNo(), removeUserIdList, configDO.getDingCorpId(), configDO.getAppType());
            } else if (CollectionUtil.isNotEmpty(removeSubTaskIdList)) {
                unifyTaskService.cancelUpcoming(enterpriseId, removeSubTaskIdList, configDO.getDingCorpId(), configDO.getAppType());
            }
            // 处理人待办通知、新增抄送人通知
//            List<String> addCcUserIds = taskReissueDTO.getAddCcUserIds();
//            TaskSubVO taskSub = taskReissueDTO.getLatestTaskSub();
//            unifyTaskService.sendTaskJms(enterpriseId, parentDO, taskStoreDO, addSubTaskList, true, addCcUserIds, taskSub.getSubTaskId());
        } catch (Exception e) {
            log.error("任务补发消息通知异常", e);
        }
    }

    /**
     * 补发处理
     * @param enterpriseId 企业id
     * @param oldTaskStore 原门店任务
     * @param newTaskStore 新门店任务
     * @param enterpriseStoreCheckSetting 企业巡店配置
     * @return 任务补发DTO
     */
    private TaskReissueDTO reissueResolve(String enterpriseId, TaskStoreDO oldTaskStore, TaskStoreDO newTaskStore, EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting) {
        Map<String, String> extendInfo = JSONObject.parseObject(newTaskStore.getExtendInfo(), Map.class);
        Map<String, List<String>> nodePerson = extendInfo.entrySet().stream()
                .filter(v -> UnifyNodeEnum.isHandleNode(v.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, v -> Arrays.asList(StringUtils.split(v.getValue(), ","))));
        List<String> handlerUserList = nodePerson.getOrDefault(UnifyNodeEnum.FIRST_NODE.getCode(), Collections.emptyList());
        List<String> auditUserList = nodePerson.get(UnifyNodeEnum.SECOND_NODE.getCode());
        List<String> recheckUserList = nodePerson.get(UnifyNodeEnum.THIRD_NODE.getCode());
        List<String> thirdApproveList = nodePerson.get(UnifyNodeEnum.FOUR_NODE.getCode());
        List<String> fourApproveList = nodePerson.get(UnifyNodeEnum.FIVE_NODE.getCode());
        List<String> fiveApproveList = nodePerson.get(UnifyNodeEnum.SIX_NODE.getCode());

        // 当前节点需要 新增 移除 的人员
        Map<String, List<String>> currentNodePersonChangeMap = unifyTaskStoreService.getCurrentNodePersonChangeMap(enterpriseId, oldTaskStore,
                handlerUserList, auditUserList, recheckUserList, thirdApproveList, fourApproveList, fiveApproveList);
        List<String> newAddPersonList = currentNodePersonChangeMap.get(Constants.PERSON_CHANGE_KEY_NEWADD);
        List<String> removePersonList = currentNodePersonChangeMap.get(Constants.PERSON_CHANGE_KEY_REMOVE);
        // 当前最新的子任务
        TaskSubVO taskSubVO = taskSubDao.getLatestSubId(enterpriseId, oldTaskStore.getUnifyTaskId(), oldTaskStore.getStoreId(), oldTaskStore.getLoopCount(), null, null, oldTaskStore.getNodeNo());
        // 巡店配置项中获取刷新策略
        JSONObject extendFile = JSONObject.parseObject(enterpriseStoreCheckSetting.getExtendField());
        String refreshStrategy = Objects.nonNull(extendFile) ? extendFile.getString("refreshStrategy") : "";
        // 新增抄送人
        Set<String> oldCcUserIds = Sets.newHashSet(StringUtils.split(Optional.ofNullable(oldTaskStore.getCcUserIds()).orElse(""), Constants.COMMA));
        Set<String> newCcUserIds = Sets.newHashSet(StringUtils.split(Optional.ofNullable(newTaskStore.getCcUserIds()).orElse(""), Constants.COMMA));
        List<String> addCcUserIds = newCcUserIds.stream().filter(v -> StringUtils.isNotBlank(v) && !oldCcUserIds.contains(v)).collect(Collectors.toList());
        return new TaskReissueDTO(newAddPersonList, removePersonList, refreshStrategy, taskSubVO, new HashSet<>(handlerUserList), newTaskStore.getExtendInfo(), newTaskStore.getCcUserIds(), addCcUserIds);
    }

}
