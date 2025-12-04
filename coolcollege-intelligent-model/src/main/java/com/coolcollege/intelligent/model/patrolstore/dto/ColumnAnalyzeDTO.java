package com.coolcollege.intelligent.model.patrolstore.dto;

import lombok.Data;

/**
 * @author shuchang.wei
 * @date 2021/7/9 14:17
 */
@Data
public class ColumnAnalyzeDTO {
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
}
