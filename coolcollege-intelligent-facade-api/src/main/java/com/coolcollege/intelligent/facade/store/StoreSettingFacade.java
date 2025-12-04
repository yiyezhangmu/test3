package com.coolcollege.intelligent.facade.store;

import com.coolcollege.intelligent.facade.dto.store.EnterpriseStoreSettingDTO;
import com.coolcollege.intelligent.facade.request.StoreSettingRequest;
import com.coolstore.base.dto.ResultDTO;


/**
 * 门店配置信
 * @author byd
 */
public interface StoreSettingFacade {


    /**
     * 获取门店证照高级配置
     * @param request 企业id
     * @return
     */
    ResultDTO<EnterpriseStoreSettingDTO> getStoreLicenseSetting(StoreSettingRequest request);

}
