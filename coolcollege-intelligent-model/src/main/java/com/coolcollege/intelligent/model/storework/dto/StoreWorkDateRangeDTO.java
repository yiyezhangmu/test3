package com.coolcollege.intelligent.model.storework.dto;

import com.coolcollege.intelligent.model.enums.StoreWorkDateRangeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 日期范围DTO
 * </p>
 *
 * @author wangff
 * @since 2025/5/22
 */
@Data
public class StoreWorkDateRangeDTO {
    /**
     * {@link StoreWorkDateRangeEnum#getType()}
     */
    @ApiModelProperty("类型, day/weekday/weekOfYear/month")
    private String type;

    @ApiModelProperty("值列表，type为day则values为日期，其他填1/2/3/..")
    private List<String> values;
}
