package com.coolcollege.intelligent.model.license;

import com.coolcollege.intelligent.model.export.request.DynamicFieldsExportRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class StoreLicenseExportRequest extends DynamicFieldsExportRequest {
    @ApiModelProperty(value = "regionId")
    private String regionId;

    @ApiModelProperty(value = "regionIds")
    private List<String> regionIds;

    @ApiModelProperty(value = "门店名")
    private String storeName;

    @ApiModelProperty("证照类型id")
    private String licenseTypeId;

    @ApiModelProperty("门店状态")
    private List<String> storeStatusList;

    @ApiModelProperty("证照状态")
    private List<String> licenseStatusList;

    private String enterpriseId;

}
