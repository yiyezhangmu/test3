package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhangnan
 * @description: 按人任务vo
 * @date 2022/4/17 12:36 PM
 */
@Data
public class GetTaskByPersonVO {

    @ApiModelProperty(value = "任务id")
    private Long id;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "任务说明")
    private String taskDesc;

    @ApiModelProperty(value = "有效期-开始时间")
    private Long beginTime;

    @ApiModelProperty(value = "有效期-结束时间")
    private Long endTime;

    @ApiModelProperty(value = "创建人id")
    private String createUserId;

    @ApiModelProperty(value = "创建人姓名")
    private String createUserName;

    @ApiModelProperty(value = "任务检查表数据，与创建任务时的入参一致")
    private List<GeneralDTO> form;

    @ApiModelProperty(value = "任务信息：执行要求，执行方式都在里面，与创建任务时的入参一致")
    private String taskInfo;

    @ApiModelProperty(value = "是否逾期")
    private Boolean isOverdue;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    /**
     * 处理人流程节点
     */
    @ApiModelProperty(value = "处理人流程节点")
    private List<TaskProcessVO> handlerProcess;

    private String parentStatus;
}
