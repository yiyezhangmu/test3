package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.model.boss.request.BossEnterpriseExportRequest;
import com.coolcollege.intelligent.model.enterprise.BannerDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseBossExportDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseDTO;
import com.coolcollege.intelligent.model.enterprise.dto.StoreBaseInfoSettingDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseBossVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseCorpNameVO;
import com.coolcollege.intelligent.model.platform.EnterpriseStoreRequiredDO;

import java.util.List;
import java.util.Map;

public interface EnterpriseService {

    EnterpriseDO selectById(String enterpriseId);

    void insertEnterprise(EnterpriseDO enterpriseDO);

    void updateEnterpriseById(EnterpriseDO enterpriseDO);

    Boolean updateInfo(EnterpriseDTO enterpriseDTO);

    Map<String, Object> getBaseInfo(String id);

    Object setBanner(String eid, List<BannerDO> banner);

    List<BannerDO> getBanner(String eid);

    List<EnterpriseStoreRequiredDO> getStoreRequired(String eid);

    Boolean saveOrUpdateSettings(String eid, EnterpriseSettingDO setting);

    EnterpriseSettingDO getEnterpriseSettings(String eid);

    Integer updateLimitStoreCount(String enterpriseId, Integer limitStoreCount);





                                  /**
                                   * 默认配置
                                   * @param eid
                                   */
    void defaultSetting(String eid);

    PageVO<EnterpriseBossVO> listEnterprise(BossEnterpriseExportRequest param);

    Map<String, Object> getSystemSetting(String eid, String model, String fields);

    EnterpriseDTO  getBusinessManagement(String eId);

    /**
     * 保存企业管理设置
     * @param eId 企业id
     * @param entity 设置详细信息
     * @return
     */
    Boolean saveBussinessManagement(String eId, EnterpriseDTO entity);

    EnterpriseSettingDO getDingSync(String eId);

    Boolean saveDingSync(String eId,EnterpriseSettingDO entity);

    StoreBaseInfoSettingDTO getStoreBaseInfoSetting(String eId);

    Boolean saveStoreBaseInfoSetting(String eId, StoreBaseInfoSettingDTO entity);

    Boolean frozenEnterprise(String eid,Boolean isFrozen);

    /**
     * 获取企业信息
     * @param enterpriseIds
     * @return
     */
    List<EnterpriseDO> getEnterpriseByIds(List<String> enterpriseIds);
    Boolean updateEnterpriseName(String eid,String enterpriseName);

    List<EnterpriseBossExportDTO> exportList(BossEnterpriseExportRequest param);

    void truncateBusinessData(String eid);

    /**
     * 是否是历史企业
     * @param enterpriseId
     * @return
     */
    boolean isHistoryEnterprise(String enterpriseId);

    /**
     * 更新企业标签
     * @param id
     * @param tag
     * @author: xugangkun
     * @return void
     * @date: 2022/4/8 10:12
     */
    void updateEnterpriseTag(String id, String tag);

    void updateEnterpriseCSM(String id, String csm);

    void updateEnterpriseIsLeaveInfo (String id);

    Integer updateDeviceCount(String enterpriseId, Integer deviceCount);


    Integer updateStoreCount(String enterpriseId, Integer storeCount);

    EnterpriseCorpNameVO getStoreCount(String corpId);
}
