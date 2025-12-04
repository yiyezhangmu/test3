package com.coolcollege.intelligent.model.unifytask.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author byd
 * @date 2024-03-16 13:58
 */
@Data
public class ProductInfoDTO {

    @ApiModelProperty("型号")
    private String type;

    @ApiModelProperty("品类Code")
    private String categoryCode;

    @ApiModelProperty("品类name")
    private String categoryName;

    @ApiModelProperty("中类Code")
    private String middleCategoryCode;

    @ApiModelProperty("中类Name")
    private String middleCategoryName;

    @ApiModelProperty("小类Code")
    private String smallCategoryCode;

    @ApiModelProperty("小类Name")
    private String smallCategoryName;

    @ApiModelProperty("型号码")
    private String modelNumber;

}
