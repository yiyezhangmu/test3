package com.coolcollege.intelligent.model.license;

import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @FileName: LicenseNoticeDTO
 * @Description:
 * @date 2022-11-08 15:01
 */
@Data
public class LicenseNoticeDTO {

    @ApiModelProperty("企业id")
    private String enterpriseId;

    @ApiModelProperty("门店id")
    private List<String> storeIds;

    @ApiModelProperty("用户id")
    private List<String> userIds;

    @ApiModelProperty("通知对象")
    private List<GeneralDTO> noticeTarget;

    @ApiModelProperty("预警id")
    private Long noticeSettingId;

    @ApiModelProperty("门店证照类型")
    private Map<Long, String> storeLicenseTypeMap;

    @ApiModelProperty("用户证照类型")
    private Map<Long, String> userLicenseTypeMap;

    public LicenseNoticeDTO(String enterpriseId, List<String> storeIds, List<String> userIds, List<GeneralDTO> noticeTarget, Long noticeSettingId, Map<Long, String> storeLicenseTypeMap, Map<Long, String> userLicenseTypeMap) {
        this.enterpriseId = enterpriseId;
        this.storeIds = storeIds;
        this.userIds = userIds;
        this.noticeTarget = noticeTarget;
        this.noticeSettingId = noticeSettingId;
        this.storeLicenseTypeMap = storeLicenseTypeMap;
        this.userLicenseTypeMap = userLicenseTypeMap;
    }
}
