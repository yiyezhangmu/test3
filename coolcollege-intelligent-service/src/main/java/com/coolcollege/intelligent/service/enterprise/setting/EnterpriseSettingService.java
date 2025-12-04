package com.coolcollege.intelligent.service.enterprise.setting;

import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.param.DingDingSyncSettingUpdParam;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseThemeColorSettingsDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;

/**
 * describe:
 *
 * @author wxp
 * @date 2021/3/25
 */
public interface EnterpriseSettingService {

    /**
     * 根据企业id获取企业配置信息
     * @param enterpriseId
     * @author: xugangkun
     * @return com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO
     * @date:
     */
    EnterpriseSettingDO selectByEnterpriseId(String enterpriseId);

    Boolean saveOrUpdateEnterpriseDingDingSyncSetting(String eid, DingDingSyncSettingUpdParam param, Long bossUserId, String username);
    /**
     * 查询企业配置
     * @param enterpriseId
     * @return
     */
    EnterpriseSettingVO getEnterpriseSettingVOByEid(String enterpriseId);

    /**
     * 获得同步角色权限
     * @param enterpriseId
     * @throws
     * @return: java.lang.Boolean
     * @Author: xugangkun
     * @Date: 2021/3/26 15:55
     */
    Boolean syncRolePermissions(String enterpriseId);

    /**
     * 获得同步职位权限
     * @param enterpriseId
     * @throws
     * @return: java.lang.Boolean
     * @Author: xugangkun
     * @Date: 2021/3/26 15:55
     */
    Boolean syncPositionPermissions(String enterpriseId);

    /**
     * 根据企业id跟新企业接入酷学院配置
     * @param record
     * @author: xugangkun
     * @return int
     * @date:
     */
    int updateAccessCoolCollegeByEnterpriseId(EnterpriseSettingDO record);

    Boolean updateSyncPassengerByEid(String eid, Boolean syncPassenger) ;


    Boolean deleteAppHomePagePic(String eid);

    Boolean updateThemeColorSetting(String enterpriseId, EnterpriseThemeColorSettingsDTO param);
}
