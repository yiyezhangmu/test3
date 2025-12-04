package com.coolcollege.intelligent.model.patrolstore.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsTableRankDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsTableRankLeLeTeaDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class PatrolStoreStatisticsTableLeLeTeaVO {
    /**
     * 检查表名称
     */
    @Excel(name = "检查表名称")
    String tableName;

    /**
     * 检查表类型
     * @Excel(name = "检查表类型")
     */
    String tableType;

    /**
     * 检查表属性
     */
    @Excel(name = "检查表属性")
    String tableProperty;

    /**
     * 检查项数
     */
    @Excel(name = "检查项数")
    Integer staColumnNum;

    /**
     * 门店总数
     */
    @Excel(name = "门店总数")
    Integer totalStoreNum;

    /**
     * 检查门店数
     */
    @Excel(name = "检查门店数")
    Integer checkedStore;

    /**
     * 检查门店率
     */
    @Excel(name = "检查门店率")
    String checkRatio;

    /**
     * 检查次数
     */
    @Excel(name = "检查次数")
    Integer checkedTimes;

    /**
     * 平均检查次数（检查次数/门店总数）
     */
    @Excel(name = "平均检查次数")
    Double avgCheckedTimes;

    /**
     * 巡店结果和比例
     */
    @Excel(name = "巡店结果和比例")
    String gradeInfo;

    /**
     * 平均得分
     */
    @Excel(name = "平均得分")
    BigDecimal avgScore;

    /**
     * 发起工单数
     */
    @Excel(name = "发起工单数")
    Integer allWorkOrderNum;

    /**
     * 已完成工单数
     */
    @Excel(name = "已完成工单数")
    Integer comWorkOrderNum;

    /**
     * 工单完成率
     */
    @Excel(name = "工单完成率")
    String comWorkOrderRatio;

    /**
     * 区域/门店排行
     */
    List<PatrolStoreStatisticsTableRankLeLeTeaDTO> scoreRankList;

    /**
     * 区域/门店对比
     */
    List<PatrolStoreStatisticsTableRankLeLeTeaDTO> scoreList;
}
