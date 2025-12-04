package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/7/15 13:49
 * @Version 1.0
 */
@Data
public class QuestionDTO {

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 工单状态
     */
    private String status;

    /**
     * 是否逾期
     */
    private Boolean isOverdue;

    /**
     * 处理人id
     */
    private String handleUserId;

    /**
     * 创建人id
     */
    private String createUserId;

    /**
     * 创建时间开始日期
     */
    private Long beginCreateDate;

    /**
     * 创建时间结束日期
     */
    private Long endCreateDate;

    /**
     * 工单名称
     */
    private String taskName;

    /**
     * 一级审批人id
     */
    private String approveUserId;

    /**
     * 二级审批人id
     */
    private String secondApproveUserId;

    /**
     * 三级审批人id
     */
    private String thirdApproveUserId;

    private String questionType;

    private Integer pageSize;

    private Integer pageNum;

    private Long questionId;

    private Long unifyTaskId;
}
