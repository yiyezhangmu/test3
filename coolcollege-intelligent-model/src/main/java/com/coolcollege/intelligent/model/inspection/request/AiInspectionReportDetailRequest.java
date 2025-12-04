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
public class AiInspectionReportDetailRequest {

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("门店id列表")
    private String storeId;

    @ApiModelProperty("日期 日报传日期，周报传周一的日期")
    private Long reportDate;

    @ApiModelProperty("报表类型 日报:DAY 周报:WEEK")
    private String reportType;
}
