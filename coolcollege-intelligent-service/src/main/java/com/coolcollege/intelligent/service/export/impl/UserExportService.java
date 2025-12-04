package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.SubordinateMappingDAO;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupDao;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupMappingDao;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.enterprise.SubordinateMappingDO;
import com.coolcollege.intelligent.model.enterprise.UserRegionMappingDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.dto.HistoryEnterpriseUserInfoExportDTO;
import com.coolcollege.intelligent.model.export.dto.UserInfoExportDTO;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.export.request.UserInfoExportRequest;
import com.coolcollege.intelligent.model.region.dto.AuthRegionStoreDTO;
import com.coolcollege.intelligent.model.region.dto.AuthRegionStoreUserDTO;
import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupDO;
import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupMappingDO;
import com.coolcollege.intelligent.model.usergroup.request.UserGroupExportRequest;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户导出service
 * @author ：xugangkun
 * @date ：2021/7/23 10:22
 */
@Service
@Slf4j
public class UserExportService implements BaseExportService {

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Resource
    private EnterpriseUserService enterpriseUserService;

    @Resource
    private AuthVisualService authVisualService;

    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private SubordinateMappingDAO subordinateMappingDAO;
    @Resource
    private EnterpriseUserGroupMappingDao enterpriseUserGroupMappingDao;
    @Resource
    private EnterpriseUserGroupDao enterpriseUserGroupDao;
    @Resource
    private UserRegionMappingDAO userRegionMappingDAO;
    @Resource
    private RegionService regionService;
    @Resource
    private RedisUtilPool redisUtilPool;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        UserInfoExportRequest userInfoExportRequest = (UserInfoExportRequest)request;
        String deptId = userInfoExportRequest.getDeptId();
        String userName = userInfoExportRequest.getUserName();
        String jobNumber = userInfoExportRequest.getJobNumber();
        String regionId = userInfoExportRequest.getRegionId();
        Long roleId = userInfoExportRequest.getRoleId();
        Integer userStatus = userInfoExportRequest.getUserStatus();
        Integer total = 0;
        if(roleId != null){
            total = enterpriseUserMapper.countUserByRole(enterpriseId, deptId, roleId, userName, jobNumber, userStatus, regionId);
        }else {
            total = enterpriseUserMapper.countUserByNotRole(enterpriseId, deptId, userName, jobNumber, userStatus, regionId);
        }
        return Long.valueOf(total);
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_USER_INFO;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        String isHistory = redisUtilPool.hashGet(RedisConstant.HISTORY_ENTERPRISE, enterpriseId);
        Boolean isHistoryEnterprise = StringUtils.isNotBlank(isHistory);
        List<UserInfoExportDTO> result = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        UserInfoExportRequest userInfoExportRequest = JSONObject.toJavaObject(request, UserInfoExportRequest.class);
        String deptId = userInfoExportRequest.getDeptId();
        String userName = userInfoExportRequest.getUserName();
        String jobNumber = userInfoExportRequest.getJobNumber();
        String regionId = userInfoExportRequest.getRegionId();
        Long roleId = userInfoExportRequest.getRoleId();
        Integer userStatus = userInfoExportRequest.getUserStatus();
        String mobile = userInfoExportRequest.getMobile();
        Integer userType = userInfoExportRequest.getUserType();
        //填充用户角色
        List<EnterpriseUserDTO> enterpriseUserList = Lists.newArrayList();
        if(roleId != null){
            enterpriseUserList = enterpriseUserMapper.fuzzyUsersByDepartment(enterpriseId, deptId, roleId, null, null, userName, jobNumber, userStatus, null,regionId, mobile, userType);
        }else {
            enterpriseUserList= enterpriseUserMapper.fuzzyUsersByNotRole(enterpriseId, deptId, null, null,  userName, jobNumber, userStatus, null,regionId, mobile, userType);
        }
        List<String> userIds = enterpriseUserService.initUserRole(enterpriseId, enterpriseUserList);
        // 获取创建人id对应名称map
        List<String> createIdList = enterpriseUserList
                .stream().map(EnterpriseUserDTO::getCreateUserId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        Map<String, String> createUserNameMap = enterpriseUserService.getUserNameMap(enterpriseId, createIdList);
        //转换对象
        enterpriseUserList.forEach(user -> {
            UserInfoExportDTO exportDTO = new UserInfoExportDTO();
            BeanUtils.copyProperties(user, exportDTO);
            exportDTO.setUserStatus(UserStatusEnum.getMessageByCode(user.getUserStatus()));
            // 时间格式化
            if (user.getCreateTime() != null) {
                exportDTO.setCreateTimeStr(DateFormatUtils.format(user.getCreateTime(), DateUtils.DATE_FORMAT_SEC));
            }
            if (user.getUpdateTime() != null) {
                exportDTO.setUpdateTimeStr(DateFormatUtils.format(user.getUpdateTime(), DateUtils.DATE_FORMAT_SEC));
            }
            // 设置创建人名称
            if (StringUtils.isNotBlank(user.getCreateUserId())) {
                exportDTO.setCreateUserName(createUserNameMap.get(user.getCreateUserId()));
            }
            result.add(exportDTO);
        });
        Map<String, String> fullNameMap = new HashMap<>();
        Map<String, List<String>> userRegionMap = new HashMap<>();
        if(!isHistoryEnterprise){
            List<UserRegionMappingDO> regionMappingList = userRegionMappingDAO.getRegionIdsByUserIds(enterpriseId, userIds);
            List<Long> regionIds = regionMappingList.stream().map(o->Long.valueOf(o.getRegionId())).distinct().collect(Collectors.toList());
            fullNameMap = regionService.getFullNameByRegionIds(enterpriseId, regionIds, Constants.SPRIT);
            userRegionMap = regionMappingList.stream().collect(Collectors.groupingBy(k -> k.getUserId(), Collectors.mapping(v -> v.getRegionId(), Collectors.toList())));
        }
        List<EnterpriseUserGroupMappingDO> enterpriseUserGroupMapping = enterpriseUserGroupMappingDao.listByUserIdList(enterpriseId, userIds);
        List<String> groupIds = ListUtils.emptyIfNull(enterpriseUserGroupMapping).stream().map(EnterpriseUserGroupMappingDO::getGroupId).distinct().collect(Collectors.toList());
        Map<String, List<String>> userGroupList = ListUtils.emptyIfNull(enterpriseUserGroupMapping).stream().collect(Collectors.groupingBy(EnterpriseUserGroupMappingDO::getUserId, Collectors.mapping(k->k.getGroupId(), Collectors.toList())));
        List<EnterpriseUserGroupDO> enterpriseUserGroup = enterpriseUserGroupDao.listByGroupIdList(enterpriseId, groupIds);
        Map<String, String> groupNameMap= ListUtils.emptyIfNull(enterpriseUserGroup).stream().collect(Collectors.toMap(k->k.getGroupId(), v->v.getGroupName(), (k1, k2)->k1));
        //获取用户所有门店和区域权限
        List<AuthRegionStoreDTO> authRegionStoreDTOList = authVisualService.authRegionStoreByUserList(enterpriseId, userIds);
        List<SubordinateMappingDO> directSuperiorList = subordinateMappingDAO.selectByUserIdsAndType(enterpriseId, userIds);
        Map<String, String> userDirectMap = directSuperiorList.stream().collect(Collectors.toMap(k -> k.getUserId(), v -> v.getPersonalId(), (k1, k2) -> k1));
        List<String> directUserIds = ListUtils.emptyIfNull(directSuperiorList).stream().map(SubordinateMappingDO::getPersonalId).distinct().collect(Collectors.toList());
        Map<String, String> directUserNameMap = enterpriseUserService.getUserNameMap(enterpriseId, directUserIds);
        Map<String, List<AuthRegionStoreUserDTO>> userAuthMap = authRegionStoreDTOList.stream()
                .collect(Collectors.toMap(AuthRegionStoreDTO::getUserId, AuthRegionStoreDTO::getAuthRegionStoreUserList, (a, b) -> a));
            Map<String, List<String>> finalUserRegionMap = userRegionMap;
            Map<String, String> finalFullNameMap = fullNameMap;
        result.forEach(user -> {
            String directUserId = userDirectMap.get(user.getUserId());
            if(StringUtils.isNotBlank(directUserId)){
                String directSuperior = directUserNameMap.get(directUserId);
                user.setDirectSuperior(directUserId + "(" + directSuperior + ")");
            }
            List<String> userGroupIds = userGroupList.get(user.getUserId());
            user.setUserGroups(getUserGroupName(userGroupIds, groupNameMap));
            if(!isHistoryEnterprise){
                user.setUserRegions(getUserRegionName(finalUserRegionMap.get(user.getUserId()), finalFullNameMap));
            }
            //填充用户的门店和区域信息
            List<AuthRegionStoreUserDTO> userAuthList = userAuthMap.get(user.getUserId());
            if (userAuthList == null) {
                return;
            }
            userAuthList.removeAll(Collections.singleton(null));
            if (userAuthList.size() == 0) {
                return;
            }
            //捞出门店列表
            List<AuthRegionStoreUserDTO> userStores = userAuthList.stream().filter(e -> e.getStoreId() != null).collect(Collectors.toList());
            //移除门店后,剩下的是区域列表
            userAuthList.removeAll(userStores);
            String storeName = userStores.stream().map(AuthRegionStoreUserDTO::getName).collect(Collectors.joining(","));
            String regionName = userAuthList.stream().map(AuthRegionStoreUserDTO::getName).collect(Collectors.joining(","));
            user.setStoreName(storeName);
            user.setRegionName(regionName);
        });
        if(isHistoryEnterprise){
            return HistoryEnterpriseUserInfoExportDTO.convertList(result);
        }
        return result;
    }

    public List<UserInfoExportDTO> exportUserList(String enterpriseId, UserInfoExportRequest request, int pageSize, int pageNum){
        String deptId = request.getDeptId();
        String userName = request.getUserName();
        String jobNumber = request.getJobNumber();
        String regionId = request.getRegionId();
        Long roleId = request.getRoleId();
        Integer userStatus = request.getUserStatus();
        String mobile = request.getMobile();
        String orderBy = request.getOrderBy();
        String orderRule = request.getOrderRule();
        Integer userType = request.getUserType();
        //填充用户角色
        List<EnterpriseUserDTO> enterpriseUserList = Lists.newArrayList();
        PageHelper.startPage(pageNum, pageSize);
        if(roleId != null){
            enterpriseUserList = enterpriseUserMapper.fuzzyUsersByDepartment(enterpriseId, deptId, roleId, orderBy, orderRule, userName, jobNumber, userStatus, null,regionId, mobile, userType);
        }else {
            enterpriseUserList= enterpriseUserMapper.fuzzyUsersByNotRole(enterpriseId, deptId, orderBy, orderRule,  userName, jobNumber, userStatus, null,regionId, mobile, userType);
        }
        List<String> userIds = enterpriseUserService.initUserRole(enterpriseId, enterpriseUserList);
        List<UserInfoExportDTO> result = new ArrayList<>();
        //转换对象
        enterpriseUserList.forEach(user -> {
            UserInfoExportDTO exportDTO = new UserInfoExportDTO();
            BeanUtils.copyProperties(user, exportDTO);
            exportDTO.setUserStatus(UserStatusEnum.getMessageByCode(user.getUserStatus()));
            result.add(exportDTO);
        });
        List<UserRegionMappingDO> regionMappingList = userRegionMappingDAO.getRegionIdsByUserIds(enterpriseId, userIds);
        List<Long> regionIds = regionMappingList.stream().map(o->Long.valueOf(o.getRegionId())).distinct().collect(Collectors.toList());
        Map<String, String> fullNameMap = regionService.getFullNameByRegionIds(enterpriseId, regionIds, Constants.SPRIT);
        Map<String, List<String>> userRegionMap = regionMappingList.stream().collect(Collectors.groupingBy(k -> k.getUserId(), Collectors.mapping(v -> v.getRegionId(), Collectors.toList())));
        List<EnterpriseUserGroupMappingDO> enterpriseUserGroupMapping = enterpriseUserGroupMappingDao.listByUserIdList(enterpriseId, userIds);
        List<String> groupIds = ListUtils.emptyIfNull(enterpriseUserGroupMapping).stream().map(EnterpriseUserGroupMappingDO::getGroupId).distinct().collect(Collectors.toList());
        Map<String, List<String>> userGroupList = ListUtils.emptyIfNull(enterpriseUserGroupMapping).stream().collect(Collectors.groupingBy(EnterpriseUserGroupMappingDO::getUserId, Collectors.mapping(k->k.getGroupId(), Collectors.toList())));
        List<EnterpriseUserGroupDO> enterpriseUserGroup = enterpriseUserGroupDao.listByGroupIdList(enterpriseId, groupIds);
        Map<String, String> groupNameMap= ListUtils.emptyIfNull(enterpriseUserGroup).stream().collect(Collectors.toMap(k->k.getGroupId(), v->v.getGroupName(), (k1, k2)->k1));
        //获取用户所有门店和区域权限
        List<AuthRegionStoreDTO> authRegionStoreDTOList = authVisualService.authRegionStoreByUserList(enterpriseId, userIds);
        List<SubordinateMappingDO> directSuperiorList = subordinateMappingDAO.selectByUserIdsAndType(enterpriseId, userIds);
        Map<String, String> userDirectMap = directSuperiorList.stream().collect(Collectors.toMap(k -> k.getUserId(), v -> v.getPersonalId(), (k1, k2) -> k1));
        List<String> directUserIds = ListUtils.emptyIfNull(directSuperiorList).stream().map(SubordinateMappingDO::getPersonalId).distinct().collect(Collectors.toList());
        Map<String, String> directUserNameMap = enterpriseUserService.getUserNameMap(enterpriseId, directUserIds);
        Map<String, List<AuthRegionStoreUserDTO>> userAuthMap = authRegionStoreDTOList.stream()
                .collect(Collectors.toMap(AuthRegionStoreDTO::getUserId, AuthRegionStoreDTO::getAuthRegionStoreUserList, (a, b) -> a));
        result.forEach(user -> {
            String directUserId = userDirectMap.get(user.getUserId());
            if(StringUtils.isNotBlank(directUserId)){
                String directSuperior = directUserNameMap.get(directUserId);
                user.setDirectSuperior(directUserId + "(" + directSuperior + ")");
            }
            List<String> userGroupIds = userGroupList.get(user.getUserId());
            user.setUserGroups(getUserGroupName(userGroupIds, groupNameMap));
            user.setUserRegions(getUserRegionName(userRegionMap.get(user.getUserId()), fullNameMap));
            //填充用户的门店和区域信息
            List<AuthRegionStoreUserDTO> userAuthList = userAuthMap.get(user.getUserId());
            if (userAuthList == null) {
                return;
            }
            userAuthList.removeAll(Collections.singleton(null));
            if (userAuthList.size() == 0) {
                return;
            }
            //捞出门店列表
            List<AuthRegionStoreUserDTO> userStores = userAuthList.stream().filter(e -> e.getStoreId() != null).collect(Collectors.toList());
            //移除门店后,剩下的是区域列表
            userAuthList.removeAll(userStores);
            String storeName = userStores.stream().map(AuthRegionStoreUserDTO::getName).collect(Collectors.joining(","));
            String regionName = userAuthList.stream().map(AuthRegionStoreUserDTO::getName).collect(Collectors.joining(","));
            user.setStoreName(storeName);
            user.setRegionName(regionName);
        });
        return result;
    }

    public String getUserGroupName(List<String> userGroupIds, Map<String, String> groupNameMap){
        StringBuilder groupName = new StringBuilder("");
        ListUtils.emptyIfNull(userGroupIds).forEach(groupId->{
            String name = groupNameMap.get(groupId);
            if(StringUtils.isBlank(name)){
                return;
            }
            groupName.append(name).append(Constants.COMMA);
        });
        if(groupName.length() > 0){
            return groupName.substring(0, groupName.length()-1);
        }
        return groupName.toString();
    }

    /**
     * 获取区域名称
     * @param userRegionIds
     * @param fullNameMap
     * @return
     */
    public String getUserRegionName(List<String> userRegionIds, Map<String, String> fullNameMap){
        StringBuilder regionName = new StringBuilder("");
        ListUtils.emptyIfNull(userRegionIds).forEach(regionId->{
            String name = fullNameMap.get(regionId);
            if(StringUtils.isBlank(name)){
                return;
            }
            String regionPathName = name.substring(Constants.INDEX_ONE, name.length() - Constants.INDEX_ONE);
            regionName.append(regionPathName).append(Constants.COMMA);
        });
        if(regionName.length() > 0){
            return regionName.substring(0, regionName.length()-Constants.INDEX_ONE);
        }
        return regionName.toString();
    }
}
