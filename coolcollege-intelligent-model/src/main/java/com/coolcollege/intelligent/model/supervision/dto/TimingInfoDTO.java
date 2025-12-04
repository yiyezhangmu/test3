package com.coolcollege.intelligent.model.supervision.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2023/4/10 16:13
 * @Version 1.0
 */
@Data
@ApiModel
public class TimingInfoDTO {

    @ApiModelProperty("任务开始后")
    private TimingInfoDetailInfoDTO afterStarting;
    @ApiModelProperty("任务结束前")
    private TimingInfoDetailInfoDTO beforeTheEnd;

}
