package com.coolcollege.intelligent.model.region.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 邵凌志
 * @date 2020/12/22 13:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionSyncDTO {

    /**
     * 区域id
     */
    private Long id;

    /**
     * 区域钉钉id
     */
    private String synDingDeptId;
}
