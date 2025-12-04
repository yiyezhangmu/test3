package com.coolcollege.intelligent.model.storework.request;

import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * @author byd
 */
@ApiModel
@Data
public class RegionSummaryDataStatisticRequest {

    @ApiModelProperty(value = "执行类型 MONTH:月 WEEK:周 DAY:天", required = true)
    private String workCycle;


    @ApiModelProperty("区域id列表")
    private List<String> regionIdList;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty(value = "开始时间(时间戳)", required = true)
    private Long beginTime;

    @ApiModelProperty(value = "结束时间(时间戳)", required = true)
    private Long endTime;

    @ApiModelProperty(value = "店务id")
    private Long storeWorkId;

    @ApiModelProperty(value = "数据表id")
    private Long tableMappingId;

    @ApiModelProperty(value = "完成状态 0:未完成 1:已完成")
    private Long completeStatus;

    @ApiModelProperty(value = "是否是查询当前区域的子区域数据")
    private Boolean childRegion = false;

    @ApiModelProperty(value = "开始时间(日期)", required = true)
    private String beginStoreWorkDate;

    @ApiModelProperty(value = "结束时间(日期)", required = true)
    private String endStoreWorkDate;

    private String regionPath;
    @ApiModelProperty(hidden = true)
    private ExportServiceEnum exportServiceEnum;
}
