package com.coolcollege.intelligent.model.supervision.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author byd
 * @date 2023-04-17 16:10
 */
@ApiModel
@Data
@AllArgsConstructor
public class TaskLabelDTO {

    @ApiModelProperty("标签编号")
    private String labelCode;

    @ApiModelProperty("标签名称")
    private String labelName;
}
