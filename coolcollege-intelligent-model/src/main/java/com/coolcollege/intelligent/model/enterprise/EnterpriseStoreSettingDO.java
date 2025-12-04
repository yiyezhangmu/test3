package com.coolcollege.intelligent.model.enterprise;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EnterpriseStoreSettingDO {
    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 门头照是否允许从相册选择
     */
    @JsonProperty("picture_set")
    private Boolean pictureSet = true;

    /**
     * 是否允许门店自定义字段编辑
     */
    @JsonProperty("field_edit")
    private Boolean fieldEdit = false;

    /**
     * 门店信息变更提醒是否推送
     */
    @JsonProperty("info_change_remind")
    private Boolean infoChangeRemind = false;

    /**
     * 动态扩展字段信息
     */
    private String extendFieldInfo;

    /**
     * 门店信息完整度判断字段
     */
    private String perfectionField;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建人
     */
    private String createUserId;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 修改人
     */
    private String updateUserId;

    /**
     * 门店证照距离到期日期时间
     */
    private Integer storeLicenseEffectiveTime;

    /**
     * 人员证照距离到期日期时间
     */
    private Integer userLicenseEffectiveTime;


    /**
     * 巡店报告通知人员
     */
    private String notificationPushUser;

    private String noNeedUploadLicenseUser;

    private String needUploadLicenseUser;

    private String noNeedUploadLicenseRegion;
}
