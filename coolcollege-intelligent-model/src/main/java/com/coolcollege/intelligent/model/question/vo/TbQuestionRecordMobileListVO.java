package com.coolcollege.intelligent.model.question.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 问题工单任务记录vo（移动端）
 * @author   zhangchenbiao
 * @date   2021-12-20 07:18
 */
@ApiModel(value = "问题工单任务记录")
@Data
public class TbQuestionRecordMobileListVO implements Serializable {

    @ApiModelProperty("主键id自增")
    private Long id;

    @ApiModelProperty("任务名称（工单名称）")
    private String taskName;

    @ApiModelProperty("检查表检查项ID")
    private Long metaColumnId;

    @ApiModelProperty("检查表检查项名称")
    private String metaColumnName;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("工单截至时间，前端根据此字段自行拆分日期和时间")
    private Date handlerEndTime;

    @ApiModelProperty("工单状态1 : 待处理 2:待审核 endNode:已完成")
    private String status;

    @ApiModelProperty(value = "是否逾期")
    private Boolean isOverdue;

    @ApiModelProperty("处理人id")
    private String handleUserId;

    @ApiModelProperty("处理人姓名，处理人，创建的时候未空")
    private String handleUserName;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人")
    private String createUserName;

    @ApiModelProperty("创建时间，前端根据此字段自行拆分日期和时间")
    private Date createTime;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    @ApiModelProperty("创建类型 1 、手动  2、自动")
    private Integer createType;

    @ApiModelProperty("数据项ID 大于0: 不合格项工单 等于0:自主工单")
    private Long dataColumnId;

    @ApiModelProperty("培训内容高级设置：先学习后处理工单（1）、边学边处理（0）")
    private Boolean learnFirst;

    @ApiModelProperty("门店任务id")
    private Long taskStoreId;

    @ApiModelProperty("审批人id")
    private String approveUserId;

    @ApiModelProperty("审批人姓名")
    private String approveUserName;

    @ApiModelProperty("工单状态1 : 待处理 2,3,4:待审核 endNode:已完成")
    private String nodeNo;
}