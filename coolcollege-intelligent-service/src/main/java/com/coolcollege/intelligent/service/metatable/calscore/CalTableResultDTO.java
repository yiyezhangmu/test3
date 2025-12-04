package com.coolcollege.intelligent.service.metatable.calscore;

import com.coolcollege.intelligent.common.constant.Constants;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: CalTableResultDTO
 * @Description: 表的分值
 * @date 2022-04-13 17:08
 */
public class CalTableResultDTO {

    /**
     * 表id
     */
    private Long dataTableId;

    /**
     * 表得分
     */
    private BigDecimal resultScore;

    /**
     * 总得奖罚
     */
    private BigDecimal resultAward;

    /**
     * 参与计算的表总分
     */
    private BigDecimal calTotalScore;

    /**
     * 参与计算的总项数
     */
    private Integer totalCalColumnNum;

    /**
     * 采集项的项数
     */
    private Integer collectColumnNum;

    public Long getDataTableId() {
        return dataTableId;
    }

    public void setDataTableId(Long dataTableId) {
        this.dataTableId = dataTableId;
    }

    public BigDecimal getResultScore() {
        if(Objects.isNull(resultScore)){
            resultScore = new BigDecimal(Constants.ZERO_STR);
        }
        return resultScore;
    }

    public void setResultScore(BigDecimal resultScore) {
        this.resultScore = resultScore;
    }

    public BigDecimal getCalTotalScore() {
        if(Objects.isNull(calTotalScore)){
            calTotalScore = new BigDecimal(Constants.ZERO_STR);
        }
        return calTotalScore;
    }

    public void setCalTotalScore(BigDecimal calTotalScore) {
        this.calTotalScore = calTotalScore;
    }

    public Integer getTotalCalColumnNum() {
        return totalCalColumnNum;
    }

    public void setTotalCalColumnNum(Integer totalCalColumnNum) {
        this.totalCalColumnNum = totalCalColumnNum;
    }

    public Integer getCollectColumnNum() {
        return collectColumnNum;
    }

    public void setCollectColumnNum(Integer collectColumnNum) {
        this.collectColumnNum = collectColumnNum;
    }

    public BigDecimal getResultAward() {
        if(Objects.isNull(resultAward)){
            resultAward = new BigDecimal(Constants.ZERO_STR);
        }
        return resultAward;
    }

    public void setResultAward(BigDecimal resultAward) {
        this.resultAward = resultAward;
    }
}
