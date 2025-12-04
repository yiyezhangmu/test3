package com.coolcollege.intelligent.model.patrolstore.vo;

import lombok.Builder;
import lombok.Data;

import java.text.NumberFormat;

/**
 * @Author suzhuhong
 * @Date 2021/10/28 17:41
 * @Version 1.0
 */
@Builder
@Data
public class PatrolStoreTaskStatisticsVO {

    private String patrolType;

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
