package com.coolcollege.intelligent.model.export.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 查询新店-分析表request
 * @author zhangnan
 * @date 2022-03-08 11:30
 */
@Data
public class NsStoreExportStatisticsRequest extends DynamicFieldsExportRequest{

    @ApiModelProperty("开始时间")
    private Long beginDate;

    @ApiModelProperty("结束时间")
    private Long endDate;

    @NotNull(message = "请选择区域")
    @ApiModelProperty("区域id")
    private Long regionId;

}
