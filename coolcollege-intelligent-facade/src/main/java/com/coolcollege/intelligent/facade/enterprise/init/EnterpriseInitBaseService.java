package com.coolcollege.intelligent.facade.enterprise.init;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.identify.Base64;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.enterprise.AuthLevelEnum;
import com.coolcollege.intelligent.common.enums.user.UserTypeEnum;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.sync.vo.AddressBookChangeReqBody;
import com.coolcollege.intelligent.common.sync.vo.EnterpriseOpenMsg;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.ScriptUtil;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.convert.ConvertFactory;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDepartmentDao;
import com.coolcollege.intelligent.dao.enterprise.dao.SubordinateMappingDAO;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dto.AuthCorpInfoDTO;
import com.coolcollege.intelligent.dto.AuthInfoDTO;
import com.coolcollege.intelligent.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.facade.SyncFacade;
import com.coolcollege.intelligent.facade.UnifyTaskFcade;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.department.dto.QueryDeptChildDTO;
import com.coolcollege.intelligent.model.enterprise.*;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.enums.TaskRunRuleEnum;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskBuildDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.rpc.license.LicenseApiService;
import com.coolcollege.intelligent.service.dingSync.DingUserSyncService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserMappingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xuanfeng
 */
@Component
@Slf4j
public abstract class EnterpriseInitBaseService {

    @Autowired
    EnterpriseUserDepartmentDao enterpriseUserDepartmentDao;

    @Autowired
    EnterpriseInitConfigApiService enterpriseInitConfigApiService;

    @Autowired
    SysDepartmentMapper sysDepartmentMapper;

    @Autowired
    SysDepartmentService sysDepartmentService;

    @Autowired
    RegionService regionService;

    @Autowired
    EnterpriseUserService enterpriseUserService;

    @Autowired
    EnterpriseUserMappingService enterpriseUserMappingService;

    @Autowired
    protected SysRoleService sysRoleService;

    @Autowired
    UserRegionMappingDAO userRegionMappingDAO;

    @Autowired
    SubordinateMappingDAO subordinateMappingDAO;

    @Autowired
    DingUserSyncService dingUserSyncService;

    @Autowired
    ConvertFactory convertFactory;

    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private SyncFacade syncFacade;
    @Resource
    protected ScriptUtil scriptUtil;
    @Resource
    private LicenseApiService licenseApiService;
    @Resource
    private UnifyTaskFcade unifyTaskFcade;

    @Value("${boss.send.message.url}")
    private String bossSendMessageUrl;

    @Value("${boss.send.message.sign}")
    private String bossSendMessageSign;

    @Value("${boss.click.url}")
    private String bossClickUrl;

    public static final Integer RECORD_MAX_SIZE = 1000;

    /**
     * 企业开通初始化
     * @param corpId
     * @param eid
     * @param appType
     * @param dbName
     * @param openUserId
     */
    public abstract void enterpriseInit(String corpId, String eid, String appType, String dbName, String openUserId);

    /**
     * 单独同步部门和区域
     * @param corpId
     * @param eid
     * @param appType
     * @param dbName
     */
    public abstract void enterpriseInitDepartment(String corpId, String eid, String appType, String dbName);

    /**
     * 单独同步人员
     * @param corpId
     * @param eid
     * @param appType
     * @param dbName
     * @param isScopeChange 是否为授权范围变更
     */
    public abstract void enterpriseInitUser(String corpId, String eid, String appType, String dbName, Boolean isScopeChange);

    /**
     * 同步人员
     * @param corpId
     * @param eid
     * @param appType
     * @param dbName
     */
    public abstract void onlySyncUser(String corpId, String eid, String appType, String dbName);

    /**
     * 构建ai用户
     * @return
     */
    public EnterpriseUserRequest getAIUser() {
        EnterpriseUserRequest enterpriseUserRequest = new EnterpriseUserRequest();
        EnterpriseUserDO enterpriseUserDO = new EnterpriseUserDO();
        enterpriseUserDO.setId(AIEnum.AI_ID.getCode());
        enterpriseUserDO.setName(AIEnum.AI_NAME.getCode());
        enterpriseUserDO.setUserId(AIEnum.AI_USERID.getCode());
        enterpriseUserDO.setMobile(AIEnum.AI_MOBILE.getCode());
        enterpriseUserDO.setRoles(AIEnum.AI_ROLES.getCode());
        enterpriseUserDO.setUnionid(AIEnum.AI_UUID.getCode());
        enterpriseUserDO.setIsAdmin(Boolean.TRUE);
        enterpriseUserDO.setActive(Boolean.TRUE);
        enterpriseUserRequest.setEnterpriseUserDO(enterpriseUserDO);
        enterpriseUserRequest.setDepartment(AIEnum.AI_DEPARTMENT.getCode());
        enterpriseUserRequest.setDepartments(AIEnum.AI_DEPARTMENT.getCode());
        return enterpriseUserRequest;
    }

    public List<EnterpriseUserRequest> getAdminList(List<String> adminList) {
        if(CollectionUtils.isEmpty(adminList)){
            return Lists.newArrayList();
        }
        List<EnterpriseUserRequest> resultList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(adminList)){
            for (String userId : adminList) {
                EnterpriseUserRequest enterpriseUserRequest = new EnterpriseUserRequest();
                EnterpriseUserDO enterpriseUserDO = new EnterpriseUserDO();
                enterpriseUserDO.setId(userId);
                enterpriseUserDO.setName(userId);
                enterpriseUserDO.setUserId(userId);
                enterpriseUserDO.setUnionid(userId);
                enterpriseUserDO.setRoles(AIEnum.AI_ROLES.getCode());
                enterpriseUserDO.setIsAdmin(Boolean.TRUE);
                enterpriseUserDO.setActive(Boolean.TRUE);
                enterpriseUserRequest.setEnterpriseUserDO(enterpriseUserDO);
                enterpriseUserRequest.setDepartment(AIEnum.AI_DEPARTMENT.getCode());
                enterpriseUserRequest.setDepartments(AIEnum.AI_DEPARTMENT.getCode());
                resultList.add(enterpriseUserRequest);
            }
        }
        return resultList;
    }

    /**
     * 处理用户部门的关系
     * @param eid
     * @param users
     * @param deptIdMap
     */
    public void handlerUserDepartmentMapping(String eid, List<EnterpriseUserRequest> users, Map<String, String> deptIdMap) {
        try {
            List<EnterpriseUserDepartmentDO> deptUsers = new ArrayList<>();
            List<String> userIds = new ArrayList<>();
            users.forEach(user -> {
                if (Objects.isNull(user.getEnterpriseUserDO())) {
                    return;
                }
                //移除不在授权范围内的部门
                List<String> deptIds = user.getDepartmentLists();
                if(CollectionUtils.isEmpty(user.getDepartmentLists())){
                    deptIds = Collections.singletonList(SyncConfig.ROOT_DEPT_ID_STR);
                }
                //创建部门用户的关系映射
                List<EnterpriseUserDepartmentDO> deptUserMapping = ListUtils.emptyIfNull(deptIds)
                        .stream()
                        .map(m -> new EnterpriseUserDepartmentDO(user.getEnterpriseUserDO().getUserId(), m, Boolean.FALSE)).collect(Collectors.toList());
                //移除不在授权范围内的部门的主管部门
                List<EnterpriseUserDepartmentDO> leaderDeptMapping = ListUtils.emptyIfNull(user.getLeaderInDepts())
                        .stream()
                        .map(m -> new EnterpriseUserDepartmentDO(user.getEnterpriseUserDO().getUserId(), m, Boolean.TRUE)).collect(Collectors.toList());
                deptUsers.addAll(deptUserMapping);
                deptUsers.addAll(leaderDeptMapping);
                userIds.add(user.getEnterpriseUserDO().getUserId());
                if (deptUsers.size() > RECORD_MAX_SIZE) {
                    //采取先删除后插入
                    enterpriseUserDepartmentDao.deleteMapping(eid, userIds);
                    enterpriseUserDepartmentDao.deleteUserDepartmentAuth(eid, userIds);
                    //插入
                    enterpriseUserDepartmentDao.batchInsert(eid, deptUsers);
                    deptUsers.clear();
                    userIds.clear();
                }
                //处理用户表中departments字段的维护 飞书不在维护
                enterpriseUserService.updateUserDeptPath(user, deptIdMap);
            });
            if (CollectionUtils.isNotEmpty(userIds)) {
                enterpriseUserDepartmentDao.deleteMapping(eid, userIds);
                enterpriseUserDepartmentDao.deleteUserDepartmentAuth(eid, userIds);
            }
            if (CollectionUtils.isNotEmpty(deptUsers)) {
                enterpriseUserDepartmentDao.batchInsert(eid, deptUsers);
            }
        } catch (Exception e) {
            log.error("设置用户部门信息异常", e);
        }
    }

    /**
     * 处理用户和区域的关系
     * @param eid
     * @param enterpriseUserRequests
     * @param isScopeChange 是否为授权范围变更
     */
    public void handlerUserRegionMapping(String eid, List<EnterpriseUserRequest> enterpriseUserRequests, Long unclassifiedRegionId, Boolean isScopeChange) {
        //用户区域映射关系
        List<UserRegionMappingDO> userRegionMappings = new ArrayList<>();
        //我的下属部门数据
        List<SubordinateMappingDO> subordinateMapping = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        List<Long> regionIds = new ArrayList<>();
        enterpriseUserRequests.forEach(user -> {
            if (Objects.isNull(user.getEnterpriseUserDO())) {
                return;
            }
            //移除不在授权范围内的部门
            List<String> deptIds = user.getDepartmentLists();
            if (CollectionUtils.isEmpty(deptIds)) {
                if (Objects.isNull(unclassifiedRegionId)) {
                    return;
                }
                //如果所属的部门未在授权部门内，挂在未分组
                regionIds.add(unclassifiedRegionId);
            } else {
                //db查询区域表的数据，找到映射的区域
                List<Long> regionIdsBySynDingDeptIds = regionService.getRegionIdsBySynDingDeptIds(eid, deptIds.stream().map(a -> a).collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(regionIdsBySynDingDeptIds)) {
                    regionIds.addAll(regionIdsBySynDingDeptIds);
                } else {
                    //未找到映射关系
                    regionIds.add(unclassifiedRegionId);
                }
            }
            //构建用户和区域的关系
            ListUtils.emptyIfNull(regionIds)
                    .stream()
                    .forEach(item -> {
                        userRegionMappings.add(buildUserRegionMappingDO(user.getEnterpriseUserDO().getUserId(), item));

                    });
            //清除用户区域的关系的regionIds用来存储下属映射的区域regionIds
            regionIds.clear();
            //构建我的下属
            //移除不在授权范围内的部门的主管部门
            List<String> leaderDeptIds = user.getLeaderInDepts();
            if (CollectionUtils.isNotEmpty(leaderDeptIds)) {
                List<Long> regionIdsBySynDingDeptIds = regionService.getRegionIdsBySynDingDeptIds(eid, leaderDeptIds.stream().map(a->a).collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(regionIdsBySynDingDeptIds)) {
                    regionIds.addAll(regionIdsBySynDingDeptIds);
                }
            }
            ListUtils.emptyIfNull(regionIds)
                    .stream()
                    .forEach(item -> {
                        subordinateMapping.add(buildSubordinateMappingDO(user.getEnterpriseUserDO().getUserId(), item));
                    });
            //用完即清除
            regionIds.clear();
            userIds.add(user.getEnterpriseUserDO().getUserId());
            //触发条件，满足一个，就开始进行处理落库
            if (userRegionMappings.size() > RECORD_MAX_SIZE || subordinateMapping.size() > RECORD_MAX_SIZE) {
                //先删除 后新增
                //用户和区域的映射关系
                if (Boolean.TRUE.equals(isScopeChange)) {
                    userRegionMappingDAO.deletedExcludeCreateByUserIds(eid, userIds);
                } else {
                    userRegionMappingDAO.deletedByUserIds(eid, userIds);
                }
                userRegionMappingDAO.batchInsertRegionMapping(eid, userRegionMappings);
                //我的下属
//                subordinateMappingDAO.deletedByUserIds(eid, userIds);
//                subordinateMappingDAO.batchInsertSubordinateMapping(eid, subordinateMapping);
                //调用订正用户表字段user_region_ids
                enterpriseUserService.updateUserRegionPathList(eid, userIds);
                userRegionMappings.clear();
                subordinateMapping.clear();
                userIds.clear();
            }
        });
        if (CollectionUtils.isNotEmpty(userRegionMappings)) {
            //先删除 后新增
            //用户和区域的映射关系
            if (Boolean.TRUE.equals(isScopeChange)) {
                userRegionMappingDAO.deletedExcludeCreateByUserIds(eid, userIds);
            } else {
                userRegionMappingDAO.deletedByUserIds(eid, userIds);
            }
            userRegionMappingDAO.batchInsertRegionMapping(eid, userRegionMappings);
            //调用订正用户表字段user_region_ids
            enterpriseUserService.updateUserRegionPathList(eid, userIds);
        }
        if (CollectionUtils.isNotEmpty(subordinateMapping)) {
            //先删除 后新增
            //我的下属
//            subordinateMappingDAO.deletedByUserIds(eid, userIds);
//            subordinateMappingDAO.batchInsertSubordinateMapping(eid, subordinateMapping);
        }
    }

    /**
     * 构建UserRegionMappingDO
     * @param userId
     * @param regionId
     * @return
     */
    public UserRegionMappingDO buildUserRegionMappingDO(String userId, Long regionId) {
        UserRegionMappingDO userRegionMappingDO = new UserRegionMappingDO();
        userRegionMappingDO.setUserId(userId);
        userRegionMappingDO.setRegionId(String.valueOf(regionId));
        return userRegionMappingDO;
    }

    /**
     * 构建SubordinateMappingDO
     * @param userId
     * @param regionId
     * @return
     */
    public SubordinateMappingDO buildSubordinateMappingDO(String userId, Long regionId) {
        SubordinateMappingDO subordinateMappingDO = new SubordinateMappingDO();
        subordinateMappingDO.setUserId(userId);
        subordinateMappingDO.setRegionId(String.valueOf(regionId));
        subordinateMappingDO.setType(SyncConfig.ZERO);
        return subordinateMappingDO;
    }

    /**
     * 删除已经移除的用户
     * @param eid
     * @param handlerUserIds
     */
    public void deleteUser(String eid, Set<String> handlerUserIds) {
        //删除门店库中和钉钉对应的用户、剩下的为待删除用户
        List<String> allUserIds = enterpriseUserService.selectAllUserIdByUserType(eid, UserTypeEnum.INTERNAL_USER);
        handlerUserIds.forEach(dingUserId -> {
            if (allUserIds.contains(dingUserId)) {
                allUserIds.remove(dingUserId);
            }
        });
        List<String> mainAdminIds = enterpriseUserService.getMainAdmin(eid).stream()
                .map(EnterpriseUserDO::getUserId)
                .collect(Collectors.toList());
        List<String> userIds = new ArrayList<>();
        for (String userId : allUserIds) {
            try {
                //不能删除AI用户和主管理员
                if (!Constants.AI_USER_ID.equals(userId) && !mainAdminIds.contains(userId)) {
                    //删除企业库对应的映射关系
                    dingUserSyncService.syncDeleteUser(userId, eid);
                    userIds.add(userId);
                }
            } catch (Exception e) {
                log.error("enterpriseInitUser delete user has error", e);
            }
        }
        //处理平台库
        //删除平台库对应的映射关系
        DataSourceHelper.reset();
        dingUserSyncService.syncDeleteConfigUser(eid, userIds);
    }

    public void deleteUserByUserIds(String eid, Set<String> deleteUserIds) {
        if(CollectionUtils.isEmpty(deleteUserIds)){
            return;
        }
        try {
            List<String> mainAdminIds = enterpriseUserService.getMainAdmin(eid).stream()
                    .map(EnterpriseUserDO::getUserId)
                    .collect(Collectors.toList());
            for (String userId : deleteUserIds) {
                try {
                    //不能删除AI用户和主管理员
                    if (!mainAdminIds.contains(userId)) {
                        //删除企业库对应的映射关系
                        dingUserSyncService.syncDeleteUser(userId, eid);
                    }
                } catch (Exception e) {
                    log.error("enterpriseInitUser delete user has error", e);
                }
            }
            //处理平台库
            //删除平台库对应的映射关系
            DataSourceHelper.reset();
            dingUserSyncService.syncDeleteConfigUser(eid, new ArrayList<>(deleteUserIds));
        } catch (Exception e) {
            log.info("enterpriseInitUser delete user has error", e);
        }
    }

    public void sendBossMessage(String corpId, String appType){
        try {
            AuthInfoDTO authInfo = enterpriseInitConfigApiService.getAuthInfo(corpId, appType);
            Long timestamp = System.currentTimeMillis();
            String serverUrl = MessageFormat.format(bossSendMessageUrl,
                    String.valueOf(timestamp), sign(timestamp));
            DingTalkClient client = new DefaultDingTalkClient(serverUrl);
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
            link.setTitle("你有一条新线索");
            AuthCorpInfoDTO authCorpInfo = authInfo.getAuthCorpInfo();
            String authLevel = AuthLevelEnum.getMessage(authCorpInfo.getAuthLevel());
            String text = "(" + authLevel + ")" + authCorpInfo.getCorpName() + "于"+ DateUtil.format(new Date(),"yyyy-MM-dd HH:mm") +"开通" + AppTypeEnum.getMessage(appType);
            link.setText(text);
            link.setMessageUrl(bossClickUrl);
            link.setPicUrl("https://oss-cool.coolstore.cn/notice_pic/1789917624436658176.png");
            request.setMsgtype("link");
            request.setLink(link);
            OapiRobotSendResponse response = client.execute(request);
            if(response != null && !response.isSuccess()){
                log.info("sendBossMessage Fail：errorCode:{},errorMessage:{}", response.getErrcode(),response.getErrmsg());
            }
        } catch (Exception e) {
            log.info("sendBossMessage Fail：corpId:{}", corpId);
        }
    }

    public String sign(long timestamp){
        try {
            String secret = bossSendMessageSign;
            String stringToSign = timestamp + "\n" + secret;
            Mac mac  = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            log.info("sendBossMessage:sign Fail：timestamp:{}",timestamp, e);
        }
        return "";
    }

    /**
     * 部门同步为区域
     * @param eid
     * @param deptId
     */
    public void initRegionByDepartment(String eid, String deptId) {
        //首次获取  获取根部门下一级的所有数据
        List<QueryDeptChildDTO> queryDeptChildDTOS = sysDepartmentMapper.getDeptChildListByParentId(eid, deptId);
        if (CollectionUtils.isEmpty(queryDeptChildDTOS)) {
            return;
        }
        List<RegionDO> regionDOS = new ArrayList<>();
        List<String> syncDingDeptIds = new ArrayList<>();
        for (QueryDeptChildDTO deptChildDTO : queryDeptChildDTOS) {
            //设置上级区域的path，方便后续的追溯
            deptChildDTO.setPath("/" + deptId + "/");
            RegionDO regionDO = new RegionDO();
            regionDO.setParentId(String.valueOf(deptId));
            regionDO.setName(deptChildDTO.getName());
            regionDO.setCreateTime(System.currentTimeMillis());
            regionDO.setUpdateTime(System.currentTimeMillis());
            regionDO.setSynDingDeptId(String.valueOf(deptChildDTO.getId()));
            regionDO.setRegionType(RegionTypeEnum.PATH.getType());
            //次序值
            regionDO.setOrderNum(deptChildDTO.getDepartOrder());
            regionDO.setRegionPath("/" +  SyncConfig.ROOT_DEPT_ID_STR + "/");
            regionDO.setDeleted(Boolean.FALSE);
            regionDOS.add(regionDO);
            syncDingDeptIds.add(String.valueOf(deptChildDTO.getId()));
            if (regionDOS.size() > RECORD_MAX_SIZE) {
                //批量插入或更新
                regionService.batchInsertRegions(regionDOS, eid);
                regionDOS.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(regionDOS)) {
            //批量插入或更新
            regionService.batchInsertRegions(regionDOS, eid);
        }
        //递归调用，接着获取下一层级的数据进行处理
        //获取region的id和部门id映射关系
        Map<String, Long> regionIdMap = regionService.getRegionSynDeptIdAndIdMapping(eid, syncDingDeptIds);
        handlerSubRegions(eid, queryDeptChildDTOS, regionIdMap);
    }

    /**
     * 递归调用获取下一级的部门数据，转换为区域
     * @param eid
     * @param depts
     */
    public void handlerSubRegions(String eid,  List<QueryDeptChildDTO> depts, Map<String, Long> regionIdMap) {
        List<RegionDO> regionDOS = new ArrayList<>();
        //暂存一层级的数据
        List<QueryDeptChildDTO> results = new ArrayList<>();
        List<String> syncDingDeptIds = new ArrayList<>();
        for (QueryDeptChildDTO dept : depts) {
            //获取该层级的子节点处理
            List<QueryDeptChildDTO> childDTOS = sysDepartmentMapper.getDeptChildListByParentId(eid, String.valueOf(dept.getId()));
            for (QueryDeptChildDTO deptChildDTO : childDTOS) {
                //设置上级区域的path，方便后续的追溯
                deptChildDTO.setPath(dept.getPath() + regionIdMap.get(deptChildDTO.getParentId()) + "/");
                RegionDO regionDO = new RegionDO();
                regionDO.setParentId(String.valueOf(regionIdMap.get(deptChildDTO.getParentId())));
                regionDO.setCreateTime(System.currentTimeMillis());
                regionDO.setName(deptChildDTO.getName());
                regionDO.setSynDingDeptId(String.valueOf(deptChildDTO.getId()));
                regionDO.setUpdateTime(System.currentTimeMillis());
                regionDO.setOrderNum(deptChildDTO.getDepartOrder());
                regionDO.setRegionType(RegionTypeEnum.PATH.getType());
                //次序值
                regionDO.setRegionPath(dept.getPath() + regionIdMap.get(deptChildDTO.getParentId()) + "/");
                regionDO.setDeleted(Boolean.FALSE);
                regionDOS.add(regionDO);
                syncDingDeptIds.add(String.valueOf(deptChildDTO.getId()));
            }
            //暂存该层级的子节点数据
            if (CollectionUtils.isNotEmpty(childDTOS)) {
                results.addAll(childDTOS);
            }
            if (regionDOS.size() > RECORD_MAX_SIZE) {
                //批量插入或更新
                regionService.batchInsertRegions(regionDOS, eid);
                regionDOS.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(regionDOS)) {
            //批量插入或更新
            regionService.batchInsertRegions(regionDOS, eid);
        }
        //递归调用
        if (CollectionUtils.isNotEmpty(results)) {
            //一次用完即清理
            regionIdMap.clear();
            //添加这一层的region的id和部门id映射关系
            regionIdMap.putAll(regionService.getRegionSynDeptIdAndIdMapping(eid, syncDingDeptIds));
            handlerSubRegions(eid, results, regionIdMap);
        }
    }

    /**
     * 执行企业端脚本
     * @param msg
     * @author: xugangkun
     * @return void
     * @date: 2022/2/11 15:34
     */
    public void runEnterpriseScript(EnterpriseOpenMsg msg) {
        DataSourceHelper.changeToSpecificDataSource(msg.getDbName());
        //执行脚本代码
        ClassPathResource rc = new ClassPathResource("script/enterpriseInit.sql");
        EncodedResource er = new EncodedResource(rc, "utf-8");
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("enterpriseId", msg.getEid());
        String userId = msg.getAuthUserId();
        if (AppTypeEnum.isQwType(msg.getAppType())) {
            userId = msg.getCorpId() + "_" + msg.getAuthUserId();
        }
        if(StringUtils.isBlank(userId)){
            objectObjectHashMap.put("userId", Constants.SYSTEM_USER_ID);
        }else {
            objectObjectHashMap.put("userId", userId);
        }
        String groupId = UUIDUtils.get32UUID();
        objectObjectHashMap.put("groupId", groupId);
        scriptUtil.executeSqlScript(er, objectObjectHashMap);
        log.info("初始化开通用户");
        if (StringUtils.isNotBlank(msg.getAuthUserId())) {
            //初始化开通用户
            initAuthUser(msg.getEid(), msg.getCorpId(), msg.getAppType(), msg.getAuthUserId(), userId, msg.getDbName());
        }
        //创建两条初始化任务
        log.info("初始化任务");
        initInitialTask(msg.getEid());
        //初始化证照信息
        try {
            log.info("初始化证照信息");
            licenseApiService.initLicense(msg.getEid(), null, msg.getDbName());
        } catch (Exception e) {
            log.error("initLicense error", e);
        } catch (Throwable e) {
            log.error("initLicense Throwable", e);
        }
    }

    /**
     * 初始化开通人以及ai用户
     * @param eid
     * @param corpId
     * @param appType
     * @param authUserId
     * @author: xugangkun
     * @return void
     * @date: 2022/2/11 15:50
     */
    public void initAuthUser(String eid, String corpId, String appType, String authUserId, String userId, String dbName) {
        List<EnterpriseUserRole> userRoles = new ArrayList<>();
        Long roleIdByRoleEnum = sysRoleService.getRoleIdByRoleEnum(eid, Role.MASTER.getRoleEnum());
        Long shopOwner = sysRoleService.getRoleIdByRoleEnum(eid, Role.SHOPOWNER.getRoleEnum());
        Long subMaster = sysRoleService.getRoleIdByRoleEnum(eid, Role.SUB_MASTER.getRoleEnum());
        //钉钉或者企业走用户，app开通不需要
        if (AppTypeEnum.isQwType(appType) || AppTypeEnum.isDingType(appType) || AppTypeEnum.FEI_SHU.getValue().equals(appType)) {
            EnterpriseUserDTO userDTO = null;
            try {
                userDTO = enterpriseInitConfigApiService.getUserDetailByUserId(corpId, authUserId, appType);
            } catch (Exception e) {
                log.error("getUserDetailByUserId error", e);
            }
            EnterpriseUserDO userDO = transUserDtoToDo(userDTO);
            userDO.setMainAdmin(true);
            userDO.setIsAdmin(true);
            enterpriseUserService.batchInsertOrUpdate(Collections.singletonList(userDO), eid);
        } else {
            //app 把开通人入库到企业库
            DataSourceHelper.reset();
            EnterpriseUserDO enterpriseUserDO = enterpriseUserService.selectConfigUserByUserId(userId);
            DataSourceHelper.changeToSpecificDataSource(dbName);
            if (Objects.nonNull(enterpriseUserDO)) {
                enterpriseUserService.batchInsertOrUpdate(Collections.singletonList(enterpriseUserDO), eid);
            }
        }
        userRoles.add(new EnterpriseUserRole(roleIdByRoleEnum.toString(), userId));
        userRoles.add(new EnterpriseUserRole(shopOwner.toString(), userId));
        userRoles.add(new EnterpriseUserRole(subMaster.toString(), userId));
        sysRoleService.insertBatchUserRole(eid, userRoles);
    }

    /**
     * 初始化初始测试任务
     * @param eid
     * @author: xugangkun
     * @return void
     * @date: 2022/2/14 15:52
     */
    public void initInitialTask(String eid) {
        try {
            CurrentUser currentUser = new CurrentUser();
            currentUser.setUserId(Constants.SYSTEM_USER_ID);
            currentUser.setName(Constants.SYSTEM_USER_NAME);
            TbMetaTableDO tableDO = tbMetaTableMapper.getInitTable(eid, UnifyTaskConstant.FormType.STANDARD);
            List<EnterpriseUserDO> enterpriseUserList = enterpriseUserMapper.getUserByAdmin(eid, true);
            List<EnterpriseUserDO> enterpriseUserListNew = ListUtils.emptyIfNull(enterpriseUserList)
                    .stream()
                    .filter(enterpriseUserDO -> !AIEnum.AI_USERID.getCode().equals(enterpriseUserDO.getUserId()))
                    .collect(Collectors.toList());
            //线下巡店任务
            if (Objects.nonNull(tableDO) && CollectionUtils.isNotEmpty(enterpriseUserListNew)) {
                initStoreOfflineTask(eid, tableDO, enterpriseUserListNew, currentUser);
            }
        } catch (Exception e) {
            log.error("initInitialTask error", e);
        }
    }


    public EnterpriseUserDO transUserDtoToDo(EnterpriseUserDTO enterpriseUserDTO) {
        EnterpriseUserDO enterpriseUserDO = new EnterpriseUserDO();
        enterpriseUserDO.setTel(enterpriseUserDTO.getTel());
        enterpriseUserDO.setRemark(enterpriseUserDTO.getRemark());
        enterpriseUserDO.setPosition(enterpriseUserDTO.getPosition());
        enterpriseUserDO.setMobile(enterpriseUserDTO.getMobile());
        enterpriseUserDO.setId(UUIDUtils.get32UUID());
        enterpriseUserDO.setUserId(enterpriseUserDTO.getUserId());
        enterpriseUserDO.setName(enterpriseUserDTO.getName());
        enterpriseUserDO.setEmail(enterpriseUserDTO.getEmail());
        enterpriseUserDO.setActive(Boolean.TRUE);
        enterpriseUserDO.setUnionid(enterpriseUserDTO.getUnionid());
        enterpriseUserDO.setAvatar(enterpriseUserDTO.getAvatar());
        enterpriseUserDO.setIsLeaderInDepts(JSONObject.toJSONString(enterpriseUserDTO.getIsLeaderInDepts()));
        enterpriseUserDO.setCreateTime(new Date());
        enterpriseUserDO.setJobnumber(enterpriseUserDTO.getJobnumber());
        return enterpriseUserDO;
    }

    private void initStoreOfflineTask(String eid, TbMetaTableDO tableDO, List<EnterpriseUserDO> enterpriseUserList, CurrentUser currentUser) {
        UnifyTaskBuildDTO task = new UnifyTaskBuildDTO();
        long createTime = System.currentTimeMillis();
        task.setBeginTime(createTime);
        task.setEndTime(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.of("+8")).toEpochMilli());
        task.setTaskName("测试体验巡店任务");
        task.setTaskType(TaskTypeEnum.PATROL_STORE_OFFLINE.getCode());
        task.setTaskPattern("NORMAL");
        task.setTaskInfo(JSONObject.toJSONString(tableDO));
        GeneralDTO store = new GeneralDTO();
        store.setType("store");
        store.setValue(Constants.DEFAULT_INIT_STORE_ID);
        task.setStoreIds(Collections.singletonList(store));
        task.setRunRule(TaskRunRuleEnum.ONCE.getCode());
        List<GeneralDTO> userList = new ArrayList<>();
        for(EnterpriseUserDO enterpriseUserDO : enterpriseUserList){
            if(AIEnum.AI_USERID.getCode().equals(enterpriseUserDO.getUserId())){
                continue;
            }
            userList.add(new GeneralDTO("person", enterpriseUserDO.getUserId()));
        }
        TaskProcessDTO taskProcessDTO = new TaskProcessDTO();
        taskProcessDTO.setNodeNo("1");
        taskProcessDTO.setApproveType("any");
        taskProcessDTO.setUser(userList);
        task.setProcess(Collections.singletonList(taskProcessDTO));
        task.setForm(Collections.singletonList(new GeneralDTO("STANDARD", String.valueOf(tableDO.getId()), tableDO.getTableName())));
        unifyTaskFcade.insertUnifyTask(eid, task, currentUser, createTime);
    }


    /**
     * 用户变更事件
     * @param param
     */
    public void userUpdateEvent(AddressBookChangeReqBody param){

    }
}
