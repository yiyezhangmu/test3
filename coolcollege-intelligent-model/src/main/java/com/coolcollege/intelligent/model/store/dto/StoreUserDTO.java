package com.coolcollege.intelligent.model.store.dto;

import lombok.Data;

/**
 * @author 邵凌志
 * @date 2020/12/8 10:52
 */
@Data
public class StoreUserDTO {

    private String storeId;

    private String userId;

    private String userName;

    private String mobile;

    private String avatar;

    private Long positionId;

    private String positionName;
}
