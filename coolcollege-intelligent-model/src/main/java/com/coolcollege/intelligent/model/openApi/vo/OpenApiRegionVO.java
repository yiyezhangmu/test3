package com.coolcollege.intelligent.model.openApi.vo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @ClassName RegionNode
 * @Description 用一句话描述什么
 */
@Data
public class OpenApiRegionVO {
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
