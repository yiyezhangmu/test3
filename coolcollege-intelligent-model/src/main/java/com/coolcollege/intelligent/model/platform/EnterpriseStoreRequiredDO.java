package com.coolcollege.intelligent.model.platform;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotNull;

/**
 * @author 邵凌志
 * @date 2020/7/7 16:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseStoreRequiredDO {

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 字段名称
     */
    @NotNull(message = "field不能为空")
    private String field;

    /**
     * 字段描述
     */
    @NotNull(message = "field_name不能为空")
    @JsonProperty("field_name")
    private String fieldName;
}
