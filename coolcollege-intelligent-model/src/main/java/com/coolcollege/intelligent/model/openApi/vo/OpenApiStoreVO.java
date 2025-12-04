package com.coolcollege.intelligent.model.openApi.vo;

import lombok.Data;

/**
 * @ClassName RegionNode
 * @Description 用一句话描述什么
 */
@Data
public class OpenApiStoreVO {
    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店编号
     */
    private String storeNum;

    /**
     * 区域id
     */
    private Long regionId;

}
