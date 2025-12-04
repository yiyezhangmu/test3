package com.coolcollege.intelligent.service.user;

import com.coolcollege.intelligent.model.user.dto.DynamicRegionDTO;
import com.coolcollege.intelligent.model.user.dto.UserJurisdictionDTO;

import java.util.List;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/7/19 13:37
 */
public interface UserJurisdictionService {
    /**
     * 更新用户所管辖门店
     * @param
     * @return
     */
    void updateAllUserJurisdictionStore(List<String> enterpriseIds);

    void updateUserJurisdictionStore(String eid);

    UserJurisdictionDTO getUserJurisdiction(String eId, String userId);

    List<DynamicRegionDTO> getDynamicRegionParam(String enterpriseId, String userId);
}
