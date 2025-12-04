package com.coolcollege.intelligent.model.patrolstore.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 检查表报表
 * @Author chenyupeng
 * @Date 2021/7/6
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsTableQuery extends PatrolStoreStatisticsBaseQuery{

    /**
     * 区域id列表  选择区域树查询时使用
     */
    private List<String> regionIds;

    /**
     * 门店id集合
     */
    private List<String> storeIds;

    /**
     * 检查表id
     */
    private Long metaTableId;

    /**
     * 区域路径
     */
    private String regionPath;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 优秀分数
     */
    private Integer excellentPercent;

    /**
     * 良好分数
     */
    private Integer goodPercent;

    /**
     * 合格分数
     */
    private Integer eligiblePercent;

    /**
     * 不合格分数
     */
    private Integer disqualificationPercent;

    /**
     * 优秀个数
     */
    private Integer excellentNum;

    /**
     * 良好个数
     */
    private Integer goodNum;

    /**
     * 合格个数
     */
    private Integer eligibleNum;

    /**
     * 不合格个数
     */
    private Integer disqualificationNum;

    /**
     * 等级规则,按分数:SCORING_RATE,按检查项数:ITEM_NUM
     */
    private String levelRule;
}
