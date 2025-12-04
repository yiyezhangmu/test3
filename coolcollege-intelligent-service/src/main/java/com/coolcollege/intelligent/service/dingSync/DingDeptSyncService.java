package com.coolcollege.intelligent.service.dingSync;

import com.coolcollege.intelligent.dto.OpStoreAndRegionDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.service.sync.SyncContext;
import com.taobao.api.ApiException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author byd
 * @date 2021-03-22 16:13
 */
public interface DingDeptSyncService {

    void setDingSyncScheduler(String enterpriseId, String userId, String userName);

    void syncDingDepartmentAll(String eid, String corpId) throws ApiException;

    Set<String> syncDept(SyncContext syncContext) throws ApiException;

    /**
     * 门店通-同步门店或区域
     * @param eid
     * @param corpId
     * @param userId
     * @param userName
     * @throws ApiException
     */
    void syncDingOnePartyRegionAndStore(String eid, String corpId, String userId, String userName, EnterpriseSettingVO enterpriseSetting) throws ApiException;

    /**
     * 门店通-同步门店分组
     * @param eid
     * @param dingCorpId
     * @param userId
     * @param userName
     * @throws ApiException
     */
    void syncDingOnePartyStoreGroup(String eid, String dingCorpId, String userId, String userName) throws ApiException;

    /**
     * 门店通-同步单个门店或区域
     * @param eid
     * @param storeAndRegion
     * @throws ApiException
     */
    void syncSingleOnePartyStoreAndRegion(String eid, OpStoreAndRegionDTO storeAndRegion) throws ApiException;

}
