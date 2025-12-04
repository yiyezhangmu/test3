package com.coolcollege.intelligent.service.enterprise;


import com.coolcollege.intelligent.common.sync.vo.AuthInfo;
import com.coolcollege.intelligent.common.sync.vo.AuthScope;
import com.coolcollege.intelligent.dto.AuthInfoDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.dingtalk.api.response.OapiCallCalluserResponse;
import com.dingtalk.api.response.OapiCallGetuserlistResponse;
import com.dingtalk.api.response.OapiRoleListResponse;
import com.taobao.api.ApiException;

import java.util.List;


public interface DingService {


    /**
     * 获取数智门店企业的suiteToken
     * @return
     */
    String getSuiteToken(String appType);

    String getCorpToken(String dingCorpId,String appType);

    /**
     * 获取企业通讯录接口调用授权凭证
     *
     * @param corpId
     * @return
     */

    String getAccessToken(String corpId, String appType) throws ApiException;

    /**
     * 获取子部门(包括自己)
     *
     * @param accessToken
    */

    List<SysDepartmentDO> getSubDepts(String id, String accessToken, String corpId, String appType) throws ApiException;

    /**
     * 获取部门详情
     *
     * @param accessToken
     * @param id
     * @return
     */
    SysDepartmentDO getDeptDetail(String accessToken, String id, String corpId, String appType) throws ApiException;

    /**
     * 获取一批部门及子部门信息
     *
     * @param ids
     * @param accessToken
     * @return
     */

    List<SysDepartmentDO> getAllDepts(List<String> ids, String accessToken, String corpId, String appType) throws ApiException;

    /**
     * 获取用户详情
     *
     * @param accessToken
     * @param userId
     * @return
     */
    EnterpriseUserRequest getUserDetail(String userId, String accessToken) throws ApiException;

   /**
    * 获取企业主管理员 有可能不存在主管理的情况
    * @param token
    * @return
    */
    List<String> getMainAdmin(String token);

    /**
     * 批量获取用户信息
     *
     * @param userIds
     * @param accessToken
     */

    List<EnterpriseUserRequest> getUsers(List<String> userIds, String accessToken) throws ApiException;

    /**
     * 查询某个部门下的所有用户
     *
     * @param deptId
     * @param accessToken
     */

    List<EnterpriseUserDO> getDeptUsers(Long deptId, String accessToken) throws ApiException;

 /**
  * 获取部门下用户详细列表
  * @param deptId
  * @param accessToken
  * @return
  */
 List<EnterpriseUserRequest> getDeptUserByAsync(String deptId, String accessToken) throws ApiException;

    /**
  * 获取部门下的用户列表（只包含当前部门，不包括子部门）
  * @return
  */
    List<EnterpriseUserDO> getDeptUserList(Long deptId,String accessToken) throws ApiException;

 /**
  * 获取管理员列表
  * @return
  */
    List<String>   getAdminList(String accessToken) throws ApiException;

 /**
  * 获取角色列表
  * @param accessToken
  * @return
  */
 List<OapiRoleListResponse.OpenRoleGroup>  getRoleList(String accessToken) throws ApiException;

 /**
  * 根据钉钉角色Id获取角色下的人员列表
  * @param accreeToken
  * @return
  */
 List  getUsersByRoleId(String accreeToken,Long roleId) throws ApiException;

 String getManiCorpId(String accessToken,String userId);


    OapiCallCalluserResponse callUser(String corpId, String sourceUserId, String targetUserId, String appType);

    OapiCallGetuserlistResponse getCallUserList(Long offSet,Long size, String appType);

    Boolean setCallUser(List<String> userIdList, String appType);

}
