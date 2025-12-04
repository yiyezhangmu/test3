package com.coolcollege.intelligent.model.inspection.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author byd
 * @date 2025-10-09 13:58
 */
@Data
public class AiInspectionStoreMappingRequest {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("映射主键（区域或者是门店）")
    private String mappingId;

    @ApiModelProperty("映射类型 region (区域) store(门店) group(门店分组)")
    private String type;
}
