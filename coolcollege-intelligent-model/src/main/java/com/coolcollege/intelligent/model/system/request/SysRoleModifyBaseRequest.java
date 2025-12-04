package com.coolcollege.intelligent.model.system.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/12/21
 */
@Data
public class SysRoleModifyBaseRequest {
    @JsonProperty("role_id")
    @NotNull(message = "职位ID")
    private Long roleId;


    @JsonProperty("role_name")
    @NotBlank(message = "职位名称")
    private String roleName;

    /**
     * 角色类型
     */
    @JsonProperty("position_type")
    @NotBlank(message = "职位类型")
    private String positionType;

    /**
     * 职位优先级
     */
    @JsonProperty("priority")
//    @NotBlank(message = "职位优先级")
    private Integer priority;
}
