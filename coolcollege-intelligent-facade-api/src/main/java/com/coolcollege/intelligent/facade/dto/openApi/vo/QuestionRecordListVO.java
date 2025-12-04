package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/7/15 10:36
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRecordListVO {
    /**
     * @ApiModelProperty("主键id自增")
     */
    private Long id;
    /**
     * @ApiModelProperty("父任务id")
     */
    private Long unifyTaskId;
    /**
     * @ApiModelProperty("门店id")
     */
    private String storeId;
    /**
     * @ApiModelProperty("门店任务id")
     */
    private Long taskStoreId;

    /**
     *  @ApiModelProperty("创建类型 1 、手动  2、自动")
     */
    private Integer createType;

    /**
     *  @ApiModelProperty("创建时间")
     */
    private Date createTime;

    /**
     *  @ApiModelProperty("创建人id")
     */
    private String createUserId;

    /**
     *  @ApiModelProperty("更新时间")
     */
    private Date updateTime;

    /**
     * @ApiModelProperty("删除标识")
     */
    private Boolean deleted;

    /**
     *  @ApiModelProperty("门店名称")
     */
    private String storeName;

    /**
     *  @ApiModelProperty("区域ID")
     */
    private Long regionId;

    /**
     *  @ApiModelProperty("区域路径")
     */
    private String regionPath;

    /**
     *   @ApiModelProperty("检查表id")
     */
    private Long metaTableId;

    /**
     *  @ApiModelProperty("检查表检查项ID")
     */
    private Long metaColumnId;

    /**
     *  @ApiModelProperty("数据项ID 大于0: 不合格项工单 等于0:自主工单")
     */
    private Long dataColumnId;

    /**
     *   @ApiModelProperty("处理人id")
     */
    private String handleUserId;

    /**
     *   @ApiModelProperty("处理人姓名，处理人，创建的时候未空")
     */
    private String handleUserName;

    /**
     *   @ApiModelProperty("处理时间")
     */
    private Date handleTime;

    /**
     *  @ApiModelProperty("处理任务状态:pass通过 reject拒绝 rectified已整改 unneeded无需整改")
     */
    private String handleActionKey;

    /**
     *  @ApiModelProperty("备注")
     */
    private String handleRemark;

    /**
     *  @ApiModelProperty("状态, 1 : 待处理 2:待审核 endNode:已完成")
     */
    private String status;

    /**
     *  @ApiModelProperty("培训内容高级设置：先学习后处理工单（1）、边学边处理（0）")
     */
    private Boolean learnFirst;

    /**
     *  @ApiModelProperty("附件地址")
     */
    private String attachUrl;

    /**
     *  @ApiModelProperty("任务名称")
     */
    private String taskName;

    /**
     *  @ApiModelProperty("子任务审批链开始时间")
     */
    private Date subBeginTime;

    /**
     *   @ApiModelProperty("子任务审批链结束时间")
     */
    private Date subEndTime;

    /**
     *  @ApiModelProperty("完成时间")
     */
    private Date completeTime;

    /**
     *  @ApiModelProperty("审批人id")
     */
    private String approveUserId;

    /**
     *  @ApiModelProperty("审批人名称")
     */
    private String approveUserName;

    /**
     *   @ApiModelProperty("审批时间")
     */
    private Date approveTime;

    /**
     *  @ApiModelProperty("审核人审批状态")
     */
    private String approveActionKey;

    /**
     *  @ApiModelProperty("审核人提交备注")
     */
    private String approveRemark;

    /**
     *  @ApiModelProperty("二级审批人id")
     */
    private String secondApproveUserId;

    /**
     * @ApiModelProperty("二级审批人状态")
     */
    private String secondApproveActionKey;

    /**
     * @ApiModelProperty("三级审批人id")
     */
    private String thirdApproveUserId;

    /**
     * @ApiModelProperty("三级审批人状态")
     */
    private String thirdApproveActionKey;

    /**
     *   @ApiModelProperty("工单来源")
     */
    private String questionType;
}
