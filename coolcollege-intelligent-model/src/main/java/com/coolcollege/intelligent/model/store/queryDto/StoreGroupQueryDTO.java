package com.coolcollege.intelligent.model.store.queryDto;

import com.coolcollege.intelligent.model.store.StoreGroupDO;
import lombok.Data;

import java.util.List;

@Data
public class StoreGroupQueryDTO {
    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店分组详情
     */
    private List<StoreGroupDO> groupDOList;

    private List<String> groupIdList;
}
