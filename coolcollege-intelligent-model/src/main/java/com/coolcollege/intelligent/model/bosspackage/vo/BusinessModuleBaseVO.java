package com.coolcollege.intelligent.model.bosspackage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/23 10:18
 */
@ApiModel(value = "业务模块信息")
@Data
public class BusinessModuleBaseVO {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("模块名称")
    private String moduleName;

}
