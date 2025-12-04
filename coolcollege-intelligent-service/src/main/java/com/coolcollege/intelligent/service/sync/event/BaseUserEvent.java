package com.coolcollege.intelligent.service.sync.event;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.TwoResultTuple;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.model.department.dto.DingDepartmentQueryDTO;
import com.coolcollege.intelligent.model.department.dto.MonitorDeptDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserRole;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.system.SysRoleQueryDTO;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserMappingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.service.lock.LockService;
import com.coolcollege.intelligent.service.sync.service.AutoSyncService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.sync.conf.SyncConfig.DEFAULT_BATCH_SIZE;

@Slf4j
public abstract class BaseUserEvent extends BaseEvent {

    private String BASE_USER_EVENT = "base_user_event";

    protected String userId;

    private String accessToken;

    // 部门id与父部门id映射
    private Map<String, String> idForPid;

//    protected String eid;

    @Override
    public void doEvent() {

    }

    private Set<Long> getDeptIdSet(String deptIdStr) {

        if (StringUtils.isNotBlank(deptIdStr)) {
            return Arrays.stream(deptIdStr.replaceAll("\\[", "").replaceAll("\\]", "").split(",")).filter(id -> StringUtils.isNotEmpty(id)).map(Long::valueOf).collect(Collectors.toSet());
        }
        return null;
    }

    private List<EnterpriseUserRequest> getUsers(List<String> userIds, String accessToken) {
        try {
            return SpringContextUtil.getBean("dingService", DingService.class).getUsers(userIds, accessToken);
        } catch (ApiException e) {
            log.error("getUsers error, corpId={}, eventType={}, userId={}", corpId, getEventType(), userId, e);
        }
        return null;
    }


//    @Transactional
    public void addUserReleatedInfo(List<EnterpriseUserRequest> users) {
        //入库的时候永远是激活状态, 是否激活以我们系统为准
        users.forEach(user -> {
            user.getEnterpriseUserDO().setActive(true);
        });

        DataSourceHelper.reset();
        String eid = getEid();

        //保存用户到平台库
        EnterpriseUserService enterpriseUserService = SpringContextUtil.getBean("enterpriseUserService", EnterpriseUserService.class);
        List<EnterpriseUserDO> collect = ListUtils.emptyIfNull(users)
                .stream()
                .map(EnterpriseUserRequest::getEnterpriseUserDO)
                .collect(Collectors.toList());
        List<EnterpriseUserDO> userList=new ArrayList<>();
        Lists.partition(collect,DEFAULT_BATCH_SIZE).forEach(data->{
            enterpriseUserService.batchInsertPlatformUsers(data);
            userList.addAll(data);

        });



        //记录企业用户关系映射
        EnterpriseUserMappingService enterpriseUserMappingService = SpringContextUtil.getBean("enterpriseUserMappingService", EnterpriseUserMappingService.class);
        List<EnterpriseUserMappingDO> mappings = enterpriseUserMappingService.buildEnterpriseUserMappings(eid, userList);
        enterpriseUserMappingService.batchInsertOrUpdate(mappings);

        //记录密码信息
        /*EnterpriseUserPasswordService enterpriseUserPasswordService = SpringContextUtil.getBean("enterpriseUserPasswordService", EnterpriseUserPasswordService.class);
        List<EnterpriseUserPassword> passwords = enterpriseUserPasswordService.buildEnterpriseUserPasswords(userList);
        enterpriseUserPasswordService.batchInsertOrUpdate(passwords);*/

        //保存用户到企业库
        DataSourceHelper.changeToSpecificDataSource(getDbName());
        SpringContextUtil.getBean("enterpriseUserService", EnterpriseUserService.class).batchInsertOrUpdate(userList, eid);

        SysRoleService sysRoleService = SpringContextUtil.getBean("sysRoleService", SysRoleService.class);

//        List<String> adminUserIds = userList.stream()
//                .filter(s -> Objects.nonNull(s.getUserId()) && s.getIsAdmin())
//                .map(EnterpriseUserDO::getUserId).collect(Collectors.toList());
        // 维护数智门店自定义角色
        Long employeeRoleId = sysRoleService.getRoleIdByRoleEnum(eid, Role.EMPLOYEE.getRoleEnum());
        if (this instanceof UserAddEvent) {
//            if (CollUtil.isNotEmpty(adminUserIds)) {
//                SysRoleQueryDTO admin= SysRoleQueryDTO.builder().roleId(Role.MASTER.getId()).build();
//                admin.setUserIds(adminUserIds);
//                sysRoleService.addPersonToUser(eid,admin, true);
//            }
            List<String> employeeUserIds = userList.stream()
                    .filter(s -> Objects.nonNull(s.getUserId()) )
                    .map(EnterpriseUserDO::getUserId).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(employeeUserIds)) {
                SysRoleQueryDTO employee = SysRoleQueryDTO.builder().roleId(employeeRoleId.toString()).build();
                employee.setUserIds(employeeUserIds);
                sysRoleService.addPersonToUser(eid,employee, true);
                DataSourceHelper.changeToSpecificDataSource(getDbName());
            }
        }
        if (this instanceof UserModifyEvent) {
            if (userList.get(0).getMainAdmin()) {
                EnterpriseUserService userService = SpringContextUtil.getBean("enterpriseUserService", EnterpriseUserService.class);
                userService.updateUserMainAdmin(eid, userId);
//                if (CollUtil.isNotEmpty(adminUserIds)) {
//                    SysRoleQueryDTO admin= SysRoleQueryDTO.builder().roleId(Role.MASTER.getId()).userIds(adminUserIds).build();
//                    sysRoleService.addPersonToUser(eid,admin, true);
//                }
            }
            DataSourceHelper.changeToSpecificDataSource(getDbName());
            sysRoleService.deleteSyncRoleRelate(eid, userList.get(0).getUserId());
        }

        List<EnterpriseUserRole> userRoles = new ArrayList<>();
        userList.forEach(f -> {
            String roles = f.getRoles();
            if (StrUtil.isBlank(roles)) {
                return;
            }
            String[] split = roles.split(",");
            for (String roleId: split) {
                userRoles.add(new EnterpriseUserRole(roleId, f.getUserId()));
            }
        });
        if (CollUtil.isNotEmpty(userRoles)) {
            sysRoleService.insertBatchUserRole(eid, userRoles);
        }
        //如果该用户没有职位则分配至未分配
        ListUtils.emptyIfNull(userList)
                .forEach(data->{
                    List<String> roleIdList = sysRoleService.getRoleIdByUserId(eid, data.getUserId());
                    if(CollectionUtils.isEmpty(roleIdList)){
                        SysRoleQueryDTO employee = SysRoleQueryDTO.builder().roleId(employeeRoleId.toString()).build();
                        employee.setUserIds(Collections.singletonList(data.getUserId()));
                        sysRoleService.addPersonToUser(eid,employee, true);
                    }
                });


        /*List<Long> joinUserIds = userList.stream().map(EnterpriseUserDO::getId).collect(Collectors.toList());
        QuitUserBaseOut quitUserBaseOut = new QuitUserBaseOut(eid, joinUserIds);
        SpringContextUtil.getBean("redisUtil", RedisUtil.class).batchListPushHead("join_user_", Arrays.asList(JSON.toJSONString(quitUserBaseOut)), Boolean.TRUE);
*/
    }

    protected void syncUser() throws InterruptedException {
        List<String> originUserIds = Arrays.stream(userId.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "").split(",")).collect(Collectors.toList());
       if(CollectionUtils.isNotEmpty(originUserIds)&&StringUtils.equalsIgnoreCase("null",originUserIds.get(0))){
           return;
       }
        doParentEvent();

        log.info("企业id为{}", eid);
        if (getEnableDingSync()) {
            RedisUtilPool redisUtil = SpringContextUtil.getBean("redisUtilPool", RedisUtilPool.class);
            log.info("添加人员自动开启同步。。。。。。。。。。");
            if (StringUtils.isNotEmpty(redisUtil.getString("syncDingStore" + eid))) {
                Long second = redisUtil.getExpire("syncDingStore" + eid);
                log.info("线程处于同步中，在等待。。。。");
                Thread.sleep(second * 1000L);
            }
            DataSourceHelper.changeToSpecificDataSource(getDbName());
            addStoreAndPosition(eid, originUserIds, redisUtil, accessToken);
        }
    }

    private void doParentEvent() {
        String eid = getEid();
        List<String> originUserIds = Arrays.stream(userId.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "").split(",")).collect(Collectors.toList());

        accessToken = getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return;
        }
        LockService lockService = SpringContextUtil.getBean("lockService", LockService.class);
        List<String> userIds = Lists.newArrayList();

        originUserIds.forEach(userId -> {
            boolean lock = lockService.lock(BASE_USER_EVENT, corpId, userId);
            if (lock) {
                userIds.add(userId);
            } else {
                log.warn("baseUserEvent can not get lock, eventType={}, corpId={}, userId={}", getEventType(), corpId, userId);
            }
        });

        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        List<EnterpriseUserRequest> users = getUsers(userIds, accessToken);

        if (CollectionUtils.isEmpty(users)) {
            return;
        }

        //EnterpriseUserDO dingUser = users.get(0);

        String dbName = getDbName();

        EnterpriseUserService enterpriseUserService = SpringContextUtil.getBean("enterpriseUserService", EnterpriseUserService.class);

        DataSourceHelper.changeToSpecificDataSource(dbName);
        //EnterpriseUserDO originUser = enterpriseUserService.selectByUserId(eid, dingUser.getUserId());

        SysDepartmentService sysDepartmentService = SpringContextUtil.getBean("sysDepartmentService", SysDepartmentService.class);

        TwoResultTuple<Set<String>, Map<String, String>> tuple = sysDepartmentService.getAllDeptInfo(eid);
        idForPid = tuple.second;
        enterpriseUserService.setUsersInfo(eid, users, tuple.first, tuple.second);
        try {
            addUserReleatedInfo(users);
        } catch (Exception e) {
            log.error("addUserReleatedInfo error, corpId={}, eventType={}, userId={}", corpId, getEventType(), userId, e);
            return;
        } finally {

            userIds.forEach(userId -> {
                lockService.unlock(BASE_USER_EVENT, corpId, userId);
            });
        }

        // 异步处理用户岗位相关数据
//        CompletableFuture.runAsync(new SyncPositionTask(dbName, eid, users));
        // TODO 异步处理企业配额的任务
//        if (getEventType().equals(USER_ADD_ORG)) {
//            CompletableFuture.runAsync(new QuotaTask(getEid(), corpId));
//        }
    }

    protected void addStoreAndPosition(String eid, List<String> userIds, RedisUtilPool redisUtil, String accessToken) {

        log.info("eid>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + eid);

        try {
//            // 此次删除的部门
//            List<String> delDeptIds = ListUtils.emptyIfNull(oldDeptIds).stream().filter(f -> !newDeptIds.contains(f)).map(String::valueOf).collect(Collectors.toList());
            String departmentDTO = redisUtil.getString("departmentDTO" + eid);
            log.info("departmentDTO" + departmentDTO);
            if (StringUtils.isEmpty(departmentDTO)) {
                return;
            }
            log.info("开始同步添加门店下信息");
            DingDepartmentQueryDTO departmentQueryDTO = JSONObject.parseObject(departmentDTO, DingDepartmentQueryDTO.class);
            List<MonitorDeptDTO> departments = departmentQueryDTO.getDepartments();
            List<String> delDeptIds = new ArrayList<>();
            for (MonitorDeptDTO dept: departments) {
                List<String> storeIds = dept.getStoreIds();
                if (CollUtil.isNotEmpty(storeIds)) {
                    delDeptIds.addAll(storeIds);
                }
            }
            AutoSyncService autoService = SpringContextUtil.getBean("autoSyncService", AutoSyncService.class);
            autoService.autoSyncUser(eid, delDeptIds, userIds, departmentQueryDTO, corpId, accessToken);


        } catch (Exception e) {
            log.error("人员新增同步部门店失败{}", userIds);
            log.error("同部门点失败信息：", e);

        }
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
