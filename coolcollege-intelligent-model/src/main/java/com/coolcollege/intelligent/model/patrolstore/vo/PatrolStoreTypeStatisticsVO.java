package com.coolcollege.intelligent.model.patrolstore.vo;

import lombok.Builder;
import lombok.Data;

import java.text.NumberFormat;

/**
 * @Author suzhuhong
 * @Date 2021/10/28 16:47
 * @Version 1.0
 */
@Builder
@Data
public class PatrolStoreTypeStatisticsVO {
    /**
     * 巡店类型
     */
    private String patrolType;

    private int totalNum;

    private int taskNum;

    private int spontaneousNum;

    private String taskPercent;

    private String spontaneousPercent;

    public String getTaskPercent(){
        if(totalNum<=0) {
            return "0%";
        }
        return NumberFormat.getPercentInstance().format((taskNum*1d)/totalNum);
    }

    public String getSpontaneousPercent(){
        if(totalNum<=0) {
            return "0%";
        }
        return NumberFormat.getPercentInstance().format(1-(taskNum*1d)/totalNum);
    }
    public int getSpontaneousNum(){
        if(totalNum == 0){
            return 0;
        }
        return totalNum-taskNum;
    }
}
