package com.coolcollege.intelligent.model.patrolstore.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 检查表报表
 * @Author chenyupeng
 * @Date 2021/7/6
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsTableDTO {

    /**
     * 检查次数
     */
    Integer checkedTimes;

    /**
     * 检查门店数
     */
    Integer checkedStore;

    /**
     * 检查总分
     */
    BigDecimal sumScore;

    /**
     * 发起工单数
     */
    Integer allWorkOrderNum;

    /**
     * 已完成工单数
     */
    Integer comWorkOrderNum;

    /**
     * 区域/门店排行
     */
    List<PatrolStoreStatisticsTableRankDTO> scoreRankList;

    /**
     * 区域/门店对比
     */
    List<PatrolStoreStatisticsTableRankDTO> scoreList;

    /**
     * 门店名称
     */
    String storeName;

    /**
     * 巡检结果优秀个数
     */
    Integer excellent;

    /**
     * 巡检结果良好个数
     */
    Integer good;

    /**
     * 巡检结果合格个数
     */
    Integer eligible;

    /**
     * 巡检结果不合格个数
     */
    Integer disqualification;

    /**
     * 区域路径
     */
    String regionPath;

    /**
     * 门店id
     */
    String storeId;

    BigDecimal taskCalTotalScore;

    BigDecimal checkScore;

    BigDecimal percent;
}
