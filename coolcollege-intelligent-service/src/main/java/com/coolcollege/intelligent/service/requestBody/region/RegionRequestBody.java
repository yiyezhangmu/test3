package com.coolcollege.intelligent.service.requestBody.region;

import lombok.Data;

/**
 * @ClassName RegionRequestBody
 * @Description 用一句话描述什么
 */
@Data
public class RegionRequestBody {
    /**
     * 区域ID
     */
    private String region_id;
    /**
     * 名称
     */
    private String name;
    /**
     * 父ID
     */
    private String parent_id;

    /**
     * 外部组织
     */
    private Boolean isExternalNode;
}
