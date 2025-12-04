package com.coolcollege.intelligent.model.enterprise.vo;

import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author byd
 */
@Data
@Accessors(chain = true)
public class EnterpriseStoreSettingVO {
    /**
     * 企业id
     */
    private String enterpriseId;


    /**
     * 门店证照距离到期日期时间
     */
    private Integer storeLicenseEffectiveTime;

    /**
     * 人员证照距离到期日期时间
     */
    private Integer userLicenseEffectiveTime;

    @ApiModelProperty("需要上传证照的用户")
    private List<StoreWorkCommonDTO> needUploadLicenseUser;

    @ApiModelProperty("不需要上传证照的用户")
    private List<StoreWorkCommonDTO> noNeedUploadLicenseUser;

    @ApiModelProperty("不需要上传证照的门店")
    private List<StoreWorkCommonDTO> noNeedUploadLicenseRegion;

}
