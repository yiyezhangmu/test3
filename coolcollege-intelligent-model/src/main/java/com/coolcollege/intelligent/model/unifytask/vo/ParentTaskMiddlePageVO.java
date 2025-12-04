package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.model.unifytask.dto.UnifyParentStatisticsDTO;
import com.github.pagehelper.PageInfo;
import lombok.Data;

/**
    wxp
 */
@Data
public class ParentTaskMiddlePageVO {

    private TaskParentVO taskParentVO;
    /**
     * 任务列表
     */
    private PageInfo pageInfo;
    /**
     * 统计信息
     */
    private UnifyParentStatisticsDTO statistics;
}
