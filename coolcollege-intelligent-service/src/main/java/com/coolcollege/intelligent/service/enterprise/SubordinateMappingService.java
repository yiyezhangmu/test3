package com.coolcollege.intelligent.service.enterprise;

import java.util.List;

public interface SubordinateMappingService {


    /**
     * 判断用户是否管辖全部用户
     * @param enterpriseId
     * @param currentUserId
     * @return
     */
    Boolean checkHaveAllSubordinateUser(String enterpriseId, String currentUserId);


    /**
     * 判断用户是否管辖全部门店
     * @param enterpriseId
     * @param currentUserId
     * @return
     */
    Boolean checkHaveAllSubordinateStore(String enterpriseId, String currentUserId);

    /**
     * 获取管辖用户
     * @param enterpriseId
     * @param currentUserId
     * @return
     */
    List<String> getSubordinateUserIdList(String enterpriseId, String currentUserId,Boolean addCurrentFlag);

    /**
     *  保留管辖用户
     * @param enterpriseId
     * @param currentUserId
     * @param userIdList
     * @return
     */
    List<String> retainSubordinateUserIdList(String enterpriseId, String currentUserId, List<String> userIdList,Boolean addCurrentFlag);

}
