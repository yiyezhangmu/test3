package com.coolcollege.intelligent.model.enterprise.dto;

import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EnterpriseStoreCheckDTO {

    /**
     * 字段名
     */
    @JsonProperty("field_name_list")
    private List<String> fieldNameList;
    /**
     * 巡店设置
     */
    @JsonProperty("store_check_setting")
    private EnterpriseStoreCheckRequestDTO storeCheckSetting;
}
