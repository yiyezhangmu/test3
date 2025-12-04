package com.coolcollege.intelligent.model.question.request;

import com.coolcollege.intelligent.model.page.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 问题工单任务记录查询请求参数
 * @author zhangnan
 * @date 2021-12-21 19:13
 */
@ApiModel(value = "工单管理记录查询请求参数")
@Data
public class QuestionRecordListRequest extends PageRequest {

    /**
     * 工单状态
     */
    @ApiModelProperty(value = "状态, 1 : 待处理 2:待审核 endNode:已完成")
    private String status;
    /**
     * 工单名称
     */
    @ApiModelProperty(value = "工单名称")
    private String questionName;


    @ApiModelProperty(value = "是否逾期 1:逾期 0:不逾期")
    private Boolean overdue;

    @NotEmpty(message = "父工单id不能为空")
    @ApiModelProperty(value = "父工单id")
    private Long questionParentInfoId;

    /**
     *
     */
    @ApiModelProperty(value = "节点, 1 : 待处理 2:一级审核  2:一级审核 3:二级审核 4:三级审核 endNode:已完成")
    private String nodeNo;
}
