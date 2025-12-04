package com.coolcollege.intelligent.service.enterprise.impl;

import com.coolcollege.intelligent.dao.enterprise.BizMarketBuyServiceMapper;
import com.coolcollege.intelligent.model.enterprise.BizMarketBuyDO;
import com.coolcollege.intelligent.service.enterprise.BizMarketBuyService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2020/1/16.
 */
@Service
public class BizMarketBuyServiceImpl implements BizMarketBuyService {


    @Resource
    public BizMarketBuyServiceMapper bizMarketBuyServiceMapper;

    @Override
    public BizMarketBuyDO getByCorpId(String corpId) {
        DataSourceHelper.reset();
        List<BizMarketBuyDO> list = bizMarketBuyServiceMapper.getCorpId(corpId);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public void updateBizMarketBuy(BizMarketBuyDO bizMarketBuyDO) {
        bizMarketBuyServiceMapper.updateBizMarketBuy(bizMarketBuyDO);
    }
}
