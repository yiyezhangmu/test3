package com.coolcollege.intelligent.model.enterprise.dto;

import com.coolcollege.intelligent.model.enterprise.BannerDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.platform.EnterpriseStoreRequiredDO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName EnterpriseDTO
 * @Description 用一句话描述什么
 * @author 首亮
 */
@Data
public class EnterpriseDTO {

    /**
     * 企业主键
     */
    private String id;

    /**
     * 企业名称
     */
    private String name;

    /**
     * 企业电话
     */
    private String mobile;

    /**
     * 企业logo
     */
    @JsonProperty("corp_logo_url")
    private String corpLogoUrl;

    /**
     * logo名称
     */
    @JsonProperty("logo_name")
    private String logoName;

    /**
     * banner图列表
     */
    private List<BannerDO> banner;

    /**
     * 门店必填字段列表
     */
    @JsonProperty("field_list")
    private List<EnterpriseStoreRequiredDO> fieldList;

    /**
     * 企业通用设置
     */
    private EnterpriseSettingDO setting;
}
