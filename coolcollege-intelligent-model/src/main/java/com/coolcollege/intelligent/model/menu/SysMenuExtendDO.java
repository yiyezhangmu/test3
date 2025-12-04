package com.coolcollege.intelligent.model.menu;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wxp
 * @date   2023-12-27 04:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysMenuExtendDO implements Serializable {
    @ApiModelProperty("自增id")
    private Long id;

    @ApiModelProperty("菜单id")
    private Long menuId;

    @ApiModelProperty("自定义的菜单名称")
    private String defineName;

    @ApiModelProperty("菜单图片")
    private String menuPic;

    @ApiModelProperty("菜单类型")
    private String platform;
}