package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author byd
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskStoreDayVO {
    /**
     * 日 逾期可执行
     */
    private List<TaskStoreDO> dayOverdueList;

    /**
     * 日 有效期可执行
     */
    private List<TaskStoreDO> dayList;

    /**
     * 周有效期可执行
     */
    private List<TaskStoreDO> weekList;

    /**
     * 周 逾期可执行
     */
    private List<TaskStoreDO> weekOverdueList;

    /**
     * 周有效期可执行
     */
    private List<TaskStoreDO> monthList;

    /**
     * 周 逾期可执行
     */
    private List<TaskStoreDO> monthOverdueList;
}
