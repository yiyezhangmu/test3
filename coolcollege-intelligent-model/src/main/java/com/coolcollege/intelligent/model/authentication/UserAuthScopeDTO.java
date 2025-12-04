package com.coolcollege.intelligent.model.authentication;

import lombok.Data;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: UserAuthScopeDTO
 * @Description:用户权限范围
 * @date 2022-11-10 15:38
 */
@Data
public class UserAuthScopeDTO {

    /**
     * 是否是管理员
     */
    private Boolean isAdmin;

    /**
     * 管辖范围内的用户
     */
    private List<String> userIds;

    /**
     * 管辖范围内的门店
     */
    private List<String> storeIds;

    public UserAuthScopeDTO(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public UserAuthScopeDTO(boolean isAdmin, List<String> userIds, List<String> storeIds) {
        this.isAdmin = isAdmin;
        this.userIds = userIds;
        this.storeIds = storeIds;
    }
}
