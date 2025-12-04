package com.coolcollege.intelligent.facade.dto.store;

import lombok.Data;

import java.util.List;

/**
 * 获取门店信息请求参数DTO
 * @author zhangnan
 * @date 2021-11-19 14:12
 */
@Data
public class GetStoreDTO{

    /**
     * 用户id
     */
    private String userId;

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 统计用户数量：true，false
     */
    private Boolean hasUserCount;

    private List<String> storeStatusList;

    private Integer pageSize;

    private Integer pageNum;
}
