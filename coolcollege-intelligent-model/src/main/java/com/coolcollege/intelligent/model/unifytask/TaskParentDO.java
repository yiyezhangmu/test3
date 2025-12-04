package com.coolcollege.intelligent.model.unifytask;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/26 15:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskParentDO {
    /**
     * ID
     */
    private Long id;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务类型
     */
    private String taskType;
    /**
     * 开始时间
     */
    private Long beginTime;
    /**
     * 结束时间
     */
    private Long endTime;
    /**
     * 任务创建者
     */
    private String createUserId;
    /**
     * 任务创建时间
     */
    private Long createTime;
    /**
     * 任务更新者
     */
    private String updateUserId;
    /**
     * 任务更新时间
     */
    private Long updateTime;
    /**
     * 任务描述
     */
    private String taskDesc;
    /**
     * 对应定时任务id
     */
    private String scheduleId;
    /**
     * 对应流程模板id
     */
    private String templateId;
    /**
     * 父任务状态
     */
    private String parentStatus;
    /**
     * 节点信息
     */
    private String nodeInfo;
    /**
     * 创建人名称
     */
    private String createUserName;
    /**
     * 任务周期 DAY MONTH YEAR
     */
    private String taskCycle;
    /**
     * 运行规则ONCE单次/LOOP循环
     */
    private String runRule;
    /**
     * 执行日期（周(1234567)，月(1~31)
     */
    private String runDate;
    /**
     * 定时任务执行时间,例12:00
     */
    private String calendarTime;
    /**
     * 非表单类任务传递内容
     * 例门店信息补全任务
     * “store,address....”
     */
    private String taskInfo;
    /**
     * 子任务限制时间
     */
    private Double limitHour;
    /**
     * 循环任务循环轮次
     */
    private Long loopCount;

    /**
     * 附件地址
     */
    private String attachUrl;

    private Boolean regionModel;

    private String extraParam;

    private Long storeOpenRuleId;
    //百丽商品货号
    private String productNo;

    /**
     * 任务状态 1：未停止 0：已停止
     */
    private Integer statusType;

    private List<String> collaboratorId;

    /**
     * 是否逾期可执行
     */
    private String isOperateOverdue;

    /**
     * 是否进行AI审核 : 0 不需要  1 需要'
     */
    private Boolean aiAudit;
}
