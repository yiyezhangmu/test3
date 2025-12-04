package com.coolcollege.intelligent.model.store.dto;

import lombok.Data;

/**
 * @author 邵凌志
 * @date 2020/11/18 15:52
 */
@Data
public class SelectStoreDTO {

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店地址
     */
    private String locationAddress;

    /**
     * 阿里云corpId
     */
    private String corpId;

    private Long regionId;

    /**
     * 区域路径
     */
    private String regionPath;


    /**
     * 杰峰门店id
     */
    private String nodeId;
}
