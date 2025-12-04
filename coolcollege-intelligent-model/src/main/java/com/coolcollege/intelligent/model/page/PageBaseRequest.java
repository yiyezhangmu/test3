package com.coolcollege.intelligent.model.page;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangnan
 * @date 2022-03-08 9:47
 */
@Data
public class PageBaseRequest {

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("页大小")
    private Integer pageSize;
}
