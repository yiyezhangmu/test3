package com.coolcollege.intelligent.service.user;

/**
 * @author zhangchenbiao
 * @FileName: ExternalUserService
 * @Description: 外部用户service
 * @date 2023-10-18 15:17
 */
public interface ExternalUserService {

    /**
     * 开启或关闭外部用户
     * @param enterpriseId
     * @param enableExternalUser
     */
    void openOrCloseExternalUser(String enterpriseId, Boolean enableExternalUser);
}
