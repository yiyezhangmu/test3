package com.coolcollege.intelligent.model.region.dto;

import lombok.Data;

/**
 * @ClassName RegionQueryDTO
 * @Description 区域查询条件
 */
@Data
public class RegionQueryDTO {

    private String keyword;
    /**
     * 是否查询门店数量
     * true/false
     */
    private String queryStoreCount;

    /**
     * 区域ID
     */
    private String regionId;
    /**
     * 用户id
     */
    private String userId;

    /**
     * 是否是外部组织
     */
    private Boolean isExternalNode;
}
