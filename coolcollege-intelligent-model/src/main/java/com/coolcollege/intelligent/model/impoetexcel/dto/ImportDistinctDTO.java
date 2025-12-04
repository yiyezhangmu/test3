package com.coolcollege.intelligent.model.impoetexcel.dto;

import lombok.Data;

/**
 * @author 邵凌志
 * @date 2020/12/15 14:27
 */
@Data
public class ImportDistinctDTO {

    /**
     * 去重字段
     */
    private String uniqueField;

    /**
     * 去重名称
     */
    private String uniqueName;
}
