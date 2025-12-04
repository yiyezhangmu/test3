package com.coolcollege.intelligent.model.store.dto;

import lombok.Data;

/**
 * @Description: 动态扩展字段信息
 * @Author chenyupeng
 * @Date 2021/6/25
 * @Version 1.0
 */
@Data
public class ExtendFieldDataDTO {
    /**
     * 扩展字段名称
     */
    private String extendFieldValue;

    /**
     * 扩展字段Key
     */
    private String extendFieldKey;

}
