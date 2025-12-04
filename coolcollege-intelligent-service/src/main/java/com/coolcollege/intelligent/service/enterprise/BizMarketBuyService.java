package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.model.enterprise.BizMarketBuyDO;

/**
 * Created by Administrator on 2020/1/16.
 */
public interface BizMarketBuyService {

    BizMarketBuyDO getByCorpId(String corpId);

    void updateBizMarketBuy(BizMarketBuyDO bizMarketBuyDO);
}
