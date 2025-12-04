package com.coolcollege.intelligent.model.patrolstore.dto;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2021/11/17 20:34
 * @Version 1.0
 */
@Data
public class SummaryByStoreDTO {
    /**
     * 巡店次数
     */
    private Integer patrolNum;

    /**
     * 不适用数
     */
    private Integer unapplicableNum;
    /**
     * 不合格数
     */
    private Integer failedNum;
    /**
     * 合格数
     */
    private Integer passNum;
    /**
     * 门店Id
     */
    private String storeId;
    /**
     * 门店名称
     */
    private String StoreName;
}
