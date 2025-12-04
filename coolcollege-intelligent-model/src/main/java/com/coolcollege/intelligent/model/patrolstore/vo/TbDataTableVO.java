package com.coolcollege.intelligent.model.patrolstore.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author byd
 * @date 2023-01-04 19:49
 */
@Builder
@Data
public class TbDataTableVO {

    private Long id;


    private Long metaTableId;

    /**
     * 总检查项数
     */
    private Integer allCount;

    /**
     * 不合格检查项数
     */
    private Integer unPassCount;

    /**
     * 不合格检查项数
     */
    private Integer passCount;

    /**
     * 不适用检查项数
     */
    private Integer inApplicableCount;


    /**
     * 检查表名称
     */
    private String metaTableName;

    /**
     * 检查表属性
     */
    private Integer tableProperty;

    /**
     * 检查表总分
     */
    private BigDecimal totalScore;


    /**
     * 参与计算的任务总分 根据适用项规则计算得出
     */
    private BigDecimal taskCalTotalScore;

    /**
     * 表总得分
     */
    private BigDecimal checkScore;
}
