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
public class AchievementReportQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Integer pageNumber = 1;
    /**
     *
     */
    private Integer pageSize = 10;

    @ApiModelProperty("大类")
    private String mainClass;


    @ApiModelProperty("品类")
    private String category;

    @ApiModelProperty("中类")
    private String middleClass;

    @ApiModelProperty("型号")
    private String type;

    @ApiModelProperty("开始时间")
    private Long beginTime;

    @ApiModelProperty("结束时间")
    private Long endTime;

    @ApiModelProperty("区域id ，默认为 1")
    private Long regionId;

    @ApiModelProperty("门店id")
    private String storeId;
}
