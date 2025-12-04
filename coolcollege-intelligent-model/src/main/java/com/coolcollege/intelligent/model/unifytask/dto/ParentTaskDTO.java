package com.coolcollege.intelligent.model.unifytask.dto;

import com.github.pagehelper.PageInfo;
import lombok.Data;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/29 19:55
 */
@Data
public class ParentTaskDTO {
    /**
     * 任务列表
     */
    private PageInfo pageInfo;
    /**
     * 统计信息
     */
    private UnifyParentStatisticsDTO statistics;
}
