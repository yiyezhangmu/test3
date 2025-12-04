package com.coolcollege.intelligent.facade.open.api.organization;

import com.coolcollege.intelligent.facade.dto.openApi.OpenApiAddRoleDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiDeleteRolesDTO;
import com.coolstore.base.response.rpc.OpenApiResponseVO;

/**
 * @Author: hu hu
 * @Date: 2025/1/8 11:14
 * @Description:
 */
public interface RoleApi {

    /**
     * 新增或更新职位
     * @param param 参数信息
     * @return 结果
     */
    OpenApiResponseVO insertOrUpdateRole(OpenApiAddRoleDTO param);


    /**
     * 删除职位
     * @param param 参数信息
     * @return 结果
     */
    OpenApiResponseVO deleteRoles(OpenApiDeleteRolesDTO param);

}
