package com.coolcollege.intelligent.model.patrolstore.statistics;

import lombok.Data;

import java.util.Date;

/**
 * @author 邵凌志
 * @date 2021/1/11 16:39
 */
@Data
public class PatrolStoreStatisticsDTO {

    private String storeId;

    private String storeName;

    /**
     * 巡店次数
     */
    private Integer count = 0;

    /**
     * 最后巡店时间
     */
    private Date lastTime;

    /**
     * 最后巡店人id
     */
    private String userId;

    /**
     * 最后巡店人
     */
    private String userName;

    /**
     * 是否检查
     */
    private boolean checked = false;
}
