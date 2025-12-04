package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 门店信息完整度字段
 * @author ：xugangkun
 * @date ：2021/11/3 14:18
 */
@Data
public class FieldDTO {
    /**
     * 字段名称
     */
    @NotNull(message = "field不能为空")
    private String field;

    /**
     * 字段描述
     */
    @NotNull(message = "field_name不能为空")
    private String fieldName;
}
