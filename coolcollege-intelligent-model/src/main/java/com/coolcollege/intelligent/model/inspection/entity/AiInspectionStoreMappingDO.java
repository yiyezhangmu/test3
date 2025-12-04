package com.coolcollege.intelligent.model.inspection.entity;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI巡检门店映射表
 * @author   zhangchenbiao
 * @date   2025-09-25 05:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiInspectionStoreMappingDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("a巡检策略id")
    private Long inspectionId;

    @ApiModelProperty("映射主键（区域或者是门店）")
    private String mappingId;

    @ApiModelProperty("映射类型 region (区域) store(门店) group(门店分组) groupRegion (分组区域)")
    private String type;
}