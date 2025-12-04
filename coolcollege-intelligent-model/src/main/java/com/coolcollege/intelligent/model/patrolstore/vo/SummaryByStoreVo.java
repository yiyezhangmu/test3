package com.coolcollege.intelligent.model.patrolstore.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author suzhuhong
 * @Date 2021/11/17 20:12
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryByStoreVo {

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
