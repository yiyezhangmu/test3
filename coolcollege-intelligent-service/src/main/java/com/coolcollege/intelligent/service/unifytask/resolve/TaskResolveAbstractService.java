package com.coolcollege.intelligent.service.unifytask.resolve;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.dao.device.dao.DeviceDao;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskParentDao;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskStoreDao;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskSubDao;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskParentCcUserDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentCcUserDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskReissueDTO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskSubVO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhangchenbiao
 * @FileName: TaskResolve
 * @Description:任务分解
 * @date 2025-01-07 10:18
 */
@Slf4j
public abstract class TaskResolveAbstractService<T> implements ITaskResolve{

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
    protected DeviceDao deviceDao;
    @Resource
    protected EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;
    @Resource
    protected RedisUtilPool redisUtilPool;
    @Resource
    protected SimpleMessageService simpleMessageService;
    @Resource
    protected UnifyTaskStoreService unifyTaskStoreService;
    @Resource
    protected UnifyTaskParentCcUserDao unifyTaskParentCcUserDao;
    @Resource
    protected UnifyTaskService unifyTaskService;

    /**
     * 是否需要过滤门店任务
     * @param enterpriseId
     * @param taskStore
     */
    @Override
    public boolean isFilterStoreTask(String enterpriseId, TaskParentDO taskParent, TaskStoreDO taskStore){
        if(Objects.isNull(taskStore)){
            log.info("任务分解 门店任务实体不存在");
            return true;
        }
        Long unifyTaskId = taskStore.getUnifyTaskId(), loopCount = taskStore.getLoopCount();
        String storeId = taskStore.getStoreId();
        StoreDO storeDO = storeDao.getByStoreId(enterpriseId, storeId);
        //线上线下巡店、陈列任务、定时巡检、巡店计划、ai巡检 如果门店不是open则，不在发任务
        boolean isContainTaskType = TaskTypeEnum.isTaskTypeFilterStoreStatus(taskStore.getTaskType());
        if (!Constants.STORE_STATUS_OPEN.equals(storeDO.getStoreStatus()) && isContainTaskType) {
            log.info("任务分解 #该门店状态不满足生成任务条件, storeId:{}, storeName:{}, storeStatus:{}, taskId:{}, taskName:{}, loopCount:{}",
                    storeId, storeDO.getStoreName(), storeDO.getStoreStatus(), taskStore.getUnifyTaskId(), taskStore.getTaskName(), loopCount);
            return true;
        }
        if (TaskTypeEnum.isNeedDevice(taskStore.getTaskType())) {
            int deviceNum = deviceDao.countDeviceByStoreId(enterpriseId, DeviceTypeEnum.DEVICE_VIDEO.getCode(), storeId);
            if (deviceNum == Constants.INDEX_ZERO) {
                log.info("任务分解 该门店的定时巡检任务无摄像头， 无法生成门店任务");
                return true;
            }
        }
        boolean isFilter = filterSongXiaStoreTask(enterpriseId, storeId, taskParent);
        if(!isFilter){
            log.info("任务分解 松下撤样任务，该门店未出样该型号或者库存为0，不生成任务,enterpriseId = {},taskId = {}, storeId = {}", enterpriseId, unifyTaskId, storeId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TaskSubDO> createTaskStoreAndSubTask(String enterpriseId, TaskParentDO taskParent, TaskStoreDO taskStore, EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting){
        //获取子任务
        List<TaskSubDO> subTaskList = getSubTaskList(taskParent, taskStore);
        taskSubDao.batchInsertTaskSub(enterpriseId, subTaskList);
        taskStore.setEditTime(new Date());
        //插入门店任务
        taskStoreDao.addTaskStore(enterpriseId, taskStore);
        if(!taskParent.getLoopCount().equals(taskStore.getLoopCount()) && taskStore.getLoopCount() > taskParent.getLoopCount()){
            //父任务中的loopCount 和 门店任务中的loopCount不相等  且门店任务的loopCount 大于父任务的loopCount 的时候才会更新，
            taskParentDao.updateLoopCountById(enterpriseId, taskParent.getId(), taskStore.getLoopCount());
        }
        //新增对应的记录
        addBusinessRecord(enterpriseId, taskParent, taskStore, subTaskList, enterpriseStoreCheckSetting);
        List<String> ccUserList = toListByString(taskStore.getCcUserIds());
        if(CollectionUtils.isNotEmpty(ccUserList)){
            List<UnifyTaskParentCcUserDO> list = CollStreamUtil.toList(ccUserList, ccUserId -> new UnifyTaskParentCcUserDO(taskParent.getId(), taskParent.getTaskName(),
                    taskParent.getTaskType(), ccUserId, UnifyStatus.ONGOING.getCode(), taskParent.getBeginTime(), taskParent.getEndTime()));
            unifyTaskParentCcUserDao.batchInsertOrUpdate(enterpriseId, list);
        }
        return subTaskList;
    }

    /**
     * 获取门店任务详情
     * @param enterpriseId
     * @param unifyTaskId
     * @param storeId
     * @param loopCount
     * @return
     */
    public TaskStoreDO getTaskStoreDetail(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount){
        return taskStoreDao.getTaskStoreDetail(enterpriseId, unifyTaskId, storeId, loopCount);
    }

    /**
     * 获取子任务列表
     * @param taskStore
     * @return
     */
    private List<TaskSubDO> getSubTaskList(TaskParentDO taskParentDO, TaskStoreDO taskStore){
        String extendInfo = taskStore.getExtendInfo();
        JSONObject jsonObject = JSONObject.parseObject(extendInfo);
        String handlerUserIdsStr = jsonObject.getString(UnifyNodeEnum.FIRST_NODE.getCode());
        String[] split = handlerUserIdsStr.split(Constants.COMMA);
        List<String> handlerUserIds = Stream.of(split).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        Date handleEndTimeDate = getHandleEndTime(taskParentDO, taskStore.getSubBeginTime(), taskStore.getSubEndTime());
        JSONObject taskInfoJson = JSON.parseObject(taskParentDO.getTaskInfo());
        String isOperateOverdue = taskInfoJson.containsKey("isOperateOverdue") ? taskInfoJson.getString("isOperateOverdue") : null;
        List<TaskSubDO> subTaskList = new ArrayList<>();
        for (String handlerUserId : handlerUserIds) {
            subTaskList.add(TaskSubDO.convertSubTask(taskStore, handlerUserId, handleEndTimeDate, isOperateOverdue));
        }
        taskStore.setHandlerEndTime(handleEndTimeDate);
        return subTaskList;
    }

    /**
     * 获取截止处理时间
     * @param taskParentDO
     * @param beginTime
     * @param endTime
     * @return
     */
    public Date getHandleEndTime(TaskParentDO taskParentDO, Date beginTime, Date endTime){
        Pair<String, Long> handleEndTimeAndLimitHour = getHandleEndTimeAndLimitHour(taskParentDO);
        if(Objects.isNull(handleEndTimeAndLimitHour)){
            return endTime;
        }
        String handleEndTime = handleEndTimeAndLimitHour.getKey();
        Long handleLimitHour = handleEndTimeAndLimitHour.getValue();
        Date handleEndTimeDate = null;
        if (TaskRunRuleEnum.ONCE.getCode().equals(taskParentDO.getRunRule())) {
            if (StringUtils.isNotBlank(handleEndTime)) {
                handleEndTimeDate = com.coolcollege.intelligent.common.util.DateUtil.parse(handleEndTime + ":59", DatePattern.NORM_DATETIME_PATTERN);
            }
        } else if (TaskRunRuleEnum.LOOP.getCode().equals(taskParentDO.getRunRule())) {
            if (handleLimitHour != null) {
                long handleLimitHourTime = handleLimitHour * 60;
                handleEndTimeDate = org.apache.commons.lang3.time.DateUtils.addMinutes(beginTime, (int) handleLimitHourTime);
            }
        }
        if(Objects.isNull(handleEndTimeDate)){
            return endTime;
        }
        return handleEndTimeDate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> taskReissue(String enterpriseId, TaskStoreDO oldTaskStore, TaskParentDO taskParentDO, TaskReissueDTO taskReissueDTO, boolean isRefresh) {
        log.info("任务补发");
        String refreshStrategy = taskReissueDTO.getRefreshStrategy();
        List<String> newAddPersonList = taskReissueDTO.getNewAddPersonList();
        List<String> removePersonList = taskReissueDTO.getRemovePersonList();
        Set<String> handlerUserSet = taskReissueDTO.getHandlerUserSet();
        TaskSubVO latestTaskSub = taskReissueDTO.getLatestTaskSub();
        if (UnifyNodeEnum.END_NODE.getCode().equals(oldTaskStore.getNodeNo())) {
            log.info("taskReissue#任务已完成，不在进行补发，eid:{},taskId:{},storeId:{}, loopCount:{}", enterpriseId, oldTaskStore.getUnifyTaskId(), oldTaskStore.getStoreId(), oldTaskStore.getLoopCount());
        }

        Long createTime = System.currentTimeMillis();
        // 重新分配节点人员
        // 跟刷新操作区别开来，刷新的某些情况下，是不给新增人员补发子任务的
        if (!isRefresh) {
            unifyTaskService.reallocateStoreTask(enterpriseId, oldTaskStore, taskParentDO, newAddPersonList, null, createTime, null, latestTaskSub, true);
        }
        Map<String, Object> result = Collections.emptyMap();
        boolean clearHandlerUser = false;
        if (isRefresh) {
            log.info("任务刷新");
            // 实际处理人
            String actualHandlerUser = oldTaskStore.getHandlerUserId();
            if (UnifyNodeEnum.FIRST_NODE.getCode().equals(oldTaskStore.getNodeNo())) {
                // 没有实际处理人的情况下，给新增人员补发子任务、非处理人删除子任务
                if (StringUtils.isBlank(actualHandlerUser)) {
                    result = unifyTaskService.reallocateStoreTask(enterpriseId, oldTaskStore,
                            taskParentDO, newAddPersonList, removePersonList, createTime, null, latestTaskSub, false);
                } else if (!handlerUserSet.contains(actualHandlerUser) && TaskRefreshSettingEnum.ABSENT_CLEAR_HANDLER_USER.getCode().equals(refreshStrategy)) {
                    // 有实际处理人 且 实际处理人不在处理人中 且 刷新策略为“仅实际处理人不在当前处理人中时清空实际处理人”时
                    // 清空实际处理人、给新增人员补发子任务
                    clearHandlerUser = true;
                    result = unifyTaskService.reallocateStoreTask(enterpriseId, oldTaskStore,
                            taskParentDO, newAddPersonList, removePersonList, createTime, null, latestTaskSub, false);
                }
            } else if (UnifyNodeEnum.isApproveNode(oldTaskStore.getNodeNo())) {
                // 实际处理人不在处理人中 且 刷新策略为“仅实际处理人不在当前处理人中时清空实际处理人” 时
                // 1.清空处理人
                // 2.任务被驳回后流程引擎给所有处理人发子任务（原逻辑只给处理节点的处理人发子任务）
                if (!handlerUserSet.contains(actualHandlerUser) && TaskRefreshSettingEnum.ABSENT_CLEAR_HANDLER_USER.getCode().equals(refreshStrategy)) {
                    clearHandlerUser = true;
                }
                // 新增人员补发子任务、非处理人删除子任务
                result = unifyTaskService.reallocateStoreTask(enterpriseId, oldTaskStore,
                        taskParentDO, newAddPersonList, removePersonList, createTime, null, latestTaskSub, false);
            }
        }
        // 更新task_store 表
        updateTaskReissuePerson(enterpriseId, oldTaskStore.getId(), taskParentDO, taskReissueDTO.getNewExtendInfoStr(), taskReissueDTO.getNewCcUserIdsStr(), clearHandlerUser);
        return result;
    }

    /**
     * 更新任务补发人员数据
     * <p> 包括门店任务extendInfo、ccUserIds、父任务抄送人映射表
     * @param enterpriseId 企业id
     * @param taskStoreId 门店任务id
     * @param taskParentDO 父任务
     * @param extendInfoStr 门店任务扩展信息
     * @param ccUserIdStr 抄送人id集合
     * @param clearHandlerUser 清空处理人
     */
    private void updateTaskReissuePerson(String enterpriseId, Long taskStoreId, TaskParentDO taskParentDO, String extendInfoStr, String ccUserIdStr, boolean clearHandlerUser) {
        // 更新父任务抄送人映射表
        List<String> ccUserList = toListByString(ccUserIdStr);
        if(CollectionUtils.isNotEmpty(ccUserList)){
            List<UnifyTaskParentCcUserDO> list = CollStreamUtil.toList(ccUserList, ccUserId -> new UnifyTaskParentCcUserDO(taskParentDO.getId(), taskParentDO.getTaskName(),
                    taskParentDO.getTaskType(), ccUserId, UnifyStatus.ONGOING.getCode(), taskParentDO.getBeginTime(), taskParentDO.getEndTime()));
            unifyTaskParentCcUserDao.batchInsertOrUpdate(enterpriseId, list);
        }

        log.info("任务补发后节点taskStoreId:{}, extendInfoJsonStr:{}", taskStoreId, extendInfoStr);
        // 更新门店任务extendInfo、ccUserIds、handlerUserId
        taskStoreDao.updateExtendAndCcInfoAndClearHandlerUser(enterpriseId, taskStoreId, extendInfoStr, ccUserIdStr, clearHandlerUser);
    }

    private List<String> toListByString(String userIdStr){
        if (StringUtils.isBlank(userIdStr)) {
            return Collections.emptyList();
        }
        return Arrays.asList(StringUtils.split(userIdStr, Constants.COMMA));
    }


    protected boolean filterSongXiaStoreTask(String enterpriseId, String storeId, TaskParentDO parentDO){
        return true;
    }

    /**
     * 新增业务记录
     * @param enterpriseId
     * @param taskParent
     * @param taskStore
     * @param subTaskList
     */
    protected abstract boolean addBusinessRecord(String enterpriseId, TaskParentDO taskParent, TaskStoreDO taskStore, List<TaskSubDO> subTaskList, EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting);

    /**
     * 获取截止时间
     * @param taskParent
     * @return
     */
    protected Pair<String, Long> getHandleEndTimeAndLimitHour(TaskParentDO taskParent){
        return null;
    }

}
