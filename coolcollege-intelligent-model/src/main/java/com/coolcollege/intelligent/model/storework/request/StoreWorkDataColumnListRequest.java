package com.coolcollege.intelligent.model.storework.request;

import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 店务记录门店统计查询请求参数
 * @author wxp
 * @date 2022-9-21 19:13
 */
@ApiModel
@Data
public class StoreWorkDataColumnListRequest {

    @ApiModelProperty("完成状态 0:未完成  1:已完成")
    private Integer submitStatus;

    @ApiModelProperty(value = "数据表id", required = true)
    private Long dataTableId;
    @ApiModelProperty(hidden = true)
    private ExportServiceEnum exportServiceEnum;
}
