package com.coolcollege.intelligent.model.storework.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author suzhuhong
 * @Date 2022/9/21 10:21
 * @Version 1.0
 */
@Data
public class StoreWorkOverViewDataDTO {

    /**
     * 总项数
     */
    private Integer totalColumnNum;

    /**
     * 点评过的表的总项数
     */
    private Integer commentTotalColumnNum;
    /**
     * 合格项数
     */
    private Integer passColumnNum;

    /**
     * 点评过检查表数量
     */
    private Integer commentTableNum;
    /**
     * 不合格项数
     */
    private Integer failColumnNum;
    /**
     * 不适用项数
     */
    private Integer inapplicableColumnNum;
    /**
     *采集项数
     */
    private Integer collectColumnNum;
    /**
     * 总分
     */
    private BigDecimal totalScore;
    /**
     * 得分
     */
    private BigDecimal score;
}
