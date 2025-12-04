package com.coolcollege.intelligent.model.unifytask.dto;

import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import lombok.Data;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: TaskNumDTO
 * @Description: 任务数量
 * @date 2022-06-29 9:57
 */
@Data
public class TaskNumDTO {

    private Integer taskBeginDate;

    private Integer taskEndDate;

    private String nodeNo;

    private Integer taskNum;

    /**
     * 是否完成
     * @param taskNumList
     * @param timeUnion
     * @param maxTimeCycle
     * @return
     */
    public static Boolean isNoComplete(List<TaskNumDTO> taskNumList, Integer timeUnion, Integer maxTimeCycle){
        Boolean isFinish = null;
        for (TaskNumDTO taskNumDTO : taskNumList) {
            if(taskNumDTO.getTaskBeginDate() <= maxTimeCycle && taskNumDTO.getTaskEndDate() >= timeUnion){
                if(!UnifyNodeEnum.END_NODE.getCode().equals(taskNumDTO.getNodeNo())){
                    return false;
                }
                isFinish = true;
            }
        }
        return isFinish;
    }

}
