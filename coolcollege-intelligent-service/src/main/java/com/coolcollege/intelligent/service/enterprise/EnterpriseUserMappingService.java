package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserMappingDO;

import java.util.List;

/**
 * Created by Administrator on 2020/1/16.
 */
public interface EnterpriseUserMappingService {

    List<EnterpriseUserMappingDO> buildEnterpriseUserMappings(String eid, List<EnterpriseUserDO> deptUsers);

    void batchInsertOrUpdate(List<EnterpriseUserMappingDO> mappings);

    /**
     * 删除平台映射表用户关系
     *
     * @param delUserIds
     */
    void deleteByUserIds(List<String> delUserIds, String eid);

    /**
     * 根据userId 获取企业id
     * @param userIds
     * @return
     */
    List<EnterpriseUserMappingDO> getUserAllEnterpriseIdsByUserIds(List<String> userIds, String enterpriseId);

    /**
     * 根据企业id和用户id获得企业用户映射关系
     * @param eid
     * @param userId
     * @throws
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseUserMappingDO
     * @Author: xugangkun
     * @Date: 2021/3/29 10:34
     */
    EnterpriseUserMappingDO selectByEidAndUserId(String eid, String userId);

    /**
     * 更新用户状态
     * @param unionid
     * @param enterpriseId
     * @param userStatus
     * @return
     */
    Integer updateEnterpriseUserStatus(String unionid, String enterpriseId, Integer userStatus);

    /**
     * 跟新平台库用户状态
     * @param unionids
     * @param enterpriseId
     * @param userStatus
     * @return
     */
    Integer updateEnterpriseUserStatus(List<String> unionids, String enterpriseId, Integer userStatus);

    /**
     * 新增usserMappring信息
     * @param enterpriseId
     * @param userId
     * @param unionId
     * @param userStatus
     * @return
     */
    Integer insertEnterpriseUserMapping(String enterpriseId, String userId, String unionId, Integer userStatus);

    /**
     * 根据unionid获取映射关系
     * @param enterpriseId
     * @param unionId
     * @return
     */
    EnterpriseUserMappingDO selectByEnterpriseIdAndUnionid(String enterpriseId, String unionId);

    /**
     * 删除映射关系
     * @param id
     * @return
     */
    Integer deleteUserMappingById(String id);

    /**
     * 根据企业获取用户映射关系
     * @param enterpriseId
     * @return
     */
    List<EnterpriseUserMappingDO> getUserMappingListByEnterpriseId(String enterpriseId);

    /**
     * 删除用户映射关系
     * @param enterpriseId
     * @param unionId
     * @return
     */
    Integer deleteUserMappingByUnionid(String enterpriseId, String unionId);

    /**
     * 批量删除
     * @param enterpriseId
     * @param unionIds
     * @return
     */
    Integer deleteUserMappingByUnionids(String enterpriseId, List<String> unionIds);


    Integer saveEnterpriseUserMapping(EnterpriseUserMappingDO saveDo);

    /**
     * 更新userMapping unionid
     * @return
     */
    Integer updateUserMappingUnionid();

    /**
     * 获取用户所有企业
     * @param userId
     * @return
     */
    List<EnterpriseUserMappingDO> getUserAllEnterpriseIdsByUserId(String userId);


}
