package com.coolcollege.intelligent.model.device.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SetStorageStrategyDTO {

    @ApiModelProperty("是否循环存储")
    private Boolean isLoopStorage;
}
