package com.coolcollege.intelligent.model.storework.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author byd
 * @Date 2022/9/16 15:01
 * @Version 1.0
 */
@ApiModel
@Data
public class StoreWorkStatisticsExecutiveDTO {

    @ApiModelProperty("第一阶段数量 60%以下")
    private Long firstStageNum;

    @ApiModelProperty("第二阶段数量 60%-80%")
    private Long secondStageNum;

    @ApiModelProperty("第三阶段数量 80%-99%")
    private Long thirdStageNum;

    @ApiModelProperty("第四阶段数量 100%")
    private Long fourStageNum;

    @ApiModelProperty("总数量")
    private Long totalNum;
}
