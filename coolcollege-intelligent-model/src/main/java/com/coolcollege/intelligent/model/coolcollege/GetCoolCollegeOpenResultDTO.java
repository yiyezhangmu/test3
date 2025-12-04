package com.coolcollege.intelligent.model.coolcollege;

import lombok.Data;

/**
 * @author xuanfeng
 * @since 2022/4/22
 */
@Data
public class GetCoolCollegeOpenResultDTO {

    private String appType;

    private String corpId;

    private String storeEnterpriseId;

    private Long regionId;

    public GetCoolCollegeOpenResultDTO(String appType, String corpId, String storeEnterpriseId,Long regionId) {
        this.appType = appType;
        this.corpId = corpId;
        this.storeEnterpriseId = storeEnterpriseId;
        this.regionId = regionId;
    }
}
