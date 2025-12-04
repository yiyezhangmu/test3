package com.coolcollege.intelligent.service.enterprise.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import com.coolcollege.intelligent.common.http.CoolHttpClientResult;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.sync.vo.AuthInfo;
import com.coolcollege.intelligent.common.sync.vo.AuthScope;
import com.coolcollege.intelligent.common.util.ListOptUtils;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.common.util.isv.Env;
import com.coolcollege.intelligent.dto.AuthInfoDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.region.dto.LimitDTO;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.dingSync.DingTalkClientService;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.util.DingTalkLimitingUtil;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2020/1/16.
 */
@Service(value = "dingService")
@Slf4j
public class DingServiceImpl implements DingService {


    @Resource
    private Env env;

    @Value("${suite_token.url}")
    private String suiteTokenUrl;

    @Value("${corp_token.url}")
    private String corpTokenUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DingTalkClientService dingTalkClientService;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;
    @Resource
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;

    @Resource(name = "thirdPartyThreadPool")
    private ThreadPoolTaskExecutor executor;

    private long startOffset = 0L;
    private long defautlQuerySize = 100L;

    @Override
    public String getSuiteToken(String appType) {

        HttpEntity httpEntity = new HttpEntity(new AuthInfo(), this.getHeaders(null));
        String tokenUrl = String.format(suiteTokenUrl, appType);
        ResponseEntity<JSONObject> exchange = restTemplate.exchange(tokenUrl, HttpMethod.GET, httpEntity, JSONObject.class, Maps.newHashMap());
        log.info("getSuiteToken exchange:{}", Objects.requireNonNull(exchange.getBody()).toJSONString());
        JSONObject body = exchange.getBody();
        return body.getString("suite_token");
    }

    @Override
    public String getCorpToken(String dingCorpId,String appType) {
        HttpEntity httpEntity = new HttpEntity(new AuthInfo(), this.getHeaders(null));
        String tokenUrl = String.format(corpTokenUrl, dingCorpId,appType);
        ResponseEntity<JSONObject> exchange = restTemplate.exchange(tokenUrl, HttpMethod.GET, httpEntity, JSONObject.class, Maps.newHashMap());
        log.info("getCorpToken exchange:{}", Objects.requireNonNull(exchange.getBody()).toJSONString());
        JSONObject body = exchange.getBody();
        return body.getString("access_token");
    }

    /**
     * 获取Headers
     *
     * @param access_token
     * @return
     */
    private MultiValueMap<String, String> getHeaders(String access_token) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap();
        headers.add("X-Access-Token", access_token);
        headers.add("Content-Type", "application/json");
        return headers;
    }

    /**
     * 获取企业通讯录接口调用授权凭证
     *
     * @param corpId
     * @return
     */
    @Override
    public String getAccessToken(String corpId, String appType) throws ApiException {
        return enterpriseInitConfigApiService.getAccessToken(corpId, appType);
    }

    /**
     * 获取子部门(包括自己)
     *
     * @param accessToken
     * @param id
     * @return
     * @throws ApiException
     */
    @Override
    public List<SysDepartmentDO> getSubDepts(String id, String accessToken, String corpId, String appType) throws ApiException {
        List<SysDepartmentDO> sysDepartments = new ArrayList<>();
        //获取该节点部门的详情
        accessToken = this.getAccessToken(corpId, appType);
        SysDepartmentDO deptDetail = getDeptDetail(accessToken, id, corpId, appType);
        sysDepartments.add(deptDetail);
        //获取当前id下的子部门
        OapiV2DepartmentListsubidResponse.DeptListSubIdResponse response = null;
        int tryCount = 0;
        boolean isTry = true;
        while (tryCount < 3 && isTry){
            try {
                accessToken = this.getAccessToken(corpId, appType);
                response = dingTalkClientService.getChildDeptIds(id, accessToken);
                isTry = false;
            } catch (ApiException e) {
                try {
                    tryCount ++;
                    Thread.sleep(2000);
                } catch (Exception exception) {
                    log.info("getSubDepts重试失败:{}, 部门id:{}", tryCount, id);
                }
            }
        }
        List<Long> deptIdList = response.getDeptIdList();
        //递归获取所有的子节点
        List<String> deptIds = ListOptUtils.longListConvertStringList(deptIdList);
        if (CollectionUtils.isNotEmpty(ListOptUtils.longListConvertStringList(deptIdList))) {
            accessToken = this.getAccessToken(corpId, appType);
            getChildIdList(deptIds, accessToken, sysDepartments, corpId, appType,new LimitDTO(System.currentTimeMillis(),0));
        }
        return sysDepartments;
    }

    private LimitDTO getChildIdList(List<String> childDeptIds, String accessToken, List<SysDepartmentDO> sysDepartments, String corpId, String appType,LimitDTO limitDTO) throws ApiException {

        //当前时间\
        //log.info("limitTime:{}，time:{}",limitDTO.getCount(),limitDTO.getStartTime());
        //如果超过500 且时间少于1分钟 防止发生钉钉限流
        limitDTO = DingTalkLimitingUtil.minuteLimit(limitDTO.getCount(), limitDTO.getStartTime());
        for (String childDeptId : childDeptIds) {
            //根据childDeptId获取部门详情
            SysDepartmentDO deptDetail = getDeptDetail(accessToken, childDeptId, corpId, appType);
            limitDTO.setCount(limitDTO.getCount()+1);
            //log.info("limitTime1:{}",limitDTO.getCount());
            sysDepartments.add(deptDetail);
            //获取此部门的下级部门的id
            OapiV2DepartmentListsubidResponse.DeptListSubIdResponse response = null;
            try {
                response = dingTalkClientService.getChildDeptIds(childDeptId, accessToken);
                limitDTO.setCount(limitDTO.getCount()+1);
                //log.info("limitTime2:{}",limitDTO.getCount());
            } catch (ApiException e) {
                try {
                    Thread.sleep(2000);
                    accessToken = this.getAccessToken(corpId, appType);
                    response = dingTalkClientService.getChildDeptIds(childDeptId, accessToken);
                } catch (Exception exception) {
                    throw new ServiceException(ErrorCodeEnum.GET_DEPT_ERROR);
                }
            }
            List<Long> deptIdList = response.getDeptIdList();
            List<String> deptIds = ListOptUtils.longListConvertStringList(deptIdList);
            limitDTO.setCount(limitDTO.getCount());
            limitDTO.setStartTime(limitDTO.getStartTime());
            if (CollectionUtils.isNotEmpty(deptIdList)) {
                limitDTO = getChildIdList(deptIds, accessToken, sysDepartments, corpId, appType, limitDTO);
            }
        }
        return limitDTO;
    }
    /**
     * 获取部门详情
     *
     * @param accessToken
     * @param id
     * @return
     * @throws ApiException
     */
    @Override
    public SysDepartmentDO getDeptDetail(String accessToken, String id, String corpId, String appType) throws ApiException {

        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/get");
        OapiDepartmentGetRequest request = new OapiDepartmentGetRequest();
        request.setId(id);
        request.setHttpMethod("GET");
        OapiDepartmentGetResponse response = client.execute(request, accessToken);

        if (response.getErrcode() != SyncConfig.DING_NORMAL_CODE) {
            if (response.getErrcode() == SyncConfig.DING_DEPT_NOT_AUTHED) {
                log.warn("getDeptDetail failed, deptId not auth, deptId={}, accessToken={}", id, accessToken);
                return null;
            } else if(response.getErrcode() == SyncConfig.DING_ACCESS_TOKEN_INVALID
                    || response.getErrcode() == SyncConfig.DING_SYS_ERROR
                    || response.getErrcode() == SyncConfig.DING_AUTH_ERROR) {
                // accessToken无效,系统繁忙,鉴权异常，只重试一次
                if(StringUtils.isAnyBlank(corpId, appType)) {
                    log.warn("getDeptDetail failed, accessToken invalid, corpId={},deptId={}, accessToken={}", corpId, id, accessToken);
                    return null;
                }
                accessToken = this.getAccessToken(corpId, appType);
                response = client.execute(request, accessToken);;
            }else if(response.getSubCode().equals(String.valueOf(SyncConfig.API_EXCEED_LIMIT))) {
                // 当前所有钉钉应用调用该接口次数过多，超出了该接口承受的最大qps，请求被暂时限制了，建议错开整点时刻调用该接口
                log.info("部门详情接口超限getDeptDetail部门id : {}", id);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.info("部门详情接口超限InterruptedException:{}",e);
                }
                response = client.execute(request, accessToken);;
            }else {
                throw new ApiException(String.valueOf(response.getErrcode()), response.getErrmsg());
            }
        }

        SysDepartmentDO sysDepartment = new SysDepartmentDO();
        sysDepartment.setId(String.valueOf(response.getId()));
        sysDepartment.setName(response.getName());
        sysDepartment.setParentId(response.getParentid()!=null?String.valueOf(response.getParentid()):null);
        //加上部门的order
        if(response.getOrder() != null){
            sysDepartment.setDepartOrder(response.getOrder().intValue());
        }
        return sysDepartment;
    }

    /**
     * 获取一批部门及子部门信息
     *
     * @param ids
     * @param accessToken
     * @return
     */
    @Override
    public List<SysDepartmentDO> getAllDepts(List<String> ids, String accessToken, String corpId, String appType) throws ApiException {

        List<SysDepartmentDO> departments = Lists.newArrayList();
        log.info("getAllDepts_ids{}",JSONObject.toJSONString(ids));
        for (String id : ids) {
            List<SysDepartmentDO> subDepts = getSubDepts(id, accessToken, corpId, appType);
            if (CollectionUtils.isNotEmpty(subDepts)) {
                departments.addAll(subDepts);
            }
        }

        //去重
        departments = departments.stream().distinct().collect(Collectors.toList());
        Set<String> idSet = departments.stream().map(SysDepartmentDO::getId).collect(Collectors.toSet());

        departments.forEach(d -> {
            if (!idSet.contains(d.getParentId())) {
                d.setParentId(SyncConfig.ROOT_DEPT_ID_STR);
            }
        });

        return departments;
    }

    /**
     * 获取用户详情
     *
     * @param accessToken
     * @param userId
     * @return
     */
    @Override
    public EnterpriseUserRequest getUserDetail(String userId, String accessToken) throws ApiException {
        OapiUserGetRequest request = new OapiUserGetRequest();
        request.setUserid(userId);
        request.setHttpMethod("GET");

        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get");
        OapiUserGetResponse response = client.execute(request, accessToken);

        if (response.getErrcode() != SyncConfig.DING_NORMAL_CODE) {
            if (response.getErrcode() == SyncConfig.DING_USER_NOT_AUTHED) {
                log.warn("getUserDetail failed, userId not auth, userId={}, accessToken={}", userId, accessToken);
                return null;
            } else {
                throw new ApiException(String.valueOf(response.getErrcode()), response.getErrmsg());
            }
        }
        log.info("getUserDetail response : {}", response.getBody().toString());
        EnterpriseUserDO enterpriseUser = new EnterpriseUserDO();
        BeanUtils.copyProperties(response, enterpriseUser);
        enterpriseUser.setId(UUIDUtils.get32UUID());
        enterpriseUser.setUserId(response.getUserid());
        //和企业的leader格式保持一致，为了后续统一处理
        Map<Long, Boolean> leaderMap = Objects.nonNull(response.getIsLeaderInDepts()) ? JSONObject.parseObject(response.getIsLeaderInDepts(), Map.class) : new HashMap<>();
        enterpriseUser.setIsLeaderInDepts(JSONObject.toJSONString(leaderMap.keySet()));
        enterpriseUser.setIsLeader(Boolean.valueOf(response.getIsLeaderInDepts()));
        List<OapiUserGetResponse.Roles> roles = response.getRoles();
        if (CollUtil.isNotEmpty(roles)) {
            List<Long> roleIds = response.getRoles().stream().map(m -> m.getId()).collect(Collectors.toList());
            enterpriseUser.setRoles(JSONObject.toJSONString(roleIds));
        }

        EnterpriseUserRequest enterpriseUserRequest = new EnterpriseUserRequest();
        enterpriseUserRequest.setEnterpriseUserDO(enterpriseUser);
        List<Long> departments = response.getDepartment();
        if (CollectionUtils.isNotEmpty(departments)) {
            enterpriseUserRequest.setDepartment(JSONObject.toJSONString(departments));
        }
        return enterpriseUserRequest;
    }

    @Override
    public List<String> getMainAdmin(String token) {
        OapiUserGetAdminRequest  request = new OapiUserGetAdminRequest();
        request.setHttpMethod("GET");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get_admin");
        OapiUserGetAdminResponse response = null;
        try {
            response = client.execute(request, token);
        } catch (ApiException e) {
            log.error("获取钉钉主管理员失败", e);
        }
        String mainAdminUserId = null;
        // 管理员角色，1表示主管理员，2表示子管理员
        if (response != null ) {
          return ListUtils.emptyIfNull(response.getAdminList())
                  .stream()
                  .filter(f -> f.getSysLevel().equals(1L))
                  .map(OapiUserGetAdminResponse.AdminList::getUserid)
                  .collect(Collectors.toList());

        }
        return new ArrayList<>();
    }


    /**
     * 批量获取用户信息
     *
     * @param userIds
     * @param accessToken
     * @return
     * @throws ApiException
     */
    @Override
    public List<EnterpriseUserRequest> getUsers(List<String> userIds, String accessToken) throws ApiException {
        List<String> mainAdminUserIdList = getMainAdmin(accessToken);
        List<EnterpriseUserRequest> users = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(userIds)) {
            for (String userId : userIds) {
                EnterpriseUserRequest userDetail = getUserDetail(userId, accessToken);
                if (userDetail != null) {
                    // 判断是否是主管理员
                    userDetail.getEnterpriseUserDO()
                            .setMainAdmin(ListUtils.emptyIfNull(mainAdminUserIdList).stream().anyMatch(data->data.equals(userDetail.getEnterpriseUserDO().getUserId())));
                    users.add(userDetail);
                }
            }
        }
        return users;
    }

    /**
     * 查询某个部门下的所有用户
     *
     * @param deptId
     * @param accessToken
     * @return
     * @throws ApiException
     */
    @Override
    public List<EnterpriseUserDO> getDeptUsers(Long deptId, String accessToken) throws ApiException {

        List<EnterpriseUserDO> userList = Lists.newArrayList();

        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/listbypage");
        OapiUserListbypageRequest request = new OapiUserListbypageRequest();
        request.setDepartmentId(deptId);
        long offset = startOffset;
        request.setOffset(offset);
        request.setSize(defautlQuerySize);
        request.setOrder("entry_desc");
        request.setHttpMethod("GET");
        OapiUserListbypageResponse response = new OapiUserListbypageResponse();
        List<String> mainAdminUserIdList= getMainAdmin(accessToken);
        int pageNumber = 1;
        while (offset == startOffset || response.getHasMore()) {
            response = client.execute(request, accessToken);
            if (response.getErrcode() != SyncConfig.DING_NORMAL_CODE) {
                if (response.getErrcode() == SyncConfig.DING_DEPT_NOT_AUTHED) {
                    log.warn("getDeptUsers failed, deptId not auth, deptId={}, accessToken={}", deptId, accessToken);
                    //企业部门不在授权范围
                    break;
                } else {
                    throw new ApiException(String.valueOf(response.getErrcode()), response.getErrmsg());
                }
            }

            response.getUserlist().forEach(u -> {

                EnterpriseUserDO user = new EnterpriseUserDO();
                BeanUtils.copyProperties(u, user);
                String userId = u.getUserid();
                user.setMainAdmin(ListUtils.emptyIfNull(mainAdminUserIdList).stream().anyMatch(data->data.equals(userId)));
                user.setUserId(userId);
                userList.add(user);
            });

            pageNumber++;
            offset = (pageNumber - 1) * defautlQuerySize;
            request.setOffset(offset);
        }

        return userList;
    }

    @Override
    public List<EnterpriseUserRequest> getDeptUserByAsync(String deptId, String accessToken) throws ApiException {
        List<EnterpriseUserRequest> userList = Lists.newArrayList();
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getDeptMember");
        OapiUserGetDeptMemberRequest req = new OapiUserGetDeptMemberRequest();
        req.setDeptId(deptId);
        req.setHttpMethod("GET");
        OapiUserGetDeptMemberResponse rsp = client.execute(req, accessToken);
        List<String> userIds = rsp.getUserIds();
        List<Future<EnterpriseUserRequest>> futures = new ArrayList<>();
        for (String userId: userIds) {
            futures.add(executor.submit(() -> getUserDetail(userId, accessToken)));
        }
        for (Future<EnterpriseUserRequest> future: futures) {
            try {
                userList.add(future.get());
            } catch (Exception e) {
                log.error("获取用户数据失败, ", e);
                throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "获取用户信息失败");
            }
        }
        return userList;
    }

    @Override
    public List<EnterpriseUserDO> getDeptUserList(Long deptId, String accessToken) throws ApiException {
        DingTalkClient  dingTalkClient=new DefaultDingTalkClient("https://oapi.dingtalk.com/user/simplelist");
        OapiUserSimplelistRequest oapiUserSimplelistRequest=new OapiUserSimplelistRequest();
        oapiUserSimplelistRequest.setDepartmentId(deptId);
        oapiUserSimplelistRequest.setHttpMethod("GET");
        OapiUserSimplelistResponse response=new OapiUserSimplelistResponse();
        response = dingTalkClient.execute(oapiUserSimplelistRequest, accessToken);
        if (response.getErrcode() != SyncConfig.DING_NORMAL_CODE) {
            if (response.getErrcode() == SyncConfig.DING_DEPT_NOT_AUTHED) {
                log.warn("getDeptUsers failed, deptId not auth, deptId={}, accessToken={}", deptId, accessToken);
                //企业部门不在授权范围
            } else {
                throw new ApiException(String.valueOf(response.getErrcode()), response.getErrmsg());
            }
        }
        List<EnterpriseUserDO> userList = Lists.newArrayList();

        response.getUserlist().forEach(u->{
            EnterpriseUserDO user = new EnterpriseUserDO();
            BeanUtils.copyProperties(u, user);
            user.setUserId(u.getUserid());
            userList.add(user);
        });
        return userList;
    }

    @Override
    public List<String> getAdminList(String accessToken) throws ApiException {
        DingTalkClient  dingTalkClient=new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get_admin");
        OapiUserGetAdminRequest oapiUserSimplelistRequest=new OapiUserGetAdminRequest();
        oapiUserSimplelistRequest.setHttpMethod("GET");
        OapiUserGetAdminResponse response=dingTalkClient.execute(oapiUserSimplelistRequest, accessToken);
        if (response.getErrcode() != SyncConfig.DING_NORMAL_CODE) {
            if (response.getErrcode() == SyncConfig.DING_DEPT_NOT_AUTHED) {
                log.warn("getAdmin failed,,  accessToken={}",  accessToken);
                //企业部门不在授权范围
            } else {
                throw new ApiException(String.valueOf(response.getErrcode()), response.getErrmsg());
            }
        }
        List<String> userIdList=Lists.newArrayList();
        response.getAdminList().forEach(u->{
            userIdList.add(u.getUserid());
        });
        return userIdList ;
    }

    @Override
    public List<OapiRoleListResponse.OpenRoleGroup> getRoleList(String accessToken) throws ApiException {
        DingTalkClient  dingTalkClient=new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/role/list");
        OapiRoleListRequest oapiRoleListRequest=new OapiRoleListRequest();
        long startOffset = 0L;
        oapiRoleListRequest.setOffset(startOffset);
        oapiRoleListRequest.setSize(200L);
        List<OapiRoleListResponse.OpenRoleGroup> resultList = new ArrayList<>();
        boolean hasMore;
        do {
            OapiRoleListResponse response = dingTalkClient.execute(oapiRoleListRequest, accessToken);
            hasMore = response.getResult().getHasMore();
            if (response.getErrcode() != SyncConfig.DING_NORMAL_CODE) {
                if (response.getErrcode() == SyncConfig.DING_DEPT_NOT_AUTHED) {
                    log.warn("getRole failed,,  accessToken={}",  accessToken);
                    //企业部门不在授权范围
                } else {
                    throw new ApiException(String.valueOf(response.getErrcode()), response.getErrmsg());
                }
            }
            OapiRoleListResponse.PageVo result = response.getResult();
            resultList.addAll(result.getList());
            oapiRoleListRequest.setOffset(++startOffset);
        } while (hasMore);

        return resultList;
    }

    @Override
    public List getUsersByRoleId(String accessToken,Long roleId) throws ApiException {
        DingTalkClient  dingTalkClient=new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/role/simplelist");
        OapiRoleSimplelistRequest  oapiRoleSimplelistRequest =new OapiRoleSimplelistRequest();
        oapiRoleSimplelistRequest.setRoleId(roleId);
        OapiRoleSimplelistResponse response = dingTalkClient.execute(oapiRoleSimplelistRequest, accessToken);
        if (response.getErrcode() != SyncConfig.DING_NORMAL_CODE) {
            if (response.getErrcode() == SyncConfig.DING_DEPT_NOT_AUTHED) {
                log.warn("getUsers failed,,  accessToken={}",  accessToken);
                //企业部门不在授权范围
            } else {
                throw new ApiException(String.valueOf(response.getErrcode()), response.getErrmsg());
            }
        }
        List<OapiRoleSimplelistResponse.OpenEmpSimple> list = response.getResult().getList();
        List<EnterpriseUserDO>  enterpriseUserDos=new ArrayList<>();
        list.forEach(p->{
            EnterpriseUserDO enterpriseUserDO=new EnterpriseUserDO();
            enterpriseUserDO.setUserId(p.getUserid());
            enterpriseUserDO.setName(p.getName());
            enterpriseUserDos.add(enterpriseUserDO);
        });
        return enterpriseUserDos;
    }

    @Override
    public String getManiCorpId(String accessToken, String userId)  {

        if(StringUtils.isBlank(userId)){
            return null;
        }
        String url =  "https://api.dingtalk.com/v1.0/appMarket/personalExperiences";

        Map<String,String> headMap=new HashMap<>();
        headMap.put("x-acs-dingtalk-access-token",accessToken);
        Map<String, String> paramMap=new HashMap<>();
        paramMap.put("userId",userId);
        try {
            CoolHttpClientResult coolHttpClientResult = CoolHttpClient.doGet(url, headMap, paramMap);
            String content = coolHttpClientResult.getContent();
            JSONObject jsonObject = JSONObject.parseObject(content);
            if(jsonObject.getJSONObject("result")!=null){
              return   jsonObject.getJSONObject("result").getString("mainCorpId");
            }
        } catch (Exception e) {
            log.info("请求主应用CorpId错误！",e);
            return null;
        }
        return null;
    }

    @Override
    public OapiCallCalluserResponse callUser(String corpId, String sourceUserId, String targetUserId, String appType) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/call/calluser");
        OapiCallCalluserRequest request = new OapiCallCalluserRequest();
        request.setStaffId(sourceUserId);
        request.setAuthedCorpId(corpId);
        request.setAuthedStaffId(targetUserId);
        try {
            log.info("callUser start:req={}", JSONObject.toJSONString(request));
            OapiCallCalluserResponse response = client.execute(request, getSuiteToken(appType));
            log.info("callUser end:response={}", JSONObject.toJSONString(response));
           return response;
        } catch (ApiException e) {
            log.error("callUser error!", e);

        }
        return null;
    }
    @Override
    public OapiCallGetuserlistResponse getCallUserList(Long offSet,Long size, String appType) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/call/getuserlist");
        OapiCallGetuserlistRequest request = new OapiCallGetuserlistRequest();
        List<String> userIdList=new ArrayList<>();
        request.setOffset(offSet);
        request.setSize(size);
        try {
            log.info("getCallUserList start:req={}", JSONObject.toJSONString(request));
            OapiCallGetuserlistResponse response = client.execute(request, getSuiteToken(appType));

            log.info("getCallUserList end:response={}", JSONObject.toJSONString(response));
            return response;

        } catch (ApiException e) {
            log.error("getCallUserList error!", e);
        }
        return null;
    }

    @Override
    public Boolean setCallUser(List<String> userIdList, String appType) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/call/setuserlist");
        OapiCallSetuserlistRequest request = new OapiCallSetuserlistRequest();
        String userStr = ListUtils.emptyIfNull(userIdList)
                .stream()
                .collect(Collectors.joining(","));
        request.setStaffIdList(userStr);
        try {
            log.info("setCallUser start:req={}", JSONObject.toJSONString(request));
            OapiCallSetuserlistResponse response = client.execute(request, getSuiteToken(appType));
            log.info("setCallUser end:response={}", JSONObject.toJSONString(response));
            if(response.getErrcode()==0){
                return true;
            }
        } catch (ApiException e) {
            log.error("setCallUser error!", e);
        }
        return false;
    }

    /**
     * 根据corpId获取企业开通的企业应用类型
     * @param corpId
     * @return
     */
    private String getAppType(String corpId) {
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        DataSourceHelper.reset();
        String appType = AppTypeEnum.WX_APP.getValue();
/*        if (StringUtils.isNotBlank(corpId)) {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByCorpId(corpId);
            if (enterpriseConfig != null) {
                if (StringUtils.isNotBlank(enterpriseConfig.getAppType())) {
                    appType = enterpriseConfig.getAppType();
                }
            }
        }*/
        if (StringUtils.isNotBlank(dbName)) {
            DataSourceHelper.changeToSpecificDataSource(dbName);
        }
        return appType;
    }


}
