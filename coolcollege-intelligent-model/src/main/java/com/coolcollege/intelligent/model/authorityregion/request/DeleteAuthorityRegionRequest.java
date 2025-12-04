package com.coolcollege.intelligent.model.authorityregion.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: huhu
 * @Date: 2024/11/25 16:40
 * @Description:
 */
@ApiModel("删除授权区域")
@Data
public class DeleteAuthorityRegionRequest {

    @NotNull(message = "id不能为空")
    @ApiModelProperty("授权区域id")
    private Long authorityRegionId;

}
