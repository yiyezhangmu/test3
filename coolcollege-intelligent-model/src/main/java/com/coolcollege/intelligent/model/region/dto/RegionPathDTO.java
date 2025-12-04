package com.coolcollege.intelligent.model.region.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 邵凌志
 * @date 2020/12/22 13:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionPathDTO {

    private String regionId;

    private String parentId;

    private String regionPath;

    private Integer storeNum;

    private String regionName;

    private String regionType;

    private String storeId;

    /**
     * 根据配置的门店统计范围统计门店数量（默认统计范围是全部门店状态）
     */
    private Integer storeStatNum;
}
