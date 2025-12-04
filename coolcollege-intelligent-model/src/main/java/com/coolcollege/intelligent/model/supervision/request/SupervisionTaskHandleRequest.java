package com.coolcollege.intelligent.model.supervision.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author wxp
 * @Date 2023/2/1 19:15
 * @Version 1.0
 */
@Data
@ApiModel
public class SupervisionTaskHandleRequest {

    @ApiModelProperty("督导任务ID")
    @NotNull(message = "督导任务ID不能为空")
    private Long supervisionTaskId;

    @ApiModelProperty("手动填写的文本")
    @NotBlank(message = "文字描述不能为空")
    private String manualText;

    @ApiModelProperty("手动上传的图片")
    @NotBlank(message = "图片不能为空")
    private String manualPics;

    @ApiModelProperty("手动上传的附件")
    private String manualAttach;

    @ApiModelProperty("是否提交")
    private Boolean submit;

    @ApiModelProperty(hidden = true)
    private Integer taskState;
}
