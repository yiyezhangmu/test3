package com.coolcollege.intelligent.service.unifytask.resolve.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.enums.UnifyTaskDataTypeEnum;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.param.PatrolStoreBuildParam;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskPersonTaskInfoDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.service.unifytask.resolve.TaskResolveAbstractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: PatrolStoreTaskResolveImpl
 * @Description:
 * @date 2025-01-07 10:29
 */
@Slf4j
@Service
public class PatrolStoreTaskResolveImpl extends TaskResolveAbstractService<TbPatrolStoreRecordDO> {

    @Resource
    private PatrolStoreService patrolStoreService;
    @Resource
    private TaskMappingMapper taskMappingMapper;
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;

    @Override
    public TbPatrolStoreRecordDO getBusinessData(String enterpriseId, Long unifyTaskId, String storeId, long loopCount) {
        return tbPatrolStoreRecordMapper.getRecordByTaskLoopCountAndOne(enterpriseId, unifyTaskId, storeId, loopCount);
    }

    @Override
    public boolean addBusinessRecord(String enterpriseId, TaskParentDO taskParent, TaskStoreDO taskStore, List<TaskSubDO> subTaskList, EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting) {
        Long unifyTaskId = taskParent.getId();
        // 检查表ids
        List<UnifyFormDataDTO> unifyFormDataDTOList =
                taskMappingMapper.selectMappingDataByTaskId(enterpriseId, unifyTaskId);
        Set<Long> metaTableIds = unifyFormDataDTOList.stream()
                .filter(a -> UnifyTaskDataTypeEnum.STANDARD.getCode().equals(a.getType())
                        || UnifyTaskDataTypeEnum.DEFINE.getCode().equals(a.getType())
                        || UnifyTaskDataTypeEnum.AI.getCode().equals(a.getType()))
                .map(a -> Long.valueOf(a.getOriginMappingId())).collect(Collectors.toSet());
        TaskSubDO taskSubDO = subTaskList.get(0);
        // 子任务
        PatrolStoreBuildParam.PatrolStoreSubBuildParam subBuildParam = PatrolStoreBuildParam.PatrolStoreSubBuildParam.builder().subTaskId(taskSubDO.getId())
                .subBeginTime(taskSubDO.getSubBeginTime()).subEndTime(taskSubDO.getSubEndTime())
                .storeId(taskSubDO.getStoreId()).loopCount(taskSubDO.getLoopCount()).handleUserId(taskSubDO.getHandleUserId()).build();
        // 参数构建
        PatrolStoreBuildParam patrolStoreBuildParam = PatrolStoreBuildParam.builder().unifyTaskId(unifyTaskId).taskType(taskParent.getTaskType())
                .patrolType(taskParent.getTaskType()).createUserId(taskParent.getCreateUserId())
                .metaTableIds(new ArrayList<>(metaTableIds)).subBuildParams(subBuildParam).taskName(taskParent.getTaskName())
                .storeCheckSettingDO(enterpriseStoreCheckSetting).taskInfo(taskParent.getTaskInfo()).build();
        if (CollectionUtils.isNotEmpty(metaTableIds)) {
            patrolStoreBuildParam.setMetaTableId(new ArrayList<>(metaTableIds).get(0));
        }

        //幂等处理,防止重复添加
        String lockKey = enterpriseId + "_" + patrolStoreBuildParam.getUnifyTaskId() + "_" + patrolStoreBuildParam.getSubBuildParams().getStoreId()
                + "_" + patrolStoreBuildParam.getSubBuildParams().getLoopCount();
        boolean lock = false;
        boolean result = false;
        if (TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskParent.getTaskType())) {
            lock = true;
            TaskPersonTaskInfoDTO taskPersonTaskInfoDTO = JSONObject.parseObject(taskParent.getTaskInfo(), TaskPersonTaskInfoDTO.class);
            patrolStoreBuildParam.setPatrolType(taskPersonTaskInfoDTO.getExecuteWay().getWay());
        }else {
            lock = redisUtilPool.setNxExpire(lockKey, lockKey, CommonConstant.PATROL_LOCK_TIMES);
        }
        if (lock) {
            try {
                result = patrolStoreService.buildPatrolStore(enterpriseId, patrolStoreBuildParam);
            } catch (Exception e) {
                log.error("addPatrolStoreTask -> buildPatrolStore has error", e);
            } finally {
                redisUtilPool.delKey(lockKey);
            }
        } else {
            throw new ServiceException(ErrorCodeEnum.PATROL_STORE_RECORD_CREATING);
        }
        return result;
    }

    @Override
    protected Pair<String, Long> getHandleEndTimeAndLimitHour(TaskParentDO taskParent) {
        if(Objects.isNull(taskParent)){
            return null;
        }
        JSONObject taskInfoJsonObj = JSON.parseObject(taskParent.getTaskInfo());
        JSONObject patrolStoreDefined = taskInfoJsonObj.getJSONObject("patrolStoreDefined");
        String handleEndTime = null;
        Long handleLimitHour = null;
        if (patrolStoreDefined != null) {
            //巡店总结
            handleEndTime = patrolStoreDefined.getString("handleEndTime");
            //巡店签名
            handleLimitHour = patrolStoreDefined.getLong("handleLimitHour");
        }
        return Pair.of(handleEndTime, handleLimitHour);
    }
}
