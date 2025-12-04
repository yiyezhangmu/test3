package com.coolcollege.intelligent.model.inspection;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * AI巡检抓拍时间段信息表
 * @author   zhangchenbiao
 * @date   2025-09-25 04:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiInspectionTimePeriodDTO implements Serializable {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("开始时间 如 09:00")
    private String beginTime;

    @ApiModelProperty("结束时间 如 21:00")
    private String endTime;

    @ApiModelProperty("间隔时间,分钟")
    private Integer period;
}