package com.coolcollege.intelligent.model.menu.request;


import com.fasterxml.jackson.annotation.JsonProperty;
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
public class MenuAddRequest {
    @NotBlank(message = "名称不能为空")
    private String name;
    @JsonProperty("parent_id")
    @NotNull(message = "父Id不能为空")
    private Long parentId;
    @NotBlank(message = "地址不能为空")
    private String path;
    private String component;
    private String target;
    private String icon;
    private String env;

    private String label;


}
