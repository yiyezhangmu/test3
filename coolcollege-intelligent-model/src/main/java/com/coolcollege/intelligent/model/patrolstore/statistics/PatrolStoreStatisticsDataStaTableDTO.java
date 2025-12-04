package com.coolcollege.intelligent.model.patrolstore.statistics;

import java.math.BigDecimal;
import java.util.Date;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 检查项基础详情表
 * 
 * @author yezhe
 * @date 2020/12/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsDataStaTableDTO {

    private static final long serialVersionUID = 1L;

    private Long dataTableId;

    /** 表ID */
    private Long bueinessId;


    /** 表ID */
    private Long businessId;


    /**
     * 门店区域
     */
    private String regionName;

    /**
     * 门店全区域路径
     */
    @Excel(name = "门店区域")
    private String fullRegionName;

    /**
     * 门店名称
     */
    @Excel(name = "门店名称")
    private String storeName;


    @Excel(name = "门店编号")
    private String storeNum;
    /**
     * 检查表名称
     */
    @Excel(name = "检查表名称")
    private String tableName;

    /**
     * 巡店人/处理人
     */
    @Excel(name = "巡店人/处理人")
    private String supervisorName;

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
     * 表单得分
     */
    @Excel(name = "表单得分")
    private BigDecimal score;

    /**
     * 奖罚得分
     */
    private Double rewardPenaltMoney;

    /**
     * 是否过期完成
     */
    @Excel(name = "是否过期完成", replace = {"已过期_true", "未过期_false", "_null"})
    private String overdue;

    /**
     * 巡店开始时间
     */
    @Excel(name = "巡店开始时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date signStartTime;

    /**
     * 巡店结束时间
     */
    @Excel(name = "巡店结束时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date signEndTime;

    /**
     * 检查时长
     */
    private Long tourTime;

    @Excel(name = "检查时长")
    private String tourTimeStr;

    public String getTourTimeStr() {
        if (tourTime == null) {
            return null;
        }
        long hour = tourTime / 1000 / 60 / 60;
        long minute = (tourTime - hour * 1000 * 60 * 60) / 1000 / 60;
        long second = (tourTime - hour * 1000 * 60 * 60 - minute * 1000 * 60) / 1000;
        return hour + "时" + minute + "分" + second + "秒";
    }

    /**
     * 巡店开始地址
     */
    @Excel(name = "巡店开始地址")
    private String signStartAddress;

    /**
     * 巡店结束地址
     */
    @Excel(name = "巡店结束地址")
    private String signEndAddress;

    /**
     * 开始地址异常
     */
    @Excel(name = "开始地址异常")
    private String signInStatus;

    /**
     * 结束地址异常
     */
    @Excel(name = "结束地址异常")
    private String signOutStatus;

    /**
     * 总检查项数
     */
    @Excel(name = "总检查项数")
    private int totalColumnCount;

    /**
     * 合格项数
     */
    @Excel(name = "合格项数")
    private int passColumnCount;

    /**
     * 不适用项数
     */
    @Excel(name = "不适用项数")
    private int inapplicableColumnCount;

    /**
     * 不合格项数
     */
    @Excel(name = "不合格项数")
    private int failColumnCount;

    /**
     * 类型
     */
    @Excel(name = "类型")
    private String patrolType;

    /**
     * 任务名称
     */
    @Excel(name = "任务名称")
    private String taskName;

    /**
     * 有效期
     */
    @Excel(name = "有效期")
    private String validTime;

    /**
     * 创建人
     */
    @Excel(name = "创建人")
    private String createUserName;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 任务说明
     */
    @Excel(name = "任务说明")
    private String taskDesc;

}
