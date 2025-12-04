package com.coolcollege.intelligent.service.enterprise.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.PersonTypeEnum;
import com.coolcollege.intelligent.common.enums.UserRangeTypeEnum;
import com.coolcollege.intelligent.common.enums.myj.MyjEnterpriseEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupDao;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupMappingDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserSingleDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonUsePositionDTO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.metatable.dto.PatrolMetaDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupDO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.enterprise.SubordinateMappingService;
import com.coolcollege.intelligent.service.enterprise.UserPersonInfoService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2023-01-05 14:13
 */
@Service
public class UserPersonInfoServiceImpl implements UserPersonInfoService {

    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Resource
    private EnterpriseUserGroupMappingDao enterpriseUserGroupMappingDao;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private SubordinateMappingService subordinateMappingService;

    @Resource
    private EnterpriseService enterpriseService;

    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private EnterpriseUserGroupDao enterpriseUserGroupDao;


    @Override
    public String getUserIds(String eid, String usePersonInfo, String useRange, String userId) {
        List<String> userIdList = getUserIdList(eid, usePersonInfo, useRange, userId);
        if(CollectionUtils.isEmpty(userIdList)){
            return "";
        }
        return Constants.COMMA + StringUtils.join(userIdList, Constants.COMMA) + Constants.COMMA;
    }

    @Override
    public List<String> getUserIdList(String eid, String usePersonInfo, String useRange, String userId) {
        UserRangeTypeEnum userRangeTypeEnum = UserRangeTypeEnum.getByType(useRange);
        if (userRangeTypeEnum == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        Set<String> userIdList = new HashSet<>();
        switch (userRangeTypeEnum) {
            case PART:
                List<PersonUsePositionDTO> personPositionDTOList = JSONObject.parseArray(usePersonInfo, PersonUsePositionDTO.class);
                List<String> userList = getUserList(eid, personPositionDTOList, userId);
                if(CollectionUtils.isNotEmpty(userList)){
                    userIdList.addAll(userList);
                }
                break;
            case SELF:
                userIdList.add(userId);
                break;
            default:
                break;
        }
        return new ArrayList<>(userIdList);
    }

    @Override
    public List<String> getUserNameList(String eid, String usePersonInfo, String useRange, String userId) {
        List<String> userIdList = getUserIdList(eid, usePersonInfo, useRange, userId);
        if(CollectionUtils.isEmpty(userIdList)){
            return new ArrayList<>();
        }
        //限制不能查过1000
        if(userIdList.size() > Constants.TWO_HUNDRED){
            userIdList = userIdList.subList(0,  Constants.TWO_HUNDRED - 1);
        }
        return enterpriseUserDao.selectUserNamesByUserIds(eid, userIdList);
    }

    @Override
    public List<String> getUserIdListByTaskProcess(String enterpriseId, List<TaskProcessDTO> taskProcessDTOList) {
        if (CollectionUtils.isEmpty(taskProcessDTOList)) {
            return new ArrayList<>();
        }
        Set<String> noticeUserIdList = new HashSet<>();
        taskProcessDTOList.forEach(proItem -> {
            if(proItem != null){
                List<GeneralDTO> proUserList = proItem.getUser();
                if (CollectionUtils.isNotEmpty(proUserList)) {
                    List<String> positionList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.POSITION.equals(f.getType()))
                            .map(GeneralDTO::getValue).collect(Collectors.toList());
                    List<String> nodePersonList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.PERSON.equals(f.getType()))
                            .map(GeneralDTO::getValue).collect(Collectors.toList());
                    List<String> groupIdList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.USER_GROUP.equals(f.getType()))
                            .map(GeneralDTO::getValue).collect(Collectors.toList());
                    List<String> regionIdList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.ORGANIZATION.equals(f.getType()))
                            .map(GeneralDTO::getValue).collect(Collectors.toList());
                    List<String> personIdList = this.getUserList(enterpriseId, positionList, nodePersonList, groupIdList, regionIdList);
                    if (CollectionUtils.isNotEmpty(personIdList)) {
                        noticeUserIdList.addAll(personIdList);
                    }
                }
            }
        });

        if (CollectionUtils.isEmpty(noticeUserIdList)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(noticeUserIdList);
    }

    @Override
    public List<String> getUserIdListByCommonDTO(String eid, List<StoreWorkCommonDTO> commonDTOList) {
        if (CollectionUtils.isEmpty(commonDTOList)) {
            return new ArrayList<>();
        }
        Set<String> noticeUserIdList = new HashSet<>();

        List<String> positionList = commonDTOList.stream().filter(f -> UnifyTaskConstant.PersonType.POSITION.equals(f.getType()))
                .map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        List<String> nodePersonList = commonDTOList.stream().filter(f -> UnifyTaskConstant.PersonType.PERSON.equals(f.getType()))
                .map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        List<String> groupIdList = commonDTOList.stream().filter(f -> UnifyTaskConstant.PersonType.USER_GROUP.equals(f.getType()))
                .map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        List<String> regionIdList = commonDTOList.stream().filter(f -> UnifyTaskConstant.PersonType.ORGANIZATION.equals(f.getType()))
                .map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        List<String> personIdList = this.getUserList(eid, positionList, nodePersonList, groupIdList, regionIdList);
        if (CollectionUtils.isNotEmpty(personIdList)) {
            noticeUserIdList.addAll(personIdList);
        }

        if (CollectionUtils.isEmpty(noticeUserIdList)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(noticeUserIdList);
    }

    @Override
    public PatrolMetaDTO dealMetaTableUserInfo(String enterpriseId, TbMetaTableDO table, Boolean resultViewUserWithUserRang) {
        String createUserId = table.getCreateUserId();
        //美宜佳单独去除管辖权限过滤
        if(MyjEnterpriseEnum.myjCompany(enterpriseId)){
            createUserId = null;
        }
        String usePersonInfo = filterPersonInfo(enterpriseId, table.getUsePersonInfo());
        String resultViewPersonInfo = filterPersonInfo(enterpriseId, table.getResultViewPersonInfo());
        String commonEditPersonInfo = filterPersonInfo(enterpriseId, table.getCommonEditPersonInfo());
        table.setCommonEditPersonInfo(commonEditPersonInfo);
        table.setUsePersonInfo(usePersonInfo);
        table.setResultViewPersonInfo(resultViewPersonInfo);
        // 获取用户信息
        String useRange = table.getUseRange();
        List<PersonUsePositionDTO> userPersonPositionList = null;
        List<String> useUserList = new ArrayList<>();
        boolean isUseAll = UserRangeTypeEnum.ALL.getType().equals(useRange);
        if(UserRangeTypeEnum.PART.getType().equals(useRange) && StringUtils.isNotBlank(usePersonInfo)){
            userPersonPositionList = JSONObject.parseArray(usePersonInfo, PersonUsePositionDTO.class);
            if(CollectionUtils.isNotEmpty(userPersonPositionList)){
                List<PersonUsePositionDTO> rootOrg = userPersonPositionList.stream().filter(o -> PersonTypeEnum.ORGANIZATION.getType().equals(o.getType()) && Constants.ROOT_REGION_ID.equals(o.getValue())).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(rootOrg)){
                    table.setUseRange(UserRangeTypeEnum.ALL.getType());
                    table.setUsePersonInfo("");
                    isUseAll = true;
                }else{
                    useUserList = getUserList(enterpriseId, userPersonPositionList, createUserId);
                }
            }
            useUserList.add(table.getCreateUserId());
        }else if (UserRangeTypeEnum.SELF.getType().equals(table.getResultViewRange())) {
            useUserList.add(table.getCreateUserId());
        }
        String resultViewRange = table.getResultViewRange();
        List<PersonUsePositionDTO> resultPersonPositionList = JSONObject.parseArray(resultViewPersonInfo, PersonUsePositionDTO.class);
        if(resultViewUserWithUserRang != null && resultViewUserWithUserRang){
            if(isUseAll){
                table.setResultViewRange(UserRangeTypeEnum.ALL.getType());
                table.setResultViewPersonInfo("");
            }else if(resultPersonPositionList == null){
                resultPersonPositionList = userPersonPositionList;
            }else if (CollectionUtils.isNotEmpty(userPersonPositionList)){
                resultPersonPositionList.addAll(userPersonPositionList);
            }
        }
        List<String> resultViewUserIds = new ArrayList<>();
        if(UserRangeTypeEnum.PART.getType().equals(resultViewRange) && StringUtils.isNotBlank(resultViewPersonInfo)){
            if(CollectionUtils.isNotEmpty(resultPersonPositionList)){
                List<PersonUsePositionDTO> rootOrg = resultPersonPositionList.stream().filter(o -> PersonTypeEnum.ORGANIZATION.getType().equals(o.getType()) && Constants.ROOT_REGION_ID.equals(o.getValue())).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(rootOrg)){
                    table.setResultViewRange(UserRangeTypeEnum.ALL.getType());
                    table.setResultViewPersonInfo("");
                }else{
                    resultViewUserIds = getUserList(enterpriseId, resultPersonPositionList, createUserId);
                    table.setResultViewPersonInfo(JSONObject.toJSONString(resultPersonPositionList));
                }
            }
            resultViewUserIds.add(table.getCreateUserId());
        }else if(UserRangeTypeEnum.SELF.getType().equals(resultViewRange)){
            resultViewUserIds = Collections.singletonList(table.getCreateUserId());
        }
        Set<String> commonEditUserIds = new HashSet<>();
        //共同编辑人
        if(StringUtils.isNotBlank(commonEditPersonInfo)){
            commonEditUserIds = new HashSet(getUserIdList(enterpriseId, commonEditPersonInfo, UserRangeTypeEnum.PART.getType(), createUserId));
            if (MyjEnterpriseEnum.myjCompany(enterpriseId)) {
                List<String> roleUserIdList = enterpriseUserRoleMapper.selectUserIdsByRoleId(enterpriseId, Constants.MYJ_ROLE_ID);
                commonEditUserIds.retainAll(roleUserIdList);
            }
        }
        commonEditUserIds.add(table.getCreateUserId());
        if(UserRangeTypeEnum.ALL.getType().equals(table.getUseRange())){
            useUserList = Collections.singletonList(UserRangeTypeEnum.ALL_USER_ID);;
        }
        if(UserRangeTypeEnum.ALL.getType().equals(table.getResultViewRange())){
            resultViewUserIds = Collections.singletonList(UserRangeTypeEnum.ALL_USER_ID);
        }
        return new PatrolMetaDTO(table, useUserList, new ArrayList<>(commonEditUserIds), resultViewUserIds);
    }

    @Override
    public String filterPersonInfo(String enterpriseId, String usePersonInfo) {
        if(StringUtils.isBlank(usePersonInfo)){
            return usePersonInfo;
        }
        List<PersonUsePositionDTO> personList = JSONObject.parseArray(usePersonInfo, PersonUsePositionDTO.class);
        if(CollectionUtils.isEmpty(personList)){
            return usePersonInfo;
        }
        List<PersonUsePositionDTO> resultList = new ArrayList<>();
        Map<String, List<PersonUsePositionDTO>> groupMap = personList.stream().collect(Collectors.groupingBy(PersonUsePositionDTO::getType));
        List<PersonUsePositionDTO> personInfoList = groupMap.get(PersonTypeEnum.PERSON.getType());
        List<PersonUsePositionDTO> positionInfoList = groupMap.get(PersonTypeEnum.POSITION.getType());
        List<PersonUsePositionDTO> userGroupInfoList = groupMap.get(PersonTypeEnum.USER_GROUP.getType());
        List<PersonUsePositionDTO> orgInfoList = groupMap.get(PersonTypeEnum.ORGANIZATION.getType());
        if(CollectionUtils.isNotEmpty(personInfoList)){
            List<String> userIds = personInfoList.stream().map(PersonUsePositionDTO::getValue).distinct().collect(Collectors.toList());
            List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, userIds);
            ListUtils.emptyIfNull(userList).forEach(user -> resultList.add(PersonUsePositionDTO.builder().type(PersonTypeEnum.PERSON.getType()).value(user.getUserId()).name(user.getName()).build()));
        }
        if(CollectionUtils.isNotEmpty(positionInfoList)){
            List<Long> roleIds = positionInfoList.stream().map(PersonUsePositionDTO::getValue).map(Long::valueOf).distinct().collect(Collectors.toList());
            List<SysRoleDO> roleList = sysRoleMapper.getRoleByRoleIds(enterpriseId, roleIds);
            ListUtils.emptyIfNull(roleList).forEach(role -> resultList.add(PersonUsePositionDTO.builder().type(PersonTypeEnum.POSITION.getType()).value(role.getId().toString()).name(role.getRoleName()).build()));
        }
        if(CollectionUtils.isNotEmpty(userGroupInfoList)){
            List<String> userGroupIds = userGroupInfoList.stream().map(PersonUsePositionDTO::getValue).distinct().collect(Collectors.toList());
            List<EnterpriseUserGroupDO> userGroupList = enterpriseUserGroupDao.listByGroupIdList(enterpriseId, userGroupIds);
            ListUtils.emptyIfNull(userGroupList).forEach(userGroup -> resultList.add(PersonUsePositionDTO.builder().type(PersonTypeEnum.USER_GROUP.getType()).value(userGroup.getGroupId()).name(userGroup.getGroupName()).build()));
        }
        if(CollectionUtils.isNotEmpty(orgInfoList)){
            resultList.addAll(orgInfoList);
        }
        return JSONObject.toJSONString(resultList);
    }


    private List<String> getUserList(String eid, List<String> positionIds, List<String> nodePersonList, List<String> groupIdList, List<String> regionIdList) {
        List<String> allUserIds = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(positionIds)) {
            List<String> userIds = sysRoleMapper.getPositionUserIds(eid, positionIds);
            allUserIds.addAll(userIds);
        }
        if (CollectionUtils.isNotEmpty(groupIdList)) {
            List<String> groupUserIdList = enterpriseUserGroupMappingDao.getUserIdsByGroupIdList(eid, groupIdList);
            if (CollectionUtils.isNotEmpty(groupUserIdList)) {
                allUserIds.addAll(groupUserIdList);
            }
        }
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            List<String> regionUserIdList = new ArrayList<>();
            //查看是否是老企业
            boolean historyEnterprise = enterpriseService.isHistoryEnterprise(eid);
            if (historyEnterprise) {
                regionUserIdList = enterpriseUserDao.listUserIdByDepartmentIdList(eid, regionIdList);
            } else {
                regionUserIdList = enterpriseUserDao.getUserIdsByRegionIdList(eid, regionIdList);
            }
            if (CollectionUtils.isNotEmpty(regionUserIdList)) {
                allUserIds.addAll(regionUserIdList);
            }
        }
        if (CollectionUtils.isNotEmpty(nodePersonList)) {
            allUserIds.addAll(nodePersonList);
        }
        return allUserIds;
    }

    private List<String> getUserList(String eid, List<PersonUsePositionDTO> personPositionList, String userId) {
        Set<String> userIdList = new HashSet<>();
        for (PersonUsePositionDTO personPositionDTO : personPositionList) {
            PersonTypeEnum personTypeEnum = PersonTypeEnum.getByType(personPositionDTO.getType());
            switch (personTypeEnum) {
                case PERSON:
                    userIdList.add(personPositionDTO.getValue());
                    break;
                case POSITION:
                    List<String> roleUserIdList = enterpriseUserRoleMapper.selectUserIdsByRoleId(eid, personPositionDTO.getValue());
                    if (CollectionUtils.isNotEmpty(roleUserIdList)) {
                        userIdList.addAll(roleUserIdList);
                    }
                    break;
                case USER_GROUP:
                    List<String> groupUserIdList = enterpriseUserGroupMappingDao.getUserIdsByGroupIdList(eid, Collections.singletonList(personPositionDTO.getValue()));
                    if (CollectionUtils.isNotEmpty(groupUserIdList)) {
                        userIdList.addAll(groupUserIdList);
                    }
                    break;
                case ORGANIZATION:
                    //查看是否是老企业
                    boolean historyEnterprise = enterpriseService.isHistoryEnterprise(eid);
                    List<String> regionUserIdList;
                    if (historyEnterprise) {
                        regionUserIdList = enterpriseUserDao.selectUserByDepartmentId(eid, personPositionDTO.getValue());
                    } else {
                        regionUserIdList = enterpriseUserDao.selectUserByRegionId(eid, personPositionDTO.getValue());
                    }
                    if (CollectionUtils.isNotEmpty(regionUserIdList)) {
                        userIdList.addAll(regionUserIdList);
                    }
                    break;
                default:
                    break;
            }
        }
        //过滤人员管辖范围权限
        if(CollectionUtils.isNotEmpty(userIdList) && StringUtils.isNotBlank(userId)){
            return subordinateMappingService.retainSubordinateUserIdList(eid, userId, new ArrayList<>(userIdList),Boolean.TRUE);
        }
        return new ArrayList<>(userIdList);
    }
}
