package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.BizMarketBuyDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2020/1/16.
 * @author shoul
 */
@Mapper
public interface BizMarketBuyServiceMapper {

    /**
     * 获取corpid
     * @param corpId
     * @return
     */
    List<BizMarketBuyDO> getCorpId(@Param("corpId") String corpId);

    /**
     * 更新企业id
     * @param bizMarketBuyDO
     */
    void  updateBizMarketBuy(BizMarketBuyDO bizMarketBuyDO);
}
