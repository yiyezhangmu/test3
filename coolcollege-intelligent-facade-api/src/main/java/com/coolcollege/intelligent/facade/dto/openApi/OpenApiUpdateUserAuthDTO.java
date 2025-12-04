package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.List;

/**
 * @Author byd
 * @Date 2022/11/01 17:04
 * @Version 1.0
 * 门店通企业更新用户区域权限和角色
 */
@Data
public class OpenApiUpdateUserAuthDTO {

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
         * 用户权限
         */
        public List<UserAuth> userAuthList;

        /**
         * 用户角色
         */
        public List<UserRole> roleList;

    }

    @Data
    public static class UserAuth{
        /**
         * 部门id
         */
        private String sourceDeptId;

        /**
         * 钉钉的部门id
         */
        private String dingDeptId;

    }

    @Data
    public static class UserRole{

        /**
         * 角色名称
         */
        private String roleName;

        /**
         * 角色id
         */
        private String sourceRoleId;
    }

}


