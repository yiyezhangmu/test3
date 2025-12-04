package com.coolcollege.intelligent.service.dingSync;

import com.coolcollege.intelligent.dto.OpRoleDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.dingtalk.api.response.OapiRoleListResponse;
import com.taobao.api.ApiException;

import java.util.List;

/**
 * 钉钉角色同步
 *
 * @ClassName: DingRoleSyncService
 * @Author: xugangkun
 * @Date: 2021/3/23 10:14
 */
public interface DingRoleSyncService {

    /**
     * 同步企业角色信息
     * @param eid 钉钉的企业标识
     * @param openRoleList
     * @throws ApiException
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/22 15:29
     */
    void syncDingRoles(String eid, List<OapiRoleListResponse.OpenRoleGroup> openRoleList, EnterpriseSettingVO setting);

    /**
     * 删除所有的钉钉角色以及对应的用户映射关系
     * @param eid
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/4/2 16:11
     */
    void deleteDingRole(String eid,EnterpriseSettingVO vo);

    /**
     * 删除所有的钉钉职位以及对应的用户映射关系
     * @param eid
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/4/2 16:11
     */
    void deleteDingPosition(String eid,EnterpriseSettingVO vo);

    /**
     * 门店通：同步角色
     * @param eid
     * @param openRoles
     * @param roleId
     */
    void syncDingOnePartyRoles(String eid, List<OpRoleDTO> openRoles, Long roleId);
}
