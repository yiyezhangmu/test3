package com.coolcollege.intelligent.model.unifytask.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * 门店任务转交
 * @author byd
 */
@ApiModel
@Data
public class UnifyStoreTaskBatchTurnDTO {

    @ApiModelProperty("门店任务id列表")
    @NotNull(message = "该任务不存在")
    private List<Long> taskStoreIdList;

    @ApiModelProperty("转交人id")
    @NotBlank(message = "转交人不能为空")
    private String turnUserId;

    @ApiModelProperty("备注")
    private String remark;
}
