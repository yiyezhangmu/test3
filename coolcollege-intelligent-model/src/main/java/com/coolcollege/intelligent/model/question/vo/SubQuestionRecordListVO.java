package com.coolcollege.intelligent.model.question.vo;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


/**
 * @author byd
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubQuestionRecordListVO {
    /**
     *
     */
    @ApiModelProperty("工单记录id")
    private Long id;
    /**
     *
     */
    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    /**
     *
     */
    @ApiModelProperty("门店任务id")
    private Long taskStoreId;

    /**
     *
     */
    @ApiModelProperty("轮次")
    private Long loopCount;


    /**
     *
     */
    @ApiModelProperty("状态, 1 : 待处理 2:待审核 endNode:已完成")
    private String status;


    /**
     *
     */
    @ApiModelProperty("子工单名称")
    private String taskName;

    /**
     *
     */
    @ApiModelProperty("开始时间")
    private Date subBeginTime;

    /**
     *
     */
    @ApiModelProperty("截止时间")
    private Date subEndTime;


    @ApiModelProperty("工单描述")
    private String taskDesc;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("是否逾期")
    private Boolean overdue;

    @ApiModelProperty("发起人id")
    private String createUserId;

    @ApiModelProperty("发起人姓名")
    private String createUserName;


    @ApiModelProperty("当前节点处理人")
    private List<PersonDTO> currentUserList;

    @ApiModelProperty("检查项名称")
    private String metaColumnName;

    @ApiModelProperty("检查项Id")
    private Long metaColumnId;

    /**
     * 处理人，创建的时候未空
     */
    @ApiModelProperty("处理人id")
    private String handleUserId;

    /**
     * 处理人姓名，处理人，创建的时候未空
     */
    @ApiModelProperty("处理人姓名")
    private String handleUserName;

    /**
     * 审批人id
     */
    @ApiModelProperty("审批人id")
    private String approveUserId;

    /**
     * 父工单id
     */
    @ApiModelProperty("父工单id")
    private Long parentQuestionId;

    /**
     * 审批人名称
     */
    @ApiModelProperty("审批人名称")
    private String approveUserName;

    @ApiModelProperty("是否为当前处理人")
    private Boolean handler;

    @ApiModelProperty("AI工单:AI、普通工单:common、巡店工单:patrolStore")
    private String questionType;

    @ApiModelProperty("当前流程进度节点")
    private String nodeNo;

    @ApiModelProperty("检查表id")
    private Long metaTableId;

    @ApiModelProperty("检查表名称")
    private String metaTableName;

    @ApiModelProperty("最新整改记录(审批时使用)")
    private TbQuestionHistoryVO handleHistory;

    @ApiModelProperty("处理流水")
    private List<TbQuestionHistoryVO> operateHistory;

    @ApiModelProperty("是否需要ai检查")
    private Integer isAiCheck;


    public static SubQuestionRecordListVO convertToVO(TbQuestionRecordDO recordDO){
        SubQuestionRecordListVO result = new SubQuestionRecordListVO();
        result.setId(recordDO.getId());
        result.setTaskStoreId(recordDO.getTaskStoreId());
        result.setSubBeginTime(recordDO.getSubBeginTime());
        result.setSubEndTime(recordDO.getSubEndTime());
        result.setStatus(recordDO.getStatus());
        result.setTaskName(recordDO.getTaskName());
        result.setUnifyTaskId(recordDO.getUnifyTaskId());
        result.setTaskDesc(recordDO.getTaskDesc());
        result.setStoreId(recordDO.getStoreId());
        result.setStoreName(recordDO.getStoreName());
        result.setOverdue(recordDO.getSubEndTime().before(new Date()));
        result.setCreateUserId(recordDO.getCreateUserId());
        result.setLoopCount(recordDO.getLoopCount());
        result.setMetaColumnId(recordDO.getMetaColumnId());
        result.setQuestionType(recordDO.getQuestionType());
        result.setHandleUserId(recordDO.getHandleUserId());
        result.setHandleUserName(recordDO.getHandleUserName());
        result.setApproveUserId(recordDO.getApproveUserId());
        result.setApproveUserName(recordDO.getApproveUserName());
        result.setMetaTableId(result.getMetaTableId());
        return result;
    }
}
