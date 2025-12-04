package com.coolcollege.intelligent.facade.dto.store;

import lombok.Data;

import java.util.List;

/**
 * 门店信息
 * @author zhangnan
 * @date 2021-11-22 10:55
 */
@Data
public class StoreFacadeDTO {

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 区域路径
     */
    private String regionPath;

    /**
     * 用户数量
     */
    private Integer userCount;

    /**
     * 用户id列表
     */
    private List<String> userIds;

}
