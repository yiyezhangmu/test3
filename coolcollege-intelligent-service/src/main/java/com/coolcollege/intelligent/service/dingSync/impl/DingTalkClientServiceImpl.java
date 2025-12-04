package com.coolcollege.intelligent.service.dingSync.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.service.dingSync.DingTalkClientService;
import com.coolcollege.intelligent.service.sync.SyncContext;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 钉钉请求客户端服务层
 *
 * @author byd
 * @date 2021-03-23 10:00
 */
@Service
@Slf4j
public class DingTalkClientServiceImpl implements DingTalkClientService {

    @Override
    public OapiV2DepartmentListsubResponse.DeptBaseResponse getDeptDetail(String deptId, String accessToken) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(SyncConfig.DEPT_SUB_DETAIL_API);
        OapiV2DepartmentGetRequest req = new OapiV2DepartmentGetRequest();
        req.setDeptId(Long.valueOf(deptId));
        req.setLanguage("zh_CN");
        OapiV2DepartmentGetResponse rsp = client.execute(req, accessToken);
        OapiV2DepartmentGetResponse.DeptGetResponse deptGetResponse = rsp.getResult();
        if (rsp.getErrcode() != SyncConfig.DING_NORMAL_CODE && rsp.getErrcode() != SyncConfig.DING_DEPT_NOT_AUTHED) {
            throw new ApiException(String.valueOf(rsp.getErrcode()), rsp.getErrmsg());
        }
        if(rsp.getErrcode() == SyncConfig.DING_DEPT_NOT_AUTHED){
            OapiV2DepartmentListsubResponse.DeptBaseResponse deptBaseResponse = new OapiV2DepartmentListsubResponse.DeptBaseResponse();
            deptBaseResponse.setDeptId(null);
        }
        if (deptGetResponse == null) {
            return null;
        }
        OapiV2DepartmentListsubResponse.DeptBaseResponse deptBaseResponse = new OapiV2DepartmentListsubResponse.DeptBaseResponse();
        deptBaseResponse.setDeptId(deptGetResponse.getDeptId());
        deptBaseResponse.setName(deptGetResponse.getName());
        deptBaseResponse.setParentId(deptBaseResponse.getParentId());
        return deptBaseResponse;
    }

    @Override
    public List<OapiV2DepartmentListsubResponse.DeptBaseResponse> getDeptList(Long deptId, String accessToken) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(SyncConfig.DEPT_SUB_LIST_API);
        //处理掉
        OapiV2DepartmentListsubRequest req = new OapiV2DepartmentListsubRequest();
        req.setDeptId(deptId);
        req.setLanguage("zh_CN");
        OapiV2DepartmentListsubResponse response = client.execute(req, accessToken);
        if(response.getSubCode().equals(String.valueOf(SyncConfig.API_EXCEED_LIMIT))) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("子部门列表接口超限getDeptList",e);
            }
            response = client.execute(req, accessToken);;
        }
        if (response.getErrcode() != SyncConfig.DING_NORMAL_CODE) {
            throw new ApiException(String.valueOf(response.getErrcode()), response.getErrmsg());
        }
        return response.getResult();
    }

    @Override
    public List<OapiV2DepartmentListsubResponse.DeptBaseResponse> getAuthDeptList(String corpId, String appType, String accessToken) throws ApiException {
        SyncContext syncContext = new SyncContext(corpId, appType);
        List<String> deptIdList = syncContext.getAuthScope().getDeptIdList();
        List<OapiV2DepartmentListsubResponse.DeptBaseResponse> departments = Lists.newArrayList();
        for (String deptId : deptIdList) {
            OapiV2DepartmentListsubResponse.DeptBaseResponse deptDetail = getDeptDetail(deptId, accessToken);
            if (deptDetail != null) {
                departments.add(deptDetail);
            }
        }
        return departments;
    }


    @Override
    public OapiV2UserGetResponse.UserGetResponse getUserDetail(String userId, String accessToken) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(SyncConfig.USER_SUB_DETAIL_API);
        OapiV2UserGetRequest req = new OapiV2UserGetRequest();
        req.setUserid(userId);
        req.setLanguage("zh_CN");
        OapiV2UserGetResponse response = client.execute(req, accessToken);

        if (response.getErrcode() != SyncConfig.DING_NORMAL_CODE) {
            if (response.getErrcode() == SyncConfig.DING_USER_NOT_AUTHED) {
                log.warn("getUserDetail failed, userId not auth, userId={}, accessToken={}", userId, accessToken);
                return null;
            } else {
                throw new ApiException(String.valueOf(response.getErrcode()), response.getErrmsg());
            }
        }
        log.info("钉钉返回用户详情为：" + JSON.toJSONString(response.getResult()));
        return response.getResult();
    }

    /**
     * 根据userIdList获取用户详情列表
     * @param userIds
     * @param accessToken
     * @throws ApiException
     * @return: EnterpriseUserDO
     * @Author: xugangkun
     * @Date: 2021/3/23 11:08
     */
    @Override
    public List<OapiV2UserGetResponse.UserGetResponse> getUserDetailList(List<String> userIds, String accessToken) throws ApiException {
        List<OapiV2UserGetResponse.UserGetResponse> users = Lists.newArrayList();
        for (String userId : userIds) {
            OapiV2UserGetResponse.UserGetResponse response = getUserDetail(userId, accessToken);
            if (response != null) {
                users.add(response);
            }
        }
        return users;
    }

    @Override
    public List<String>  getMainAdmin(String token) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/user/listadmin");
        OapiUserListadminRequest req = new OapiUserListadminRequest();
        OapiUserListadminResponse response = null;
        try {
            response = client.execute(req, token);
        } catch (ApiException e) {
            log.error("获取钉钉主管理员失败", e);
        }
        List<String> mainAdminUserIds = new ArrayList<>();
        // 管理员角色，1表示主管理员，2表示子管理员
        if (response != null ) {
            List<String> mainAdmin = ListUtils.emptyIfNull(response.getResult()).stream()
                    .filter(f -> f.getSysLevel().equals(1L))
                    .map(OapiUserListadminResponse.ListAdminResponse::getUserid)
                    .collect(Collectors.toList());
            if (mainAdmin != null && mainAdmin.size() > 0) {
                mainAdminUserIds = mainAdmin;
            }
        }
        return mainAdminUserIds;
    }

    @Override
    public OapiUserListidResponse.ListUserByDeptResponse getDeptUserIdList(String deptId, String accessToken) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(SyncConfig.DEPT_USER_LIST_API);
        OapiUserListidRequest req = new OapiUserListidRequest();
        req.setDeptId(Long.valueOf(deptId));
        OapiUserListidResponse rsp = client.execute(req, accessToken);
        if (rsp.getErrcode() != SyncConfig.DING_NORMAL_CODE) {
            throw new ApiException(String.valueOf(rsp.getErrcode()), rsp.getErrmsg());
        }
        return rsp.getResult() != null ? rsp.getResult() : new OapiUserListidResponse.ListUserByDeptResponse();
    }

    @Override
    public OapiAuthScopesResponse getAuthScopes(String accessToken) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(SyncConfig.AUTH_SCOPES_API);
        OapiAuthScopesRequest req = new OapiAuthScopesRequest();
        req.setHttpMethod("GET");
        OapiAuthScopesResponse rsp = client.execute(req, accessToken);
        return rsp;
    }

    @Override
    public OapiV2DepartmentListsubidResponse.DeptListSubIdResponse getChildDeptIds(String deptId, String accessToken) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(SyncConfig.DEPT_SUB_ID);
        OapiV2DepartmentListsubidRequest req = new OapiV2DepartmentListsubidRequest();
        req.setDeptId(Long.valueOf(deptId));
        req.setHttpMethod("POST");
        OapiV2DepartmentListsubidResponse rsp = client.execute(req, accessToken);
        if (rsp.getErrcode() != SyncConfig.DING_NORMAL_CODE) {
            throw new ApiException(String.valueOf(rsp.getErrcode()), rsp.getErrmsg());
        }
        log.info("获取同步的部门下级节点结果 result:{}", JSONObject.toJSONString(rsp.getResult()));
        return rsp.getResult();
    }

}
