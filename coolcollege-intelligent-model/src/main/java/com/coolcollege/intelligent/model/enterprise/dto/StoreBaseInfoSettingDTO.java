package com.coolcollege.intelligent.model.enterprise.dto;

import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.platform.EnterpriseStoreRequiredDO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
@Data
@Accessors(chain = true)
public class StoreBaseInfoSettingDTO {

    /**
     * 门店必填字段列表
     */
    @Valid
    @NotNull(message = "门店信息不能为空")
    @JsonProperty("field_list")
    private List<EnterpriseStoreRequiredDO> fieldList;

    /**
     * 门店基础设置
     */
    @JsonProperty("store_setting")
    private EnterpriseStoreSettingDO storeSetting;

    /**
     * 门店信息完整度字段
     */
    private List<FieldDTO> perfectionFieldList;



}
