package com.coolcollege.intelligent.facade.enterprise.init;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.songxia.SongXiaEnterpriseEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.ListOptUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dto.AuthInfoDTO;
import com.coolcollege.intelligent.dto.AuthScopeDTO;
import com.coolcollege.intelligent.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.dto.SysDepartmentDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserRole;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 企微的企业开通的初始化同步逻辑
 * @author xuanfeng
 * @FileName: DingConfig
 * @Description:
 */
@Data
@Slf4j
@Component
public class QwEnterpriseInitService extends EnterpriseInitBaseService {

    @Autowired
    private ChatService chatService;

    @Autowired
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Override
    public void enterpriseInit(String corpId, String eid, String appType, String dbName, String openUserId) {
        try {
            //优先处理ai用户 保证能够超登
            List<EnterpriseUserRequest> authUsers = new ArrayList<>();
            //添加ai用户
            //authUsers.add(getAIUser());
            //是否自建企业
            boolean flag = chatService.checkWxCorpIdFromRedis(corpId);
            List<String> adminList = new ArrayList<>();
            //兼容自建企业微信应用，自建应用没有管理员查询接口
            if (!flag) {
                //获取管理员
                adminList = enterpriseInitConfigApiService.getAdminUserList(corpId, appType);
            }
            DataSourceHelper.changeToSpecificDataSource(dbName);
            //设置管理员
            chatService.setAdminRoles(corpId, authUsers, adminList, eid, dbName);
            //获取开通授权信息
            AuthInfoDTO authInfo = enterpriseInitConfigApiService.getAuthInfo(corpId, appType);
            //记录此次处理的用户的id，
            Set<String> handlerUserIds = new HashSet<>();
            //处理ai用户
            dealUsers(authUsers, eid, corpId, dbName, new HashMap<>(), null, authInfo, handlerUserIds, false);
            //初始化部门
            List<SysDepartmentDTO> sysDepartmentDTOS = initDept(corpId, eid, appType, dbName);
            //初始化根区域
            initRootRegion(sysDepartmentDTOS, eid, dbName);
            //初始化用户
            initUser(sysDepartmentDTOS,corpId,eid,appType,dbName, adminList, authInfo, handlerUserIds, false);
        } catch (ApiException e) {
            log.error("enterpriseInit enterpriseInit error,corpId:{},appType:{}", corpId, appType, e);
            throw new ServiceException(ErrorCodeEnum.QW_SERVICE_EXCEPTION);
        }
    }

    @Override
    public void enterpriseInitDepartment(String corpId, String eid, String appType, String dbName) {
        //初始化部门
        List<SysDepartmentDTO> sysDepartmentDTOS = initDept(corpId, eid, appType, dbName);
        //初始化根区域
        initRootRegion(sysDepartmentDTOS, eid, dbName);
    }

    @Override
    public void enterpriseInitUser(String corpId, String eid, String appType, String dbName, Boolean isScopeChange) {
        try {
            DataSourceHelper.changeToSpecificDataSource(dbName);

            //是否自建企业
            boolean flag = chatService.checkWxCorpIdFromRedis(corpId);
            List<String> adminList = new ArrayList<>();
            //兼容自建企业微信应用，自建应用没有管理员查询接口
            if (!flag) {
                //获取管理员
                adminList = enterpriseInitConfigApiService.getAdminUserList(corpId, appType);
            }
            List<SysDepartmentDTO> sysDepartmentDTOS = initDept(corpId, eid, appType, dbName);
            //记录此次处理的用户的id， 用于做用户无用用户的删除
            Set<String> handlerUserIds = new HashSet<>();
            //获取开通授权信息
            AuthInfoDTO authInfo = enterpriseInitConfigApiService.getAuthInfo(corpId, appType);
            initUser(sysDepartmentDTOS, corpId, eid, appType, dbName, adminList, authInfo, handlerUserIds, isScopeChange);
            //无用用户的删除
            //松下不做处理
            if(SongXiaEnterpriseEnum.songXiaCompany(eid)){
                return;
            }else{
                deleteUser(eid, handlerUserIds);
            }
        } catch (ApiException e) {
            log.error("enterpriseInit enterpriseInitUser error,corpId:{},appType:{}", corpId, appType, e);
        }
    }

    @Override
    public void onlySyncUser(String corpId, String eid, String appType, String dbName) {
        try {
            DataSourceHelper.changeToSpecificDataSource(dbName);
            //是否自建企业
            boolean flag = chatService.checkWxCorpIdFromRedis(corpId);
            List<String> adminList = new ArrayList<>();
            //兼容自建企业微信应用，自建应用没有管理员查询接口
            if (!flag) {
                //获取管理员
                adminList = enterpriseInitConfigApiService.getAdminUserList(corpId, appType);
            }
            List<SysDepartmentDTO> sysDepartmentDTOS = getAllDeptByQw(corpId, appType);
            //记录此次处理的用户的id， 用于做用户无用用户的删除
            Set<String> handlerUserIds = new HashSet<>();
            //获取开通授权信息
            AuthInfoDTO authInfo = enterpriseInitConfigApiService.getAuthInfo(corpId, appType);
            initUser(sysDepartmentDTOS, corpId, eid, appType, dbName, adminList, authInfo, handlerUserIds, false);
            //无用用户的删除
            deleteUser(eid, handlerUserIds);
        } catch (ApiException e) {
            log.error("【DingEnterpriseInitService onlySyncUser call rpc has exception】", e);
        }
    }

    public List<SysDepartmentDTO> getAllDeptByQw(String corpId, String appType) {
        List<SysDepartmentDTO> departments = new ArrayList<>();
        try {
            //获取所有部门
            departments = enterpriseInitConfigApiService.getDepartments(corpId, appType, null);
            if (CollectionUtils.isEmpty(departments)) {
                log.info("enterpriseInit getAllDeptByQw getDepartments is empty,corpId:{},appType:{}", corpId, appType);
                return departments;
            }
        } catch (Exception e) {
            log.error("enterpriseInit getAllDeptByQw error,corpId:{},appType:{}", corpId, appType, e);
            throw new ServiceException(ErrorCodeEnum.QW_SERVICE_EXCEPTION);
        }
        return departments;
    }


    public List<SysDepartmentDTO> initDept(String corpId, String eid, String appType, String dbName) {
        List<SysDepartmentDTO> departments = null;
        try {
            //获取所有部门
            departments = enterpriseInitConfigApiService.getDepartments(corpId, appType, null);
            if (CollectionUtils.isEmpty(departments)) {
                log.info("enterpriseInit initDept getDepartments is empty,corpId:{},appType:{}", corpId, appType);
                return departments;
            }
            DataSourceHelper.changeToSpecificDataSource(dbName);
            List<SysDepartmentDO> sysDepartmentDOS = convertFactory.convertDeptList(departments, appType, null);
            if (CollectionUtils.isNotEmpty(sysDepartmentDOS)) {
                //插入数据库，100条分区插入
                Lists.partition(sysDepartmentDOS, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
                    sysDepartmentMapper.batchInsertOrUpdate(p, eid);
                });
            }
        } catch (Exception e) {
            log.error("enterpriseInit initDept error,corpId:{},appType:{}", corpId, appType, e);
            throw new ServiceException(ErrorCodeEnum.QW_SERVICE_EXCEPTION);
        }
        return departments;
    }

    public void initRootRegion(List<SysDepartmentDTO> sysDepartmentDTOS, String eid, String dbName) {

        try {
            Optional<SysDepartmentDTO> first = sysDepartmentDTOS.stream()
                    .filter(s -> Objects.equals(SyncConfig.ROOT_DEPT_ID, s.getId())).findFirst();

            if (first.isPresent()) {
                DataSourceHelper.changeToSpecificDataSource(dbName);
                SysDepartmentDTO sysDepartmentDTO = first.get();
                RegionDO regionDO = new RegionDO();
                regionDO.setId(1L);
                regionDO.setRegionType(RegionTypeEnum.ROOT.getType());
                regionDO.setName(sysDepartmentDTO.getName());
                regionDO.setCreateName(Constants.SYSTEM);
                regionDO.setCreateTime(Calendar.getInstance().getTimeInMillis());
                regionDO.setSynDingDeptId(SyncConfig.ROOT_DEPT_ID_STR);
                regionDO.setUnclassifiedFlag(SyncConfig.ZERO);
                regionDO.setRegionPath(null);
                regionDO.setStoreNum(SyncConfig.ONE);
                regionService.insertRoot(eid, regionDO);
                //同步部门为区域节点
                initRegionByDepartment(eid, SyncConfig.ROOT_DEPT_ID_STR);
            }
        } catch (Exception e) {
            log.error("enterpriseInit initRootRegion error,eid:{}", eid, e);
            throw new ServiceException(ErrorCodeEnum.QW_SERVICE_EXCEPTION);
        }
    }

    /**
     *
     * @param sysDepartmentDTOS
     * @param corpId
     * @param eid
     * @param appType
     * @param dbName
     * @param adminList
     * @param authInfo 开通授权信息
     * @param handlerUserIds 记录同步人员的id
     * @param isScopeChange 是否为授权范围变更
     */
    public void initUser(List<SysDepartmentDTO> sysDepartmentDTOS, String corpId, String eid, String appType, String dbName,
                         List<String> adminList, AuthInfoDTO authInfo, Set<String> handlerUserIds, Boolean isScopeChange) {
        try {
            //先查询是否存在未分组区域
            RegionDO unclassifiedRegionDO = regionService.getUnclassifiedRegionDO(eid);
            //获取通讯录授权范围
            AuthScopeDTO authScope = enterpriseInitConfigApiService.getAuthScope(corpId, appType);
            List<EnterpriseUserDTO> enterpriseUserDTOS = null;
            if (CollectionUtils.isNotEmpty(authScope.getUserIdList())) {
                //通过授权范围获取授权用户
                enterpriseUserDTOS = enterpriseInitConfigApiService.getUserDetailByUserIds(corpId, authScope.getUserIdList(), appType);
            }
            List<String> deptIdLists = ListUtils.emptyIfNull(sysDepartmentDTOS).stream().map(SysDepartmentDTO::getId).collect(Collectors.toList());
            //判断授权范围内是否有根节点，没有则移除
            if (CollectionUtils.isNotEmpty(authScope.getDeptIdList()) && !authScope.getDeptIdList().contains(SyncConfig.ROOT_DEPT_ID)) {
                deptIdLists.remove(SyncConfig.ROOT_DEPT_ID);
                sysDepartmentDTOS = ListUtils.emptyIfNull(sysDepartmentDTOS)
                        .stream()
                        .filter(a -> !a.getId().equals(SyncConfig.ROOT_DEPT_ID))
                        .collect(Collectors.toList());
            }
            Map<String, String> deptIdMap = ListUtils.emptyIfNull(sysDepartmentDTOS).stream()
                    .filter(d -> d.getParentId() != null)
                    .collect(Collectors.toMap(SysDepartmentDTO::getId, SysDepartmentDTO::getParentId));
            List<EnterpriseUserRequest> authUsers = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(enterpriseUserDTOS)) {
                EnterpriseUserRequest tempRequest;
                EnterpriseUserDO tempDo;
                for (EnterpriseUserDTO enterpriseUserDTO : enterpriseUserDTOS) {
                    tempDo = convertFactory.convertEnterpriseUserDTO2EnterpriseUserDO(enterpriseUserDTO);
                    tempRequest = new EnterpriseUserRequest();
                    tempRequest.setEnterpriseUserDO(tempDo);
                    tempRequest.setDepartmentLists(ListOptUtils.getIntersection(enterpriseUserDTO.getDepartmentLists(), deptIdLists));
                    tempRequest.setLeaderInDepts(ListOptUtils.getIntersection(enterpriseUserDTO.getIsLeaderInDepts(), deptIdLists));
                    authUsers.add(tempRequest);
                }
            }
            //设置管理员
            chatService.setAdminRoles(corpId, authUsers, adminList, eid, dbName);
            dealUsers(authUsers,eid,corpId,dbName, deptIdMap, unclassifiedRegionDO.getId(), authInfo, handlerUserIds, isScopeChange);
            List<EnterpriseUserRequest> deptUsers;
            if (CollectionUtils.isNotEmpty(sysDepartmentDTOS)) {

                for (SysDepartmentDTO sysDepartmentDTO : sysDepartmentDTOS) {
                    //通过授权部门获取授权用户
                    List<EnterpriseUserDTO> departmentUsersQw = enterpriseInitConfigApiService.getDepartmentUsers(corpId, sysDepartmentDTO.getId(), appType);
                    deptUsers = ListUtils.emptyIfNull(departmentUsersQw).stream().map(e -> {
                        EnterpriseUserRequest enterpriseUserRequest = new EnterpriseUserRequest();
                        enterpriseUserRequest.setEnterpriseUserDO(convertFactory.convertEnterpriseUserDTO2EnterpriseUserDO(e));
                        enterpriseUserRequest.setDepartmentLists(ListOptUtils.getIntersection(e.getDepartmentLists(), deptIdLists));
                        enterpriseUserRequest.setLeaderInDepts(ListOptUtils.getIntersection(e.getIsLeaderInDepts(), deptIdLists));
                        return enterpriseUserRequest;
                    }).collect(Collectors.toList());

                    if (CollectionUtils.isNotEmpty(deptUsers)) {
                        log.info("enterpriseInit initUser deptUsersInfo, corpId={}, deptId={}, userSize={}", corpId, sysDepartmentDTO.getId(), deptUsers.size());
                        //设置管理员
                        chatService.setAdminRoles(corpId, deptUsers, adminList, eid, dbName);
                        dealUsers(deptUsers, eid, corpId, dbName, deptIdMap, unclassifiedRegionDO.getId(), authInfo, handlerUserIds, isScopeChange);
                    }
                }
            }
        } catch (Exception e) {
            log.error("enterpriseInit initUser error,eid:{},appType:{}", eid, appType, e);
            throw new ServiceException(ErrorCodeEnum.QW_SERVICE_EXCEPTION);
        }
    }

    public void dealUsers(List<EnterpriseUserRequest> users, String eid, String corpId, String dbName,
                          Map<String, String> deptIdMap, Long unclassifiedRegionId, AuthInfoDTO authInfo, Set<String> handlerUserIds, Boolean isScopeChange) {

        DataSourceHelper.changeToSpecificDataSource(dbName);
        try {
            handlerUserDepartmentMapping(eid, users, deptIdMap);
            insertUserRelatedInfo(users, eid, dbName, authInfo, handlerUserIds);
            if(SongXiaEnterpriseEnum.songXiaCompany(eid)){
                log.info("songXiaCompany 不处理部门数据 error, eid={}", eid);
                return;
            }
            //处理用户和区域的关系
            handlerUserRegionMapping(eid, users, unclassifiedRegionId, isScopeChange);
        } catch (Exception e) {
            log.error("dealUsers insertUserRelatedInfo error, corpId={}", corpId, e);
            return;
        }
    }

    public void insertUserRelatedInfo(List<EnterpriseUserRequest> deptUsers, String eid, String dbName, AuthInfoDTO authInfo, Set<String> handlerUserIds) {

        if (CollectionUtils.isEmpty(deptUsers)) {
            log.info("insertUserRelatedInfo deptUsers is empty,eid:{}",eid);
            return;
        }
        log.info("insertUserRelatedInfo-{}, deptUsersSize：{}",eid, deptUsers.size());
        DataSourceHelper.reset();
        //提取enterpriseUserDO
        List<EnterpriseUserDO> collect = ListUtils.emptyIfNull(deptUsers).stream()
                .map(EnterpriseUserRequest::getEnterpriseUserDO)
                .collect(Collectors.toList());
        //将用户插入平台库
        enterpriseUserService.batchInsertPlatformUsers(collect);
        List<EnterpriseUserMappingDO> mappings = enterpriseUserMappingService.buildEnterpriseUserMappings(eid, collect);
        enterpriseUserMappingService.batchInsertOrUpdate(mappings);

        DataSourceHelper.changeToSpecificDataSource(dbName);
        enterpriseUserService.batchInsertOrUpdate(collect, eid);
        // 同步用户与角色的关系
        Long masterRoleId = sysRoleService.getRoleIdByRoleEnum(eid, Role.MASTER.getRoleEnum());
        Long employeeRoleId = sysRoleService.getRoleIdByRoleEnum(eid, Role.EMPLOYEE.getRoleEnum());
        Long subMaster = sysRoleService.getRoleIdByRoleEnum(eid, Role.SUB_MASTER.getRoleEnum());
        Long shopOwner = sysRoleService.getRoleIdByRoleEnum(eid, Role.SHOPOWNER.getRoleEnum());
        List<EnterpriseUserRole> userRoles = new ArrayList<>();
        collect.forEach(f -> {
            if (Objects.nonNull(f)) {
                //记录此次操作的用户
                handlerUserIds.add(f.getUserId());
                if (Objects.nonNull(f.getIsAdmin()) && f.getIsAdmin()) {
                    log.info("绑定管理员角色，{}", f.getUserId());
                    userRoles.add(new EnterpriseUserRole(masterRoleId.toString(), f.getUserId()));
                    //如果是开通人，再给子管理员权限
                    if (f.getUserId().equals(authInfo.getAuthUserInfo().getUserId())) {
                        userRoles.add(new EnterpriseUserRole(subMaster.toString(), f.getUserId()));
                        userRoles.add(new EnterpriseUserRole(shopOwner.toString(), f.getUserId()));
                    }
                } else {
                    //如果用户没有角色，设置为未分配
                    if (enterpriseUserRoleMapper.selectCountsByUserId(eid, f.getUserId()) == 0) {
                        userRoles.add(new EnterpriseUserRole(employeeRoleId.toString(), f.getUserId()));
                    }
                }
            }
        });
        if (CollectionUtils.isNotEmpty(userRoles)) {
            sysRoleService.insertBatchUserRole(eid, userRoles);
        }
    }
}




