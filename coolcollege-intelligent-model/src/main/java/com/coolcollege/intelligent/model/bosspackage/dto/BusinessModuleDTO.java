package com.coolcollege.intelligent.model.bosspackage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/23 10:18
 */
@ApiModel(value = "业务模块入参实体")
@Data
public class BusinessModuleDTO {

    @ApiModelProperty("主键-更新操作的时候需要传入")
    private Long moduleId;

    @ApiModelProperty("模块名称")
    private String moduleName;

    @ApiModelProperty("pc端菜单")
    private List<Long> menus;

    @ApiModelProperty("移动端菜单")
    private List<Long> appMenuList;

}
