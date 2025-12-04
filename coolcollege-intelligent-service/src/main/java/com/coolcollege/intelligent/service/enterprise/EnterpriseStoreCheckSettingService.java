package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterprisePatrolLevelDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseStoreCheckDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseStoreCheckNewDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterprisePatrolBiosVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterprisePatrolCheckResultVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterprisePatrolLevelVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseStoreCheckSettingVO;

import java.util.List;

public interface EnterpriseStoreCheckSettingService {
    /**
     * 获取企业巡店设置
     * @param eid 企业id
     * @return
     */
    EnterpriseStoreCheckSettingVO queryEnterpriseStoreCheckSettingVO(String eid);

    EnterpriseStoreCheckSettingDO getEnterpriseStoreCheckSetting(String eid);


    /**
     * 增加或修改企业巡店设置
     * @param eid 企业id
     * @param entity 巡店配置详情
     * @return
     */
    Boolean saveOrUpdateStoreCheckSetting(String eid, EnterpriseStoreCheckDTO entity);

    /**
     * 增加或修改企业巡店设置
     * @param eid 企业id
     * @param entity 巡店配置详情
     * @return
     */
    Boolean saveOrUpdateStoreCheckSettingNew(String eid, EnterpriseStoreCheckNewDTO entity);


    /**
     * 获取企业巡店bios配置信息
     * @param eid
     * @return
     */
    EnterprisePatrolBiosVO getPatrolBiosInfo(String eid);

    /**
     * 获取企业巡店等级信息
     * @param eid
     * @return
     */
    EnterprisePatrolLevelVO getStoreCheckLevel(String eid);

    /**
     * 获取企业巡店检查结果信息
     * @param eid
     * @return
     */
    EnterprisePatrolCheckResultVO getStoreCheckResult(String eid);

    /**
     * 修改企业
     * @param eid
     * @param bios
     * @return
     */
    Boolean updateStoreBios(String eid, EnterprisePatrolBiosVO bios);
}
