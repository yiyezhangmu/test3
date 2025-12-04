package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/7/18 10:39
 * @Version 1.0
 */
@Data
public class RegionDetailVO {

    private Long id;

    private String name;

    private String parentId;

    private String synDingDeptId;

    private String regionType;

    private String regionPath;

    private String storeId;

    private Long regionId;
}
