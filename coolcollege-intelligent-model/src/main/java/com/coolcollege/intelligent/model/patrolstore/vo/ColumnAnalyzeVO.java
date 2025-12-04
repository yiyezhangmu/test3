package com.coolcollege.intelligent.model.patrolstore.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2021/11/19 14:47
 * @Version 1.0
 */
@Data
@Builder
public class ColumnAnalyzeVO {
    /**
     * 门店id
     */
    private String storeId;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 区域名称
     */
    private String regionName;
    /**
     * 门店数量
     */
    private int storeNum;

    /**
     * 检查门店数
     */
    private int checkStoreNum;

    /**
     * 检查次数
     */
    private int checkNum;

    /**
     * 问题工单数
     */
    private int questionNum;

    /**
     * 检查项数
     */
    private int columnNum;

    /**
     * 合格项数
     */
    private int qualifiedNum;

    private Boolean containSubArea;
}
