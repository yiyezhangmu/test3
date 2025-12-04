package com.coolcollege.intelligent.model.newstore.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangnan
 * @description: 拜访表数据项DTO
 * @date 2022/3/7 9:31 PM
 */
@Data
public class NsVisitTableDataColumnDTO {

    @ApiModelProperty("数据项id")
    private Long id;

    @ApiModelProperty("检查项id")
    private Long metaColumnId;

    @ApiModelProperty("值1")
    private String value1;

    @ApiModelProperty("值2")
    private String value2;
}
