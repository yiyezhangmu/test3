package com.coolcollege.intelligent.model.store.vo;

import lombok.Data;

/**
 * @Description: 动态扩展字段
 * @Author chenyupeng
 * @Date 2021/6/28
 * @Version 1.0
 */
@Data
public class ExtendFieldInfoVO {
    /**
     * 扩展字段名称
     */
    private String extendFieldName;

    /**
     * 扩展字段类型（1：单行；2：多行）
     */
    private String extendFieldType;

    /**
     * 扩展字段Key
     */
    private String extendFieldKey;

    /**
     * 扩展字段value
     */
    private String extendFieldValue;
}
