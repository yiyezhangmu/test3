package com.coolcollege.intelligent.model.storework.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/9/22 15:28
 * @Version 1.0
 */
@Data
@ApiModel(value = "店务检查表转交")
public class TransferHandlerRequest {
    @ApiModelProperty(value = "店务数据表ID")
    private List<Long> storeWorkDataTableIds;
    @ApiModelProperty(value = "转交人ID")
    private String transferUserId;
}
