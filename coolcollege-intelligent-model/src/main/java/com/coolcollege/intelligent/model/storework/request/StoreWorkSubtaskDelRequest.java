package com.coolcollege.intelligent.model.storework.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: hu hu
 * @Date: 2025/1/9 10:20
 * @Description: 店务子任务删除
 */
@Data
@ApiModel("店务子任务删除")
public class StoreWorkSubtaskDelRequest {

    @NotEmpty(message = "需要删除的门店数据不能为空")
    @ApiModelProperty("门店id集合")
    private List<String> storeIds;

    @NotNull(message = "店务id不能为空")
    @ApiModelProperty("店务id")
    private Long storeWorkId;

    @NotBlank(message = "店务日期不能为空")
    @ApiModelProperty("店务日期")
    private String storeWorkDate;
}
