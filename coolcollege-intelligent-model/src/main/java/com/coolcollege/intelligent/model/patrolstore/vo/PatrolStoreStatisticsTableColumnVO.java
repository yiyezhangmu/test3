package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsTableColumnDTO;
import lombok.Data;

import java.util.List;

/**
 * @Description:检查表报表图表-检查项统计
 * @Author chenyupeng
 * @Date 2021/7/8
 * @Version 1.0
 */
@Data
public class PatrolStoreStatisticsTableColumnVO
{
    /**
     *
     * 最多不合格项
     */
    List<PatrolStoreStatisticsTableColumnDTO> failList;

    /**
     *
     * 最多失分项
     */
    List<PatrolStoreStatisticsTableColumnDTO> lostPointList;
}
