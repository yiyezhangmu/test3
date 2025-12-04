package com.coolcollege.intelligent.facade.enterprise.init;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.shade.com.google.common.util.concurrent.RateLimiter;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.constant.TwoResultTuple;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.songxia.SongXiaEnterpriseEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.ListOptUtils;
import com.coolcollege.intelligent.convert.ConvertFactory;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dto.AuthInfoDTO;
import com.coolcollege.intelligent.dto.AuthScopeDTO;
import com.coolcollege.intelligent.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.dto.SysDepartmentDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserRole;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseInitDeptOrderDTO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.sync.conf.SyncConfig.DEFAULT_BATCH_MAX_SIZE;
import static com.coolcollege.intelligent.common.sync.conf.SyncConfig.DEFAULT_BATCH_SIZE;


/**
 * 钉钉的企业开通的初始化同步逻辑
 * @author xuanfeng
 */
@Component
@Data
@Slf4j
public class DingEnterpriseInitService extends EnterpriseInitBaseService {

    @Autowired
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource(name = "isvDingDingQwThreadPool")
    private ThreadPoolTaskExecutor executor;
    @Resource
    private SimpleMessageService simpleMessageService;

    @Autowired
    private RedisUtilPool redisUtilPool;

    private final RateLimiter rateLimiter = RateLimiter.create(5);

    @Override
    public void enterpriseInit(String corpId, String eid, String appType, String dbName, String openUserId){
        try {
            //优先处理ai用户  保证可超登
            List<EnterpriseUserRequest> deptUsers = new ArrayList<>();
            //处理ai用户
            //deptUsers.add(getAIUser());
            //获取授权信息
            AuthInfoDTO authInfo = enterpriseInitConfigApiService.getAuthInfo(corpId, appType);
            //记录此次处理的用户的id
            Set<String> handlerUserIds = new HashSet<>();
            dealUsers(deptUsers, eid, corpId, dbName, authInfo, appType, new HashMap<>(), null, handlerUserIds, Boolean.TRUE, false);
            //初始化部门
            SysDepartmentDTO sysDepartmentDTO = initDepartment(corpId, eid, appType, dbName, authInfo);
            //初始化根区域
            initRootRegion(sysDepartmentDTO, eid, dbName);
            TwoResultTuple<Set<String>, Map<String, String>> allDeptInfo = sysDepartmentService.getAllDeptInfo(eid);
            //初始化用户
            initUser(corpId, eid, appType, dbName, authInfo, allDeptInfo, handlerUserIds, Boolean.TRUE, false);
            // 设置留资缓存
            redisUtilPool.hashSet(RedisConstant.LEAVE_ENTERPRISE, eid, eid, 7 * 24 * 60 * 60);
            //send 消息 去异步补全部门和区域顺序值
            EnterpriseInitDeptOrderDTO msgDto = new EnterpriseInitDeptOrderDTO(appType, corpId, eid,
                    dbName, allDeptInfo.first.stream().collect(Collectors.toList()));
            simpleMessageService.send(JSONObject.toJSONString(msgDto), RocketMqTagEnum.ENTERPRISE_INIT_DEPT_ORDER);
        } catch (ApiException e) {
            log.error("【DingEnterpriseInitService enterpriseInit call rpc has exception】", e);
            throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
        }
    }

    @Override
    public void enterpriseInitDepartment(String corpId, String eid, String appType, String dbName) {
        try {
            //获取授权信息
            AuthInfoDTO authInfo = enterpriseInitConfigApiService.getAuthInfo(corpId, appType);
            //初始化部门
            SysDepartmentDTO sysDepartmentDTO = initDepartment(corpId, eid, appType, dbName, authInfo);
            //初始化根区域
            initRootRegion(sysDepartmentDTO, eid, dbName);
        } catch (ApiException e) {
            log.error("【DingEnterpriseInitService enterpriseInitDepartment call rpc has exception】", e);
        }
    }

    @Override
    public void enterpriseInitUser(String corpId, String eid, String appType, String dbName, Boolean isScopeChange) {
        try {
            DataSourceHelper.changeToSpecificDataSource(dbName);
            //获取授权信息
            AuthInfoDTO authInfo = enterpriseInitConfigApiService.getAuthInfo(corpId, appType);
            initDepartment(corpId, eid, appType, dbName, authInfo);
            TwoResultTuple<Set<String>, Map<String, String>> allDeptInfo = sysDepartmentService.getAllDeptInfo(eid);
            //记录此次处理的用户的id， 用于做用户无用用户的删除
            Set<String> handlerUserIds = new HashSet<>();
            //初始化用户
            initUser(corpId, eid, appType, dbName, authInfo, allDeptInfo, handlerUserIds, Boolean.FALSE, isScopeChange);
//            //无用用户的删除
//            deleteUser(eid, handlerUserIds);
        } catch (ApiException e) {
            log.error("【DingEnterpriseInitService enterpriseInitUser call rpc has exception】", e);
        }
    }

    @Override
    public void onlySyncUser(String corpId, String eid, String appType, String dbName) {
        try {
            DataSourceHelper.changeToSpecificDataSource(dbName);
            //获取授权信息
            AuthInfoDTO authInfo = enterpriseInitConfigApiService.getAuthInfo(corpId, appType);
            //获取钉钉部门
            List<SysDepartmentDTO> sysDepartmentDOS = getAllDepartmentsByDing(corpId, appType, authInfo);
            Set<String> deptIdSet = sysDepartmentDOS.stream().map(SysDepartmentDTO::getId).collect(Collectors.toSet());
            Map<String, String> deptIdMap = sysDepartmentDOS.stream().filter(d -> d.getParentId() != null).collect(Collectors.toMap(SysDepartmentDTO::getId, SysDepartmentDTO::getParentId));
            TwoResultTuple<Set<String>, Map<String, String>> allDeptInfo = new TwoResultTuple<>(deptIdSet, deptIdMap);
            //记录此次处理的用户的id， 用于做用户无用用户的删除
            Set<String> handlerUserIds = new HashSet<>();
            //初始化用户
            initUser(corpId, eid, appType, dbName, authInfo, allDeptInfo, handlerUserIds, Boolean.FALSE, false);
            //无用用户的删除
            deleteUser(eid, handlerUserIds);
        } catch (ApiException e) {
            log.error("【DingEnterpriseInitService onlySyncUser call rpc has exception】", e);
        }
    }

    public List<SysDepartmentDTO> getAllDepartmentsByDing(String corpId, String appType, AuthInfoDTO authInfo) {
        List<SysDepartmentDTO> sysDepartmentDOS = new ArrayList<>();
        try {
            //rpc 首次获取授权范围的部门信息
            List<SysDepartmentDTO> departments = enterpriseInitConfigApiService.getDepartments(corpId, appType, null);
            //命中授权范围里是否有根部门，没有则需要构造一个根部门
            Optional<SysDepartmentDTO> first = ListUtils.emptyIfNull(departments)
                    .stream()
                    .filter(s -> Objects.equals(SyncConfig.ROOT_DEPT_ID_STR, s.getId()))
                    .findFirst();
            if (!first.isPresent()) {
                //构造根部门
                if (Objects.nonNull(authInfo)) {
                    SysDepartmentDTO rootDepartment = new SysDepartmentDTO();
                    rootDepartment.setId(SyncConfig.ROOT_DEPT_ID_STR);
                    rootDepartment.setName(authInfo.getAuthCorpInfo().getCorpName());
                    //根部门单独落库
                    sysDepartmentDOS.add(rootDepartment);
                }
            }
            //获取下一级的所有部门信息
            if (CollectionUtils.isNotEmpty(departments)) {
                sysDepartmentDOS.addAll(departments);
                getChildDepartment(departments, corpId, appType, sysDepartmentDOS);
            }
        } catch (ApiException e) {
            log.error("【DingEnterpriseInitService getAllDepartmentsByDing call rpc has exception】", e);
            throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
        }
        return sysDepartmentDOS;
    }

    public void getChildDepartment(List<SysDepartmentDTO> sysDepartmentDTOS, String corpId, String appType, List<SysDepartmentDTO> sysDepartmentDOS) {
        List<CompletableFuture<List<SysDepartmentDTO>>> futureList = new ArrayList<>();
        List<SysDepartmentDTO> results = new ArrayList<>();
        for (SysDepartmentDTO sysDepartment : sysDepartmentDTOS) {
            try {
                rateLimiter.acquire();
                CompletableFuture<List<SysDepartmentDTO>> future = CompletableFuture.supplyAsync(() -> {
                    List<SysDepartmentDTO> depts = new ArrayList<>();
                    try {
                        depts = enterpriseInitConfigApiService.getDepartments(corpId, appType, sysDepartment.getId());
                    } catch (Exception e) {
                        log.error("【DingEnterpriseInitService getChildDepartment call rpc has exception】", e);
                        throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
                    }
                    return depts;
                }, executor);
                futureList.add(future);
            } catch (Exception e) {
                throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
            }
        }
        for (Future<List<SysDepartmentDTO>> future : futureList) {
            try {
                List<SysDepartmentDTO> departmentDTOS = future.get();
                if (CollectionUtils.isNotEmpty(departmentDTOS)) {
                    results.addAll(departmentDTOS);
                }
            } catch (Exception e) {
                log.error("【get result has exception】", e);
                throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
            }
        }
        if (CollectionUtils.isNotEmpty(results)) {
            sysDepartmentDOS.addAll(results);
            //递归继续获取下一层级的所有部门数据
            getChildDepartment(results, corpId, appType, sysDepartmentDOS);
        }
    }

    /**
     * 初始化部门信息，此方法中包含rpc首次获取授权部门信息，落库，以及获取下一级所有部门数据
     * 采用一层的数据进行一次获取，进行一次落库
     * @param corpId
     * @param eid
     * @param appType
     * @param dbName
     */
    public SysDepartmentDTO initDepartment(String corpId, String eid, String appType, String dbName, AuthInfoDTO authInfo) {
        SysDepartmentDTO rootDepartment = new SysDepartmentDTO();
        try {
            //rpc 首次获取授权范围的部门信息
            List<SysDepartmentDTO> departments = enterpriseInitConfigApiService.getDepartments(corpId, appType, null);
            //命中授权范围里是否有根部门，没有则需要构造一个根部门
            Optional<SysDepartmentDTO> first = ListUtils.emptyIfNull(departments)
                    .stream()
                    .filter(s -> Objects.equals(SyncConfig.ROOT_DEPT_ID_STR, s.getId()))
                    .findFirst();
            if (!first.isPresent()) {
                //构造根部门
                if (Objects.nonNull(authInfo)) {
                    rootDepartment.setId(SyncConfig.ROOT_DEPT_ID_STR);
                    rootDepartment.setName(authInfo.getAuthCorpInfo().getCorpName());
                    //根部门单独落库
                    insertDepartment(Arrays.asList(rootDepartment), eid, dbName, appType);
                }
            } else {
                rootDepartment = first.get();
            }
            //首次落库，采用层级落库，一层级进行落库一次
            insertDepartment(departments, eid, dbName, appType);
            //获取下一级的所有部门信息
            if (CollectionUtils.isNotEmpty(departments)) {
                handlerDepartment(departments, corpId, eid, appType, dbName);
            }
        } catch (ApiException e) {
            log.error("【DingEnterpriseInitService initDepartment call rpc has exception】", e);
            throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
        }
        //返回根部门，为了后续构造根区域
        return rootDepartment;
    }

    /**
     * 处理rpc数据，递归调用，获取下一层级所有部门，进行落库
     * @param departments
     * @param corpId
     * @param eid
     * @param appType
     * @param dbName
     */
    public void handlerDepartment(List<SysDepartmentDTO> departments, String corpId, String eid, String appType, String dbName) {
        List<SysDepartmentDTO> results = new ArrayList<>();
        List<CompletableFuture<List<SysDepartmentDTO>>> futures = new ArrayList<>();
        for (SysDepartmentDTO sysDepartmentDTO : departments) {
            try {
                rateLimiter.acquire();
                CompletableFuture<List<SysDepartmentDTO>> future = CompletableFuture.supplyAsync(() -> {
                    List<SysDepartmentDTO> serviceDepartments = new ArrayList<>();
                    try {
                        serviceDepartments = enterpriseInitConfigApiService.getDepartments(corpId, appType, sysDepartmentDTO.getId());
                    } catch (Exception e) {
                        log.error("【DingEnterpriseInitService handlerDepartment call rpc has exception】", e);
                        throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
                    }
                    return serviceDepartments;
                }, executor);
                futures.add(future);
            } catch (Exception e) {
                log.error("【DingEnterpriseInitService handlerDepartment call rpc has exception】", e);
                throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
            }
        }
        for (Future<List<SysDepartmentDTO>> future : futures) {
            try {
                List<SysDepartmentDTO> departmentDTOS = future.get();
                if (CollectionUtils.isNotEmpty(departmentDTOS)) {
                    results.addAll(departmentDTOS);
                }
            } catch (Exception e) {
                log.error("【future get result has exception】", e);
                throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
            }
        }
        if (CollectionUtils.isNotEmpty(results)) {
            //采用层级落库，一层级进行落库一次，获取的次级数据落库
            insertDepartment(results, eid, dbName, appType);
            //递归继续获取下一层级的所有部门数据
            handlerDepartment(results, corpId, eid, appType, dbName);
        }
    }

    /**
     * rpc部门信息，进行层级落库插入
     * @param departmentDTOS
     * @param eid
     * @param dbName
     */
    public void insertDepartment(List<SysDepartmentDTO> departmentDTOS, String eid, String dbName, String appType) {
        //切库
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<SysDepartmentDO> sysDepartments = convertFactory.convertDeptList(departmentDTOS, appType, null);
        if (CollectionUtils.isNotEmpty(sysDepartments)) {
            //插入数据库，100条分区插入
            Lists.partition(sysDepartments, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
                sysDepartmentMapper.batchInsertOrUpdate(p, eid);
            });
        }
    }

    /**
     * 初始化根部区域
     * @param eid
     * @param dbName
     */
    public void initRootRegion(SysDepartmentDTO sysDepartmentDTO, String eid, String dbName) {
        try {
            //构造根部门
            if (Objects.nonNull(sysDepartmentDTO)) {
                RegionDO regionDO = new RegionDO();
                regionDO.setId(Long.valueOf(SyncConfig.ROOT_DEPT_ID));
                regionDO.setName(sysDepartmentDTO.getName());
                regionDO.setCreateName(Constants.SYSTEM);
                regionDO.setSynDingDeptId(SyncConfig.ROOT_DEPT_ID_STR);
                regionDO.setRegionPath(null);
                regionDO.setRegionType(RegionTypeEnum.ROOT.getType());
                regionDO.setUnclassifiedFlag(SyncConfig.ZERO);
                regionDO.setCreateTime(Calendar.getInstance().getTimeInMillis());
                regionDO.setStoreNum(SyncConfig.ONE);
                //切库
                DataSourceHelper.changeToSpecificDataSource(dbName);
                //根区域落库
                regionService.insertRoot(eid, regionDO);
                //同步部门为区域节点
                initRegionByDepartment(eid, SyncConfig.ROOT_DEPT_ID_STR);
            }
        } catch (Exception e) {
            log.error("【DingEnterpriseInitService initRootRegion has exception】", e);
            throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
        }
    }

    /**
     * 初始化同步用户
     * @param corpId
     * @param eid
     * @param appType
     * @param dbName
     * @param authInfo 开通授权信息
     * @param allDeptInfo 部门的信息，上级id和自身id 的map
     * @param handlerUserIds 记录同步人员的id
     * @param isScopeChange 是否为授权范围变更
     */
    public void initUser(String corpId, String eid, String appType, String dbName, AuthInfoDTO authInfo,
                         TwoResultTuple<Set<String>, Map<String, String>> allDeptInfo, Set<String> handlerUserIds, Boolean isFirstOpen, Boolean isScopeChange) {
        try {
            //切库
            DataSourceHelper.changeToSpecificDataSource(dbName);
            //先查询是否存在未分组区域
            RegionDO unclassifiedRegionDO = regionService.getUnclassifiedRegionDO(eid);
            //部门id
            Set<String> deptIds = allDeptInfo.first;
            //获取授权的用户id
            AuthScopeDTO authScope = enterpriseInitConfigApiService.getAuthScope(corpId, appType);
            if (!authScope.getDeptIdList().contains(SyncConfig.ROOT_DEPT_ID)) {
                deptIds.remove(SyncConfig.ROOT_DEPT_ID);
            }
            List<String> allDeptIds = new ArrayList<>(deptIds);
            //初始化数据集合
            List<EnterpriseUserRequest> deptUsers = new ArrayList<>();
            //处理可见范围下的人的入库
            if (CollectionUtils.isNotEmpty(authScope.getUserIdList())) {
                //根据用户id获取用的详情数据，调用rpc
                List<EnterpriseUserDTO> enterpriseUserDTOS = enterpriseInitConfigApiService.getUserDetailByUserIds(corpId, authScope.getUserIdList(), appType);
                if (CollectionUtils.isNotEmpty(enterpriseUserDTOS)) {
                    for (EnterpriseUserDTO enterpriseUserDTO : enterpriseUserDTOS) {
                        //返回结果转换
                        EnterpriseUserDO enterpriseUserDO = convertFactory.convertEnterpriseUserDTO2EnterpriseUserDO(enterpriseUserDTO);
                        //封装使用的结果
                        EnterpriseUserRequest enterpriseUserRequest = new EnterpriseUserRequest();
                        enterpriseUserRequest.setEnterpriseUserDO(enterpriseUserDO);
                        enterpriseUserRequest.setDepartmentLists(ListOptUtils.getIntersection(allDeptIds, enterpriseUserDTO.getDepartmentLists()));
                        enterpriseUserRequest.setLeaderInDepts(ListOptUtils.getIntersection(allDeptIds, enterpriseUserDTO.getIsLeaderInDepts()));
                        deptUsers.add(enterpriseUserRequest);
                        if (deptUsers.size() > RECORD_MAX_SIZE) {
                            dealUsers(deptUsers, eid, corpId, dbName, authInfo, appType, allDeptInfo.second, unclassifiedRegionDO.getId(), handlerUserIds, isFirstOpen, isScopeChange);
                            deptUsers.clear();
                        }
                    }
                    if (CollectionUtils.isNotEmpty(deptUsers)) {
                        dealUsers(deptUsers, eid, corpId, dbName, authInfo, appType, allDeptInfo.second, unclassifiedRegionDO.getId(), handlerUserIds, isFirstOpen, isScopeChange);
                        deptUsers.clear();
                    }
                }
            }
            //授权部门的用户处理
            handlerDepartmentUser(corpId, eid, appType, dbName, authInfo, allDeptInfo, handlerUserIds, unclassifiedRegionDO, deptIds, allDeptIds, isFirstOpen, isScopeChange);
        } catch (Exception e) {
            log.error("dingTalk enterpriseInit initUser error,eid:{},appType:{}", eid, appType, e);
            throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
        }
    }

    /**
     *
     * @param corpId
     * @param eid
     * @param appType
     * @param dbName
     * @param authInfo
     * @param allDeptInfo
     * @param handlerUserIds
     * @param unclassifiedRegionDO
     * @param deptIds
     * @param allDeptIds
     * @param isScopeChange 是否为授权范围变更
     */
    private void handlerDepartmentUser(String corpId, String eid, String appType, String dbName,
                                       AuthInfoDTO authInfo, TwoResultTuple<Set<String>, Map<String, String>> allDeptInfo, Set<String> handlerUserIds,
                                       RegionDO unclassifiedRegionDO, Set<String> deptIds, List<String> allDeptIds, Boolean isFirstOpen, Boolean isScopeChange) {
        List<EnterpriseUserRequest> deptUsers = new ArrayList<>();
        //处理授权下的部门用户同步
        List<CompletableFuture<List<EnterpriseUserRequest>>> futures = new ArrayList<>();
        for (String deptId : deptIds) {
            try {
                rateLimiter.acquire();
                CompletableFuture<List<EnterpriseUserRequest>> future = CompletableFuture.supplyAsync(() -> {
                    List<EnterpriseUserRequest> results = new ArrayList<>();
                    try {
                        //rpc调用获取部门下的用户
                        List<EnterpriseUserDTO> dingTalkDepartmentUsers = null;
                        int retryCount = 0;
                        while (true) {
                            try {
                                dingTalkDepartmentUsers = enterpriseInitConfigApiService.getDepartmentUsers(corpId, deptId, appType);
                                break; // 成功获取数据，跳出重试循环
                            } catch (Exception e) {
                                retryCount++;
                                if (retryCount >= 3) {
                                    // 达到最大重试次数，抛出异常
                                    throw e;
                                }
                                // 等待一段时间再重试
                                Thread.sleep(1000);
                                log.warn("DingEnterpriseInitService initUser call rpc failed, retrying {}, deptId {}", retryCount, deptId, e);
                            }
                        }
                        ListUtils.emptyIfNull(dingTalkDepartmentUsers)
                                .forEach(e -> {
                                    if (Objects.isNull(e)) {
                                        return;
                                    }
                                    EnterpriseUserRequest enterpriseUserRequest = new EnterpriseUserRequest();
                                    enterpriseUserRequest.setEnterpriseUserDO(convertFactory.convertEnterpriseUserDTO2EnterpriseUserDO(e));
                                    enterpriseUserRequest.setDepartmentLists(ListOptUtils.getIntersection(allDeptIds, e.getDepartmentLists()) );
                                    enterpriseUserRequest.setLeaderInDepts(ListOptUtils.getIntersection(allDeptIds, e.getIsLeaderInDepts()));
                                    results.add(enterpriseUserRequest);
                                });
                        return results;
                    } catch (Exception e) {
                        log.error("【DingEnterpriseInitService initUser call rpc has exception】", e);
                        throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
                    }
                }, executor);
                futures.add(future);
            } catch (Exception e) {
                log.error("【DingEnterpriseInitService initUser call rpc has exception】", e);
                throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
            }
            if (futures.size() > DEFAULT_BATCH_SIZE) {
                futures.forEach(data -> {
                    try {
                        List<EnterpriseUserRequest> enterpriseUserRequests = data.get();
                        if (CollectionUtils.isNotEmpty(enterpriseUserRequests)) {
                            deptUsers.addAll(enterpriseUserRequests);
                        }
                    } catch (Exception e) {
                        log.error("【DingEnterpriseInitService initUser future get has exception】", e);
                        throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
                    }
                });
                //处理授权用户的信息，进行落库等操作
                dealUsers(deptUsers, eid, corpId, dbName, authInfo, appType, allDeptInfo.second, unclassifiedRegionDO.getId(), handlerUserIds, isFirstOpen, isScopeChange);
                //清空集合
                deptUsers.clear();
                futures.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(futures)) {
            for (CompletableFuture<List<EnterpriseUserRequest>> data : futures) {
                try {
                    List<EnterpriseUserRequest> enterpriseUserRequests = data.get();
                    if (CollectionUtils.isNotEmpty(enterpriseUserRequests)) {
                        deptUsers.addAll(enterpriseUserRequests);
                    }
                } catch (Exception e) {
                    log.error("【DingEnterpriseInitService initUser get future has exception】", e);
                    //throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
                    continue;
                }
            }
            //处理授权用户的信息，进行落库等操作
            dealUsers(deptUsers, eid, corpId, dbName, authInfo, appType, allDeptInfo.second, unclassifiedRegionDO.getId(), handlerUserIds, isFirstOpen, isScopeChange);
            //清空集合
            deptUsers.clear();
            futures.clear();
        }
    }

    /**
     * 处理api获取到的用户详细信息
     * @param users
     * @param eid
     * @param corpId
     * @param dbName
     * @param authInfo
     * @param appType
     * @param deptIdMap 部门的信息，上级id和自身id 的map
     * @param unclassifiedRegionId 未分组的id
     * @param handlerUserIds 记录同步人员的id，做授权变更后的删除
     * @param isScopeChange 是否为授权范围变更
     */
    public void dealUsers(List<EnterpriseUserRequest> users, String eid, String corpId, String dbName, AuthInfoDTO authInfo,
                          String appType, Map<String, String> deptIdMap, Long unclassifiedRegionId, Set<String> handlerUserIds, Boolean isFirstOpen, Boolean isScopeChange) {
        if (CollectionUtils.isEmpty(users)) {
            return;
        }
        // 过滤掉 enterpriseUserDO 为 null 或者 active 为 false 的用户
        // 提取并过滤出 active 为 true 的 EnterpriseUserDO 列表
        Set<String> deleteUsersIds = users.stream().map(EnterpriseUserRequest::getEnterpriseUserDO).filter(Objects::nonNull).filter(user -> Objects.nonNull(user.getActive()) && !user.getActive())
                .map(EnterpriseUserDO::getUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        DataSourceHelper.changeToSpecificDataSource(dbName);
        deleteUserByUserIds(eid, deleteUsersIds);
        users = users.stream().filter(user -> Objects.nonNull(user.getEnterpriseUserDO()) && Objects.nonNull(user.getEnterpriseUserDO().getActive()) && user.getEnterpriseUserDO().getActive()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(users)){
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //处理用户和部门的关系
        //设置用户部门全路劲
        handlerUserDepartmentMapping(eid, users, deptIdMap);
        //处理用户的落库以及角色关系
        handlerUserRelatedInfo(users, eid, corpId, dbName, authInfo, appType, deptIdMap, handlerUserIds, isFirstOpen);
        //处理用户和区域的关系
        handlerUserRegionMapping(eid, users, unclassifiedRegionId, isScopeChange);
    }

    /**
     * 处理用户信息的落库以及角色
     * @param users
     * @param eid
     * @param corpId
     * @param dbName
     * @param authInfo
     * @param appType
     * @param deptIdMap  部门的信息，上级id和自身id 的map
     * @param handlerUserIds 记录同步人员的id，做授权变更后的删除
     */
    public void handlerUserRelatedInfo(List<EnterpriseUserRequest> users, String eid, String corpId, String dbName,
                                       AuthInfoDTO authInfo, String appType, Map<String, String> deptIdMap, Set<String> handlerUserIds, Boolean isFirstOpen) {
        try {
            //处理个人应用开通获取用户基本信息
            Boolean isPersonal = false;
            //切平台库
            DataSourceHelper.reset();
            if (CollectionUtils.isNotEmpty(users) && users.size() == SyncConfig.ONE && isFirstOpen) {
                String mainUserId = authInfo.getAuthUserInfo().getUserId();
                String mainCorpId = enterpriseInitConfigApiService.getMainCorpId(corpId, mainUserId, appType);
                if (StringUtils.isNotBlank(mainCorpId)) {
                    enterpriseConfigMapper.updateMainCorpIdByEnterpriseId(eid, mainCorpId);
                    isPersonal = true;
                    try {
                        EnterpriseUserDTO enterpriseUserDTO = enterpriseInitConfigApiService.getUserDetailByUserId(corpId, mainUserId, appType);
                        EnterpriseUserRequest enterpriseUserRequest = new EnterpriseUserRequest();
                        enterpriseUserRequest.setDepartmentLists(enterpriseUserDTO.getDepartmentLists());
                        enterpriseUserRequest.setEnterpriseUserDO(convertFactory.convertEnterpriseUserDTO2EnterpriseUserDO(enterpriseUserDTO));
                        enterpriseUserService.updateUserDeptPath(enterpriseUserRequest, deptIdMap);
                        users.add(enterpriseUserRequest);
                    } catch (ApiException e) {
                        log.error("个人开通获取用户信息失败 corpId={},userId:{}", corpId, mainUserId);
                    }
                }
            }

            //提取enterpriseUserDO
            List<EnterpriseUserDO> enterpriseUserDOS = users.stream()
                    .map(EnterpriseUserRequest::getEnterpriseUserDO)
                    .collect(Collectors.toList());
            List<EnterpriseUserDO> userList = new ArrayList<>();
            log.info("插入平台库总用户数用户日志:{}", JSONObject.toJSONString(users.size()));
            Lists.partition(enterpriseUserDOS, DEFAULT_BATCH_SIZE).forEach(data -> {
                enterpriseUserService.batchInsertPlatformUsers(data);
                userList.addAll(data);

            });
            //企业用户映射关系，平台库
            List<EnterpriseUserMappingDO> mappings = enterpriseUserMappingService.buildEnterpriseUserMappings(eid, userList);
            enterpriseUserMappingService.batchInsertOrUpdate(mappings);
            //切换企业库
            DataSourceHelper.changeToSpecificDataSource(dbName);
            //同步用户与角色的关系
            Long roleIdByRoleEnum = sysRoleService.getRoleIdByRoleEnum(eid, Role.MASTER.getRoleEnum());
            Long subMaster = sysRoleService.getRoleIdByRoleEnum(eid, Role.SUB_MASTER.getRoleEnum());
            //rpc 调用获取管理员列表
            List<String> mainAdminIdList = enterpriseInitConfigApiService.getAdminUserList(corpId, appType);
            log.info("人员列表：{}, 主管理员id：{}", userList, mainAdminIdList);
            List<EnterpriseUserRole> userRoles = new ArrayList<>();
            List<String> userIds = new LinkedList<>();
            for (EnterpriseUserDO enterpriseUserDO : userList) {
                //记录此次处理用户的id
                handlerUserIds.add(enterpriseUserDO.getUserId());
                Integer hasRecord = sysRoleService.countRoleByPerson(eid, enterpriseUserDO.getUserId());
                if (Objects.nonNull(hasRecord) && hasRecord > 0) {
                    //do noting
                    enterpriseUserDO.setMainAdmin(ListUtils.emptyIfNull(mainAdminIdList).stream().anyMatch(data -> data.equals(enterpriseUserDO.getUserId())));
                } else {
                    userIds.add(enterpriseUserDO.getUserId());
                    enterpriseUserDO.setMainAdmin(ListUtils.emptyIfNull(mainAdminIdList).stream().anyMatch(data -> data.equals(enterpriseUserDO.getUserId())));
                    //管理员第一次开通设置为数智门店管理员，个人应用开通设置为管理员和主管理员
                    if ((enterpriseUserDO.getIsAdmin() && isFirstOpen) || isPersonal) {
                        log.info("绑定管理员角色，{}", enterpriseUserDO.getUserId());
                        userRoles.add(new EnterpriseUserRole(roleIdByRoleEnum.toString(), enterpriseUserDO.getUserId()));
                        if(isPersonal){
                            enterpriseUserDO.setMainAdmin(true);
                        }
                    }
                    //如果是开通人，再给子管理员权限
                    if (enterpriseUserDO.getUserId().equals(authInfo.getAuthUserInfo().getUserId())) {
                        userRoles.add(new EnterpriseUserRole(subMaster.toString(), enterpriseUserDO.getUserId()));
                    }
                }
            }
            //在处理完是否是主管理员后，再进行添加或者更新
            log.info("在处理完是否是主管理员后，进行添加或者更新。人员列表：{}", userList);
            enterpriseUserService.batchInsertOrUpdate(userList, eid);
            if (CollectionUtils.isNotEmpty(userIds)) {
                List<EnterpriseUserRole> enterpriseUserRoleList = userRoles.stream()
                        .filter(data -> userIds.contains(data.getUserId()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(enterpriseUserRoleList)) {
                    sysRoleService.insertBatchUserRole(eid, enterpriseUserRoleList);
                }
            }
        } catch (ApiException e) {
            log.error("insertUserRelatedInfo error, corpId={}", corpId, e);
            throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
        }
    }

}
