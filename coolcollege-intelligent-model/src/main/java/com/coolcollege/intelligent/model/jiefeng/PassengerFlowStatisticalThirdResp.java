package com.coolcollege.intelligent.model.jiefeng;

/**
 * @author byd
 * @date 2025-09-10 11:43
 */


import lombok.Data;

import java.math.BigDecimal;

/**
 * AiPassengerFlowStatisticalThirdResp
 *
 * @author byd
 */
@Data
public class PassengerFlowStatisticalThirdResp {
    /**
     * 进店率
     */
    private BigDecimal inboundRate;
    /**
     * 年龄未知数量
     */
    private Integer sumAgeUnknownCount;
    /**
     * 孩童数
     */
    private Integer sumChildrenCount;
    /**
     * 进店去重数
     */
    private Integer sumDedInboundCount;
    /**
     * 出店去重数
     */
    private Integer sumDedOutboundCount;
    /**
     * 过店去重数
     */
    private Integer sumDedPassCount;
    /**
     * 双人批次数量
     */
    private Integer sumDoubleBatchCount;
    /**
     * 总客流
     */
    private Integer sumFlowCount;
    /**
     * 性别未知数量
     */
    private Integer sumGenderUnknownCount;
    /**
     * 进店客流
     */
    private Integer sumInboundCount;
    /**
     * 男数量
     */
    private Integer sumManCount;
    /**
     * 多人批次数量
     */
    private Integer sumManyBatchCount;
    /**
     * 中年数
     */
    private Integer sumMiddleCount;
    /**
     * 老年数
     */
    private Integer sumOldCount;
    /**
     * 出店客流
     */
    private Integer sumOutboundCount;
    /**
     * 过店客流
     */
    private Integer sumPassCount;
    /**
     * 单人批次数量
     */
    private Integer sumSingleBatchCount;
    /**
     * 女数量
     */
    private Integer sumWomanCount;
    /**
     * 青年数
     */
    private Integer sumYoungCount;
}