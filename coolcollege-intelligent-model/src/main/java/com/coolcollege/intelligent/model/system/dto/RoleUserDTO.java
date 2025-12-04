package com.coolcollege.intelligent.model.system.dto;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/12/08
 */
@Data
public class RoleUserDTO {
    private Long roleId;
    private String roleName;
    private Integer userCount;
    private String source;
}
