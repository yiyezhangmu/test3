package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: UserAccessTokenVO
 * @Description:
 * @date 2024-09-20 10:35
 */
@Data
public class UserAccessTokenVO {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 过期时间 单位秒
     */
    private Long expiresTime;


    public UserAccessTokenVO(String accessToken, Long expiresTime) {
        this.accessToken = accessToken;
        this.expiresTime = expiresTime;
    }

    public UserAccessTokenVO() {
    }
}
