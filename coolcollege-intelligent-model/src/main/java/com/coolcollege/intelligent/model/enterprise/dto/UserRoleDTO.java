package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/11/06
 */
@Data
public class UserRoleDTO {
    private String userId;
    private String roleName;
    private String roleAuth;
    private Long roleId;
    private Integer priority;

    private String roleEnum;


}
