package com.coolcollege.intelligent.model.newstore.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 查询新店-分析表request
 * @author zhangnan
 * @date 2022-03-08 11:30
 */
@Data
public class NsStoreGetStatisticsRequest {

    @NotNull(message = "请选择新店创建日期")
    @ApiModelProperty("新店创建开始日期")
    private Long beginDate;

    @NotNull(message = "请选择新店创建日期")
    @ApiModelProperty("新店创建结束日期")
    private Long endDate;

    @NotNull(message = "请选择区域")
    @ApiModelProperty("区域id")
    private Long regionId;

}
