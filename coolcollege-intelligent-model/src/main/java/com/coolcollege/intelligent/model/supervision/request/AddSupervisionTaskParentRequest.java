package com.coolcollege.intelligent.model.supervision.request;

import com.coolcollege.intelligent.common.enums.supervison.SupervisionTaskPriorityEnum;
import com.coolcollege.intelligent.model.sop.vo.TaskSopListVO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.supervision.dto.ApproveInfoDTO;
import com.coolcollege.intelligent.model.supervision.dto.TimingInfoDTO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;


/**
 * @Author suzhuhong
 * @Date 2023/2/1 14:42
 * @Version 1.0
 */
@Data
@ApiModel
public class AddSupervisionTaskParentRequest {

    @ApiModelProperty("ID")
    private Long id ;
    @ApiModelProperty("任务名")
    private String taskName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("开始时间")
    private Date taskStartTime;

    @ApiModelProperty("结束时间")
    private Date taskEndTime;

    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("业务ID")
    private String businessId;

    @ApiModelProperty("优先级 固定选项：紧急,一般")
    private SupervisionTaskPriorityEnum priority;

    @ApiModelProperty("标签（支持多个 逗号隔开）")
    private String tags;

    @ApiModelProperty("检查门店ID")
    private List<String> checkStoreIds;

    @ApiModelProperty("检查门店")
    private List<GeneralDTO> storeIds;

    @ApiModelProperty("处理方式 {code:1,name:',id:'} code值 0 无需操作,1 填写表单,2 点击按钮")
    private String handleWay;

    @ApiModelProperty("检验code")
    private String checkCode;

    @ApiModelProperty("任务描述")
    private String desc;

    @ApiModelProperty("表单ID")
    private String formId;

    @ApiModelProperty("执行人")
    private String executePersons;

    @ApiModelProperty("附件ID集合")
    TaskSopListVO taskSopListVO;

    @ApiModelProperty("创建人ID")
    private String createUserId;

    @ApiModelProperty("1.2新增_任务分组")
    private String taskGrouping;

    @ApiModelProperty("1.2新增_定时提醒信息")
    private TimingInfoDTO timingInfoDTO;

    @ApiModelProperty("1.2新增_审批流信息")
    private ApproveInfoDTO approveInfoDTO;


}
