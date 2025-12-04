package com.coolcollege.intelligent.service.storework;

import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;

import java.util.List;
import java.util.Map;

/**
 * @Author wxp
 * @Date 2022/9/14 9:39
 * @Version 1.0
 */
public interface StoreWorkRangeService {

    /**
     * 获取门店范围
     * @param enterpriseId
     * @param storeWorkIdList
     * @return
     */
    Map<Long, List<StoreWorkCommonDTO>> listStoreRange(String enterpriseId, List<Long> storeWorkIdList);
}
