package com.coolcollege.intelligent.service.metatable.calscore;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: CalResultDTO
 * @Description: 计算结果DTO
 * @date 2022-04-13 17:04
 */
@Data
public class CalResultDTO {

    /**
     * 总得分
     */
    private BigDecimal resultScore;

    /**
     * 总得奖罚
     */
    private BigDecimal resultAward;

    /**
     * 任务  参与计算的表总分
     */
    private BigDecimal calTotalScore;

    /**
     * 任务 参与计算的总项数
     */
    private Integer totalCalColumnNum;

    /**
     * 任务 采集项的项数
     */
    private Integer collectColumnNum;

    /**
     * 多个表的明细
     */
    private List<CalTableResultDTO> tableList;

}
