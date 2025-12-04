package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;

import java.util.List;


public interface EnterpriseConfigService {

    EnterpriseConfigDO selectByCorpId(String dingCorpId, String appType);

    EnterpriseConfigDO selectByEnterpriseId(String enterpriseId);

    void updateByEnterpriseId(EnterpriseConfigDO enterpriseConfigDO);

    /**
     * 批量获取企业
     * @param enterpriseIds
     * @return
     */
    List<EnterpriseConfigDO> selectByEnterpriseIds(List<String> enterpriseIds);

    /**
     * 获取所有
     * @param enterpriseIds
     * @return
     */
    List<EnterpriseConfigDO> selectAllEnterpriseConfig(List<String> enterpriseIds);

    List<EnterpriseConfigDO> selectAllEnterpriseConfig(List<String> enterpriseIds, List<Integer> statusList);

    /**
     * 根据dbName获取dbServer
     * @param dbName
     * @return
     */
    String getDbServerByDbName(String dbName);

    /**
     * 获取dbServer
     * @return
     */
    List<String> getDistinctDbServer();

    /**
     * 更新企业套餐
     * @param enterpriseId
     * @param packageId
     * @author: xugangkun
     * @return void
     * @date: 2022/3/25 11:36
     */
    void updateCurrentPackageByEnterpriseId(String enterpriseId, Long packageId);

    EnterpriseConfigDO selectByDingCorpId(String dingCorpId);
}
