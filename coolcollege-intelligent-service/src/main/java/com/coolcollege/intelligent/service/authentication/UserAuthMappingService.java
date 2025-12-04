package com.coolcollege.intelligent.service.authentication;

import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/11/11
 */
public interface UserAuthMappingService {
    /**
     * 删除用户权限信息
     * @param eid
     * @param userId
     * @return
     */
    Boolean deleteUserAuthMapping(String eid,String userId);

    /**
     * 获取用户权限
     * @param eid
     * @param userId
     * @return
     */
    List<UserAuthMappingDO> listUserAuthMappingByUserId(String eid, String userId);

    /**
     * 删除用户区域权限
     * @param eid
     * @param userId
     * @param mappingIds
     */
    void deleteAuthMappingByUserIdAndMappingIds(String eid,String userId, List<String> mappingIds);

    /**
     * 删除用户区域权限
     * @param eid
     * @param userId
     * @param mappingIds
     */
    void addUserRegionAuth(String eid,String userId, List<String> mappingIds);


    void changeUserRegionAuth(String eid,String userId, List<String> mappingIds);
}
