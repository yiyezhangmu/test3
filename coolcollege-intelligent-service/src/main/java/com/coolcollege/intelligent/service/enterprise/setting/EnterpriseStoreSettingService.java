package com.coolcollege.intelligent.service.enterprise.setting;

import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseLicenseSettingRequest;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseStoreSettingRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseStoreSettingVO;
import com.coolcollege.intelligent.model.store.dto.ExtendFieldInfoDTO;

public interface EnterpriseStoreSettingService {

    /**
     * @Author chenyupeng
     * @Description 修改门店动态扩展字段
     * @Date 2021/6/29
     * @param enterpriseId
     * @param extendFieldInfoDTO
     */
    String updateExtendFieldInfo(String enterpriseId, ExtendFieldInfoDTO extendFieldInfoDTO);

    /**
     * @Author chenyupeng
     * @Description 删除门店动态扩展字段（部分删除）
     * @Date 2021/6/29
     * @param enterpriseId
     * @param extendFieldKey
     */
    Integer deleteExtendFieldInfo(String enterpriseId, String extendFieldKey);

    /**
     * 查询动态扩展字段
     * @Author chenyupeng
     * @Date 2021/7/6
     * @param enterpriseId
     * @return: java.lang.String
     */
    String queryExtendFieldInfo(String enterpriseId);

    /**
     * 获取门店基础信息设置
     * @param enterpriseId
     * @author: xugangkun
     * @return com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO
     * @date: 2021/11/3 17:05
     */
    EnterpriseStoreSettingDO getEnterpriseStoreSetting(String enterpriseId);


    /**
     * 设置门店的完善度
     * @param store 门店对象
     * @param perfectionField 完善度判断字段
     * @author: xugangkun
     * @return java.lang.String
     * @date: 2021/11/3 16:56
     */
    String getStorePerfection(Object store, String perfectionField);

    /**
     * 获取企业门店配置
     * @param enterpriseId
     * @return
     */
    EnterpriseStoreSettingVO getEnterpriseStoreSettingVO(String enterpriseId);

    /**
     * 修改证照时长
     * @param enterpriseStoreSettingRequest
     */
    void updateStoreTimeSetting(String enterpriseId, EnterpriseStoreSettingRequest enterpriseStoreSettingRequest);

    void updateLicenseSetting(String enterpriseId, EnterpriseLicenseSettingRequest enterpriseStoreSettingRequest);
}
