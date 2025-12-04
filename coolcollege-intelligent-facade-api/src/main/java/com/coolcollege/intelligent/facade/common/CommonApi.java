package com.coolcollege.intelligent.facade.common;

import com.coolcollege.intelligent.facade.dto.ResultDTO;
import com.coolcollege.intelligent.facade.dto.organization.OrganizationUserDTO;
import com.coolcollege.intelligent.facade.dto.role.RoleDTO;
import com.coolcollege.intelligent.facade.dto.user.UserAuthScopeDTO;
import com.taobao.api.ApiException;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: CommonFacade
 * @Description:
 * @date 2022-11-07 14:14
 */
public interface CommonApi {

    /**
     * 获取角色信息
     * @param enterpriseId
     * @param roleIds
     * @return
     */
    ResultDTO<List<RoleDTO>> getRoleByRoleIds(String enterpriseId, List<Long> roleIds) throws ApiException;

    /**
     * 获取权限范围
     * @param enterpriseId
     * @param userId
     * @return
     */
    ResultDTO<UserAuthScopeDTO> getUserAuthStoreIdsAndUserIds(String enterpriseId, String userId) throws ApiException;

    /**
     * 获取门店下的人（组织架构）
     * @param enterpriseId
     * @param storeId
     * @return
     */
    ResultDTO<OrganizationUserDTO>  getOrganizationUserIds(String enterpriseId, String storeId) throws ApiException;



    ResultDTO<List<String>>  getUserNumOfRoot(String enterpriseId)throws ApiException;

}
