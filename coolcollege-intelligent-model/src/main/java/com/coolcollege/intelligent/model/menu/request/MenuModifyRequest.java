package com.coolcollege.intelligent.model.menu.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/09/23
 */
@Data
public class MenuModifyRequest  {

    @NotNull(message = "id不能为空")
    private Long id;

    @NotBlank(message = "地址不能为空")
    private String path;

    @NotBlank(message = "名称不能为空")
    private String name;

    private String component;
    private String target;
    private String icon;
    private String env;

    /**
     * 常用功能图标
     */
    private String commonFunctionsIcon;

    private String label;
}
