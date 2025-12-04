package com.coolcollege.intelligent.model.metatable.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/4/2 11:09
 * @Version 1.0
 */
@Data
public class ColumnCategoryRequest {

    @ApiModelProperty("项分类ID")
    private Long id;

    @ApiModelProperty("项分类名称")
    private String categoryName;

    private List<Long> ids;

}
