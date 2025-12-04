package com.coolcollege.intelligent.model.question.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 问题工单任务记录查询请求参数
 * @author zhangnan
 * @date 2021-12-21 19:13
 */
@Data
public class TbQuestionRecordSearchDTO {

    private String enterpriseId;

    /**
     * 区域地址
     */
    private String fullRegionPath;

    /**
     * 多区域地址
     */
    private List<String> fullRegionPathList;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店id
     */
    private List<String> storeIds;

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
    private Date beginCreateDate;

    /**
     * 创建时间结束日期
     */
    private Date endCreateDate;

    /**
     * 检查表id
     */
    private Long metaTableId;

    /**
     * 检查项ids
     */
    private List<Long> metaColumnIds;

    /**
     * 工单名称
     */
    private String taskName;

    /**
     * 审批人id
     */
    private String approveUserId;

    /**
     * @see com.coolcollege.intelligent.common.enums.patrol.QuestionTypeEnum
     * 工单来源
     */
    private String questionType;

    /**
     * 二级审批人id
     */
    private String secondApproveUserId;

    /**
     * 三级审批人id
     */
    private String thirdApproveUserId;

    /**
     * 任务id
     */
    private Long unifyTaskId;

    /**
     * 父工单id
     */
    private Long questionParentInfoId;


    private List<Long> questionParentInfoIdList;

    /**
     * 节点
     */
    private String nodeNo;

    /**
     * 是否为管理员
     */
    private Boolean isAdmin;
}
