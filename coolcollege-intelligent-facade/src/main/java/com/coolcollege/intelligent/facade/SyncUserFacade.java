package com.coolcollege.intelligent.facade;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.RoleSyncTypeEnum;
import com.coolcollege.intelligent.common.enums.ak.AkEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.position.PositionSourceEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.enums.role.CoolPositionTypeEnum;
import com.coolcollege.intelligent.common.enums.songxia.SongXiaEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.enums.user.UserTypeEnum;
import com.coolcollege.intelligent.common.enums.xfsg.XfsgEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.xfsg.XfsgRoleEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.ThirdDepartmentDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dto.AuthScopeDTO;
import com.coolcollege.intelligent.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.facade.dto.*;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.department.dto.SyncTreeNode;
import com.coolcollege.intelligent.model.enterprise.*;
import com.coolcollege.intelligent.model.enterprise.dto.EntUserRoleDTO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.enums.UserAuthMappingSourceEnum;
import com.coolcollege.intelligent.model.enums.UserAuthMappingTypeEnum;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.baili.ThirdOaDeptSyncService;
import com.coolcollege.intelligent.service.dingSync.DingDeptSyncService;
import com.coolcollege.intelligent.service.dingSync.DingTalkClientService;
import com.coolcollege.intelligent.service.dingSync.DingUserSyncService;
import com.coolcollege.intelligent.service.enterprise.*;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.dingtalk.api.response.OapiUserListidResponse;
import com.github.pagehelper.PageHelper;
import com.taobao.api.ApiException;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户信息同步
 *
 * @ClassName: SyncUserFacade
 * @Author: xugangkun
 * @Date: 2021/3/23 10:39
 */
@Service
@Slf4j
public class SyncUserFacade {

    @Autowired
    private DingTalkClientService dingTalkClientService;

    @Autowired
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Autowired
    private EnterpriseUserService enterpriseUserService;
    @Autowired
    private DingUserSyncService dingUserSyncService;
    @Autowired
    private RedisUtilPool redisUtilPool;
    @Lazy
    @Autowired
    private SyncDeptFacade syncDeptFacade;
    @Autowired
    private SysDepartmentService sysDepartmentService;
    @Autowired
    private EnterpriseOperateLogService enterpriseOperateLogService;
    @Autowired
    private EnterpriseSettingService enterpriseSettingService;
    @Autowired
    private SyncRoleFacade syncRoleFacade;
    @Autowired
    private DingService dingService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private RedisConstantUtil redisConstantUtil;
    @Autowired
    private SyncSingleUserFacade syncSingleUserFacade;
    @Autowired
    private DingDeptSyncService dingDeptSyncService;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Autowired
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;
    @Autowired
    private ThirdOaDeptSyncService thirdOaDeptSyncService;
    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private FsService fsService;
    @Autowired
    private UserRegionMappingService userRegionMappingService;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;
    private final Integer PAGE_SIZE = 200;
    private final Integer PAGE_MAX_SIZE = 200000;
    @Resource
    private SyncStoreFacade syncStoreFacade;
    @Resource
    private ThirdDepartmentDao thirdDepartmentDao;
    @Resource
    private EnterpriseMapper enterpriseMapper;
    @Lazy
    @Autowired
    private NewSyncFacade newSyncFacade;

    /**
     * 同步部门下用户
     * @param eid
     * @param dbName
     * @param dingUserIdList
     * @param setting
     * @param deptId
     * @param isSyncRoleAndAuth
     * @date: 2021/5/6 15:27
     * @return void
     * @author: xugangkun
     */
    private Boolean syncDeptUser(String corpId, String eid, String dbName, Set<String> dingUserIdList,
                                 EnterpriseSettingVO setting, String deptId,
                                 Boolean isSyncRoleAndAuth, EnterpriseConfigDO config) throws ApiException {
        String appType = config.getAppType();
        List<String> deptUserIds = new ArrayList<>();
        int maxTryTimes = 2;
        while (maxTryTimes-- > 0) {
            try {
                String accessToken = enterpriseInitConfigApiService.getAccessToken(corpId, appType);
                OapiUserListidResponse.ListUserByDeptResponse response = dingTalkClientService.getDeptUserIdList(deptId, accessToken);
                if (CollectionUtils.isEmpty(response.getUseridList())) {
                    log.info("获取部门下用户id {},返回用户id为null", deptId);
                    return true;
                }
                deptUserIds = response.getUseridList();
                dingUserIdList.addAll(response.getUseridList());
                log.info("获取部门下用户id {} ,返回用户id {} ", deptId, response.getUseridList());
                break;
            } catch (Exception e) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException interruptedException) {

                }
            }
        }
        for (String userId : deptUserIds) {
            if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(appType)) {
                syncSingleUserFacade.asyncOnePartyUser(eid, dbName, userId, corpId, appType);
            }else{
                syncSingleUserFacade.asyncUser(userId, config, setting, isSyncRoleAndAuth);
            }
        }
        return false;
    }

    private void syncFsDeptUser(String corpId, String eid, String dbName, Set<String> dingUserIdList, String deptId, String appType){
        List<EnterpriseUserRequest> deptUsers =fsService.getDeptUsers(corpId, deptId, appType);
        log.info("获取飞书部门下用户的部门id {} ,返回用户详情列表 {} ", deptId, JSONObject.toJSONString(deptUsers));
        if (CollectionUtils.isEmpty(deptUsers)){
            return;
        }
        for (EnterpriseUserRequest deptUser : deptUsers) {
            if (Objects.nonNull(deptUser.getEnterpriseUserDO())) {
                dingUserIdList.add(deptUser.getEnterpriseUserDO().getUserId());
                try {
                    String userId = deptUser.getEnterpriseUserDO().getUserId();
                    syncSingleUserFacade.asyncFsUser(corpId, userId, eid, dbName, appType);
                } catch (Exception e) {
                    log.error("syncFsDeptUser,当前用户同步失败 {} ", deptUser.getEnterpriseUserDO().getUserId(), e);
                }
            }
        }
    }

    /**
     * 同步企微部门下用户
     * @param eid
     * @param dbName
     * @param dingUserIdList 这次同步的用户id列表
     * @param deptId
     * @date: 2021/10/28 15:27
     * @return void
     * @author: xugangkun
     */
    private void syncQwDeptUser(String corpId, String eid, String dbName, Set<String> dingUserIdList, String deptId, String appType) throws ApiException {
        String accessToken = null;
        try {
            accessToken = enterpriseInitConfigApiService.getAccessToken(corpId, appType);
        } catch (ApiException e) {
            //重试
            accessToken = enterpriseInitConfigApiService.getAccessToken(corpId, appType);
        }
        boolean flag = chatService.checkWxCorpIdFromRedis(corpId);
        List<EnterpriseUserRequest> deptUsers = new ArrayList<>();
        try {
            deptUsers = chatService.getDeptUsers(corpId, deptId, accessToken, flag, appType);
        } catch (ApiException e) {
            //如果是token失效，再获取一次
            if (Constants.DING_TOKEN_INVALID_CODE.equals(e.getErrCode())) {
                accessToken = enterpriseInitConfigApiService.getAccessToken(corpId, appType);
                try {
                    deptUsers = chatService.getDeptUsers(corpId, deptId, accessToken, flag, appType);
                } catch (Exception e1) {
                    log.error("fullSyncUser,当前部门用户同步失败 {} ", deptId, e1);
                }
            } else {
                log.error("fullSyncUser,当前部门用户同步失败, 停止企微同步 {} ", deptId, e);
                //抛出异常，停止此次企微同步，不然dingUserIdList没有新数据，最后会删除这个部门下的用户
                throw new ApiException(e);
            }
        }
        log.info("获取部门下用户的部门id {} ,返回用户详情列表 {} ", deptId, JSONObject.toJSONString(deptUsers));
        for (EnterpriseUserRequest deptUser : deptUsers) {
            if (Objects.nonNull(deptUser.getEnterpriseUserDO())) {
                dingUserIdList.add(deptUser.getEnterpriseUserDO().getUserId());
                try {
                    //切除获得userId corpId之外布冯半部
                    accessToken = enterpriseInitConfigApiService.getAccessToken(corpId, appType);
                    String userId = deptUser.getEnterpriseUserDO().getUserId().substring(corpId.length() + 1);
                    syncSingleUserFacade.asyncQwUser(corpId, userId, accessToken, eid, dbName, appType);
                } catch (Exception e) {
                    log.error("fullSyncUser,当前用户同步失败 {} ", deptUser.getEnterpriseUserDO().getUserId(), e);
                }
            }
        }
    }

    /**
     * 同步删除用户(员工离职事件)
     * @param eid
     * @param userId
     */
    public void syncDeleteUser(String eid, String userId, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //松下不做处理
        if(SongXiaEnterpriseEnum.songXiaCompany(eid)){
            return;
        }
        //删除企业库对应的映射关系
        dingUserSyncService.syncDeleteUser(userId, eid);
        //处理平台库
        DataSourceHelper.reset();
        //删除平台库对应的映射关系
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        dingUserSyncService.syncDeleteConfigUser(eid, userIds);
    }

    /**
     * 单个同步百丽人员权限
     * @author chenyupeng
     * @date 2021/8/17
     * @param eid
     * @return void
     */
    public void syncThirdOaSingleUserAuth(String eid, String userId, List<String> unitIdList, List<Long> orgIds) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        EnterpriseUserDO enterpriseUser = enterpriseUserDao.selectByUserId(eid,userId);
        if(Objects.isNull(enterpriseUser)){
            return;
        }
        List<UserAuthMappingDO> userAuthMappingList =  getSyncAuthMappingListBaili(eid, unitIdList,userId, enterpriseUser);
        //用户在cool中已有的可视范围映射关系
        List<UserAuthMappingDO> coolUserAuthList = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userId);
        Map<String, UserAuthMappingDO> userAuthMap = CollectionUtils.isEmpty(coolUserAuthList) ? null :
                coolUserAuthList.stream().collect(Collectors.toMap(UserAuthMappingDO::getMappingId, data -> data, (a, b) -> a));

        userAuthMappingList.forEach(userAuth -> {
            UserAuthMappingDO auth = userAuthMap == null ? null : userAuthMap.get(userAuth.getMappingId());
            if (auth == null) {
                userAuthMappingMapper.insertUserAuthMapping(eid, userAuth);
            } else {
                coolUserAuthList.remove(auth);
            }
        });
        if (CollectionUtils.isNotEmpty(coolUserAuthList)) {
            List<Long> deleteAuthIds = coolUserAuthList.stream()
                    .filter(data -> UserAuthMappingSourceEnum.SYNC.getCode().equals(data.getSource()) )
                    .map(UserAuthMappingDO::getId).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(deleteAuthIds)){
                userAuthMappingMapper.deleteAuthMappingByIds(eid, deleteAuthIds);
            }
        }
        //处理用户节点关系
        userRegionMappingService.dealUserRegionMappingBySynDingDeptId(eid, userId, orgIds);
        enterpriseUserService.updateUserRegionPathList(eid, Arrays.asList(userId));
    }

    /**
     * 单个同步森宇OA用户权限
     * @author wxp
     * @date 2021/9/8
     * @return void
     */
    public void syncSenYuSingleUserAuth(String eid,String thirdOaUniqueFlag,String userId,List<String> kehbmList,
                                        String userName, String roleCode, String roleName, String mobile, Boolean active, String parentThirdOaUniqueFlag) {
        if(StringUtil.isEmpty(thirdOaUniqueFlag)){
            log.info("身份证号码为空，无法进行同步");
            return;
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        // 修改用户信息
        EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserIdIgnoreActive(eid, userId);
        enterpriseUserDO.setName(userName);
        enterpriseUserDO.setMobile(mobile);
        enterpriseUserDO.setActive(active);
        enterpriseUserDao.updateEnterpriseUser(eid, enterpriseUserDO);
        // 身份证对应的区域
        RegionDO myRegionDO = regionMapper.getBySynDingDeptId(eid, thirdOaUniqueFlag);

        // 同步森宇角色，及用户角色关系
        syncSenYuUserRole(eid, roleName, userId);

        //用户在cool中已有的可视范围映射关系
        List<UserAuthMappingDO> coolUserAuthList = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userId);
        Map<String, UserAuthMappingDO> userAuthMap = CollectionUtils.isEmpty(coolUserAuthList) ? null :
                coolUserAuthList.stream().collect(Collectors.toMap(UserAuthMappingDO::getMappingId, data -> data, (a, b) -> a));
        // 促销员
        if(Constants.SENYU_ROLE_PROMOTION.equals(roleCode)){
            List<UserAuthMappingDO> userAuthMappingList =  getSyncAuthMappingListSenYu(eid, kehbmList, thirdOaUniqueFlag);
            userAuthMappingList.forEach(userAuth -> {
                UserAuthMappingDO auth = userAuthMap == null ? null : userAuthMap.get(userAuth.getMappingId());
                if (auth == null) {
                    userAuthMappingMapper.insertUserAuthMapping(eid, userAuth);
                } else {
                    // 存在移除list
                    coolUserAuthList.remove(auth);
                }
            });
        }else {
            // 非促销员
            if(enterpriseUserDO != null && myRegionDO != null){
                UserAuthMappingDO auth = userAuthMap == null ? null : userAuthMap.get(myRegionDO.getId());
                if(auth == null){
                    auth = new UserAuthMappingDO(enterpriseUserDO.getUserId(), myRegionDO.getId().toString(), UserAuthMappingTypeEnum.REGION.getCode()
                            , UserAuthMappingSourceEnum.SYNC.getCode(), Constants.SYSTEM_USER_NAME, System.currentTimeMillis());
                    userAuthMappingMapper.insertUserAuthMapping(eid, auth);
                } else {
                    // 存在移除list
                    coolUserAuthList.remove(auth);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(coolUserAuthList)) {
            List<Long> deleteAuthIds = coolUserAuthList.stream()
                    .filter(data -> UserAuthMappingSourceEnum.SYNC.getCode().equals(data.getSource()) )
                    .map(UserAuthMappingDO::getId).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(deleteAuthIds)){
                userAuthMappingMapper.deleteAuthMappingByIds(eid, deleteAuthIds);
            }
        }
        // 给上级赋区域权限
       if(StringUtils.isNotEmpty(parentThirdOaUniqueFlag)){
           EnterpriseUserDO parentEnterpriseUser = enterpriseUserDao.selectByThirdOaUniqueFlag(eid, parentThirdOaUniqueFlag);
           if(parentEnterpriseUser != null && myRegionDO != null){
               UserAuthMappingDO parentMappingDO = userAuthMappingMapper.getUserAuthByUserIdAndMappingId(eid, parentEnterpriseUser.getUserId(), myRegionDO.getId().toString(), UserAuthMappingTypeEnum.REGION.getCode());
               if(parentMappingDO == null){
                   parentMappingDO = new UserAuthMappingDO(parentEnterpriseUser.getUserId(), myRegionDO.getId().toString(), UserAuthMappingTypeEnum.REGION.getCode()
                           , UserAuthMappingSourceEnum.SYNC.getCode(), Constants.SYSTEM_USER_NAME, System.currentTimeMillis());
                   userAuthMappingMapper.insertUserAuthMapping(eid, parentMappingDO);
               }
           }
       }

        DataSourceHelper.reset();
        EnterpriseUserDO update = new EnterpriseUserDO();
        update.setUnionid(enterpriseUserDO.getUnionid());
        update.setName(userName);
        update.setMobile(mobile);
        if(StringUtils.isNotBlank(thirdOaUniqueFlag)){
            update.setThirdOaUniqueFlag(thirdOaUniqueFlag);
        }
        update.setActive(active);
        enterpriseUserDao.updateConfigEnterpriseUserByUnionId(update);

    }

    // 同步森宇角色，及用户角色关系
    private void syncSenYuUserRole(String eid, String position, String userId) {

        //判断系统中是否有该角色，没有，添加角色
        List<SysRoleDO> titleList = sysRoleMapper.selectByRoleNameAndSource(eid, position, PositionSourceEnum.CREATE.getValue());
        if (titleList == null || titleList.size() == 0) {
            //库中无该职位信息，添加
            SysRoleDO sysRoleDO = new SysRoleDO();
            sysRoleDO.setId(System.currentTimeMillis() + new Random().nextInt(1000))
                    .setRoleName(position)
                    .setRoleAuth(AuthRoleEnum.PERSONAL.getCode())
                    .setSource(PositionSourceEnum.CREATE.getValue())
                    .setPositionType(CoolPositionTypeEnum.STORE_OUTSIDE.getCode())
                    .setPriority(sysRoleService.getNormalRoleMaxPriority(eid) + 10)
                    .setCreateTime(new Date())
                    .setCreateUser(AIEnum.AI_USERID.getCode());
            sysRoleMapper.addSystemRole(eid, sysRoleDO);
            // 给新增角色初始化移动端菜单
            try {
                sysRoleService.initMenuWhenSyncRole(eid,sysRoleDO.getId());
            } catch (Exception e) {
                log.error("syncSenYuUserRole给新增角色初始化移动端菜单,企业id:{},角色Id:{}", eid, sysRoleDO.getId(), e);
            }
            titleList.add(sysRoleDO);
        }
        EnterpriseUserRole exsitUserRole = enterpriseUserRoleMapper.selectByUserIdAndRoleId(eid, userId, titleList.get(0).getId().toString());
        if(exsitUserRole == null){
            enterpriseUserRoleMapper.save(eid, new EnterpriseUserRole(titleList.get(0).getId().toString(), userId, RoleSyncTypeEnum.SYNC.getCode()));
        }

    }

    /**
     * 获取权限
     * @author chenyupeng
     * @date 2021/8/16
     * @param eid
     * @param unitIdList
     * @param userId
     * @return java.util.List<com.coolcollege.intelligent.model.authentication.UserAuthMappingDO>
     */
    private List<UserAuthMappingDO> getSyncAuthMappingListBaili(String eid, List<String> unitIdList, String userId, EnterpriseUserDO enterpriseUser){
        if(CollectionUtils.isEmpty(unitIdList) || StringUtil.isEmpty(userId)){
            log.info("组织id或userId为空，unitId:{},employeeCode:{}",unitIdList,userId);
            return new ArrayList<>();
        }
        List<RegionDO> regions = regionMapper.getRegionByDingDeptIds(eid, unitIdList);
        //添加人员权限映射表，即用户可见范围
        List<UserAuthMappingDO> userAuthMappingDOList = new ArrayList<>();
        //用户在该区域(部门)是管理员时，拥有可见权限
        regions.forEach(region -> {
            //通过工号查询用户
            UserAuthMappingDO mappingDO = new UserAuthMappingDO(enterpriseUser.getUserId(), region.getId().toString(), UserAuthMappingTypeEnum.REGION.getCode()
                    , UserAuthMappingSourceEnum.SYNC.getCode(), Constants.SYSTEM_USER_NAME, System.currentTimeMillis());
            userAuthMappingDOList.add(mappingDO);
        });
        return userAuthMappingDOList;
    }


    /**
     * 根据门店编码获取森宇已同步过来的门店权限
     * @author wxp
     * @date 2021/9/8
     * @param eid
     * @param kehbmList
     * @param thirdOaUniqueFlag
     * @return java.util.List<com.coolcollege.intelligent.model.authentication.UserAuthMappingDO>
     */
    private List<UserAuthMappingDO> getSyncAuthMappingListSenYu(String eid, List<String> kehbmList, String thirdOaUniqueFlag){
        if(CollectionUtils.isEmpty(kehbmList) || StringUtil.isEmpty(thirdOaUniqueFlag)){
            return new ArrayList<>();
        }
        List<StoreDO> storeList = storeMapper.getStoreByDingDeptIds(eid, kehbmList);
        //添加人员权限映射表，即用户可见范围
        List<UserAuthMappingDO> userAuthMappingDOList = new ArrayList<>();
        //用户在该区域(部门)是管理员时，拥有可见权限

        //通过身份证号码查询用户
        EnterpriseUserDO enterpriseUser = enterpriseUserDao.selectByThirdOaUniqueFlag(eid,thirdOaUniqueFlag);
        storeList.forEach(storeDO -> {
            RegionDO regionDO = regionMapper.getByStoreId(eid, storeDO.getStoreId());
            UserAuthMappingDO mappingDO;
            if(regionDO != null){
                mappingDO  = new UserAuthMappingDO(enterpriseUser.getUserId(), String.valueOf(regionDO.getId()), UserAuthMappingTypeEnum.REGION.getCode()
                        , UserAuthMappingSourceEnum.SYNC.getCode(), Constants.SYSTEM_USER_NAME, System.currentTimeMillis());
            }else {
                mappingDO  = new UserAuthMappingDO(enterpriseUser.getUserId(), storeDO.getStoreId(), UserAuthMappingTypeEnum.STORE.getCode()
                        , UserAuthMappingSourceEnum.SYNC.getCode(), Constants.SYSTEM_USER_NAME, System.currentTimeMillis());
            }
            userAuthMappingDOList.add(mappingDO);
        });
        return userAuthMappingDOList;
    }

    public void syncUserAndAuthOa(SyncAllRequest request){
        String eid=request.getEnterpriseId();
        String unitId=request.getUnitId();
        Long logId=request.getLogId();

        List<RegionDTO> resultList = request.getDeptList();
            DataSourceHelper.reset();
            EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
            String dbName=config.getDbName();
            if(logId == null){
                EnterpriseOperateLogDO logDO = EnterpriseOperateLogDO.builder().enterpriseId(eid).operateDesc("第三方OA优化同步")
                        .operateType(SyncConfig.ENTERPRISE_OPERATE_LOG_SYNC).operateStartTime(new Date()).userName(request.getUserName()).createTime(new Date())
                        .status(SyncConfig.SYNC_STATUS_ONGOING).userId(request.getUserId()).build();
                enterpriseOperateLogService.insert(logDO);
                logId = logDO.getId();
            }
            try {

                DataSourceHelper.changeToSpecificDataSource(dbName);
                //同步第三方OA组织架构
                thirdOaDeptSyncService.syncOrgAll(eid,unitId, resultList);
                //分页查询人员信息并同步其人员权限权限
                syncThirdUserAuth(eid,dbName);
                DataSourceHelper.reset();
                enterpriseOperateLogService.updateStatusAndOperateEndTimeById(SyncConfig.SYNC_STATUS_SUCCESS, new Date(), null, logId);
            }catch (Exception exception){
                String errMsg = null;
                if(exception instanceof ServiceException){
                    errMsg = ((ServiceException) exception).getErrorMessage();
                }
                if(exception instanceof DuplicateKeyException){
                    errMsg = "####DuplicateKeyException ";
                }
                if(exception instanceof ApiException){
                    errMsg = ((ApiException) exception).getErrMsg();
                }
                //同步失败删除拦截key
                redisUtilPool.delKey(redisConstantUtil.getSyncEidEffectiveKey(eid));
                log.error("第三方OA优化同步失败 eid : {}", eid, exception);
                //失败
                DataSourceHelper.reset();
                enterpriseOperateLogService.updateStatusAndOperateEndTimeById(SyncConfig.SYNC_STATUS_FAIL, new Date(), errMsg, logId);
            }finally {
                //任务无论完成失败，都删除区域和门店缓存
                String regionKey = redisConstantUtil.getSyncRegionKey(eid);
                String storeKey = redisConstantUtil.getSyncStoreKey(eid);
                redisUtilPool.delKey(regionKey);
                redisUtilPool.delKey(storeKey);
            }
    }
    public void syncThirdOaAll(String eid, String userName, String userId, EnterpriseConfigDO enterpriseConfig ,String unitId, List<RegionDTO> resultList, Long logId,Long regionId, List<ThirdDepartmentDTO> thirdDeptList){
        String corpId = enterpriseConfig.getDingCorpId();
        String dbName = enterpriseConfig.getDbName();
        DataSourceHelper.reset();
        EnterpriseDO enterprise = enterpriseMapper.selectById(eid);
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        if(logId == null){
            EnterpriseOperateLogDO logDO = EnterpriseOperateLogDO.builder().enterpriseId(eid).operateDesc("第三方OA同步")
                    .operateType(SyncConfig.ENTERPRISE_OPERATE_LOG_SYNC).operateStartTime(new Date()).userName(userName).createTime(new Date())
                    .status(SyncConfig.SYNC_STATUS_ONGOING).userId(userId).build();
            enterpriseOperateLogService.insert(logDO);
            logId = logDO.getId();
        }
        log.info("startSync:corpId:{},regionId:{}",corpId,regionId);
        try {
            //如果区域不为null的时候，只全量同步人员,不同步部门等
            String remark = "";
            if (regionId!=null){
                syncSpecifyNodeUser(eid, null, true, enterpriseConfig, enterpriseSettingVO);
                remark = "同步用户成功";
            }else {
                DataSourceHelper.changeToSpecificDataSource(dbName);
                //同步百丽组织架构
                thirdOaDeptSyncService.syncOrgAll(eid,unitId, resultList);
                // 同步鲜丰水果ehr部门
                syncXfsgEhrDepartment(eid, thirdDeptList);
                //同步部门
                syncDeptFacade.syncDept(enterpriseConfig, enterprise, null, logId);
                //同步角色
                syncRoleFacade.syncDingRoles(eid);
                //同步用户职位
                syncSpecifyNodeUser(eid, null, true, enterpriseConfig, enterpriseSettingVO);
                remark = "全量同步成功";
            }
            //成功
            DataSourceHelper.reset();
            enterpriseOperateLogService.updateStatusAndOperateEndTimeById(SyncConfig.SYNC_STATUS_SUCCESS, new Date(), remark, logId);
        }catch (Exception e){
            String errMsg = null;
            if(e instanceof ServiceException){
                errMsg = ((ServiceException) e).getErrorMessage();
            }
            if(e instanceof DuplicateKeyException){
                errMsg = "DuplicateKeyException ";
            }
            if(e instanceof ApiException){
                errMsg = ((ApiException) e).getErrMsg();
            }
            //同步失败删除拦截key
            redisUtilPool.delKey(redisConstantUtil.getSyncEidEffectiveKey(eid));
            log.error("同步钉钉失败 eid : {}, {}", eid, e);
            //失败
            DataSourceHelper.reset();
            enterpriseOperateLogService.updateStatusAndOperateEndTimeById(SyncConfig.SYNC_STATUS_FAIL, new Date(), errMsg, logId);
        }finally {
            //无论是否失败，删除节点同步锁
            redisUtilPool.delKey(redisConstantUtil.getSyncLockKey(eid));
            //任务无论完成失败，都删除区域和门店缓存
            String regionKey = redisConstantUtil.getSyncRegionKey(eid);
            String storeKey = redisConstantUtil.getSyncStoreKey(eid);
            redisUtilPool.delKey(regionKey);
            redisUtilPool.delKey(storeKey);
        }
    }

    private void syncXfsgEhrDepartment(String eid, List<ThirdDepartmentDTO> thirdDeptList) {
        if (XfsgEnterpriseEnum.xfsgCompany(eid)) {
            List<ThirdDepartmentDO> thirdDepartments = ListUtils.emptyIfNull(thirdDeptList).stream().map(e -> {
                ThirdDepartmentDO thirdDepartmentDO = new ThirdDepartmentDO();
                BeanUtils.copyProperties(e, thirdDepartmentDO);
                return thirdDepartmentDO;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(thirdDepartments)) {
                List<ThirdDepartmentDO> exsitThirdDepartment = thirdDepartmentDao.listAllThirdDepartment(eid);
                com.google.common.collect.Lists.partition(thirdDepartments, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
                    thirdDepartmentDao.batchInsertOrUpdate(eid, p);
                });
                List<String> addDepartmentCodes = thirdDepartments.stream().map(ThirdDepartmentDO::getDepartmentCode).collect(Collectors.toList());
                List<String> delDepartmentCodes = exsitThirdDepartment.stream().filter(t -> !addDepartmentCodes.contains(t.getDepartmentCode()))
                        .map(ThirdDepartmentDO::getDepartmentCode).collect(Collectors.toList());
                thirdDepartmentDao.deleteThirdDepartment(eid, delDepartmentCodes);
            }
        }
    }

    private void syncThirdUserAuth(String eid,String dbName) {
        Integer pages = (PAGE_MAX_SIZE + PAGE_SIZE - 1) / PAGE_SIZE;
        for (int curPage = 1; curPage <= pages; curPage++) {
            PageHelper.startPage(curPage, PAGE_SIZE);
            List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserDao.selectUsersByStatusAndUserIds(eid, null, UserStatusEnum.NORMAL.getCode(), null);
            if (CollectionUtils.isEmpty(enterpriseUserDOList)) {
                break;
            }
            if (CollectionUtils.isNotEmpty(enterpriseUserDOList)) {
                enterpriseUserDOList.forEach(user -> {
                    syncSingleUserFacade.syncThirdUserAuth(user.getUserId(), user.getJobnumber(), user.getThirdOaUniqueFlag(), eid, dbName);
                });
            }
        }
    }

    public void syncBailiUserRegion(String eid, String userId, List<Long> orgIds) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        userRegionMappingService.deletedUserRegionMappingByUserIds(eid, userId);
        EnterpriseUserDO enterpriseUser = enterpriseUserDao.selectByUserId(eid, userId);
        if(Objects.isNull(enterpriseUser)){
            return;
        }
        //处理用户节点关系
        userRegionMappingService.dealUserRegionMappingBySynDingDeptId(eid, userId, orgIds);
        enterpriseUserService.updateUserRegionPathList(eid, Arrays.asList(userId));
    }

    /**
     * 自建应用同步用户名称
     * @param corpId
     * @param eid
     * @param dbName
     * @param dingUserIdList
     * @param deptId
     * @param appType
     * @throws ApiException
     */
    public void syncQwSelfDeptUser(String corpId, String eid, String dbName, Set<String> dingUserIdList, String deptId, String appType) throws ApiException {
        List<EnterpriseUserDTO> userList = enterpriseInitConfigApiService.getDepartmentUsers(corpId, deptId, appType);
        log.info("获取部门下用户的部门corpId:{},deptId:{}", corpId, deptId);
        for (EnterpriseUserDTO deptUser : userList) {
            if (Objects.nonNull(deptUser)) {
                dingUserIdList.add(deptUser.getUserId());
                try {
                    //切除获得userId corpId之外布冯半部
                    String userId = deptUser.getUserId().substring(corpId.length() + 1);
                    syncSingleUserFacade.asyncQwUser(corpId, userId, null, eid, dbName, appType);
                } catch (Exception e) {
                    log.error("syncQwSelfDeptUser,当前用户同步失败deptId:{},userId:{}", deptId,deptUser.getUserId(), e);
                    throw new ServiceException(ErrorCodeEnum.QW_SERVICE_DEPT_SYNC_EXCEPTION);
                }
            }
        }
    }

    private void syncAuthUser(String corpId, EnterpriseConfigDO config, Set<String> dingUserIdList,EnterpriseSettingVO setting, Boolean isSyncRoleAndAuth) throws ApiException {
        String appType = config.getAppType();
        String dbName = config.getDbName();
        String eid = config.getEnterpriseId();
        AuthScopeDTO authScopeDTO = enterpriseInitConfigApiService.getAuthScope(corpId, appType);
        if(authScopeDTO == null || CollectionUtils.isEmpty(authScopeDTO.getUserIdList())){
            log.info("syncQwSelfAuthUser授权用户为空corpId:{},appType:{}", corpId, appType);
            return;
        }
        for (String userId : authScopeDTO.getUserIdList()) {
            if (Objects.nonNull(userId)) {
                dingUserIdList.add(userId);
                try {
                    if(AppTypeEnum.isWxSelfAndPrivateType(appType)) {
                        dingUserIdList.add(corpId + "_" + userId);
                        //切除获得userId corpId之外布冯半部
                        syncSingleUserFacade.asyncQwUser(corpId, userId, null, eid, dbName, appType);
                    }else if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(appType)){
                        // 门店通同步授权范围用户
                        syncSingleUserFacade.asyncOnePartyUser(eid, dbName, userId, corpId, appType);
                    } else if (AppTypeEnum.isDingType(appType)) {
                        log.info("同步授权范围内的用户corpId:{},appType:{},userId:{}", corpId, appType, userId);
                        syncSingleUserFacade.asyncUser(userId, config, setting, isSyncRoleAndAuth);
                    } else if (AppTypeEnum.isFsType(appType)) {
                        log.info("飞书同步授权范围内的用户corpId:{},appType:{},userId:{}", corpId, appType, userId);
                        syncSingleUserFacade.asyncFsUser(corpId, userId, eid, dbName, appType);
                    }
                } catch (Exception e) {
                    log.error("syncQwSelfAuthUser,当前用户同步失败userId:{}", userId, e);
                }
            }
        }
    }

    /**
     * 门店通-钉钉全量同步
     * @param eid
     * @param userName
     * @param userId
     * @param dingCorpId
     * @param dbName
     */
    public void syncAllForOneParty(String eid, String userName, String userId, String dingCorpId, String dbName) {
        DataSourceHelper.reset();
        EnterpriseOperateLogDO logDO = EnterpriseOperateLogDO.builder().enterpriseId(eid).operateDesc("钉钉-门店通应用同步")
                .operateType(SyncConfig.ENTERPRISE_OPERATE_LOG_SYNC).operateStartTime(new Date()).userName(userName).createTime(new Date())
                .status(SyncConfig.SYNC_STATUS_ONGOING).userId(userId).build();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(eid);
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        enterpriseOperateLogService.insert(logDO);
        try {
            DataSourceHelper.changeToSpecificDataSource(dbName);
            // 1.部门（部分旧业务通用）
            syncDeptFacade.syncDingOnePartyDept(eid, dingCorpId);
            // 2.区域&门店（新）
            syncStoreFacade.syncDingOnePartyStoreAndRegion(eid, dingCorpId, userId, userName, enterpriseSettingVO);
            // 3.同步门店分组（新）
            // 不从一方同步门店分组(2023-09-12)
//            syncStoreFacade.syncDingOnePartyStoreGroup(eid, dingCorpId, userId, userName);
            // 4.同步职位信息（新）  奥康企业 不做全量职位同步  只做单个职位监听  全量会导致ehr职位重复
            if(!AkEnterpriseEnum.aokangAffiliatedCompany(eid)){
                syncRoleFacade.syncDingOnePartyRoles(eid);
            }
            // 5.同步用户&用户所在部门&用户职位关系&门店权限
            syncSpecifyNodeUser(eid, null, true, enterpriseConfig, enterpriseSettingVO);
            // 6.初始化ai用户
            syncSingleUserFacade.initAiUser(eid, dbName);
            newSyncFacade.syncToCoolCollege(enterpriseConfig, enterpriseSettingVO, null);
            //成功
            DataSourceHelper.reset();
            enterpriseOperateLogService.updateStatusAndOperateEndTimeById(SyncConfig.SYNC_STATUS_SUCCESS, new Date(), null, logDO.getId());
        }catch (Exception e){
            String errMsg = null;
            if(e instanceof ServiceException){
                errMsg = ((ServiceException) e).getErrorMessage();
            }
            if(e instanceof DuplicateKeyException){
                errMsg = "DuplicateKeyException ";
            }
            if(e instanceof ApiException){
                errMsg = ((ApiException) e).getErrMsg();
            }
            //同步失败删除拦截key
            redisUtilPool.delKey(redisConstantUtil.getSyncEidEffectiveKey(eid));
            log.error("同步失败 eid:{}, appType:{}", eid, AppTypeEnum.ONE_PARTY_APP.getValue(), e);
            //失败
            DataSourceHelper.reset();
            enterpriseOperateLogService.updateStatusAndOperateEndTimeById(SyncConfig.SYNC_STATUS_FAIL, new Date(), errMsg, logDO.getId());
        }finally {
            //任务无论完成失败，都删除区域和门店缓存
            String regionKey = redisConstantUtil.getSyncRegionKey(eid);
            String storeKey = redisConstantUtil.getSyncStoreKey(eid);
            redisUtilPool.delKey(regionKey);
            redisUtilPool.delKey(storeKey);
        }
    }


    /**
     * 同步人员 支持全量和指定部门的人员
     * @param eid
     * @param regionId
     * @param isSyncRoleAndAuth
     * @throws ApiException
     */
    public void syncSpecifyNodeUser(String eid, Long regionId, Boolean isSyncRoleAndAuth, EnterpriseConfigDO config, EnterpriseSettingVO setting) throws ApiException {
        log.info("开始同步用户eid:{}", eid);
        String corpId = config.getDingCorpId();
        String dbName = config.getDbName();
        Boolean isDingType = AppTypeEnum.isDingType(config.getAppType());
        //这次同步的用户id
        Set<String> dingUserIdList = new HashSet<>();
        //切换企业库
        DataSourceHelper.changeToSpecificDataSource(dbName);
        RegionDO regionDO = regionMapper.getByRegionId(eid, regionId);
        List<SyncTreeNode> deptList = new ArrayList<>();
        //遍历部门列表以及对应的用户列表  regionDO为null的时候遍历所有的用户
        if (regionId == null||regionDO == null){
            deptList = sysDepartmentService.getSyncDeptTreeList(eid, null);
        }else {
            deptList = sysDepartmentService.getSyncDeptTreeList(eid, regionDO.getSynDingDeptId());
        }
        List<String> deptIds = deptList.stream().map(SyncTreeNode::getId).collect(Collectors.toList());
        for (String deptId : deptIds) {
            if (isDingType) {
                //同步钉钉部门用户
                syncDeptUser(corpId, eid, dbName, dingUserIdList, setting, deptId, isSyncRoleAndAuth, config);
            } else if(AppTypeEnum.isWxSelfAndPrivateType(config.getAppType()))  {
//                //同步企微部门用户私服以及第三方
                syncQwSelfDeptUser(corpId, eid, dbName, dingUserIdList, deptId, config.getAppType());
            } else if(AppTypeEnum.isFsType(config.getAppType()))  {
                syncFsDeptUser(corpId, eid, dbName, dingUserIdList, deptId, config.getAppType());
            }else {
                //同步企微部门用户
                syncQwDeptUser(corpId, eid, dbName, dingUserIdList, deptId, config.getAppType());
            }
        }
        //同步授权范围用户
        if(AppTypeEnum.isWxSelfAndPrivateType(config.getAppType()) || AppTypeEnum.isDingType(config.getAppType()) || AppTypeEnum.isFsType(config.getAppType())){
            syncAuthUser(corpId, config, dingUserIdList,setting, isSyncRoleAndAuth);
        }
        log.info("dingUserIdList size:{}", dingUserIdList.size());
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //如果是节点同步,不执行人员删除操作
        if(regionId!=null){
            log.info("节点用户同步完成eid:{}，regionId={}", eid,regionId);
            return;
        }
        //飞书暂不删除用户
        if (!AppTypeEnum.isFsType(config.getAppType())) {
            //删除门店库中和钉钉对应的用户、剩下的为待删除用户
            List<String> coolUserIdList = enterpriseUserService.selectAllUserIdByUserType(eid, UserTypeEnum.INTERNAL_USER);;
            List<String> deleteUserIds = coolUserIdList.stream().filter(dingUserId -> !dingUserIdList.contains(dingUserId)).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(deleteUserIds)){
                log.info("无需要删除的用户");
                return;
            }
            log.info("需要删除的用户个数为 {}", deleteUserIds.size());
            long startTime = System.currentTimeMillis();
            List<String> mainAdminIds = Lists.newArrayList();
            // 非门店通应用-获取管理员用户id
            if(!AppTypeEnum.ONE_PARTY_APP.getValue().equals(config.getAppType())) {
                mainAdminIds = enterpriseUserService.getMainAdmin(eid).stream()
                        .map(EnterpriseUserDO::getUserId)
                        .collect(Collectors.toList());
            }
            for (String coolUserId : deleteUserIds) {
                try {
                    //不能删除AI用户和主管理员
                    if (!Constants.AI_USER_ID.equals(coolUserId) && !mainAdminIds.contains(coolUserId)) {
                        syncDeleteUser(eid, coolUserId, dbName);
                    }
                } catch (Exception e) {
                    log.info("fullSyncUser corpId={}", corpId, e);
                }
            }
            log.info("删除用户 共耗时:{}", DateUtils.formatBetween(System.currentTimeMillis() - startTime));
        }
        log.info("同步用户完成eid:{}", eid);
    }


    public void syncMyjOrgAll(String eid, String userName, String userId, String corpId, String dbName, String unitId, List<RegionDTO> resultList, Long logId, Long regionId) {

        DataSourceHelper.reset();
        if (logId == null) {
            EnterpriseOperateLogDO logDO = EnterpriseOperateLogDO.builder().enterpriseId(eid).operateDesc("第三方OA同步")
                    .operateType(SyncConfig.ENTERPRISE_OPERATE_LOG_SYNC).operateStartTime(new Date()).userName(userName).createTime(new Date())
                    .status(SyncConfig.SYNC_STATUS_ONGOING).userId(userId).build();
            enterpriseOperateLogService.insert(logDO);
            logId = logDO.getId();
        }
        log.info("syncMyjOrgAll:corpId:{},regionId:{}", corpId, null);
        try {
            //如果区域不为null的时候，只全量同步人员,不同步部门等
            String remark = "";
            DataSourceHelper.changeToSpecificDataSource(dbName);
            //同步美宜佳组织架构
            thirdOaDeptSyncService.newSyncOrgAll(eid, null, unitId, resultList);
            remark = "全量同步成功";
            //成功
            DataSourceHelper.changeToSpecificDataSource(dbName);
            //同步用户职位以及区域门店权限
            syncMyjUserAuth(eid, dbName);
            DataSourceHelper.reset();
            enterpriseOperateLogService.updateStatusAndOperateEndTimeById(SyncConfig.SYNC_STATUS_SUCCESS, new Date(), remark, logId);
        } catch (Exception e) {
            String errMsg = null;
            if (e instanceof ServiceException) {
                errMsg = ((ServiceException) e).getErrorMessage();
            }
            if (e instanceof DuplicateKeyException) {
                errMsg = "DuplicateKeyException ";
            }

            log.error("同步钉钉失败 eid : {}", eid, e);
            //失败
            DataSourceHelper.reset();
            enterpriseOperateLogService.updateStatusAndOperateEndTimeById(SyncConfig.SYNC_STATUS_FAIL, new Date(), errMsg, logId);
        } finally {
            //同步失败删除拦截key
            redisUtilPool.delKey(redisConstantUtil.getSyncEidEffectiveKey(eid));
            //无论是否失败，删除节点同步锁
            redisUtilPool.delKey(redisConstantUtil.getSyncLockKey(eid));
            //任务无论完成失败，都删除区域和门店缓存
            String regionKey = redisConstantUtil.getSyncRegionKey(eid);
            String storeKey = redisConstantUtil.getSyncStoreKey(eid);
            redisUtilPool.delKey(regionKey);
            redisUtilPool.delKey(storeKey);
        }
    }


    @Async("syncThreadPool")
    public void syncMyjAll(String eid, List<RegionDTO> resultList) {
        DataSourceHelper.reset();
        EnterpriseOperateLogDO logDO = EnterpriseOperateLogDO.builder().enterpriseId(eid).operateDesc("第三方OA同步")
                .operateType(SyncConfig.ENTERPRISE_OPERATE_LOG_SYNC).operateStartTime(new Date()).userName("system").createTime(new Date())
                .status(SyncConfig.SYNC_STATUS_ONGOING).userId("system").build();
        enterpriseOperateLogService.insert(logDO);
        Long logId = logDO.getId();
        log.info("syncMyjAll:eid:{}", eid);
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
            EnterpriseSettingVO enterpriseSetting = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
            DataSourceHelper.changeToSpecificDataSource(config.getDbName());
            //同步美宜佳组织架构
            thirdOaDeptSyncService.newSyncOrgAll(eid, null, Constants.ONE_STR, resultList);
            //同步部门
            dingDeptSyncService.syncDingDepartmentAll(eid, config.getDingCorpId());
            this.syncSpecifyNodeUser(eid, null, true, config, enterpriseSetting);
            DataSourceHelper.reset();
            enterpriseOperateLogService.updateStatusAndOperateEndTimeById(SyncConfig.SYNC_STATUS_SUCCESS, new Date(), "remark", logId);
        } catch (Exception e) {
            String errMsg = null;
            if (e instanceof ServiceException) {
                errMsg = ((ServiceException) e).getErrorMessage();
            }
            if (e instanceof DuplicateKeyException) {
                errMsg = "DuplicateKeyException ";
            }
            log.error("同步美宜佳失败 eid : {}", eid, e);
            //失败
            DataSourceHelper.reset();
            enterpriseOperateLogService.updateStatusAndOperateEndTimeById(SyncConfig.SYNC_STATUS_FAIL, new Date(), errMsg, logId);
        } finally {
            //同步失败删除拦截key
            redisUtilPool.delKey(redisConstantUtil.getSyncEidEffectiveKey(eid));
            //无论是否失败，删除节点同步锁
            redisUtilPool.delKey(redisConstantUtil.getSyncLockKey(eid));
            //任务无论完成失败，都删除区域和门店缓存
            String regionKey = redisConstantUtil.getSyncRegionKey(eid);
            String storeKey = redisConstantUtil.getSyncStoreKey(eid);
            redisUtilPool.delKey(regionKey);
            redisUtilPool.delKey(storeKey);
        }
    }

    /**
     * 同步美宜佳权限
     * @param eid
     * @param dbName
     */
    private void syncMyjUserAuth(String eid, String dbName) {
        log.info("开始同步用户syncMyjUserAuth");
        int pageSize = 1000;
        int maxSize = 150000;
        long size = 0L;
        long pages = (maxSize + pageSize - 1) / pageSize;
        for (int curPage = 1; curPage <= pages; curPage++) {
            PageHelper.startPage(curPage, pageSize, false);
            List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserMapper.selectUsersByStatusAndUserIdsForMyj(eid, null, null, null);
            if (CollectionUtils.isEmpty(enterpriseUserDOList)) {
                break;
            }
            size = size + enterpriseUserDOList.size();
            enterpriseUserDOList.forEach(user -> syncSingleUserFacade.syncMyjUserAuth(user.getUserId(), user.getJobnumber(), user.getThirdOaUniqueFlag(), eid, dbName));
        }
        log.info("同步用户结束syncMyjUserAuth.size:{}", size);
    }

    /**
     * 美宜佳单个用户权限
     * @param eid
     * @param userRequest
     */
    public void syncMyjSingleUserAuth(String eid, SyncMyjUserRequest userRequest) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        EnterpriseUserDO enterpriseUser = enterpriseUserDao.selectByUserId(eid, userRequest.getUserId());
        if (Objects.isNull(enterpriseUser)) {
            return;
        }
        List<RegionDO> regionDOList = new ArrayList<>();
        List<Long> regionIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userRequest.getRegionList())) {
            regionDOList = regionMapper.selectRegionBySynDingDeptIds(eid, userRequest.getRegionList());
        }
        if (CollectionUtils.isNotEmpty(regionDOList)) {
            regionIdList = regionDOList.stream().map(RegionDO::getId).collect(Collectors.toList());
        }

        //处理用户节点关系
        dingUserSyncService.syncUserRegionMapping(eid, userRequest.getUserId(), userRequest.getRegionList(), Boolean.TRUE);
        enterpriseUserService.updateUserRegionPathList(eid, Collections.singletonList(userRequest.getUserId()));

        //区域门店
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            List<UserAuthMappingDO> userAuthMappingList = getSyncAuthMappingListBaili(eid, userRequest.getRegionList(), userRequest.getUserId(), enterpriseUser);
            //用户在cool中已有的可视范围映射关系
            List<UserAuthMappingDO> coolUserAuthList = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userRequest.getUserId());
            Map<String, UserAuthMappingDO> userAuthMap = CollectionUtils.isEmpty(coolUserAuthList) ? null :
                    coolUserAuthList.stream().collect(Collectors.toMap(UserAuthMappingDO::getMappingId, data -> data, (a, b) -> a));

            userAuthMappingList.forEach(userAuth -> {
                UserAuthMappingDO auth = userAuthMap == null ? null : userAuthMap.get(userAuth.getMappingId());
                if (auth == null) {
                    userAuthMappingMapper.insertUserAuthMapping(eid, userAuth);
                } else {
                    coolUserAuthList.remove(auth);
                }
            });
            if (CollectionUtils.isNotEmpty(coolUserAuthList)) {
                List<Long> deleteAuthIds = coolUserAuthList.stream()
                        .filter(data -> UserAuthMappingSourceEnum.SYNC.getCode().equals(data.getSource()))
                        .map(UserAuthMappingDO::getId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(deleteAuthIds)) {
                    userAuthMappingMapper.deleteAuthMappingByIds(eid, deleteAuthIds);
                }
            }


        } else {
            userAuthMappingMapper.deleteAuthMappingByUserIdAndSource(eid, Collections.singletonList(userRequest.getUserId()), UserAuthMappingSourceEnum.SYNC.getCode());
        }
        //职位
        dingUserSyncService.syncDingPosition(eid, userRequest.getPosition(), userRequest.getUserId(),  null);
    }


    /**
     * 鲜丰水果单个用户权限
     * @param eid
     * @param userRequest
     */
    public void syncXfsgSingleUserAuth(String eid, SyncXfsgOARequest userRequest) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        EnterpriseUserDO enterpriseUser = enterpriseUserDao.selectByUserId(eid, userRequest.getUserId());
        if (Objects.isNull(enterpriseUser)) {
            return;
        }
        enterpriseUser.setThirdOaUniqueFlag(userRequest.getIdCard());
        Boolean active = Objects.isNull(userRequest.getStatus()) ? null : userRequest.getStatus() != 0;
        enterpriseUser.setActive(active);
        enterpriseUserDao.updateEnterpriseUser(eid, enterpriseUser);
        ThirdDepartmentDO thirdDepartmentDO = thirdDepartmentDao.getByDepartmentCode(eid, userRequest.getDepartmentCode());
        // 处理用户所属关系
        List<String> belongSynDingDeptIdList = Lists.newArrayList();
        if(thirdDepartmentDO != null && StringUtils.isNotBlank(thirdDepartmentDO.getDepartmentName())){
            List<RegionDO> belongRegionList = regionMapper.listRegionsByNames(eid, Collections.singletonList(thirdDepartmentDO.getDepartmentName()));
            belongSynDingDeptIdList = ListUtils.emptyIfNull(belongRegionList).stream().map(RegionDO::getSynDingDeptId).collect(Collectors.toList());
            //处理用户节点关系
            dingUserSyncService.syncUserRegionMapping(eid, userRequest.getUserId(), belongSynDingDeptIdList, Boolean.TRUE);
            enterpriseUserService.updateUserRegionPathList(eid, Collections.singletonList(userRequest.getUserId()));
        }
        //处理用户管辖权限
        List<Long> hasRoleIdList = enterpriseUserRoleMapper.selectRoleIdsByUserId(eid, userRequest.getUserId());
        List<String> authSynDingDeptIdList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(hasRoleIdList) && (hasRoleIdList.contains(XfsgRoleEnum.XFSG_CLERK.getCode())
                || hasRoleIdList.contains(XfsgRoleEnum.XFSG_SHOPOWNER.getCode()) || hasRoleIdList.contains(XfsgRoleEnum.SUPERVISION.getCode())) ){
            authSynDingDeptIdList = belongSynDingDeptIdList;
        }else if(CollectionUtils.isNotEmpty(hasRoleIdList) && (hasRoleIdList.contains(XfsgRoleEnum.REGION_MANAGER.getCode())
                || hasRoleIdList.contains(XfsgRoleEnum.THEATER_MANAGER.getCode())) ){
            List<ThirdDepartmentDO>  thirdDepartmentDOList = thirdDepartmentDao.listByDeptPrincipals(eid, Collections.singletonList(enterpriseUser.getJobnumber()));
            List<String> departmentNames = ListUtils.emptyIfNull(thirdDepartmentDOList).stream().map(ThirdDepartmentDO::getDepartmentName).distinct().collect(Collectors.toList());
            List<RegionDO>  authRegionList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(departmentNames)){
                authRegionList = regionMapper.listRegionsByNames(eid, departmentNames);
            }
            authSynDingDeptIdList = ListUtils.emptyIfNull(authRegionList).stream().map(RegionDO::getSynDingDeptId).collect(Collectors.toList());
        }
        // userRegionMappingService.deletedUserRegionMappingByUserIds(eid, userRequest.getUserId());
        if (CollectionUtils.isNotEmpty(authSynDingDeptIdList)) {
            List<UserAuthMappingDO> userAuthMappingList = getSyncAuthMappingListBaili(eid, authSynDingDeptIdList, userRequest.getUserId(), enterpriseUser);
            //用户在cool中已有的可视范围映射关系
            List<UserAuthMappingDO> coolUserAuthList = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userRequest.getUserId());
            Map<String, UserAuthMappingDO> userAuthMap = CollectionUtils.isEmpty(coolUserAuthList) ? null :
                    coolUserAuthList.stream().collect(Collectors.toMap(UserAuthMappingDO::getMappingId, data -> data, (a, b) -> a));

            userAuthMappingList.forEach(userAuth -> {
                UserAuthMappingDO auth = userAuthMap == null ? null : userAuthMap.get(userAuth.getMappingId());
                if (auth == null) {
                    userAuthMappingMapper.insertUserAuthMapping(eid, userAuth);
                } else {
                    coolUserAuthList.remove(auth);
                }
            });
            if (CollectionUtils.isNotEmpty(coolUserAuthList)) {
                List<Long> deleteAuthIds = coolUserAuthList.stream()
                        .filter(data -> UserAuthMappingSourceEnum.SYNC.getCode().equals(data.getSource()))
                        .map(UserAuthMappingDO::getId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(deleteAuthIds)) {
                    userAuthMappingMapper.deleteAuthMappingByIds(eid, deleteAuthIds);
                }
            }
        } else {
            userAuthMappingMapper.deleteAuthMappingByUserIdAndSource(eid, Collections.singletonList(userRequest.getUserId()), UserAuthMappingSourceEnum.SYNC.getCode());
        }
        // 如果用户所属区域是鲜丰的门店，则丢消息维护培训注册人员
        // 鲜丰门店对应的ehr部门code和  门店主数据的code一致
        StoreDO storeDO = storeMapper.selectStoreNameByNum(eid, userRequest.getDepartmentCode());
        if(storeDO != null){
            // 拉取培训人员
            SyncXfsgTrainingPersonInfoDTO syncXfsgTrainingPersonInfoDTO = SyncXfsgTrainingPersonInfoDTO.builder()
                    .enterpriseId(eid)
                    .jobnumber(enterpriseUser.getJobnumber())
                    .idCard(enterpriseUser.getThirdOaUniqueFlag())
                    .storeNum(storeDO.getStoreNum())
                    .build();
            simpleMessageService.send(JSONObject.toJSONString(syncXfsgTrainingPersonInfoDTO), RocketMqTagEnum.SYNC_TRAINING_PERSON);
        }
    }
}
