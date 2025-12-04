package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseOpenLeaveInfoDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

import java.util.List;

/**
 * 企业留资
 *
 * @author wxp
 * @since 2022/8/17
 */
public interface EnterpriseOpenLeaveInfoService {

    /**
     * @param enterpriseId
     * @param authCode
     * @param user
     * @return
     */
    EnterpriseOpenLeaveInfoDO saveEnterpriseOpenLeaveInfo(String enterpriseId, String authCode, CurrentUser user);

    EnterpriseOpenLeaveInfoDO saveEnterpriseOpenLeaveInfoByFsAndQw(String enterpriseId, String phoneNum, CurrentUser user,String smsCode);

    /**
     * 检查用户是否需要弹框授权留资
     * @param enterpriseId
     * @param user
     * @param enterpriseDO
     * @return
     */
    boolean checkUserLeaveInfo(String enterpriseId, CurrentUser user, EnterpriseDO enterpriseDO, String appType);

    List<EnterpriseOpenLeaveInfoDO> listByEnterpriseIds(List<String> enterpriseIds);
}
