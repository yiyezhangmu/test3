package com.coolcollege.intelligent.facade.store.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.role.CoolPositionTypeEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.store.GetStoreDTO;
import com.coolcollege.intelligent.facade.dto.store.GetStoreUserDTO;
import com.coolcollege.intelligent.facade.dto.store.StoreFacadeDTO;
import com.coolcollege.intelligent.facade.dto.store.StoreUserInfoDTO;
import com.coolcollege.intelligent.facade.request.GetStoreUserRequest;
import com.coolcollege.intelligent.facade.store.StoreFacade;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.UserRegionMappingDO;
import com.coolcollege.intelligent.model.enterprise.dto.EntUserRoleDTO;
import com.coolcollege.intelligent.model.enums.StoreIsDeleteEnum;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.AuthBaseVisualDTO;
import com.coolcollege.intelligent.model.region.dto.AuthStoreUserDTO;
import com.coolcollege.intelligent.model.region.dto.RegionNode;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.store.dto.StoreUserDTO;
import com.coolcollege.intelligent.model.store.queryDto.StoreQueryDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.SubordinateMappingService;
import com.coolcollege.intelligent.service.enterprise.UserPersonInfoService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.dto.ResultDTO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 门店信息RPC接口实现
 * @author zhangnan
 * @date 2021-11-19 11:21
 */
@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.STORE_FACADE_FACADE_UNIQUE_ID ,interfaceType = StoreFacade.class, bindings = {@SofaServiceBinding(bindingType = "bolt")})
@Component
public class StoreFacadeImpl implements StoreFacade{

    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private AuthVisualService authVisualService;
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private UserRegionMappingDAO userRegionMappingDAO;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private SubordinateMappingService subordinateMappingService;

    @Resource
    private UserPersonInfoService userPersonInfoService;
    @Autowired
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Override
    public ResultDTO<PageDTO<StoreFacadeDTO>> getStorePage(GetStoreDTO request) {
        log.info("facade api get store page request:{}", JSONObject.toJSONString(request));
        PageDTO<StoreFacadeDTO> storePage = new PageDTO<>();
        storePage.setPageNum(request.getPageNum());
        storePage.setPageSize(request.getPageSize());
        storePage.setTotal((long)Constants.ZERO);
        if(StringUtils.isBlank(request.getEnterpriseId())) {
            return ResultDTO.successResult(storePage);
        }
        // 根据企业id切库
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(request.getEnterpriseId());
        DataSourceHelper.changeToSpecificDataSource(dbName);
        // 查询用户权限范围
        AuthBaseVisualDTO baseVisualDTO = authVisualService.baseAuth(request.getEnterpriseId(), request.getUserId());
        // 判断用户没有权限直接返回空的分页结果
        if(CollectionUtils.isEmpty(baseVisualDTO.getStoreIdList())
                && CollectionUtils.isEmpty(baseVisualDTO.getRegionIdList())
                && !baseVisualDTO.getIsAllStore()){
            return ResultDTO.successResult(storePage);
        }
        // 根据权限和入参查询门店信息（分页）
        storePage = this.getStorePage(request, baseVisualDTO);
        // 没有查到门店数据，直接返回结果
        if(CollectionUtils.isEmpty(storePage.getList())) {
            return ResultDTO.successResult(storePage);
        }
        // 根据门店查询对应区域信息
        this.getStoreRegion(request.getEnterpriseId(), storePage.getList());
        // 判断是否统计门店用户数量

        if(BooleanUtils.isTrue(request.getHasUserCount())) {
            this.countStoreUser(request.getEnterpriseId(), storePage.getList());
        }
        return ResultDTO.successResult(storePage);
    }

    @Override
    public ResultDTO<PageDTO<StoreFacadeDTO>> getOrganizationStorePage(GetStoreDTO request) {
        log.info("facade api getOrganizationStorePage request:{}", JSONObject.toJSONString(request));
        PageDTO<StoreFacadeDTO> storePage = new PageDTO<>();
        storePage.setPageNum(request.getPageNum());
        storePage.setPageSize(request.getPageSize());
        storePage.setTotal((long)Constants.ZERO);
        if(StringUtils.isBlank(request.getEnterpriseId())) {
            return ResultDTO.successResult(storePage);
        }
        // 根据企业id切库
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(request.getEnterpriseId());
        DataSourceHelper.changeToSpecificDataSource(dbName);
        // 查询用户权限范围
        AuthBaseVisualDTO baseVisualDTO = authVisualService.baseAuth(request.getEnterpriseId(), request.getUserId());
        // 判断用户没有权限直接返回空的分页结果
        if(CollectionUtils.isEmpty(baseVisualDTO.getStoreIdList())
                && CollectionUtils.isEmpty(baseVisualDTO.getRegionIdList())
                && !baseVisualDTO.getIsAllStore()){
            return ResultDTO.successResult(storePage);
        }
        // 根据权限和入参查询门店信息（分页）
        storePage = this.getStorePage(request, baseVisualDTO);
        // 没有查到门店数据，直接返回结果
        if(CollectionUtils.isEmpty(storePage.getList())) {
            return ResultDTO.successResult(storePage);
        }
        // 根据门店查询对应区域信息
        this.getStoreRegion(request.getEnterpriseId(), storePage.getList());
        // 判断是否统计门店用户数量

        if(BooleanUtils.isTrue(request.getHasUserCount())) {
            this.countStoreUsers(request.getEnterpriseId(), storePage.getList());
        }
        return ResultDTO.successResult(storePage);
    }

    @Override
    public ResultDTO<PageDTO<GetStoreUserDTO>> getStoreUserInfoByStoreIdPage(GetStoreUserRequest request) {
        log.info("StoreFacade->getStoreUserInfoByStoreIdPage request:{}", JSONObject.toJSONString(request));
        if(StringUtils.isBlank(request.getEnterpriseId()) || StringUtils.isBlank(request.getStoreId())) {
            return ResultDTO.failResult("enterpriseId or storeId can`t be empty");
        }
        String enterpriseId = request.getEnterpriseId();
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        RegionDO regionDO = regionMapper.getByStoreId(enterpriseId, request.getStoreId());
        //分页获取门店下人员信息  根据组织结构取
        //List<StoreUserDTO> storeUserDTOS = getStoreUserDTO(request, Boolean.TRUE);
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        Page<String> userIds = (Page<String>) userRegionMappingDAO.getUserIdsByRegionIds(request.getEnterpriseId(), Arrays.asList(regionDO.getRegionId()));
        PageDTO<GetStoreUserDTO> pageResult = new PageDTO<>();
        if (CollectionUtils.isEmpty(userIds)) {
            return ResultDTO.successResult(pageResult);
        }
        Map<String, String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, userIds);
        List<GetStoreUserDTO> result = ListUtils.emptyIfNull(userIds)
                .stream()
                .map(data -> {
                    GetStoreUserDTO dto = new GetStoreUserDTO();
                    dto.setUserId(data);
                    dto.setEnterpriseId(request.getEnterpriseId());
                    dto.setUserName(userNameMap.get(data));
                    return dto;
                }).collect(Collectors.toList());
        pageResult.setList(result);
        pageResult.setPageNum(userIds.getPageNum());
        pageResult.setPageSize(userIds.getPageSize());
        pageResult.setTotal(userIds.getTotal());
        return ResultDTO.successResult(pageResult);
    }


    @Override
    public ResultDTO<List<GetStoreUserDTO>> getStoreUserInfoByStoreId(GetStoreUserRequest request) {
        log.info("StoreFacade->getStoreUserInfoByStoreId request:{}", JSONObject.toJSONString(request));
        if(StringUtils.isBlank(request.getEnterpriseId()) || StringUtils.isBlank(request.getStoreId())) {
            return ResultDTO.failResult("enterpriseId or storeId can`t be empty");
        }
        //不分页获取门店下人员信息
        List<StoreUserDTO> storeUserDTO = getStoreUserDTO(request, Boolean.FALSE);
        List<GetStoreUserDTO> result = ListUtils.emptyIfNull(storeUserDTO)
                .stream()
                .map(data -> {
                    GetStoreUserDTO dto = new GetStoreUserDTO();
                    dto.setUserId(data.getUserId());
                    dto.setUserName(data.getUserName());
                    dto.setEnterpriseId(request.getEnterpriseId());
                    return dto;
                }).collect(Collectors.toList());
        return ResultDTO.successResult(result);
    }

    @Override
    public ResultDTO<Integer> getEnterpriseStoreNum(String enterpriseId) {
        log.info("StoreFacade->getEnterpriseStoreNum request:{}", enterpriseId);
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        return ResultDTO.successResult(storeMapper.countStore(enterpriseId));
    }

    @Override
    public ResultDTO<com.coolcollege.intelligent.facade.dto.store.StoreUserDTO> getStoreUserList(String enterpriseId, String storeId) {
        log.info("StoreFacade->getStoreUserList request:{}", enterpriseId);
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<String> storeIds = new ArrayList<>();
        storeIds.add(storeId);
        List<AuthStoreUserDTO> authStoreUserDTOS = authVisualService.authStoreUser(enterpriseId, storeIds, CoolPositionTypeEnum.STORE_INSIDE.getCode());
        Map<String, List<String>> storeUserMap = authStoreUserDTOS.stream().collect(Collectors.toMap(k -> k.getStoreId(), v -> v.getUserIdList(), (k1, k2) -> k1));
        com.coolcollege.intelligent.facade.dto.store.StoreUserDTO storeUser = new com.coolcollege.intelligent.facade.dto.store.StoreUserDTO();
        storeUser.setStoreId(storeId);
        storeUser.setUserIds(storeUserMap.get(storeId));
        return ResultDTO.successResult(storeUser);
    }

    @Override
    public ResultDTO<List<StoreFacadeDTO>> getStoreNameByStoreIds(String enterpriseId, List<String> storeIds) {
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<StoreDO> storeList = storeMapper.getStoreNameByIds(enterpriseId, storeIds);
        List<StoreFacadeDTO> resultList = new ArrayList<>();
        ListUtils.emptyIfNull(storeList).forEach(k->{
            StoreFacadeDTO result = new StoreFacadeDTO();
            result.setStoreId(k.getStoreId());
            result.setStoreName(k.getStoreName());
            resultList.add(result);
        });
        return ResultDTO.successResult(resultList);
    }

    @Override
    public ResultDTO<Map<String,List<String>>> getStoreMapByRegionIds(String eid, List<String> regionIds) {
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(eid);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //区域下门店
        Map<String,List<String>> regionAndStoreIdsMap = new HashMap<>();
        //查出区域下所有门店
        List<StoreAreaDTO> storeAreaDTOS = storeMapper.listStoreByRegionIdList(eid, regionIds);
        Set<String> regionIdSet = new HashSet<>(regionIds);
        storeAreaDTOS.parallelStream()
                .forEach(storeAreaDTO -> {
                    String storeId = storeAreaDTO.getStoreId();
                    String[] regionPathParts = storeAreaDTO.getRegionPath().split("/");
                    for (String regionIdPart : regionPathParts) {
                        if (!regionIdPart.isEmpty() && regionIdSet.contains(regionIdPart)) {
                            regionAndStoreIdsMap.computeIfAbsent(regionIdPart, k -> Collections.synchronizedList(new ArrayList<>())).add(storeId);
                        }
                    }
                });
        return ResultDTO.successResult(regionAndStoreIdsMap);
    }


    @Override
    public ResultDTO<List<String>> getSubordinateUserIdList(String eid, String currentUserId, Boolean addCurrentFlag) {
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(eid);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<String> subordinateUserIdList = subordinateMappingService.getSubordinateUserIdList(eid, currentUserId, addCurrentFlag);
        return ResultDTO.successResult(subordinateUserIdList);
    }

    @Override
    public ResultDTO<List<String>> getUsersByDTO(String eid, String userDTOStr){
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(eid);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<StoreWorkCommonDTO> userDTOs = JSONObject.parseArray(userDTOStr, StoreWorkCommonDTO.class);
        List<String> userId = userPersonInfoService.getUserIdListByCommonDTO(eid,userDTOs);
        return ResultDTO.successResult(userId);
    }

    @Override
    public ResultDTO<List<String>> getStoreUserByStoreList(String eid, List<String> storeIds) {
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(eid);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<RegionDO> regionIdByStoreIds = regionMapper.getRegionIdByStoreIds(eid, storeIds);
        List<String> regionIds = regionIdByStoreIds.stream().map(c -> c.getRegionId().toString()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(regionIds)) {
            return ResultDTO.successResult(Collections.emptyList());
        }
        List<String> userIds = ListUtils.emptyIfNull(userRegionMappingDAO.getUserIdsByRegionIds(eid, regionIds));
        return ResultDTO.successResult(userIds);
    }

    @Override
    public ResultDTO<List<String>> getStoreByRegionIds(String eid, List<String> regionIds) {
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(eid);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<StoreAreaDTO> storeAreaDTOS = storeMapper.listStoreByRegionIdList(eid, regionIds);
        List<String> storeIds = storeAreaDTOS.stream().map(c -> c.getStoreId()).collect(Collectors.toList());
        return ResultDTO.successResult(storeIds);
    }

    @Override
    public ResultDTO<List<StoreUserInfoDTO>> getUserListByStoreId(String eid, String storeId) {
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(eid);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<RegionDO> regionList = regionMapper.getRegionIdByStoreIds(eid, Collections.singletonList(storeId));
        if(CollectionUtils.isEmpty(regionList)){
            return ResultDTO.successResult(Collections.emptyList());
        }
        List<String> regionIds = regionList.stream().map(RegionDO::getRegionId).collect(Collectors.toList());
        List<String> userIds = ListUtils.emptyIfNull(userRegionMappingDAO.getUserIdsByRegionIds(eid, regionIds));
        List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(eid, userIds);
        List<EntUserRoleDTO> userRoleList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(userIds)){
            userRoleList = enterpriseUserRoleMapper.selectUserRoleByUserIds(eid, userIds);
        }
        Map<String, List<String>> userRoleMap = userRoleList.stream().collect(Collectors.groupingBy(EntUserRoleDTO::getUserId, Collectors.mapping(EntUserRoleDTO::getRoleName, Collectors.toList())));
        List<StoreUserInfoDTO> resultList = ListUtils.emptyIfNull(userList).stream().map(enterpriseUserDO -> {
            StoreUserInfoDTO user = new StoreUserInfoDTO();
            user.setUserId(enterpriseUserDO.getUserId());
            user.setUserName(enterpriseUserDO.getName());
            user.setAvatar(enterpriseUserDO.getAvatar());
            user.setMobile(enterpriseUserDO.getMobile());
            user.setPositionNameList(userRoleMap.get(enterpriseUserDO.getUserId()));
            return user;
        }).collect(Collectors.toList());
        return ResultDTO.successResult(resultList);
    }

    @Override
    public ResultDTO<Map<String, List<String>>> getUserListByStoreIds(String enterpriseId, List<String> storeIds) {
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<RegionDO> regionList = regionMapper.getRegionIdByStoreIds(enterpriseId, storeIds);
        Map<String, String> storeRegionMap = CollStreamUtil.toMap(regionList, o->String.valueOf(o.getRegionId()), RegionDO::getStoreId);
        List<String> regionIds = ListUtils.emptyIfNull(regionList).stream().map(o->String.valueOf(o.getRegionId())).collect(Collectors.toList());
        List<UserRegionMappingDO> userRegionList = userRegionMappingDAO.selectUserListByRegionIds(enterpriseId, regionIds);
        Map<String, List<String>> regionUserMap = ListUtils.emptyIfNull(userRegionList).stream().collect(Collectors.groupingBy(UserRegionMappingDO::getRegionId, Collectors.mapping(UserRegionMappingDO::getUserId, Collectors.toList())));
        Map<String, List<String>> storeUserMap = new HashMap<>();
        regionUserMap.forEach((regionId, userList)->{
            String storeId = storeRegionMap.get(regionId);
            storeUserMap.put(storeId, userList);
        });
        return ResultDTO.successResult(storeUserMap);
    }

    /**
     * 根据参数查询门店下人员
     * @param request
     * @param isPage
     * @return
     */
    private List<StoreUserDTO> getStoreUserDTO (GetStoreUserRequest request, boolean isPage) {
        DataSourceHelper.reset();
        List<StoreUserDTO> storeUserDTOS = new ArrayList<>();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(request.getEnterpriseId());
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //获取门店下权限人数
        List<AuthStoreUserDTO> authStoreUserDTOList = authVisualService.authStoreUser(request.getEnterpriseId(), Arrays.asList(request.getStoreId()), CoolPositionTypeEnum.STORE_INSIDE.getCode());
        if (CollectionUtils.isEmpty(authStoreUserDTOList)) {
            return storeUserDTOS;
        }
        List<String> userIdList = ListUtils.emptyIfNull(authStoreUserDTOList)
                .stream()
                .map(AuthStoreUserDTO::getUserIdList)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        //获取人员信息
        if(CollectionUtils.isNotEmpty(userIdList)){
            if (isPage) {
                PageHelper.startPage(request.getPageNum(), request.getPageSize());
            }
            storeUserDTOS = roleMapper.userAndPositionListDistinct(request.getEnterpriseId(), userIdList, null, CoolPositionTypeEnum.STORE_INSIDE.getCode());
        }
        return storeUserDTOS;
    }

    @Override
    public ResultDTO<StoreFacadeDTO> getStoreByStoreId(String enterpriseId, String storeId) {
        if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(storeId)) {
            return ResultDTO.successResult();
        }
        // 根据企业id切库
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, storeId);
        return ResultDTO.successResult(this.parseStoreDoToStoreFacadeDto(storeDO));
    }

    /**
     * 获取门店用户数量
     * @param list
     */
    private void countStoreUser(String enterpriseId, List<StoreFacadeDTO> list) {
        List<String> storeIds = list.stream().map(StoreFacadeDTO::getStoreId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(storeIds)) {
            return;
        }
        List<AuthStoreUserDTO> authStoreUserDTOList = authVisualService.authStoreUser(enterpriseId, storeIds, CoolPositionTypeEnum.STORE_INSIDE.getCode());
        if(CollectionUtils.isEmpty(authStoreUserDTOList)){
            return;
        }
        Map<String, List<String>> storeUserMap = new HashMap<>();
        authStoreUserDTOList.forEach(authStoreUserDTO -> {
            List<String> normalUserIds = new ArrayList<>();
            List<String> userIds = ListUtils.emptyIfNull(authStoreUserDTO.getUserIdList())
                    .stream()
                    .distinct()
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(userIds)){
                List<StoreUserDTO> storeUserDTOS = roleMapper.userAndPositionListDistinct(enterpriseId, userIds, null, CoolPositionTypeEnum.STORE_INSIDE.getCode());
                normalUserIds = ListUtils.emptyIfNull(storeUserDTOS)
                        .stream()
                        .map(StoreUserDTO::getUserId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList());
            }
            storeUserMap.put(authStoreUserDTO.getStoreId(), normalUserIds);
        });
        list.forEach(store -> store.setUserIds(storeUserMap.get(store.getStoreId())));
        list.forEach(store -> store.setUserCount(CollectionUtils.emptyIfNull(storeUserMap.get(store.getStoreId())).size()));
    }

    private void countStoreUsers(String enterpriseId, List<StoreFacadeDTO> list) {
        List<String> storeIds = list.stream().map(StoreFacadeDTO::getStoreId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(storeIds)) {
            return;
        }
        List<RegionDO> regionList = regionMapper.getRegionIdByStoreIds(enterpriseId, storeIds);
        List<String> regionIds = regionList.stream().map(o->o.getRegionId()).collect(Collectors.toList());
        Map<String, String> storeRegionMap = regionList.stream().collect(Collectors.toMap(k->k.getStoreId(), v->v.getRegionId(), (k1, k2)->k1));
        List<UserRegionMappingDO> userRegionList = userRegionMappingDAO.listByUserIdsAndRegionIds(enterpriseId, null, regionIds);
        if(CollectionUtils.isEmpty(userRegionList)){
            return;
        }
        List<String> allUserIds = userRegionList.stream().map(UserRegionMappingDO::getUserId).collect(Collectors.toList());
        List<String> normalUserIds = enterpriseUserDao.selectByUserIdsAndStatus(enterpriseId, allUserIds, UserStatusEnum.NORMAL.getCode());
        Map<String, List<String>> regionUserMap = userRegionList.stream().collect(Collectors.groupingBy(k -> k.getRegionId(), Collectors.mapping(o -> o.getUserId(), Collectors.toList())));
        Map<String, List<String>> storeUserMap = new HashMap<>();
        storeIds.forEach(storeId -> {
            String regionId = storeRegionMap.get(storeId);
            List<String> users = regionUserMap.get(regionId);
            if(CollectionUtils.isNotEmpty(users)){
                List<String> regionUsers = users.stream().filter(o -> normalUserIds.contains(o)).collect(Collectors.toList());
                storeUserMap.put(storeId, regionUsers);

            }
        });
        list.forEach(store -> store.setUserIds(storeUserMap.get(store.getStoreId())));
        list.forEach(store -> store.setUserCount(CollectionUtils.emptyIfNull(storeUserMap.get(store.getStoreId())).size()));
    }

    /**
     * 获取门店的区域信息
     * @param list
     */
    private void getStoreRegion(String enterpriseId, List<StoreFacadeDTO> list) {
        List<String> regionIds = list.stream().map(StoreFacadeDTO::getRegionId).distinct().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(regionIds)) {
            return;
        }
        // 通过区域id列表查询区域信息
        List<RegionDO> regionDOList = regionMapper.getRegionByRegionIds(enterpriseId, regionIds);
        // 组装regionName到门店列表中
        Map<String, String> regionDOMap = regionDOList.stream().collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getName, (a, b) -> b));
        list.forEach(store -> store.setRegionName(regionDOMap.get(store.getRegionId())));
    }


    /**
     * 分页查询门店信息
     * @param request GetStoreDTO
     * @param auth 权限
     * @return PageInfo<StoreDTO>
     */
    private PageDTO<StoreFacadeDTO> getStorePage(GetStoreDTO request, AuthBaseVisualDTO auth) {
        // 组装门店查询参数
        StoreQueryDTO queryDTO = new StoreQueryDTO();
        queryDTO.setIs_admin(auth.getIsAllStore());
        queryDTO.setStoreIds(auth.getStoreIdList());
        queryDTO.setStore_name(request.getStoreName());
        queryDTO.setRecursion(Boolean.TRUE);
        if(StringUtils.isNotBlank(request.getRegionId())) {
            RegionNode regionNode = regionMapper.getRegionByRegionId(request.getEnterpriseId(), request.getRegionId());
            queryDTO.setStore_area(regionNode.getFullRegionPath());
        }
        queryDTO.setRegionPathList(auth.getFullRegionPathList());
        queryDTO.setIs_delete(StoreIsDeleteEnum.EFFECTIVE.getValue());
        queryDTO.setStoreStatusList(request.getStoreStatusList());
        queryDTO.setOrderBy("id");
        // 组装分页参数，如果pageSize或pageNum为空 设置默认值 第1页/10条
        if(request.getPageNum() == null) {
            request.setPageNum(Constants.INDEX_ONE);
        }
        if(request.getPageSize() == null) {
            request.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        }
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        // 分页查询门店信息
        PageInfo<StoreDTO> page = new PageInfo<>(storeMapper.getStores(request.getEnterpriseId(), queryDTO));
        // 组装分页结果数据
        PageDTO<StoreFacadeDTO> storeFacadePage = new PageDTO<>();
        storeFacadePage.setTotal(page.getTotal());
        storeFacadePage.setPageNum(page.getPageNum());
        storeFacadePage.setPageSize(page.getPageSize());
        storeFacadePage.setList(page.getList().stream().map(this::parseStoreDtoToStoreFacadeDto).collect(Collectors.toList()));
        return storeFacadePage;
    }

    /**
     * StoreDTO转StoreFacadeDTO
     * @param storeDTO StoreDTO
     * @return StoreFacadeDTO
     */
    private StoreFacadeDTO parseStoreDtoToStoreFacadeDto(StoreDTO storeDTO) {
        StoreFacadeDTO storeFacadeDTO = new StoreFacadeDTO();
        storeFacadeDTO.setStoreId(storeDTO.getStoreId());
        storeFacadeDTO.setStoreName(storeDTO.getStoreName());
        storeFacadeDTO.setRegionId(storeDTO.getRegionId().toString());
        return storeFacadeDTO;
    }

    /**
     * StoreDO转StoreFacadeDTO
     * @param storeDO StoreDO
     * @return StoreFacadeDTO
     */
    private StoreFacadeDTO parseStoreDoToStoreFacadeDto(StoreDO storeDO) {
        StoreFacadeDTO storeFacadeDTO = new StoreFacadeDTO();
        storeFacadeDTO.setStoreId(storeDO.getStoreId());
        storeFacadeDTO.setStoreName(storeDO.getStoreName());
        storeFacadeDTO.setRegionId(storeDO.getRegionId().toString());
        storeFacadeDTO.setRegionPath(storeDO.getRegionPath());
        return storeFacadeDTO;
    }
}
