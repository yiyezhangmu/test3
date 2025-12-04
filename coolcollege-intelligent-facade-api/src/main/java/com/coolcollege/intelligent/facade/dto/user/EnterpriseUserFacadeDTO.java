package com.coolcollege.intelligent.facade.dto.user;

import lombok.Data;

/**
 * 用户信息
 * @author zhangnan
 * @date 2021-11-23 17:45
 */
@Data
public class EnterpriseUserFacadeDTO {

    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户姓名
     */
    private String name;
}
