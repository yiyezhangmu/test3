package com.coolcollege.intelligent.model.patrolstore.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2021/11/18 14:15
 * @Version 1.0
 */
@Data
@Builder
public class PatrolStoreStatisticsUserRankVO {

    private String userId;

    private String userName;

    /**
     * 巡店次数
     */
    private Integer patrolNum;
}
