package com.coolcollege.intelligent.model.store.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zhangchenbiao
 * @FileName: StoreIdDTO
 * @Description:
 * @date 2022-12-20 15:08
 */
@Data
public class StoreIdDTO {

    @NotBlank(message = "门店id不能为空")
    @ApiModelProperty("门店Id")
    private String storeId;
}
