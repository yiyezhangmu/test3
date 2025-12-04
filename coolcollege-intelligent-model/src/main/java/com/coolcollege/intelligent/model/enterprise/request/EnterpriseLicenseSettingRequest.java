package com.coolcollege.intelligent.model.enterprise.request;

import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author cfj
 */
@Data
public class EnterpriseLicenseSettingRequest {


    @ApiModelProperty("需要上传证照的用户")
    private List<StoreWorkCommonDTO> needUploadLicenseUser;

    @ApiModelProperty("不需要上传证照的用户")
    private List<StoreWorkCommonDTO> noNeedUploadLicenseUser;

    @ApiModelProperty("不需要上传证照的门店")
    private List<StoreWorkCommonDTO> noNeedUploadLicenseRegion;
}
