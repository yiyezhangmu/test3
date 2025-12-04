package com.coolcollege.intelligent.model.patrolstore.vo;

import lombok.Builder;
import lombok.Data;

import java.text.NumberFormat;

/**
 * @Author suzhuhong
 * @Date 2021/10/28 15:35
 * @Version 1.0
 */
@Data
@Builder
public class PatrolStoreStatisticsProblemRankVO {

    private String storeId;

    private String storeName;

    private int count = 0;

    private int finishQuestionNum;

    private String finishPercent;

    public String getFinishPercent() {
        if(finishQuestionNum == 0){
            return "0%";
        }else {
            return NumberFormat.getPercentInstance().format((finishQuestionNum*1d)/getCount());

        }
    }
}
