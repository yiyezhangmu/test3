package com.coolcollege.intelligent.model.newbelle.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 公共基础类_商品信息
 */
@Data
public class BaseGoodsDetailRequest {
    @ApiModelProperty("商品编码(14位流水号)")
    private List<String> product_no;
}
