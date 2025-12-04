package com.coolcollege.intelligent.service.sync.service;

import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.common.enums.position.PositionSourceEnum;
import com.coolcollege.intelligent.common.enums.store.DingSyncType;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDeptDao;
import com.coolcollege.intelligent.model.department.dto.DingDepartmentQueryDTO;
import com.coolcollege.intelligent.model.department.dto.MonitorDeptTypeDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.dto.ManualDeptUserDTO;
import com.coolcollege.intelligent.model.enums.PositionEnum;
import com.coolcollege.intelligent.model.store.StoreSupervisorMappingDO;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 邵凌志
 * @date 2020/10/20 11:47
 */
@Slf4j
@Service
public class ManualSyncService {

    @Autowired
    private EnterpriseUserDeptDao userDeptDao;

    @Lazy
    @Autowired
    private SysDepartmentService deptService;

    @Autowired
    private DingService dingService;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

//    /**
//     * 获取人员-门店同步关联关系映射
//     *
//     * @param eid               企业id
//     * @param sysDepartments    部门列表
//     * @param dingAndStoreId    部门id与门店映射
//     * @param typeInfo          监控的节点信息
//     * @param positionId        岗位id
//     * @param accessToken       钉钉授权token
//     * @return
//     */
//    public List<StoreSupervisorMappingDO> getSupervisorMapping(String eid, List<SysDepartmentDO> sysDepartments,
//                                                               Map<String, String> dingAndStoreId, MonitorDeptTypeDTO typeInfo,
//                                                               String positionId, String accessToken) {
//        String type = typeInfo.getType();
//        if (StrUtil.isBlank(type)) {
//            return new ArrayList<>();
//        }
//        // 如果是同步管理员
//        if (Objects.equals(type, DingSyncType.ADMINUSER.getValue()) ||
//                Objects.equals(type, DingSyncType.PARENTADMIN.getValue())) {
//            // 获取企业下所有的管理员
//            List<String> adminList = deptService.getAdminList(accessToken);
//            List<Long> deptIds = getDeptIds(sysDepartments);
//            List<ManualDeptUserDTO> deptUsers = userDeptDao.getManualDeptUserList(eid, adminList, deptIds);
//            if (Objects.equals(type, DingSyncType.ADMINUSER.getValue())) {
//                return getCurrentMapping(sysDepartments, dingAndStoreId, positionId, deptUsers);
//            } else {
//                return getParenMapping(sysDepartments, dingAndStoreId, positionId, deptUsers);
//            }
//        }
//        // 如果是所有人员
//        if (Objects.equals(type, DingSyncType.ALLUSER.getValue()) ||
//                Objects.equals(type, DingSyncType.PARENTUSER.getValue())) {
//            // 获取全部的部门与用户的映射
//            List<ManualDeptUserDTO> deptUsers = userDeptDao.getManualDeptUserList(eid, null, null);
//            if (Objects.equals(type, DingSyncType.ALLUSER.getValue())) {
//                return getCurrentMapping(sysDepartments, dingAndStoreId, positionId, deptUsers);
//            } else {
//                return getParenMapping(sysDepartments, dingAndStoreId, positionId, deptUsers);
//            }
//        }
//        // 如果是主管权限
//        if (Objects.equals(type, DingSyncType.CHARGE.getValue()) ||
//                Objects.equals(type, DingSyncType.PARENT_CHARGE.getValue())) {
//            List<Long> deptIds = getDeptIds(sysDepartments);
//            // 获取全部的部门与用户的映射
//            List<ManualDeptUserDTO> deptUsers = userDeptDao.getManualDeptUserList(eid, null, deptIds);
//            if (Objects.equals(type, DingSyncType.CHARGE.getValue())) {
//                return getChargeMapping(sysDepartments, dingAndStoreId, positionId, deptUsers, false);
//            } else {
//                return getChargeMapping(sysDepartments, dingAndStoreId, positionId, deptUsers, true);
//            }
//        }
//        /**
//         * 如果是指定角色
//         */
//        if (Objects.equals(type, DingSyncType.ROLE.getValue()) ||
//                Objects.equals(type, DingSyncType.PARENT_ROLE.getValue())) {
//            List<Long> deptIds = getDeptIds(sysDepartments);
//            // 获取全部的部门与用户的映射
//            List<ManualDeptUserDTO> deptUsers = userDeptDao.getManualDeptUserList(eid, null, deptIds);
//            if (Objects.equals(type, DingSyncType.ROLE.getValue())) {
//                return getRoleMapping(sysDepartments, dingAndStoreId, positionId, deptUsers, typeInfo.getIds(), false);
//            } else {
//                return getRoleMapping(sysDepartments, dingAndStoreId, positionId, deptUsers, typeInfo.getIds(), true);
//            }
//        }
//        return new ArrayList<>();
//    }

    private List<String> getDeptIds(List<SysDepartmentDO> deptList) {
        List<String> ids = new ArrayList<>();
        for (SysDepartmentDO dept: deptList) {
            ids.add(dept.getId());
            ids.add(dept.getParentId());
        }
        return ids;
    }

//    public void recoveryStoreSupervisor(String eid, Map<String, String> positionIdMap, String corpId,
//                                        List<SysDepartmentDO> sysDepartments, Map<String, String> storeForDingId,
//                                        DingDepartmentQueryDTO departmentQuery, String dbName) {
//        String accessToken;
//        try {
//            DataSourceHelper.reset();
//            EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(eid);
//            accessToken = dingService.getAccessToken(config.getDingCorpId(), config.getAppType());
//        } catch (ApiException e) {
//            log.error("获取钉钉token失败：", e);
//            return;
//        }
//        List<StoreSupervisorMappingDO> all = new ArrayList<>();
//        DataSourceHelper.changeToSpecificDataSource(dbName);
//        MonitorDeptTypeDTO clerk = departmentQuery.getClerk();
//        if (clerk != null && StrUtil.isNotBlank(clerk.getType())) {
//            String positionId = positionIdMap.get(PositionEnum.CLERK.getCode());
//            all.addAll(getSupervisorMapping(eid, sysDepartments, storeForDingId, clerk, positionId, accessToken));
//        }
//        MonitorDeptTypeDTO operator = departmentQuery.getOperator();
//        if (operator != null && StrUtil.isNotBlank(operator.getType())) {
//            String positionId = positionIdMap.get(PositionEnum.OPERATOR.getCode());
//            all.addAll(getSupervisorMapping(eid, sysDepartments, storeForDingId, operator, positionId, accessToken));
//        }
//        MonitorDeptTypeDTO shopowner = departmentQuery.getShopowner();
//        if (shopowner != null && StrUtil.isNotBlank(shopowner.getType())) {
//            String positionId = positionIdMap.get(PositionEnum.SHOPOWNER.getCode());
//            all.addAll(getSupervisorMapping(eid, sysDepartments, storeForDingId, shopowner, positionId, accessToken));
//        }
//    }

    private List<StoreSupervisorMappingDO> getParenMapping(List<SysDepartmentDO> sysDepartments, Map<String, String> dingAndStoreId,
                                                           String positionId, List<ManualDeptUserDTO> deptUsers) {
        List<StoreSupervisorMappingDO> result = new ArrayList<>();
        sysDepartments.forEach(s -> {
            String storeId = dingAndStoreId.get(s.getId().toString());
            if (StrUtil.isNotBlank(storeId)) {
                // 获取当前部门的父部门人员
                deptUsers.stream().filter(f -> f.getDeptId().equals(s.getParentId()))
                        // 装配门店-人员关联关系
                        .forEach(m -> result.add(new StoreSupervisorMappingDO(storeId, m.getUserId(), m.getUserName(), positionId, System.currentTimeMillis(), PositionSourceEnum.SYNC.getValue(), true)));
            }
        });
        return result;
    }

    /**
     * 获取当前部门下的人员列表
     *
     * @param sysDepartments
     * @param dingAndStoreId
     * @param positionId
     * @param deptUsers
     * @return
     */
    private List<StoreSupervisorMappingDO> getCurrentMapping(List<SysDepartmentDO> sysDepartments, Map<String, String> dingAndStoreId,
                                                           String positionId, List<ManualDeptUserDTO> deptUsers) {
        List<StoreSupervisorMappingDO> result = new ArrayList<>();
        sysDepartments.forEach(s -> {
            String storeId = dingAndStoreId.get(s.getId().toString());
            if (StrUtil.isNotBlank(storeId)) {
                deptUsers.stream().filter(f -> s.getId().equals(f.getDeptId()))
                        // 装配门店-人员关联关系
                        .forEach(m -> result.add(new StoreSupervisorMappingDO(storeId, m.getUserId(), m.getUserName(), positionId, System.currentTimeMillis(), PositionSourceEnum.SYNC.getValue(), true)));
            }
        });
        return result;
    }

    /**
     * 获取部门下拥有主管角色的用户映射
     * @param sysDepartments
     * @param dingAndStoreId
     * @param positionId
     * @param deptUsers
     * @param isParent
     * @return
     */
    private List<StoreSupervisorMappingDO> getChargeMapping(List<SysDepartmentDO> sysDepartments, Map<String, String> dingAndStoreId,
                                                            String positionId, List<ManualDeptUserDTO> deptUsers, boolean isParent) {
        List<StoreSupervisorMappingDO> result = new ArrayList<>();
        sysDepartments.forEach(f -> {
            String deptId = isParent ? f.getParentId(): f.getId();
            String storeId = dingAndStoreId.get(f.getId().toString());
            if (StrUtil.isNotBlank(storeId)) {
                String deptCharge = deptId + ":true";
                deptUsers.stream().filter(d -> StrUtil.isNotBlank(d.getIsLeaderInDept()) && d.getDeptId().equals(deptId)
                        && d.getIsLeaderInDept().contains(deptCharge))
                        .forEach(m -> result.add(new StoreSupervisorMappingDO(storeId, m.getUserId(), m.getUserName(), positionId, System.currentTimeMillis(), PositionSourceEnum.SYNC.getValue(), true)));
            }
        });
        return result;
    }

    private List<StoreSupervisorMappingDO> getRoleMapping(List<SysDepartmentDO> sysDepartments, Map<String, String> dingAndStoreId,
                                                          String positionId, List<ManualDeptUserDTO> deptUsers, List<Long> roleIds, boolean isParent) {
        List<StoreSupervisorMappingDO> result = new ArrayList<>();
        sysDepartments.forEach(f -> {
            String deptId = isParent ? f.getParentId(): f.getId();
            String storeId = dingAndStoreId.get(f.getId().toString());
            if (StrUtil.isNotBlank(storeId)) {
                deptUsers.stream().filter(d -> !Collections.disjoint(d.getRoleIds(), roleIds) && d.getDeptId().equals(deptId))
                        .forEach(m -> result.add(new StoreSupervisorMappingDO(storeId, m.getUserId(), m.getUserName(), positionId, System.currentTimeMillis(), PositionSourceEnum.SYNC.getValue(), true)));
            }
        });
        return result;
    }
}
