package com.coolcollege.intelligent.service.enterprise;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: UserRegionMappingService
 * @Description:
 * @date 2022-03-03 10:10
 */
public interface UserRegionMappingService {

    /**
     *
     * @param enterpriseId 企业id
     * @param userId 用户id
     * @param regionIds  区域ids
     */
    void dealUserRegionMapping(String enterpriseId, String userId, List<Long> regionIds);

    /**
     *
     * @param enterpriseId 企业id
     * @param userId 用户id
     * @param synDingDeptIds  第三方同步id
     */
    void dealUserRegionMappingBySynDingDeptId(String enterpriseId, String userId, List<Long> synDingDeptIds);

    /**
     * 删除user_region_mapping
     * @param enterpriseId
     * @param userId
     */
    void deletedUserRegionMappingByUserIds(String enterpriseId, String userId);

}
