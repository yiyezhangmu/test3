package com.coolcollege.intelligent.model.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/20 17:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasicsStoreDTO {

    private String storeId;
    private String storeName;
    private Boolean valid;
    private String type;
    private String regionName;
    private String filterRegionId;

    public BasicsStoreDTO(String storeId, String storeName) {
        this.storeId = storeId;
        this.storeName = storeName;
    }

    public BasicsStoreDTO(String storeId, String storeName, String regionName) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.regionName = regionName;
    }
}
