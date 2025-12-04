package com.coolcollege.intelligent.service.qywx;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpHelper;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.sync.vo.AuthScope;
import com.coolcollege.intelligent.common.util.ListOptUtils;
import com.coolcollege.intelligent.common.util.sign.HmacSHATool;
import com.coolcollege.intelligent.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.event.UserNotAuthEvent;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.taobao.api.ApiException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Value("${qywx.token.url}")
    private String chat_token_url;

    @Value("${qywx.insideToken.url}")
    private String chat_inside_token_url;

    @Value("${isv.url}")
    private String isv_url;

    @Value("${qywx.dkfToken.url}")
    private String chat_dkf_token_url;


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EventBus eventBus;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Autowired
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;


    @Value("${spring.profiles.active}")
    private String env;


    /**
     * 获取企业通讯录接口调用授权凭证
     *
     * @param corpId
     * @return
     */
    public String getPyAccessToken(String corpId, String appType) {
        JSONObject jsonObject = restTemplate.getForObject(
                String.format(chat_token_url, corpId, "accessToken", appType), JSONObject.class);
        logger.info("getPyAccessToken accessTokenVo:{}", jsonObject.toString());
        String access_token = jsonObject.getString("access_token");
        return access_token;
    }

    /**
     * 获得企业内部通讯录授权凭证
     * @param corpId
     * @param corpSecret 应用的凭证密钥
     * @param appType
     * @author: xugangkun
     * @return java.lang.String
     * @date: 2021/8/5 17:46
     */
    public String getInsideAccessToken(String corpId, String corpSecret, String appType) {
        JSONObject jsonObject = restTemplate.getForObject(
                String.format(chat_inside_token_url, corpId, corpSecret, appType), JSONObject.class);
        logger.info("getInsideAccessToken accessTokenVo:{}", jsonObject.toString());
        String access_token = jsonObject.getString("access_token");
        return access_token;
    }

    /**
     * 获得代开发token
     * @param corpId
     * @author: xugangkun
     * @return java.lang.String
     * @date: 2021/10/27 11:18
     */
    public String getDkfAccessToken(String corpId) {
        try {
            JSONObject jsonObject = restTemplate.getForObject(
                    String.format(chat_dkf_token_url, corpId), JSONObject.class);
            logger.info("getDkfAccessToken accessTokenVo:{}", jsonObject.toString());
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            logger.error("getDkfAccessToken error", e);
        }
        return null;
    }

    /**
     * 获得企微token，如果存在代开发token,则返回代开发token
     * @param corpId
     * @param appType
     * @author: xugangkun
     * @return java.lang.String
     * @date: 2021/10/27 11:26
     */
    public String getDkfOrQwAccessToken(String corpId, String appType) {
        if(AppTypeEnum.isWxSelfAndPrivateType(appType)){
            return getDkfAccessToken(corpId);
        }
        return getPyAccessToken(corpId, appType);
    }




    /**
     * 查询isv获取企业微信管理员集合
     * @param corpId
     * @return
     */
    public List<String> getWxAdminList(String corpId, String appType) {
        //自建应用没有查询员接口
        if(AppTypeEnum.isWxSelfAndPrivateType(appType)){
            return new ArrayList<>();
        }
        String url = isv_url + "/qywxisv/admin/get?corpId=%s&appType=%s";
        String adminListStr = restTemplate.getForObject(String.format(url, corpId, appType), String.class);
        JSONObject adminList = JSON.parseObject(adminListStr);
        logger.info("getWxAdminList adminList:{}", adminList);
        JSONArray jsonArray = adminList.getJSONArray("admin");
        List<String> adminUserIdList = Lists.newArrayList();

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject admin = (JSONObject) jsonArray.get(i);
            String userid = admin.getString("userid");
            String authType = admin.getString("auth_type");
            if (authType.equals(Constants.ONE_VALUE_STRING)) {
                adminUserIdList.add(userid);
            }
        }
        return adminUserIdList;
    }

    /**
     * 设置企业微信管理员角色权限
     * @param authUsers
     * @param adminList
     * @return
     */
    public List<EnterpriseUserRequest> setAdminRoles(String corpId, List<EnterpriseUserRequest> authUsers, List<String> adminList,String eid,String dbName) {
        logger.info("setAdminRoles deptUsers size:{}, adminList:{}", authUsers.size(), adminList.size());
        if (CollectionUtils.isNotEmpty(adminList) && CollectionUtils.isNotEmpty(authUsers)) {
            DataSourceHelper.changeToSpecificDataSource(dbName);
            Long masterRoleId = sysRoleService.getRoleIdByRoleEnum(eid, Role.MASTER.getRoleEnum());
            DataSourceHelper.reset();
            adminList.forEach(a -> {
                authUsers.forEach(u -> {
                    if (Objects.nonNull(u.getEnterpriseUserDO()) && (corpId + "_" + a).equals(u.getEnterpriseUserDO().getUserId())) {
                        u.getEnterpriseUserDO().setRoles(masterRoleId.toString());
                        u.getEnterpriseUserDO().setIsAdmin(Boolean.TRUE);
                    }
                });
            });
        }
        return authUsers;
    }

    /**
     * 检测企业是否是企业微信自建企业
     * @param corpId
     * @return
     */
    public Boolean checkWxCorpIdFromRedis(String corpId) {
        String corpIds = redisUtilPool.getString("wx_corp_map");
        if(StrUtil.isEmpty(corpIds)){
            return false;
        }
        String[] jsonArray = corpIds.split(",");
        boolean flag = false;
        for (int i = 0; i < jsonArray.length; i++) {
            if (jsonArray[i].equals(corpId)) {
                flag = true;
            }
        }
        return flag;
    }


    /**
     * 获取企业通讯录可见范围
     *
     * @param accessToken
     * @return
     */
    public AuthScope getAuthScope(String accessToken) {
        logger.info("获取企业通讯录可见范围:{}", accessToken);
        AuthScope authScope = new AuthScope();
        String chatUserUrl = "https://qyapi.weixin.qq.com/cgi-bin/agent/list?access_token=%s";

        String agentid = null;
        JSONObject responseDetail;
        int errcode;
        String errmsg;
        try {
            responseDetail = restTemplate.getForObject(String.format(chatUserUrl, accessToken), JSONObject.class);

            logger.info("agent/listresponseDetail:{}", responseDetail.toJSONString());

            errcode = Integer.parseInt(responseDetail.get("errcode").toString());
            errmsg = responseDetail.get("errmsg").toString();
            if (errcode != SyncConfig.DING_NORMAL_CODE) {
                throw new ApiException(String.valueOf(errcode), errmsg);
            }
            JSONArray jsonArray = responseDetail.getJSONArray("agentlist");
            if (jsonArray.size() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                agentid = jsonObject.get("agentid").toString();
            }

            String eInfoUrl = "https://qyapi.weixin.qq.com/cgi-bin/agent/get?access_token=%s&agentid=%s";

            responseDetail = restTemplate.getForObject(String.format(eInfoUrl, accessToken, agentid), JSONObject.class);
            logger.info("agent/get responseDetail:{}", responseDetail.toJSONString());
            Set<String> userIdList = new HashSet<>();
            //先设置可见部门范围为空列表
            authScope.setDeptIdList(new ArrayList<>());
            JSONObject allow_partys = responseDetail.getJSONObject("allow_partys");
            if (allow_partys != null) {
                JSONArray departList = allow_partys.getJSONArray("partyid");
                if (departList != null && departList.size() > 0) {
                    logger.info("getAuthScope departList : {}", departList.size());
                    authScope.setDeptIdList(ListOptUtils.longListConvertStringList(departList.toJavaList(Long.class)));
                }
            }
            JSONObject userInfoJson =  responseDetail.getJSONObject("allow_userinfos");
            if (userInfoJson != null) {
                JSONArray userinfoArray = userInfoJson.getJSONArray("user");
                if (userinfoArray != null && userinfoArray.size() > 0) {
                    for (int i = 0; i < userinfoArray.size(); i++) {
                        JSONObject userinfosJson = userinfoArray.getJSONObject(i);
                        String uid = userinfosJson.getString("userid");
                        userIdList.add(uid);
                    }
                }
            }

            List<String> ls = Arrays.asList(userIdList.toArray(new String[0]));
            authScope.setUserIdList(ls);
            logger.info("userIdList:{}", userIdList.size());

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("getAuthScope error:{}", e.getMessage());
        }
        return authScope;
    }

    /**
     * 获取子部门(包括自己)
     *
     * @param accessToken
     * @param id
     * @return
     * @throws ApiException
     */
    public List<SysDepartmentDO> getSubDepts(String id, String accessToken) {
        List<SysDepartmentDO> sysDepartments = Lists.newArrayList();

        String url = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=%s&id=%s";
        JSONObject responseDetail = restTemplate.getForObject(String.format(url, accessToken, id), JSONObject.class);
        logger.info("getSubDepts responseDetail:{}", responseDetail.toJSONString());
        if (Objects.isNull(responseDetail)) {
            return sysDepartments;
        }
        int errcode = Integer.parseInt(responseDetail.get("errcode").toString());
        String errmsg = responseDetail.get("errmsg").toString();

        if (errcode == SyncConfig.DING_DEPT_NOT_AUTHED || errcode == SyncConfig.DING_DEPT_NOT_AUTHORITY) {
            logger.warn("getSubDepts failed, deptId not auth, deptId={}，errmsg:{}", id, errmsg);
            return sysDepartments;
        }
        JSONArray array = responseDetail.getJSONArray("department");
        logger.info("getSubDepts array size:{}", array.size());
        if (array.size() > 0) {
            for (int j = 0; j < array.size(); j++) {
                JSONObject arrayJson = array.getJSONObject(j);
                SysDepartmentDO sysDepartment = new SysDepartmentDO();
                sysDepartment.setId(arrayJson.getString("id"));
                sysDepartment.setName(arrayJson.getString("name"));
                sysDepartment.setParentId(arrayJson.getString("parentid"));
                sysDepartment.setDepartOrder(arrayJson.getInteger("order"));
                sysDepartments.add(sysDepartment);
            }
        }

        return sysDepartments;
    }

    /**
     * 获取部门详情
     *
     * @param accessToken
     * @param id
     * @return
     * @throws ApiException
     */
    public SysDepartmentDO getDeptDetail(String accessToken, Long id) throws ApiException {
        SysDepartmentDO sysDepartment = new SysDepartmentDO();
        String chatTokenUrl = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=%s&id=%s";
        JSONObject responseDetail = restTemplate.getForObject(String.format(chatTokenUrl, accessToken, id), JSONObject.class);
        logger.info("getDeptDetail responseDetail={}", responseDetail);
        if (Objects.isNull(responseDetail)) {
            return new SysDepartmentDO();
        }
        int errcode = Integer.parseInt(responseDetail.get("errcode").toString());
        String errmsg = responseDetail.get("errmsg").toString();

        if (errcode != SyncConfig.DING_NORMAL_CODE) {
            throw new ApiException(String.valueOf(errcode), errmsg);
        }
        JSONArray array = responseDetail.getJSONArray("department");
        if (array != null && array.size() > 0) {
            JSONObject arrayJson = array.getJSONObject(0);
            sysDepartment = JSON.toJavaObject(arrayJson, SysDepartmentDO.class);
            sysDepartment.setParentId(arrayJson.getString("parentid"));
        }

        logger.info("sysDepartment:{}", sysDepartment.toString());
        return sysDepartment;
    }

    /**
     * 获取一批部门及子部门信息
     *
     * @param ids
     * @param accessToken
     * @return
     */
    public List<SysDepartmentDO> getAllDepts(List<String> ids, String accessToken) {

        List<SysDepartmentDO> departments = Lists.newArrayList();

        for (String id : ids) {
            List<SysDepartmentDO> subDepts = getSubDepts(id, accessToken);
            if (CollectionUtils.isNotEmpty(subDepts)) {
                departments.addAll(subDepts);
            }
        }

        //去重
        departments = departments.stream().distinct().collect(Collectors.toList());
        Set<String> idSet = departments.stream().map(r -> r.getId()).distinct().collect(Collectors.toSet());

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
    @Deprecated
    public EnterpriseUserRequest getUserDetail(String corpId, String userId, String accessToken, boolean flag,String employeeRoleId, String appType) throws ApiException {
        String chatUserUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=%s&userid=%s";
        JSONObject responseDetail = restTemplate.getForObject(String.format(chatUserUrl, accessToken, userId), JSONObject.class);
        logger.info("getUserDetail responseDetail:{}", responseDetail.toJSONString());
        EnterpriseUserRequest enterpriseUserRequest = new EnterpriseUserRequest();
        if (Objects.isNull(responseDetail)) {
            //初始化防止出现null 后续用到引发空指针
            EnterpriseUserDO enterpriseUserDO = new EnterpriseUserDO();
            enterpriseUserDO.setActive(Boolean.FALSE);
            enterpriseUserRequest.setEnterpriseUserDO(enterpriseUserDO);
            return enterpriseUserRequest;
        }
        int errcode = Integer.parseInt(responseDetail.get("errcode").toString());
        String errmsg = responseDetail.get("errmsg").toString();

        if (errcode != SyncConfig.DING_NORMAL_CODE) {
            UserNotAuthEvent userNotAuthEvent = new UserNotAuthEvent();
            userNotAuthEvent.setCorpId(corpId);
            //企微userId需要单独处理
            userId = corpId + "_" + userId;
            userNotAuthEvent.setUserIds(Lists.newArrayList(userId));
            userNotAuthEvent.setAppType(appType);
            eventBus.post(userNotAuthEvent);
            logger.warn("getUserDetail failed, userId not auth, userId={}, accessToken={}", userId, accessToken);
            throw new ApiException(String.valueOf(errcode), errmsg);
        }
        //转换一遍，防止转换对象报错
        responseDetail.put("extattr", responseDetail.getString("extattr"));
        EnterpriseUserDO enterpriseUser = JSON.toJavaObject(responseDetail, EnterpriseUserDO.class);
        String status = responseDetail.get("status").toString();
        //激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业
        if (status.equals(Constants.ONE_VALUE_STRING) || status.equals(Constants.FOUR_VALUE_STRING)) {
            enterpriseUser.setActive(Boolean.TRUE);
        } else {
//            logger.info("用户离职,不同步 id:{},name:{}", enterpriseUser.getUserId(), enterpriseUser.getName());
            enterpriseUser.setActive(Boolean.FALSE);
        }
        if (responseDetail.get("is_leader_in_dept") != null) {
            enterpriseUser.setIsLeaderInDepts(responseDetail.get("is_leader_in_dept").toString());
        }

        enterpriseUser.setCreateTime(new Date());
        if (flag) {
            enterpriseUser.setUnionid(enterpriseUser.getUserId());
        } else {
            if (responseDetail.get("open_userid") != null) {
                enterpriseUser.setUnionid(responseDetail.get("open_userid").toString());
            }
        }
        enterpriseUser.setIsAdmin(false);
        enterpriseUser.setRoles(employeeRoleId);
        // 来源类型：微信同步
        enterpriseUser.setAppType(appType);
        enterpriseUser.setRemark(enterpriseUser.getUserId());
        enterpriseUser.setUserId(corpId + "_" + enterpriseUser.getUserId());
        enterpriseUserRequest.setEnterpriseUserDO(enterpriseUser);
        if (StringUtils.isNotBlank(responseDetail.getString("department"))) {
            enterpriseUserRequest.setDepartment(responseDetail.getString("department"));
            enterpriseUserRequest.setDepartmentLists(JSONObject.parseArray(responseDetail.getString("department"), String.class));
        }
        return enterpriseUserRequest;

    }

    /**
     * 查询某个部门下的所有用户
     *
     * @param deptId
     * @param accessToken
     * @return
     * @throws ApiException
     */
    public List<EnterpriseUserRequest> getDeptUsers(String corpId, String deptId, String accessToken, boolean flag, String appType) throws ApiException {

        List<EnterpriseUserRequest> userList = Lists.newArrayList();
        String deptUserUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=%s&department_id=%s&fetch_child=0";
        JSONObject responseDetail = restTemplate.getForObject(String.format(deptUserUrl, accessToken, Long.valueOf(deptId)), JSONObject.class);
        logger.info("responseDetail getDeptUsers:{}", responseDetail);
        if (StrUtil.isBlank(responseDetail.toString())) {
            return userList;
        }

        int errcode = Integer.parseInt(responseDetail.get("errcode").toString());
        String errmsg = String.valueOf(responseDetail.get("errmsg").toString());
        if (errcode == SyncConfig.DING_DEPT_NOT_AUTHED || errcode == SyncConfig.DING_DEPT_NOT_AUTHORITY) {
            logger.warn("getSubDepts failed, deptId not auth, deptId={}，errmsg:{}", deptId, errmsg);
            return userList;
        }

        if (errcode != SyncConfig.DING_NORMAL_CODE) {
            logger.warn("getUserDetail failed, userId not auth, deptId={}，errmsg:{}", deptId, errmsg);
            throw new ApiException(String.valueOf(errcode), errmsg);
        }

        JSONArray userListArray =  responseDetail.getJSONArray("userlist");
        for (int i = 0; i < userListArray.size(); i++) {
            JSONObject userJsonList = userListArray.getJSONObject(i);
            EnterpriseUserDO enterpriseUser = JSON.toJavaObject(userJsonList, EnterpriseUserDO.class);
            String status = userJsonList.get("status").toString();
            if (status.equals(Constants.ONE_VALUE_STRING) || status.equals(Constants.FOUR_VALUE_STRING)) {
                enterpriseUser.setActive(Boolean.TRUE);
            } else {
                logger.info("用户离职,不同步 id:{},name:{}", enterpriseUser.getUserId(), enterpriseUser.getName());
                continue;
            }
            enterpriseUser.setCreateTime(new Date());
            if (flag) {
                //兼容企业微信自建应用
                enterpriseUser.setUnionid(enterpriseUser.getUserId());
                enterpriseUser.setJobnumber(getJobNumber(userJsonList.getString("extattr")));
            } else {
                //企业微信isv
                if (userJsonList.get("open_userid") != null) {
                    enterpriseUser.setUnionid(userJsonList.get("open_userid").toString());
                }
            }

            enterpriseUser.setIsAdmin(false);
            // 来源类型：微信同步
            enterpriseUser.setAppType(appType);
            enterpriseUser.setRemark(enterpriseUser.getUserId());
            enterpriseUser.setUserId(corpId + "_" + enterpriseUser.getUserId());
            if (userJsonList.get("is_leader_in_dept") != null) {
                enterpriseUser.setIsLeaderInDepts(userJsonList.get("is_leader_in_dept").toString());
            }
            EnterpriseUserRequest enterpriseUserRequest = new EnterpriseUserRequest();
            enterpriseUserRequest.setEnterpriseUserDO(enterpriseUser);
            if (StringUtils.isNotBlank(userJsonList.getString("department"))) {
                enterpriseUserRequest.setDepartment(userJsonList.getString("department"));
            }
            userList.add(enterpriseUserRequest);
        }
        return userList;
    }
    public String getJobNumber(String str) {
        String jobnumber = null;
        if (StrUtil.isBlank(str)) {
            return jobnumber;
        }
        JSONArray jsonArray = JSON.parseObject(str).getJSONArray("attrs");
        for (int i = 0; i < jsonArray.size(); i++) {
            String name = jsonArray.getJSONObject(i).getString("name");
            if (name.equals("工号")) {
                String v = jsonArray.getJSONObject(i).getString("value");
                if (StrUtil.isNotBlank(v)) {
                    jobnumber = v;
                }
                break;
            }
        }
        return jobnumber;
    }

    /**
     * 企业微信通讯录搜索
     * @param corpId
     * @param appType
     * @param queryWord
     * @param queryType 查询类型 1：查询用户，返回用户userid列表 2：查询部门，返回部门id列表。 不填该字段或者填0代表同时查询部门跟用户
     * @param pageNum
     * @param pageSize
     * @author: xugangkun
     * @return Pair<List<String>, List<Long>> key:用户id, value: 部门id
     * @date: 2021/11/26 11:18
     */
    public Pair<List<String>, List<Long>> searchUserOrDeptByName(String corpId, String appType, String queryWord, String queryType, Integer pageNum, Integer pageSize) {
        List<String> userIdList = new ArrayList<>();
        List<Long> deptIdList = new ArrayList<>();
        try {
            String url = isv_url + "/qywxisv/provider-access-tokens/get" + "?appType=" + appType;

//            JSONObject tokenJson = HttpHelper.httpGetCrm(url);
            JSONObject tokenJson = restTemplate.getForObject(url, JSONObject.class);
            logger.info("get provider_access_token:{}", tokenJson);
            String providerAccessToken = tokenJson.getString("provider_access_token");

            //查询获取用户id
            String userUrl = "https://qyapi.weixin.qq.com/cgi-bin/service/contact/search?provider_access_token=" + providerAccessToken;
            HashMap<String, String> userParam = new HashMap<>();
            userParam.put("auth_corpid", corpId);
            userParam.put("query_word", queryWord);
            userParam.put("query_type", queryType);
            userParam.put("offset", String.valueOf(((pageNum - 1) * pageSize)));
            userParam.put("limit", pageSize.toString());
            logger.info("contact/search requestUrl:{}", userUrl);
            JSONObject userInfo = HttpHelper.post(userUrl, userParam);
            logger.info("contact/search response:{}", userInfo);
            if (StringUtils.isEmpty(userInfo.getString("errcode")) || "0".equals(userInfo.getString("errcode"))) {
                JSONObject queryResult = userInfo.getJSONObject("query_result");
                //用户名称查询
                if (Constants.ONE_VALUE_STRING.equals(queryType)) {
                    JSONObject users = queryResult.getJSONObject("user");
                    if (users == null) {
                        return Pair.of(userIdList, deptIdList);
                    }
                    JSONArray userIds = users.getJSONArray("userid");
                    //重新组装userId
                    userIds.forEach(userId ->{
                        userIdList.add(corpId + Constants.UNDERLINE + userId);
                    });
                }
                //部门名称查询
                if (Constants.TWO_VALUE_STRING.equals(queryType)) {
                    JSONObject partyList = queryResult.getJSONObject("party");
                    if (partyList == null) {
                        return Pair.of(userIdList, deptIdList);
                    }
                    JSONArray deptIds = partyList.getJSONArray("department_id");
                    //重新组装userId
                    deptIds.forEach(deptId ->{
                        deptIdList.add(Long.valueOf(deptId.toString()));
                    });
                }
            } else {
                logger.error("searchUserByName errmsg:{}", userInfo.getString("errmsg"));;
            }

        } catch (Exception e) {
            logger.error("searchUserByName error,", e);
        }
        return Pair.of(userIdList, deptIdList);
    }

    /**
     * 获取订单详情
     * https://work.weixin.qq.com/api/doc/15219
     * @param orderid
     * @param appType
     * @return
     */
    public JSONObject getOrderDetail(String orderid, String appType) {

        String url = isv_url + "/qywxisv/suite_access_tokens/get";
        if (StringUtils.isNotBlank(appType)) {
            url = url + "?appType=" + appType;
        }

        JSONObject jsonObject = restTemplate.getForObject(url, JSONObject.class);
        String suiteAccessToken = jsonObject.getString("suite_access_token");
        String orderUrl = "https://qyapi.weixin.qq.com/cgi-bin/service/get_order?suite_access_token=" + suiteAccessToken;
        logger.info("getOrderDetail url:{}", orderUrl);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderid", orderid);
        JSONObject responseResult = null;
        try {
//            responseResult = restTemplate.postForObject(url, params, JSONObject.class);
            responseResult = HttpHelper.post(orderUrl, params);
            logger.info("getOrderDetail responseResult:{}", responseResult.toString());
        } catch (Exception e) {
            logger.error("getOrderDetail error", e);
        }

        return responseResult;

    }

    /**
     * 获取自建应用信息
     * @param corpId
     * @param userId
     * @param flag
     * @param employeeRoleId
     * @param appType
     * @return
     * @throws ApiException
     */
    public EnterpriseUserRequest getSelfUserDetail(String corpId, String userId, boolean flag, String employeeRoleId, String appType) throws ApiException {
        EnterpriseUserDTO enterpriseUserDTO = enterpriseInitConfigApiService.getUserDetailByUserId(corpId, userId, appType);
        EnterpriseUserRequest enterpriseUserRequest = new EnterpriseUserRequest();
        if (Objects.isNull(enterpriseUserDTO)) {
            //初始化防止出现null 后续用到引发空指针
            enterpriseUserRequest.setEnterpriseUserDO(new EnterpriseUserDO());
            return enterpriseUserRequest;
        }
        EnterpriseUserDO enterpriseUser = new EnterpriseUserDO();
        //激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业
        if (enterpriseUserDTO.getActive() != null && enterpriseUserDTO.getActive()) {
            enterpriseUser.setActive(enterpriseUserDTO.getActive());
        } else {
            enterpriseUser.setActive(Boolean.FALSE);
        }
        enterpriseUserRequest.setDepartmentLists(enterpriseUserDTO.getDepartmentLists());
        enterpriseUserRequest.setLeaderInDepts(enterpriseUserDTO.getIsLeaderInDepts());

        if (CollectionUtils.isNotEmpty(enterpriseUserDTO.getIsLeaderInDepts())) {
            enterpriseUser.setIsLeaderInDepts(JSONObject.toJSONString(enterpriseUserDTO.getIsLeaderInDepts()));
        }
        enterpriseUser.setCreateTime(new Date());
        enterpriseUser.setUnionid(enterpriseUserDTO.getUnionid());
        enterpriseUser.setIsAdmin(false);
        enterpriseUser.setRoles(employeeRoleId);
        // 来源类型：微信同步
        enterpriseUser.setAppType(appType);
        enterpriseUser.setRemark(enterpriseUserDTO.getUserId());
        enterpriseUser.setUserId(corpId + "_" + enterpriseUserDTO.getUserId());
        enterpriseUser
                .setName(enterpriseUserDTO.getName());
        enterpriseUser
                .setPosition(enterpriseUserDTO.getPosition());
        enterpriseUser
                .setMobile(enterpriseUserDTO.getMobile());
        //松下jobNumber
        String extattr = enterpriseUserDTO.getExtattr();
        if (StringUtils.isNotBlank(extattr)){
            JSONObject jsonObject = JSONObject.parseObject(extattr);
            //获取attrs List
            List<JSONObject> attrs = jsonObject.getJSONArray("attrs").toJavaList(JSONObject.class);
            if (CollectionUtils.isNotEmpty(attrs)){
                //找用户id
                List<JSONObject> mobile = attrs.stream().filter(c -> "用户ID".equals(c.getString("name"))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(mobile)){
                    enterpriseUser.setMobile(mobile.get(0).getString("value"));
                }

                List<JSONObject> franchiseUniqueCodeList = attrs.stream().filter(c -> "加盟编号".equals(c.getString("name"))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(franchiseUniqueCodeList)){
                    enterpriseUser.setFranchiseUniqueCode(franchiseUniqueCodeList.get(0).getString("value"));
                }
            }
        }
        enterpriseUserRequest.setEnterpriseUserDO(enterpriseUser);
        return enterpriseUserRequest;
    }

    /**
     * 获取微信小程序token
     * @param enterpriseId 企业id
     * @param forceRefresh 强制重新获取token
     * @return accessToken
     */
    public String getAppletToken(String enterpriseId, String appid, String secret, boolean forceRefresh) {
        String cacheKey = MessageFormat.format(RedisConstant.APPLET_ACCESS_TOKEN_KEY, enterpriseId);
        String accessToken = redisUtilPool.getString(cacheKey);
        if (!forceRefresh && StringUtils.isNotBlank(accessToken)) {
            return accessToken;
        }
        String url = MessageFormat.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}", appid, secret);
        JSONObject jsonObject = HttpHelper.get(url);
        logger.info("getAppletToken response:{}", JSONObject.toJSONString(jsonObject));
        if (Objects.nonNull(jsonObject)) {
            accessToken = jsonObject.getString("access_token");
            int expiresIn = jsonObject.getInteger("expires_in");
            expiresIn = expiresIn - 1800 > 0 ? expiresIn - 1800 : expiresIn;
            redisUtilPool.setString(cacheKey, accessToken, expiresIn);
            return accessToken;
        }
        throw new ServiceException(ErrorCodeEnum.LOGIN_AUTH_ERROR);
    }

    /**
     * 获取微信小程序手机号
     * @param mobileCode 手机号授权凭证
     * @param accessToken token
     * @return 手机号
     */
    public String getAppletMobile(String mobileCode, String accessToken) {
        String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + accessToken;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("code", mobileCode);
        JSONObject response = HttpHelper.post(url, paramMap);
        if (Objects.nonNull(response)) {
            Integer errcode = response.getInteger("errcode");
            if (Constants.INDEX_ZERO.equals(errcode)) {
                JSONObject phoneInfo = response.getJSONObject("phone_info");
                if (Objects.nonNull(phoneInfo)) {
                    return phoneInfo.getString("phoneNumber");
                }
            } else if (Integer.valueOf(40001).equals(errcode)) {
                throw new ServiceException(ErrorCodeEnum.APPLET_TOKEN_EXPIRE);
            }
        }
        logger.info("小程序手机号获取失败, response:{}", JSONObject.toJSONString(response));
        throw new ServiceException(ErrorCodeEnum.APPLET_MOBILE_AUTH_ERROR);
    }

    /**
     * 获取小程序用户信息
     * @param code 登录授权凭证
     * @param appid appid
     * @param secret secret
     * @return com.alibaba.fastjson.JSONObject
     */
    public JSONObject getAppletUserInfo(String code, String appid, String secret) {
        String url = MessageFormat.format("https://api.weixin.qq.com/sns/jscode2session?appid={0}&secret={1}&js_code={2}&grant_type=authorization_code", appid, secret, code);
        JSONObject response = HttpHelper.get(url);
        if (Objects.nonNull(response)) {
            if (response.containsKey("errcode") && !Constants.INDEX_ZERO.equals(response.getInteger("errcode"))) {
                logger.info("小程序获取用户信息失败, response:{}", JSONObject.toJSONString(response));
                throw new ServiceException(ErrorCodeEnum.APPLET_USER_INFO_ERROR);
            }
            return response;
        }
        logger.info("小程序获取用户信息失败, response:{}", JSONObject.toJSONString(response));
        throw new ServiceException(ErrorCodeEnum.API_ERROR);
    }

    public String getAskBotLoginToken(String userId, String companyId, String companyName,String appId, String appSecret) {
        String url = "https://test.open.askbot.cn/openplatform-api/sso/enhanced-login";
        /*if (Constants.ONLINE_ENV.equals(env) || Constants.HD_ENV.equals(env)) {

        }*/
        Map<String, Object> paramMap = new HashMap<>();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String signStr = "timestamp=" + timestamp;
        String signature = SecureUtil.hmacSha256(appSecret).digestHex(signStr).toLowerCase();
        paramMap.put("userId", userId);
        paramMap.put("companyId", companyId);
        paramMap.put("companyName", companyName);
        paramMap.put("appId", appId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("signature", signature);
        JSONObject response = HttpHelper.post(url, paramMap);
        logger.info("果然单点登录, response:{}", JSONObject.toJSONString(response));
        if (Objects.nonNull(response) && response.getBoolean("success")) {
            return response.getString("token");
        }
        throw new ServiceException(ErrorCodeEnum.ASKBOT_TOKEN_ERROR);
    }

}
