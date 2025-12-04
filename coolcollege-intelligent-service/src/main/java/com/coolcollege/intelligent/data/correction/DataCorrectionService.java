package com.coolcollege.intelligent.data.correction;

import com.coolcollege.intelligent.model.dataCorrection.BaiduChangeGaodeDTO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.store.StoreDO;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zyp
 */
public interface DataCorrectionService {
    /**
     * 11月7号版本菜单表更
     */
    void authMenuChange();
    void roleDuplicateFix(String db, List<EnterpriseConfigDO> enterpriseConfigDOList, AtomicInteger count);
    void storeRegionIdFIx(String db, List<EnterpriseConfigDO> enterpriseConfigDOList, AtomicInteger count);
    void baiduChangeGaode(String eid, List<BaiduChangeGaodeDTO> baiduChangeGaodeDTOList);
    Long syncRegionPath(String eid, String storeId, boolean isRunIncrement, String dbName);


    void syncStoreRegionPath(String eid,String storeId);

    /**
     * 更新门店的区域路径为全路径
     * @param eid
     * @param storeId
     * @author: xugangkun
     * @return void
     * @date: 2022/1/13 14:19
     */
    void syncStoreRegionPath2(String eid,String storeId);

    void syncDeviceBindStoreId(String eid);
    void syncRootCorpDevice(String eid);
    void deleteRootCorpId(String rootCorpId);
    void syncEhrAddress(String eid,Integer  unitId,Boolean isChange);
    void setFormWork(String eid);
    void fixDevice(String eid, DeviceDO deviceDO);

    void passengerFix(String eid,String time,String deviceIds);

    /**
     * 订正区域路径
     * @param enterpriseId 企业id
     * @param singleStore 门店
     * @param dbName 数据库名
     */
    void syncRegionPath(String enterpriseId, StoreDO singleStore, String dbName);
    /**
     * 同步导入的用户信息到平台库
     * @param enterpriseId 企业id
     */
    Integer syncImportUserForPlatform(String enterpriseId);

    void updateRoleMenu(String eid);

    void initColumnResult(String eid,String dbName, String beginTime);

    @Deprecated
    void initCheckResultLevel(String eid,String dbName);

    void dealHighResultScore(String eid,String dbName, String beginTime);

    void initAiColumnResult(String eid,String dbName, String beginTime);
}
