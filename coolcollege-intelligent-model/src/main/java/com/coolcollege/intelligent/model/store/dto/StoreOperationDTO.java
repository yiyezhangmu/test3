package com.coolcollege.intelligent.model.store.dto;

import lombok.Data;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2021/1/21 14:03
 */
@Data
public class StoreOperationDTO {

    /**
     * 问题工单数
     */
    private Integer questionAllNum;
    /**
     * 逾期问题工单数
     */
    private Integer questionOverdueNum;
    /**
     * 任务数
     */
    private Integer taskAllNum;
    /**
     * 逾期任务数
     */
    private Integer taskOverdueNum;
    /**
     * 被巡店次数
     */
    private Integer patrolStoreNum;
}
