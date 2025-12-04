package com.coolcollege.intelligent.model.region.dto;

import lombok.Data;

/**
 * @author 邵凌志
 * @date 2020/12/10 16:03
 */
@Data
public class RegionSelectDTO {

    private String regionId;

    private String parentId;

    private String name;

    private String path;

    private String parentName;

    private String fullRegionPath;
}
