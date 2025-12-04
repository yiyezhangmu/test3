package com.coolcollege.intelligent.model.enterprise.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author wxp
 * @Date 2022/11/01 17:04
 * @Version 1.0
 * @description  主题色自定义
 */
@Data
public class EnterpriseThemeColorSettingsDTO {

    @ApiModelProperty("主题色")
    private String themeColor;

    @ApiModelProperty("移动端图标修改 0圆形图标 1矩形图标")
    private Boolean mobileIcon;

    @ApiModelProperty("管理图标")
    private String manageIcon;
    /**
     * 更新的菜单信息
     */
    private List<SysMenuExtendInfo> menuExtendInfoList;

    @Data
    public static class SysMenuExtendInfo{

        @ApiModelProperty("菜单id")
        private Long menuId;

        @ApiModelProperty("自定义的菜单名称")
        private String defineName;

        @ApiModelProperty("菜单图片")
        private String menuPic;

        @ApiModelProperty("菜单类型")
        private String platform;

    }

}


