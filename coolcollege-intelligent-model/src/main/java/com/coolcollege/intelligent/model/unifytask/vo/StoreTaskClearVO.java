package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: StoreTaskClearVO
 * @Description: 门店日清任务
 * @date 2022-06-30 15:11
 */
@Data
public class StoreTaskClearVO {

    private Long id;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    @ApiModelProperty("任务处理时间")
    private Date handleTime;

    @ApiModelProperty("审批链任务开始时间")
    private Date subBeginTime;

    @ApiModelProperty("审批链任务结束时间")
    private Date subEndTime;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("任务数据")
    private String taskInfo;

    @ApiModelProperty("任务类型:陈列，巡店，工单 等,来源父任务")
    private String taskType;

    @ApiModelProperty("子任务状态")
    private String subStatus;

    @ApiModelProperty("当前流程进度节点")
    private String nodeNo;

    @ApiModelProperty("循环任务循环轮次")
    private Long loopCount;

    @ApiModelProperty("是否逾期")
    private Boolean isOverDue;

    @ApiModelProperty("是否完成")
    private Boolean isFinish;

    public static StoreTaskClearVO convertVO(TaskStoreDO taskStoreDO){
        if(Objects.isNull(taskStoreDO)){
            return null;
        }
        StoreTaskClearVO storeTaskClearVO = new StoreTaskClearVO();
        storeTaskClearVO.setId(taskStoreDO.getId());
        storeTaskClearVO.setStoreId(taskStoreDO.getStoreId());
        storeTaskClearVO.setUnifyTaskId(taskStoreDO.getUnifyTaskId());
        storeTaskClearVO.setHandleTime(taskStoreDO.getHandleTime());
        storeTaskClearVO.setSubBeginTime(taskStoreDO.getSubBeginTime());
        storeTaskClearVO.setSubEndTime(taskStoreDO.getSubEndTime());
        storeTaskClearVO.setTaskName(taskStoreDO.getTaskName());
        storeTaskClearVO.setTaskInfo(taskStoreDO.getTaskInfo());
        storeTaskClearVO.setTaskType(taskStoreDO.getTaskType());
        storeTaskClearVO.setSubStatus(taskStoreDO.getSubStatus());
        storeTaskClearVO.setNodeNo(taskStoreDO.getNodeNo());
        storeTaskClearVO.setLoopCount(taskStoreDO.getLoopCount());
        storeTaskClearVO.setIsOverDue(Boolean.FALSE);
        return storeTaskClearVO;
    }

    public static List<StoreTaskClearVO> convertVOList(List<TaskStoreDO> taskList){
        if(CollectionUtils.isEmpty(taskList)){
            return Lists.newArrayList();
        }
        List<StoreTaskClearVO> resultList = new ArrayList<>();
        for (TaskStoreDO taskStoreDO : taskList) {
            StoreTaskClearVO storeTaskClearVO = new StoreTaskClearVO();
            storeTaskClearVO.setId(taskStoreDO.getId());
            storeTaskClearVO.setStoreId(taskStoreDO.getStoreId());
            storeTaskClearVO.setUnifyTaskId(taskStoreDO.getUnifyTaskId());
            storeTaskClearVO.setHandleTime(taskStoreDO.getHandleTime());
            storeTaskClearVO.setSubBeginTime(taskStoreDO.getSubBeginTime());
            storeTaskClearVO.setSubEndTime(taskStoreDO.getSubEndTime());
            storeTaskClearVO.setTaskName(taskStoreDO.getTaskName());
            storeTaskClearVO.setTaskInfo(taskStoreDO.getTaskInfo());
            storeTaskClearVO.setTaskType(taskStoreDO.getTaskType());
            storeTaskClearVO.setSubStatus(taskStoreDO.getSubStatus());
            storeTaskClearVO.setNodeNo(taskStoreDO.getNodeNo());
            storeTaskClearVO.setLoopCount(taskStoreDO.getLoopCount());
            storeTaskClearVO.setIsOverDue(Boolean.FALSE);
            storeTaskClearVO.setIsFinish(Boolean.FALSE);
            resultList.add(storeTaskClearVO);
        }
        return resultList;
    }

}
