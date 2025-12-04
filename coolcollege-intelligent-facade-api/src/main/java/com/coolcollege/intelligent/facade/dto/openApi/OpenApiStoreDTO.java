package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/7/18 17:04
 * @Version 1.0
 */
@Data
public class OpenApiStoreDTO {

    private Long regionId;

    private String storeName;

    private Boolean currentRegionData;

    private Integer pageSize;

    private Integer pageNum;

    private String storeId;

    /**
     * 创建时间开始
     */
    private Long beginTime;

}
