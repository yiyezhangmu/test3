package com.coolcollege.intelligent.model.openApi.vo;

import lombok.Data;

/**
 * @author chenyupeng
 * @since 2022/4/28
 */
@Data
public class OpenApiRoleVO {
    private Long roleId;
    private String roleName;
    private String source;
    private String positionType;
}
