package com.coolcollege.intelligent.model.bosspackage.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/24 14:11
 */
@Data
public class BusinessModuleDetailVO {
    @ApiModelProperty("业务模块id")
    private Long moduleId;

    @ApiModelProperty("业务模块名称")
    private String moduleName;

    @ApiModelProperty("pc端菜单")
    private List<Long> menus;

    @ApiModelProperty("移动端菜单")
    private List<Long> appMenuList;

}
