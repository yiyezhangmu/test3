package com.coolcollege.intelligent.model.system.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/12/09
 */
@Data
public class RoleDeleteRequest {
    @JsonProperty("role_id_list")
    @NotNull(message = "角色Id")
    private List<Long> roleIdList;
}
