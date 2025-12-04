package com.coolcollege.intelligent.model.operationboard.dto;

import com.coolcollege.intelligent.model.operationboard.query.BaseQuery;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.text.NumberFormat;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/1/11 13:37
 */
@Data
@ToString
public class TaskStatisticsDTO {
    /**
     * 总任务数
     */
    private Integer totalTaskNum;

    /**
     * 完成的任务数
     */
    private Integer finishTaskNum;

    /**
     * 未完成的任务数
     */
    private Integer undoTaskNum;

    /**
     * 完成的比例
     */
    private String finishTaskPer;

    /**
     * 未完成的比例
     */
    private String undoTaskPer;

    private List<SysRoleDO> defaultRoleList;

    public String getFinishTaskPer(){
        if(totalTaskNum <=0){
            return "-";
        }
        return NumberFormat.getPercentInstance().format((finishTaskNum*1d)/totalTaskNum);
    }

    public String getUndoTaskPer(){
        if(totalTaskNum <=0){
            return "-";
        }
        return NumberFormat.getPercentInstance().format((undoTaskNum*1d)/totalTaskNum);
    }
}
