package com.coolcollege.intelligent.model.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: PageRequest
 * @Description:
 * @date 2021-10-18 10:14
 */
@ApiModel
@Data
public class PageRequest {

    @ApiModelProperty(value = "第几页", required = true)
    private Integer pageNumber;

    @ApiModelProperty(value = "分页大小", required = true)
    private Integer pageSize;
}
