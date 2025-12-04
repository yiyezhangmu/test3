package com.coolcollege.intelligent.model.authorityregion.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Author: hu hu
 * @Date: 2024/11/25 16:59
 * @Description:
 */
@Data
@ApiModel("我的授权区域")
@Builder
public class MyAuthorityRegionVO {

    @ApiModelProperty("授权区域名称集合")
    private List<String> names;
}
