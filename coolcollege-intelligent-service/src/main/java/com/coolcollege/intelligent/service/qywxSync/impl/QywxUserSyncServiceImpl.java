package com.coolcollege.intelligent.service.qywxSync.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.TwoResultTuple;
import com.coolcollege.intelligent.common.enums.RoleSyncTypeEnum;
import com.coolcollege.intelligent.common.enums.baili.BailiEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.songxia.SongXiaEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserDepartmentMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserRole;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.service.dingSync.DingUserSyncService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserMappingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.qywxSync.QywxUserSyncService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 企业微信用户同步实现类
 *
 * @ClassName: QywxUserSyncServiceImpl
 * @Author: wxp
 * @Date: 2021/6/12 14:38
 */
@Slf4j
@Service(value = "qywxUserSyncService")
public class QywxUserSyncServiceImpl implements QywxUserSyncService {

    @Autowired
    private EnterpriseUserMapper enterpriseUserMapper;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Resource
    private EnterpriseUserDepartmentMapper enterpriseUserDepartmentMapper;

    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;

    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Resource
    private EnterpriseUserMappingMapper enterpriseUserMappingMapper;

    @Resource
    private EnterpriseSettingService enterpriseSettingService;

    @Resource
    private SysDepartmentService sysDepartmentService;

    @Resource
    private ChatService chatService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private EnterpriseUserMappingService enterpriseUserMappingService;

    @Autowired
    private DingUserSyncService dingUserSyncService;

    @Override
    public void syncWeComUser(String corpId, String userId, String accessToken, String eid, String dbName,
                              String appType) {
        DataSourceHelper.reset();
        //企业配置信息
        EnterpriseSettingVO setting = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        EnterpriseUserRequest qywxEnterpriseUser;
        try {
            DataSourceHelper.changeToSpecificDataSource(dbName);
            Long employeeRoleId = sysRoleService.getRoleIdByRoleEnum(eid, Role.EMPLOYEE.getRoleEnum());
            //获得企业微信用户详情
            qywxEnterpriseUser = chatService.getSelfUserDetail(corpId, userId, false, employeeRoleId.toString(), appType);
            DataSourceHelper.reset();
        } catch (ApiException e) {
            log.error("企业微信,当前用户同步失败 {} ", userId, e);
            return;
        }
        log.info("获得企业微信用户详情 {} ", JSONObject.toJSONString(qywxEnterpriseUser));
        //使用userId获得企业微信用户信息后，转换为门店的userid
        userId = corpId + "_" + userId;
        qywxEnterpriseUser.getEnterpriseUserDO().setUserId(userId);
        qywxEnterpriseUser.getEnterpriseUserDO().setUserStatus(UserStatusEnum.NORMAL.getCode());
        log.info("微信企业配置信息 {} ", JSONObject.toJSONString(setting));
        //判断用户是否是管理员
        List<String> adminList = chatService.getWxAdminList(corpId, appType);
        log.info("企业微信管理员列表：{}", adminList.toString());
        if (CollectionUtils.isNotEmpty(adminList)) {
            adminList.forEach(a -> {
                if ((corpId + Constants.UNDERLINE + a).equals(qywxEnterpriseUser.getEnterpriseUserDO().getUserId())) {
                    qywxEnterpriseUser.getEnterpriseUserDO().setIsAdmin(Boolean.TRUE);
                }
            });
        }
        //先处理企业库
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //设置用户部门全路劲
        TwoResultTuple<Set<String>, Map<String, String>> tuple = sysDepartmentService.getAllDeptInfo(eid);
        enterpriseUserService.updateUserDeptPath(qywxEnterpriseUser, tuple.second);
        EnterpriseUserDO coolEnterpriseUser = enterpriseUserDao.selectByUserIdIgnoreActive(eid, userId);
        log.info("数智门店企业用户信息 {} ", JSONObject.toJSONString(coolEnterpriseUser));
        if (coolEnterpriseUser != null) {
            //设置下级是否变动
            qywxEnterpriseUser.getEnterpriseUserDO().setSubordinateChange(coolEnterpriseUser.getSubordinateChange());
            qywxEnterpriseUser.getEnterpriseUserDO().setId(coolEnterpriseUser.getId());
            if (coolEnterpriseUser.getUserStatus() != null) {
                qywxEnterpriseUser.getEnterpriseUserDO().setUserStatus(coolEnterpriseUser.getUserStatus());
            }
            //因为企业微信每次用户更新都会把用户的名字覆盖为userId,不对用户名称进行更新(代开发应用除外)
            if (StringUtils.isNotBlank(coolEnterpriseUser.getName()) && !AppTypeEnum.isWxSelfAndPrivateType(appType)) {
                qywxEnterpriseUser.getEnterpriseUserDO().setName(coolEnterpriseUser.getName());
            }
        }
        syncEntUser(qywxEnterpriseUser, eid, setting, appType);
        //处理平台库
        DataSourceHelper.reset();
        EnterpriseUserDO coolConfigUser = enterpriseUserService.selectConfigUserByUserId(userId);
        log.info("数智门店平台库企业用户信息 {} ", JSONObject.toJSONString(coolConfigUser));
        String configUserId = coolConfigUser == null ? UUIDUtils.get32UUID() : coolConfigUser.getId();
        qywxEnterpriseUser.getEnterpriseUserDO().setId(configUserId);
        syncConfigUser(qywxEnterpriseUser.getEnterpriseUserDO(), eid, coolConfigUser == null);
    }

    /**
     * 同步平台库的用户信息
     * @param enterpriseUser
     * @param eid
     * @param flag 是否新增
     * @return: void
     * @Author: wxp
     * @Date: 2021/6/12 11:48
     */
    @Override
    public void syncConfigUser(EnterpriseUserDO enterpriseUser, String eid, Boolean flag) {
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
        enterpriseUserMappingDO.setUnionid(enterpriseUser.getUnionid());
        enterpriseUserMappingDO.setUserStatus(enterpriseUser.getUserStatus());
        enterpriseUserMappingDO.setEnterpriseId(eid);
        enterpriseUserMappingDO.setCreateTime(new Date());
        /*EnterpriseUserMappingDO checkDo = enterpriseUserMappingMapper.selectByEidAndUserId(eid, enterpriseUser.getId());
        if (checkDo == null) {
            enterpriseUserMappingMapper.save(enterpriseUserMappingDO);
        }*/
        enterpriseUserMappingService.saveEnterpriseUserMapping(enterpriseUserMappingDO);
    }


    /**
     * 同步修改企业用户
     * @param request
     * @param eid
     * @return: void
     * @Author: wxp
     * @Date: 2021/6/12 15:02
     */
    @Override
    public void syncEntUser(EnterpriseUserRequest request, String eid, EnterpriseSettingVO setting, String appType) {
        EnterpriseUserDO enterpriseUser = request.getEnterpriseUserDO();
        if (Objects.isNull(enterpriseUser)) {
            return;
        }
        Boolean isAdd = enterpriseUser.getId() == null;
        String userId = enterpriseUser.getUserId();
        //TODO 一.插入或者更新用户信息
        if (enterpriseUser.getId() == null) {
            enterpriseUser.setId(UUIDUtils.get32UUID());
            enterpriseUser.setSubordinateRange(setting.getManageUser());
            enterpriseUserService.insertEnterpriseUser(eid, enterpriseUser);
        } else {
            enterpriseUserDao.updateEnterpriseUser(eid, enterpriseUser);
        }
        //TODO 二.更新该用户的部门信息
        //ding部门id列表
        List<String> qywxDeptIds = CollectionUtils.isEmpty(request.getDepartmentLists()) ? new ArrayList<>() : request.getDepartmentLists();
        //该用户在cool中的部门列表,该部门id为cool的id。
        dingUserSyncService.syncUserDepartment(eid, userId, qywxDeptIds);
        //同步用户的部门权限
        List<String> manageDeptIds = JSONObject.parseArray(request.getEnterpriseUserDO().getIsLeaderInDepts(), String.class);
        dingUserSyncService.syncUserDepartmentAuth(eid, userId, manageDeptIds);
        //同步用户和区域的关系
        dingUserSyncService.syncUserRegionMapping(eid, userId, ListUtils.emptyIfNull(qywxDeptIds).stream().map(a -> String.valueOf(a)).collect(Collectors.toList()),
                !Objects.equals(setting.getEnableDingSync(), Constants.ENABLE_DING_SYNC_NOT_OPEN));
        //补全该用户的user_region_ids
        enterpriseUserService.updateUserRegionPathList(eid, Arrays.asList(userId));
        if (isAdd && BailiEnterpriseEnum.bailiAffiliatedCompany(eid)) {
            dingUserSyncService.syncSubordinateMapping(eid, userId, ListUtils.emptyIfNull(manageDeptIds).stream().map(a -> String.valueOf(a)).collect(Collectors.toList()));
        }
        //如果用户是企微管理员，同步为门店的管理员
        if (enterpriseUser.getIsAdmin()) {
            EnterpriseUserRole masterRole = enterpriseUserRoleMapper.selectByUserIdAndRoleId(eid, enterpriseUser.getUserId(), Role.MASTER.getId());
            if (masterRole == null) {
                Long roleIdByRoleEnum = sysRoleService.getRoleIdByRoleEnum(eid, Role.MASTER.getRoleEnum());
                enterpriseUserRoleMapper.save(eid, new EnterpriseUserRole(roleIdByRoleEnum.toString(), userId, RoleSyncTypeEnum.SYNC.getCode()));
            }
        }
        //判断是否开启自动同步,未开启直接中断
        if (Constants.ENABLE_DING_SYNC_NOT_OPEN.equals(setting.getEnableDingSync())) {
            //如果用户没有角色，设置为未分配
            if (enterpriseUserRoleMapper.selectCountsByUserId(eid, userId) == 0) {
                Long roleIdByRoleEnum = sysRoleService.getRoleIdByRoleEnum(eid, Role.EMPLOYEE.getRoleEnum());
                enterpriseUserRoleMapper.save(eid, new EnterpriseUserRole(roleIdByRoleEnum.toString(), userId, RoleSyncTypeEnum.SYNC.getCode()));
            }
            return;
        }
        //TODO 三更新该用户的角色信息：新增或者删除
        //角色和职位规则
        String dingSyncRoleRuleDetail = setting.getDingSyncRoleRuleDetail();
        if (setting.getDingSyncRoleRule().equals(SyncConfig.TWO)) {
            //如果配置了同步的职位规则，只有规则内的职位才进行同步
            if(!SyncConfig.checkRoleRule(enterpriseUser.getPosition(), dingSyncRoleRuleDetail) && !setting.getDingSyncRoleRule().equals(SyncConfig.THREE)){
                log.info("同步的职位不在同步规则内：{},同步规则：{}",enterpriseUser.getPosition(), dingSyncRoleRuleDetail);
            } else {
                Map<String, Boolean> leaderMap = new HashMap<>();
                if(CollectionUtils.isNotEmpty(manageDeptIds)){
                    for (String manageDeptId : manageDeptIds) {
                        leaderMap.put(manageDeptId, Boolean.TRUE);
                    }
                }
                dingUserSyncService.syncDingPosition(eid, enterpriseUser.getPosition(), userId, leaderMap);
            }
        }
        //TODO 四.更新该用户区域权限信息(可见区域与门店) 2021-5-6 新增判断字段，存在只添加用户信息以及用户部门信息的场景
        //根据钉钉返回信息，获得该用户拥有的可视化权限
        Map<String, Boolean> leaderMap = new HashMap<>();
        List<String> leaderInDept = new ArrayList<>();
        if (StringUtils.isNotBlank(enterpriseUser.getIsLeaderInDepts())) {
            String leaderDeptStr = enterpriseUser.getIsLeaderInDepts().replaceAll("\\[", "").replaceAll("\\]", "");
            leaderDeptStr = leaderDeptStr.replace(" ", "");
            if(StringUtils.isNotBlank(leaderDeptStr)){
                leaderInDept = Arrays.stream(leaderDeptStr.split(",")).map(String::valueOf).collect(Collectors.toList());
            }
        }
        //自建私服直接去字段
        if(AppTypeEnum.isWxSelfAndPrivateType(appType) && CollectionUtils.isNotEmpty(request.getLeaderInDepts())){
            leaderInDept = request.getLeaderInDepts();
        }
        for (String deptId : qywxDeptIds) {
            leaderMap.put(deptId, leaderInDept.contains(deptId));
        }
        dingUserSyncService.syncUserAuth(userId, eid, qywxDeptIds, setting, enterpriseUser.getPosition(), null, leaderMap, appType);
    }

    @Override
    public void syncDeleteWeComUser(String eid, String userId, String dbName) {
        //删除企业库对应的映射关系
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //松下人员不做删除，改为冻结
        if(SongXiaEnterpriseEnum.songXiaCompany(eid)){
            dingUserSyncService.syncFreezeUser(eid, userId);
        }else{
            syncDeleteEntUser(userId, eid);
            //处理平台库
            DataSourceHelper.reset();
            //删除平台库对应的映射关系
            List<String> userIds = new ArrayList<>();
            userIds.add(userId);
            //删除用户企业映射信息
            enterpriseUserMappingMapper.batchDeleteUserByUserIds(userIds, eid);
        }

    }

    @Override
    public void syncDeleteEntUser(String userId, String eid) {
        // 1.更新用户信息
        EnterpriseUserDO enterpriseUser = enterpriseUserDao.selectByUserId(eid, userId);
        if (enterpriseUser != null) {
            enterpriseUser.setActive(false);
            enterpriseUserDao.updateEnterpriseUser(eid, enterpriseUser);
        }
        // 2.删除用户角色映射关系
        //获得用户在cool中的角色列表
        List<Long> userRoleIds = enterpriseUserRoleMapper.selectIdsByUserId(eid, userId);
        if (CollectionUtils.isNotEmpty(userRoleIds)) {
            enterpriseUserRoleMapper.deleteBatchByPrimaryKey(eid, userRoleIds);
        }
        // 3.删除该用户的部门信息
        List<Integer> userDepartmentIds = enterpriseUserDepartmentMapper.getIdsByUserId(eid, userId);
        if (CollectionUtils.isNotEmpty(userDepartmentIds)) {
            enterpriseUserDepartmentMapper.deleteByIdList(eid, userDepartmentIds);
        }
        // 4.删除该用户的可见范围映射信息
        List<Long> userAuthIds = userAuthMappingMapper.selectIdsByUserId(eid, userId);
        if (CollectionUtils.isNotEmpty(userAuthIds)) {
            userAuthMappingMapper.deleteAuthMappingByIds(eid, userAuthIds);
        }

    }
}
