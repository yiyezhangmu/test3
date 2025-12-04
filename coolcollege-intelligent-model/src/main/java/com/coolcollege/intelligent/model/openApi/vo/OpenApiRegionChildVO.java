package com.coolcollege.intelligent.model.openApi.vo;

import lombok.Data;

/**
 * @ClassName RegionNode
 * @Description 用一句话描述什么
 */
@Data
public class OpenApiRegionChildVO {
    /**
     * 自增ID
     */
    private Long id;

    /**
     * 名称
     */
    private String name;
    /**
     * 父ID
     */
    private String parentId;
    /**
     * root path store
     */
    private String regionType;
    /**
     * 路径
     */
    private String regionPath;

    private String storeId;
}
