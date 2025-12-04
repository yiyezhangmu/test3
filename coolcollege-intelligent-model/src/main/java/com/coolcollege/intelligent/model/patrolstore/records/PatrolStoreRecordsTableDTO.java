package com.coolcollege.intelligent.model.patrolstore.records;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PatrolStoreRecordsTableDTO extends PatrolStoreRecordsBaseDTO{
    
    

    public PatrolStoreRecordsTableDTO() {
        super();
    }

    private Long businessId;

    @Excel(name = "检查表")
    private String tableName;

    private Long metaTableId;

    /**
     * 总检查项数
     */
    @Excel(name = "总检查项数")
    private Integer totalColumnCount = 0 ;

    /**
     * 不适用项数
     */
    @Excel(name = "不适用项数")
    private Integer unQualifiedCount = 0  ;

    /**
     * 巡店人、处理人
     */
    @Excel(name = "巡店人/处理人")
    private String supervisorName;

    /**
     * 处理人
     */
    @Excel(name = "审批人")
    private String handler;

    /**
     * 复检人
     */
    @Excel(name = "复审人")
    private String reChecker;



    /**
     * 门店得分
     */
    @Excel(name = "门店得分")
    private BigDecimal score;

    /**
     * 门店评价
     */
    @Excel(name = "门店评价")
    private String storeEvaluation;

    /**
     * 是否延期完成
     */
    @Excel(name = "是否过期完成")
    private String isOverdue;

    /**
     * 巡店开始时间
     */
    @Excel(name = "巡店开始时间")
    private String signInTime;

    /**
     * 巡店结束时间
     */
    @Excel(name = "巡店结束时间")
    private String signOutTime;

    /**
     * 巡店时长
     */
    @Excel(name = "巡店时长")
    private String patrolTime;
    /**
     * 巡店地址
     */
    @Excel(name = "巡店签到地址")
    private String signInAddress;
    @Excel(name = "巡店签退地址")
    private String signEndAddress;

    @Excel(name = "签退地址异常")
    private String signOutStatus;

    /**
     * 地址异常是否异常
     */
    @Excel(name = "签到地址异常")
    private String isAddException;


    /**
     * 记录类型
     */
    @Excel(name = "类型")
    private String recordType;

    /**
     * 任务名称
     */
    @Excel(name = "任务名称")
    private String taskName;

    /**
     * 有效期
     */
    @Excel(name = "有效期")
    private String effectiveTime;

    /**
     * 创建人
     */
    @Excel(name = "创建人")
    private String createUserName;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间")
    private String createTime;

    /**
     * 任务说明
     */
    @Excel(name = "任务说明")
    private String note;

    /**
     * 流程状态
     */
    @Excel(name = "任务状态")
    private String taskStatus;

    /**
     * 巡店结果
     */
    @Excel(name = "巡店结果")
    private String checkResult;

    public String getCheckResult() {
        if (failColumnCount > 0) {
            return "不合格";
        }
        return "合格";
    }

    /**
     * 合格项数
     */
    @Excel(name = "合格项数")
    private int passColumnCount;

    /**
     * 不适用项数
     */
    private int inapplicableColumnCount;

    /**
     * 不合格项数
     */
    @Excel(name = "不合格项数")
    private int failColumnCount;

    /**
     * 表单类型 巡店检查表类型 DEFINE(自定义) STANDARD(标准检查表)
     */
    private String tableType;

}
