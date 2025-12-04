package com.coolcollege.intelligent.service.selectcomponent.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.enums.role.CoolPositionTypeEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserDepartmentMapper;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.department.DeptNode;
import com.coolcollege.intelligent.model.department.dto.DeptChildDTO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.dto.SelectUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.SelectUserInfoDTO;
import com.coolcollege.intelligent.model.enums.UserAuthMappingTypeEnum;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.*;
import com.coolcollege.intelligent.model.selectcomponent.*;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.system.dto.RoleDTO;
import com.coolcollege.intelligent.model.system.dto.RoleUserDTO;
import com.coolcollege.intelligent.model.system.dto.UserDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.SubordinateMappingService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.recent.LRUService;
import com.coolcollege.intelligent.service.selectcomponent.SelectionComponentService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: xuanfeng
 * @date: 2021-10-27 14:45
 */
@Slf4j
@Service
public class SelectionComponentServiceImpl implements SelectionComponentService {

    @Autowired
    private EnterpriseUserDao enterpriseUserDao;
    @Autowired
    private LRUService lruService;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private AuthVisualService visualService;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;
    @Autowired
    private StoreMapper storeMapper;
    @Autowired
    private RegionMapper regionMapper;
    @Resource
    public SysDepartmentMapper sysDepartmentMapper;
    @Autowired
    private EnterpriseUserDepartmentMapper enterpriseUserDepartmentMapper;
    @Autowired
    private RedisUtilPool redis;
    @Resource
    private DeviceMapper deviceMapper;
    @Autowired
    private EnterpriseConfigService enterpriseConfigService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private SubordinateMappingService subordinateMappingService;
    @Autowired
    private SysRoleService sysRoleService;

    @Override
    public PageVO<SelectComponentUserVO> getSelectionUserByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize, Boolean active, String currentUserId) {
        return getSelectionUserByKeyword(eid, keyword, pageNum, pageSize, active, currentUserId, true);
    }

    @Override
    public PageVO<SelectComponentUserVO> getSelectionUserByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize, Boolean active, String currentUserId, Boolean hasAuth) {
        if (StringUtils.isBlank(keyword) || StringUtils.isBlank(eid)) {
            return new PageVO<>();
        }
        String userId = UserHolder.getUser().getUserId();
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(eid);
        DataSourceHelper.changeToMy();
        List<EnterpriseUserDO> userDOS = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        //搜索用户的信息
        if (AppTypeEnum.isQwType(config.getAppType())) {
            List<String> userIds = chatService.
                    searchUserOrDeptByName(config.getDingCorpId(), config.getAppType(), keyword, Constants.ONE_VALUE_STRING, pageNum, pageSize).getKey();
            if (CollectionUtils.isEmpty(userIds)) {
                return new PageVO<>();
            }
            userDOS = enterpriseUserDao.selectUserByKeyword(eid, null, UserStatusEnum.NORMAL.getCode(), userIds, active);
        } else {
            userDOS = enterpriseUserDao.selectUserByKeyword(eid, keyword, UserStatusEnum.NORMAL.getCode(), null, active);
        }

        if (CollectionUtils.isEmpty(userDOS)) {
            return new PageVO<>();
        }
        List<String> userIds = userDOS.stream()
                .map(EnterpriseUserDO::getUserId)
                .collect(Collectors.toList());
        //获取用户的岗位
        Map<String, List<SelectComponentUserRoleVO>> userRoles = getUserRoles(eid, userIds);
        //获取用户的门店 和 区域 使用存在的接口
        Map<String, AuthRegionStoreDTO> authRegionStoreMap = getAuthRegionStoreMap(eid, userIds);
        //构造返回参数
        List<SelectComponentUserVO> selectComponentUserVos = fetchSelectComponentUserVo(userDOS, userRoles, authRegionStoreMap);
        Boolean haveAllSubordinateUser = subordinateMappingService.checkHaveAllSubordinateUser(eid, currentUserId);
        //【2025-05-26修改：是否获取所有用户，增加hasAuth字段，默认为true-即开启权限，默认使用权限】
        if (!hasAuth){
            haveAllSubordinateUser = true;
        }

        List<String> userSubordinateList = Lists.newArrayList();
        if (!haveAllSubordinateUser) {
            userSubordinateList = subordinateMappingService.getSubordinateUserIdList(eid, currentUserId, Boolean.TRUE);
        }
        List<String> finalUserSubordinateList = userSubordinateList;
        Boolean finalHaveAllSubordinateUser = haveAllSubordinateUser;
        selectComponentUserVos.forEach(f -> {
            if (finalHaveAllSubordinateUser) {
                f.setSelectFlag(true);
            } else {
                f.setSelectFlag(finalUserSubordinateList.contains(f.getUserId()));
            }
        });

        PageInfo<EnterpriseUserDO> dos = new PageInfo<>(userDOS);
        //分页结果集替换
        PageVO<SelectComponentUserVO> results = new PageVO<>();
        results.setPageNum(dos.getPageNum());
        results.setPageSize(dos.getPageSize());
        results.setPage_num(dos.getPageNum());
        results.setPage_size(dos.getPageSize());
        results.setTotal(dos.getTotal());
        results.setList(selectComponentUserVos);
        // 搜素时添加缓存
        if (StrUtil.isNotBlank(keyword)) {
            lruService.putRecentUseUser(eid, userId, userIds);
        }
        return results;
    }

    @Override
    public PageVO<SelectComponentPositionVO> getSelectionPositionByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize) {
        if (StringUtils.isBlank(keyword) || StringUtils.isBlank(eid)) {
            return new PageVO<>();
        }
        //分页查询岗位
        PageHelper.startPage(pageNum, pageSize);
        List<RoleDTO> sysRoleDos = sysRoleMapper.fuzzyRole(eid, keyword, null);
        if (CollectionUtils.isEmpty(sysRoleDos)) {
            return new PageVO<>();
        }
        //统计岗位下的人数
        List<Long> roleIds = ListUtils.emptyIfNull(sysRoleDos)
                .stream()
                .map(RoleDTO::getId)
                .collect(Collectors.toList());
        List<RoleUserDTO> roleUserDTOList = sysRoleMapper.selectRoleUserByRoleIds(eid, roleIds);
        Map<Long, Integer> userCountMap = ListUtils.emptyIfNull(roleUserDTOList).stream()
                .filter(a -> a.getRoleId() != null && a.getUserCount() != null)
                .collect(Collectors.toMap(RoleUserDTO::getRoleId, RoleUserDTO::getUserCount, (a, b) -> a));
        //构造返回的结果
        List<SelectComponentPositionVO> positionVOS = ListUtils.emptyIfNull(sysRoleDos).stream()
                .map(data -> {
                    SelectComponentPositionVO positionVO = new SelectComponentPositionVO();
                    positionVO.setName(data.getRoleName());
                    positionVO.setId(data.getId());
                    positionVO.setSource(data.getSource());
                    positionVO.setPositionId(data.getId().toString());
                    Integer userCount = userCountMap.get(data.getId());
                    if (Objects.isNull(userCount)) {
                        positionVO.setUserCount(0);
                    } else {
                        positionVO.setUserCount(userCount);
                    }
                    return positionVO;
                }).collect(Collectors.toList());
        PageInfo<RoleDTO> dos = new PageInfo<>(sysRoleDos);
        //分页结果集替换  解决直接用vos PageInfo 分页的参数total不对
        PageVO<SelectComponentPositionVO> results = new PageVO<>();
        results.setPageNum(dos.getPageNum());
        results.setPageSize(dos.getPageSize());
        results.setPage_num(dos.getPageNum());
        results.setPage_size(dos.getPageSize());
        results.setTotal(dos.getTotal());
        results.setList(positionVOS);
        return results;
    }

    @Override
    public PageVO<SelectComponentStoreVO> getSelectionStoreByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize, List<String> storeStatusList) {
        if (StringUtils.isBlank(keyword) || StringUtils.isBlank(eid)) {
            return new PageVO<>();
        }
        String userId = UserHolder.getUser().getUserId();
        //用户的权限下的门店
        AuthBaseVisualDTO baseAuth = visualService.baseAuth(eid, userId);
        List<String> authStoreIdList = baseAuth.getStoreIdList();
        List<String> authFullRegionPathList = baseAuth.getFullRegionPathList();
        if (!baseAuth.getIsAllStore() && CollectionUtils.isEmpty(authStoreIdList) && CollectionUtils.isEmpty(authFullRegionPathList)) {
            return new PageVO<>();
        }
        //分页模糊查询获取门店信息
        PageHelper.startPage(pageNum, pageSize);
        List<StoreDO> storeDOS = storeMapper.getAuthStoreByName(eid, keyword, baseAuth.getIsAdmin(),
                authStoreIdList, authFullRegionPathList, storeStatusList);
        if (CollectionUtils.isEmpty(storeDOS)) {
            return new PageVO<>();
        }
        //获取区域
        List<RegionDO> regionDOS = getRegionsByStores(storeDOS, eid);
        //获取门店的人员数
        List<String> storeIds = storeDOS.stream()
                .map(StoreDO::getStoreId)
                .distinct()
                .collect(Collectors.toList());
        Map<String, Integer> storeUsers = getStoreUsers(storeIds, eid);
        List<SelectComponentStoreVO> vos = fetchSelectComponentStoreVO(storeDOS, regionDOS, storeUsers);
        PageInfo<StoreDO> dos = new PageInfo<>(storeDOS);
        //分页结果集替换  解决直接用vos PageInfo 分页的参数total不对
        PageVO<SelectComponentStoreVO> results = new PageVO<>();
        results.setPageSize(dos.getPageSize());
        results.setPageNum(dos.getPageNum());
        results.setPage_num(dos.getPageNum());
        results.setPage_size(dos.getPageSize());
        results.setTotal(dos.getTotal());
        results.setList(vos);
        return results;
    }

    private List<RegionDO> getRegionsByStores(List<StoreDO> storeDOS, String eid) {
        List<String> regionPaths = storeDOS.stream()
                .map(StoreDO::getRegionPath)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<String> regionIds = getRegionIdsByRegionPath(regionPaths);
        //获取区域
        if (CollectionUtils.isNotEmpty(regionIds)) {
            return regionMapper.getRegionByRegionIds(eid, regionIds);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<RegionDO> getRegionsByRegionPath(List<RegionDO> regionDOS, String eid) {
        //解析regionPath
        List<String> regionPaths = ListUtils.emptyIfNull(regionDOS)
                .stream()
                .map(RegionDO::getRegionPath)
                .collect(Collectors.toList());
        //获取所有regionPath的区域信息
        List<String> regionIds = getRegionIdsByRegionPath(regionPaths);
        //获取区域
        if (CollectionUtils.isNotEmpty(regionIds)) {
            return regionMapper.getRegionByRegionIds(eid, regionIds);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public PageVO<SelectComponentDepartmentVO> getSelectionDepartmentByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize) {
        if (StringUtils.isBlank(keyword) || StringUtils.isBlank(eid)) {
            return new PageVO<>();
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(eid);
        DataSourceHelper.changeToMy();
        List<DeptNode> deptNodes = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        if (AppTypeEnum.isQwType(config.getAppType())) {
            List<Long> deptIds = chatService.
                    searchUserOrDeptByName(config.getDingCorpId(), config.getAppType(), keyword, Constants.TWO_VALUE_STRING, pageNum, pageSize).getValue();
            if (CollectionUtils.isEmpty(deptIds)) {
                return new PageVO<>();
            }
            deptNodes = sysDepartmentMapper.getDepListByDepName(eid, null, deptIds);
        } else {
            deptNodes = sysDepartmentMapper.getDepListByDepName(eid, keyword, null);
        }

        if (CollectionUtils.isEmpty(deptNodes)) {
            return new PageVO<>();
        }
        List<String> deptIds = deptNodes.stream()
                .map(DeptNode::getId)
                .collect(Collectors.toList());
        //统计部门人数
        List<EnterpriseUserDepartmentDO> userDepartments = enterpriseUserDepartmentMapper.getUserDepartments(eid, deptIds);
        Map<String, List<EnterpriseUserDepartmentDO>> departUsersMap = ListUtils.emptyIfNull(userDepartments)
                .stream()
                .collect(Collectors.groupingBy(EnterpriseUserDepartmentDO::getDepartmentId));
        List<SelectComponentDepartmentVO> vos = new ArrayList<>();
        for (DeptNode deptNode : deptNodes) {
            SelectComponentDepartmentVO vo = new SelectComponentDepartmentVO();
            vo.setDepartmentId(deptNode.getId());
            vo.setName(deptNode.getDepartmentName());
            vo.setUserCount(CollectionUtils.isEmpty(departUsersMap.get(Long.valueOf(deptNode.getId()))) ? 0 : departUsersMap.get(Long.valueOf(deptNode.getId())).size());
            List<DepartmentInfoVO> departmentInfoVOS = new ArrayList<>();
            departmentInfoVOS = getParentDepartmentRecursion(eid, deptNode.getParentId(), departmentInfoVOS);
            //倒置数据，使关系链正向
            Collections.reverse(departmentInfoVOS);
            vo.setDepartmentInfos(departmentInfoVOS);
            vos.add(vo);
        }
        PageInfo<DeptNode> dos = new PageInfo<>(deptNodes);
        //分页结果集替换  解决直接用vos PageInfo 分页的参数total不对
        PageVO<SelectComponentDepartmentVO> results = new PageVO<>();
        results.setPage_num(dos.getPageNum());
        results.setPageSize(dos.getPageSize());
        results.setPageNum(dos.getPageNum());
        results.setPage_size(dos.getPageSize());
        results.setTotal(dos.getTotal());
        results.setList(vos);
        return results;
    }

    @Override
    public List<DeptChildDTO> supplementDeptUserQueryResult(String eid, List<DeptChildDTO> deptUserList) {
        List<String> userIds = ListUtils.emptyIfNull(deptUserList)
                .stream()
                .filter(DeptChildDTO::getUserFlag)
                .map(DeptChildDTO::getId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIds)) {
            return deptUserList;
        }
        //获取用户的岗位
        Map<String, List<SelectComponentUserRoleVO>> userRoles = getUserRoles(eid, userIds);
        //获取用户的门店 和 区域 使用存在的接口
        Map<String, AuthRegionStoreDTO> authRegionStoreMap = getAuthRegionStoreMap(eid, userIds);
        //拆分门店和区域
        Map<String, List<SelectComponentRegionVO>> regionVOSMap = new HashMap<>(16);
        Map<String, List<SelectComponentStoreVO>> storeVOSMap = new HashMap<>(16);
        analysisStoreAndRegion(authRegionStoreMap, regionVOSMap, storeVOSMap);
        for (DeptChildDTO dto : deptUserList) {
            if (CollectionUtils.isNotEmpty(userRoles.get(dto.getId()))) {
                dto.setPositionInfo(userRoles.get(dto.getId()).get(0));
            }
            dto.setRegionVos(regionVOSMap.get(dto.getId()));
            dto.setStoreVos(storeVOSMap.get(dto.getId()));
        }
        return deptUserList;
    }

    @Override
    public List<SelectUserDTO> supplementRecentUserQueryResult(String eid, List<SelectUserDTO> selectUserDTOS) {
        List<String> userIds = ListUtils.emptyIfNull(selectUserDTOS)
                .stream()
                .map(SelectUserDTO::getUserId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIds)) {
            return selectUserDTOS;
        }
        //获取用户的岗位
        Map<String, List<SelectComponentUserRoleVO>> userRoles = getUserRoles(eid, userIds);
        //获取用户的门店 和 区域 使用存在的接口
        Map<String, AuthRegionStoreDTO> authRegionStoreMap = getAuthRegionStoreMap(eid, userIds);
        //拆分门店和区域
        Map<String, List<SelectComponentRegionVO>> regionVOSMap = new HashMap<>(16);
        Map<String, List<SelectComponentStoreVO>> storeVOSMap = new HashMap<>(16);
        analysisStoreAndRegion(authRegionStoreMap, regionVOSMap, storeVOSMap);
        for (SelectUserDTO dto : selectUserDTOS) {
            if (CollectionUtils.isNotEmpty(userRoles.get(dto.getUserId()))) {
                dto.setPositionInfo(userRoles.get(dto.getUserId()).get(0));
            }
            dto.setRegionVos(regionVOSMap.get(dto.getUserId()));
            dto.setStoreVos(storeVOSMap.get(dto.getUserId()));
        }
        return selectUserDTOS;
    }

    @Override
    public SelectUserInfoDTO supplementClickUserQueryResult(String eid, SelectUserInfoDTO selectUserInfoDTO) {
        if (Objects.isNull(selectUserInfoDTO) || StringUtils.isBlank(selectUserInfoDTO.getUserId())) {
            return selectUserInfoDTO;
        }
        String userId = selectUserInfoDTO.getUserId();
        //获取用户的门店 和 区域 使用存在的接口
        Map<String, AuthRegionStoreDTO> authRegionStoreMap = getAuthRegionStoreMap(eid, Arrays.asList(userId));
        //拆分门店和区域
        Map<String, List<SelectComponentRegionVO>> regionVOSMap = new HashMap<>(16);
        Map<String, List<SelectComponentStoreVO>> storeVOSMap = new HashMap<>(16);
        analysisStoreAndRegion(authRegionStoreMap, regionVOSMap, storeVOSMap);
        selectUserInfoDTO.setRegionVos(regionVOSMap.get(userId));
        selectUserInfoDTO.setStoreVos(storeVOSMap.get(userId));
        return selectUserInfoDTO;
    }

    @Override
    public List<UserDTO> supplementSelectRoleUserQueryResult(String eid, List<UserDTO> userDTOS) {
        List<String> userIds = ListUtils.emptyIfNull(userDTOS)
                .stream()
                .map(UserDTO::getUserId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIds)) {
            return userDTOS;
        }
        //获取用户的岗位
        Map<String, List<SelectComponentUserRoleVO>> userRoles = getUserRoles(eid, userIds);
        //获取用户的门店 和 区域 使用存在的接口
        Map<String, AuthRegionStoreDTO> authRegionStoreMap = getAuthRegionStoreMap(eid, userIds);
        //拆分门店和区域
        Map<String, List<SelectComponentRegionVO>> regionVOSMap = new HashMap<>(16);
        Map<String, List<SelectComponentStoreVO>> storeVOSMap = new HashMap<>(16);
        analysisStoreAndRegion(authRegionStoreMap, regionVOSMap, storeVOSMap);
        for (UserDTO dto : userDTOS) {
            if (CollectionUtils.isNotEmpty(userRoles.get(dto.getUserId()))) {
                dto.setPositionInfo(userRoles.get(dto.getUserId()).get(0));
            }
            dto.setRegionVos(regionVOSMap.get(dto.getUserId()));
            dto.setStoreVos(storeVOSMap.get(dto.getUserId()));
            dto.setAvatar(StrUtil.isBlank(dto.getFaceUrl()) ? dto.getAvatar() : dto.getFaceUrl());
        }
        return userDTOS;
    }

    @Override
    public List<SelectComponentUserVO> getSelectUserByStoreId(String eid, String storeId, Boolean active) {
        List<SelectComponentUserVO> results = new ArrayList<>();
        //获取门店下权限的用户
        List<AuthStoreUserDTO> authStoreUserDTOList = visualService.authStoreUser(eid,
                Collections.singletonList(storeId), CoolPositionTypeEnum.STORE_INSIDE.getCode());
        if (CollectionUtils.isEmpty(authStoreUserDTOList)) {
            return results;
        }
        List<String> userIdList = ListUtils.emptyIfNull(authStoreUserDTOList)
                .stream()
                .map(AuthStoreUserDTO::getUserIdList)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(userIdList)) {
            List<EnterpriseUserDO> userDOS = enterpriseUserDao.selectUsersByStatusAndUserIds(eid, userIdList, UserStatusEnum.NORMAL.getCode(), active);
            if (CollectionUtils.isEmpty(userDOS)) {
                return results;
            }
            List<String> userIds = userDOS.stream()
                    .map(EnterpriseUserDO::getUserId)
                    .collect(Collectors.toList());
            //获取用户的岗位
            Map<String, List<SelectComponentUserRoleVO>> userRoles = getUserRoles(eid, userIds);
            //获取用户的门店 和 区域 使用存在的接口
            Map<String, AuthRegionStoreDTO> authRegionStoreMap = getAuthRegionStoreMap(eid, userIds);
            //构造返回结果
            results = fetchSelectComponentUserVo(userDOS, userRoles, authRegionStoreMap);
        }
        return results;
    }

    @Override
    public List<SelectComponentUserVO> getSelectUserByStoreIdAndKeyword(String eid, String storeId, String keyword) {
        List<SelectComponentUserVO> results = new ArrayList<>();
        //获取门店下权限的用户
        List<AuthStoreUserDTO> authStoreUserDTOList = visualService.authStoreUser(eid,
                Collections.singletonList(storeId), CoolPositionTypeEnum.STORE_INSIDE.getCode());
        if (CollectionUtils.isEmpty(authStoreUserDTOList)) {
            return results;
        }
        //门店下的人员id
        List<String> storeUserIdList = ListUtils.emptyIfNull(authStoreUserDTOList)
                .stream()
                .map(AuthStoreUserDTO::getUserIdList)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(eid);
        DataSourceHelper.changeToMy();
        List<EnterpriseUserDO> userDOS = new ArrayList<>();
        //搜索用户的信息
        if (AppTypeEnum.isQwType(config.getAppType())) {
            List<String> qwUserIds = chatService.
                    searchUserOrDeptByName(config.getDingCorpId(), config.getAppType(), keyword, Constants.ONE_VALUE_STRING,
                            Constants.INDEX_ONE, Constants.TWO_HUNDRED).getKey();
            if (CollectionUtils.isEmpty(qwUserIds)) {
                return results;
            }
            List<String> userIds = new ArrayList<>();
            qwUserIds.forEach(userId -> {
                if (storeUserIdList.contains(userId)) {
                    userIds.add(userId);
                }
            });
            if (CollectionUtils.isEmpty(userIds)) {
                return results;
            }
            //获得门店下人员和企微人员的并集
            userDOS = enterpriseUserDao.selectUserByKeyword(eid, null, UserStatusEnum.NORMAL.getCode(), userIds, null);
        } else {
            userDOS = enterpriseUserDao.selectUserByKeyword(eid, keyword, UserStatusEnum.NORMAL.getCode(), storeUserIdList, null);
        }
        if (CollectionUtils.isEmpty(userDOS)) {
            return results;
        }
        List<String> userIds = userDOS.stream()
                .map(EnterpriseUserDO::getUserId)
                .collect(Collectors.toList());
        //获取用户的岗位
        Map<String, List<SelectComponentUserRoleVO>> userRoles = getUserRoles(eid, userIds);
        //获取用户的门店 和 区域 使用存在的接口
        Map<String, AuthRegionStoreDTO> authRegionStoreMap = getAuthRegionStoreMap(eid, userIds);
        //构造返回参数
        results = fetchSelectComponentUserVo(userDOS, userRoles, authRegionStoreMap);

        return results;
    }

    @Override
    public PageVO<SelectComponentStoreVO> getCommonStores(String eid, String keyword, Integer pageNum, Integer pageSize, Boolean isByKeyword, List<String> storeStatusList) {
        if (isByKeyword && StringUtils.isBlank(keyword)) {
            return new PageVO<>();
        }
        CurrentUser user = UserHolder.getUser();
        String key = LRUService.getKey(eid, user.getUserId(), LRUService.RECENT_USE_STORE);
        Set<String> recentStoreColl = redis.zrange(key, Constants.INDEX_ZERO, -1);
        if (CollectionUtils.isEmpty(recentStoreColl)) {
            return new PageVO<>();
        }
        //所有数据权限
        if (!AuthRoleEnum.ALL.getCode().equals(user.getRoleAuth())) {
            //查询权限门店
            AuthVisualDTO authVisualDTO = visualService.authRegionStoreByRole(eid, user.getUserId());
            List<String> storeIds = authVisualDTO.getStoreIdList();
            if (CollectionUtils.isEmpty(storeIds)) {
                return new PageVO<>();
            }
            //过滤无权限门店
            Set<String> set = storeIds.stream().collect(Collectors.toSet());
            recentStoreColl = recentStoreColl.stream()
                    .filter(data -> set.contains(data))
                    .collect(Collectors.toSet());
        }
        if (CollectionUtils.isEmpty(recentStoreColl)) {
            return new PageVO<>();
        }
        PageHelper.startPage(pageNum, pageSize);
        List<StoreDO> storeDOS = storeMapper.selectRecentStoreByKeyword(eid, new ArrayList<>(recentStoreColl), isByKeyword ? keyword : null, storeStatusList);
        //获取返回信息 构建VO
        List<SelectComponentStoreVO> vos = fetchSelectStoreVOS(storeDOS, eid);
        PageInfo<StoreDO> dos = new PageInfo<>(storeDOS);
        //分页结果集替换  解决直接用vos PageInfo 分页的参数total不对
        PageVO<SelectComponentStoreVO> results = new PageVO<>();
        results.setPage_num(dos.getPageNum());
        results.setPage_size(dos.getPageSize());
        results.setPageSize(dos.getPageSize());
        results.setPageNum(dos.getPageNum());
        results.setTotal(dos.getTotal());
        results.setList(vos);
        return results;
    }

    @Override
    public PageVO<SelectComponentStoreVO> getStoresByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize, String userId, List<String> storeStatusList) {
        if (StringUtils.isBlank(keyword) || StringUtils.isBlank(eid)) {
            return new PageVO<>();
        }
        //用户的权限下的门店
        AuthBaseVisualDTO baseAuth = visualService.baseAuth(eid, userId);
        List<String> authFullRegionPathList = baseAuth.getFullRegionPathList();
        List<String> authStoreIdList = baseAuth.getStoreIdList();
        if (!baseAuth.getIsAllStore() && CollectionUtils.isEmpty(authFullRegionPathList)
                && CollectionUtils.isEmpty(authStoreIdList)) {
            return new PageVO<>();
        }
        //分页模糊查询获取门店信息
        PageHelper.startPage(pageNum, pageSize);
        List<StoreDO> storeDOS = storeMapper.getAuthStoreByName(eid, keyword, baseAuth.getIsAdmin(),
                authStoreIdList, authFullRegionPathList, storeStatusList);
        if (CollectionUtils.isEmpty(storeDOS)) {
            return new PageVO<>();
        }
        //获取返回信息 构建VO
        List<SelectComponentStoreVO> vos = fetchSelectStoreVOS(storeDOS, eid);
        PageInfo<StoreDO> dos = new PageInfo<>(storeDOS);
        //分页结果集替换  解决直接用vos PageInfo 分页的参数total不对
        PageVO<SelectComponentStoreVO> results = new PageVO<>();
        results.setPage_num(dos.getPageNum());
        results.setPage_size(dos.getPageSize());
        results.setPageSize(dos.getPageSize());
        results.setPageNum(dos.getPageNum());
        results.setTotal(dos.getTotal());
        results.setList(vos);
        //常用添加
        if (StrUtil.isNotBlank(keyword)) {
            List<String> storeIds = storeDOS.stream()
                    .map(StoreDO::getStoreId)
                    .collect(Collectors.toList());
            lruService.putRecentUseStore(eid, UserHolder.getUser().getUserId(), storeIds);
        }
        return results;
    }

    @Override
    public PageVO<SelectComponentRegionVO> getRegionsByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize, String userId) {
        if (StringUtils.isBlank(keyword) || StringUtils.isBlank(eid)) {
            return new PageVO<>();
        }
        //用户权限区域
        AuthBaseVisualDTO baseAuth = visualService.baseAuth(eid, userId);
        List<String> regionIdList = baseAuth.getRegionIdList();
        List<String> fullRegionPathList = baseAuth.getFullRegionPathList();
        //查询区域
        PageHelper.startPage(pageNum, pageSize);
        //权限控制
        if (!baseAuth.getIsAllStore() && CollectionUtils.isEmpty(regionIdList) && CollectionUtils.isEmpty(fullRegionPathList)) {
            return new PageVO<>();
        }
        List<RegionDO> regionDOList = regionMapper.getRegionsByKeyword(eid, regionIdList, keyword, fullRegionPathList, baseAuth.getIsAdmin());

        if (CollectionUtils.isEmpty(regionDOList)) {
            return new PageVO<>();
        }
        //获取所有的regionpath 然后解析region 查询区域信息
        List<String> regionPaths = regionDOList.stream()
                .map(RegionDO::getRegionPath)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<String> pathRegionIds = getRegionIdsByRegionPath(regionPaths);
        //获取区域
        List<RegionDO> regionByRegionIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(pathRegionIds)) {
            regionByRegionIds = regionMapper.getRegionByRegionIds(eid, pathRegionIds);
        }
        //构建区域信息
        List<SelectComponentRegionVO> vos = fetchRegionVOByRegionDO(regionDOList, regionByRegionIds);
        PageInfo<RegionDO> dos = new PageInfo<>(regionDOList);
        //分页结果集替换  解决直接用vos PageInfo 分页的参数total不对
        PageVO<SelectComponentRegionVO> results = new PageVO<>();
        results.setPage_num(dos.getPageNum());
        results.setPage_size(dos.getPageSize());
        results.setPageSize(dos.getPageSize());
        results.setPageNum(dos.getPageNum());
        results.setTotal(dos.getTotal());
        results.setList(vos);
        return results;
    }

    @Override
    public PageVO<SelectComponentRegionVO> getZxjpRegionsByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize, String userId) {
        if (StringUtils.isBlank(keyword) || StringUtils.isBlank(eid)) {
            return new PageVO<>();
        }
        //用户权限区域
        AuthBaseVisualDTO baseAuth = visualService.baseAuth(eid, userId);
        List<String> regionIdList = baseAuth.getRegionIdList();
        List<String> fullRegionPathList = baseAuth.getFullRegionPathList();
        //查询区域
        PageHelper.startPage(pageNum, pageSize);
        //正新做特殊处理，放开权限限制
//        //权限控制
//        if (!baseAuth.getIsAllStore() && CollectionUtils.isEmpty(regionIdList) && CollectionUtils.isEmpty(fullRegionPathList)) {
//            return new PageVO<>();
//        }

        List<RegionDO> regionDOList = regionMapper.getRegionsByKeyword(eid, regionIdList, keyword, fullRegionPathList, true);

        if (CollectionUtils.isEmpty(regionDOList)) {
            return new PageVO<>();
        }
        //获取所有的regionpath 然后解析region 查询区域信息
        List<String> regionPaths = regionDOList.stream()
                .map(RegionDO::getRegionPath)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<String> pathRegionIds = getRegionIdsByRegionPath(regionPaths);
        //获取区域
        List<RegionDO> regionByRegionIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(pathRegionIds)) {
            regionByRegionIds = regionMapper.getRegionByRegionIds(eid, pathRegionIds);
        }
        //构建区域信息
        List<SelectComponentRegionVO> vos = fetchRegionVOByRegionDO(regionDOList, regionByRegionIds);
        List<SelectComponentRegionVO> vos1 = new ArrayList<>();
        List<String> key = new ArrayList<>();
        //排除区域 4-培训部，32-总裁办，350-酷店掌，460-上海豆码网络科技有限公司，461-上海火码信息科技有限公司，463-上海稳码信息科技有限公司
        key.add("32");
        key.add("4");
        key.add("350");
        key.add("460");
        key.add("461");
        key.add("463");
        for (SelectComponentRegionVO vo : vos) {
            List<String> regionIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(vo.getRegions())) {
                regionIds = vo.getRegions().stream().map(SelectComponentRegionVO::getId).collect(Collectors.toList());
            }
            if ( !key.contains(vo.getId())
                    && !new HashSet<>(regionIds).containsAll(key)) {
                vos1.add(vo);
            }
        }
        PageInfo<SelectComponentRegionVO> dos = new PageInfo<>(vos1);
        //分页结果集替换  解决直接用vos PageInfo 分页的参数total不对
        PageVO<SelectComponentRegionVO> results = new PageVO<>();
        results.setPage_num(dos.getPageNum());
        results.setPage_size(dos.getPageSize());
        results.setPageSize(dos.getPageSize());
        results.setPageNum(dos.getPageNum());
        results.setTotal(dos.getTotal());
        results.setList(vos);
        return results;
    }

    @Override
    public SelectComptRegionStoreVO getRegionAndStore(String eid, Long parentId, String userId, List<String> storeStatusList) {
        //1.查询用户角色
        SelectComptRegionStoreVO result = new SelectComptRegionStoreVO();
        //parentId不为空，则只需要查parentId的子级区域
        if (parentId != null) {
            List<RegionDO> regionDOS = regionMapper.getRegionsByParentId(eid, parentId);
            //获取所有regionPath的区域信息
            List<RegionDO> regions = getRegionsByRegionPath(regionDOS, eid);
            //根据区域id转成map
            Map<String, RegionDO> regionDOSMap = ListUtils.emptyIfNull(regions)
                    .stream()
                    .collect(Collectors.toMap(RegionDO::getRegionId, Function.identity(), (r, e) -> r));
            //获取所有节点路径
            List<SelectComponentRegionVO> regionList = new ArrayList<>();
            List<SelectComponentRegionVO> allRegionList = new ArrayList<>();
            ListUtils.emptyIfNull(regionDOS).stream().
                    forEach(data -> {
                        SelectComponentRegionVO regionVO = new SelectComponentRegionVO();
                        List<String> regionIdList = StrUtil.splitTrim(data.getRegionPath(), "/");
                        regionVO.setRegions(fetchSelectComponentRegionVO(regionIdList, regionDOSMap));
                        regionVO.setStoreNum(data.getStoreNum());
                        regionVO.setId(String.valueOf(data.getId()));
                        regionVO.setName(data.getName());
                        regionVO.setStoreId(data.getStoreId());
                        regionVO.setRegionType(data.getRegionType());
                        //区域过滤掉门店
                        if (!RegionTypeEnum.STORE.getType().equals(data.getRegionType())) {
                            regionList.add(regionVO);
                        }
                        allRegionList.add(regionVO);
                    });
            result.setRegionList(regionList);
            //后续改造需要，先设置值
            // 填充allRegionList门店类型区域中的门店相关信息
            fillAllRegionListStoreInfo(allRegionList, eid);
            result.setAllRegionList(allRegionList);
            //门店按区域分组
            result.setStoreList(new ArrayList<>());
            //设置状态和收藏状态
            List<StoreDO> storeDOS = storeMapper.getStoreByRegionId(eid, Collections.singletonList(parentId), storeStatusList);
            // 过滤掉非指定状态的门店类型的region
            if (CollectionUtils.isNotEmpty(storeStatusList) && CollectionUtils.isNotEmpty(result.getAllRegionList())) {
                Set<String> storeIds = CollStreamUtil.toSet(storeDOS, StoreDO::getStoreId);
                List<SelectComponentRegionVO> filterAllRegionList = result.getAllRegionList().stream()
                        .filter(region -> RegionTypeEnum.PATH.getType().equals(region.getRegionType()) || storeIds.contains(region.getStoreId()))
                        .collect(Collectors.toList());
                result.setAllRegionList(filterAllRegionList);
            }
            List<SelectComponentStoreVO> vos = fetchSelectStoreVOS(storeDOS, eid);
            result.setStoreList(vos);
            return result;
        }
        //parentId为空，则是第一加载，需要从权限里获取
        List<String> storeIds = new ArrayList<>();
        List<String> regionIds = new ArrayList<>();
        boolean isAdmin = sysRoleService.checkIsAdmin(eid, userId);
        if (isAdmin) {
            regionIds.add(Constants.ROOT_REGION_ID);
        } else {
            List<UserAuthMappingDO> userAuthMappingDOS = userAuthMappingMapper.listUserAuthMappingByUserAndType(eid,
                    userId, null);
            ListUtils.emptyIfNull(userAuthMappingDOS).stream().forEach(data -> {
                if (StringUtils.equals(UserAuthMappingTypeEnum.REGION.getCode(), data.getType())) {
                    regionIds.add(data.getMappingId());
                }
                if (StringUtils.equals(UserAuthMappingTypeEnum.STORE.getCode(), data.getType())) {
                    storeIds.add(data.getMappingId());
                }
            });
        }
        if (CollectionUtils.isNotEmpty(regionIds)) {
            //查询区域节点
            List<RegionDO> regionDOList = regionMapper.getRegionByRegionIds(eid, regionIds);
            result.setRegionList(new ArrayList<>());
            if (CollectionUtils.isNotEmpty(regionDOList)) {
                //设置allRegionList
                List<RegionDO> regions = getRegionsByRegionPath(regionDOList, eid);
                result.setAllRegionList(fetchRegionVOByRegionDO(regionDOList, regions));
                // 填充allRegionList门店类型区域中的门店相关信息
                fillAllRegionListStoreInfo(result.getAllRegionList(), eid);
                // 是否显示区域对应门店的权限
                //取区域类型是门店的，添加到之前的storeIds
                List<String> regionStoreIds = regionDOList.stream()
                        .filter(item -> RegionTypeEnum.STORE.getType().equals(item.getRegionType()))
                        .map(RegionDO::getStoreId)
                        .collect(Collectors.toList());
                storeIds.addAll(regionStoreIds);

                //只取区域类型
                List<RegionDO> regionExculeStoreList = regionDOList.stream()
                        .filter(item -> !RegionTypeEnum.STORE.getType().equals(item.getRegionType()))
                        .collect(Collectors.toList());
                //获取所有regionPath的区域信息
                List<RegionDO> regionExculeStores = getRegionsByRegionPath(regionExculeStoreList, eid);
                result.setRegionList(fetchRegionVOByRegionDO(regionExculeStoreList, regionExculeStores));
            }
        }
        if (CollectionUtils.isNotEmpty(storeIds)) {
            List<StoreDO> storeDOList = storeMapper.getByStoreIdList(eid, storeIds);
            //设置设备和收藏状态
            List<SelectComponentStoreVO> vos = fetchSelectStoreVOS(storeDOList, eid);
            result.setStoreList(vos);
        }
        return result;
    }

    @Override
    public SelectComptRegionStoreVO getRegion(String eid, Long parentId, String userId) {
        //1.查询用户角色
        SelectComptRegionStoreVO result = new SelectComptRegionStoreVO();
        //parentId不为空，则只需要查parentId的子级区域
        if (parentId != null) {
            List<RegionDO> regionDOS = regionMapper.getRegionsByParentId(eid, parentId);
            //获取所有regionPath的区域信息
            List<RegionDO> regions = getRegionsByRegionPath(regionDOS, eid);
            //根据区域id转成map
            Map<String, RegionDO> regionDOSMap = ListUtils.emptyIfNull(regions)
                    .stream()
                    .collect(Collectors.toMap(RegionDO::getRegionId, Function.identity(), (r, e) -> r));
            //获取所有节点路径
            List<SelectComponentRegionVO> regionList = new ArrayList<>();
            List<SelectComponentRegionVO> allRegionList = new ArrayList<>();
            ListUtils.emptyIfNull(regionDOS).stream().
                    forEach(data -> {
                        SelectComponentRegionVO regionVO = new SelectComponentRegionVO();
                        List<String> regionIdList = StrUtil.splitTrim(data.getRegionPath(), "/");
                        regionVO.setRegions(fetchSelectComponentRegionVO(regionIdList, regionDOSMap));
                        regionVO.setStoreNum(data.getStoreNum());
                        regionVO.setId(String.valueOf(data.getId()));
                        regionVO.setName(data.getName());
                        regionVO.setStoreId(data.getStoreId());
                        regionVO.setRegionType(data.getRegionType());
                        //区域过滤掉门店
                        if (!RegionTypeEnum.STORE.getType().equals(data.getRegionType())) {
                            regionList.add(regionVO);
                            allRegionList.add(regionVO);
                        }
                    });
            result.setRegionList(regionList);
            //后续改造需要，先设置值
            // 填充allRegionList门店类型区域中的门店相关信息
            fillAllRegionListStoreInfo(allRegionList, eid);
            result.setAllRegionList(allRegionList);

            return getSelectComptRegionStoreVO(result);
        }
        //parentId为空，则是第一加载，需要从权限里获取
        List<String> regionIds = new ArrayList<>();
        boolean isAdmin = sysRoleService.checkIsAdmin(eid, userId);
        if (isAdmin) {
            regionIds.add(Constants.ROOT_REGION_ID);
        } else {
            List<UserAuthMappingDO> userAuthMappingDOS = userAuthMappingMapper.listUserAuthMappingByUserAndType(eid,
                    userId, null);
            ListUtils.emptyIfNull(userAuthMappingDOS).stream().forEach(data -> {
                if (StringUtils.equals(UserAuthMappingTypeEnum.REGION.getCode(), data.getType())) {
                    regionIds.add(data.getMappingId());
                }
            });
        }
        if (CollectionUtils.isNotEmpty(regionIds)) {
            //查询区域节点
            List<RegionDO> regionDOList = regionMapper.getRegionByRegionIds(eid, regionIds);
            result.setRegionList(new ArrayList<>());
            if (CollectionUtils.isNotEmpty(regionDOList)) {
                //设置allRegionList
                List<RegionDO> regions = getRegionsByRegionPath(regionDOList, eid);
                result.setAllRegionList(fetchRegionVOByRegionDO(regionDOList, regions));
                // 填充allRegionList门店类型区域中的门店相关信息
                fillAllRegionListStoreInfo(result.getAllRegionList(), eid);

                //只取区域类型
                List<RegionDO> regionExculeStoreList = regionDOList.stream()
                        .filter(item -> !RegionTypeEnum.STORE.getType().equals(item.getRegionType()))
                        .collect(Collectors.toList());
                //获取所有regionPath的区域信息
                List<RegionDO> regionExculeStores = getRegionsByRegionPath(regionExculeStoreList, eid);
                result.setRegionList(fetchRegionVOByRegionDO(regionExculeStoreList, regionExculeStores));
            }
        }
        return getSelectComptRegionStoreVO(result);
    }

    private SelectComptRegionStoreVO getSelectComptRegionStoreVO(SelectComptRegionStoreVO result) {
        if (result == null ){
            return new SelectComptRegionStoreVO();
        }
        SelectComptRegionStoreVO response = new SelectComptRegionStoreVO();
        List<SelectComponentRegionVO> regionList1 = new ArrayList<>();
        List<SelectComponentRegionVO> allRegionList1 = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(result.getRegionList())) {
            for (SelectComponentRegionVO dto : result.getRegionList()) {
                ZXJPExcludeRegion(regionList1, dto);
            }
        }
       if (CollectionUtils.isNotEmpty(result.getAllRegionList())) {
           for (SelectComponentRegionVO dto : result.getAllRegionList()) {
               ZXJPExcludeRegion(allRegionList1, dto);
           }
       }
        response.setRegionList(regionList1);
        response.setAllRegionList(allRegionList1);
        return response;
    }

    private void ZXJPExcludeRegion(List<SelectComponentRegionVO> regionList, SelectComponentRegionVO dto) {
        //排除区域 4-培训部，32-总裁办，350-酷店掌，460-上海豆码网络科技有限公司，461-上海火码信息科技有限公司，463-上海稳码信息科技有限公司
        if (!"32".equals(dto.getId()) && !"4".equals(dto.getId())
                && !"350".equals(dto.getId()) && !"460".equals(dto.getId())
                && !"461".equals(dto.getId()) && !"463".equals(dto.getId())
        ) {
            regionList.add(dto);
        }
    }

    @Override
    public SelectComptRegionStoreVO getRegionAndStoreFullPath(String eid, Long parentId) {
        CurrentUser user = UserHolder.getUser();
        //1.查询用户角色
        SelectComptRegionStoreVO result = new SelectComptRegionStoreVO();
        List<UserAuthMappingDO> userAuthMappingDOS = userAuthMappingMapper.listUserAuthMappingByUserAndType(eid, user.getUserId(), null);
        if (!StringUtils.equals(user.getRoleAuth(), AuthRoleEnum.ALL.getCode()) && CollectionUtils.isEmpty(userAuthMappingDOS)) {
            return result;
        }
        //parentId不为空，则只需要查parentId的有权限的子级区域
        if (parentId != null) {
            List<String> regionList = userAuthMappingDOS.stream()
                    .map(UserAuthMappingDO::getMappingId)
                    .collect(Collectors.toList());
            //查询出所有权限的fullRegionPath
            List<RegionDO> authUserRegionList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(regionList)) {
                authUserRegionList = regionMapper.getRegionByRegionIds(eid, regionList);

            }
            List<String> authFullRegionPath = ListUtils.emptyIfNull(authUserRegionList)
                    .stream()
                    .map(data -> StrUtil.splitTrim(data.getFullRegionPath(), "/"))
                    .flatMap(Collection::stream)
                    .distinct()
                    .collect(Collectors.toList());
            //如果传入的parentId的父节点在权限范围内则直接给节点下的所有下一级节点,或者是管理员直接给节点下的所有下一级节点
            RegionDO parentRegionDO = regionMapper.getByRegionId(eid, parentId);
            List<RegionDO> regionDOS = regionMapper.getRegionsByParentId(eid, parentId);
            List<String> parentPathList = StrUtil.splitTrim(parentRegionDO.getFullRegionPath(), "/");
            List<RegionDO> authRegionList;
            if (StringUtils.equals(user.getRoleAuth(), AuthRoleEnum.ALL.getCode()) || isAuth(authUserRegionList, parentPathList)) {
                authRegionList = regionDOS;
            } else {
                //获取需要获取节点的下一级所有数据
                //过滤掉没有权限的区域和门店
                authRegionList = ListUtils.emptyIfNull(regionDOS)
                        .stream()
                        .filter(data -> authFullRegionPath.contains(data.getId().toString()))
                        .collect(Collectors.toList());
            }

            Map<String, RegionDO> regionDOSMap = ListUtils.emptyIfNull(authRegionList)
                    .stream()
                    .collect(Collectors.toMap(RegionDO::getRegionId, Function.identity(), (r, e) -> r));
            //获取所有节点路径
            List<SelectComponentRegionVO> fullRegionNameList = new ArrayList<>();
            List<String> storeIdList = new ArrayList<>();
            ListUtils.emptyIfNull(authRegionList).
                    forEach(data -> {
                        SelectComponentRegionVO regionVO = new SelectComponentRegionVO();
                        List<String> regionIdList = StrUtil.splitTrim(data.getRegionPath(), "/");
                        regionVO.setRegions(fetchSelectComponentRegionVO(regionIdList, regionDOSMap));
                        regionVO.setStoreNum(data.getStoreNum());
                        regionVO.setId(String.valueOf(data.getId()));
                        regionVO.setName(data.getName());
                        regionVO.setStoreId(data.getStoreId());
                        regionVO.setRegionType(data.getRegionType());
                        //区域过滤掉门店
                        if (!RegionTypeEnum.STORE.getType().equals(data.getRegionType())) {
                            fullRegionNameList.add(regionVO);
                        } else {
                            storeIdList.add(data.getStoreId());
                        }

                    });
            result.setRegionList(fullRegionNameList);
            //后续改造需要，先设置值
            result.setStoreList(new ArrayList<>());
            //设置状态和收藏状态
            if (CollectionUtils.isNotEmpty(storeIdList)) {
                List<StoreDO> storeDOS = storeMapper.getByStoreIdList(eid, storeIdList);
                List<SelectComponentStoreVO> vos = fetchSelectStoreVOS(storeDOS, eid);
                result.setStoreList(vos);
            }

            return result;
        }
        //parentId为空，加载根目录信息
        //计算用户权限门店数
        Integer authStoreCout = 0;

        RegionDO regionDO = regionMapper.getByRegionId(eid, Long.valueOf(Constants.ROOT_REGION_ID));
        if (StringUtils.equals(user.getRoleAuth(), AuthRoleEnum.ALL.getCode())) {
            authStoreCout = regionDO.getStoreNum();
        } else {
            List<AuthStoreCountDTO> authStoreCountDTOList = visualService.authStoreCount(eid, Collections.singletonList(user.getUserId()), false);
            if (CollectionUtils.isNotEmpty(authStoreCountDTOList)) {
                authStoreCout = authStoreCountDTOList.get(0).getStoreCount();
            }
        }
        List<SelectComponentRegionVO> regionList = new ArrayList<>();
        SelectComponentRegionVO vo = new SelectComponentRegionVO();
        vo.setId(regionDO.getRegionId());
        vo.setStoreNum(regionDO.getStoreNum());
        vo.setName(regionDO.getName());
        vo.setRegionType(regionDO.getRegionType());
        vo.setStoreNum(authStoreCout);
        regionList.add(vo);
        result.setRegionList(regionList);
        return result;
    }

    private boolean isAuth(List<RegionDO> authUserRegionList, List<String> parentPathList) {
        return CollectionUtils.isNotEmpty(authUserRegionList) &&
                authUserRegionList.stream().anyMatch(data -> parentPathList.stream().anyMatch(path -> StringUtils.equals(path, data.getId().toString())));
    }


    @Override
    public SelectComponentRegionVO getParentRegionsByRegionId(String eid, String regionId, CurrentUser user) {
        SelectComponentRegionVO result = new SelectComponentRegionVO();
        if (StringUtils.isBlank(regionId) || StringUtils.isBlank(eid)) {
            return result;
        }
        List<RegionDO> regions = regionMapper.getRegionByRegionIds(eid, Arrays.asList(regionId));
        if (CollectionUtils.isEmpty(regions)) {
            return result;
        }
        List<RegionDO> parentRegion = getRegionsByRegionPath(regions, eid);
        Map<String, RegionDO> regionDOSMap = ListUtils.emptyIfNull(parentRegion)
                .stream()
                .collect(Collectors.toMap(RegionDO::getRegionId, Function.identity(), (r, e) -> r));
        RegionDO regionDO = regions.get(0);
        result.setRegionType(regionDO.getRegionType());
        result.setId(regionDO.getRegionId());
        result.setName(regionDO.getName());
        result.setStoreNum(regionDO.getStoreNum());
        result.setStoreId(regionDO.getStoreId());
        List<String> regionIdList = StrUtil.splitTrim(regionDO.getRegionPath(), "/");
        List<SelectComponentRegionVO> selectComponentRegionVOS = fetchSelectComponentRegionVO(regionIdList, regionDOSMap);
        setAuth(eid, selectComponentRegionVOS, user);
        result.setRegions(selectComponentRegionVOS);

        return result;
    }

    private void setAuth(String eid, List<SelectComponentRegionVO> selectComponentRegionVOS, CurrentUser user) {
        List<String> authIds = ListUtils.emptyIfNull(userAuthMappingMapper.listUserAuthMappingByUserId(eid, user.getUserId()))
                .stream()
                .map(UserAuthMappingDO::getMappingId)
                .collect(Collectors.toList());
        boolean parentHasAuth = false;
        for (SelectComponentRegionVO vo : ListUtils.emptyIfNull(selectComponentRegionVOS)) {
            if (Role.MASTER.getRoleEnum().equals(user.getSysRoleDO().getRoleEnum()) || parentHasAuth || authIds.contains(vo.getId())) {
                vo.setHasAuth(true);
                parentHasAuth = true;
            } else {
                vo.setHasAuth(false);
            }
        }
    }

    private List<SelectComponentStoreVO> fetchSelectStoreVOS(List<StoreDO> storeDOS, String eid) {
        List<String> storeIds = ListUtils.emptyIfNull(storeDOS)
                .stream()
                .map(StoreDO::getStoreId)
                .distinct()
                .collect(Collectors.toList());
        List<SelectComponentDeviceVO> deviceVOS = new ArrayList<>();
        Map<String, List<String>> storeDeviceIdMap = new HashMap<>(16);
        if (CollectionUtils.isNotEmpty(storeIds)) {
            // 获取门店设备列表
            List<DeviceDO> deviceByStoreIdList = deviceMapper.getDeviceByStoreIdList(eid, storeIds, DeviceTypeEnum.DEVICE_VIDEO.getCode(), null, null);
            storeDeviceIdMap = ListUtils.emptyIfNull(deviceByStoreIdList).stream()
                    .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
                    .collect(Collectors.groupingBy(DeviceDO::getBindStoreId,
                            Collectors.mapping(DeviceDO::getDeviceId, Collectors.toList())));
            List<String> deviceIds = ListUtils.emptyIfNull(deviceByStoreIdList).stream()
                    .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
                    .map(DeviceDO::getDeviceId)
                    .collect(Collectors.toList());
            List<DeviceDO> devices = deviceMapper.getDeviceByDeviceIdList(eid, deviceIds);
            //DO转换Vo
            deviceVOS = fetchDeviceVOByDeviceDOS(devices);
        }
        // 门店id 区域id 映射
        Map<String, Long> storeIdRegionIdMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(storeIds)) {
            List<RegionDO> storeRegionList = regionMapper.listRegionByStoreIds(eid, storeIds);
            storeIdRegionIdMap = ListUtils.emptyIfNull(storeRegionList)
                    .stream()
                    .filter(a -> StringUtils.isNotBlank(a.getStoreId()) && a.getId() != null)
                    .collect(Collectors.toMap(RegionDO::getStoreId, RegionDO::getId, (a, b) -> a));
        }
        //获取区域
        List<RegionDO> regions = getRegionsByStores(storeDOS, eid);
        Map<String, RegionDO> regionDOSMap = ListUtils.emptyIfNull(regions)
                .stream()
                .collect(Collectors.toMap(RegionDO::getRegionId, Function.identity(), (r, e) -> r));
        Map<String, Integer> storeUsers = getStoreUsers(storeIds, eid);
        // 填充门店与设备的关联数据
        List<SelectComponentStoreVO> vos = new ArrayList<>();
        for (StoreDO storeDO : storeDOS) {
            SelectComponentStoreVO result = new SelectComponentStoreVO();
            result.setName(storeDO.getStoreName());
            result.setStoreId(storeDO.getStoreId());
            result.setAddress(storeDO.getStoreAddress());
            List<String> regionIds = StrUtil.splitTrim(storeDO.getRegionPath(), "/");
            result.setRegions(fetchSelectComponentRegionVO(regionIds, regionDOSMap));
            List<String> devices = storeDeviceIdMap.get(storeDO.getStoreId());
            if (CollUtil.isEmpty(devices)) {
                result.setDevices(new ArrayList<>());
            } else {
                result.setDevices(deviceVOS.stream().filter(d -> devices.contains(d.getDeviceId())).collect(Collectors.toList()));
            }
            if (CollectionUtils.isEmpty(result.getDevices())) {
                result.setHasCamera(Boolean.FALSE);
            } else {
                result.setHasCamera(Boolean.TRUE);
            }
            if (storeIdRegionIdMap != null && storeIdRegionIdMap.get(storeDO.getStoreId()) != null) {
                result.setStoreRegionId(storeIdRegionIdMap.get(storeDO.getStoreId()));
            }
            result.setUserCount(Objects.isNull(storeUsers.get(storeDO.getStoreId())) ? 0 : storeUsers.get(storeDO.getStoreId()));
            result.setStoreStatus(storeDO.getStoreStatus());
            vos.add(result);
        }
        return vos;
    }

    /**
     * 构建区域的信息
     *
     * @param regionDOList
     * @param pathRegionDOList
     * @return
     */
    private List<SelectComponentRegionVO> fetchRegionVOByRegionDO(List<RegionDO> regionDOList, List<RegionDO> pathRegionDOList) {
        Map<String, RegionDO> regionDOSMap = ListUtils.emptyIfNull(pathRegionDOList)
                .stream()
                .collect(Collectors.toMap(RegionDO::getRegionId, Function.identity(), (r, e) -> r));
        List<SelectComponentRegionVO> results = new ArrayList<>();
        for (RegionDO regionDO : regionDOList) {
            SelectComponentRegionVO vo = new SelectComponentRegionVO();
            vo.setId(regionDO.getRegionId());
            vo.setStoreNum(regionDO.getStoreNum());
            vo.setName(regionDO.getName());
            vo.setRegionType(regionDO.getRegionType());
            List<String> regionIds = StrUtil.splitTrim(regionDO.getRegionPath(), "/");
            vo.setRegions(fetchSelectComponentRegionVO(regionIds, regionDOSMap));
            vo.setStoreId(regionDO.getStoreId());
            results.add(vo);
        }
        return results;
    }

    /**
     * 实体转换
     *
     * @param devices
     * @return
     */
    private List<SelectComponentDeviceVO> fetchDeviceVOByDeviceDOS(List<DeviceDO> devices) {
        List<SelectComponentDeviceVO> vos = new ArrayList<>();
        devices.forEach(deviceDO -> {
            SelectComponentDeviceVO vo = new SelectComponentDeviceVO();
            vo.setDeviceId(deviceDO.getDeviceId());
            vo.setDeviceName(deviceDO.getDeviceName());
            vo.setType(deviceDO.getType());
            vos.add(vo);
        });
        return vos;
    }

    /**
     * 获取岗位权限
     *
     * @param eid
     * @param userIds
     * @return
     */
    public Map<String, List<SelectComponentUserRoleVO>> getUserRoles(String eid, List<String> userIds) {
        List<SelectComponentUserRoleVO> roleVos = sysRoleMapper.selectUserRoleByUserIds(eid, userIds);
        Map<String, List<SelectComponentUserRoleVO>> userRoles = ListUtils.emptyIfNull(roleVos)
                .stream()
                .collect(Collectors.groupingBy(SelectComponentUserRoleVO::getUserId, LinkedHashMap::new, Collectors.toList()));
        return userRoles;
    }

    /**
     * 获取区域门店权限
     *
     * @param eid
     * @param userIds
     * @return
     */
    public Map<String, AuthRegionStoreDTO> getAuthRegionStoreMap(String eid, List<String> userIds) {
        List<AuthRegionStoreDTO> authRegionStoreDTOList = visualService.authRegionStoreByUserList(eid, userIds);
        Map<String, AuthRegionStoreDTO> authRegionStoreMap = ListUtils.emptyIfNull(authRegionStoreDTOList)
                .stream()
                .collect(Collectors.toMap(AuthRegionStoreDTO::getUserId, Function.identity(), (a, b) -> a));
        return authRegionStoreMap;
    }

    /**
     * 递归获取部门链路
     *
     * @param eid
     * @param parentId
     * @param departmentInfoVOS
     * @return
     */
    private List<DepartmentInfoVO> getParentDepartmentRecursion(String eid, String parentId, List<DepartmentInfoVO> departmentInfoVOS) {
        if (StringUtils.isEmpty(parentId) || parentId.equals("null")) {
            return departmentInfoVOS;
        }
        SysDepartmentDO sysDepartmentDO = sysDepartmentMapper.selectById(eid, parentId);
        DepartmentInfoVO departmentInfoVO = new DepartmentInfoVO();
        departmentInfoVO.setDepartmentId(String.valueOf(sysDepartmentDO.getId()));
        departmentInfoVO.setName(sysDepartmentDO.getName());
        departmentInfoVOS.add(departmentInfoVO);
        return getParentDepartmentRecursion(eid, String.valueOf(sysDepartmentDO.getParentId()), departmentInfoVOS);
    }

    /**
     * 获取门店的用户数
     *
     * @param storeIds
     * @param eid
     * @return
     */
    private Map<String, Integer> getStoreUsers(List<String> storeIds, String eid) {
        Map<String, Integer> result = new HashMap<>(16);
        if (CollectionUtils.isEmpty(storeIds)) {
            return result;
        }
        List<AuthStoreUserDTO> authStoreUserDTOList = visualService.authStoreUser(eid, storeIds, CoolPositionTypeEnum.STORE_INSIDE.getCode());
        if (CollectionUtils.isEmpty(authStoreUserDTOList)) {
            return result;
        }
        //获取该门店下的user
        List<String> userIdList = ListUtils.emptyIfNull(authStoreUserDTOList)
                .stream()
                .map(AuthStoreUserDTO::getUserIdList)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        //查询用户状态正常的
        List<String> normalUserIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userIdList)) {
            normalUserIds = enterpriseUserDao.selectByUserIdsAndStatus(eid, userIdList, UserStatusEnum.NORMAL.getCode());
        }
        //制定final 解决下文流式运用报错
        final List<String> normal = normalUserIds;
        //统计每个门店下的正常的人员数
        result = ListUtils.emptyIfNull(authStoreUserDTOList)
                .stream()
                .collect(Collectors.toMap(AuthStoreUserDTO::getStoreId,
                        authStoreUserDTO -> {
                            return (int) ListUtils.emptyIfNull(authStoreUserDTO.getUserIdList())
                                    .stream()
                                    .filter(normal::contains).count();
                        }, (r, e) -> r));
        return result;
    }

    /**
     * 构造门店信息的VO
     *
     * @param storeDOS
     * @param regionDOS
     * @param storeUsers
     * @return
     */
    private List<SelectComponentStoreVO> fetchSelectComponentStoreVO(List<StoreDO> storeDOS, List<RegionDO> regionDOS, Map<String, Integer> storeUsers) {
        Map<String, RegionDO> regionDOSMap = ListUtils.emptyIfNull(regionDOS)
                .stream()
                .collect(Collectors.toMap(RegionDO::getRegionId, Function.identity(), (r, e) -> r));
        List<SelectComponentStoreVO> results = new ArrayList<>();
        for (StoreDO storeDO : storeDOS) {
            SelectComponentStoreVO result = new SelectComponentStoreVO();
            result.setName(storeDO.getStoreName());
            result.setStoreId(storeDO.getStoreId());
            result.setAddress(storeDO.getStoreAddress());
            result.setUserCount(Objects.isNull(storeUsers.get(storeDO.getStoreId())) ? 0 : storeUsers.get(storeDO.getStoreId()));
            List<String> regionIds = StrUtil.splitTrim(storeDO.getRegionPath(), "/");
            result.setRegions(fetchSelectComponentRegionVO(regionIds, regionDOSMap));
            results.add(result);
        }
        return results;
    }

    /**
     * 组装SelectComponentRegionVO 信息
     *
     * @param regionIds
     * @param regionDOSMap
     * @return
     */
    @Override
    public List<SelectComponentRegionVO> fetchSelectComponentRegionVO(List<String> regionIds, Map<String, RegionDO> regionDOSMap) {
        List<SelectComponentRegionVO> results = new ArrayList<>();
        for (String region : regionIds) {
            RegionDO regionDO = regionDOSMap.get(region);
            //过滤区域类型是门店类型的
            if (Objects.isNull(regionDO) || RegionTypeEnum.STORE.getType().equals(regionDO.getRegionType())) {
                continue;
            }
            SelectComponentRegionVO result = new SelectComponentRegionVO();
            result.setId(region);
            result.setStoreNum(Objects.isNull(regionDO.getStoreNum()) ? 0 : regionDO.getStoreNum());
            result.setName(regionDO.getName());

            results.add(result);
        }
        return results;
    }

    /**
     * regionPath 转换为 regionId
     *
     * @param regionPaths
     * @return
     */
    private List<String> getRegionIdsByRegionPath(List<String> regionPaths) {
        List<String> regionIds = new ArrayList<>();
        regionPaths.forEach(regionPath -> {
            regionIds.addAll(StrUtil.splitTrim(regionPath, "/"));
        });
        return regionIds.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 构造SelectComponentUserVo
     *
     * @param userDOS
     * @param userRoles
     * @return
     */
    private List<SelectComponentUserVO> fetchSelectComponentUserVo(List<EnterpriseUserDO> userDOS, Map<String, List<SelectComponentUserRoleVO>> userRoles,
                                                                   Map<String, AuthRegionStoreDTO> authRegionStoreMap) {
        //拆分门店和区域
        Map<String, List<SelectComponentRegionVO>> regionVOSMap = new HashMap<>(16);
        Map<String, List<SelectComponentStoreVO>> storeVOSMap = new HashMap<>(16);
        analysisStoreAndRegion(authRegionStoreMap, regionVOSMap, storeVOSMap);
        //构造返回结果
        List<SelectComponentUserVO> results = new ArrayList<>();
        for (EnterpriseUserDO enterpriseUserDO : userDOS) {
            SelectComponentUserVO selectComponentUserVo = new SelectComponentUserVO();
            selectComponentUserVo.setUserId(enterpriseUserDO.getUserId());
            selectComponentUserVo.setUserName(enterpriseUserDO.getName());
            selectComponentUserVo.setAvatar(StrUtil.isBlank(enterpriseUserDO.getFaceUrl()) ? enterpriseUserDO.getAvatar() : enterpriseUserDO.getFaceUrl());
            selectComponentUserVo.setJobNumber(enterpriseUserDO.getJobnumber());
            if (CollectionUtils.isNotEmpty(userRoles.get(enterpriseUserDO.getUserId()))) {
                selectComponentUserVo.setPositionInfo(userRoles.get(enterpriseUserDO.getUserId()).get(0));
            }
            selectComponentUserVo.setRegionVos(regionVOSMap.get(enterpriseUserDO.getUserId()));
            selectComponentUserVo.setStoreVos(storeVOSMap.get(enterpriseUserDO.getUserId()));
            results.add(selectComponentUserVo);
        }
        return results;
    }

    /**
     * 解析authRegionStoreMap 提取门店和区域的信息
     *
     * @param authRegionStoreMap
     * @param regionVOSMap
     * @param storeVOSMap
     */
    public void analysisStoreAndRegion(Map<String, AuthRegionStoreDTO> authRegionStoreMap, Map<String, List<SelectComponentRegionVO>> regionVOSMap,
                                       Map<String, List<SelectComponentStoreVO>> storeVOSMap) {
        authRegionStoreMap.forEach((k, v) -> {
            List<SelectComponentRegionVO> regionVos = new ArrayList<>();
            List<SelectComponentStoreVO> storeVos = new ArrayList<>();
            v.getAuthRegionStoreUserList().forEach(auth -> {
                if (auth.getStoreFlag()) {
                    SelectComponentStoreVO storeVo = new SelectComponentStoreVO();
                    storeVo.setStoreId(auth.getId());
                    storeVo.setName(auth.getName());
                    storeVo.setStoreStatus(auth.getStoreStatus());
                    storeVos.add(storeVo);
                } else {
                    SelectComponentRegionVO regionVo = new SelectComponentRegionVO();
                    regionVo.setId(auth.getId());
                    regionVo.setName(auth.getName());
                    regionVos.add(regionVo);
                }
            });
            storeVOSMap.put(k, storeVos);
            regionVOSMap.put(k, regionVos);
        });
    }

    /**
     * 填充allRegionList门店类型区域中的门店相关信息
     *
     * @param allRegionList
     * @param eid
     */
    private void fillAllRegionListStoreInfo(List<SelectComponentRegionVO> allRegionList, String eid) {

        List<String> storeIds = ListUtils.emptyIfNull(allRegionList)
                .stream()
                .filter(item -> RegionTypeEnum.STORE.getType().equals(item.getRegionType()) && StringUtils.isNotBlank(item.getStoreId()))
                .map(SelectComponentRegionVO::getStoreId)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(storeIds)) {
            return;
        }
        List<StoreDO> storeDOS = storeMapper.getByStoreIdList(eid, storeIds);
        Map<String, StoreDO> storeDOSMap = ListUtils.emptyIfNull(storeDOS)
                .stream()
                .collect(Collectors.toMap(StoreDO::getStoreId, Function.identity(), (r, e) -> r));

        List<SelectComponentDeviceVO> deviceVOS = new ArrayList<>();
        Map<String, List<String>> storeDeviceIdMap = new HashMap<>(16);
        if (CollectionUtils.isNotEmpty(storeIds)) {
            // 获取门店设备列表
            List<DeviceDO> deviceByStoreIdList = deviceMapper.getDeviceByStoreIdList(eid, storeIds, DeviceTypeEnum.DEVICE_VIDEO.getCode(), null, null);
            storeDeviceIdMap = ListUtils.emptyIfNull(deviceByStoreIdList).stream()
                    .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
                    .collect(Collectors.groupingBy(DeviceDO::getBindStoreId,
                            Collectors.mapping(DeviceDO::getDeviceId, Collectors.toList())));
            List<String> deviceIds = ListUtils.emptyIfNull(deviceByStoreIdList).stream()
                    .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
                    .map(DeviceDO::getDeviceId)
                    .collect(Collectors.toList());
            List<DeviceDO> devices = deviceMapper.getDeviceByDeviceIdList(eid, deviceIds);
            //DO转换Vo
            deviceVOS = fetchDeviceVOByDeviceDOS(devices);
        }
        Map<String, Integer> storeUsers = getStoreUsers(storeIds, eid);
        // 填充门店与设备的关联数据
        for (SelectComponentRegionVO selectComponentRegionVO : allRegionList) {
            StoreDO storeDO = storeDOSMap.get(selectComponentRegionVO.getStoreId());
            if (storeDO == null) {
                continue;
            }
            selectComponentRegionVO.setAddress(storeDO.getStoreAddress());
            List<String> devices = storeDeviceIdMap.get(storeDO.getStoreId());
            if (CollUtil.isEmpty(devices)) {
                selectComponentRegionVO.setDevices(new ArrayList<>());
            } else {
                selectComponentRegionVO.setDevices(deviceVOS.stream().filter(d -> devices.contains(d.getDeviceId())).collect(Collectors.toList()));
            }
            if (CollectionUtils.isEmpty(selectComponentRegionVO.getDevices())) {
                selectComponentRegionVO.setHasCamera(Boolean.FALSE);
            } else {
                selectComponentRegionVO.setHasCamera(Boolean.TRUE);
            }
            selectComponentRegionVO.setUserCount(Objects.isNull(storeUsers.get(storeDO.getStoreId())) ? 0 : storeUsers.get(storeDO.getStoreId()));
        }
    }
}
