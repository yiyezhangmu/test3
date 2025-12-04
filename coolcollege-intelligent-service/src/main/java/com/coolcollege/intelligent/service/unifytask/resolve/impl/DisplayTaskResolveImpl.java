package com.coolcollege.intelligent.service.unifytask.resolve.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.SendResult;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableRecordMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.enums.UnifyTaskDataTypeEnum;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableRecordDO;
import com.coolcollege.intelligent.model.tbdisplay.param.TbDisplayTableRecordBuildParam;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayTableRecordService;
import com.coolcollege.intelligent.service.unifytask.resolve.TaskResolveAbstractService;
import com.coolstore.base.enums.BailiInformNodeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: PatrolStoreTaskResolveImpl
 * @Description:
 * @date 2025-01-07 10:29
 */
@Slf4j
@Service
public class DisplayTaskResolveImpl extends TaskResolveAbstractService<TbDisplayTableRecordDO> {

    @Resource
    private TbDisplayTableRecordMapper tbDisplayTableRecordMapper;
    @Resource
    private TaskMappingMapper taskMappingMapper;
    @Resource
    private TbDisplayTableRecordService tbDisplayTableRecordService;

    @Override
    public TbDisplayTableRecordDO getBusinessData(String enterpriseId, Long unifyTaskId, String storeId, long loopCount) {
        return tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(enterpriseId, unifyTaskId, storeId, loopCount);
    }

    @Override
    protected boolean addBusinessRecord(String enterpriseId, TaskParentDO taskParent, TaskStoreDO taskStore, List<TaskSubDO> subTaskList, EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting) {
        Long unifyTaskId = taskParent.getId();
        String taskInfo = taskParent.getTaskInfo();
        JSONObject taskInfoJsonObj = JSON.parseObject(taskInfo);
        JSONObject tbDisplayDefineObj = taskInfoJsonObj.getJSONObject("tbDisplayDefined");
        Boolean isSupportScore = false;
        Boolean isSupportPhoto = false;
        if(tbDisplayDefineObj != null){
            isSupportScore = tbDisplayDefineObj.getBoolean("isSupportScore");
            isSupportPhoto = tbDisplayDefineObj.getBoolean("isSupportPhoto");
        }
        // 检查表ids
        List<UnifyFormDataDTO> unifyFormDataDTOList = taskMappingMapper.selectMappingDataByTaskId(enterpriseId, unifyTaskId);
        List<Long> metaTableIds = unifyFormDataDTOList.stream().filter(a -> UnifyTaskDataTypeEnum.TB_DISPLAY.getCode().equals(a.getType())).map(a -> Long.valueOf(a.getOriginMappingId())).collect(Collectors.toList());
        // 子任务
        List<TbDisplayTableRecordBuildParam.TbDisplayTableRecordSubBuildParam> subBuildParams =
                subTaskList.stream()
                        .map(a -> TbDisplayTableRecordBuildParam.TbDisplayTableRecordSubBuildParam.builder().subTaskId(a.getId())
                                .storeId(a.getStoreId()).handleUserId(a.getHandleUserId()).build())
                        .collect(Collectors.toList());
        //查询任意一个子任务的时间 同步时间到陈列巡店记录表
        TaskSubDO taskSubDO = subTaskList.get(0);
        // 参数构建
        TbDisplayTableRecordBuildParam patrolStoreBuildParam = TbDisplayTableRecordBuildParam.builder()
                .unifyTaskId(unifyTaskId)
                .createUserId(taskParent.getCreateUserId())
                .metaTableId(CollUtil.isNotEmpty(metaTableIds) ? metaTableIds.get(0) : 0L)
                .attachUrl(taskParent.getAttachUrl())
                .isSupportScore(Objects.isNull(isSupportScore) ? false : isSupportScore)
                .isSupportPhoto(Objects.isNull(isSupportPhoto) ? false : isSupportPhoto)
                .loopCount(taskStore.getLoopCount())
                .subBuildParams(subBuildParams)
                .handlerEndTime(taskStore.getHandlerEndTime())
                .subBeginTime(taskStore.getSubBeginTime())
                .handlerEndTime(taskSubDO.getHandlerEndTime())
                .subEndTime(taskStore.getSubEndTime()).build();
        tbDisplayTableRecordService.buildTbDisplayTableRecord(enterpriseId, patrolStoreBuildParam);
        if(StringUtils.isNotBlank(redisUtilPool.hashGet("msgPushEnterpriseIds", enterpriseId))){
            Map<String, Object> map = Maps.newHashMap();
            Long recordId = tbDisplayTableRecordMapper.getIdByUnifyTaskIdAndStoreIdAndLoopCount(enterpriseId, unifyTaskId, taskStore.getStoreId(), taskStore.getLoopCount());
            map.put("recordId",recordId);
            send(enterpriseId, BailiInformNodeEnum.DISPLAY_TASK_RELEASE.getCode(), map);
        }
        return true;
    }

    private void send(String enterpriseId, String bizType, Map<String,Object> map){
        //mq发送签到消息
        JSONObject data = new JSONObject();
        data.put("enterpriseId", enterpriseId);
        //模块类型巡店
        data.put("moduleType", TaskTypeEnum.TB_DISPLAY_TASK.getCode());
        //业务类型
        data.put("bizType",bizType);
        //时间戳
        data.put("timestamp", System.currentTimeMillis());
        //业务数据
        data.put("data", map);
        log.info("mq消息参数:{}",data.toJSONString());
        SendResult send = simpleMessageService.send(data.toJSONString(), RocketMqTagEnum.BAILI_STATUS_INFORM, System.currentTimeMillis() + 2000);
        log.info("发送mq消息成功返回:{}",send);
    }

    @Override
    protected Pair<String, Long> getHandleEndTimeAndLimitHour(TaskParentDO taskParent) {
        String handleEndTime = null;
        Long handleLimitHour = null;
        JSONObject taskInfoJsonObj = JSON.parseObject(taskParent.getTaskInfo());
        JSONObject tbDisplayDefined = taskInfoJsonObj.getJSONObject("tbDisplayDefined");
        log.info("taskInfo实体类", tbDisplayDefined);
        if (tbDisplayDefined != null) {
            //巡店总结
            handleEndTime = tbDisplayDefined.getString("handleEndTime");
            //巡店签名
            handleLimitHour = tbDisplayDefined.getLong("handleLimitHour");
        }
        return Pair.of(handleEndTime, handleLimitHour);
    }
}
