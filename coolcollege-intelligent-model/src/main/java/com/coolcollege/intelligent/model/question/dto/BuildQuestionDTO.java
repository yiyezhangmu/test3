package com.coolcollege.intelligent.model.question.dto;

import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


/**
 * @author byd
 */
@ApiModel(value = "子工单创建")
@Data
public class BuildQuestionDTO {

    /**
     * 结束时间
     */
    @ApiModelProperty("工单截止时间")
    @NotNull(message = "工单截止时间不能为空")
    private Date endTime;
    /**
     * 任务名称
     */
    @ApiModelProperty("子工单名称")
    @NotBlank(message = "子工单名称不能为空")
    private String taskName;
    /**
     * 任务描述
     */
    @ApiModelProperty("子工单描述")
    private String taskDesc;

    /**
     * 门店id
     * type store:门店，region区域,group分组
     */
    @ApiModelProperty("门店id")
    @NotBlank(message = "门店id不能为空")
    private String storeId;

    /**
     * 节点信息
     */
    @NotEmpty(message = "流程相关信息不能为空")
    private List<TaskProcessDTO> process;

    /**
     * 非表单类任务传递内容
     * 例门店信息补全任务
     * “store,address....”
     */
    @ApiModelProperty("工单任务信息 ")
    private QuestionTaskInfoDTO taskInfo;

}
