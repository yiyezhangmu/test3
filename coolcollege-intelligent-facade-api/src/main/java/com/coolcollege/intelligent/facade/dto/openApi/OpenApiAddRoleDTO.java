package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: hu hu
 * @Date: 2025/1/8 11:17
 * @Description:
 */
@Data
public class OpenApiAddRoleDTO {

    /**
     * 第三方唯一id
     */
    private String thirdUniqueId;

    /**
     * 职位类型：store_outside-店外，store_inside-店内
     */
    private String positionType;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新人
     */
    private String updateUser;

    public boolean insertCheck() {
        return !StringUtils.isAnyBlank(positionType);
    }

    public boolean check() {
        return !StringUtils.isAnyBlank(thirdUniqueId, roleName);
    }
}
