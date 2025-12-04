package com.coolcollege.intelligent.model.patrolstore.statistics;

import lombok.Data;

/**
 * @author 邵凌志
 * @date 2021/1/11 11:54
 */
@Data
public class PatrolStoreStatisticsRankDTO {

    private String storeId;

    private String storeName;

    private int count = 0;

    private String regionName;

    private Long regionId;
}
