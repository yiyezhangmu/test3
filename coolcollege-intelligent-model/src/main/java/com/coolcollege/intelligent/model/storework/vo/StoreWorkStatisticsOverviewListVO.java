package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * @author wxp
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkStatisticsOverviewListVO {

    @ApiModelProperty(value = "区域ID")
    private Long regionId;

    @ApiModelProperty(value = "区域名称")
    private String regionName;

    @ApiModelProperty(value = "所属区域名称")
    private String fullRegionName;

    @ApiModelProperty(value = "区域下的统计列表")
    private List<StoreWorkStatisticsOverviewVO> statisticsOverviewVOList;

}
