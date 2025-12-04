package com.coolcollege.intelligent.service.metatable.calscore.table;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.service.metatable.calscore.AbstractColumnObserver;
import com.coolcollege.intelligent.service.metatable.calscore.CalColumnScoreDTO;
import com.coolcollege.intelligent.service.metatable.calscore.CalTableResultDTO;
import com.coolcollege.intelligent.service.metatable.calscore.CalTableScoreDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: WeightTableObserver
 * @Description: 权重表
 * @date 2022-04-12 10:41
 */
public class WeightTableObserver extends AbstractColumnObserver {

    @Override
    public void calTableResult(CalTableScoreDTO calTableScore, CalTableResultDTO tableResult) {
        //不适用规则 false:不计入总项数，true:计入总项数
        Boolean noApplicableRule = getNoApplicableRule(calTableScore);
        int totalCalColumnNum = 0;
        int collectColumnNum = 0;
        BigDecimal calTotalScore = new BigDecimal(Constants.ZERO_STR);
        List<CalColumnScoreDTO> calColumnScoreList = sortColumnList(calTableScore.getCalColumnList());
        for (CalColumnScoreDTO calColumnScore : calColumnScoreList) {
            //采集项不算总分  也不计入总项数  直接累计
            if(MetaColumnTypeEnum.COLLECT_COLUMN.equals(calColumnScore.getColumnTypeEnum())){
                //采集项累加
                collectColumnNum++;
                continue;
            }
            //不适用不计入项数
            if(!noApplicableRule && CheckResultEnum.INAPPLICABLE.equals(calColumnScore.getCheckResult())){
                continue;
            }
            totalCalColumnNum++;
            calTotalScore = calTotalScore.add(calColumnScore.getColumnMaxScore().multiply(calColumnScore.getWeightPercent()).divide(new BigDecimal(Constants.ONE_HUNDRED),Constants.SCALE, BigDecimal.ROUND_HALF_UP));
            BigDecimal columnScore = getColumnCalScoreHandler(calColumnScore.getColumnTypeEnum()).calColumnScore(calTableScore, calColumnScore);
            //乘以项的权重 后的得分
            BigDecimal columnFinalScore = columnScore.multiply(calColumnScore.getWeightPercent()).divide(new BigDecimal(Constants.ONE_HUNDRED), Constants.SCALE, BigDecimal.ROUND_HALF_UP);
            //表的累计的得分
            BigDecimal tableScore = tableResult.getResultScore().add(columnFinalScore);
            tableResult.setResultScore(tableScore);
            //项的奖罚
            BigDecimal columnAward = calColumnScore.getRewardPenaltMoney().multiply(calColumnScore.getAwardTimes());
            tableResult.setResultAward(tableResult.getResultAward().add(columnAward));
        }
        tableResult.setTotalCalColumnNum(totalCalColumnNum);
        tableResult.setCollectColumnNum(collectColumnNum);
        tableResult.setCalTotalScore(calTotalScore);
    }
}
