package com.coolcollege.intelligent.model.menu.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * describe:菜单权限
 *
 * @author zhouyiping
 * @date 2020/09/23
 */
@Data
public class MenuAuthAddRequest {

    @NotBlank(message = "名称不能为空")

    private String name;
    @JsonProperty("parent_id")

    @NotNull(message = "父Id不能为空")
    private Long parentId;

    @NotBlank(message = "类型")
    private String type;

    private String env;

    
}
