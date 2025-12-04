package com.coolcollege.intelligent.model.patrolstore.statistics;

import lombok.Data;

import java.text.NumberFormat;

/**
 * @author shuchang.wei
 * @date 2021/4/23 16:09
 */
@Data
public class PatrolStoreTypeStatisticsDTO{
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
