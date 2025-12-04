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
public class MenuAuthModifyRequest  {
    @NotNull(message = "id不能为空")
    private Long id;

    @NotBlank(message = "权限类型不能为空")
    private String type;

    @NotBlank(message = "名称不能为空")
    private String name;

    private String env;

}
