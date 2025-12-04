package com.coolcollege.intelligent.service.coolcollege.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.ak.AkEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.ak.TruelyAkEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataOperation;
import com.coolcollege.intelligent.common.enums.coolcollege.CoolCollegeTodoListType;
import com.coolcollege.intelligent.common.enums.yunda.YunDaQuestionCodeEnum;
import com.coolcollege.intelligent.common.enums.yunda.YundaEnterpriseEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.EncryptUtil;
import com.coolcollege.intelligent.common.util.PbeEncryptUtil;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.bosspackage.dao.EnterprisePackageDao;
import com.coolcollege.intelligent.dao.bosspackage.dao.EnterprisePackageModuleMappingDao;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDepartmentDao;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.bosspackage.EnterprisePackageDO;
import com.coolcollege.intelligent.model.coolcollege.*;
import com.coolcollege.intelligent.model.enterprise.*;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.yunda.YunDaMsgDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.dingtalk.ServiceWindowService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.jms.JmsSendMessageSyncService;
import com.coolcollege.intelligent.service.jms.constans.MqQueueNameEnum;
import com.coolcollege.intelligent.service.jms.dto.AppExtraParamDTO;
import com.coolcollege.intelligent.service.jms.dto.AppPushMsgDTO;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.wechat.WechatService;
import com.coolcollege.intelligent.service.yunda.YundaService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: xuanfeng
 * @date: 2022-03-31 10:05
 */
@Service
@Slf4j
public class CoolCollegeIntegrationApiServiceImpl implements CoolCollegeIntegrationApiService {

    @Value("${coolcollege.third.oa.get.token.url}")
    private String thirdOaGetTokenUrl;

    @Value("${coolcollege.third.oa.send.dept.url}")
    private String thirdOaSendDeptUrl;

    @Value("${coolcollege.third.oa.send.position.url}")
    private String thirdOaSendPositionUrl;

    @Value("${coolcollege.third.oa.send.user.url}")
    private String thirdOaSendUserUrl;

    @Value("${coolcollege.third.oa.open.coolcollege.url}")
    private String thirdOaOpenCoolCollegeUrl;

    @Value("${coolcollege.third.oa.open.result.secret.url}")
    private String thirdOaGetOpenResultUrl;

    @Value("${coolcollege.get.todo.list.url}")
    private String getTodoListUrl;

    @Value("${coolcollege.qywx.task.notice.url2}")
    private String qywxUrlCoolCollege;

    @Value("${qywx.config.app.suiteId2}")
    private String suiteId2;

    @Value("${qywx.task.notice.oauth.url}")
    private String oauthUrl;

    @Value("${platform.api.config.digitalStoreLoginApi}")
    private String loginUrl;

    @Value("${feishu.notice.url}")
    private String feiShuNoticeUrl;

    /**
     * 门店通工作通知跳转url
     */
    @Value("${coolcollege.dingtalk.oneparty.notice.url2}")
    private String onepartyNoticeUrlCoolCollege;

    @Value("${coolcollege.dingtalk.oneparty.notice.pcUrl}")
    private String onepartyNoticePcLinkCoolCollege;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RegionDao regionDao;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Resource
    private EnterpriseService enterpriseService;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Autowired
    private UserRegionMappingDAO userRegionMappingDAO;

    @Autowired
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Autowired
    private EnterpriseUserDepartmentDao enterpriseUserDepartmentDao;

    @Autowired
    private SimpleMessageService simpleMessageService;

    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private JmsSendMessageSyncService jmsSendMessageSyncService;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Autowired
    private EnterpriseSettingService enterpriseSettingService;

    @Autowired
    private EnterprisePackageDao enterprisePackageDao;

    @Autowired
    private EnterprisePackageModuleMappingDao enterprisePackageModuleMappingDao;

    @Autowired
    protected SysRoleService sysRoleService;

    @Autowired
    protected YundaService yundaService;

    @Autowired
    protected ServiceWindowService serviceWindowService;
    @Resource
    private WechatService wechatService;


    private final static String SEPARATOR = "|";

    private final static String SEPARATOR_QUESTION_MARK = "?";

    private final static String SEPARATOR_DOUBLE_SLASH = "//";
    /**
     * 韵达推送固定主管理员 userId
     */
    private static final List<String> YUNDA_MAIN_ADMIN_USER_ID = Arrays.asList("90158714","90035320","90195154","90195819","03363012511244693","0126386262691307","011220560520-2044284518");

    /**
     * 不依赖酷店掌管理员的业培一体 企业id
     */
    private static final List<String> NOT_DEPEND_STORE_ADMIN_LIST = Arrays.asList("6eba983038b64567b7715495da5dd7f4");

    /**
     * 韵达初始化的门店通角色id
     */
    private static final List<String> YUNDA_INIT_ROLE_ID = Arrays.asList("2410","2411","2412","2413","2414");

    private static final String YUNDA_MSG_SECRET = "hi1Im1MITpLa";

    @Override
    public String getLoginCoolCollegeTicket(String userId, String storeEnterpriseId) {
        if (StringUtils.isBlank(storeEnterpriseId)) {
            throw new ServiceException(ErrorCodeEnum.COOL_STORE_ENTERPRISE_ID_NOT_NULL);
        }
        //查询门店企业获取企业相关信息
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(storeEnterpriseId);
        EnterpriseSettingDO setting = enterpriseSettingService.selectByEnterpriseId(storeEnterpriseId);
        //校验推送的企业是否已经开通了酷学院
        if (Objects.isNull(enterpriseConfig) || Objects.isNull(setting)) {
            throw new ServiceException(ErrorCodeEnum.COOL_STORE_OPEN_TRAINING);
        }
        if (StringUtils.isBlank(enterpriseConfig.getCoolCollegeEnterpriseId()) || StringUtils.isBlank(enterpriseConfig.getCoolCollegeSecret())) {
            throw new ServiceException(ErrorCodeEnum.COOL_STORE_OPEN_TRAINING);
        }
        Boolean isCoolCollege = AppTypeEnum.isCoolCollege(enterpriseConfig.getAppType());
        //是否是企微的酷店掌
        boolean isQw = isQwType(enterpriseConfig.getAppType());
        //如果是AI用户，返回access_token
        if (Constants.AI_USER_ID.equals(userId)){
            return enterpriseConfig.getCoolCollegeEnterpriseId();
        }
        if (isQw) {
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserId(storeEnterpriseId, userId);
            if (Objects.nonNull(enterpriseUserDO)) {
                userId = enterpriseUserDO.getId();
            }
        }
        if (StringUtils.isBlank(userId)) {
            throw new ServiceException(ErrorCodeEnum.COOL_STORE_USER_ID_ERROR);
        }
        if (!isCoolCollege && setting.getAccessCoolCollege()) {
            //签名
            StringBuffer sb = new StringBuffer();
            //用户唯一标识id
            sb.append("userId=").append(userId).append("&");
            //企业唯一标识enterpriseId
            sb.append("enterpriseId=").append(enterpriseConfig.getCoolCollegeEnterpriseId());
            //加密
            String ticket = EncryptUtil.aesEncryp(sb.toString(), EncryptUtil.oaMd5());
            log.info("login ticket:" + ticket);
            return ticket;
        } else {
            throw new ServiceException(ErrorCodeEnum.COOL_STORE_TYPE_ERROR);
        }
    }


    /**
     * 酷学院超等 返回的access_token 暂时不需要
     * @param storeEnterpriseId
     * @return
     */
    public String adminLogin(String storeEnterpriseId){
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(storeEnterpriseId);
        EnterpriseDO enterpriseDO = enterpriseService.selectById(storeEnterpriseId);
        if (Objects.isNull(enterpriseConfig) || Objects.isNull(enterpriseDO)) {
            return null;
        }
        AdminLoginDTO adminLoginDTO = new AdminLoginDTO();
        adminLoginDTO.setLoginType("admin_login");
        adminLoginDTO.setEnterpriseId(enterpriseConfig.getCoolCollegeEnterpriseId());
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(loginUrl, new HttpEntity<>(JSONObject.toJSONString(adminLoginDTO), null), JSONObject.class);
        log.info("adminLogin:{}", JSONObject.toJSONString(responseEntity));
        JSONObject response = responseEntity.getBody();
        if (Objects.isNull(response) || !"200000".equals(response.getString("code"))) {
            throw new ServiceException(ErrorCodeEnum.COOL_COLLEGE_ADMIN_LOGIN_FAIL);
        }
        if (MapUtils.isEmpty((LinkedHashMap) response.get("data"))){
            throw new ServiceException(ErrorCodeEnum.COOL_COLLEGE_ADMIN_LOGIN_FAIL);
        }
        String access_token = (String) ((LinkedHashMap) response.get("data")).get("action_token");
        return access_token;
    }




    @Override
    public void openCoolCollegeAuth(String storeEnterpriseId) {
        log.info("openCoolCollegeAuth, enterpriseId is {}", storeEnterpriseId);
        if (StringUtils.isBlank(storeEnterpriseId)) {
            throw new ServiceException(ErrorCodeEnum.COOL_STORE_ENTERPRISE_ID_NOT_NULL);
        }
        //查询门店企业获取企业相关信息
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(storeEnterpriseId);
        EnterpriseDO enterpriseDO = enterpriseService.selectById(storeEnterpriseId);
        if (Objects.isNull(enterpriseConfig) || Objects.isNull(enterpriseDO)) {
            return;
        }
        //封装开通参数
        OpenCoolCollegeDTO openCoolCollegeDTO = new OpenCoolCollegeDTO();
        openCoolCollegeDTO.setCorp_id(enterpriseConfig.getDingCorpId() + SEPARATOR + enterpriseConfig.getAppType());
        openCoolCollegeDTO.setCorp_name(enterpriseDO.getName());
        openCoolCollegeDTO.setSource(SyncConfig.OPEN_SOURCE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("access-token", getOpenToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        log.info("open request is {}", JSONObject.toJSONString(openCoolCollegeDTO));
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(thirdOaOpenCoolCollegeUrl, new HttpEntity<>(JSONObject.toJSONString(openCoolCollegeDTO), httpHeaders), JSONObject.class);
        log.info("open info resp {}", JSONObject.toJSONString(responseEntity));
        JSONObject response = responseEntity.getBody();
        if (Objects.nonNull(response) && (SyncConfig.STATUS_200.equals(response.getString("code")) || SyncConfig.STATUS_870007.equals(response.getString("code")))) {
            //开通完成，异步获取开通的返回结果，用mq做20秒延迟读取，读取不到，继续延迟两分钟
            GetCoolCollegeOpenResultDTO resultDTO = new GetCoolCollegeOpenResultDTO(enterpriseConfig.getAppType(), enterpriseConfig.getDingCorpId(), storeEnterpriseId,null);
            simpleMessageService.send(JSONObject.toJSONString(resultDTO), RocketMqTagEnum.GET_OPEN_COOL_COLLEGE_RESULT, System.currentTimeMillis() + 60 * 1000);
        } else {
            log.info("酷学院企业开通异常 {}", JSONObject.toJSONString(response));
        }

    }

    /**
     * 获取开通结果
     * @param cropId
     * @param appType
     */
    @Override
    public void getOpenCoolCollegeResult(String cropId, String appType, String storeEnterpriseId) {
        if (StringUtils.isBlank(cropId) || StringUtils.isBlank(appType) || StringUtils.isBlank(storeEnterpriseId)) {
            return;
        }
        //构造请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("access-token", getOpenToken());
        ResponseEntity<JSONObject> exchange = restTemplate.exchange(String.format(thirdOaGetOpenResultUrl, cropId + SEPARATOR + appType),
                HttpMethod.GET, new HttpEntity<>(httpHeaders), JSONObject.class);
        log.info("getOpenCoolCollegeResult  corpId:{}, appType:{}, resp {}", cropId, appType, JSONObject.toJSONString(exchange));
        //获取返回的数据
        JSONObject response = exchange.getBody();
        if (Objects.isNull(response)) {
            //未获取到开通的接口，重新获取，异步获取开通的返回结果，未获取到开通完成后的结果，用mq做2分钟延迟读取，读取不到，继续延迟两分钟
            GetCoolCollegeOpenResultDTO resultDTO = new GetCoolCollegeOpenResultDTO(appType, cropId, storeEnterpriseId,null);
            simpleMessageService.send(JSONObject.toJSONString(resultDTO), RocketMqTagEnum.GET_OPEN_COOL_COLLEGE_RESULT, System.currentTimeMillis() + 2 * 60 * 1000);
            return;
        }
        if (!SyncConfig.STATUS_200.equals(response.getString("code"))) {
            if (SyncConfig.STATUS_870014.equals(response.getString("code"))) {
                //开通中
                //异步获取开通的返回结果，未获取到开通完成后的结果，用mq做2分钟延迟读取，读取不到，继续延迟两分钟
                GetCoolCollegeOpenResultDTO resultDTO = new GetCoolCollegeOpenResultDTO(appType, cropId, storeEnterpriseId,null);
                simpleMessageService.send(JSONObject.toJSONString(resultDTO), RocketMqTagEnum.GET_OPEN_COOL_COLLEGE_RESULT, System.currentTimeMillis() + 2 * 60 * 1000);
            }
            if (SyncConfig.STATUS_870013.equals(response.getString("code"))) {
                //企业未开通，则调用开通接口
                log.info("酷学院企业未开通:corpId:{}, appType:{}", cropId, appType);
                openCoolCollegeAuth(storeEnterpriseId);
            }
            if (SyncConfig.STATUS_870012.equals(response.getString("code"))) {
                //暂时不做处理
                log.info("请求酷学院鉴权失败");
            }
            if (SyncConfig.STATUS_870001.equals(response.getString("code"))) {
                //暂时不做处理
                log.info("请求酷学院参数错误");
            }
            return;
        }
        JSONObject data = response.getJSONObject("data");
        if (Objects.nonNull(data)) {
            enterpriseConfigMapper.updateCoolCollegeInfo(cropId, appType, data.getString("enterprise_id"), data.getString("secret"));
            //开通完成，异步推送数据开始，全量推送
            executor.execute(() -> {
                //推送部门
                sendDepartmentsToCoolCollege(storeEnterpriseId, Collections.emptyList(),null);
                //推送职位
                sendPositionsToCoolCollege(storeEnterpriseId, Collections.emptyList());
                //推送人员 延迟推送 延迟时间2分钟
                GetCoolCollegeOpenResultDTO resultDTO = new GetCoolCollegeOpenResultDTO(appType, cropId, storeEnterpriseId,null);
                simpleMessageService.send(JSONObject.toJSONString(resultDTO), RocketMqTagEnum.COLLEGE_SYNC_USER_DELAY, System.currentTimeMillis() + 2 * 60 * 1000);
            });
        }
    }

    /**
     * 获取开通以及开通结果需要的token
     * @return
     */
    public String getOpenToken() {
        JSONObject response = restTemplate.getForObject(String.format(thirdOaGetTokenUrl, SyncConfig.OPEN_ENTERPRISE_ID, SyncConfig.SECRET), JSONObject.class);
        log.info("getOpenToken  resp {}", JSONObject.toJSONString(response));
        if (Objects.isNull(response) || !SyncConfig.STATUS_200.equals(response.getString("code"))) {
            log.info("getOpenToken resp is {}", JSONObject.toJSONString(response));
            throw new ServiceException(ErrorCodeEnum.COOL_COLLEGE_GET_OPEN_TOKEN_ERROR);
        }
        return response.getString("data");
    }

    @Override
    public String getEnterpriseToken(String storeEnterpriseId) {
        String thirdoaTokenKey = "thirdoaTokenKey_" + storeEnterpriseId;
        String accessToken = redisUtilPool.getString(thirdoaTokenKey);
        if(StringUtils.isNotBlank(accessToken)){
            return accessToken;
        }
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(storeEnterpriseId);
        JSONObject response = restTemplate.getForObject(String.format(thirdOaGetTokenUrl, enterpriseConfig.getCoolCollegeEnterpriseId(), enterpriseConfig.getCoolCollegeSecret()), JSONObject.class);
        log.info("getEnterpriseToken  resp {}", JSONObject.toJSONString(response));
        if (Objects.isNull(response) || !SyncConfig.STATUS_200.equals(response.getString("code"))) {
            throw new ServiceException(ErrorCodeEnum.COOL_COLLEGE_GET_ENTERPRISE_TOKEN_ERROR);
        }
        accessToken =  response.getString("data");
        if(StringUtils.isNotBlank(accessToken)){
            redisUtilPool.setString(thirdoaTokenKey, accessToken, 3 * 60 * 60);
        }
        return accessToken;
    }

    @Override
    public void sendDepartmentsToCoolCollege(String storeEnterpriseId, List<Long> regionIds,Long regionId) {
        if (StringUtils.isBlank(storeEnterpriseId)) {
            throw new ServiceException(ErrorCodeEnum.COOL_STORE_ENTERPRISE_ID_NOT_NULL);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(storeEnterpriseId);
        //获取企业的token，推送数据携带，鉴权
        String enterpriseToken = getEnterpriseToken(storeEnterpriseId);
        //切回企业库
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        if (CollectionUtils.isNotEmpty(regionIds)) {
            //推送指定的组织架构节点数据
            List<RegionDO> regions = regionDao.getAllRegionByRegionIds(storeEnterpriseId, regionIds);
            //构造推送的数据，并且推送数据
            buildDepartmentParamAndSend(enterpriseToken, regions);
        } else {
            //采用全量数据推送，分页获取
            int pageSize = 500;
            for (int pageNum = 1; ; pageNum++) {
                PageHelper.startPage(pageNum, pageSize, false);
                List<RegionDO> regionsByEid = regionDao.getRegionsByEid(storeEnterpriseId,regionId);
                if (CollectionUtils.isEmpty(regionsByEid)) {
                    break;
                }
                //构造推送的数据，并且推送数据
                buildDepartmentParamAndSend(enterpriseToken, regionsByEid);
            }
        }
    }

    /**
     * 构造推送的数据，并且推送数据
     * @param enterpriseToken
     * @param regions
     */
    private void buildDepartmentParamAndSend(String enterpriseToken, List<RegionDO> regions) {
        if (CollectionUtils.isEmpty(regions)) {
            return;
        }
        List<CoolCollegeDepartmentDTO> departmentDTOS = new ArrayList<>();
        for (RegionDO region : regions) {
            if (SyncConfig.DELETE_DEPT_ID.equals(String.valueOf(region.getId()))) {
                continue;
            }
            CoolCollegeDepartmentDTO coolCollegeDepartmentDTO = new CoolCollegeDepartmentDTO();
            coolCollegeDepartmentDTO.setCreate_time(region.getCreateTime());
            coolCollegeDepartmentDTO.setId(String.valueOf(region.getId()));
            coolCollegeDepartmentDTO.setIs_delete(region.getDeleted() ? 1 : 0);
            coolCollegeDepartmentDTO.setName(region.getName());
            coolCollegeDepartmentDTO.setParent_id(SyncConfig.ROOT_DEPT_ID_STR.equals(region.getParentId()) ? null : region.getParentId());
            departmentDTOS.add(coolCollegeDepartmentDTO);
        }
        SendCoolCollegeRequestDTO sendCoolCollegeRequestDTO = new SendCoolCollegeRequestDTO();
        sendCoolCollegeRequestDTO.setData_count(departmentDTOS.size());
        sendCoolCollegeRequestDTO.setData(departmentDTOS);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("access-token", enterpriseToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        log.info("send dept info request {}", JSONObject.toJSONString(sendCoolCollegeRequestDTO));
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(thirdOaSendDeptUrl, new HttpEntity<>(JSONObject.toJSONString(sendCoolCollegeRequestDTO), httpHeaders), JSONObject.class);
        log.info("send dept info resp {}", JSONObject.toJSONString(responseEntity));
    }

    @Override
    public void sendPositionsToCoolCollege(String storeEnterpriseId, List<Long> positionIds) {
        if (StringUtils.isBlank(storeEnterpriseId)) {
            throw new ServiceException(ErrorCodeEnum.COOL_STORE_ENTERPRISE_ID_NOT_NULL);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(storeEnterpriseId);
        //获取企业的token，推送数据携带，鉴权
        String enterpriseToken = getEnterpriseToken(storeEnterpriseId);
        //切回企业库
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        if (CollectionUtils.isNotEmpty(positionIds)) {
            List<SysRoleDO> roleByRoleIds = sysRoleMapper.getRoleByRoleIds(storeEnterpriseId, positionIds);
            //构造推送的数据，并且推送数据
            buildPositionParamAndSend(enterpriseToken, roleByRoleIds, Boolean.FALSE);
        } else {
            int pageSize = 500;
            for (int pageNum = 1; ; pageNum++) {
                PageHelper.startPage(pageNum, pageSize, false);
                List<SysRoleDO> roleByEid = sysRoleMapper.getRoleByEid(storeEnterpriseId);
                if (CollectionUtils.isEmpty(roleByEid)) {
                    break;
                }
                //构造推送的数据，并且推送数据
                buildPositionParamAndSend(enterpriseToken, roleByEid, Boolean.FALSE);
            }
        }
    }

    @Override
    public void sendDelPositionsToCoolCollege(String storeEnterpriseId, List<SysRoleDO> sysRoleDOS) {
        if (StringUtils.isBlank(storeEnterpriseId)) {
            throw new ServiceException(ErrorCodeEnum.COOL_STORE_ENTERPRISE_ID_NOT_NULL);
        }
        DataSourceHelper.reset();
        //获取企业的token，推送数据携带，鉴权
        String enterpriseToken = getEnterpriseToken(storeEnterpriseId);
        buildPositionParamAndSend(enterpriseToken, sysRoleDOS, Boolean.TRUE);
    }

    /**
     * 构造推送的数据，并且推送数据
     * @param enterpriseToken
     * @param roleByRoleIds
     */
    private void buildPositionParamAndSend(String enterpriseToken, List<SysRoleDO> roleByRoleIds, Boolean isDel) {
        if (CollectionUtils.isEmpty(roleByRoleIds)) {
            return;
        }
        List<CoolCollegePositionDTO> positionDTOS = new ArrayList<>();
        for (SysRoleDO role : roleByRoleIds) {
            CoolCollegePositionDTO positionDTO = new CoolCollegePositionDTO();
            if(role.getCreateTime() != null){
                positionDTO.setCreate_time(role.getCreateTime().getTime());
            }else if(role.getUpdateTime() != null){
                //创建时间为null,取修改时间
                positionDTO.setCreate_time(role.getUpdateTime().getTime());
            }
            positionDTO.setPost_id(String.valueOf(role.getId()));
            positionDTO.setIs_delete(isDel ? 1 : 0);
            positionDTO.setPost_name(role.getRoleName());
            positionDTOS.add(positionDTO);
        }
        SendCoolCollegeRequestDTO sendCoolCollegeRequestDTO = new SendCoolCollegeRequestDTO();
        sendCoolCollegeRequestDTO.setData(positionDTOS);
        sendCoolCollegeRequestDTO.setData_count(positionDTOS.size());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("access-token", enterpriseToken);
        log.info("send position info request {}", JSONObject.toJSONString(sendCoolCollegeRequestDTO));
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(thirdOaSendPositionUrl, new HttpEntity<>(JSONObject.toJSONString(sendCoolCollegeRequestDTO), httpHeaders), JSONObject.class);
        log.info("send position info resp {}", JSONObject.toJSONString(responseEntity));
    }

    @Override
    public void sendUsersToCoolCollege(String storeEnterpriseId, List<String> userIds,Long regionId) {
        if (StringUtils.isBlank(storeEnterpriseId)) {
            throw new ServiceException(ErrorCodeEnum.COOL_STORE_ENTERPRISE_ID_NOT_NULL);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(storeEnterpriseId);
        //获取企业的token，推送数据携带，鉴权
        String enterpriseToken = getEnterpriseToken(storeEnterpriseId);
        //切回企业库
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        if (CollectionUtils.isNotEmpty(userIds)) {
            //获取用户信息
            List<EnterpriseUserDO> enterpriseUserDOS = enterpriseUserDao.listByUserIdIgnoreActive(storeEnterpriseId, userIds);
            //获取用户和职位的关系
            List<EnterpriseUserRole> enterpriseUserRoles = enterpriseUserRoleMapper.selectByUserIdList(storeEnterpriseId, userIds);
            Map<String, List<String>> positionIds = ListUtils.emptyIfNull(enterpriseUserRoles)
                    .stream()
                    .collect(Collectors.groupingBy(EnterpriseUserRole::getUserId, Collectors.mapping(EnterpriseUserRole::getRoleId, Collectors.toList())));
            //获取用户和区域的关系
            List<UserRegionMappingDO> userRegionMappingDOS = userRegionMappingDAO.listUserRegionMappingByUserId(storeEnterpriseId, userIds);
            Map<String, List<String>> regionIds = ListUtils.emptyIfNull(userRegionMappingDOS)
                    .stream()
                    .collect(Collectors.groupingBy(UserRegionMappingDO::getUserId, Collectors.mapping(UserRegionMappingDO::getRegionId, Collectors.toList())));
            // 获取用户是领导的部门
            List<EnterpriseUserDepartmentDO> deptAuthList = enterpriseUserDepartmentDao.selectDeptAuthByUserIds(storeEnterpriseId, userIds);
            List<String> authDepartmentIdList = ListUtils.emptyIfNull(deptAuthList).stream()
                    .map(EnterpriseUserDepartmentDO::getDepartmentId)
                    .collect(Collectors.toList());
            Map<String, String> regionMap = Maps.newHashMap();
            if(!AppTypeEnum.ONE_PARTY_APP.getValue().equals(enterpriseConfigDO.getAppType())) {
                List<RegionDO> regionDOS = regionDao.getRegionBySynDingDeptIds(storeEnterpriseId, authDepartmentIdList);
                //构建同步部门id和区域id 映射关系
                regionMap = ListUtils.emptyIfNull(regionDOS).stream().collect(Collectors.toMap(k->k.getSynDingDeptId(), v->String.valueOf(v.getId()), (k1, k2) -> k1));
            }else {
                regionMap = regionDao.getRegionIdByThirdDeptIds(storeEnterpriseId, authDepartmentIdList);
            }
            Map<String, String> finalRegionMap = regionMap;
            Map<String, List<String>> chargeRegionIds = ListUtils.emptyIfNull(deptAuthList)
                    .stream()
                    .collect(Collectors.groupingBy(EnterpriseUserDepartmentDO::getUserId, Collectors.mapping(s -> finalRegionMap.get(s.getDepartmentId()), Collectors.toList())));
            //构造推送的数据，并且推送数据
            buildUserParamAndSend(storeEnterpriseId, enterpriseToken, enterpriseUserDOS, positionIds, regionIds, chargeRegionIds, enterpriseConfigDO.getAppType());
        } else {
            int pageSize = 200;
            for (int pageNum = 1; ; pageNum++) {
                PageHelper.startPage(pageNum, pageSize, false);
                List<EnterpriseUserDO> enterpriseUserDOS = enterpriseUserMapper.selectUserByEid(storeEnterpriseId,regionId);
                if (CollectionUtils.isEmpty(enterpriseUserDOS)) {
                    break;
                }
                List<String> enterpriseUserIds = enterpriseUserDOS.stream()
                        .map(EnterpriseUserDO::getUserId)
                        .collect(Collectors.toList());
                //获取用户和区域的关系
                List<UserRegionMappingDO> userRegionMappingDOS = userRegionMappingDAO.listUserRegionMappingByUserId(storeEnterpriseId, enterpriseUserIds);
                Map<String, List<String>> regionIds = ListUtils.emptyIfNull(userRegionMappingDOS)
                        .stream()
                        .collect(Collectors.groupingBy(UserRegionMappingDO::getUserId, Collectors.mapping(UserRegionMappingDO::getRegionId, Collectors.toList())));
                //获取用户和职位的关系
                List<EnterpriseUserRole> enterpriseUserRoles = enterpriseUserRoleMapper.selectByUserIdList(storeEnterpriseId, enterpriseUserIds);
                Map<String, List<String>> positionIds = ListUtils.emptyIfNull(enterpriseUserRoles)
                        .stream()
                        .collect(Collectors.groupingBy(EnterpriseUserRole::getUserId, Collectors.mapping(EnterpriseUserRole::getRoleId, Collectors.toList())));
                // 获取用户是领导的部门
                List<EnterpriseUserDepartmentDO> deptAuthList = enterpriseUserDepartmentDao.selectDeptAuthByUserIds(storeEnterpriseId, enterpriseUserIds);
                List<String> authDepartmentIdList = ListUtils.emptyIfNull(deptAuthList).stream()
                        .map(EnterpriseUserDepartmentDO::getDepartmentId)
                        .collect(Collectors.toList());
                Map<String, String> regionMap = Maps.newHashMap();
                if(!AppTypeEnum.ONE_PARTY_APP.getValue().equals(enterpriseConfigDO.getAppType())) {
                    List<RegionDO> regionDOS = regionDao.getRegionBySynDingDeptIds(storeEnterpriseId, authDepartmentIdList);
                    //构建同步部门id和区域id 映射关系
                    regionMap = ListUtils.emptyIfNull(regionDOS).stream().collect(Collectors.toMap(k->k.getSynDingDeptId(), v->String.valueOf(v.getId()), (k1, k2) -> k1));
                }else {
                    regionMap = regionDao.getRegionIdByThirdDeptIds(storeEnterpriseId, authDepartmentIdList);
                }
                Map<String, String> finalRegionMap = regionMap;
                Map<String, List<String>> chargeRegionIds = ListUtils.emptyIfNull(deptAuthList)
                        .stream()
                        .collect(Collectors.groupingBy(EnterpriseUserDepartmentDO::getUserId, Collectors.mapping(s -> finalRegionMap.get(s.getDepartmentId()), Collectors.toList())));
                //构造推送的数据，并且推送数据
                buildUserParamAndSend(storeEnterpriseId, enterpriseToken, enterpriseUserDOS, positionIds, regionIds, chargeRegionIds, enterpriseConfigDO.getAppType());
            }
        }
    }

    @Override
    public void sendCoolCollegeMsg(CoolCollegeMsgDTO dto, String crop) {
        log.info("sendCoolCollegeMsg param, cropId: {} CoolCollegeMsgDTO: {}", crop, JSONObject.toJSONString(dto));
        String zxCropId = redisUtilPool.getString("zx_msg_forward_crop_id");
        if (crop.equals(zxCropId)) {
            log.info("正新消息转发");
            sendZxCoolCollegeMsgTo(dto, crop);
            return;
        }
        String lockKey = "sendCoolCollegeMsg:" + dto.getMsgId();
        boolean lock = redisUtilPool.setNxExpire(lockKey, dto.getMsgId(), CommonConstant.NORMAL_LOCK_TIMES);
        if (lock) {
            try {
                //cropId 解密 crop那边是URLDecoder.encode 防止特殊字符解析出错
                String decrypt = PbeEncryptUtil.decrypt(URLDecoder.decode(crop, "UTF-8"), SyncConfig.OPEN_ENTERPRISE_ID);
                if (StringUtils.isBlank(decrypt)) {
                    return;
                }
                if (!decrypt.contains(SEPARATOR)) {
                    return;
                }
                String appType = StringUtils.substringAfterLast(decrypt, SEPARATOR);
                String cropId = StringUtils.substringBeforeLast(decrypt, SEPARATOR);
                //鉴权，是否有这个企业
                DataSourceHelper.reset();
                EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.getEnterpriseConfigByCorpIdAndAppType(cropId, appType);
                if (Objects.isNull(enterpriseConfigDO)) {
                    log.info("this enterprise is invalid, cropId: {} appType: {}", cropId, appType);
                    return;
                }
                //如果是企微酷店掌，做userId的转换
                boolean isQw = isQwType(enterpriseConfigDO.getAppType());
                String enterpriseId = enterpriseConfigDO.getEnterpriseId();
                if (isQw) {
                    DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                    if (CollectionUtils.isNotEmpty(dto.getUserIds())) {
                        List<String> userIds = enterpriseUserMapper.selectUserIdsByIds(enterpriseConfigDO.getEnterpriseId(), dto.getUserIds());
                        dto.setUserIds(userIds);
                    }
                }
                if (CollectionUtils.isEmpty(dto.getUserIds())) {
                    log.info("this userIds is empty,not send msg");
                    return;
                }
                //处理cropId 酷学院那边crop = 门店的cropId + | + appType
                String param = "";
                if (StringUtils.isNotBlank(dto.getMessageUrl())) {
                    //小程序链接
                    String coolCollegeMessageUrl = dto.getMessageUrl();
                    String replaceUrl = StringUtils.replace(coolCollegeMessageUrl, "corp=" + decrypt + "", "corp=" + cropId + "");
//                    //pc端链接 门店端没有pc端通知  暂时不做
//                    String pcMessageUrl = URLDecoder.decode(dto.getPcMessageUrl(), "UTF-8");
//                    String replacePcUrl = StringUtils.replace(pcMessageUrl, "corp=" + decrypt + "", "corp=" + cropId + "");
                    //切割字符串
                    param = StringUtils.substringAfter(replaceUrl, SEPARATOR_QUESTION_MARK);
                }
                // OA特有参数
                String messageUrl = Constants.E_APP_COOL_COLLEGE + param;
                wechatService.sendWXMsg(enterpriseConfigDO, dto, param);
                //数智门店和酷店掌的url不同
                try {
                    if (AppTypeEnum.WX_APP2.getValue().equals(appType)) {
                        String noticeUrl = String.format(qywxUrlCoolCollege, cropId, appType);
                        messageUrl = URLEncoder.encode(noticeUrl + param, "UTF-8");
                        messageUrl = String.format(oauthUrl, suiteId2, messageUrl);
                    }
                    if (AppTypeEnum.isWxSelfAndPrivateType(appType)) {
                        String noticeUrl = String.format(qywxUrlCoolCollege, cropId, appType);
                        //正新切换域名
                        if("214ac5a3a517472a87268e02a2e6410a".equals(enterpriseId)){
                            noticeUrl = noticeUrl.replace("store-h5.coolstore.cn", "zx-h5.coolstore.cn");
                        }
                        messageUrl = URLEncoder.encode(noticeUrl + param, "UTF-8");
                        messageUrl = String.format(Constants.WX_SELF_AUTH_URL, cropId, messageUrl);
                        log.info("appType:{},messageUrl:{}", appType, messageUrl);
                    }
                    if (AppTypeEnum.FEI_SHU.getValue().equals(appType)) {
                        messageUrl = String.format(feiShuNoticeUrl + "&noticeType=coolcollege", URLEncoder.encode(param, "UTF-8"));
                        log.info("appType:{},messageUrl:{}", appType, messageUrl);
                    }
                    if (AppTypeEnum.ONE_PARTY_APP.getValue().equals(appType) || AppTypeEnum.ONE_PARTY_APP2.getValue().equals(appType)) {
                        String noticeUrl = MessageFormat.format(onepartyNoticeUrlCoolCollege, cropId);
                        messageUrl = noticeUrl + param;
                        log.info("门店通培训通知appType:{},messageUrl:{}", appType, messageUrl);
                    }
                } catch (Exception e) {
                    log.error("组装企微授权链接失败", e);
                }
                JSONObject args = new JSONObject();
                args.put("content", dto.getMessageContent());
                args.put("corpId", cropId);
                args.put("appType", appType);
                if (SyncConfig.MESSAGE_TYPE.equals(dto.getNoticeMessageType())) {
                    args.put("messageType", "text");
                }
                String headTitle = "";
                JSONObject oaJson = new JSONObject();
                oaJson.put("message_url", messageUrl);
                JSONObject headJson = new JSONObject();
                headJson.put("bgcolor", "FFBBBBBB");
                headJson.put("text", headTitle);
                oaJson.put("head", headJson);
                JSONObject bodyJson = new JSONObject();
                bodyJson.put("title", dto.getMessageTitle());
                bodyJson.put("content", dto.getMessageContent());
                if (StringUtils.isNotBlank(dto.getPicUrl())) {
                    bodyJson.put("image", dto.getPicUrl());
                }else {
                    bodyJson.put("image", "");
                }
                oaJson.put("body", bodyJson);
                args.put("oaJson", oaJson);
                args.put("outBusinessId", UUIDUtils.get32UUID());
                log.info("sendCoolCollegeMsg msg is {}", JSONObject.toJSONString(args));

                // 韵达企业消息转发给内部应用机器人
                if(YundaEnterpriseEnum.yundaAffiliatedCompany(enterpriseConfigDO.getEnterpriseId())){
                     // 韵达企业不发送门店通消息，通过韵达内部应用机器人转发消息
                     yundaService.sendServiceWindowMsg(dto, messageUrl);
                    return;
                }
                if(TruelyAkEnterpriseEnum.aokangAffiliatedCompany(enterpriseConfigDO.getEnterpriseId())){
                    // 奥康企业不发送门店通消息，通过韵达内部应用机器人转发消息
                    serviceWindowService.sendServiceWindowMsg(dto, messageUrl);
                    return;
                }
                jmsSendMessageSyncService.sendMessageAsync(cropId, dto.getUserIds(), args, MqQueueNameEnum.MQ_QUEUE_NAME_DING.getValue());
                //发送APP通知
                if(AppTypeEnum.APP.getValue().equals(appType)){
                    AppPushMsgDTO appPushMsgDTO = new AppPushMsgDTO();
                    appPushMsgDTO.setTitle(dto.getMessageTitle());
                    appPushMsgDTO.setContent(dto.getMessageContent());
                    appPushMsgDTO.setPushType("ACCOUNT");
                    String targetValue = String.join(",", ListUtils.emptyIfNull(dto.getUserIds()));
                    appPushMsgDTO.setPushTarget(targetValue);
                    AppExtraParamDTO appExtraParamDTO =new AppExtraParamDTO();
                    appExtraParamDTO.setMessageId(UUIDUtils.get32UUID());
                    appExtraParamDTO.setMessageUrl(messageUrl);
                    appExtraParamDTO.setMessageType(2);
                    appPushMsgDTO.setExtraParam(appExtraParamDTO);
                    jmsSendMessageSyncService.sendMessageAsync(cropId, dto.getUserIds(), (JSONObject)JSONObject.toJSON(appPushMsgDTO), MqQueueNameEnum.MQ_QUEUE_NAME_APP_PUSH.getValue());
                }

            } catch (Exception e) {
                log.error("sendCoolCollegeMsg-> decrypt has error", e);
            } finally {
                redisUtilPool.delKey(lockKey);
            }
        }
    }

    /**
     * 转发正新服务
     */
    private void sendZxCoolCollegeMsgTo(CoolCollegeMsgDTO dto, String crop) {
        String zxMsgForwardUrl = redisUtilPool.getString("zx_msg_forward_url");
        try {
            HttpResponse response = HttpUtil.createPost(zxMsgForwardUrl + "/v3/enterprises/college/integration/sendMsg?corp_id=" + crop)
                    .body(JSONObject.toJSONString(dto))
                    .execute();
            log.info("消息转发成功, response:{}", JSONObject.toJSONString(response));
        } catch (Exception e) {
            log.info("消息转发失败", e);
        }
    }

    /**
     * 职位较为特殊，门店端是物理删除，因此这里的新增和修改用id可以满足，删除就需要用职位对象
     * 区域即为部门，这里删除，新增，修改，都可以用id来做，因为门店端是逻辑删除，可用id
     * 人员只做修改和新增的推送  不做删除推送，因为保留酷学院历史的学习数据
     * @param dto
     */
    @Override
    public void coolStoreDataChange(CoolStoreDataChangeDTO dto) {
        if (StringUtils.isBlank(dto.getEnterpriseId())) {
            return;
        }
        //校验推送的企业是否已经开通了酷学院
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(dto.getEnterpriseId());
        EnterpriseSettingDO setting = enterpriseSettingService.selectByEnterpriseId(dto.getEnterpriseId());
        if (Objects.isNull(enterpriseConfig) || Objects.isNull(setting)) {
            return;
        }
        if (StringUtils.isBlank(enterpriseConfig.getCoolCollegeEnterpriseId()) || StringUtils.isBlank(enterpriseConfig.getCoolCollegeSecret())) {
            return;
        }
        //如果是酷店掌并且是开通了业培一体的业务  在进行数据的推送
        Boolean isCoolCollege = AppTypeEnum.isCoolCollege(enterpriseConfig.getAppType());
        log.info("coolStoreDataChange:{}",JSONObject.toJSONString(dto));
        if (!isCoolCollege && setting.getAccessCoolCollege()) {
            switch (dto.getType()) {
                case "position":
                    if (ChangeDataOperation.ADD.getCode().equals(dto.getOperation()) || ChangeDataOperation.UPDATE.getCode().equals(dto.getOperation())) {
                        List<String> positionIds = dto.getDataIds();
                        if (CollectionUtils.isNotEmpty(positionIds)) {
                            //推送职位信息
                            sendPositionsToCoolCollege(dto.getEnterpriseId(), positionIds.stream().map(m -> Long.valueOf(m)).collect(Collectors.toList()));
                        }
                    }
                    if (ChangeDataOperation.DELETE.getCode().equals(dto.getOperation())) {
                        List<SysRoleDO> sysRoleDOS = dto.getSysRoleDOS();
                        if (CollectionUtils.isNotEmpty(sysRoleDOS)) {
                            //推送删除的职位
                            sendDelPositionsToCoolCollege(dto.getEnterpriseId(), sysRoleDOS);
                        }
                    }
                    break;
                case "region":
                    List<String> regionIds = dto.getDataIds();
                    if (CollectionUtils.isNotEmpty(regionIds)) {
                        //推送区域即部门的数据
                        sendDepartmentsToCoolCollege(dto.getEnterpriseId(), regionIds.stream().map(m -> Long.valueOf(m)).collect(Collectors.toList()),null);
                    }
                    break;
                case "user":
                    List<String> userIds = dto.getDataIds();
                    if (CollectionUtils.isNotEmpty(userIds)) {
                        //推送人员的信息
                        sendUsersToCoolCollege(dto.getEnterpriseId(), userIds,null);
                    }
                    break;
                default:
                    log.info("this type is not send data to coolCollege, data is {}", JSONObject.toJSONString(dto));
                    break;
            }
        }

    }

    @Override
    public void sendDataChangeMsg(String eid, List<String> dataIds, String operation, String type) {
        CoolStoreDataChangeDTO coolStoreDataChangeDTO = new CoolStoreDataChangeDTO();
        coolStoreDataChangeDTO.setOperation(operation);
        coolStoreDataChangeDTO.setDataIds(dataIds);
        coolStoreDataChangeDTO.setEnterpriseId(eid);
        coolStoreDataChangeDTO.setType(type);
        simpleMessageService.send(JSONObject.toJSONString(coolStoreDataChangeDTO), RocketMqTagEnum.COOL_STORE_DATA_CHANGE);
    }

    @Override
    public JSONObject getCoolCollegeTodoList(String storeEnterpriseId, String userId, Integer pageSize, Integer pageNum, String type) {
        if (StringUtils.isBlank(storeEnterpriseId)) {
            throw new ServiceException(ErrorCodeEnum.COOL_STORE_ENTERPRISE_ID_NOT_NULL);
        }
        //查询门店企业获取企业相关信息
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(storeEnterpriseId);
        //构造请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("access-token", getEnterpriseToken(storeEnterpriseId));
        //是否是企微的酷店掌
        boolean isQw = isQwType(enterpriseConfig.getAppType());
        if (isQw) {
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserId(storeEnterpriseId, userId);
            if (Objects.nonNull(enterpriseUserDO)) {
                userId = enterpriseUserDO.getId();
            }
        }
        if (StringUtils.isBlank(userId)) {
            throw new ServiceException(ErrorCodeEnum.COOL_STORE_USER_ID_ERROR);
        }
        ResponseEntity<JSONObject> exchange = restTemplate.exchange(String.format(getTodoListUrl, enterpriseConfig.getDingCorpId() + SEPARATOR + enterpriseConfig.getAppType(), userId, pageNum, pageSize),
                HttpMethod.GET, new HttpEntity<>(httpHeaders), JSONObject.class);
        //获取返回的数据
        JSONObject response = exchange.getBody();
        log.info("get coolcollege todo list resp {}", JSONObject.toJSONString(response));
        if (Objects.isNull(response) || !SyncConfig.STATUS_200.equals(response.getString("code"))) {
            throw new ServiceException(ErrorCodeEnum.COOL_COLLEGE_TODO_LIST_ERROR);
        }
        JSONObject data = response.getJSONObject("data");
        if (Objects.isNull(data)) {
            throw new ServiceException(ErrorCodeEnum.COOL_COLLEGE_TODO_LIST_ERROR);
        }
        if (CoolCollegeTodoListType.TODO_EXAM.getCode().equals(type)) {
            return data.getJSONObject(CoolCollegeTodoListType.TODO_EXAM.getCode());
        }
        if (CoolCollegeTodoListType.TODO_PC_STUDY.getCode().equals(type)) {
            return data.getJSONObject(CoolCollegeTodoListType.TODO_PC_STUDY.getCode());
        }
        if (CoolCollegeTodoListType.TODO_MOBILE_RESEARCH.getCode().equals(type)) {
            return data.getJSONObject(CoolCollegeTodoListType.TODO_MOBILE_RESEARCH.getCode());
        }
        if (CoolCollegeTodoListType.TODO_MOBILE_STUDY.getCode().equals(type)) {
            return data.getJSONObject(CoolCollegeTodoListType.TODO_MOBILE_STUDY.getCode());
        }
        if (CoolCollegeTodoListType.TODO_STUDY_PROJECT.getCode().equals(type)) {
            return data.getJSONObject(CoolCollegeTodoListType.TODO_STUDY_PROJECT.getCode());
        }
        return data;
    }

    @Override
    public Boolean getEnterpriseIncludeTrainingModule(String enterpriseId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        if (config.getCurrentPackage() != null) {
            EnterprisePackageDO enterprisePackageDO = enterprisePackageDao.selectByPrimaryKey(config.getCurrentPackage());
            if (enterprisePackageDO == null) {
                throw new ServiceException(ErrorCodeEnum.INVALID_ENTERPRISE_PACKAGE);
            }
            List<Long> moduleIds = enterprisePackageModuleMappingDao.selectModuleIdsByPackageId(enterprisePackageDO.getId());
           if (moduleIds.contains(Constants.TRAINING_BUSINESS_MODULE)) {
               return Boolean.TRUE;
           }
        }
        return Boolean.FALSE;
    }

    @Override
    public void sendYunDaMsg(String eid, List<String> jobNumList, String questionOrderCode) {
        if (CollectionUtils.isEmpty(jobNumList) || StringUtils.isBlank(questionOrderCode)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        if (StringUtils.isBlank(eid)) {
            eid = YundaEnterpriseEnum.YUNDA.getCode();
        }
        YunDaQuestionCodeEnum yunDaQuestionCodeEnum = YunDaQuestionCodeEnum.map.get(questionOrderCode);
        if (yunDaQuestionCodeEnum == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        String messageTitle = yunDaQuestionCodeEnum.getTitle();
        //处理cropId 酷学院那边crop = 门店的cropId + | + appType
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
//        EnterpriseConfigDO config = new EnterpriseConfigDO();
//        config.setCoolCollegeEnterpriseId("1610824935998099463");
//        config.setDingCorpId("ding48c1573c5491897335c2f4657eb6378f");
        String param = "eid=" + config.getCoolCollegeEnterpriseId() + "&flag=study_project&projectId=" +
                yunDaQuestionCodeEnum.getProjectId() + "&pc_slide=true";
        // OA特有参数
        String noticeUrl = MessageFormat.format(onepartyNoticeUrlCoolCollege, config.getDingCorpId());
        String messageUrl = noticeUrl + param;
        log.info("门店通培训通知,messageUrl:{}", messageUrl);
        CoolCollegeMsgDTO coolCollegeMsgDTO = new CoolCollegeMsgDTO();
        // 韵达企业不发送门店通消息，通过韵达内部应用机器人转发消息
        coolCollegeMsgDTO.setMessageContent(messageTitle);
        coolCollegeMsgDTO.setMessageTitle(messageTitle);
        coolCollegeMsgDTO.setUserIds(jobNumList);
        yundaService.sendServiceWindowMsg(coolCollegeMsgDTO, messageUrl);
    }

    /**
     * 构造推送的数据，并且推送数据
     * @param enterpriseToken token
     * @param enterpriseUserDOS 用户信息
     * @param positionIds 用户和职位的映射
     * @param regionIds 用户和部门的映射
     * @param chargeRegionIds 用户和部门主管的映射
     */
    private void buildUserParamAndSend(String storeEnterpriseId, String enterpriseToken, List<EnterpriseUserDO> enterpriseUserDOS,
                                       Map<String, List<String>> positionIds, Map<String, List<String>> regionIds,
                                       Map<String, List<String>> chargeRegionIds, String appType) {
        if (CollectionUtils.isEmpty(enterpriseUserDOS)) {
            return;
        }
        List<CoolCollegeUserDTO> coolCollegeUserDTOS = new ArrayList<>();
        for (EnterpriseUserDO enterpriseUserDO : enterpriseUserDOS) {
            CoolCollegeUserDTO coolCollegeUserDTO = new CoolCollegeUserDTO();
            //授权用户全部设置为true
            coolCollegeUserDTO.setActive(Boolean.TRUE);
            //主管的id
            if (MapUtils.isNotEmpty(chargeRegionIds) && CollectionUtils.isNotEmpty(chargeRegionIds.get(enterpriseUserDO.getUserId()))) {
                coolCollegeUserDTO.setCharge_department_ids(chargeRegionIds.get(enterpriseUserDO.getUserId()));
            }
            coolCollegeUserDTO.setCreate_time(enterpriseUserDO.getCreateTime().getTime());
            //部门id
            if (MapUtils.isNotEmpty(regionIds)) {
                List<String> ids = regionIds.get(enterpriseUserDO.getUserId());
                if (CollectionUtils.isNotEmpty(ids)) {
                    //去除重复数据
                    Set<String> idSet = new HashSet<>(ids);
                    idSet.remove(SyncConfig.ROOT_DEPT_ID_STR);
                    coolCollegeUserDTO.setDepartment_ids(new ArrayList<>(idSet));
                }
            }
            coolCollegeUserDTO.setEmail(enterpriseUserDO.getEmail());
            coolCollegeUserDTO.setExtend_column(Lists.newArrayList());
            if(AppTypeEnum.isDingType(appType)){
                List<UserExpandColumnDto> extend_column = Lists.newArrayList();
                UserExpandColumnDto userExpandColumnDto = new UserExpandColumnDto();
                userExpandColumnDto.setColumn_name("钉钉用户ID");
                userExpandColumnDto.setColumn_value(enterpriseUserDO.getUserId());
                userExpandColumnDto.setColumn_order(1);
                extend_column.add(userExpandColumnDto);
                coolCollegeUserDTO.setExtend_column(extend_column);
            }
            coolCollegeUserDTO.setIs_delete(enterpriseUserDO.getActive()?0:1);
            coolCollegeUserDTO.setJobnumber(enterpriseUserDO.getJobnumber());
            coolCollegeUserDTO.setMobile(enterpriseUserDO.getMobile());
            coolCollegeUserDTO.setIsAdmin(Boolean.FALSE);
            coolCollegeUserDTO.setThird_admin(Boolean.FALSE);
            //职位的id
            if (MapUtils.isNotEmpty(positionIds)) {
                coolCollegeUserDTO.setPost_ids(positionIds.get(enterpriseUserDO.getUserId()));
                String masterRoleId = Role.MASTER.getId();
                if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(appType)) {
                    Long roleIdByRoleEnum = sysRoleService.getRoleIdByRoleEnum(storeEnterpriseId, Role.MASTER.getRoleEnum());
                    masterRoleId = String.valueOf(roleIdByRoleEnum);
                }
                if (positionIds.getOrDefault(enterpriseUserDO.getUserId(),new ArrayList<>()).contains(masterRoleId)) {
                    coolCollegeUserDTO.setIsAdmin(Boolean.TRUE);
                    coolCollegeUserDTO.setThird_admin(Boolean.TRUE);
                }
                // 韵达企业只推固定管理员
                if(YundaEnterpriseEnum.yundaAffiliatedCompany(storeEnterpriseId)){
                    if(YUNDA_MAIN_ADMIN_USER_ID.contains(enterpriseUserDO.getUserId())){
                        coolCollegeUserDTO.setIsAdmin(Boolean.TRUE);
                        coolCollegeUserDTO.setThird_admin(Boolean.TRUE);
                    }else {
                        coolCollegeUserDTO.setIsAdmin(Boolean.FALSE);
                        coolCollegeUserDTO.setThird_admin(Boolean.FALSE);
                    }
                    // 韵达过滤初始化角色id
                    List<String> post_ids = positionIds.get(enterpriseUserDO.getUserId());
                    post_ids = ListUtils.emptyIfNull(post_ids).stream().filter(post_id -> !YUNDA_INIT_ROLE_ID.contains(post_id)).collect(Collectors.toList());
                    coolCollegeUserDTO.setPost_ids(post_ids);
                }
            }
            // todo 有问题
            //是否是企微的酷店掌，使用id做userId
            boolean isQw = isQwType(appType);
            if (isQw) {
                coolCollegeUserDTO.setUser_id(enterpriseUserDO.getId());
            } else {
                coolCollegeUserDTO.setUser_id(enterpriseUserDO.getUserId());
            }
            coolCollegeUserDTO.setUser_name(enterpriseUserDO.getName());
            if(NOT_DEPEND_STORE_ADMIN_LIST.contains(storeEnterpriseId)){
                coolCollegeUserDTO.setIsAdmin(null);
                coolCollegeUserDTO.setThird_admin(null);
            }
            coolCollegeUserDTOS.add(coolCollegeUserDTO);
        }
        SendCoolCollegeRequestDTO sendCoolCollegeRequestDTO = new SendCoolCollegeRequestDTO();
        sendCoolCollegeRequestDTO.setData(coolCollegeUserDTOS);
        sendCoolCollegeRequestDTO.setData_count(coolCollegeUserDTOS.size());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("access-token", enterpriseToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        log.info("send_user_info_request {}", JSONObject.toJSONString(sendCoolCollegeRequestDTO));
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(thirdOaSendUserUrl, new HttpEntity<>(JSONObject.toJSONString(sendCoolCollegeRequestDTO), httpHeaders), JSONObject.class);
        log.info("send_user_info_resp {}", JSONObject.toJSONString(responseEntity));
        JSONObject response = responseEntity.getBody();
        if (Objects.nonNull(response) && (SyncConfig.STATUS_500.equals(response.getString("code")))) {
            if("25ae082b3947417ca2c835d8156a8407".equals(storeEnterpriseId)){
                String thirdoaTokenKey = "thirdoaTokenKey_" + storeEnterpriseId;
                redisUtilPool.delKey(thirdoaTokenKey);
            }
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(storeEnterpriseId);
            //获取企业的token，推送数据携带，鉴权
            enterpriseToken = getEnterpriseToken(storeEnterpriseId);
            //切回企业库
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
            httpHeaders.set("access-token", enterpriseToken);
            responseEntity = restTemplate.postForEntity(thirdOaSendUserUrl, new HttpEntity<>(JSONObject.toJSONString(sendCoolCollegeRequestDTO), httpHeaders), JSONObject.class);
            log.info("token无效后再试一次send_user_info_resp {}", JSONObject.toJSONString(responseEntity));
        }
    }

    private boolean isQwType(String appType) {
        return !AppTypeEnum.isCoolCollege(appType)
                && !AppTypeEnum.DING_DING2.getValue().equals(appType)
                && !AppTypeEnum.ONE_PARTY_APP2.getValue().equals(appType);
    }
}
