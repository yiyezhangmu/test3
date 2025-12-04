package com.coolcollege.intelligent.model.store.queryDto;

import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/3/2 10:59
 * @Version 1.0
 */
@Data
public class StoreInRegionRequest {
    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店编号
     */
    private String storeNum;

    private String regionId;

    private List<String> regionIdList;

    private Integer  pageSize =20 ;

    private Integer  pageNum = 1;

    /**
     * 门店状态
     */
    private String storeStatus;

    /**
     * 是否只获取当前区域门店
     */
    private Boolean currentRegionData = Boolean.FALSE;

    /**
     * 是否获取权限范围内的门店
     */
    private Boolean isGetAuthStore;

    /**
     * 品牌id
     */
    private Long brandId;
}
