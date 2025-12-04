package com.coolcollege.intelligent.model.unifytask.dto;

import com.coolcollege.intelligent.model.unifytask.vo.TaskParentVO;
import com.github.pagehelper.PageInfo;
import lombok.Data;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/31 17:34
 */
@Data
public class SubTaskDTO {
    /**
     * 任务列表
     */
    private PageInfo pageInfo;
    /**
     * 统计信息
     */
    private UnifySubStatisticsDTO statistics;
    /**
     * 父任务信息
     */
    private TaskParentVO parent;

    /**
     * 开始时间
     */
    private Long subBeginTime;
    /**
     * 结束时间
     */
    private Long subEndTime;

    /**
     * 处理结束时间
     */
    private Long handerEndTime;
}
