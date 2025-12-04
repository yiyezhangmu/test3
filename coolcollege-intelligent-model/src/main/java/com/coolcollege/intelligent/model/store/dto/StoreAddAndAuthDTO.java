package com.coolcollege.intelligent.model.store.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 门店新增及授权DTO
 * </p>
 *
 * @author wangff
 * @since 2025/7/22
 */
@Data
public class StoreAddAndAuthDTO {
    @ApiModelProperty("门店名称")
    private String storeName;
}
