package com.coolcollege.intelligent.model.region.dto;

import lombok.Data;

/**
 * @ClassName RegionDTO
 * @Description 区域
 */
@Data
public class RegionDTO {
    /**
     * 自增ID
     */
    private Long id;

    /**
     * 区域ID
     */
    private String regionId;

    /**
     * 名称
     */
    private String name;
    /**
     * 父ID
     */
    private String parentId;

    /**
     * root 跟节点  path 区域 store 门店
     */
    private String type;

    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 创建人
     */
    private String createName;
    /**
     * 更新时间
     */
    private Long updateTime;
    /**
     * 更新人
     */
    private String updateName;
}
