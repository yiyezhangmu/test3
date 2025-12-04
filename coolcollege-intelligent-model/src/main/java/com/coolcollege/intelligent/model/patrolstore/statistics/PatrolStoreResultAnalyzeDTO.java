package com.coolcollege.intelligent.model.patrolstore.statistics;

import lombok.Data;

/**
 * @author shuchang.wei
 * @date 2021/7/8 14:09
 */
@Data
public class PatrolStoreResultAnalyzeDTO {
    /**
     * 合格数
     */
    private int qualifiedNum;

    /**
     * 不合格数
     */
    private int unqualifiedNum;

    /**
     * 不适用数
     */
    private int unsuitableNum;

    /**
     * 总数
     */
    private int totalNum;
}
