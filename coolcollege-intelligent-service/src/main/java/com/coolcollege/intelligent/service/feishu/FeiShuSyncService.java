package com.coolcollege.intelligent.service.feishu;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author suzhuhong
 * @Date 2022/11/30 10:28
 * @Version 1.0
 **/
public interface FeiShuSyncService {

    /**
     * 同步飞书用户
     * @param corpId
     * @param userId
     * @param eid
     * @param dbName
     * @param appType
     */
    void syncFsUser(String corpId, String userId, String eid, String dbName, String appType);

    /**
     * 同步修改企业用户(企业库)
     * @param enterpriseUser
     * @param eid
     * @param setting
     * @param appType
     */
    void syncEntUser(EnterpriseUserRequest enterpriseUser, String eid, EnterpriseSettingVO setting, String appType);

    /**
     * 删除cool用户以及对应的映射关系
     * @param userId
     * @param eid
     */
    void syncDeleteEntUser(String userId, String eid);

    /**
     * 同步平台库的用户信息
     * @param enterpriseUser
     * @param eid
     * @param flag
     */
    void syncConfigUser(EnterpriseUserDO enterpriseUser, String eid, Boolean flag);

    /**
     * 同步删除用户信息
     * @param eid
     * @param userId
     * @param dbName
     */
    void syncDeleteWeComUser(String eid, String userId, String dbName);
}
