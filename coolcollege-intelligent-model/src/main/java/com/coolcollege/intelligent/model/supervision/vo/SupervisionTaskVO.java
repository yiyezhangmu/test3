package com.coolcollege.intelligent.model.supervision.vo;

import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserSingleDTO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopListVO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.store.dto.SingleStoreDTO;
import com.coolcollege.intelligent.model.supervision.dto.ApproveInfoDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 督导任务详情VO
 * @author   wxp
 * @date   2023/2/1 19:15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupervisionTaskVO {

    @ApiModelProperty("督导任务ID")
    private Long id;

    @ApiModelProperty("任务名")
    private String taskName;

    @ApiModelProperty("任务描述")
    private String desc;

    @ApiModelProperty("门店范围")
    private String checkStoreNames;

    @ApiModelProperty("截止时间")
    private Date taskEndTime;

    @ApiModelProperty("开始时间")
    private Date taskStartTime;

    @ApiModelProperty("优先级 固定选项：紧急,一般")
    private String priority;

    @ApiModelProperty("手动填写的文本")
    private String manualText;

    @ApiModelProperty("手动上传的图片")
    private String manualPics;

    @ApiModelProperty("手动上传的附件")
    private String manualAttach;
    @ApiModelProperty("表单ID")
    private String formId;

    private String formName;

    @ApiModelProperty(value = "处理方式")
    private HandleWay handleWay;
    @ApiModelProperty(value = "检查门店ID 判断是否选择门店")
    private String checkStoreIds;
    @ApiModelProperty(value = "附件文档")
    private List<TaskSopVO> taskSopVOList;

    @ApiModelProperty(value = "即将到期标识")
    private Boolean expireFlag;
    @ApiModelProperty(value = "一期门店未分解表示 老数据 true ")
    private Boolean oldDataFlag;

    @ApiModelProperty(value = "任务状态")
    private String taskStatus;
    private Integer taskState;
    @ApiModelProperty(value = "父任务ID")
    private Long taskParentId;


    @ApiModelProperty(value = "执行人")
    private List<EnterpriseUserSingleDTO> handleUserIds;
    @ApiModelProperty(value = "审批人")
    private List<EnterpriseUserSingleDTO> firstApproveList;
    @ApiModelProperty(value = "二级审批人")
    private List<EnterpriseUserSingleDTO> secondaryApproveList;
    @ApiModelProperty(value = "三级审批人")
    private List<EnterpriseUserSingleDTO> thirdApproveList;

    @ApiModelProperty(value = "任务分组")
    private String taskGrouping;
    @ApiModelProperty(value = "转交重新分配 0-转交 1-重新分配")
    private Integer transferReassignFlag;

    private Integer currentNode;
    @ApiModelProperty(value = "审批流信息")
    private ApproveInfoDTO approveInfoDTO;

    private List<SingleStoreDTO> storeDTOList;


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