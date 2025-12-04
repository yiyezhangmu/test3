package com.coolcollege.intelligent.model.storework.request;

import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 店务定义执行信息
 * @author wxp
 * @date 2022-09-08 10:48
 */
@ApiModel
@Data
public class StoreWorkDutyInfoRequest {

    @ApiModelProperty("分组排序")
    private Integer groupNum;

    @ApiModelProperty("执行人信息")
    @NotEmpty(message = "执行人信息不能为空")
    private List<StoreWorkCommonDTO> handlePersonInfo;

    @ApiModelProperty("执行任务信息")
    @NotEmpty(message = "执行任务信息不能为空")
    private List<StoreWorkTableInfoRequest> tableInfoList;

    @ApiModelProperty("点评人信息")
    private List<StoreWorkCommonDTO> commentPersonInfo;

}
