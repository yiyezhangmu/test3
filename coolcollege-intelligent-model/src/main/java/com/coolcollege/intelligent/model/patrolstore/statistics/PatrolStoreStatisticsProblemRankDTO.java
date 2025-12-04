package com.coolcollege.intelligent.model.patrolstore.statistics;

import lombok.Data;

import java.text.NumberFormat;

/**
 * @author shuchang.wei
 * @date 2021/4/27 17:19
 */
@Data
public class PatrolStoreStatisticsProblemRankDTO extends PatrolStoreStatisticsRankDTO{
    int finishQuestionNum;

    String finishPercent;

    public String getFinishPercent() {
        if(finishQuestionNum == 0){
            return "0%";
        }else {
            return NumberFormat.getPercentInstance().format((finishQuestionNum*1d)/getCount());

        }
    }
}
