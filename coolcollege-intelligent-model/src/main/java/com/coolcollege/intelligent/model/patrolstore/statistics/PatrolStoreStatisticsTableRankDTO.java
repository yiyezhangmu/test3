package com.coolcollege.intelligent.model.patrolstore.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Description: 检查表报表区域/门店排行
 * @Author chenyupeng
 * @Date 2021/7/8
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsTableRankDTO {
    /**
     * 区域名称
     */
    String regionName;

    /**
     * 门店名称
     */
    String storeName;

    /**
     * 得分
     */
    BigDecimal score;
}
