package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import lombok.Data;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: TaskStoreClearVO
 * @Description:
 * @date 2022-06-28 16:27
 */
@Data
public class TaskStoreClearVO {

    private Integer timeUnion;

    private Boolean isFinish;

    private TimeCycleEnum timeCycle;

    private Integer totalNum;

    private Integer finishNum;

    private List<StoreTaskClearVO> taskList;

    public TaskStoreClearVO(Integer timeUnion, Boolean isFinish, TimeCycleEnum timeCycle) {
        this.timeUnion = timeUnion;
        this.isFinish = isFinish;
        this.timeCycle = timeCycle;
    }

    public TaskStoreClearVO(Integer timeUnion, Boolean isFinish, TimeCycleEnum timeCycle, Integer totalNum, Integer finishNum, List<StoreTaskClearVO> taskList) {
        this.timeUnion = timeUnion;
        this.isFinish = isFinish;
        this.timeCycle = timeCycle;
        this.totalNum = totalNum;
        this.finishNum = finishNum;
        this.taskList = taskList;
    }
}
