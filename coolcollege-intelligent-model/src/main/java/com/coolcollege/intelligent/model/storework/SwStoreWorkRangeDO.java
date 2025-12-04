package com.coolcollege.intelligent.model.storework;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wxp
 * @date   2022-09-08 02:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwStoreWorkRangeDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("店务id")
    private Long storeWorkId;

    @ApiModelProperty("映射主键（区域或者是门店）")
    private String mappingId;

    @ApiModelProperty("映射类型 region (区域) store(门店)")
    private String type;

    @ApiModelProperty("删除标识")
    private Boolean deleted;
}