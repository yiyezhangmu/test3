package com.coolcollege.intelligent.model.patrolstore.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.common.util.NumberFormatUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;

/**
 * @Author suzhuhong
 * @Date 2021/11/19 16:58
 * @Version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PatrolStoreStatisticsRegionVO {

    private String regionId;

    private String name;

    private int storeNum;

    private int patrolStoreNum;

    private int unpatrolStoreNum;

    private int patrolPersonNum;

    private String patrolStorePercent;

    private int patrolNum;

    private int totalQuestionNum;

    private int todoQuestionNum;

    private int unRecheckQuestionNum;

    private int finishQuestionNum;

    private String doneQuestionPercent;

    private String finishQuestionPercent;

    private String unRecheckPercent;

    /**
     * 是否含有子区域
     */
    private Boolean containSubArea;

    public String getDoneQuestionPercent() {
        if(totalQuestionNum<=0) {
            return "0%";
        }
        return NumberFormatUtils.getPercentString(todoQuestionNum,totalQuestionNum);
    }
    public String getFinishQuestionPercent() {
        if(totalQuestionNum<=0) {
            return "0%";
        }
        return NumberFormatUtils.getPercentString(finishQuestionNum,totalQuestionNum);
    }
    public String getUnRecheckPercent() {
        if(totalQuestionNum<=0) {
            return "0%";
        }
        return NumberFormatUtils.getPercentString(unRecheckQuestionNum,totalQuestionNum);
    }

    public String getPatrolStorePercent() {
        if(storeNum<=0) {
            return "0%";
        }
        return NumberFormatUtils.getPercentString(patrolStoreNum,storeNum);
    }

    public int getUnpatrolStoreNum(){
        if (storeNum - patrolStoreNum<0){
            return 0;
        }
        return storeNum - patrolStoreNum;
    }
}
