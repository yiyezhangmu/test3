package com.coolcollege.intelligent.model.patrolstore.query;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 门店相关统计
 * 
 * @author jeffrey
 * @date 2020/12/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsStoreQuery extends PatrolStoreStatisticsBaseQuery {
    /**
     * 区域id
     */
    private Long regionId;

    /**
     * 门店ids
     */
    private List<String> storeIdList;

    /**
     * 区域
     */
    private List<String> regionIdList;

    /**
     * 门店状态
     */
    private String storeStatus;

}
