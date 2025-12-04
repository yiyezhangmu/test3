package com.coolcollege.intelligent.model.metatable.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/4/2 15:10
 * @Version 1.0
 */
@Data
public class TbMetaQuickColumnRequest {

    @ApiModelProperty("快速检查项id")
    private Long id;


    @ApiModelProperty("归档状态 0未归档  1归档")
    private Integer status;
}
