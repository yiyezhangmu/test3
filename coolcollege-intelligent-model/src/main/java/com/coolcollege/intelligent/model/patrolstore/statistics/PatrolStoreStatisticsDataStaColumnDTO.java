package com.coolcollege.intelligent.model.patrolstore.statistics;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @author byd
 */
@Data
public class PatrolStoreStatisticsDataStaColumnDTO {

    /**
     * 总分
     */
    private BigDecimal totalScore;

    /**
     * 检查表id
     */
    private Long metaTableId;
}
