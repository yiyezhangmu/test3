package com.coolcollege.intelligent.model.system.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/10/13
 */
@Data
public class SysRoleModifyAuthRequest {

    @JsonProperty("role_id")
    @NotNull(message = "职位ID")
    private Long roleId;


    /**
     * 可视化范围
     */
    @JsonProperty("role_auth")
    @NotBlank(message = "可视化范围")
    private String roleAuth;

    /**
     * 配置的菜单权限
     */
    List<Long> menus;

    /**
     * 移动端权限
     */
    @JsonProperty("app_menu_list")
    private List<Long> appMenuList;

}
