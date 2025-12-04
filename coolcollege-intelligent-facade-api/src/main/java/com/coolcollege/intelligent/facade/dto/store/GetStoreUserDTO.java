package com.coolcollege.intelligent.facade.dto.store;

import lombok.Data;

/**
 * 获取门店下人员信息DTO
 * @author xuanfeng
 * @date 2021-11-19 14:12
 */
@Data
public class GetStoreUserDTO {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 用户名称
     */
    private String userName;
}
