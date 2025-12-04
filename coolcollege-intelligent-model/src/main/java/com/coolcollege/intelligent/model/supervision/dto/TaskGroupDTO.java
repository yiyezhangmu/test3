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
public class TaskGroupDTO {

    @ApiModelProperty("分组编号")
    private String groupCode;

    @ApiModelProperty("分组名称")
    private String groupName;
}
