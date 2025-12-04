package com.coolcollege.intelligent.model.supervision.vo;

import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserSingleDTO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.supervision.dto.ApproveInfoDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/1 16:26
 * @Version 1.0
 */
@Data
public class SupervisionStoreTaskVO {

    @ApiModelProperty("自增id")
    private Long id;

    @ApiModelProperty("督导父任务ID")
    private Long taskParentId;

    @ApiModelProperty("任务名")
    private String taskName;

    @ApiModelProperty("开始时间")
    private Date taskStartTime;

    @ApiModelProperty("结束时间")
    private Date taskEndTime;

    @ApiModelProperty("督导ID")
    private String supervisionUserId;

    @ApiModelProperty("门店ID")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("待完成 待审核 按时完成 逾期完成")
    private Integer taskState;

    @ApiModelProperty("手动填写的文本")
    private String formId;
    @ApiModelProperty("表单名称")
    private String formName;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("取消状态 0正常 1取消")
    private Integer cancelStatus;

    @ApiModelProperty("提交时间")
    private Date submitTime;
    @ApiModelProperty("优先级")
    private String priority;

    @ApiModelProperty("任务备注")
    private String remark;

    @ApiModelProperty(value = "处理方式")
    private SupervisionTaskVO.HandleWay handleWay;
    @ApiModelProperty(value = "附件列表")
    private List<TaskSopVO> taskSopVOList;
    @ApiModelProperty(value = "任务描述")
    private String desc;

    @ApiModelProperty(value = "即将到期标识")
    private Boolean expireFlag;

    private String taskStatus;

    @ApiModelProperty(value = "执行人")
    private List<EnterpriseUserSingleDTO> handleUserIds;
    @ApiModelProperty(value = "审批人")
    private List<EnterpriseUserSingleDTO> firstApproveList;
    @ApiModelProperty(value = "二级审批人")
    private List<EnterpriseUserSingleDTO> secondaryApproveList;
    @ApiModelProperty(value = "三级审批人")
    private List<EnterpriseUserSingleDTO> thirdApproveList;

    @ApiModelProperty(value = "转交重新分配")
    private Integer transferReassignFlag;
    @ApiModelProperty(value = "任务分组")
    private String taskGrouping;


    private Integer currentNode;
    @ApiModelProperty(value = "审批流信息")
    private ApproveInfoDTO approveInfoDTO;

    @ApiModelProperty("督导任务表ID")
    private Long supervisionTaskId;

    private Boolean taskRejectFlag;

    private Integer handleOverTimeStatus;


    @Data
    public static class HandleWay {
        @ApiModelProperty(value = "处理方式：0 无需操作,1 填写表单,2 点击按钮")
        private Integer code;

        @ApiModelProperty(value = "按钮名称")
        private String name;
    }

}
