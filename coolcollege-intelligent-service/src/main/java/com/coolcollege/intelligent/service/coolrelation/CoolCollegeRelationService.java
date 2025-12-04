package com.coolcollege.intelligent.service.coolrelation;

import com.coolcollege.intelligent.common.enums.EnterpriseApiErrorEnum;
import com.coolcollege.intelligent.common.enums.PlatFormApiErrorEnum;
import com.coolcollege.intelligent.model.coolrelation.dto.AdvertDTO;
import com.coolcollege.intelligent.model.coolrelation.dto.CoolInfoDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author xugk
 */
public interface CoolCollegeRelationService {

    /**
     * 获取企业酷学院广告配置
     * @param eid
     * @param platFormType
     * @param type
     * @author: xugangkun
     * @return java.lang.Boolean
     * @date:
     */
    Boolean getAdvertSetting(String eid, String platFormType, String type);

    /**
     * 设置企业酷学院广告配置
     * @param eid
     * @param advertDTO
     * @author: xugangkun
     * @return java.lang.Boolean
     * @date:
     */
    void setAdvertSetting(String eid, AdvertDTO advertDTO);

    /**
     * 获得酷学院token
     * @param enterpriseId
     * @param corpId
     * @param userId
     * @param appType
     * @author: xugangkun
     * @return java.lang.String
     * @date: 2021/5/13 16:38
     */
    Pair<CoolInfoDTO, PlatFormApiErrorEnum> getCoolToken(String enterpriseId, String corpId, String userId, String appType);

    /**
     * 防止出现酷学院那边token被刷新出现token不一致的情况
     * @author chenyupeng
     * @date 2021/12/9
     * @param enterpriseApiErrorEnum
     * @param user
     * @return java.lang.String
     */
    String checkAndRefreshToken(EnterpriseApiErrorEnum enterpriseApiErrorEnum, CurrentUser user);
}
