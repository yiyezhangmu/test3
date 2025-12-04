package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.Data;

/**
 * @Author: hu hu
 * @Date: 2025/1/8 14:20
 * @Description:
 */
@Data
public class RoleDetailVO {

    private Long roleId;

    public RoleDetailVO(Long roleId) {
        this.roleId = roleId;
    }
}
