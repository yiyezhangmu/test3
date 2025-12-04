package com.coolcollege.intelligent.service.dingSync;

import com.dingtalk.api.response.*;
import com.taobao.api.ApiException;

import java.util.List;

/**
 * 钉钉请求客户端服务层
 * @author byd
 * @date 2021-03-23 9:59
 */
public interface DingTalkClientService {

    /**
     * 部门详情
     * @param deptId
     * @param accessToken
     * @return
     * @throws ApiException
     */
     OapiV2DepartmentListsubResponse.DeptBaseResponse getDeptDetail(String deptId, String accessToken) throws ApiException;

    /**
     * 根据deptId获取下级列表
     * @param deptId
     * @param accessToken
     * @return
     */
    List<OapiV2DepartmentListsubResponse.DeptBaseResponse> getDeptList(Long deptId, String accessToken) throws ApiException;

    // 可视部门
    List<OapiV2DepartmentListsubResponse.DeptBaseResponse> getAuthDeptList(String corpId, String appType, String accessToken) throws ApiException;


    /**
     * 根据userId获取用户详情
     * @param userId
     * @param accessToken
     * @throws ApiException
     * @return: EnterpriseUserDO
     * @Author: xugangkun
     * @Date: 2021/3/23 11:08
     */
    OapiV2UserGetResponse.UserGetResponse getUserDetail(String userId, String accessToken) throws ApiException;

    /**
     * 根据userIdList获取用户详情列表
     * @param userIds
     * @param accessToken
     * @throws ApiException
     * @return: EnterpriseUserDO
     * @Author: xugangkun
     * @Date: 2021/3/23 11:08
     */
    List<OapiV2UserGetResponse.UserGetResponse> getUserDetailList(List<String> userIds, String accessToken) throws ApiException;

    /**
     * 获得主管员id
     * @param token
     * @throws
     * @return: java.lang.String
     * @Author: xugangkun
     * @Date: 2021/3/27 10:45
     */
    List<String> getMainAdmin(String token);

    /**
     * 获得部门下的用户id列表
     * @param deptId
     * @param accessToken
     * @throws ApiException
     * @return: com.dingtalk.api.response.OapiUserListidResponse.ListUserByDeptResponse
     * @Author: xugangkun
     * @Date: 2021/3/27 19:56
     */
    OapiUserListidResponse.ListUserByDeptResponse getDeptUserIdList(String deptId, String accessToken) throws ApiException;

    /**
     * 获取可见范围
     * @param accessToken
     * @return
     * @throws ApiException
     */
    OapiAuthScopesResponse getAuthScopes(String accessToken) throws ApiException;

    /**
     * 获取子部门ID列表,只获取下一级的所有子节点，不获取整条链路
     * @param deptId
     * @param accessToken
     * @return
     * @throws ApiException
     */
    OapiV2DepartmentListsubidResponse.DeptListSubIdResponse getChildDeptIds(String deptId, String accessToken) throws ApiException;

}
