package com.coolcollege.intelligent.model.supervision.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2023/4/10 16:14
 * @Version 1.0
 */
@Data
@ApiModel
public class TimingInfoDetailInfoDTO {

    @ApiModelProperty("天数")
    private Integer dayNum;

    @ApiModelProperty("执行人flag ture选择  false不选择")
    private Boolean handleFlag;

    @ApiModelProperty("上级flag ture选择  false不选择")
    private Boolean superiorFlag;

}
