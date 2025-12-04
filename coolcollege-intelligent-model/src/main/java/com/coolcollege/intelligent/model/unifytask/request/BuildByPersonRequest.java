package com.coolcollege.intelligent.model.unifytask.request;

import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskPersonTaskInfoDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 新增按人任务request
 * @author zhangnan
 * @date 2022-04-14 15:35
 */
@Data
public class BuildByPersonRequest {

    @ApiModelProperty(value = "计划名称")
    @NotNull(message = "开始时间不能为空")
    private String taskName;

    @ApiModelProperty(value = "有效期-开始时间")
    @NotNull(message = "开始时间不能为空")
    private Long beginTime;

    @ApiModelProperty(value = "有效期-结束时间")
    @NotNull(message = "结束时间不能为空")
    private Long endTime;

    @ApiModelProperty(value = "任务描述")
    private String taskDesc;

    @ApiModelProperty(value = "任务类型：PATROL_STORE_PLAN（计划巡店）")
    @NotBlank(message = "任务类型不能为空")
    private String taskType;

    @ApiModelProperty(value = "检查表信息，与原巡店任务结构一致")
    private List<GeneralDTO> form;

    /**
     * @see TaskPersonTaskInfoDTO
     */
    @ApiModelProperty(value = "任务信息", dataType = "com.coolcollege.intelligent.model.unifytask.dto.TaskPersonTaskInfoDTO")
    private String taskInfo;


    @ApiModelProperty(value = "节点信息，与原巡店任务结构一致")
    @NotEmpty(message = "流程相关信息不能为空")
    private List<TaskProcessDTO> process;

}
