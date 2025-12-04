package com.coolcollege.intelligent.model.question.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 问题工单任务记录vo
 * @author   zhangchenbiao
 * @date   2021-12-20 07:18
 */
@ApiModel(value = "问题工单任务记录")
@Data
public class TbQuestionRecordListVO implements Serializable {

    @ApiModelProperty("主键id自增")
    private Long id;

    @ApiModelProperty("任务名称（工单名称）")
    private String taskName;

    @ApiModelProperty("工单信息")
    private String taskInfo;

    @ApiModelProperty("检查表检查项ID")
    private Long metaColumnId;

    @ApiModelProperty("检查表检查项名称")
    private String metaColumnName;

    @ApiModelProperty("标准分")
    private BigDecimal supportScore;

    @ApiModelProperty("实际得分")
    private BigDecimal checkScore;

    @ApiModelProperty("实际奖惩")
    private BigDecimal rewardPenaltMoney;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("门店编号")
    private String storeNum;

    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("区域路径")
    private String regionPath;

    @ApiModelProperty("所属区域")
    private String regionName;

    @ApiModelProperty("工单截至时间，前端根据此字段自行拆分日期和时间")
    private Date handlerEndTime;

    @ApiModelProperty("工单状态1 : 待处理 2:待审核 endNode:已完成")
    private String status;

    @ApiModelProperty("工单状态1 : 待处理 2,3,4:待审核 endNode:已完成")
    private String nodeNo;

    @ApiModelProperty(value = "是否逾期")
    private Boolean isOverdue;

    @ApiModelProperty("处理人id")
    private String handleUserId;

    @ApiModelProperty("处理人姓名，处理人，创建的时候未空")
    private String handleUserName;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人姓名")
    private String createUserName;

    @ApiModelProperty("创建时间，前端根据此字段自行拆分日期和时间")
    private Date createTime;

    @ApiModelProperty("工单说明")
    private String taskDesc;

    @ApiModelProperty("检查表id")
    private Long metaTableId;

    @ApiModelProperty("检查表名称")
    private String metaTableName;

    @ApiModelProperty("处理任务状态")
    private String handleActionKey;

    @ApiModelProperty("一级审批人id")
    private String approveUserId;

    @ApiModelProperty("一级审批人名称")
    private String approveUserName;

    @ApiModelProperty("一级审核人审批状态")
    private String approveActionKey;

    @ApiModelProperty("二级审批人id")
    private String secondApproveUserId;

    @ApiModelProperty("二级审批人名称")
    private String secondApproveUserName;

    @ApiModelProperty("二级审核人审批状态")
    private String secondApproveActionKey;

    @ApiModelProperty("三级审批人id")
    private String thirdApproveUserId;

    @ApiModelProperty("三级审批人名称")
    private String thirdApproveUserName;

    @ApiModelProperty("三级审核人审批状态")
    private String thirdApproveActionKey;

    @ApiModelProperty("抄送人")
    private List<String> ccUserNames;

    @ApiModelProperty("完成时间")
    private Date completeTime;

    @ApiModelProperty("总时长")
    private Long totalDurationTime;

    @ApiModelProperty("总时长")
    private String totalDuration;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    @ApiModelProperty("创建类型 1 、手动  2、自动")
    private Integer createType;

    @ApiModelProperty("数据项ID 大于0: 不合格项工单 等于0:自主工单")
    private Long dataColumnId;

    @ApiModelProperty("培训内容高级设置：先学习后处理工单（1）、边学边处理（0）")
    private Boolean learnFirst;

    @ApiModelProperty("门店任务id，用作重新分配")
    private Long taskStoreId;

}