package com.coolcollege.intelligent.model.operationboard.dto;

import com.coolcollege.intelligent.model.system.SysRoleDO;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.text.NumberFormat;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/1/11 14:00
 */
@Data
@ToString
public class PatrolTypeStatisticsDTO {
    /**
     * 总巡店次数
     */
    private Integer totalPatrolNum;

    /**
     * 自主线下巡店次数
     */
    private Integer offlineNum;

    /**
     * 自主线下巡店比例
     */
    private String offlinePercent;

    /**
     * 自主线上巡店次数
     */
    private Integer onlineNum;

    /**
     * 自主线上巡店比例
     */
    private String onlinePercent;

    /**
     * 线下巡店任务次数
     */
    private Integer offlineTaskNum;

    /**
     * 线下巡店任务比例
     */
    private String offlineTaskPercent;

    /**
     * 线上巡店任务次数
     */
    private Integer onlineTaskNum;

    /**
     * 线上巡店任务比例
     */
    private String onlineTaskPercent;

    private List<SysRoleDO> defaultRoleList;


    public String getOfflinePercent(){
        if(totalPatrolNum <= 0){
            return "-";
        }
        return NumberFormat.getPercentInstance().format((offlineNum*1d)/totalPatrolNum);
    }
    public String getOnlinePercent(){
        if(totalPatrolNum <= 0){
            return "-";
        }
        return NumberFormat.getPercentInstance().format((onlineNum*1d)/totalPatrolNum);
    }
    public String getOfflineTaskPercent(){
        if(totalPatrolNum <= 0){
            return "-";
        }
        return NumberFormat.getPercentInstance().format((offlineTaskNum*1d)/totalPatrolNum);
    }
    public String getOnlineTaskPercent(){
        if(totalPatrolNum <= 0){
            return "-";
        }
        return NumberFormat.getPercentInstance().format((onlineTaskNum*1d)/totalPatrolNum);
    }
}
