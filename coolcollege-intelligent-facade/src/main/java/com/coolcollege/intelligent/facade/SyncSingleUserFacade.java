package com.coolcollege.intelligent.facade;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.common.util.sign.DeptInfoUtil;
import com.coolcollege.intelligent.common.util.sign.DeptStructData;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.facade.dto.SyncRequest;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserRole;
import com.coolcollege.intelligent.model.enterprise.dto.EntUserRoleDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.enums.UserAuthMappingSourceEnum;
import com.coolcollege.intelligent.model.enums.UserAuthMappingTypeEnum;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.dingSync.DingTalkClientService;
import com.coolcollege.intelligent.service.dingSync.DingUserSyncService;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.feishu.FeiShuSyncService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.qywxSync.QywxUserSyncService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 同步单一用户
 *
 * @ClassName: SyncSingleUserFacade
 * @Author: xugangkun
 * @Date: 2021/4/9 17:43
 */
@Service
@Slf4j
public class SyncSingleUserFacade {

    @Autowired
    private DingTalkClientService dingTalkClientService;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Autowired
    private DingUserSyncService dingUserSyncService;

    @Autowired
    private EnterpriseSettingService enterpriseSettingService;

    @Autowired
    private DingService dingService;

    @Autowired
    private QywxUserSyncService qywxUserSyncService;

    @Autowired
    private FeiShuSyncService feiShuSyncService;

    @Autowired
    private ChatService chatService;

    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private DeptInfoUtil deptInfoUtil;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    /**
     * 同步用户信息
     * @param userId
     * @param setting
     * @param isSyncRoleAndAuth 2021-5-6新增，用于判断是否需要同步用户的角色以及区域权限的信息
     * @author: xugangkun
     * @return void
     * @date:
     */
    public void syncUser(String userId, EnterpriseConfigDO conf, EnterpriseSettingVO setting, Boolean isSyncRoleAndAuth) throws ApiException {
        DataSourceHelper.reset();
        String eid = conf.getEnterpriseId();
        String dbName = conf.getDbName();
        //获得钉钉用户详情
        OapiV2UserGetResponse.UserGetResponse response = null;
        int maxTryTimes = 10;
        while (response == null && maxTryTimes-- > 0){
            try {
                String accessToken = enterpriseInitConfigApiService.getAccessToken(conf.getDingCorpId(), conf.getAppType());
                response = dingTalkClientService.getUserDetail(userId, accessToken);
            } catch (Exception e) {
                log.error("获取钉钉用户详情异常", e);
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException interruptedException) {

                }
            }
        }
        //先处理企业库
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //处理数据，封装用户的详细信息以及角色列表，还要判断该用户的可见权限， 如果用户是未激活，不做同步处理
        if (response == null) {
            return;
        }
        EnterpriseUserDO dingEnterpriseUser = dingUserSyncService.initEnterpriseUser(response, new EnterpriseUserDO(), eid);
        List<String> mainAdminUserIds = enterpriseInitConfigApiService.getAdminUserList(conf.getDingCorpId(), conf.getAppType());
        dingEnterpriseUser.setMainAdmin(mainAdminUserIds.contains(dingEnterpriseUser.getUserId()));
        EnterpriseUserDO coolEnterpriseUser = enterpriseUserDao.selectByUserIdIgnoreActive(eid, userId);
        if (coolEnterpriseUser != null) {
            //设置下级是否变动
            dingEnterpriseUser.setSubordinateChange(coolEnterpriseUser.getSubordinateChange());
            dingEnterpriseUser.setId(coolEnterpriseUser.getId());
            if (coolEnterpriseUser.getUserStatus() != null) {
                dingEnterpriseUser.setUserStatus(coolEnterpriseUser.getUserStatus());
            }
            if (StringUtils.isNotBlank(coolEnterpriseUser.getThirdOaUniqueFlag())) {
                dingEnterpriseUser.setThirdOaUniqueFlag(coolEnterpriseUser.getThirdOaUniqueFlag());
            }
        }
        dingUserSyncService.syncUser(response, dingEnterpriseUser, eid, setting, isSyncRoleAndAuth);
        //处理平台库
        DataSourceHelper.reset();
        EnterpriseUserDO coolConfigUser = enterpriseUserService.selectConfigUserByUnionid(dingEnterpriseUser.getUnionid());
        String configUserId = coolConfigUser == null ? UUIDUtils.get32UUID() : coolConfigUser.getId();
        dingEnterpriseUser.setId(configUserId);
        dingEnterpriseUser.setAppType(AppTypeEnum.DING_DING.getValue());
        dingUserSyncService.syncConfigUser(response, dingEnterpriseUser, eid, coolConfigUser == null);
    }

    /**
     *
     * @param userId
     * @param setting
     * @param isSyncRoleAndAuth
     * @throws ApiException
     */
    @Async("syncUserThreadPool")
    public void asyncUser(String userId, EnterpriseConfigDO config, EnterpriseSettingVO setting, Boolean isSyncRoleAndAuth) throws ApiException {
        this.syncUser(userId, config, setting, isSyncRoleAndAuth);
    }

    /**
     *
     * @param corpId
     * @param userId
     * @param accessToken
     * @param eid
     * @param dbName
     * @param appType
     */
    @Async("syncUserThreadPool")
    public void asyncQwUser(String corpId, String userId, String accessToken, String eid, String dbName, String appType) {
        qywxUserSyncService.syncWeComUser(corpId, userId, accessToken, eid, dbName, appType);
    }


    @Async("syncUserThreadPool")
    public void asyncFsUser(String corpId, String userId,  String eid, String dbName, String appType) {
        feiShuSyncService.syncFsUser(corpId, userId, eid, dbName, appType);
    }

    /**
     * 周大福部门信息
     * @param corpId
     * @param deptId
     * @param eid
     * @param dbName
     */
    public void asyncQwUserForZdf(String corpId, String deptId, String eid, String dbName) {
        log.info("开始部门管理员处理数据,deptId:{}", deptId);
        //处理管理员部门信息
        try {
            DeptStructData deptStructData = deptInfoUtil.getDeptInfo(deptId);
            if (deptStructData != null && StringUtils.isNotBlank(deptStructData.getManagerId())) {
                String userId = deptStructData.getManagerId();
                log.info("开始部门管理员处理数据管理员,deptId:{},userId:{}", deptId, userId);
                //周大福部门下主管，设置职位区域权限
                syncZdfUserDeptAuth(eid, corpId, dbName, userId, deptStructData);
            }
        } catch (Exception e) {
            log.error("部门管理员处理数据异常,deptId:{}", deptId, e);
        }

    }

    @Async("isvDingDingQwThreadPool")
    public void syncThirdUserAuth( String userId, String jobNumber,String thirdOaUniqueFlag,String eid,String dbName) {

        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<EntUserRoleDTO> userRoleDTOList = enterpriseUserRoleMapper.selectUserRoleByUserId(eid, userId);
        List<String> roleNameList = ListUtils.emptyIfNull(userRoleDTOList)
                .stream()
                .map(EntUserRoleDTO::getRoleName)
                .collect(Collectors.toList());
        SyncRequest syncRequest = SyncRequest.builder()
                .employeeCode(jobNumber)
                .enterpriseId(eid)
                .userId(userId)
                .thirdOaUniqueFlag(thirdOaUniqueFlag)
                .roleNameList(roleNameList)
                .build();
        simpleMessageService.send(JSONObject.toJSONString(syncRequest), RocketMqTagEnum.THIRD_OA_SYNC_SINGLE_QUEUE);
    }

    @Async("isvDingDingQwThreadPool")
    public void syncMyjUserAuth( String userId, String jobNumber,String thirdOaUniqueFlag,String eid,String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        SyncRequest syncRequest = SyncRequest.builder()
                .employeeCode(jobNumber)
                .enterpriseId(eid)
                .userId(userId)
                .thirdOaUniqueFlag(thirdOaUniqueFlag)
                .build();
        simpleMessageService.send(JSONObject.toJSONString(syncRequest), RocketMqTagEnum.THIRD_OA_SYNC_SINGLE_QUEUE);
    }


    /**
     * 门店通-同步用户(异步)
     * @param eid
     * @param dbName
     * @param userId
     * @param corpId
     * @param appType
     */
    @Async("isvDingDingQwThreadPool")
    public void asyncOnePartyUser(String eid, String dbName, String userId, String corpId, String appType) {
        try {
            this.syncOnePartyUser(eid, dbName, userId, corpId, appType, SyncConfig.OP_USER_CONTACT_SYNC_ALL);
        } catch (ApiException e) {
            log.error("syncOnePartyUser,当前用户同步失败 {} ", userId, e);
        }
    }

    /**
     * 门店通-同步用户
     *
     * @param eid
     * @param dbName
     * @param userId
     * @param corpId
     * @param appType
     * @param syncUserContactCode
     * @throws ApiException
     */
    public void syncOnePartyUser(String eid, String dbName, String userId, String corpId, String appType, Integer syncUserContactCode) throws ApiException {
        // 1.获得钉钉用户详情
        EnterpriseUserDTO dingEnterpriseUserDTO = null;
        try {
            dingEnterpriseUserDTO = enterpriseInitConfigApiService.getUserDetailByUserId(corpId, userId, appType);
        } catch (ApiException e) {
            log.error("syncOnePartyUser,当前用户同步失败并重试 {} ", userId, e);
            dingEnterpriseUserDTO = enterpriseInitConfigApiService.getUserDetailByUserId(corpId, userId, appType);
        }
        // 2.同步用户信息
        if(SyncConfig.OP_USER_CONTACT_SYNC_ALL == syncUserContactCode || SyncConfig.OP_USER_CONTACT_SYNC_INFO == syncUserContactCode) {
            this.syncUser(eid, dbName, userId, appType, dingEnterpriseUserDTO);
        }
        DataSourceHelper.changeToSpecificDataSource(dbName);
        // 3.同步所属区域
        if(SyncConfig.OP_USER_CONTACT_SYNC_ALL == syncUserContactCode || SyncConfig.OP_USER_CONTACT_SYNC_NODE == syncUserContactCode) {
            // 所属区域没有获取到，默认在根节点下
            List<String> storeList = CollectionUtils.emptyIfNull(dingEnterpriseUserDTO.getStoreList()).stream().map(String::valueOf).collect(Collectors.toList());
            dingUserSyncService.syncUserRegionMapping(eid, userId, storeList, Boolean.TRUE);
            enterpriseUserService.updateUserRegionPathList(eid, Collections.singletonList(userId));
        }
        // 4.同步权限范围
        if(SyncConfig.OP_USER_CONTACT_SYNC_ALL == syncUserContactCode || SyncConfig.OP_USER_CONTACT_SYNC_SCOPE == syncUserContactCode) {
            dingUserSyncService.syncDingOnePartyUserAuth(eid, userId, dingEnterpriseUserDTO.getScopeList());
        }
    }

    private void syncUser(String eid, String dbName, String userId, String appType, EnterpriseUserDTO dingEnterpriseUserDTO) {
        // 同步企业库用户
        DataSourceHelper.changeToSpecificDataSource(dbName);
        EnterpriseUserDO dingEnterpriseUser = dingUserSyncService.syncOnePartyUser(eid, userId, dingEnterpriseUserDTO);
        // 处理平台库(引用旧逻辑)
        DataSourceHelper.reset();
        EnterpriseUserDO coolConfigUser = enterpriseUserService.selectConfigUserByUnionid(dingEnterpriseUser.getUnionid());
        String configUserId = coolConfigUser == null ? UUIDUtils.get32UUID() : coolConfigUser.getId();
        dingEnterpriseUser.setId(configUserId);
        dingEnterpriseUser.setAppType(appType);
        dingUserSyncService.syncConfigUser(null, dingEnterpriseUser, eid, coolConfigUser == null);
    }

    /**
     * 初始化ai用户
     * @param eid
     * @param dbName
     */
    public void initAiUser(String eid, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        EnterpriseUserDO aiUser = enterpriseUserDao.selectByUserId(eid, AIEnum.AI_USERID.getCode());
        if(Objects.nonNull(aiUser)) {
            return;
        }
        EnterpriseUserDTO dingEnterpriseUserDTO = dingUserSyncService.initAiUser(eid);
        this.syncUser(eid, dbName, AIEnum.AI_USERID.getCode(), AppTypeEnum.DING_DING.getValue(), dingEnterpriseUserDTO);
    }

    private void syncZdfUserDeptAuth(String eid, String corpId, String dbName, String userId, DeptStructData deptStructData) {
        if (deptStructData == null) {
            return;
        }
        log.info("部门数据deptId:{},deptStructData#deptRange:{},deptRange:{}", deptStructData.getDeptId(), deptStructData.getDeptRange(), Constants.ZDF_DEPT_RANGE_SHOP_OWNER);

        userId = corpId + "_" + userId;
        EnterpriseUserDO userDO = enterpriseUserDao.selectByUserId(eid, userId);
        if (userDO == null) {
            log.error("部门管理员不存在,deptId:{},userId:{}", deptStructData.getDeptId(), userId);
            return;
        }
        // 同步企业库用户
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //1、查询部门列表时，判断部门的主管，在分店宝中，是否有该区域（门店）的权限，没有的化，就在分店宝上系统上该用户的管辖范围上加上
        RegionDO regionDO = regionMapper.selectBySynDingDeptId(eid, Long.valueOf(deptStructData.getDeptId()));
        if (regionDO != null) {
            UserAuthMappingDO userAuthMappingDO = userAuthMappingMapper.getUserAuthByUserIdAndMappingId(eid, userId, String.valueOf(regionDO.getId()), UserAuthMappingTypeEnum.REGION.getCode());
            if (userAuthMappingDO == null) {
                userAuthMappingDO = new UserAuthMappingDO(userId, String.valueOf(regionDO.getId()), UserAuthMappingTypeEnum.REGION.getCode()
                        , UserAuthMappingSourceEnum.SYNC.getCode(), Constants.SYSTEM_USER_NAME, System.currentTimeMillis());
                userAuthMappingMapper.insertUserAuthMapping(eid, userAuthMappingDO);
            }
        }

        //2、就是部门列表接口中当判断到部门范围是 6， 9，10， 11的时候时候，需要判断下这个主管用户 在分店宝上是否有 “小区主管的角色” 没有就加上。
        if (Constants.ZDF_DEPT_RANG_LIST.contains(deptStructData.getDeptRange())) {
            List<SysRoleDO> sysRoleList = sysRoleMapper.selectRoleByIdList(eid, Collections.singletonList(Constants.ZDF_MAIN_MANAGE_ROLE_ID));
            if (CollectionUtils.isNotEmpty(sysRoleList)) {
                EnterpriseUserRole userRole = enterpriseUserRoleMapper.selectByUserIdAndRoleId(eid, userId, Constants.ZDF_MAIN_MANAGE_ROLE_ID);
                if (userRole == null) {
                    userRole = new EnterpriseUserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(Constants.ZDF_MAIN_MANAGE_ROLE_ID);
                    userRole.setCreateTime(new Date());
                    userRole.setUpdateTime(new Date());
                    enterpriseUserRoleMapper.save(eid, userRole);
                }
            }
        }
        //3、部门范围为12，需要确认分店表中是否已经添加店长角色
        if (Constants.ZDF_DEPT_RANGE_SHOP_OWNER.equals(deptStructData.getDeptRange())) {
            log.info("处理时角色数据deptId:{},userId:{}", deptStructData.getDeptId(), userId);
            List<SysRoleDO> sysRoleList = sysRoleMapper.selectRoleByIdList(eid, Collections.singletonList(Role.SHOPOWNER.getId()));
            if (CollectionUtils.isNotEmpty(sysRoleList)) {
                EnterpriseUserRole userRole = enterpriseUserRoleMapper.selectByUserIdAndRoleId(eid, userId, Role.SHOPOWNER.getId());
                if (userRole == null) {
                    userRole = new EnterpriseUserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(Role.SHOPOWNER.getId());
                    userRole.setCreateTime(new Date());
                    userRole.setUpdateTime(new Date());
                    enterpriseUserRoleMapper.save(eid, userRole);
                }
            }
        }
    }
}
