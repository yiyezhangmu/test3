package com.coolcollege.intelligent.model.patrolstore.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 检查项数量统计
 * 
 * @author yezhe
 * @date 2020/12/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsDataColumnCountDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 检查表id
     */
    private Long dataTableId;

    /**
     * 检查
     */
    private Long businessId;

    /**
     * 总检查项数
     */
    private int totalColumnCount;

    /**
     * 合格检查项数
     */
    private int passColumnCount;

    /**
     * 不合格检查项数
     */
    private int inapplicableColumnCount;

    /**
     * 不适用检查项数
     */
    private int failColumnCount;

}
