package com.coolcollege.intelligent.model.patrolstore.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 检查项基础详情
 * 
 * @author 叶哲
 * @date 2020/12/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsDataStaColumnQuery extends PatrolStoreStatisticsBaseQuery {

    /**
     * 区域id
     */
    private Long regionId;

    private String regionPath;

    private Long businessId;

    private String patrolType;
    /**
     * 所属门店
     */
    private String storeId;
    /**
     * 人员
     */
    private String createUserId;
    /**
     * 所属检查表
     */
    private Long metaTableId;
    /**
     * 检查项id
     */
    private Long metaColumnId;
    /**
     * 数据检查表id
     */
    private Long dataTableId;
    /**
     * 结果
     */
    private String checkResult;
    /**
     * 是否只需要有问题的检查项
     */
    private Boolean onlyQuestion;
    /**
     * 巡店状态
     */
    private Integer businessStatus;
}
