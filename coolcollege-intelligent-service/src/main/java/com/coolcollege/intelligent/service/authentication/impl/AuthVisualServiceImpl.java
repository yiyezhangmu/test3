package com.coolcollege.intelligent.service.authentication.impl;

import static com.coolcollege.intelligent.common.enums.role.AuthRoleEnum.*;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.authentication.UserAuthScopeDTO;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coolcollege.intelligent.common.constant.TwoResultTuple;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.CommonNodeUtils;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.UserRoleDTO;
import com.coolcollege.intelligent.model.enums.UserAuthMappingTypeEnum;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.*;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.system.VO.SysRoleVO;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;


/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/10/14
 */
@Service
public class AuthVisualServiceImpl implements AuthVisualService {

    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private RedisConstantUtil redisConstantUtil;
    @Autowired
    private RedisUtilPool redisUtilPool;
    @Autowired
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;
    @Resource
    private UserRegionMappingDAO userRegionMappingDAO;
    @Resource
    private RegionDao regionDao;


    @Override
    public List<AuthRegionStoreUserDTO> authRegionStore(String eid, String userId) {

        List<UserAuthMappingDO> userAuthMappingList = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userId);
        return getAuthRegionStoreUserDTO(eid, userAuthMappingList);

    }

    @Override
    public List<AuthRegionStoreDTO> authRegionStoreByUserList(String eid, List<String> userIdList) {

        List<UserAuthMappingDO> userAuthMappingList = userAuthMappingMapper.listUserAuthMappingByUserIdList(eid, userIdList);
        List<AuthRegionStoreUserDTO> authRegionStoreUserDTO = getAuthRegionStoreUserDTO(eid, userAuthMappingList);
        Map<String, AuthRegionStoreUserDTO> regionStoreUserMap = ListUtils.emptyIfNull(authRegionStoreUserDTO)
                .stream()
                .collect(Collectors.toMap(AuthRegionStoreUserDTO::getId, data -> data, (a, b) -> a));
        Map<String, List<UserAuthMappingDO>> userAuthGroup = ListUtils.emptyIfNull(userAuthMappingList)
                .stream()
                .collect(Collectors.groupingBy(UserAuthMappingDO::getUserId));
        return userIdList.stream()
                .map(data -> mapAuthRegionStoreDTO(regionStoreUserMap, userAuthGroup, data))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public AuthRegionStoreVisualDTO authRegionStoreVisual(String eid, String userId) {

        // 判断是否是管理员
        Boolean isAdmin = sysRoleService.checkIsAdmin(eid, userId);

        AuthRegionStoreVisualDTO authRegionStoreVisualDTO = new AuthRegionStoreVisualDTO();
        if (isAdmin) {
            authRegionStoreVisualDTO.setIsAllStore(true);
            authRegionStoreVisualDTO.setUserId(userId);
            return authRegionStoreVisualDTO;
        }
        List<String> userAuthRegionIds = userAuthMappingMapper.getMappingUserAuthMappingByUserId(eid, userId);
        boolean isAll = CollectionUtils.isNotEmpty(userAuthRegionIds) && userAuthRegionIds.contains(Constants.ROOT_DEPT_ID_STR);
        if (isAll) {
            authRegionStoreVisualDTO.setIsAllStore(true);
            authRegionStoreVisualDTO.setUserId(userId);
            return authRegionStoreVisualDTO;
        }
        authRegionStoreVisualDTO.setIsAllStore(false);
        authRegionStoreVisualDTO.setUserId(userId);
        List<AuthRegionStoreUserDTO> authRegionStoreUserDTOS = authChildRegion(eid, userId);
        authRegionStoreVisualDTO.setAuthRegionStoreUserList(authRegionStoreUserDTOS);
        return authRegionStoreVisualDTO;
    }

    private AuthRegionStoreDTO mapAuthRegionStoreDTO(Map<String, AuthRegionStoreUserDTO> regionStoreUserMap, Map<String, List<UserAuthMappingDO>> userAuthGroup, String data) {
        if (MapUtils.isNotEmpty(userAuthGroup) && CollectionUtils.isNotEmpty(userAuthGroup.get(data)) && MapUtils.isNotEmpty(regionStoreUserMap)) {
            List<AuthRegionStoreUserDTO> authRegionStoreUserDTOList = ListUtils.emptyIfNull(userAuthGroup.get(data))
                    .stream()
                    .map(userAuthMappingDO -> regionStoreUserMap.get(userAuthMappingDO.getMappingId()))
                    .distinct()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            AuthRegionStoreDTO authRegionStoreDTO = new AuthRegionStoreDTO();
            authRegionStoreDTO.setUserId(data);
            authRegionStoreDTO.setAuthRegionStoreUserList(authRegionStoreUserDTOList);
            return authRegionStoreDTO;
        }
        return null;
    }

    @Override
    public List<AuthRegionStoreUserDTO> getAuthRegionStoreUserDTO(String eid, List<UserAuthMappingDO> userAuthMappingList) {
        List<AuthRegionStoreUserDTO> authRegionStoreDTOList = new ArrayList<>();
        TwoResultTuple<List<String>, List<String>> listListTwoResultTuple = splitUserAuthMapping(userAuthMappingList);
        List<String> storeIdList = listListTwoResultTuple.first;
        List<String> regionIdList = listListTwoResultTuple.second;
        //将组织架构权限中的区域转换
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            List<RegionDO> regionByRegionIds = regionMapper.getRegionByRegionIds(eid, regionIdList);
            List<AuthRegionStoreUserDTO> regionAuthRegionStoreList = ListUtils.emptyIfNull(regionByRegionIds).stream()
                    .map(data -> mapAuthRegionStoreByRegion(data.getName(), data.getRegionId(), false, data.getStoreId(), data.getStoreStatus()))
                    .collect(Collectors.toList());
            List<String> regionStoreIdList = ListUtils.emptyIfNull(regionByRegionIds).stream()
                    .filter(e -> StringUtils.isNotBlank(e.getStoreId()) && RegionTypeEnum.STORE.getType().equals(e.getRegionType()))
                    .map(RegionDO::getStoreId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(regionAuthRegionStoreList)) {
                authRegionStoreDTOList.addAll(regionAuthRegionStoreList);
            }
            // 是否需要展示区域对应门店权限数据
            if (storeIdList != null && CollectionUtils.isNotEmpty(regionStoreIdList)
                    && StringUtils.isNotBlank(redisUtilPool.getString(redisConstantUtil.getShowStoreAuthKey()))) {
                storeIdList.addAll(regionStoreIdList);
            }
        }
        //将组织架构权限中的门店转换
        if (CollectionUtils.isNotEmpty(storeIdList) && StringUtils.isNotBlank(redisUtilPool.getString(redisConstantUtil.getShowStoreAuthKey()))) {

            List<StoreDTO> storeListByStoreIds = storeMapper.getStoreListByStoreIds(eid, storeIdList);
            List<AuthRegionStoreUserDTO> storeAuthRegionStoreList = ListUtils.emptyIfNull(storeListByStoreIds).stream()
                    .map(data -> mapAuthRegionStoreByRegion(data.getStoreName(), data.getStoreId(), true, data.getStoreId(), data.getStoreStatus()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(storeAuthRegionStoreList)) {
                authRegionStoreDTOList.addAll(storeAuthRegionStoreList);
            }
        }
        return authRegionStoreDTOList;
    }

    @Override
    public AuthBaseVisualDTO baseAuth(String eid, String userId) {

        Boolean isAdmin = sysRoleService.checkIsAdmin(eid, userId);
        AuthBaseVisualDTO authBaseVisualDTO =new AuthBaseVisualDTO();
        authBaseVisualDTO.setUserId(userId);
        authBaseVisualDTO.setIsAdmin(false);
        authBaseVisualDTO.setIsAllStore(false);
        if (isAdmin) {
            authBaseVisualDTO.setIsAdmin(true);
            authBaseVisualDTO.setIsAllStore(true);
            return authBaseVisualDTO;
        }
        List<UserAuthMappingDO> userAuthMappingList = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userId);
        TwoResultTuple<List<String>, List<String>> listListTwoResultTuple = splitUserAuthMapping(userAuthMappingList);
        List<String> storeIdList = listListTwoResultTuple.first;
        List<String> regionIdList = listListTwoResultTuple.second;
        authBaseVisualDTO.setStoreIdList(storeIdList);
        authBaseVisualDTO.setRegionIdList(regionIdList);
        //获取权限区域的全路径
        if(CollectionUtils.isNotEmpty(regionIdList)){
            List<Long> idList = regionIdList.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            List<RegionDO> regionDOList = regionMapper.getRegionPathByIds(eid, idList);
            List<String> fullRegionIdList = ListUtils.emptyIfNull(regionDOList)
                    .stream()
                    .map(RegionDO::getFullRegionPath)
                    .collect(Collectors.toList());
            authBaseVisualDTO.setFullRegionPathList(fullRegionIdList);
        }
        return authBaseVisualDTO;
    }

    @Override
    public List<String> getStoreAuthUserIds(String eid, List<String> storeIds) {
        List<StoreAreaDTO> storeAreaList = storeMapper.getStoreAreaList(eid, storeIds);
        if (CollectionUtils.isEmpty(storeAreaList)) {
            return Collections.emptyList();
        }
        List<String> fullAreaIdList = ListUtils.emptyIfNull(storeAreaList)
                .stream()
                .map(StoreAreaDTO::getAreaIdList)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        List<String> userIds = userAuthMappingMapper.getUserIdsByMappingIds(eid, fullAreaIdList);
        if(CollectionUtils.isEmpty(userIds)){
            return Lists.newArrayList();
        }
        return enterpriseUserDao.selectByUserIdsAndStatus(eid, userIds, UserStatusEnum.NORMAL.getCode());
    }

    @Override
    public UserAuthScopeDTO getUserAuthStoreIdsAndUserIds(String eid, String userId) {
        Boolean isAdmin = sysRoleService.checkIsAdmin(eid, userId);
        if(isAdmin){
            return new UserAuthScopeDTO(isAdmin);
        }
        //用户的权限
        List<String> mappingIds = userAuthMappingMapper.getMappingIdsByUserId(eid, userId);
        if(CollectionUtils.isEmpty(mappingIds)){
            return new UserAuthScopeDTO(isAdmin, Lists.newArrayList(), Lists.newArrayList());
        }
        //跟节点权限 视管理员权限
        if(mappingIds.contains(Constants.ROOT_DEPT_ID_STR)){
            return new UserAuthScopeDTO(true);
        }
        //用户的权限下存在的门店
        List<String> regionIdList = regionDao.getSubIdsByRegionIds(eid, mappingIds);
        regionIdList.addAll(mappingIds);
        List<String> storeIds = regionMapper.getStoreIdByRegionIds(eid, regionIdList);
        List<String> userIds = userRegionMappingDAO.getUserIdsByRegionIds(eid, regionIdList);
        return new UserAuthScopeDTO(isAdmin, userIds, storeIds);
    }

    @Override
    public UserAuthScopeDTO getUserAuthStoreIds(String eid, String userId) {
        Boolean isAdmin = sysRoleService.checkIsAdmin(eid, userId);
        if(isAdmin){
            return new UserAuthScopeDTO(true);
        }
        //用户的权限
        List<String> mappingIds = userAuthMappingMapper.getMappingIdsByUserId(eid, userId);
        if(CollectionUtils.isEmpty(mappingIds)){
            return new UserAuthScopeDTO(false, Lists.newArrayList(), Lists.newArrayList());
        }
        //跟节点权限 视管理员权限
        if(mappingIds.contains(Constants.ROOT_DEPT_ID_STR)){
            return new UserAuthScopeDTO(true);
        }
        //用户的权限下存在的门店
        List<String> regionIdList = regionDao.listSubIdsByRegionIds(eid, mappingIds);
        regionIdList.addAll(mappingIds);
        List<String> storeIds = regionMapper.getStoreIdByRegionIds(eid, regionIdList);
        return new UserAuthScopeDTO(false, null, storeIds);
    }

    @Override
    public List<AuthRegionStoreUserDTO> authChildRegion(String eid, String userId) {
        List<RegionDO> allRegion = regionMapper.getAllRegion(eid);
        //全企业数据直接返回所有区域
        Boolean isAdmin = sysRoleService.checkIsAdmin(eid, userId);
        if (isAdmin) {
            return ListUtils.emptyIfNull(allRegion).stream()
                    .map(data -> mapAuthRegionStoreByRegion(data, data.getRegionId()))
                    .collect(Collectors.toList());
        }
        List<UserAuthMappingDO> userAuthMappingDOList = userAuthMappingMapper.listUserAuthMappingByUserAndType(eid,
                userId, UserAuthMappingTypeEnum.REGION.getCode());
        ListUtils.emptyIfNull(allRegion).forEach(this::initRoot);

        Map<String, RegionDO> regionMap = ListUtils.emptyIfNull(allRegion)
                .stream()
                .collect(Collectors.toMap(RegionDO::getRegionId, data -> data, (a, b) -> a));
        List<Long> all = ListUtils.emptyIfNull(allRegion)
                .stream()
                .map(RegionDO::getId)
                .collect(Collectors.toList());
        Map<Long, List<Long>> regionParentGroupMap = ListUtils.emptyIfNull(allRegion)
                .stream()
                .collect(Collectors.groupingBy(data -> Long.valueOf(data.getParentId()),
                        Collectors.mapping(RegionDO::getId, Collectors.toList())));
        List<String> allChildListContainList = new ArrayList<>();

        //是否获取权限区域的子节点
                ListUtils.emptyIfNull(userAuthMappingDOList).forEach(data -> {
                    List<Long> childRegion = CommonNodeUtils.getAllChildListContainSelf(0L,
                            Long.valueOf(data.getMappingId()), all, regionParentGroupMap);
                    if (CollectionUtils.isNotEmpty(childRegion)) {
                        allChildListContainList.addAll(childRegion.stream().map(Object::toString).collect(Collectors.toList()));
                    }
                });
        return ListUtils.emptyIfNull(allChildListContainList)
                .stream().filter(data -> regionMap.get(data) != null)
                .map(data -> mapAuthRegionStoreByRegion(regionMap.get(data), data))
                .collect(Collectors.toList());
    }

    private AuthRegionStoreUserDTO mapAuthRegionStoreByRegion(RegionDO data, String regionId) {
        AuthRegionStoreUserDTO authRegionStoreDTO = new AuthRegionStoreUserDTO();
        authRegionStoreDTO.setId(regionId);
        authRegionStoreDTO.setName(data.getName());
        authRegionStoreDTO.setStoreFlag(false);
        return authRegionStoreDTO;
    }

    private void initRoot(RegionDO regionDO) {
        if (regionDO.getParentId() == null) {
            regionDO.setParentId("0");
        }
    }




    /**
     * 获取区域下的门店(不包含子节点)
     *
     * @param eid
     * @param regionList
     * @return
     */
    private List<StoreDO> getRegionStoreListNotChild(String eid, List<String> regionList) {

        List<StoreDO> allAuthStoreList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(regionList)) {
            regionList.forEach(data -> {
                List<StoreDO> storeDOList = storeMapper.listStoreByRegionIdNotChild(eid, data);
                if (CollectionUtils.isNotEmpty(storeDOList)) {
                    allAuthStoreList.addAll(storeDOList);
                }
            });
        }
        return allAuthStoreList;
    }

    @Override
    public TwoResultTuple<List<String>, List<String>> splitUserAuthMapping(List<UserAuthMappingDO> userAuthMappingList) {

        List<UserAuthMappingDO> store = new ArrayList<>();
        List<UserAuthMappingDO> region = new ArrayList<>();
        ListUtils.emptyIfNull(userAuthMappingList)
                .forEach(data -> {
                    if (data.getType().equals(UserAuthMappingTypeEnum.STORE.getCode())) {
                        store.add(data);
                    } else {
                        region.add(data);
                    }
                });
        List<String> storeIdList = ListUtils.emptyIfNull(store).stream()
            .map(UserAuthMappingDO::getMappingId).distinct().filter(Objects::nonNull).collect(Collectors.toList());
        List<String> regionIdList = ListUtils.emptyIfNull(region).stream()
            .map(UserAuthMappingDO::getMappingId).distinct().filter(Objects::nonNull).collect(Collectors.toList());
        return new TwoResultTuple(storeIdList, regionIdList);
    }

    private List<String> authStoreId(List<String> storeIdList,
                                     List<String> areaStoreList) {

        List<String> allStoreIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(storeIdList)) {
            allStoreIdList.addAll(storeIdList);
        }
        //聚合区域下的门店信息

        if (CollectionUtils.isNotEmpty(areaStoreList)) {
            allStoreIdList.addAll(areaStoreList);
        }


        //去除重复的StoreId
        return ListUtils.emptyIfNull(allStoreIdList).stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public AuthVisualDTO authRegionStoreByRole(String eid, String userId) {

        AuthVisualDTO authVisualDTO = new AuthVisualDTO();
        String username = enterpriseUserDao.selectNameByUserId(eid, userId);
        if (username == null) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "用户不存在");
        }
        authVisualDTO.setUserId(userId);
        authVisualDTO.setUserName(username);
        Boolean isAdmin = sysRoleService.checkIsAdmin(eid, userId);
        if (isAdmin) {
            authVisualDTO.setIsAdmin(true);
            authVisualDTO.setIsAllStore(true);
            return authVisualDTO;
        }
        authVisualDTO.setIsAdmin(false);
        List<UserAuthMappingDO> userAuthMappingList = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userId);
        TwoResultTuple<List<String>, List<String>> listListTwoResultTuple = splitUserAuthMapping(userAuthMappingList);
        List<String> storeIdList = listListTwoResultTuple.first;
        List<String> regionIdList = listListTwoResultTuple.second;
        List<StoreAreaDTO> storeAreaDTOS = new ArrayList<>();
        List<String> areaStoreList = new ArrayList<>();

                if (CollectionUtils.isNotEmpty(regionIdList)) {
                    storeAreaDTOS = storeDao.listStoreByRegionIdList(eid, regionIdList);
                }
                areaStoreList = ListUtils.emptyIfNull(storeAreaDTOS)
                        .stream()
                        .map(StoreAreaDTO::getStoreId)
                        .collect(Collectors.toList());
                List<String> includeSubordinateStoreIdList = authStoreId(storeIdList, areaStoreList);
                authVisualDTO.setRoleAuth(INCLUDE_SUBORDINATE.getCode());
                authVisualDTO.setIsAllStore(false);
                authVisualDTO.setStoreIdList(includeSubordinateStoreIdList);
        return authVisualDTO;
    }

    @Override
    public List<AuthVisualDTO> authRegionStoreByRole(String eid, List<String> userIdList) {
        return ListUtils.emptyIfNull(userIdList).stream().map(a -> authRegionStoreByRole(eid, a)).collect(Collectors.toList());
    }

    @Override
    public AuthVisualDTO authRegionStoreByRegion(String eid, String userId, String storeId, String regionId) {
        if (StringUtils.isBlank(storeId) && StringUtils.isBlank(regionId)) {
            return authRegionStoreByRole(eid, userId);
        }

        AuthVisualDTO authVisualDTO = authRegionStoreByRole(eid, userId);
        if (!authVisualDTO.getIsAllStore() && CollectionUtils.isEmpty(authVisualDTO.getStoreIdList())) {
            return authVisualDTO;
        }
        List<String> storeIdList = getAuthStoreByStoreOrRegion(eid, storeId, regionId);
        if (CollectionUtils.isEmpty(storeIdList)) {
            authVisualDTO.setStoreIdList(Collections.EMPTY_LIST);
            return authVisualDTO;
        }
        //过滤页面传入查询范围和权限范围
        if (authVisualDTO.getIsAllStore()) {
            authVisualDTO.setStoreIdList(storeIdList);
        } else {
            List<String> filterStoreIdList = storeIdList.stream()
                    .filter(data -> authVisualDTO.getStoreIdList().contains(data))
                    .collect(Collectors.toList());
            authVisualDTO.setStoreIdList(filterStoreIdList);
        }

        return authVisualDTO;
    }

    @Override
    public AuthVisualDTO authRegionStoreByStore(String eid, String userId, List<String> storeIdList) {
        AuthVisualDTO authVisualDTO = authRegionStoreByRole(eid, userId);
        if (CollectionUtils.isEmpty(storeIdList) && !authVisualDTO.getIsAllStore()) {
            authVisualDTO.setStoreIdList(Collections.EMPTY_LIST);
            return authVisualDTO;
        }
        //过滤页面传入查询范围和权限范围
        if (authVisualDTO.getIsAllStore()) {
            authVisualDTO.setStoreIdList(storeIdList);
        } else {
            List<String> filterStoreIdList = storeIdList.stream()
                    .filter(data -> authVisualDTO.getStoreIdList().contains(data))
                    .collect(Collectors.toList());
            authVisualDTO.setStoreIdList(filterStoreIdList);
        }

        return authVisualDTO;
    }

    private List<String> getAuthStoreByStoreOrRegion(String eid, String storeId, String regionId) {
        List<String> storeIdList = new ArrayList<>();
        if (StringUtils.isNotBlank(storeId)) {
            storeIdList.add(storeId);
        }
        if (StringUtils.isNotBlank(regionId)) {
            List<StoreDO> storeList = storeMapper.listStoreByRegionId(eid, regionId);
            storeIdList = ListUtils.emptyIfNull(storeList)
                    .stream()
                    .map(StoreDO::getStoreId)
                    .collect(Collectors.toList());
        }
        return storeIdList;
    }



    @Override
    public List<AuthStoreUserDTO> authStoreUser(String eid, List<String> storeIdList, String positionType) {
        List<AuthStoreUserDTO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(storeIdList)) {
            return result;
        }
        //将拥有管理员角色、角色属性为全企业数据的人查询出来
        List<SysRoleVO> roleUserByRoleId = sysRoleMapper.getRoleUserByRoleEnum(eid, Role.MASTER.getRoleEnum(),positionType);
        List<SysRoleVO> roleUserByRoleAuth = sysRoleMapper.getRoleUserByRoleAuth(eid, ALL.getCode(),positionType);
        //组合出拥有所有门店信息的人
        List<String>  allStoreUserIdList = getAllStoreUserIdList(roleUserByRoleId, roleUserByRoleAuth);
        //查询出有门店权限配置的的人员
        //   1.将门店区域切分出门店所属于的区域ID
        //   2.将配置了区域的人 查询出来
        //   3.将配置了门店的人 查询出来
        List<StoreAreaDTO> storeAreaList = storeMapper.getStoreAreaList(eid, storeIdList);
        if (CollectionUtils.isEmpty(storeAreaList)) {
            return Collections.emptyList();
        }

        List<String> fullAreaIdList = ListUtils.emptyIfNull(storeAreaList)
                .stream()
                .map(StoreAreaDTO::getAreaIdList)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());


        List<String> lastAreaIdList = ListUtils.emptyIfNull(storeAreaList)
                .stream()
                .map(StoreAreaDTO::getAreaId)
                .distinct()
                .collect(Collectors.toList());

        //除不包含子区域的可视化范围的区域配置。
        List<UserAuthMappingDO> regionUserAuthMappingList = userAuthMappingMapper.listUserAuthMappingByAuth(eid,
                UserAuthMappingTypeEnum.REGION.getCode(), fullAreaIdList, positionType, NOT_INCLUDE_SUBORDINATE.getCode());
        //不包含子区域的的直属连接门店的区域下的配置(会重复一些选择了上面数据)
        List<UserAuthMappingDO> notIncludeRegionUserAuthMappingList = userAuthMappingMapper.listUserAuthMappingByAuth(eid,
                UserAuthMappingTypeEnum.REGION.getCode(), lastAreaIdList, positionType, null);
        //配置了门店的的配置
        List<UserAuthMappingDO> storeUserAuthMappingList = userAuthMappingMapper.listUserAuthMappingByMappingList(eid,
                storeIdList, UserAuthMappingTypeEnum.STORE.getCode());
        return ListUtils.emptyIfNull(storeAreaList)
                .stream()
                .map(data -> mapAuthStoreUserDTO(regionUserAuthMappingList, allStoreUserIdList, storeUserAuthMappingList, notIncludeRegionUserAuthMappingList, data))
                .collect(Collectors.toList());
    }

    @Override
    public List<AuthStoreCountDTO> authStoreCount(String eid, List<String> userIdList, Boolean isReturnList) {
        /**
         * 1.查询出所有关于用户的权限门店
         *     1.权限区域角色权限
         *     2.权限门店
         * 2.分组聚合
         * 3.去重统计门店数
         */
        //用户配置的区域权限
        List<UserAuthMappingDO> userAuthMappingDOList = userAuthMappingMapper.listUserAuthMappingByUserList(eid, userIdList);

        List<String> allAuthRegionList = ListUtils.emptyIfNull(userAuthMappingDOList)
                .stream()
                .filter(data -> StringUtils.equals(UserAuthMappingTypeEnum.REGION.getCode(), data.getType()))
                .map(UserAuthMappingDO::getMappingId)
                .distinct()
            .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<StoreAreaDTO> storeAreaDTOS = storeDao.listStoreByRegionIdList(eid, allAuthRegionList);
        Map<String, List<String>> storeAreaMap = ListUtils.emptyIfNull(storeAreaDTOS)
                .stream()
                .collect(Collectors.groupingBy(StoreAreaDTO::getAreaId,
                        Collectors.mapping(StoreAreaDTO::getStoreId, Collectors.toList())));
        Map<String, List<UserAuthMappingDO>> authMappingMap = ListUtils.emptyIfNull(userAuthMappingDOList)
                .stream()
                .collect(Collectors.groupingBy(UserAuthMappingDO::getUserId));
        //用户角色可视化范围
        List<UserRoleDTO> userRoleList = sysRoleMapper.userAndRolesByUserId(eid, userIdList);
        //取优先级最大角色去查询权限
            //todo role
        Map<String, UserRoleDTO> userRoleMap = ListUtils.emptyIfNull(userRoleList)
                .stream()
                .collect(Collectors.toMap(UserRoleDTO::getUserId, data -> data, (a, b) -> {
                    if (a.getPriority() == null || b.getPriority() == null) {
                        return a;
                    }
                    return a.getPriority() > b.getPriority() ? b : a;
                }));
        List<UserRoleDTO> minUserRoleList = new ArrayList<UserRoleDTO>(userRoleMap.values());
        //所有门店
        List<String> allStoreList = storeDao.getAllStoreList(eid, isReturnList);
        Integer allStoreCount = storeMapper.countStore(eid);

        //子区域计算
        List<Long> all = null;
        Map<Long, List<Long>> regionParentGroupMap = null;
        Map<String, String> regionIdStoreIdMap = null;
        if (CollectionUtils.isNotEmpty(allAuthRegionList)) {
            List<RegionDO> allRegion = regionMapper.getAllRegion(eid);
            ListUtils.emptyIfNull(allRegion).forEach(this::initRoot);
            all = ListUtils.emptyIfNull(allRegion)
                    .stream()
                    .map(RegionDO::getId)
                    .collect(Collectors.toList());
            regionParentGroupMap = ListUtils.emptyIfNull(allRegion)
                    .stream()
                    .collect(Collectors.groupingBy(data -> Long.valueOf(data.getParentId()),
                            Collectors.mapping(RegionDO::getId, Collectors.toList())));

            List<Long> storeRegionIdList = allAuthRegionList.stream()
                    .map(e -> Long.valueOf(e)).collect(Collectors.toList());
            List<RegionDO> storeRegionList = regionMapper.listStoreRegionByIds(eid, storeRegionIdList);
            regionIdStoreIdMap = ListUtils.emptyIfNull(storeRegionList).stream()
                    .filter(e -> StringUtils.isNotBlank(e.getStoreId()) && !e.getDeleted() && RegionTypeEnum.STORE.getType().equals(e.getRegionType()))
                    .collect(Collectors.toMap(data->String.valueOf(data.getId()), RegionDO::getStoreId, (a, b) -> a));
        }

        List<Long> finalAll = all;
        Map<Long, List<Long>> finalRegionParentGroupMap = regionParentGroupMap;
        Map<String, String> finalRegionIdStoreIdMap = regionIdStoreIdMap;
        return ListUtils.emptyIfNull(minUserRoleList).stream()
                .map(data -> mapAuStoreCountDTO(data, authMappingMap, storeAreaMap,
                        allStoreList, allStoreCount, finalAll, finalRegionParentGroupMap, finalRegionIdStoreIdMap))
                .collect(Collectors.toList());
    }


    private AuthStoreCountDTO mapAuStoreCountDTO(UserRoleDTO userRoleDTO,
                                                 Map<String, List<UserAuthMappingDO>> authMappingMap,
                                                 Map<String, List<String>> storeAreaMap,
                                                 List<String> invalidStores,
                                                 Integer allStoreCount,
                                                 List<Long> all,
                                                 Map<Long, List<Long>> regionParentGroupMap, Map<String, String> regionIdStoreIdMap) {
        AuthStoreCountDTO authStoreCountDTO = new AuthStoreCountDTO();
        String userId = userRoleDTO.getUserId();
        authStoreCountDTO.setUserId(userId);
        //全企业数据或者管理员 直接返回企业下所有的门店总数
        if (StringUtils.equals(userRoleDTO.getRoleEnum(), (Role.MASTER.getRoleEnum()))) {
            authStoreCountDTO.setStoreList(invalidStores);
            authStoreCountDTO.setStoreCount(allStoreCount);
            return authStoreCountDTO;
        }
        List<String> storeIdList = new ArrayList<>();
        List<String> regionIdList = new ArrayList<>();
        List<String> areaStoreList;

        if (MapUtils.isNotEmpty(authMappingMap)) {
            List<UserAuthMappingDO> userAuthMappingDOList = authMappingMap.get(userId);
            TwoResultTuple<List<String>, List<String>> listListTwoResultTuple = splitUserAuthMapping(userAuthMappingDOList);
            storeIdList = listListTwoResultTuple.first;
            regionIdList = listListTwoResultTuple.second;
            if(CollectionUtils.isNotEmpty(regionIdList) && regionIdStoreIdMap != null){
                List<String> finalStoreIdList = storeIdList;
                regionIdList.forEach(regionId -> {
                    String regionStoreId = regionIdStoreIdMap.get(regionId);
                    if (StringUtils.isNotBlank(regionStoreId)) {
                        finalStoreIdList.add(regionStoreId);
                    }
                });
            }
        }

        if (userRoleDTO == null || userRoleDTO.getRoleAuth() == null) {
            return authStoreCountDTO;
        }
        switch (AuthRoleEnum.getByCode(userRoleDTO.getRoleAuth())) {
            case ALL:
                authStoreCountDTO.setStoreList(invalidStores);
                authStoreCountDTO.setStoreCount(allStoreCount);
                break;
            case INCLUDE_SUBORDINATE:

                areaStoreList = ListUtils.emptyIfNull(regionIdList)
                        .stream()
                        .map(data -> CommonNodeUtils.getAllChildListContainSelf(0L,
                                Long.valueOf(data), all, regionParentGroupMap))
                        .flatMap(Collection::stream)
                        .map(data -> data.toString())
                        .map(data -> {
                            if (MapUtils.isNotEmpty(storeAreaMap)) {
                                return storeAreaMap.get(data);
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
                List<String> includeSubordinateStoreIdList = authStoreId(storeIdList, areaStoreList);
                if (CollectionUtils.isNotEmpty(includeSubordinateStoreIdList)) {
                    authStoreCountDTO.setStoreList(includeSubordinateStoreIdList);
                    authStoreCountDTO.setStoreCount(ListUtils.emptyIfNull(includeSubordinateStoreIdList).size());

                }
                break;
            case PERSONAL:
                areaStoreList = ListUtils.emptyIfNull(regionIdList)
                        .stream()
                        .map(data -> CommonNodeUtils.getAllChildListContainSelf(0L,
                                Long.valueOf(data), all, regionParentGroupMap))
                        .flatMap(Collection::stream)
                        .map(data -> data.toString())
                        .map(data -> {
                            if (MapUtils.isNotEmpty(storeAreaMap)) {
                                return storeAreaMap.get(data);
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
                List<String> personalStoreIdList = authStoreId(storeIdList, areaStoreList);
                if (CollectionUtils.isNotEmpty(personalStoreIdList)) {
                    authStoreCountDTO.setStoreList(personalStoreIdList);
                    authStoreCountDTO.setStoreCount(ListUtils.emptyIfNull(personalStoreIdList).size());
                }
                break;

            default:
                break;
        }
        return authStoreCountDTO;

    }

    private List<String> getAllStoreUserIdList(List<SysRoleVO> roleUserByRoleId, List<SysRoleVO> roleUserByRoleAuth) {
        List<String> allUserIdList= new ArrayList<>();
        List<String> masterUserList = ListUtils.emptyIfNull(roleUserByRoleId).stream()
                .map(SysRoleVO::getEnterpriseDOs)
                .flatMap(Collection::stream)
                .map(EnterpriseUserDO::getUserId)
                .collect(Collectors.toList());
        List<String> roleAllUserList = ListUtils.emptyIfNull(roleUserByRoleAuth).stream()
                .map(SysRoleVO::getEnterpriseDOs)
                .flatMap(Collection::stream)
                .map(EnterpriseUserDO::getUserId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(masterUserList)) {
            allUserIdList.addAll(masterUserList);
        }
        if (CollectionUtils.isNotEmpty(roleAllUserList)) {
            allUserIdList.addAll(roleAllUserList);
        }
       return ListUtils.emptyIfNull(allUserIdList)
                .stream()
                .distinct()
                .collect(Collectors.toList());

    }

    private AuthStoreUserDTO mapAuthStoreUserDTO(
                                                 List<UserAuthMappingDO> regionUserAuthMappingList,
                                                 List<String> allStoreUserIdList,
                                                 List<UserAuthMappingDO> storeUserAuthMappingList,
                                                 List<UserAuthMappingDO> notIncludeRegionUserAuthMappingList,
                                                 StoreAreaDTO data) {

        //组装全部的人员信息
        List<String> authStoreIdList = new ArrayList<>();
        AuthStoreUserDTO authStoreUserDTO = new AuthStoreUserDTO();
        authStoreUserDTO.setStoreId(data.getStoreId());
        authStoreUserDTO.setStoreName(data.getStoreName());
        //组装拥有所有门店信息的人
        if (CollectionUtils.isNotEmpty(allStoreUserIdList)) {
            authStoreIdList.addAll(allStoreUserIdList);
        }
        //组装配置了区域权限的人(除了不包含子区域可视化范围)
        List<String> storeAreaIdList = data.getAreaIdList();
        List<String> regionUserIdList = ListUtils.emptyIfNull(regionUserAuthMappingList).stream()
                .filter(area -> storeAreaIdList.contains(area.getMappingId()))
                .map(UserAuthMappingDO::getUserId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(regionUserIdList)) {
            authStoreIdList.addAll(regionUserIdList);
        }
        //组装配置了区域权限的人(不包含子区域可视化范围)
        List<String> notIncludeRegionUserIdList = ListUtils.emptyIfNull(notIncludeRegionUserAuthMappingList).stream()
                .filter(area -> StringUtils.equals(data.getAreaId(), area.getMappingId()))
                .map(UserAuthMappingDO::getUserId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(notIncludeRegionUserIdList)) {
            authStoreIdList.addAll(notIncludeRegionUserIdList);
        }
        //组装配置了门店权限的人
        List<String> storeUserIdList = ListUtils.emptyIfNull(storeUserAuthMappingList).stream()
                .filter(store -> StringUtils.equals(store.getMappingId(), data.getStoreId()))
                .map(UserAuthMappingDO::getUserId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(storeUserIdList)) {
            authStoreIdList.addAll(storeUserIdList);
        }

        //去除重复数据
        List<String> distinctUserIdList = ListUtils.emptyIfNull(authStoreIdList).stream()
                .distinct()
                .collect(Collectors.toList());
        authStoreUserDTO.setUserIdList(distinctUserIdList);
        return authStoreUserDTO;
    }



    private AuthRegionStoreUserDTO mapAuthRegionStoreByRegion(String name, String regionId, boolean b, String storeId, String storeStatus) {

        AuthRegionStoreUserDTO authRegionStoreDTO = new AuthRegionStoreUserDTO();
        authRegionStoreDTO.setName(name);
        authRegionStoreDTO.setId(regionId);
        authRegionStoreDTO.setStoreFlag(b);
        authRegionStoreDTO.setStoreId(storeId);
        authRegionStoreDTO.setStoreStatus(storeStatus);
        return authRegionStoreDTO;
    }


}
