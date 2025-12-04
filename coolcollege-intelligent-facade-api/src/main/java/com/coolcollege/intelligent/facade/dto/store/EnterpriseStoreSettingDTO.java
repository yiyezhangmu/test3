package com.coolcollege.intelligent.facade.dto.store;

import lombok.Data;

/**
 * @author byd
 * @date 2021-11-19 17:27
 */
@Data
public class EnterpriseStoreSettingDTO {


    /**
     * 门店证照距离到期日期时间
     */
    private Integer storeLicenseEffectiveTime;

    /**
     * 人员证照距离到期日期时间
     */
    private Integer userLicenseEffectiveTime;

    private String noNeedUploadLicenseUser;

    private String needUploadLicenseUser;

    private String noNeedUploadLicenseRegion;

}
