package com.coolcollege.intelligent.service.requestBody.region;

import lombok.Data;

/**
 * @ClassName RegionRequestBody
 * @Description 用一句话描述什么
 */
@Data
public class RegionOrStoreRequest {
    /**
     * 区域ID
     */
    private String regionId;
    private String personalId;
    private String storeId;
    /**
     * 类型 区域 门店 人员
     */
    private String type;
    /**
     * 名称
     */
    private String name;
    /**
     * 父ID
     */
    private String parentId;
}
