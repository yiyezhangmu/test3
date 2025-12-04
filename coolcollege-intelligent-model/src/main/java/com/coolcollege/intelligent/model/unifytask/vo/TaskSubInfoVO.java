package com.coolcollege.intelligent.model.unifytask.vo;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/30 21:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSubInfoVO {
    /**
     * ID
     */
    @Id
    private Long id;
    /**
     * 父任务id
     */
    private Long unifyTaskId;
    /**
     * 任务发起者
     */
    private String createUserId;
    /**
     * 任务处理者
     */
    private String handleUserId;
    /**
     * 任务发起时间
     */
    private Long createTime;
    /**
     * 任务处理时间
     */
    private Long handleTime;
    /**
     * 任务状态
     */
    private String actionKey;
    /**
     * 备注
     */
    private String remark;
    /**
     * 门店id
     */
    private String storeId;
    /**
     * 当前流程进度节点
     */
    private String nodeNo;
    /**
     * 实例id
     */
    private String instanceId;
    /**
     * 对应流程模板id
     */
    private String templateId;
    /**
     * 子任务状态
     */
    private String subStatus;
    /**
     * 循环次数
     */
    private Long cycleCount;
    /**
     * 对应cform
     */
    private String bizCode;
    /**
     * 对应cform的cid
     */
    private String cid;
    /**
     * 转交原subid
     */
    private Long parentTurnSubId;
    /**
     * 工作流状态
     * 初始化init/进行中processed
     */
    private String flowState;
    /**
     * 一次审批流分组字段
     */
    private Long groupItem;
    /**
     * 循环任务循环轮次
     */
    private Long loopCount;
    /**
     * 任务转交者
     */
    private String turnUserId;
    private String storeName;
    private String taskName;
    private Long endTime;
    /**
     * 任务数据
     */
    private String taskData;
    /**
     * 任务类型
     */
    private String taskType;
    /**
     * 审批链任务开始时间
     */
    private Long subBeginTime;
    /**
     * 审批链任务结束时间
     */
    private Long subEndTime;
    /**
     * 非表单类任务传递内容
     * 例门店信息补全任务
     * “store,address....”
     */
    private String taskInfo;

    /**
     * 区域路径
     */
    private String storeArea;

    private Long regionId;

    /**
     * 任务截止时间
     */
    private Date handlerEndTime;

    private String isOperateOverdue;
}
