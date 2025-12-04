package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.common.util.NumberFormatUtils;
import lombok.Data;

import java.text.NumberFormat;

/**
 * @Author suzhuhong
 * @Date 2021/11/18 15:28
 * @Version 1.0
 */
@Data
public class HomePageVo {

    /**
     * 总门店数
     */
    private Integer storeNum;

    /**
     * 巡店次数
     */
    private Integer patrolNum;

    /**
     * 已检查门店数
     */
    private Integer patrolStoreNum;

    /**
     * 未检查门店数
     */
    private int unPatrolStoreNum;

    /**
     * 巡店覆盖率
     */
    private String patrolStorePercent;

    /**
     * 未检查门店率
     */
    private String unPatrolStorePercent;

    /**
     * 总工单数
     */
    private Integer totalQuestionNum;

    /**
     * 代办工单数
     */
    private int todoQuestionNum;
    /**
     * 待复检工单数
     */
    private int unRecheckQuestionNum;

    /**
     * 已解决问题数
     */
    private Integer finishQuestionNum;

    /**
     * 问题解决率
     */
    private String finishQuestionPercent;
    /**
     * 待整改工单率
     */
    private String doneQuestionPercent;
    /**
     * 待复检工单率
     */
    private String unRecheckPercent;

    /**
     * 巡店人数
     */
    private Integer patrolPersonNum;

    public String getFinishQuestionPercent() {
        if(finishQuestionNum ==0){
            return "0%";
        }else {
            return NumberFormatUtils.getPercentString(finishQuestionNum,getTotalQuestionNum());
        }
    }

    public String getPatrolStorePercent() {
        if(patrolStoreNum==0){
            return "0%";
        }else {
            return NumberFormatUtils.getPercentString(patrolStoreNum,getStoreNum());
        }
    }

    public String getUnPatrolStorePercent() {
        if(unPatrolStoreNum==0){
            return "0%";
        }else {
            return NumberFormatUtils.getPercentString(unPatrolStoreNum,getStoreNum());
        }
    }

    public int getUnpatrolStoreNum(){
        if (storeNum - patrolStoreNum<0){
            return 0;
        }
        return storeNum - patrolStoreNum;
    }
    public String getDoneQuestionPercent() {
        if(totalQuestionNum<=0) {
            return "0%";
        }
        return NumberFormatUtils.getPercentString(todoQuestionNum,totalQuestionNum);
    }

    public String getUnRecheckPercent() {
        if(totalQuestionNum<=0) {
            return "0%";
        }
        return NumberFormatUtils.getPercentString(unRecheckQuestionNum,totalQuestionNum);
    }

    public HomePageVo(Integer storeNum, Integer patrolNum, Integer patrolStoreNum, Integer totalQuestionNum, Integer finishQuestionNum, Integer patrolPersonNum) {
        this.storeNum = storeNum;
        this.patrolNum = patrolNum;
        this.patrolStoreNum = patrolStoreNum;
        this.totalQuestionNum = totalQuestionNum;
        this.finishQuestionNum = finishQuestionNum;
        this.patrolPersonNum = patrolPersonNum;
    }
    public HomePageVo() {
    }
}
