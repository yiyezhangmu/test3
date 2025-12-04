package com.coolcollege.intelligent.model.patrolstore.statistics;

import lombok.*;

/**
 * 门店相关统计
 * @author jeffrey
 * @date 2020/12/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsHistoryDTO {

    private static final long serialVersionUID = 1L;

    /** 巡店次数 */
    private int patrolNum;
    /**
     * 总问题数
     */
    private int totalQuestionNum;
    /**
     * 待整改问题数
     */
    private int todoQuestionNum;


}
