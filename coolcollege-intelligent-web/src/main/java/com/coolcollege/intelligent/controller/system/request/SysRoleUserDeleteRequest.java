package com.coolcollege.intelligent.controller.system.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/12/11
 */
@Data
public class SysRoleUserDeleteRequest {
    @JsonProperty("role_id")
    @NotBlank(message = "角色ID不能为空")
    private Long roleId ;

    @JsonProperty("user_id_list")
    private List<String> userIdList;


}
