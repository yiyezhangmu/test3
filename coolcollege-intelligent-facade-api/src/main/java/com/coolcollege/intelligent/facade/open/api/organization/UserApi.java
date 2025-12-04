package com.coolcollege.intelligent.facade.open.api.organization;

import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.*;
import com.coolcollege.intelligent.facade.dto.openApi.vo.UserAccessTokenVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.UserInfoVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.UserListVO;
import com.coolstore.base.response.rpc.OpenApiResponseVO;

/**
 * @Author byd
 * @Date 2022/11/01 17:04
 * @Version 1.0
 */
public interface UserApi {
    /**
     * 获取用户信息
     * @param openApiUserDTO
     * @return
     */
    OpenApiResponseVO getUserInfo(OpenApiUserDTO openApiUserDTO);


    /**
     * 修改用户角色
     * @param openApiUserDTO
     * @return
     */
    OpenApiResponseVO updateUserRole(OpenApiUserDTO openApiUserDTO);

    /**
     * 更新用户角色和权限
     * @param openApiUserDTO
     * @return
     */
    OpenApiResponseVO updateUseRoleAndAuth(OpenApiUpdateUserAuthDTO openApiUserDTO);

    /**
     * 非门店通企业更新用户角色和权限
     * @param openApiUpdateUserRoleAndAuthDTO
     * @return
     */
    OpenApiResponseVO updateUseRoleAndRegionAuth(OpenApiUpdateUserRoleAndAuthDTO openApiUpdateUserRoleAndAuthDTO);

    /**
     * 新增用户
     * @param param
     * @return
     */
    OpenApiResponseVO addUser(OpenApiAddUserDTO param);

    /**
     * 删除用户
     * @param param
     * @return
     */
    OpenApiResponseVO deleteUser(OpenApiDeleteUserDTO param);

    /**
     * 获取用户token
     * @param param
     * @return
     */
    OpenApiResponseVO<UserAccessTokenVO> getUserAccessToken(OpenApiGetUserAccessTokenDTO param);

    /**
     * 慧云班-人员分页查询
     */
    OpenApiResponseVO<PageDTO<UserInfoVO>> getUserPage(OpenApiUserQueryDTO param);

    /**
     * 慧云班-根据用户id查询
     * @param param 用户查询DTO
     * @return 用户信息VO
     */
    OpenApiResponseVO<UserListVO> getUserByIds(OpenApiUserQueryDTO param);

    /**
     * 慧云班-获取管理员用户id列表
     * @return 用户id列表VO
     */
    OpenApiResponseVO<PageDTO<String>> getAdminUserIds(PageQueryDTO param);
}
