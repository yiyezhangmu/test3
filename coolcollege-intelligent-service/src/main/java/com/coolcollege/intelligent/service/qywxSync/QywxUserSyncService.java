package com.coolcollege.intelligent.service.qywxSync;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;

/**
 * 企业微信用户同步
 * @ClassName: DingUserSyncService
 * @Author: wxp
 * @Date: 2021/6/12 14:38
 */
public interface QywxUserSyncService {

    /**
     * 同步企业微信用户
     * @param corpId
     * @param userId
     * @param accessToken
     * @param eid
     * @param dbName
     * @param appType
     * @author: xugangkun
     * @return void
     * @date: 2021/6/24 10:28
     */
    void syncWeComUser(String corpId, String userId, String accessToken, String eid, String dbName,
                       String appType);

    /**
     * 同步修改企业用户(企业库)
     * @param enterpriseUser
     * @param eid
     * @param setting
     * @return: void
     * @Author: wxp
     * @Date: 2021/3/23 15:02
     */
    void syncEntUser(EnterpriseUserRequest enterpriseUser, String eid, EnterpriseSettingVO setting, String appType);

    /**
     * 删除cool用户以及对应的映射关系
     * @param userId
     * @param eid
     * @return: void
     * @Author: wxp
     * @Date: 2021/6/12 13:50
     */
    void syncDeleteEntUser(String userId, String eid);

    /**
     * 同步平台库的用户信息
     * @param enterpriseUser
     * @param eid
     * @param flag 是否新增
     * @return: void
     * @Author: wxp
     * @Date: 2021/6/12 11:48
     */
    void syncConfigUser(EnterpriseUserDO enterpriseUser, String eid, Boolean flag);

    /**
     * 同步删除用户信息
     * @param eid
     * @param userId
     * @param dbName
     * @return: void
     * @Author: wxp
     * @Date: 2021/6/12 17:27
     */
    void syncDeleteWeComUser(String eid, String userId, String dbName);

}
