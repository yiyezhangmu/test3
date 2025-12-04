package com.coolcollege.intelligent.model.enterprise.request;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @author byd
 */
@Data
@Accessors(chain = true)
public class EnterpriseStoreSettingRequest {

    /**
     * 门店证照距离到期日期时间
     */
    @NotNull
    private Integer storeLicenseEffectiveTime;

    /**
     * 人员证照距离到期日期时间
     */
    @NotNull
    private Integer userLicenseEffectiveTime;

}
