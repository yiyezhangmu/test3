package com.coolcollege.intelligent.model.impoetexcel;

import lombok.Data;

/**
 * @author 邵凌志
 * @date 2020/12/15 14:27
 */
@Data
public class ImportDistinctDO {

    private Long id;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 去重字段
     */
    private String uniqueField;

    /**
     * 去重名称
     */
    private String uniqueName;
}
