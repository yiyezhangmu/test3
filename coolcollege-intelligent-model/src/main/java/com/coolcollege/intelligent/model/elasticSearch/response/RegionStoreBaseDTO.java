package com.coolcollege.intelligent.model.elasticSearch.response;

import com.coolcollege.intelligent.model.elasticSearch.annotation.GroupKey;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: RegionStoreBaseDTO
 * @Description:
 * @date 2021-10-25 20:35
 */
@Data
public class RegionStoreBaseDTO {

    /**
     * 区域id
     */
    @GroupKey
    private String regionId;
    /**
     * 门店id
     */
    private String storeId;

}
