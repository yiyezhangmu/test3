package com.coolcollege.intelligent.model.unifytask.request;

import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zhangchenbiao
 * @FileName: StoreTaskClearRequest
 * @Description: 门店任务日清
 * @date 2022-06-28 11:23
 */
@Data
public class StoreTaskClearRequest {

    @NotBlank
    @ApiModelProperty("门店ID")
    private String storeId;

    @NotNull
    @ApiModelProperty("时间周期值")
    private Integer timeUnion;

    @NotNull
    @ApiModelProperty("时间周期")
    private TimeCycleEnum timeCycle;

    @ApiModelProperty("完成状态")
    private Boolean isFinish;

    @ApiModelProperty("逾期状态")
    private Boolean isOverDue;

}
