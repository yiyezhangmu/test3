package com.coolcollege.intelligent.service.sync.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.enums.position.PositionSourceEnum;
import com.coolcollege.intelligent.common.enums.store.DingSyncType;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDeptDao;
import com.coolcollege.intelligent.dao.store.StoreDeviceMappingDao;
import com.coolcollege.intelligent.model.department.dto.DingDepartmentQueryDTO;
import com.coolcollege.intelligent.model.department.dto.MonitorDeptDTO;
import com.coolcollege.intelligent.model.department.dto.MonitorDeptTypeDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.dto.ManualDeptUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.UserDeptRoleDTO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.enums.StoreIsDeleteEnum;
import com.coolcollege.intelligent.model.store.StoreDeviceMappingDO;
import com.coolcollege.intelligent.model.store.StoreSupervisorMappingDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.store.dto.StoreSupervisorMappingDTO;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2020/9/16 16:59
 */
@Service("autoSyncService")
@Slf4j
public class AutoSyncService {

    @Autowired
    private StoreDeviceMappingDao storeDeviceMappingDao;

    @Autowired
    private StoreService storeService;

    @Autowired
    private DingService dingService;

    @Autowired
    private EnterpriseUserDeptDao userDeptDao;

    public Boolean autoSyncDept(String eid, List<String> storeIds, SysDepartmentDO department,
                                DingDepartmentQueryDTO departmentQuery, String corpId,
                                boolean syncToChild, List<String> monitorDeptIds, String accessToken) {
        // 获取同步店员的信息
        MonitorDeptTypeDTO clerk = departmentQuery.getClerk();
        // 获取同步运营的信息
        MonitorDeptTypeDTO operator = departmentQuery.getOperator();
        // 获取同步店长的信息
        MonitorDeptTypeDTO shopowner = departmentQuery.getShopowner();
        //删除部门下历史绑定的人员
        List<StoreSupervisorMappingDO> newUserStoreList = new ArrayList<>();
        List<StoreSupervisorMappingDO> clerkList = getNewDeptUserMapping(eid, clerk, null, storeIds, department, syncToChild, accessToken);
        List<StoreSupervisorMappingDO> operatorList = getNewDeptUserMapping(eid, operator, null, storeIds, department, syncToChild, accessToken);
        List<StoreSupervisorMappingDO> shopownerList = getNewDeptUserMapping(eid, shopowner, null, storeIds, department, syncToChild, accessToken);
        newUserStoreList.addAll(clerkList);
        newUserStoreList.addAll(operatorList);
        newUserStoreList.addAll(shopownerList);
        List<StoreDeviceMappingDO> deviceMapping = storeDeviceMappingDao.getDeviceMapping(eid, storeIds, null);
        // 绑定了打卡组实例
        if (CollUtil.isNotEmpty(deviceMapping)) {
            storeService.addInstanceGroup(eid, storeIds, new ArrayList<>(), newUserStoreList, corpId, true);
        }
        return Boolean.TRUE;
    }

    public Boolean autoSyncUser(String eid, List<String> allDeptIds, List<String> userIds,
                                DingDepartmentQueryDTO departmentQuery, String corpId, String accessToken) {

        // 获取同步店员的信息
        MonitorDeptTypeDTO clerk = departmentQuery.getClerk();
        // 获取同步运营的信息
        MonitorDeptTypeDTO operator = departmentQuery.getOperator();
        // 获取同步店长的信息
        MonitorDeptTypeDTO shopowner = departmentQuery.getShopowner();

        UserDeptRoleDTO user = userDeptDao.getUserDeptRole(eid, userIds.get(0));

        List<MonitorDeptDTO> departments = departmentQuery.getDepartments();
        if (CollUtil.isEmpty(departments)) {
            return false;
        }
        List<StoreSupervisorMappingDO> newUserStoreList = new ArrayList<>();
        List<StoreSupervisorMappingDO> clerkList = getNewUserMapping(eid, clerk, user, null, departments, allDeptIds, accessToken);
        List<StoreSupervisorMappingDO> operatorList = getNewUserMapping(eid, operator,user, null, departments, allDeptIds, accessToken);
        List<StoreSupervisorMappingDO> shopownerList = getNewUserMapping(eid, shopowner, user, null, departments, allDeptIds, accessToken);
        newUserStoreList.addAll(clerkList);
        newUserStoreList.addAll(operatorList);
        newUserStoreList.addAll(shopownerList);
        List<StoreSupervisorMappingDTO> allOldStoreSupervisorMappings = new ArrayList<>();
        // 绑定了打卡组实例
        if (CollUtil.isNotEmpty(allDeptIds)) {
            // 绑定打卡组  并且同步人员映射信息
            storeService.addInstanceGroup(eid, allDeptIds, allOldStoreSupervisorMappings, newUserStoreList, corpId, false);
        }

        return Boolean.TRUE;
    }


    public List<StoreSupervisorMappingDO> getNewDeptUserMapping(String eid, MonitorDeptTypeDTO positionInfo, String positionId, List<String> storeIds, SysDepartmentDO department, boolean syncToChild, String accessToken) {
        String type = positionInfo.getType();
        if (StrUtil.isBlank(type)) {
            return new ArrayList<>();
        }

        List<ManualDeptUserDTO> depUsers = new ArrayList<>();
        /*
         * 如果与管理员有关
         * 1.当前节点管理员则查出当前节点下的部门与人员映射
         * 2.如果是父节点，则查出父节点下的管理员
         */
        if (Objects.equals(type, DingSyncType.ADMINUSER.getValue()) ||
                Objects.equals(type, DingSyncType.PARENTADMIN.getValue())) {
            if (syncToChild) {
                return new ArrayList<>();
            }
            List<String> adminList;
            try {
                adminList = dingService.getAdminList(accessToken);
            } catch (ApiException e) {
                log.error("获取管理员列表失败，", e);
                throw new ServiceException("获取管理员列表失败");
            }
            List<String> deptIds = new ArrayList<>();
            if (type.equals(DingSyncType.ADMINUSER.getValue())) {
                deptIds.add(department.getId());
            } else {
                deptIds.add(department.getParentId());
            }
            depUsers = userDeptDao.getManualDeptUserList(eid, adminList, deptIds);
        }

        /*
         * 如果是所有的用户
         * 1. 父节点所有用户不用递归查
         * 2. 此节点所有用户则需要查出此节点所有的用户（递归获取）
         */
        if (Objects.equals(type, DingSyncType.ALLUSER.getValue()) ||
                Objects.equals(type, DingSyncType.PARENTUSER.getValue())) {
            List<String> childDeptIdList;
            if (Objects.equals(type, DingSyncType.ALLUSER.getValue())) {
                // 如果该部门同步到监控的子节点下，则需把该部门下的人员同步到此子节点下
                childDeptIdList = Collections.singletonList(department.getId());
            } else {
                if (syncToChild) {
                    // 设置一个不存在的值
                    childDeptIdList = Collections.singletonList("-1");
                } else {
                    childDeptIdList = Collections.singletonList(department.getParentId());
                }
            }
            depUsers = userDeptDao.getManualDeptUserList(eid, null, childDeptIdList);
        }

        /**
         * 如果是主管权限
         */
        if (Objects.equals(type, DingSyncType.CHARGE.getValue()) ||
                Objects.equals(type, DingSyncType.PARENT_CHARGE.getValue())) {
            if (syncToChild) {
                return new ArrayList<>();
            }
            String finalDeptId = Objects.equals(type, DingSyncType.CHARGE.getValue()) ? department.getId(): department.getParentId();
            depUsers = userDeptDao.getManualDeptUserList(eid, null, Collections.singletonList(finalDeptId));
            String leaderFilter = finalDeptId + ":true";
            // 过滤该部门下的主管人员
            depUsers = depUsers.stream().filter(m -> StrUtil.isNotBlank(m.getIsLeaderInDept())
                    && m.getIsLeaderInDept().contains(leaderFilter)).collect(Collectors.toList());
        }

        /**
         * 如果是指定的角色
         */
        if (Objects.equals(type, DingSyncType.ROLE.getValue()) ||
                Objects.equals(type, DingSyncType.PARENT_ROLE.getValue())) {
            if (syncToChild) {
                return new ArrayList<>();
            }
            String finalDeptId = Objects.equals(type, DingSyncType.ROLE.getValue()) ? department.getId(): department.getParentId();
            depUsers = userDeptDao.getManualDeptUserList(eid, null, Collections.singletonList(finalDeptId));
            // 筛选拥有指定角色的人员
            List<Long> ids = positionInfo.getIds();
            depUsers = depUsers.stream().filter(f -> !Collections.disjoint(ids, f.getRoleIds())).collect(Collectors.toList());
        }
        depUsers = depUsers.stream().filter(f -> !f.getUserId().equals(AIEnum.AI_USERID.getCode())).collect(Collectors.toList());
        return packageMapping(storeIds, depUsers, positionId);
    }

    public List<StoreSupervisorMappingDO> getNewUserMapping(String eid, MonitorDeptTypeDTO positionInfo, UserDeptRoleDTO user,
                                                            String positionId, List<MonitorDeptDTO> departments,
                                                            List<String> allDeptIds, String accessToken) {
        String type = positionInfo.getType();
        if (StrUtil.isBlank(type)) {
            return new ArrayList<>();
        }
        List<StoreDTO> allStoreList = new ArrayList<>();
        /*
         * 判断是否是管理员  如果是管理员则直接添加关联关系
         */
        if (Objects.equals(type, DingSyncType.ADMINUSER.getValue()) ||
                Objects.equals(type, DingSyncType.PARENTADMIN.getValue())) {
            List<EnterpriseUserRequest> dingUsers;
            try {
                dingUsers = dingService.getUsers(Collections.singletonList(user.getUserId()), accessToken);
            } catch (ApiException e) {
                log.error("获取人员信息失败，", e);
                throw new ServiceException("获取人员失败");
            }
            if (CollUtil.isEmpty(dingUsers) || (CollUtil.isNotEmpty(dingUsers) && !dingUsers.get(0).getEnterpriseUserDO().getIsAdmin())) {
                return new ArrayList<>();
            }
            List<String> finalDeptId = getFinalDeptId(user, type, allDeptIds, departments);
            // 如果是管理员则按照配置直接加入到节点下
            allStoreList = storeService.getAllStoreList(eid, finalDeptId, StoreIsDeleteEnum.EFFECTIVE.getValue());
        }

        /*
         * 如果是主管权限
         */
        if (Objects.equals(type, DingSyncType.CHARGE.getValue()) ||
                Objects.equals(type, DingSyncType.PARENT_CHARGE.getValue())) {

            String isLeaderInDept = user.getIsLeaderInDept();
            if (StrUtil.isBlank(isLeaderInDept)) {
                return new ArrayList<>();
            }
            List<String> finalDeptId = new ArrayList<>();
            if (Objects.equals(type, DingSyncType.CHARGE.getValue())) {
                finalDeptId = getFinalDeptId(user, type, allDeptIds, departments);
                // 过滤当前人员担任部门主管的部门
                finalDeptId = finalDeptId.stream().filter(f -> isLeaderInDept.contains(f + ":true")).collect(Collectors.toList());
            } else {
                List<MonitorDeptDTO> parentNode = departments.stream()
                        .filter(f -> isLeaderInDept.contains(f.getDepartmentId() + ":true"))
                        .collect(Collectors.toList());
                for (MonitorDeptDTO node : parentNode) {
                    finalDeptId.addAll(node.getStoreIds());
                }
            }

            allStoreList = storeService.getAllStoreList(eid, finalDeptId, StoreIsDeleteEnum.EFFECTIVE.getValue());
        }

        /*
         * 如果是指定角色
         */
        if (Objects.equals(type, DingSyncType.ROLE.getValue()) ||
                Objects.equals(type, DingSyncType.PARENT_ROLE.getValue())) {
            List<String> finalDeptId = getFinalDeptId(user, type, allDeptIds, departments);
            List<String> roleIds = user.getRoleIds().stream().map(String::valueOf).collect(Collectors.toList());
            // 获取特定岗位下的岗位id列表
            List<String> ids = positionInfo.getIds().stream().map(String::valueOf).collect(Collectors.toList());
            // 判断人员是否具有指定角色
            if (Collections.disjoint(ids, roleIds)) {
                return new ArrayList<>();
            }
            allStoreList = storeService.getAllStoreList(eid, finalDeptId, StoreIsDeleteEnum.EFFECTIVE.getValue());
        }

        /* 如果是所有用户 */
        if (Objects.equals(type, DingSyncType.ALLUSER.getValue()) ||
                Objects.equals(type, DingSyncType.PARENTUSER.getValue())) {
            List<String> finalDeptId = getFinalDeptId(user, type, allDeptIds, departments);
            allStoreList = storeService.getAllStoreList(eid, finalDeptId, StoreIsDeleteEnum.EFFECTIVE.getValue());
        }
//        allStoreList = allStoreList.stream().filter(f -> !f.getUserId().equals(AIEnum.AI_USERID.getCode())).collect(Collectors.toList());
        return packageMapping(user, allStoreList, positionId);
    }

    private List<String> getFinalDeptId(UserDeptRoleDTO user, String type, List<String> allDeptIds, List<MonitorDeptDTO> departments) {
        List<String> result = new ArrayList<>();
        List<String> deptIds = user.getDeptIds();
        // 如果是同步到父部门下，则获取人员在监控父部门下的子节点
        if (type.contains("parent")) {
            List<MonitorDeptDTO> parentNode = departments.stream().filter(f -> deptIds.contains(f.getDepartmentId().toString())).collect(Collectors.toList());
            for (MonitorDeptDTO node : parentNode) {
                result.addAll(node.getStoreIds());
            }
        } else {
            // 如果是节点信息为节点全部人员
            result = deptIds.stream().filter(allDeptIds::contains).collect(Collectors.toList());
        }
        return result;
    }

    /**
     * 递归获取需要同步的部门id
     * @param monitorDeptIds
     * @param deptIds
     * @param deptId
     */
    private void recursiveGetDeptId(List<String> monitorDeptIds, List<String> deptIds, String deptId, Map<Long, Long> idForPid) {
        if (deptId == null) {
            return;
        }
        if (monitorDeptIds.contains(deptId)) {
            return;
        }
        if (deptIds.contains(deptId)) {
            monitorDeptIds.add(deptId);
        }
        String parentId = MapUtil.getStr(idForPid, Long.parseLong(deptId));
        recursiveGetDeptId(monitorDeptIds, deptIds, parentId, idForPid);
    }

    /**
     * 组装用户与门店映射列表
     *
     * @param storeIds
     * @param depUsers
     * @return
     */
    public List<StoreSupervisorMappingDO> packageMapping(List<String> storeIds, List<ManualDeptUserDTO> depUsers, String positionId) {
        List<StoreSupervisorMappingDO> newMapping = new ArrayList<>();
        for (String storeId: storeIds) {
            for (ManualDeptUserDTO user: depUsers) {
                StoreSupervisorMappingDO storeSupervisorMappingDO = new StoreSupervisorMappingDO();
                storeSupervisorMappingDO.setStoreId(storeId);
                storeSupervisorMappingDO.setCreateTime(System.currentTimeMillis());
                storeSupervisorMappingDO.setUserId(user.getUserId());
                storeSupervisorMappingDO.setUserName(user.getUserName());
                storeSupervisorMappingDO.setSource(PositionSourceEnum.SYNC.getValue());
                storeSupervisorMappingDO.setIsValid(true);
                storeSupervisorMappingDO.setType(positionId);
                newMapping.add(storeSupervisorMappingDO);
            }
        }
        return newMapping;
    }

    public  List<StoreSupervisorMappingDO> packageMapping(UserDeptRoleDTO user, List<StoreDTO> stores, String positionId) {
        return stores.stream().map(m -> {
            StoreSupervisorMappingDO storeSupervisorMappingDO = new StoreSupervisorMappingDO();
            storeSupervisorMappingDO.setStoreId(m.getStoreId());
            storeSupervisorMappingDO.setCreateTime(System.currentTimeMillis());
            storeSupervisorMappingDO.setUserId(user.getUserId());
            storeSupervisorMappingDO.setUserName(user.getName());
            storeSupervisorMappingDO.setSource(PositionSourceEnum.SYNC.getValue());
            storeSupervisorMappingDO.setIsValid(true);
            storeSupervisorMappingDO.setType(positionId);
            return storeSupervisorMappingDO;
        }).collect(Collectors.toList());
    }
}
