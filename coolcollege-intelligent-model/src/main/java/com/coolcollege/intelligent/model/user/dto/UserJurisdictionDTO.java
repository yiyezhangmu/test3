package com.coolcollege.intelligent.model.user.dto;

import lombok.Data;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/7/28 15:57
 */
@Data
public class UserJurisdictionDTO {
    private String enterpriseId;
    private String enterpriseName;
    private String userId;
    private String userName;
    private Integer adminStore;
    private Integer adminUser;
}
