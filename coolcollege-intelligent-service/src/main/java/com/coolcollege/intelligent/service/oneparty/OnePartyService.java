package com.coolcollege.intelligent.service.oneparty;

import com.coolcollege.intelligent.model.oneparty.dto.OnePartyBusinessRestrictionsDTO;

/**
 * @author zhangnan
 * @date 2022-06-20 10:55
 */
public interface OnePartyService {
    /**
     * 获取业务限制
     * @param eid
     * @param businessCode
     * @return
     */
    OnePartyBusinessRestrictionsDTO getBusinessRestrictions(String eid, String businessCode);

    /**
     * 检查套餐门店数量
     * 判断套餐：免费版/收费版
     * 判断巡店数量
     * @return
     */
    void checkStoreQuantity(String enterpriseId, String corpId, String storeId);
}
