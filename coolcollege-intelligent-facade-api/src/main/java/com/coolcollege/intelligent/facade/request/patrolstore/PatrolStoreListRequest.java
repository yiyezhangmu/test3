package com.coolcollege.intelligent.facade.request.patrolstore;

import com.coolcollege.intelligent.facade.request.PageRequest;
import lombok.Data;

import java.util.Date;

/**
 * @author byd
 * @date 2022-07-11 10:30
 */
@Data
public class PatrolStoreListRequest extends PageRequest {

    /**
     * 区域id
     */
    private Long regionId;

    /**
     * 开始时间
     */
    private Long beginTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 巡店类型
     * PATROL_STORE_ONLINE：线上巡店
     * PATROL_STORE_OFFLINE：线下巡店
     * PATROL_STORE_PICTURE_ONLINE：定时巡检
     * PATROL_STORE_AI：AI巡检
     * STORE_SELF_CHECK：交叉巡店
     * PATROL_STORE_PLAN：计划巡店
     * PATROL_STORE_FORM：表单巡店
     */
    private String patrolType;

    /**
     * 状态 0:待处理 1：已完成 2:待审批 3：未开始
     */
    private Integer status;
}
