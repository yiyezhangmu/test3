package com.coolcollege.intelligent.model.storework.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ColumnListRequest {

    @ApiModelProperty(value = "店务id")
    private String swWorkId;

    @ApiModelProperty("tableMappingId")
    private String tableMappingId;
}
