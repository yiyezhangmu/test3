package com.coolcollege.intelligent.model.brand.request;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 品牌查询request
 * </p>
 *
 * @author wangff
 * @since 2025/3/6
 */
@Data
public class EnterpriseBrandQueryRequest extends PageBaseRequest {
    @ApiModelProperty("品牌名称")
    private String name;
}
