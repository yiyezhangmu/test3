package com.coolcollege.intelligent.model.patrolstore.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author suzhuhong
 * @Date 2021/10/28 11:11
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatrolStoreStatisticsRankVO {

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 巡店数量
     */
    private int count = 0;
}