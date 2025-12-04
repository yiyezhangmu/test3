package com.coolcollege.intelligent.model.patrolstore.statistics;

import lombok.Data;

import java.text.NumberFormat;

/**
 * @author shuchang.wei
 * @date 2021/4/23 16:18
 */
@Data
public class PatrolStoreTaskStatisticsDTO {
    private int totalNum;

    private int onTimeNum;

    private int timeOutNum;

    private String onTimePercent;

    private String timeOutPercent;

    public String getOnTimePercent(){
        if(totalNum<=0) {
            return "0%";
        }
        return NumberFormat.getPercentInstance().format((onTimeNum*1d)/totalNum);
    }

    public String getTimeOutPercent(){
        if(totalNum<=0) {
            return "0%";
        }
        return NumberFormat.getPercentInstance().format(1-(onTimeNum*1d)/totalNum);
    }
    public int getTimeOutNum(){
        if(totalNum == 0){
            return 0;
        }
        return totalNum-onTimeNum;
    }
}
