package com.coolcollege.intelligent.model.question.request;

import com.coolcollege.intelligent.model.question.dto.BuildQuestionDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;


/**
 * @author byd
 */
@ApiModel(value = "工单创建")
@Data
public class BuildQuestionRequest {

    /**
     * 任务名称
     */
    @ApiModelProperty("父工单名称")
    @NotBlank(message = "父工单不能为空")
    private String taskName;

    @ApiModelProperty("子工单列表")
    @NotEmpty(message = "子工单不能为空")
    private List<BuildQuestionDTO> questionList;

    @ApiModelProperty("AI工单:AI、普通检查项:common、巡店工单:patrolStore, 店务工单:storeWork, 复审工单:patrolRecheck, 稽核工单:safetyCheck")
    private String questionType;

    private String extraParam;
}
