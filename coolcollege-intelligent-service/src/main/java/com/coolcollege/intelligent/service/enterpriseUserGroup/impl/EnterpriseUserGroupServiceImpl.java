package com.coolcollege.intelligent.service.enterpriseUserGroup.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserDepartmentMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupDao;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupMappingDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.SubordinateUserRangeDTO;
import com.coolcollege.intelligent.model.region.dto.AuthRegionStoreDTO;
import com.coolcollege.intelligent.model.region.dto.AuthStoreCountDTO;
import com.coolcollege.intelligent.model.user.dto.UserSimpleDTO;
import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupDO;
import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupMappingDO;
import com.coolcollege.intelligent.model.usergroup.dto.UserGroupDTO;
import com.coolcollege.intelligent.model.usergroup.request.UserGroupAddRequest;
import com.coolcollege.intelligent.model.usergroup.request.UserGroupRemoveRequest;
import com.coolcollege.intelligent.model.usergroup.vo.UserGroupVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.SubordinateMappingService;
import com.coolcollege.intelligent.service.enterpriseUserGroup.EnterpriseUserGroupService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author wxp
 * @Date 2022/12/29 11:18
 * @Version 1.0
 */
@Service
@Slf4j
public class EnterpriseUserGroupServiceImpl implements EnterpriseUserGroupService {

    @Resource
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;

    @Resource
    private EnterpriseUserGroupDao enterpriseUserGroupDao;

    @Resource
    private EnterpriseUserGroupMappingDao enterpriseUserGroupMappingDao;

    @Autowired
    public EnterpriseUserService enterpriseUserService;

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private AuthVisualService visualService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SubordinateMappingService subordinateMappingService;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Override
    public Boolean saveOrUpdateUserGroup(String enterpriseId, UserGroupAddRequest userGroupAddRequest, CurrentUser currentUser) {
        int count = enterpriseUserGroupDao.countByGroupName(enterpriseId, userGroupAddRequest.getGroupName(), userGroupAddRequest.getGroupId());
        if (count > 0) {
            throw new ServiceException(ErrorCodeEnum.USER_GROUP_NAME_EXIST);
        }
        EnterpriseUserGroupDO userGroupDO =  translateToUserGroupDO(userGroupAddRequest, currentUser);
        if(StringUtils.isBlank(userGroupAddRequest.getGroupId())){
            enterpriseUserGroupDao.insertSelective(userGroupDO, enterpriseId);
        }else {
            enterpriseUserGroupDao.updateByGroupId(userGroupDO, enterpriseId);
        }
        List<String> userIdList = userGroupAddRequest.getUserIdList();
        if(CollectionUtils.isEmpty(userIdList)){
            return Boolean.TRUE;
        }
        enterpriseUserGroupMappingDao.deleteUserGroupMappingByGroupId(enterpriseId, userGroupDO.getGroupId());
        List<EnterpriseUserGroupMappingDO> userGroupMappingDOList = new ArrayList<>();
        for (String userId: userIdList) {
            EnterpriseUserGroupMappingDO userGroupMappingDO = new EnterpriseUserGroupMappingDO();
            userGroupMappingDO.setUserId(userId);
            userGroupMappingDO.setGroupId(userGroupDO.getGroupId());
            userGroupMappingDO.setCreateUserId(currentUser.getUserId());
            userGroupMappingDO.setUpdateUserId(currentUser.getUserId());
            userGroupMappingDOList.add(userGroupMappingDO);
        }
        if (CollectionUtils.isNotEmpty(userGroupMappingDOList)) {
            enterpriseUserGroupMappingDao.batchInsertOrUpdateUserGroupMapping(enterpriseId, userGroupMappingDOList);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateUserGroup(String enterpriseId, String userGroupId, List<String> userIdList){
        log.info("沪上接口更新用户分组groupId:{} storeIds:{}",userGroupId, JSONObject.toJSONString(userIdList));
        if (StringUtils.isEmpty(userGroupId)){
            throw  new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        enterpriseUserGroupMappingDao.deleteUserGroupMappingByGroupId(enterpriseId,userGroupId);
        if (CollectionUtils.isEmpty(userIdList)){
            return Boolean.TRUE;
        }
        List<EnterpriseUserGroupMappingDO> userGroupMappingDOList = new ArrayList<>();
        for (String userId: userIdList) {
            EnterpriseUserGroupMappingDO userGroupMappingDO = new EnterpriseUserGroupMappingDO();
            userGroupMappingDO.setUserId(userId);
            userGroupMappingDO.setGroupId(userGroupId);
            userGroupMappingDO.setCreateUserId("a100000001");
            userGroupMappingDO.setUpdateUserId("a100000001");
            userGroupMappingDOList.add(userGroupMappingDO);
        }
        if (CollectionUtils.isNotEmpty(userGroupMappingDOList)) {
            enterpriseUserGroupMappingDao.batchInsertOrUpdateUserGroupMapping(enterpriseId, userGroupMappingDOList);
        }
        return Boolean.TRUE;
    }

    @Override
    public void batchDeleteGroup(String enterpriseId, String groupId, List<String> userIdList) {
        enterpriseUserGroupMappingDao.deleteMappingByGroupIdList(enterpriseId, groupId, userIdList);
        if(CollectionUtils.isEmpty(userIdList)){
            enterpriseUserGroupDao.deleteByGroupIdList(enterpriseId, Collections.singletonList(groupId));
        }
    }

    @Override
    public List<UserGroupVO> listUserGroup(String enterpriseId, String groupName, CurrentUser user) {
        List<EnterpriseUserGroupDO> userGroupDOList = enterpriseUserGroupDao.listUserGroup(enterpriseId,groupName);
        List<UserGroupVO> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(userGroupDOList)) {
            return resultList;
        }
        List<String> groupIdList = userGroupDOList.stream().map(EnterpriseUserGroupDO::getGroupId).collect(Collectors.toList());
        Set<String> userIdSet = userGroupDOList.stream()
                .flatMap(c->Stream.of(c.getCreateUserId(),c.getUpdateUserId()))
                .collect(Collectors.toSet());
        Map<String, EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(enterpriseId, new ArrayList<>(userIdSet));
        List<EnterpriseUserGroupMappingDO> userGroupMappingDOList = enterpriseUserGroupMappingDao.listByGroupIdList(enterpriseId, groupIdList);
        Map<String, List<EnterpriseUserGroupMappingDO>> userGroupMappingMap = userGroupMappingDOList.stream()
                .collect(Collectors.groupingBy(EnterpriseUserGroupMappingDO::getGroupId));
        for (EnterpriseUserGroupDO enterpriseUserGroupDO : userGroupDOList) {
            UserGroupVO userGroupVO = new UserGroupVO();
            userGroupVO.setCreateUserId(enterpriseUserGroupDO.getCreateUserId());
            userGroupVO.setGroupId(enterpriseUserGroupDO.getGroupId());
            userGroupVO.setGroupName(enterpriseUserGroupDO.getGroupName());
            userGroupVO.setCreateTime(enterpriseUserGroupDO.getCreateTime());
            userGroupVO.setUpdateTime(enterpriseUserGroupDO.getUpdateTime());
            EnterpriseUserDO createUser = userMap.get(enterpriseUserGroupDO.getCreateUserId());
            if (createUser != null) {
                userGroupVO.setCreateUserName(createUser.getName());
            }
            EnterpriseUserDO updateUser = userMap.get(enterpriseUserGroupDO.getUpdateUserId());
            if (updateUser != null) {
                userGroupVO.setUpdateUserName(updateUser.getName());
            }
            if(!CollectionUtils.isEmpty(userGroupMappingMap.get(enterpriseUserGroupDO.getGroupId()))){
                userGroupVO.setUserCount(userGroupMappingMap.get(enterpriseUserGroupDO.getGroupId()).size());
            }
            userGroupVO.setEditFlag(checkUserEditFlag(enterpriseId, enterpriseUserGroupDO, user.getUserId()));
            resultList.add(userGroupVO);
        }
        return resultList;
    }

    @Override
    public UserGroupVO getGroupInfo(String enterpriseId, String groupId, CurrentUser user) {
        EnterpriseUserGroupDO userGroupDO = enterpriseUserGroupDao.getByGroupId(enterpriseId, groupId);
        if (userGroupDO == null) {
            throw new ServiceException(ErrorCodeEnum.USER_GROUP_NOT_EXIST);
        }
        UserGroupVO userGroupVO = new UserGroupVO();
        BeanUtils.copyProperties(userGroupDO, userGroupVO);
        fillUserGroupVO(enterpriseId, userGroupDO, userGroupVO);
        userGroupVO.setEditFlag(checkUserEditFlag(enterpriseId, userGroupDO, user.getUserId()));
        return userGroupVO;
    }

    @Override
    public PageInfo<EnterpriseUserDTO> listUserByGroupId(String enterpriseId, String groupId, String userName, Integer pageNum, Integer pageSize, CurrentUser currentUser) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        PageHelper.startPage(pageNum,pageSize);
        List<EnterpriseUserDTO> enterpriseUserList = new ArrayList<>();
        if (AppTypeEnum.isQwType(config.getAppType())) {
            List<String> userIdList = new ArrayList<>();
            if (StringUtils.isNotBlank(userName)) {
                userIdList = chatService.searchUserOrDeptByName(config.getDingCorpId(), config.getAppType(), userName, Constants.ONE_VALUE_STRING, pageNum, pageSize).getKey();
                if (CollectionUtils.isEmpty(userIdList)) {
                    return new PageInfo<>(enterpriseUserList);
                }
            }
            enterpriseUserList = enterpriseUserMapper.listUserByGroupId(enterpriseId, groupId, userName, userIdList);
        } else {
            enterpriseUserList = enterpriseUserMapper.listUserByGroupId(enterpriseId, groupId, userName, null);
        }
        PageInfo pageInfo = new PageInfo<>(enterpriseUserList);
        if (CollectionUtils.isEmpty(enterpriseUserList)) {
            return pageInfo;
        }
        //填充角色信息如果存在角色信息
        List<String> userIdList = enterpriseUserService.initUserRole(enterpriseId, enterpriseUserList);
        List<AuthRegionStoreDTO> authRegionStoreDTOList = visualService.authRegionStoreByUserList(enterpriseId, userIdList);
        Map<String, AuthRegionStoreDTO> authRegionStoreMap = ListUtils.emptyIfNull(authRegionStoreDTOList)
                .stream()
                .collect(Collectors.toMap(AuthRegionStoreDTO::getUserId, data -> data, (a, b) -> a));
        Map<String, SubordinateUserRangeDTO> subordinateUserRangeMap = enterpriseUserService.fillUserSubordinateNames(enterpriseId, userIdList);
        Map<String, String> userRegionMap = enterpriseUserService.getUserRegion(enterpriseId, userIdList);

        Boolean haveAllSubordinateUser = subordinateMappingService.checkHaveAllSubordinateUser(enterpriseId, currentUser.getUserId());
        List<String> userSubordinateList = Lists.newArrayList();
        if(!haveAllSubordinateUser){
            userSubordinateList = subordinateMappingService.getSubordinateUserIdList(enterpriseId, currentUser.getUserId(),Boolean.TRUE);
        }
        List<String> finalUserSubordinateList = userSubordinateList;
        enterpriseUserList.stream()
                .forEach(data->{
                    if(MapUtils.isNotEmpty(authRegionStoreMap)&&authRegionStoreMap.get(data.getUserId())!=null){
                        AuthRegionStoreDTO authRegionStoreDTO = authRegionStoreMap.get(data.getUserId());
                        data.setAuthRegionStoreList(authRegionStoreDTO.getAuthRegionStoreUserList());
                    }
                    // 填充下属用户
                    if (subordinateUserRangeMap.get(data.getUserId()) != null){
                        data.setSubordinateUserRange(subordinateUserRangeMap.get(data.getUserId()).getSubordinateUserRange());
                        data.setSourceList(subordinateUserRangeMap.get(data.getUserId()).getSourceList());
                        data.setMySubordinates(subordinateUserRangeMap.get(data.getUserId()).getMySubordinates());
                    }
                    data.setDepartment(userRegionMap.get(data.getUserId()));
                    if(haveAllSubordinateUser){
                        data.setSelectFlag(true);
                    }else {
                        data.setSelectFlag(finalUserSubordinateList.contains(data.getUserId()));
                    }
                });
        return pageInfo;
    }

    @Override
    public void updateUserGroup(String enterpriseId, List<String> groupIdList, String userId, CurrentUser currentUser) {
        if(CollectionUtils.isEmpty(groupIdList)){
            return;
        }
        enterpriseUserGroupMappingDao.deleteMappingByUserIdList(enterpriseId, Collections.singletonList(userId));
        List<EnterpriseUserGroupMappingDO> userGroupMappingDOList = new ArrayList<>();
        for (String groupId: groupIdList) {
            EnterpriseUserGroupMappingDO userGroupMappingDO = new EnterpriseUserGroupMappingDO();
            userGroupMappingDO.setUserId(userId);
            userGroupMappingDO.setGroupId(groupId);
            userGroupMappingDO.setCreateUserId(currentUser.getUserId());
            userGroupMappingDO.setUpdateUserId(currentUser.getUserId());
            userGroupMappingDOList.add(userGroupMappingDO);
        }
        if (CollectionUtils.isNotEmpty(userGroupMappingDOList)) {
            enterpriseUserGroupMappingDao.batchInsertOrUpdateUserGroupMapping(enterpriseId, userGroupMappingDOList);
        }
    }

    @Override
    public Boolean configUser(String enterpriseId, UserGroupAddRequest userGroupAddRequest, CurrentUser currentUser) {
        EnterpriseUserGroupDO userGroupDO = enterpriseUserGroupDao.getByGroupId(enterpriseId, userGroupAddRequest.getGroupId());
        if (userGroupDO == null) {
            throw new ServiceException(ErrorCodeEnum.USER_GROUP_NOT_EXIST);
        }
        List<String> userIdList = userGroupAddRequest.getUserIdList();
        if(CollectionUtils.isEmpty(userIdList)){
            return Boolean.TRUE;
        }
        List<EnterpriseUserGroupMappingDO> userGroupMappingDOList = new ArrayList<>();
        for (String userId: userIdList) {
            EnterpriseUserGroupMappingDO userGroupMappingDO = new EnterpriseUserGroupMappingDO();
            userGroupMappingDO.setUserId(userId);
            userGroupMappingDO.setGroupId(userGroupDO.getGroupId());
            userGroupMappingDO.setCreateUserId(currentUser.getUserId());
            userGroupMappingDO.setUpdateUserId(currentUser.getUserId());
            userGroupMappingDOList.add(userGroupMappingDO);
        }
        if (CollectionUtils.isNotEmpty(userGroupMappingDOList)) {
            enterpriseUserGroupMappingDao.batchInsertOrUpdateUserGroupMapping(enterpriseId, userGroupMappingDOList);
        }
        return Boolean.TRUE;
    }

    @Override
    public Map<String, List<UserGroupDTO>> getUserGroupMap(String enterpriseId, List<String> userIdList) {
        Map<String, List<UserGroupDTO>> resultMap = Maps.newHashMap();
        if(CollectionUtils.isEmpty(userIdList)){
            return resultMap;
        }
        List<EnterpriseUserGroupMappingDO> enterpriseUserGroupMappingDOList = enterpriseUserGroupMappingDao.listByUserIdList(enterpriseId, userIdList);
        List<String> allGroupIdList = ListUtils.emptyIfNull(enterpriseUserGroupMappingDOList)
                .stream().map(EnterpriseUserGroupMappingDO::getGroupId)
                .collect(Collectors.toList());
        List<EnterpriseUserGroupDO> userGroupDOList = enterpriseUserGroupDao.listByGroupIdList(enterpriseId, allGroupIdList);
        //封装 userId-userGroup map,以表示一个用户对应几个分组
        Map<String, Set<String>> userGroupMap = ListUtils.emptyIfNull(enterpriseUserGroupMappingDOList)
                .stream().collect(Collectors.groupingBy(EnterpriseUserGroupMappingDO::getUserId,
                        Collectors.mapping(EnterpriseUserGroupMappingDO::getGroupId, Collectors.toSet())));
        //获得分组id -分组名称的map
        Map<String, String> groupNameMap = ListUtils.emptyIfNull(userGroupDOList)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getGroupName()))
                .collect(Collectors.toMap(EnterpriseUserGroupDO::getGroupId, EnterpriseUserGroupDO::getGroupName, (a, b) -> a));
        userIdList.forEach(userId -> {
            Set<String> groupIdList = userGroupMap.get(userId);
            if (CollectionUtils.isNotEmpty(groupIdList)) {
                List<UserGroupDTO> userGroupDTOList = resultMap.get(userId);
                if (CollectionUtils.isEmpty(userGroupDTOList)) {
                    userGroupDTOList = Lists.newArrayList();
                    resultMap.put(userId, userGroupDTOList);
                }
                for (String groupId: groupIdList) {
                    UserGroupDTO userGroupDTO = new UserGroupDTO();
                    userGroupDTO.setGroupId(groupId);
                    userGroupDTO.setGroupName(groupNameMap.get(groupId));
                    userGroupDTOList.add(userGroupDTO);
                }
            }
        });
        return resultMap;
    }

    @Override
    public Boolean batchRemoveUser(String enterpriseId, UserGroupRemoveRequest userGroupRemoveRequest, CurrentUser user) {
        enterpriseUserGroupMappingDao.deleteMappingByGroupIdList(enterpriseId, userGroupRemoveRequest.getGroupId(), userGroupRemoveRequest.getUserIdList());
        return Boolean.TRUE;
    }

    @Override
    public Boolean batchExport(String enterpriseId, UserGroupRemoveRequest userGroupRemoveRequest, CurrentUser user) {
        return null;
    }

    private void fillUserGroupVO(String enterpriseId, EnterpriseUserGroupDO userGroupDO, UserGroupVO userGroupVO) {
        List<EnterpriseUserGroupMappingDO> userGroupMappingDOList = enterpriseUserGroupMappingDao.listByGroupIdList(enterpriseId, Collections.singletonList(userGroupDO.getGroupId()));
        if (CollectionUtils.isNotEmpty(userGroupMappingDOList)){
            List<String> configUserIdList = userGroupMappingDOList.stream().map(EnterpriseUserGroupMappingDO::getUserId).collect(Collectors.toList());
            List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserDao.selectByUserIds(enterpriseId, configUserIdList);
            List<UserSimpleDTO> configUserList = ListUtils.emptyIfNull(enterpriseUserDOList).stream()
                    .map(this::translateToUserSimpleDTO)
                    .collect(Collectors.toList());
            userGroupVO.setConfigUserList(configUserList);
        }
        if(StringUtils.isNotBlank(userGroupDO.getCommonEditUserids())){
            List<String> commonEditUserIdList = StrUtil.splitTrim(userGroupDO.getCommonEditUserids(), ",");
            List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserDao.selectByUserIds(enterpriseId, commonEditUserIdList);
            List<UserSimpleDTO> commonEditUserList = ListUtils.emptyIfNull(enterpriseUserDOList).stream()
                    .map(this::translateToUserSimpleDTO)
                    .collect(Collectors.toList());
            userGroupVO.setCommonEditUserList(commonEditUserList);
        }

        List<String> userIdList = Lists.newArrayList();
        userIdList.add(userGroupDO.getCreateUserId());
        userIdList.add(userGroupDO.getUpdateUserId());
        Map<String, EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(enterpriseId, userIdList);
        EnterpriseUserDO createUser = userMap.get(userGroupDO.getCreateUserId());
        if (createUser != null) {
            userGroupVO.setCreateUserName(createUser.getName());
        }
        EnterpriseUserDO updateUser = userMap.get(userGroupDO.getUpdateUserId());
        if (updateUser != null) {
            userGroupVO.setUpdateUserName(updateUser.getName());
        }
        if (Constants.SYSTEM_USER_ID.equals(userGroupDO.getCreateUserId())) {
            userGroupVO.setCreateUserName(Constants.SYSTEM_USER_NAME);
        }
    }

    private UserSimpleDTO translateToUserSimpleDTO(EnterpriseUserDO enterpriseUserDO) {
        UserSimpleDTO userSimpleDTO = new UserSimpleDTO();
        userSimpleDTO.setUserId(enterpriseUserDO.getUserId());
        userSimpleDTO.setUserName(enterpriseUserDO.getName());
        return userSimpleDTO;
    }

    public EnterpriseUserGroupDO translateToUserGroupDO(UserGroupAddRequest request, CurrentUser user) {
        EnterpriseUserGroupDO userGroupDO = new EnterpriseUserGroupDO();
        String groupId = StringUtils.isNotBlank(request.getGroupId()) ? request.getGroupId() : UUIDUtils.get32UUID();
        userGroupDO.setGroupId(groupId);
        userGroupDO.setGroupName(request.getGroupName());
        if(CollectionUtils.isNotEmpty(request.getCommonEditUserIdList())){
            userGroupDO.setCommonEditUserids(Constants.COMMA + StringUtils.join(request.getCommonEditUserIdList(), Constants.COMMA) + Constants.COMMA);
        }else {
            userGroupDO.setCommonEditUserids("");
        }
        if(StringUtils.isNotBlank(request.getGroupId())){
            userGroupDO.setUpdateTime(new Date());
            userGroupDO.setUpdateUserId(user.getUserId());
        }else {
            userGroupDO.setCreateTime(new Date());
            userGroupDO.setCreateUserId(user.getUserId());
        }
        return userGroupDO;
    }


    // 校验用户是否有编辑权限
    public Boolean checkUserEditFlag(String eid, EnterpriseUserGroupDO enterpriseUserGroupDO, String userId){
        // 是否管理员
        boolean isAdmin = sysRoleService.checkIsAdmin(eid, userId);
        if(isAdmin || userId.equals(enterpriseUserGroupDO.getCreateUserId()) || (StringUtils.isNotBlank(enterpriseUserGroupDO.getCommonEditUserids()) && enterpriseUserGroupDO.getCommonEditUserids().contains(userId))){
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        String skipEidStr = ",a,b,c,";
        List<String> skipEidList = StrUtil.splitTrim(skipEidStr, ",");
        System.out.println(skipEidList);

        String name = StringUtils.join(skipEidList, Constants.COMMA);
        System.out.println(name);
    }


}
