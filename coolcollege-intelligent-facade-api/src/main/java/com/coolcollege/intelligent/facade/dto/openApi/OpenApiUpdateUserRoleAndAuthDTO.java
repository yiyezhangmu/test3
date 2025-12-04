package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.List;

/**
 * @Author wxp
 * @Date 2022/11/01 17:04
 * @Version 1.0
 * @description  非门店通企业更新用户区域权限和角色
 */
@Data
public class OpenApiUpdateUserRoleAndAuthDTO {

    /**
     * 更新的用户信息
     */
    private List<UpdateUserRoleAndAuth> updateUserList;


    @Data
    public static class UpdateUserRoleAndAuth{

        /**
         * 用户id
         */
        public String userId;

        /**
         * 用户区域权限列表
         */
        private List<String> regionIdList;
        /**
         * 用户角色列表
         */
        public List<String> roleIdList;

    }

}


