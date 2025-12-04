package com.coolcollege.intelligent.model.menu.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/26
 */
@Data
public class AppMenuAddRequest {
    @NotBlank(message = "名称不能为空")
    private String name;
    @NotNull(message = "菜单key不能为空")
    private String key;
    @NotNull(message = "父Id不能为空")
    private Long parentId;
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    @NotNull(message = "环境配置不能为空")
    private String env;

    private String path;

    private String label;

    private String icon;

}
