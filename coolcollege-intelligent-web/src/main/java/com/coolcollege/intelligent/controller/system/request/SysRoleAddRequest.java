package com.coolcollege.intelligent.controller.system.request;

import com.coolcollege.intelligent.model.menu.vo.RoleMenuAuthVO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/10/13
 */
@Data
public class SysRoleAddRequest {

    @JsonProperty("role_name")
    @NotBlank(message = "职位名称")
    private String roleName;

    @JsonProperty("priority")
    @NotBlank(message = "职位优先级")
    private Integer priority;

    @JsonProperty("position_type")
    @NotBlank(message = "职位类型")
    private String positionType;


    @JsonProperty("app_menu")
    private String appMenu;

    /**
     * app菜单权限id
     */
    @JsonProperty("app_menu_id_list")
    private List<Long> appMenuIdList;



}
