package com.coolcollege.intelligent.service.dingSync;

import com.coolcollege.intelligent.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.dingtalk.api.response.OapiV2UserGetResponse;

import java.util.List;
import java.util.Map;

/**
 * 钉钉用户同步
 * @ClassName: DingUserSyncService
 * @Author: xugangkun
 * @Date: 2021/3/23 14:38
 */
public interface DingUserSyncService {

    /**
     * 把钉钉返回信息转化为门店中对应的实体
     * @param response 请求结果
     * @param enterpriseUser 企业用户实体
     * @param eid
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/23 14:40
     */
    EnterpriseUserDO initEnterpriseUser(OapiV2UserGetResponse.UserGetResponse response, EnterpriseUserDO enterpriseUser, String eid);

    /**
     * 同步修改企业用户(企业库)
     * @param response
     * @param enterpriseUser
     * @param eid
     * @param isSyncRoleAndAuth
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/23 15:02
     */
    void syncUser(OapiV2UserGetResponse.UserGetResponse response, EnterpriseUserDO enterpriseUser
            , String eid, EnterpriseSettingVO setting, Boolean isSyncRoleAndAuth);

    /**
     * 删除cool用户以及对应的映射关系
     * @param userId
     * @param eid
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/25 13:50
     */
    void syncDeleteUser(String userId, String eid);

    /**
     * 同步删除用户列表
     * @param userList
     * @param eid
     * @throws
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/25 14:27
     */
    void syncDeleteUserList(List<EnterpriseUserDO> userList, String eid);

    /**
     * 同步平台库的用户信息
     * @param response
     * @param enterpriseUser
     * @param eid
     * @param flag 是否新增
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/24 11:48
     */
    void syncConfigUser(OapiV2UserGetResponse.UserGetResponse response, EnterpriseUserDO enterpriseUser, String eid, Boolean flag);

    /**
     * 同步删除用户信息(平台库)
     * @param eid
     * @param userIds
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/25 17:27
     */
    void syncDeleteConfigUser(String eid, List<String> userIds);

    /**
     * 处理用户-钉钉职位关系
     * @param eid
     * @param position
     * @param userId
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/31 10:19
     */
    void syncDingPosition(String eid, String position,String userId, Map<String, Boolean> leaderMap);

    /**
     * 同步用户部门
     * @param eid
     * @param userId
     * @param deptIds 用户所在的部门列表
     * @author: xugangkun
     * @return void
     * @date: 2021/10/27 15:04
     */
    void syncUserDepartment(String eid, String userId, List<String> deptIds);

    /**
     * 同步用户部门权限
     * @param eid
     * @param userId
     * @param deptIds
     */
    void syncUserDepartmentAuth(String eid, String userId, List<String> deptIds);

    /**
     * 同步用户和区域直接的关系
     * @param eid
     * @param userId
     * @param deptIds
     */
    void syncUserRegionMapping(String eid, String userId, List<String> deptIds, Boolean enableDingSync);

    /**
     * 同步用户的部门我的下级数据
     * @param eid
     * @param userId
     * @param deptIds
     */
    void syncSubordinateMapping(String eid, String userId, List<String> deptIds);
    /**
     * 同步用户区域权限
     * @param userId 用户id
     * @param eid
     * @param deptIds 部门列表
     * @param setting 企业配置
     * @param position 用户职位
     * @param response 钉钉用户详情，如果是企业微信同步，该对象为null
     * @param leaderMap 用户对应的部门职级，key:部门id,value:对应部门是否是上级
     * @author: xugangkun
     * @return void
     * @date: 2021/10/27 14:50
     */
    void syncUserAuth(String userId, String eid, List<String> deptIds, EnterpriseSettingVO setting, String position,
                             OapiV2UserGetResponse.UserGetResponse response, Map<String, Boolean> leaderMap, String appType);

    EnterpriseUserDO syncOnePartyUser(String eid, String userId, EnterpriseUserDTO dingEnterpriseUserDTO);

    EnterpriseUserDTO initAiUser(String eid);

    /**
     * 门店通-同步用户权限
     * @param eid
     * @param userId
     * @param dingDeptIds
     */
    void syncDingOnePartyUserAuth(String eid, String userId, List<Long> dingDeptIds);

    void syncFreezeUser(String eid, String userId);
}
