package com.coolcollege.intelligent.model.inspection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author byd
 * @date 2025-09-29 15:25
 */
@Data
public class AiInspectionStatisticsDTO  {

    @ApiModelProperty("场景id列表")
    private List<Long> sceneIdList;

    @ApiModelProperty("门店id列表")
    private List<String> storeIdList;

    @ApiModelProperty("区域id列表")
    private List<String> regionIdList;

    @ApiModelProperty(value = "开始时间", required = true)
    private Long beginTime;

    @ApiModelProperty(value = "结束时间",required = true)
    private Long endTime;

    @ApiModelProperty("统计类型 failNum: 不合格次数  failRate:不合格率")
    private String reportType;

}
