package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/7/25 10:38
 */
@Data
public class OpenApiStoreLicenseRequestDTO {
    private Long id;
    private String storeId;
    private String storeCode;
    private Long licenseTypeId;
    private String picture;
    private String extendFieldInfo;
    private String expiryType;
    private Long expiryBeginDate;
    private Long expiryEndDate;
}
