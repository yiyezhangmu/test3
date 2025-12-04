package com.coolcollege.intelligent.model.inspection.request;

import com.coolcollege.intelligent.model.page.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author byd
 * @date 2025-09-29 15:25
 */
@Data
public class AiInspectionReportRequest extends PageRequest {

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("门店id列表")
    private List<String> storeIdList;

    @ApiModelProperty("区域id列表")
    private List<String> regionIdList;

    @ApiModelProperty("开始时间")
    private Long beginTime;

    @ApiModelProperty("结束时间")
    private Long endTime;

    @ApiModelProperty("巡检结果 :PASS 合格,FAIL 不合格,INAPPLICABLE 不适用")
    private String inspectionResult;

    @ApiModelProperty("报表类型 日报:DAY 周报:WEEK")
    private String reportType;
}
