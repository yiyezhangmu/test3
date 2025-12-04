package com.coolcollege.intelligent.service.dingSync.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.constant.TwoResultTuple;
import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.enums.LxzEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.RoleSyncTypeEnum;
import com.coolcollege.intelligent.common.enums.baili.BailiEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.enterprise.SubordinateSourceEnum;
import com.coolcollege.intelligent.common.enums.enterprise.UserSelectRangeEnum;
import com.coolcollege.intelligent.common.enums.position.PositionSourceEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.enums.role.CoolPositionTypeEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.enums.xfsg.XfsgEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.xfsg.XfsgRoleEnum;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import com.coolcollege.intelligent.common.http.CoolHttpClientResult;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.ListOptUtils;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.convert.ConvertFactory;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.*;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.SubordinateMappingDAO;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.facade.dto.SyncRequest;
import com.coolcollege.intelligent.mapper.homeTemplate.HomeTemplateRoleMappingDAO;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.*;
import com.coolcollege.intelligent.model.enterprise.dto.UserDingPositionDTO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.enums.UserAuthMappingSourceEnum;
import com.coolcollege.intelligent.model.enums.UserAuthMappingTypeEnum;
import com.coolcollege.intelligent.model.homeTemplate.HomeTemplateRoleMappingDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.dingSync.DingUserSyncService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserMappingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.service.homeTemplate.HomeTemplateRoleMappingService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 钉钉用户同步实现类
 *
 * @ClassName: DingUserSyncServiceImpl
 * @Author: xugangkun
 * @Date: 2021/3/23 14:38
 */
@Slf4j
@Service
public class DingUserSyncServiceImpl implements DingUserSyncService {

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Resource
    private EnterpriseUserDepartmentMapper enterpriseUserDepartmentMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;

    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Resource
    private EnterpriseUserMappingMapper enterpriseUserMappingMapper;

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Resource
    private StoreMapper storeMapper;

    @Autowired
    private EnterpriseUserMappingService enterpriseUserMappingService;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Autowired
    UserRegionMappingDAO userRegionMappingDAO;

    @Autowired
    SubordinateMappingDAO subordinateMappingDAO;
    @Resource
    private ConvertFactory convertFactory;

    @Autowired
    HomeTemplateRoleMappingService homeTemplateRoleMappingService;

    @Autowired
    HomeTemplateRoleMappingDAO homeTemplateRoleMappingDAO;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Value("${api.domain.url}")
    private String apiDmoainUrl;
    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;

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
    @Override
    public void syncConfigUser(OapiV2UserGetResponse.UserGetResponse response, EnterpriseUserDO enterpriseUser, String eid, Boolean flag) {
        List<EnterpriseUserDO> insertUsers = new ArrayList<>();
        insertUsers.add(enterpriseUser);
        if (flag) {
            //处理新增逻辑1.创建平台用户
            enterpriseUserDao.batchInsertPlatformUsers(insertUsers);
        } else {
            //处理更新逻辑
            enterpriseUserDao.updateConfigEnterpriseUser(enterpriseUser);
        }
        //添加用户企业关联信息
        EnterpriseUserMappingDO enterpriseUserMappingDO = new EnterpriseUserMappingDO();
        enterpriseUserMappingDO.setId(UUIDUtils.get32UUID());
        enterpriseUserMappingDO.setUserId(enterpriseUser.getId());
        enterpriseUserMappingDO.setEnterpriseId(eid);
        enterpriseUserMappingDO.setUnionid(enterpriseUser.getUnionid());
        enterpriseUserMappingDO.setUserStatus(enterpriseUser.getUserStatus());
        enterpriseUserMappingDO.setCreateTime(new Date());
        /*EnterpriseUserMappingDO checkDo = enterpriseUserMappingMapper.selectByEidAndUserId(eid, enterpriseUser.getId());
        if (checkDo == null) {
            enterpriseUserMappingMapper.save(enterpriseUserMappingDO);
        }*/
        enterpriseUserMappingService.saveEnterpriseUserMapping(enterpriseUserMappingDO);
    }

    @Override
    public void syncDeleteConfigUser(String eid, List<String> userIds) {
        if(CollectionUtils.isEmpty(userIds)){
            return;
        }
        //设置用户为未激活
        enterpriseUserDao.batchDeleteUserIdsConfig(userIds);
        //删除用户企业映射信息
        enterpriseUserMappingMapper.batchDeleteUserByUserIds(userIds, eid);
    }

    /**
     * 把钉钉返回信息转化为门店中对应的实体
     * @param response 请求结果
     * @param enterpriseUser 企业用户实体
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/23 14:40
     */
    @Override
    public EnterpriseUserDO initEnterpriseUser(OapiV2UserGetResponse.UserGetResponse response, EnterpriseUserDO enterpriseUser,
                                               String eid) {
        BeanUtils.copyProperties(response, enterpriseUser);
        enterpriseUser.setJobnumber(response.getJobNumber());
        List<OapiV2UserGetResponse.DeptLeader> leaderInDept = response.getLeaderInDept();
        enterpriseUser.setUserId(response.getUserid());
        enterpriseUser.setIsLeaderInDepts(JSONObject.toJSONString(leaderInDept));
        enterpriseUser.setIsHide(response.getHideMobile());
        enterpriseUser.setActive(response.getActive());
        enterpriseUser.setIsAdmin(response.getAdmin());
        Boolean isLeader = false;
        for (int i = 0; i < leaderInDept.size(); i++) {
            if (leaderInDept.get(i).getLeader()) {
                isLeader = true;
                break;
            }
        }
        enterpriseUser.setIsLeader(isLeader);
        enterpriseUser.setLanguage("zh_cn");
        List<OapiV2UserGetResponse.UserRole> roleList = response.getRoleList();
        //设置角色的id列表
        if (CollUtil.isNotEmpty(roleList)) {
            List<Long> roleIds = roleList.stream().map(m -> m.getId()).collect(Collectors.toList());
            String strRoleIds = StrUtil.join(",", roleIds);
            enterpriseUser.setRoles(strRoleIds);
        }
        String value = redisUtilPool.hashGet(RedisConstant.HISTORY_ENTERPRISE, eid);
        //设置该用户所属的部门id列表
        if (CollectionUtils.isNotEmpty(response.getDeptIdList()) && StringUtils.isNotBlank(value)) {
            EnterpriseUserRequest enterpriseUserRequest = new EnterpriseUserRequest();
            enterpriseUserRequest.setEnterpriseUserDO(enterpriseUser);
            List<String> deptIds = new ArrayList<>();
            for (Long deptId :response.getDeptIdList()) {
                deptIds.add(String.valueOf(deptId));
            }
            enterpriseUserRequest.setDepartmentLists(deptIds);
            enterpriseUserRequest.setDepartment("[" + response.getDeptIdList().stream().map(String::valueOf).collect(Collectors.joining(",")) + "]");
            TwoResultTuple<Set<String>, Map<String, String>> tuple = sysDepartmentService.getAllDeptInfo(eid);
            enterpriseUserService.updateUserDeptPath(enterpriseUserRequest, tuple.second);
        }
        enterpriseUser.setUserStatus(UserStatusEnum.NORMAL.getCode());
        enterpriseUser.setCreateTime(new Date());
        return enterpriseUser;
    }

    /**
     * 同步修改企业用户
     * @param enterpriseUser
     * @param eid
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/23 15:02
     */
    @Override
    public void syncUser(OapiV2UserGetResponse.UserGetResponse response, EnterpriseUserDO enterpriseUser, String eid
                ,EnterpriseSettingVO setting, Boolean isSyncRoleAndAuth) {
        Boolean isAdd = enterpriseUser.getId() == null;
        String userId = enterpriseUser.getUserId();
        //TODO 一.插入或者更新用户信息
        if (enterpriseUser.getId() == null) {
            enterpriseUser.setId(UUIDUtils.get32UUID());
            enterpriseUser.setSubordinateRange(setting.getManageUser());
            Boolean isActive = enterpriseUserService.insertEnterpriseUser(eid, enterpriseUser);
            if (!isActive) {
                return;
            }
        } else {
            enterpriseUserDao.updateEnterpriseUser(eid, enterpriseUser);
        }
        //TODO 三.更新该用户的部门信息 2021-5-6 初始化同步的时候，一定要同步用户基本信息以及部门信息，代码往上提
        //ding部门id列表
        List<Long> dingDeptIds = Optional.ofNullable(response.getDeptIdList()).orElseGet(ArrayList::new);
        List<String> dingDeptIdList = ListOptUtils.longListConvertStringList(dingDeptIds);
        syncUserDepartment(eid, userId, dingDeptIdList);
        //同步用户部门权限
        List<OapiV2UserGetResponse.DeptLeader> leaderInDept = response.getLeaderInDept();
        List<Long> authDeptIds = leaderInDept.stream()
                .filter(OapiV2UserGetResponse.DeptLeader::getLeader)
                .map(OapiV2UserGetResponse.DeptLeader::getDeptId)
                .collect(Collectors.toList());
        syncUserDepartmentAuth(eid, userId, ListOptUtils.longListConvertStringList(authDeptIds));
        //同步用户和区域的关系
        syncUserRegionMapping(eid, userId, ListUtils.emptyIfNull(dingDeptIds).stream().map(String::valueOf).collect(Collectors.toList()),
                !Objects.equals(setting.getEnableDingSync(), Constants.ENABLE_DING_SYNC_NOT_OPEN));
        //补全该用户的user_region_ids
        enterpriseUserService.updateUserRegionPathList(eid, Arrays.asList(userId));
        if (isAdd && BailiEnterpriseEnum.bailiAffiliatedCompany(eid)) {
            syncSubordinateMapping(eid, userId, ListUtils.emptyIfNull(authDeptIds).stream().map(String::valueOf).collect(Collectors.toList()));
        }
        //TODO 判断是否开启自动同步。未开启自动同步，只同步用户基础信息，不同步用户对应的映射关系,如果该用户当前没有任何职位，设置为未分配。2021-5-6 关闭钉钉同步时，也需要同步用户部门信息
        if (Objects.equals(setting.getEnableDingSync(), Constants.ENABLE_DING_SYNC_NOT_OPEN)) {
            if (enterpriseUserRoleMapper.selectCountsByUserId(eid, userId) == 0) {
                Long roleId = sysRoleService.getRoleIdByRoleEnum(eid, Role.EMPLOYEE.getRoleEnum());
                enterpriseUserRoleMapper.save(eid, new EnterpriseUserRole(roleId.toString(), userId, RoleSyncTypeEnum.SYNC.getCode()));
            }
            return;
        }
        //TODO 二更新该用户的角色信息：新增或者删除 2021-5-6 新增判断字段，存在只添加用户信息以及用户部门信息的场景
        Map<String, Boolean> leaderMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(response.getLeaderInDept())) {
            Map<String, Boolean> map = new HashMap<>();
            for (OapiV2UserGetResponse.DeptLeader deptLeader : response.getLeaderInDept()) {
                map.putIfAbsent(String.valueOf(deptLeader.getDeptId()), deptLeader.getLeader());
            }
            leaderMap = map;
        }
        boolean isSyncRole = true;
        String isTest = redisUtilPool.hashGet("bailiEhrTest20250424", eid);
        if(StringUtils.isNotBlank(isTest)){
            //百丽企业判断用户是否是在灰度中 如果在则不需要从钉钉同步职位
            try {
                CoolHttpClientResult httpClientResult = CoolHttpClient.doGet(apiDmoainUrl + "special/getUserGreyOrgan?enterpriseId=" + eid + "&jobNumber=" + enterpriseUser.getJobnumber());
                log.info("百丽灰度企业：userId:{}，response:{}", enterpriseUser.getUserId(), JSONObject.toJSONString(httpClientResult));
                isSyncRole = !"true".equals(httpClientResult.getContent());
                if(!isSyncRole){
                    log.info("百丽灰度企业：删除同步过来的角色:{}", enterpriseUser.getUserId());
                    enterpriseUserRoleMapper.deleteUserRoleByUserId(eid, enterpriseUser.getUserId(), RoleSyncTypeEnum.SYNC.getCode());
                }
            } catch (Exception ignored) {

            }
        }
        if (isSyncRoleAndAuth && isSyncRole) {
            syncDingUserRole(response, userId, eid, setting, leaderMap);
        }

        //钉钉同步下执行权限同步
        if (Objects.equals(setting.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN)) {
            //开启钉钉同步，处理直属上级节点
            syncDingUserDirectSuperior(response, userId, eid, setting);
            //TODO 四.更新该用户区域权限信息(可见区域与门店) 2021-5-6 新增判断字段，存在只添加用户信息以及用户部门信息的场景
            //根据钉钉返回信息，获得该用户拥有的可视化权限
            syncUserAuth(userId, eid, dingDeptIdList, setting, response.getTitle(), response, leaderMap, null);
        }else if(Objects.equals(setting.getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD)){
            //第三方oa同步权限
            SyncRequest syncRequest = SyncRequest.builder()
                    .employeeCode(enterpriseUser.getJobnumber())
                    .enterpriseId(eid)
                    .userId(enterpriseUser.getUserId())
                    .thirdOaUniqueFlag(enterpriseUser.getThirdOaUniqueFlag())
                    .roleNameList(Optional.ofNullable(response.getRoleList()).orElseGet(ArrayList::new).stream()
                            .map(OapiV2UserGetResponse.UserRole::getName)
                            .collect(Collectors.toList()))
                    .build();
            simpleMessageService.send(JSONObject.toJSONString(syncRequest), RocketMqTagEnum.THIRD_OA_SYNC_SINGLE_QUEUE);
        }
    }


    /**
     * 处理用户-钉钉职位关系
     * @param eid
     * @param position
     * @param userId
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/31 10:19
     */
    @Override
    public void syncDingPosition(String eid, String position, String userId, Map<String, Boolean> leaderMap) {
        //用户与钉钉职位的映射关系
        List<UserDingPositionDTO> userDingPositions = enterpriseUserRoleMapper.selectUserDingPosition(eid, userId);
        if (position == null || position.equals("")) {
            //需要判断当前用户是否已有职位映射关系，若有，需要删除
            if (userDingPositions != null && userDingPositions.size() != 0) {
                List<Long> deleteIds = userDingPositions.stream().map(UserDingPositionDTO::getUserRoleId).collect(Collectors.toList());
                enterpriseUserRoleMapper.deleteBatchByPrimaryKey(eid, deleteIds);
            }
            return;
        }
        if(XfsgEnterpriseEnum.xfsgCompany(eid)){
            position = XfsgRoleEnum.translateToInitPosition(position);
        }
        //判断系统中是否有该职位的角色，没有，添加角色
        List<SysRoleDO> titleList = sysRoleMapper.selectByRoleNameAndSource(eid, position, PositionSourceEnum.SYNC_POSITION.getValue());
        if (titleList == null || titleList.size() == 0) {
            //库中无该职位信息，添加
            SysRoleDO sysRoleDO = new SysRoleDO();
            sysRoleDO.setId(System.currentTimeMillis() + new Random().nextInt(1000))
                    .setRoleName(position)
                    .setRoleAuth(AuthRoleEnum.PERSONAL.getCode())
                    .setSource(PositionSourceEnum.SYNC_POSITION.getValue())
                    .setPositionType(CoolPositionTypeEnum.STORE_OUTSIDE.getCode())
                    .setPriority(sysRoleService.getNormalRoleMaxPriority(eid) + 10)
                    .setCreateTime(new Date())
                    .setCreateUser(AIEnum.AI_USERID.getCode());
            sysRoleMapper.addSystemRole(eid, sysRoleDO);
            // 给新增角色初始化移动端菜单
//            try {
//                sysRoleService.initMenuWhenSyncRole(eid,sysRoleDO.getId());
//            } catch (Exception e) {
//                log.error("syncDingPosition给新增角色初始化移动端菜单,企业id:{},角色Id:{}", eid, sysRoleDO.getId(), e);
//            }
            //角色与模板添加映射关系
            HomeTemplateRoleMappingDO homeTemplateRoleMappingDO = homeTemplateRoleMappingService.initHomeTempRoleMapping(sysRoleDO.getId(), sysRoleDO.getPositionType());
            homeTemplateRoleMappingDAO.batchInsert(eid,Arrays.asList(homeTemplateRoleMappingDO));
            titleList.add(sysRoleDO);
        }

        //用户只允许拥有一个钉钉职位，删除多余
        if (userDingPositions.size() > 1) {
            List<Long> deleteIds = userDingPositions.stream().map(UserDingPositionDTO::getUserRoleId).collect(Collectors.toList());
            enterpriseUserRoleMapper.deleteBatchByPrimaryKey(eid, deleteIds);
            enterpriseUserRoleMapper.save(eid, new EnterpriseUserRole(titleList.get(0).getId().toString(), userId, RoleSyncTypeEnum.SYNC.getCode()));
            return;
        }
        //该用户没有映射信息，添加
        if (userDingPositions.size() == 0) {
            enterpriseUserRoleMapper.save(eid, new EnterpriseUserRole(titleList.get(0).getId().toString(), userId, RoleSyncTypeEnum.SYNC.getCode()));
            return;
        }
        if (userDingPositions.size() == 1 && !userDingPositions.get(0).getUserDingPosition().equals(position)) {
            enterpriseUserRoleMapper.deleteByPrimaryKey(eid, userDingPositions.get(0).getUserRoleId());
            enterpriseUserRoleMapper.save(eid, new EnterpriseUserRole(titleList.get(0).getId().toString(), userId, RoleSyncTypeEnum.SYNC.getCode()));
        }
    }

    /**
     * 处理用户-钉钉角色关系
     * @param response
     * @param userId
     * @param eid
     * @param setting
     * @throws
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/31 10:19
     */
    private void syncDingUserRole(OapiV2UserGetResponse.UserGetResponse response, String userId, String eid
            ,EnterpriseSettingVO setting, Map<String, Boolean> leaderMap) {
        //角色和职位规则
        String dingSyncRoleRuleDetail = setting.getDingSyncRoleRuleDetail();
        //1.先处理用户职位信息,当企业配置了同步职位时，才进行
        if (setting.getDingSyncRoleRule().equals(SyncConfig.TWO) || setting.getDingSyncRoleRule().equals(SyncConfig.THREE)) {
            //如果配置了同步的职位规则，只有规则内的职位才进行同步
            if(!SyncConfig.checkRoleRule(response.getTitle(),dingSyncRoleRuleDetail) && !setting.getDingSyncRoleRule().equals(SyncConfig.THREE)){
                log.info("同步的职位不在同步规则内,{},同步规则：{}",response.getTitle(),dingSyncRoleRuleDetail);
                //删掉现在的用户钉钉职位权限
                deleteUserSyncPosition(eid, userId);
            }else {
                syncDingPosition(eid, response.getTitle(), userId, leaderMap);
            }
        }
        //2.配置了角色同步，进行角色同步,不对子管理与负责人进行处理
        List<OapiV2UserGetResponse.UserRole> roleList = Optional.ofNullable(response.getRoleList()).orElseGet(ArrayList::new).stream()
                .filter(role -> !SyncConfig.checkSubManage(role.getName()))
                .collect(Collectors.toList());
        if (setting.getDingSyncRoleRule().equals(SyncConfig.ONE) || setting.getDingSyncRoleRule().equals(SyncConfig.THREE)) {
            //获得门店中角色列表
            List<SysRoleDO> coolRoleList = sysRoleMapper.selectSysRoleBySource(eid, PositionSourceEnum.SYNC.getValue());
            Map<Long, Long> roleIdDingRoleIdMap = ListUtils.emptyIfNull(coolRoleList)
                    .stream()
                    .collect(Collectors.toMap(SysRoleDO::getSynDingRoleId, SysRoleDO::getId, (a, b) -> a));
            List<Long> coolRoleIds = ListUtils.emptyIfNull(coolRoleList).stream().map(SysRoleDO::getSynDingRoleId).collect(Collectors.toList());
            //获得用户在cool中的角色列表,需要筛选出用户与钉钉同步角色的映射关系,同时获得该用户与已删除角色的映射关系，这些也需要删除
            List<EnterpriseUserRole> userRoles = enterpriseUserRoleMapper.selectDingRoleMappingByUserId(eid, userId);
            //如果库中没有相应的映射关系，设置map为null，否则设置map k=roleId,v=映射对象
            Map<String, EnterpriseUserRole> roleMap = ListUtils.emptyIfNull(userRoles).stream().collect(Collectors.toMap(EnterpriseUserRole::getRoleId, data -> data, (a, b) -> a));

            ListUtils.emptyIfNull(roleList).forEach(role -> {
                //如果配置了同步的角色规则，只有规则内的角色才进行同步
                if(!SyncConfig.checkRoleRule(role.getName(),dingSyncRoleRuleDetail) && !setting.getDingSyncRoleRule().equals(SyncConfig.THREE)){
                    return;
                }
                Long roleId = roleIdDingRoleIdMap.get(role.getId());
                log.info("dingRoleId:{},roleId:{}", role.getId(), roleId);
                EnterpriseUserRole check = MapUtils.isEmpty(roleMap)  || Objects.isNull(roleId) ? null : roleMap.get(roleId.toString());
                if (check != null) {
                    userRoles.remove(check);
                } else if (CollectionUtils.isNotEmpty(coolRoleIds)&&coolRoleIds.contains(role.getId())) {
                    enterpriseUserRoleMapper.save(eid, new EnterpriseUserRole(roleId.toString(), userId, RoleSyncTypeEnum.SYNC.getCode()));
                }
            });
            if (userRoles != null && userRoles.size() != 0) {
                //删除对应的映射关系
                List<Long> deleteUserRoleIds = userRoles.stream().map(EnterpriseUserRole::getId).collect(Collectors.toList());
                enterpriseUserRoleMapper.deleteBatchByPrimaryKey(eid, deleteUserRoleIds);
            }
        }
        //如果该用户没有任何角色，为该用户添加未分配角色
        if (enterpriseUserRoleMapper.selectCountsByUserId(eid, userId) == 0) {
            Long roleIdByRoleEnum = sysRoleService.getRoleIdByRoleEnum(eid, Role.EMPLOYEE.getRoleEnum());
            enterpriseUserRoleMapper.save(eid, new EnterpriseUserRole(roleIdByRoleEnum.toString(), userId, RoleSyncTypeEnum.SYNC.getCode()));
        }
    }

    @Override
    public void syncDeleteUser(String userId, String eid) {
        // 1.更新用户信息
        EnterpriseUserDO enterpriseUser = enterpriseUserDao.selectByUserId(eid, userId);
        if (enterpriseUser != null) {
            enterpriseUser.setActive(false);
            enterpriseUserDao.updateEnterpriseUser(eid, enterpriseUser);
        }
        // 2.删除用户角色映射关系
        //获得用户在cool中的角色列表
        List<Long> userRoleIds = enterpriseUserRoleMapper.selectIdsByUserId(eid, userId);
        // 3.删除该用户的部门信息
        List<Integer> userDepartmentIds = enterpriseUserDepartmentMapper.getIdsByUserId(eid, userId);
        //删除用户和区域的关联关系
        userRegionMappingDAO.deletedByUserIds(eid, Arrays.asList(userId));
        //删除用户的下属
        subordinateMappingDAO.deletedByUserIds(eid, Arrays.asList(userId));
        // 4.删除该用户的可见范围映射信息
        List<Long> userAuthIds = userAuthMappingMapper.selectIdsByUserId(eid, userId);
        deleteUserAndMappings(userRoleIds, userDepartmentIds, userAuthIds, eid);
    }

    @Override
    public void syncDeleteUserList(List<EnterpriseUserDO> userList, String eid) {
        //用户id列表
        List<String> userIds = new ArrayList<>();
        //用户角色映射新增、删除列表
        List<Long> allUserRoleDeleteList = new ArrayList<>();
        //用户-部门映射新增、删除列表
        List<Integer> allUserDeptDeleteList = new ArrayList<>();
        //用户权限信息(可视范围)映射新增、删除列表
        List<Long> allUserAuthDeleteList = new ArrayList<>();
        userList.forEach(user -> {
            userIds.add(user.getUserId());
            List<Long> userRoleIds = enterpriseUserRoleMapper.selectIdsByUserId(eid, user.getUserId());
            allUserRoleDeleteList.addAll(userRoleIds);
            List<Integer> userDepartmentIds = enterpriseUserDepartmentMapper.getIdsByUserId(eid, user.getUserId());
            allUserDeptDeleteList.addAll(userDepartmentIds);
            List<Long> userAuthIds = userAuthMappingMapper.selectIdsByUserId(eid, user.getUserId());
            allUserAuthDeleteList.addAll(userAuthIds);
        });
        enterpriseUserMapper.batchDeleteUserIds(eid, userIds);
        deleteUserAndMappings(allUserRoleDeleteList, allUserDeptDeleteList, allUserAuthDeleteList, eid);
    }

    @Override
    public void syncUserDepartment(String eid, String userId, List<String> deptIds) {
        if (CollectionUtils.isEmpty(deptIds)) {
            return;
        }
        //该用户在cool中的部门列表,该部门id为cool的id。
        List<EnterpriseUserDepartmentDO> userDepartments = enterpriseUserDepartmentMapper.selectUserDeptByUserId(eid, userId);
        Map<String, EnterpriseUserDepartmentDO> userDeptMap = ListUtils.emptyIfNull(userDepartments)
                .stream()
                .collect(Collectors.toMap(EnterpriseUserDepartmentDO::getDepartmentId, data -> data, (a, b) -> a));
        List<EnterpriseUserDepartmentDO> dos = new ArrayList<>();
        deptIds.forEach(deptId -> {
            EnterpriseUserDepartmentDO userDepartmentDO = userDeptMap.get(deptId);
            if (Objects.nonNull(userDepartmentDO)) {
                userDepartments.remove(userDepartmentDO);
            } else {
                //换成批量新增
                EnterpriseUserDepartmentDO departmentDO = new EnterpriseUserDepartmentDO(userId, deptId, Boolean.FALSE);
                dos.add(departmentDO);
            }
        });
        //批量新增
        batchInsertOrDelUserDepartment(dos, eid, userDepartments);

    }

    @Override
    public void syncUserDepartmentAuth(String eid, String userId, List<String> deptIds) {
        if (CollectionUtils.isEmpty(deptIds)) {
            return;
        }
        //该用户在cool中的部门列表,该部门id为cool的id。
        List<EnterpriseUserDepartmentDO> userDepartmentsAuth = enterpriseUserDepartmentMapper.selectUserDeptAuthByUserId(eid, userId);
        Map<String, EnterpriseUserDepartmentDO> userDeptAuthMap = ListUtils.emptyIfNull(userDepartmentsAuth)
                .stream()
                .collect(Collectors.toMap(EnterpriseUserDepartmentDO::getDepartmentId, data -> data, (a, b) -> a));
        List<EnterpriseUserDepartmentDO> enterpriseUserDepartmentDOS = new ArrayList<>();
        for (String deptId : deptIds) {
            EnterpriseUserDepartmentDO userDepartmentDO = userDeptAuthMap.get(deptId);
            if (Objects.nonNull(userDepartmentDO)) {
                userDepartmentsAuth.remove(userDepartmentDO);
            } else {
                //换成批量新增
                EnterpriseUserDepartmentDO departmentDO = new EnterpriseUserDepartmentDO(userId, deptId, Boolean.TRUE);
                enterpriseUserDepartmentDOS.add(departmentDO);
            }
        }
        batchInsertOrDelUserDepartment(enterpriseUserDepartmentDOS, eid, userDepartmentsAuth);
    }

    @Override
    public void syncUserRegionMapping(String eid, String userId, List<String> deptIds, Boolean enableDingSync) {
        //没开启同步、或者没有部门不同步
        if (enableDingSync != null && !enableDingSync || CollectionUtils.isEmpty(deptIds)) {
            return;
        }
        //现在已经存在的用户和区域的映射关系
        List<UserRegionMappingDO> userRegionMappingDOS = userRegionMappingDAO.listUserRegionMappingByUserId(eid, Collections.singletonList(userId));
        Map<String, UserRegionMappingDO> userRegionMap = ListUtils.emptyIfNull(userRegionMappingDOS)
                .stream()
                .collect(Collectors.toMap(UserRegionMappingDO::getRegionId, data -> data, (a, b) -> a));

        //兰湘子合并部门门店权限
        if(LxzEnterpriseEnum.lxzCompany(eid)){
           this.handleLxzDepartmentPermission(eid, deptIds, null);
        }

        //db查询区域表的数据，找到映射的区域
        List<Long> regionIds = regionMapper.selectRegionIdsBySynDingDeptIds(eid, deptIds);
        // 如果是乐乐茶 往上找一级 判断是不是门店  是 把上级是门店的移在门店下
        String storeIsLeafNode = redisUtilPool.hashGet(RedisConstant.STORE_ARE_NON_LEAF_NODES, eid);
        if(StringUtils.isNotBlank(storeIsLeafNode)){
            // 查出parentid，再去region表查上级是不是门店  加到regionIds上
            List<Long> deptIdList = deptIds.stream().map(Long::valueOf).collect(Collectors.toList());
            List<Long> parentDeptIdList = sysDepartmentService.listParentIdByIdList(eid, deptIdList);
            if (CollectionUtils.isNotEmpty(parentDeptIdList)) {
                List<String> parentDeptIdStrs = parentDeptIdList.stream().map(String::valueOf).collect(Collectors.toList());
                List<Long> parentRegionIds = regionMapper.selectStoreRegionIdsBySynDingDeptIds(eid, parentDeptIdStrs);
                if (CollectionUtils.isNotEmpty(parentRegionIds)) {
                    if(CollectionUtils.isEmpty(regionIds)){
                        regionIds = Lists.newArrayList();
                    }
                    regionIds.addAll(parentRegionIds);
                }
            }
        }

        if (CollectionUtils.isEmpty(regionIds)) {
            //没有任何映射的区域放在未分组下
            regionIds.add(SyncConfig.UNGROUPED_DEPT_ID);
        }
        List<UserRegionMappingDO> userRegionMappings = new ArrayList<>();
        for (Long regionId : regionIds) {
            UserRegionMappingDO userRegionMappingDO = userRegionMap.get(String.valueOf(regionId));
            if (Objects.nonNull(userRegionMappingDO)) {
                userRegionMappingDOS.remove(userRegionMappingDO);
            } else {
                //换成批量新增
                UserRegionMappingDO userRegion = new UserRegionMappingDO();
                userRegion.setUserId(userId);
                userRegion.setCreateTime(System.currentTimeMillis());
                userRegion.setUpdateTime(System.currentTimeMillis());
                userRegion.setRegionId(String.valueOf(regionId));
                userRegion.setSource(UserAuthMappingSourceEnum.SYNC.getCode());
                userRegionMappings.add(userRegion);
            }
            if (userRegionMappings.size() > SyncConfig.DEFAULT_BATCH_MAX_SIZE) {
                userRegionMappingDAO.batchInsertRegionMapping(eid, userRegionMappings);
                userRegionMappings.clear();
            }
        }

        //批量新增
        if (CollectionUtils.isNotEmpty(userRegionMappings)) {
            Lists.partition(userRegionMappings, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
                userRegionMappingDAO.batchInsertRegionMapping(eid, p);
            });
        }
        //删除用户移除的区域关系
        if (CollectionUtils.isNotEmpty(userRegionMappingDOS)) {
            List<Integer> deleteUserRegions = userRegionMappingDOS.stream().filter(e -> !UserAuthMappingSourceEnum.CREATE.getCode().equals(e.getSource()))
                    .map(UserRegionMappingDO::getId)
                    .collect(Collectors.toList());
            userRegionMappingDAO.deletedByIds(eid, deleteUserRegions);
        }

    }

    @Override
    public void syncSubordinateMapping(String eid, String userId, List<String> deptIds) {
        //清除下属关系映射
        subordinateMappingDAO.deletedByUserIds(eid, Arrays.asList(userId));
        /*if (CollectionUtils.isEmpty(deptIds)) {
            return;
        }*/
        //db查询区域表的数据，找到映射的区域
        // List<Long> regionIds = regionMapper.selectRegionIdsBySynDingDeptIds(eid, deptIds);
        //我的下属部门数据
        List<SubordinateMappingDO> subordinateMapping = new ArrayList<>();
        SubordinateMappingDO subordinateMappingDO = fillDefaultAutoSubordinate(userId, UserSelectRangeEnum.DEFINE.getCode(), SubordinateSourceEnum.AUTO.getCode());
        subordinateMapping.add(subordinateMappingDO);
       /* ListUtils.emptyIfNull(regionIds)
                .stream()
                .forEach(item -> {
                    SubordinateMappingDO subordinateMappingDO = new SubordinateMappingDO();
                    subordinateMappingDO.setUserId(userId);
                    subordinateMappingDO.setRegionId(String.valueOf(item));
                    subordinateMappingDO.setType(SyncConfig.ZERO);
                    subordinateMappingDO.setCreateTime(System.currentTimeMillis());
                    subordinateMappingDO.setUpdateTime(System.currentTimeMillis());
                    subordinateMapping.add(subordinateMappingDO);
                });*/
        //我的下属
        if (CollectionUtils.isNotEmpty(subordinateMapping)) {
            //先删除，在新增，进行覆盖我的下级数据
            subordinateMappingDAO.batchInsertSubordinateMapping(eid, subordinateMapping);
        }
    }

    private SubordinateMappingDO fillDefaultAutoSubordinate(String userId, String userRange, String source) {
        SubordinateMappingDO subordinateMappingDO = new SubordinateMappingDO();
        subordinateMappingDO.setUserId(userId);
        subordinateMappingDO.setRegionId(Constants.ZERO_STR);
        subordinateMappingDO.setCreateId(Constants.SYSTEM);
        subordinateMappingDO.setUpdateId(Constants.SYSTEM);
        subordinateMappingDO.setType(Constants.INDEX_ZERO);
        subordinateMappingDO.setUserRange(userRange);
        subordinateMappingDO.setSource(source);
        return subordinateMappingDO;
    }

    /**
     * 批量新增或者删除用户和部门的关系
     * @param enterpriseUserDepartmentDOS
     * @param eid
     * @param userDepartmentsAuth
     */
    private void batchInsertOrDelUserDepartment(List<EnterpriseUserDepartmentDO> enterpriseUserDepartmentDOS, String eid,
                                                List<EnterpriseUserDepartmentDO> userDepartmentsAuth) {
        //批量新增
        if (CollectionUtils.isNotEmpty(enterpriseUserDepartmentDOS)) {
            Lists.partition(enterpriseUserDepartmentDOS, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
                enterpriseUserDepartmentMapper.batchInsert(eid, p);
            });
        }
        if (CollectionUtils.isNotEmpty(userDepartmentsAuth)) {
            List<Integer> deleteUserDeptIds = userDepartmentsAuth.stream()
                    .map(EnterpriseUserDepartmentDO::getId)
                    .collect(Collectors.toList());
            enterpriseUserDepartmentMapper.deleteByIdList(eid, deleteUserDeptIds);
        }
    }

    @Override
    public void syncUserAuth(String userId, String eid, List<String> deptIds, EnterpriseSettingVO setting, String position,
                             OapiV2UserGetResponse.UserGetResponse response, Map<String, Boolean> leaderMap, String appType) {
        // 如果是乐乐茶 往上找一级 判断是不是门店  是 把上级是门店的移在门店下
        String storeIsLeafNode = redisUtilPool.hashGet(RedisConstant.STORE_ARE_NON_LEAF_NODES, eid);
        if(StringUtils.isNotBlank(storeIsLeafNode)){
            // 查出parentid，再去region表查上级是不是门店  加到regionIds上
            List<Long> deptIdList = deptIds.stream().map(data -> Long.valueOf(data)).collect(Collectors.toList());
            List<Long> parentDeptIdList = sysDepartmentService.listParentIdByIdList(eid, deptIdList);
            if (CollectionUtils.isNotEmpty(parentDeptIdList)) {
                List<String> parentDeptIdStrs = parentDeptIdList.stream().map(data -> String.valueOf(data)).collect(Collectors.toList());
                List<String> parentDeptIds = regionMapper.retainStoreTypeBySynDingDeptIds(eid, parentDeptIdStrs);
                if (CollectionUtils.isNotEmpty(parentDeptIds)) {
                    if(CollectionUtils.isEmpty(deptIds)){
                        deptIds = Lists.newArrayList();
                    }
                    deptIds.addAll(parentDeptIds);
                }
            }
        }

        List<UserAuthMappingDO> dingUserAuthList = getSyncAuthMappingList(response, leaderMap, position, userId, eid, deptIds, setting, appType);
        //用户在cool中已有的可视范围映射关系
        List<UserAuthMappingDO> coolUserAuthList = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userId);
        Map<String, UserAuthMappingDO> userAuthMap = ListUtils.emptyIfNull(coolUserAuthList)
                .stream()
                .collect(Collectors.toMap(UserAuthMappingDO::getMappingId, data -> data, (a, b) -> a));
        for (UserAuthMappingDO userAuth : dingUserAuthList) {
            UserAuthMappingDO auth = userAuthMap == null ? null : userAuthMap.get(userAuth.getMappingId());
            if (auth == null) {
                userAuthMappingMapper.insertUserAuthMapping(eid, userAuth);
            } else {
                coolUserAuthList.remove(auth);
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
    }

    /**
     * 获得该用户拥有的可视化权限
     * @param response 钉钉获取的用户详情，允许为空.企业微信同步时，该对象为空
     * @param map 用户对应的部门职级，key:部门id,value:对应部门是否是上级
     * @param position 用户职位
     * @param userId
     * @param eid
     * @param deptIds 钉钉-企微部门id列表
     * @param setting 企业配置信息
     * @return: java.util.List<com.coolcollege.intelligent.model.authentication.UserAuthMappingDO>
     * @Author: xugangkun
     * @Date: 2021/3/24 11:09
     */
    private List<UserAuthMappingDO> getSyncAuthMappingList(OapiV2UserGetResponse.UserGetResponse response, Map<String, Boolean> map
            , String position, String userId, String eid, List<String> deptIds, EnterpriseSettingVO setting, String appType) {
        //该用户相关区域 3-25现在用户可视权限不涉及门店，只需要考虑区域,而且区域信息表中会冗余门店的信息，根据区域类型判断是否是门店
        if(CollectionUtils.isEmpty(deptIds)){
            return Lists.newArrayList();
        }
        List<RegionDO> regions = regionMapper.getRegionByDingDeptIds(eid, deptIds);
//            List<StoreDO> stores = storeMapper.getStoreByDingDeptIds(eid, dingDeptIds); 获得所有的门店类型 2021-5-6 区域表修改，不在冗余门店类型的区域
        Map<String, String> nameDeptIdMap = new HashMap<>();
        //兰湘子合并部门门店权限
        if(LxzEnterpriseEnum.lxzCompany(eid)){
            nameDeptIdMap = this.handleLxzDepartmentPermission(eid, deptIds, regions);
        }

        //添加人员权限映射表，即用户可见范围
        List<UserAuthMappingDO> userAuthMappingDOList = new ArrayList<>();
        //判断是否是自定义区域权限
        Boolean isCustomizeRole = checkCustomizeRole(response, setting, position);
        //用户在该区域(部门)是管理员时，拥有可见权限
        Map<String, String> finalNameDeptIdMap = nameDeptIdMap;
        regions.forEach(region -> {
            UserAuthMappingDO mappingDO = new UserAuthMappingDO(userId, region.getId().toString(), UserAuthMappingTypeEnum.REGION.getCode()
                    , UserAuthMappingSourceEnum.SYNC.getCode(), "system", System.currentTimeMillis());
            //该区域是门店类型，获得配置信息，开启门店权限规则，则添加
            if (RegionTypeEnum.STORE.getType().equals(region.getRegionType())) {
                //把区域权限id改为门店id
//                StoreDO store = storeMapper.getStoreBySynId(eid, region.getSynDingDeptId());
//                mappingDO.setMappingId(store.getStoreId());
//                mappingDO.setType(UserAuthMappingTypeEnum.STORE.getCode());
                if (setting.getStoreNodeRuleOpen()) {
                    userAuthMappingDOList.add(mappingDO);
                    return;
                }
            }
            //如果是区域类型，1.开启部门的领导者赋权配置，且为该区域(部门)领导，添加
            Boolean isRegionLeader = map.get(region.getSynDingDeptId());
           //兰湘子合并部门领导权限
            if(LxzEnterpriseEnum.lxzCompany(eid) &&  (isRegionLeader == null || !isRegionLeader)){
               String leadSyncDingDeptId = finalNameDeptIdMap.get(region.getName());
               if(StringUtils.isNotBlank(leadSyncDingDeptId)){
                   isRegionLeader = map.get(leadSyncDingDeptId);
               }
            }
            if (setting.getRegionLeaderRuleOpen() && isRegionLeader != null && isRegionLeader) {
                userAuthMappingDOList.add(mappingDO);
                return;
            }
            //2.自定义区域权限配置
            if (isCustomizeRole) {
                userAuthMappingDOList.add(mappingDO);
                return;
            }
        });
        return userAuthMappingDOList;
    }

    /**
     * 判断是否开启了 符合设置的职位，则赋予该区域权限
     * @param response
     * @param setting
     * @param position
     * @author: xugangkun
     * @return java.lang.Boolean
     * @date: 2021/10/27 14:40
     */
    private Boolean checkCustomizeRole(OapiV2UserGetResponse.UserGetResponse response, EnterpriseSettingVO setting, String position) {
        String customizeRoleContent = setting.getCustomizeRoleContent();
        //如果自定义配置内容为空，或者未开启自定义配置，返回false
        if (StrUtil.isBlank(customizeRoleContent) || !setting.getCustomizeRoleRuleOpen()) {
            return false;
        }
        //删除空格和双引号
        customizeRoleContent = customizeRoleContent.replace(" ", "");
        customizeRoleContent= customizeRoleContent.replace("\"", "");
        String[] customizeRoles = customizeRoleContent.split(",");
        String[] customizeRoles2 = customizeRoleContent.split("，");
        if (response != null) {
            if (isCustomizeRole(response, customizeRoles) || isCustomizeRole(response, customizeRoles2)) {
                return true;
            }
        } else {
            if (isCustomizeRoleQw(position, customizeRoles) || isCustomizeRoleQw(position, customizeRoles2)) {
                return true;
            }
        }
        return false;
    }

    private Boolean isCustomizeRole(OapiV2UserGetResponse.UserGetResponse response, String[] customizeRoles) {
        List<OapiV2UserGetResponse.UserRole> roleList = response.getRoleList();
        for (String customizeRole : customizeRoles) {
            //职位与自定义配置相符
            if (StrUtil.isNotBlank(response.getTitle()) && customizeRole.equals(response.getTitle())) {
                return true;
            }
            //角色与自定义配置相符
            OapiV2UserGetResponse.UserRole userRole = Optional.ofNullable(roleList).orElseGet(ArrayList::new).stream()
                    .filter(role -> customizeRole.equals(role.getName()))
                    .findFirst().orElse(null);
            if (userRole != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 企业微信同步，判断用户职位是否符合配置的区域权限职位
     * @param position
     * @param customizeRoles
     * @author: xugangkun
     * @return java.lang.Boolean
     * @date: 2021/10/27 14:35
     */
    private Boolean isCustomizeRoleQw(String position, String[] customizeRoles) {
        for (String customizeRole : customizeRoles) {
            //职位与自定义配置相符
            if (StrUtil.isNotBlank(position) && customizeRole.equals(position)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 批量删除用户对应的映射关系
     * @param allUserRoleDeleteList 用户角色映射
     * @param allUserDeptDeleteList 用户部门映射
     * @param allUserAuthDeleteList 用户可视化范围映射
     * @param eid 企业di
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/25 11:41
     */
    private void deleteUserAndMappings(List<Long> allUserRoleDeleteList, List<Integer> allUserDeptDeleteList, List<Long> allUserAuthDeleteList, String eid) {
        if (allUserRoleDeleteList != null) {
            Lists.partition(allUserRoleDeleteList, Constants.BATCH_INSERT_COUNT).forEach(deleteUserRoleIds -> {
                enterpriseUserRoleMapper.deleteBatchByPrimaryKey(eid, deleteUserRoleIds);
            });
        }
        if (allUserDeptDeleteList != null) {
            Lists.partition(allUserDeptDeleteList, Constants.BATCH_INSERT_COUNT).forEach(userDeptDeleteList -> {
                enterpriseUserDepartmentMapper.deleteByIdList(eid, userDeptDeleteList);
            });
        }
        if (allUserAuthDeleteList != null) {
            Lists.partition(allUserAuthDeleteList, Constants.BATCH_INSERT_COUNT).forEach(deleteUserAuths -> {
                userAuthMappingMapper.deleteAuthMappingByIds(eid, deleteUserAuths);
            });
        }
    }

    @Override
    public EnterpriseUserDO syncOnePartyUser(String eid, String userId, EnterpriseUserDTO dingEnterpriseUserDTO) {
        // 1.构建用户DO
        EnterpriseUserDO enterpriseUser = convertFactory.convertEnterpriseUserDTO2EnterpriseUserDO(dingEnterpriseUserDTO);
        // 2.设置该用户所属的部门id列表
        if (CollectionUtils.isNotEmpty(dingEnterpriseUserDTO.getDepartmentLists())) {
            EnterpriseUserRequest enterpriseUserRequest = new EnterpriseUserRequest();
            enterpriseUserRequest.setEnterpriseUserDO(enterpriseUser);
            enterpriseUserRequest.setDepartmentLists(dingEnterpriseUserDTO.getDepartmentLists());
            enterpriseUserRequest.setDepartment("[" + dingEnterpriseUserDTO.getDepartmentLists().stream().map(String::valueOf).collect(Collectors.joining(",")) + "]");
            TwoResultTuple<Set<String>, Map<String, String>> tuple = sysDepartmentService.getAllDeptInfo(eid);
            enterpriseUserService.updateUserDeptPath(enterpriseUserRequest, tuple.second);
        }
        enterpriseUser.setUserStatus(UserStatusEnum.NORMAL.getCode());
        enterpriseUser.setCreateTime(new Date());
        EnterpriseUserDO coolEnterpriseUser = enterpriseUserDao.selectByUserIdIgnoreActive(eid, userId);
        // 3.插入或者更新用户信息
        if (Objects.isNull(coolEnterpriseUser)) {
            if(Objects.isNull(enterpriseUser.getId()) || !AIEnum.AI_ID.getCode().equals(enterpriseUser.getId())) {
                enterpriseUser.setId(UUIDUtils.get32UUID());
            }
            enterpriseUserService.insertEnterpriseUser(eid, enterpriseUser);
        } else {
            enterpriseUserDao.updateEnterpriseUser(eid, enterpriseUser);
        }
        // 4.更新该用户的部门信息
        syncUserDepartment(eid, userId, dingEnterpriseUserDTO.getDepartmentLists());
        // 5.同步用户部门权限
        syncUserDepartmentAuth(eid, userId, dingEnterpriseUserDTO.getIsLeaderInDepts());
        // 6.同步用户的角色信息
        syncDingOnePartyUserRole(eid, userId, dingEnterpriseUserDTO.getRoles());
        return enterpriseUser;
    }

    @Override
    public EnterpriseUserDTO initAiUser(String eid) {
        EnterpriseUserDTO dingEnterpriseUserDTO =  new EnterpriseUserDTO();
        dingEnterpriseUserDTO.setId(AIEnum.AI_ID.getCode());
        dingEnterpriseUserDTO.setName(AIEnum.AI_NAME.getCode());
        dingEnterpriseUserDTO.setUserId(AIEnum.AI_USERID.getCode());
        dingEnterpriseUserDTO.setMobile(AIEnum.AI_MOBILE.getCode());
        dingEnterpriseUserDTO.setRoles(AIEnum.AI_ROLES.getCode());
        dingEnterpriseUserDTO.setUnionid(AIEnum.AI_UUID.getCode());
        dingEnterpriseUserDTO.setIsAdmin(Boolean.TRUE);
        dingEnterpriseUserDTO.setActive(Boolean.TRUE);
        dingEnterpriseUserDTO.setRoles(sysRoleService.getRoleIdByRoleEnum(eid, Role.MASTER.getRoleEnum()).toString());
        dingEnterpriseUserDTO.setScopeList(Lists.newArrayList(regionMapper.selectRootRegionId(eid)));
        return dingEnterpriseUserDTO;
    }


    @Override
    public void syncDingOnePartyUserAuth(String eid, String userId, List<Long> dingDeptIds) {
        // 1.查询cool中已有权限，对比钉钉权限范围
        List<UserAuthMappingDO> coolUserAuthList = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userId);
        if(CollectionUtils.isEmpty(dingDeptIds)) {
            if(CollectionUtils.isEmpty(coolUserAuthList)) {
                return;
            }
        }else {
            // 2.根据钉钉权限范围构建用户权限关系
            List<UserAuthMappingDO> dingUserAuthList = Lists.newArrayList();
            // 根据钉钉权限范围查询cool中的区域（门店区域化所以区域包含门店）
            List<RegionDO> regions = regionMapper.getRegionByDingDeptIds(eid, ListOptUtils.longListConvertStringList(dingDeptIds));
            for (RegionDO region : regions) {
                UserAuthMappingDO mappingDO = new UserAuthMappingDO(userId, region.getId().toString(), UserAuthMappingTypeEnum.REGION.getCode()
                        , UserAuthMappingSourceEnum.SYNC.getCode(), "system", System.currentTimeMillis());
                dingUserAuthList.add(mappingDO);
            }
            Map<String, UserAuthMappingDO> userAuthMap = ListUtils.emptyIfNull(coolUserAuthList)
                    .stream()
                    .collect(Collectors.toMap(UserAuthMappingDO::getMappingId, data -> data, (a, b) -> a));
            for (UserAuthMappingDO userAuth : dingUserAuthList) {
                UserAuthMappingDO auth = userAuthMap == null ? null : userAuthMap.get(userAuth.getMappingId());
                if (auth == null) {
                    // cool中不存在的权限做新增
                    userAuthMappingMapper.insertUserAuthMapping(eid, userAuth);
                } else {
                    // cool中存在的权限不处理
                    coolUserAuthList.remove(auth);
                }
            }
        }
        // cool中多出的权限做删除
        if (CollectionUtils.isNotEmpty(coolUserAuthList)) {
            List<Long> deleteAuthIds = coolUserAuthList.stream()
                    .filter(data -> UserAuthMappingSourceEnum.SYNC.getCode().equals(data.getSource()) )
                    .map(UserAuthMappingDO::getId).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(deleteAuthIds)){
                userAuthMappingMapper.deleteAuthMappingByIds(eid, deleteAuthIds);
            }
        }
    }

    /**
     * 同步用户角色映射
     * @param eid
     * @param userId
     * @param roles
     */
    private void syncDingOnePartyUserRole(String eid, String userId, String roles) {
        if(StringUtils.isBlank(roles)) {
            return;
        }
        List<String> userRoleIdList = Arrays.stream(StringUtils.split(roles, Constants.COMMA)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(userRoleIdList)) {
            return;
        }
        //获得门店中角色列表
        List<SysRoleDO> coolRoleList = sysRoleMapper.selectBySynDingRoleIdAndSource(eid, null, null);
        // List<SysRoleDO> coolRoleList = sysRoleMapper.selectSysRoleBySource(eid, PositionSourceEnum.SYNC.getValue());
        List<Long> coolRoleIds = ListUtils.emptyIfNull(coolRoleList).stream().map(SysRoleDO::getSynDingRoleId).collect(Collectors.toList());
        //获得用户在cool中的角色列表,需要筛选出用户与钉钉同步角色的映射关系,同时获得该用户与已删除角色的映射关系，这些也需要删除
        List<EnterpriseUserRole> userRoles = enterpriseUserRoleMapper.selectMdtRoleMappingByUserId(eid, userId);
        //如果库中没有相应的映射关系，设置map为null，否则设置map k=roleId,v=映射对象
        Map<String, EnterpriseUserRole> roleMap = ListUtils.emptyIfNull(userRoles).stream().collect(Collectors.toMap(EnterpriseUserRole::getRoleId, data -> data, (a, b) -> a));
        ListUtils.emptyIfNull(userRoleIdList).forEach(userRoleId -> {
            EnterpriseUserRole check = MapUtils.isEmpty(roleMap) ? null : roleMap.get(userRoleId);
            if (check != null) {
                userRoles.remove(check);
            } else if (CollectionUtils.isNotEmpty(coolRoleIds)&&coolRoleIds.contains(Long.parseLong(userRoleId))) {
                enterpriseUserRoleMapper.save(eid, new EnterpriseUserRole(userRoleId, userId, RoleSyncTypeEnum.SYNC.getCode()));
            }
        });
        if (userRoles != null && userRoles.size() != 0) {
            //删除对应的映射关系
            List<Long> deleteUserRoleIds = userRoles.stream().map(EnterpriseUserRole::getId).collect(Collectors.toList());
            enterpriseUserRoleMapper.deleteBatchByPrimaryKey(eid, deleteUserRoleIds);
        }
    }

    private void deleteUserSyncPosition(String eid, String userId){
        log.info("刪除用戶同步职位权限,eid:{},userId:{}", eid, userId);
        //用户与钉钉职位的映射关系
        List<UserDingPositionDTO> userDingPositions = enterpriseUserRoleMapper.selectUserDingPosition(eid, userId);
        if(CollectionUtils.isNotEmpty(userDingPositions)){
            List<Long> deleteIds = userDingPositions.stream().map(UserDingPositionDTO::getUserRoleId).collect(Collectors.toList());
            enterpriseUserRoleMapper.deleteBatchByPrimaryKey(eid, deleteIds);
        }
    }

    /**
     * 处理直属上级l
     *
     * @param response
     * @param userId
     * @param eid
     * @param setting
     */
    private void syncDingUserDirectSuperior(OapiV2UserGetResponse.UserGetResponse response, String userId, String eid
            , EnterpriseSettingVO setting) {
        log.info("syncDingUserDirectSuperior同步上级userId:{},managerUserid:{}", userId, response.getManagerUserid());
        if (setting.getSyncDirectSuperior() != null && setting.getSyncDirectSuperior()) {
            log.info("syncDingUserDirectSuperior开始开始同步上级userId:{},managerUserid:{}", userId, response.getManagerUserid());
            SubordinateMappingDO subordinateMappingDO = subordinateMappingDAO.selectByUserIdAndType(eid, userId);
            if (StringUtils.isBlank(response.getManagerUserid())) {
                if (subordinateMappingDO != null && SubordinateSourceEnum.AUTO.getCode().equals(subordinateMappingDO.getSource())) {
                    //直接删除当前人的直属上级(同步过来的)
                    subordinateMappingDAO.deletedByUserIdsAndType(eid, Lists.newArrayList(userId));
                }
                return;
            }
            if (subordinateMappingDO == null) {
                // 新增直属上级
                SubordinateMappingDO subordinateMapping = new SubordinateMappingDO();
                subordinateMapping.setUserId(userId);
                subordinateMapping.setPersonalId(response.getManagerUserid());
                subordinateMapping.setType(Constants.INDEX_ONE);
                subordinateMapping.setSource(SubordinateSourceEnum.AUTO.getCode());
                subordinateMappingDAO.batchInsertSubordinateMapping(eid, Lists.newArrayList(subordinateMapping));
            }
            if (subordinateMappingDO != null) {
                //只更改自动同步的上级
                if (SubordinateSourceEnum.AUTO.getCode().equals(subordinateMappingDO.getSource())) {
                    log.info("syncDingUserDirectSuperior自动同步上级领导userId:{},managerUserid:{}", userId, response.getManagerUserid());
                    //修改直属上级
                    subordinateMappingDAO.updateByUserIdAndType(eid, userId, response.getManagerUserid(), userId);
                    return;
                }
                log.info("syncDingUserDirectSuperior手动创建上级不改动userId:{},managerUserid:{}", userId, response.getManagerUserid());
            }
        }
    }

    @Override
    public void syncFreezeUser(String eid, String userId) {
        EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserIdIgnoreActive(eid, userId);
        if(enterpriseUserDO != null){
            enterpriseUserDO.setUserStatus(2);
            enterpriseUserDao.updateEnterpriseUser(eid,enterpriseUserDO);
        }

    }

    /**
     * 处理兰湘子合并部门门店权限
     * 特别处理厨政中心及其子部门的区域权限转换
     */
    private Map<String, String> handleLxzDepartmentPermission(String eid, List<String> deptIds, List<RegionDO> regions) {
        Map<String, String> nameDeptIdMap = new HashMap<>();
        List<SysDepartmentDO> sysDepartmentDOList = sysDepartmentMapper.getDepartmentList(eid, deptIds);
        SysDepartmentDO centerDO = sysDepartmentMapper.getDepartmentByName(eid, Constants.CHE_ZHENG_CENTER);

        if (centerDO != null && CollectionUtils.isNotEmpty(sysDepartmentDOList)) {
            // 获取厨政中心的子部门
            List<SysDepartmentDO> centerSubDOList = sysDepartmentDOList.stream()
                    .filter(sysDepartmentDO -> sysDepartmentDO.getParentIds()
                            .contains(Constants.SLASH + centerDO.getId() + Constants.SLASH))
                    .collect(Collectors.toList());

            // 转换部门为区域权限
            if (CollectionUtils.isNotEmpty(centerSubDOList)) {
                List<String> centerSubNameList = centerSubDOList.stream()
                        .map(e -> e.getName().replace(Constants.CHE_ZHENG_BACK_CENTER_NODE, ""))
                        .collect(Collectors.toList());

                nameDeptIdMap = centerSubDOList.stream()
                        .collect(Collectors.toMap(
                                e -> e.getName().replace(Constants.CHE_ZHENG_BACK_CENTER_NODE, ""),
                                SysDepartmentDO::getId
                        ));

                List<RegionDO> centerSubRegionList = regionMapper.listRegionsByNames(eid, centerSubNameList);

                List<String> deptIdList = centerSubRegionList.stream()
                        .map(RegionDO::getSynDingDeptId)
                        .collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(centerSubRegionList) && regions != null) {
                    regions.addAll(centerSubRegionList);
                }

                if(regions == null && deptIds !=null){
                    deptIds.addAll(deptIdList);
                }
            }
        }
        return nameDeptIdMap;
    }
}
