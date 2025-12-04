package com.coolcollege.intelligent.service.metatable.calscore.table;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.service.metatable.calscore.AbstractColumnObserver;
import com.coolcollege.intelligent.service.metatable.calscore.CalColumnScoreDTO;
import com.coolcollege.intelligent.service.metatable.calscore.CalTableResultDTO;
import com.coolcollege.intelligent.service.metatable.calscore.CalTableScoreDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: DeductScoreTableObserver
 * @Description: 扣分表
 * @date 2022-04-12 10:42
 */
public class DeductScoreTableObserver extends AbstractColumnObserver {


    @Override
    public void calTableResult(CalTableScoreDTO calTableScore, CalTableResultDTO tableResult) {
        //不适用规则 false:不计入总项数，true:计入总项数
        boolean noApplicableRule = getNoApplicableRule(calTableScore);
        int totalCalColumnNum = 0;
        int collectColumnNum = 0;
        List<CalColumnScoreDTO> voteFailList = calTableScore.getCalColumnList().stream().filter(o -> CheckResultEnum.FAIL.equals(o.getCheckResult())).filter(o -> MetaColumnTypeEnum.VETO_COLUMN.equals(o.getColumnTypeEnum())).collect(Collectors.toList());
        boolean isFail = CollectionUtils.isNotEmpty(voteFailList);
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
            //项的分数
            BigDecimal columnScore = calColumnScore.getScore().abs();
            //表的累计得分
            BigDecimal tableScore = tableResult.getResultScore().add(columnScore);
            tableResult.setResultScore(tableScore);
            //项的奖罚
            BigDecimal columnAward = calColumnScore.getRewardPenaltMoney().multiply(calColumnScore.getAwardTimes());
            tableResult.setResultAward(tableResult.getResultAward().add(columnAward));
        }
        tableResult.setTotalCalColumnNum(totalCalColumnNum);
        tableResult.setCollectColumnNum(collectColumnNum);
        tableResult.setCalTotalScore(calTableScore.getMetaTable().getTotalScore());
        tableResult.setResultScore(tableResult.getCalTotalScore().subtract(tableResult.getResultScore()));
        //总分减累计得分  存在否决项 表0分
        if(isFail){
            tableResult.setResultScore(new BigDecimal(Constants.ZERO_STR));
        }

    }

}
