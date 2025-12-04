package com.coolcollege.intelligent.model.unifytask.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author byd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementReportDetailQuery implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty("品类")
    private String category;

    @ApiModelProperty("中类")
    private String middleClass;

    @ApiModelProperty("开始时间")
    private Long beginTime;

    @ApiModelProperty("结束时间")
    private Long endTime;

    @ApiModelProperty("区域id ，默认为 1")
    private Long regionId;

    @ApiModelProperty("统计类型 按天:DAY 按月；MONTH")
    private String reportType;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("类型")
    private String goodType;
}
