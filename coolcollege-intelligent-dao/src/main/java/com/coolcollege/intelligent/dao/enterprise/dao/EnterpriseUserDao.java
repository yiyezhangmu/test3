package com.coolcollege.intelligent.dao.enterprise.dao;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.enums.user.UserTypeEnum;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserSingleDTO;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户管理dao
 * @author ：xugangkun
 * @date ：2022/1/12 19:40
 */
@Repository
public class EnterpriseUserDao {

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    public Boolean insertEnterpriseUser(String eid, EnterpriseUserDO entity) {
        if (StringUtils.isBlank(entity.getUnionid()) || StringUtils.isBlank(entity.getUserId())) {
            return false;
        }
        if (StringUtils.isBlank(entity.getName())) {
            entity.setName(entity.getUserId());
        }
        if(Objects.isNull(entity.getUserType())){
            entity.setUserType(UserTypeEnum.INTERNAL_USER.getCode());
        }
        enterpriseUserMapper.insertEnterpriseUser(eid, entity);
        return true;
    }

    public void batchInsertOrUpdate(List<EnterpriseUserDO> users, String eid) {
        List<EnterpriseUserDO> result = new ArrayList<>();
        users.forEach(user -> {
            if (StringUtils.isBlank(user.getUnionid()) || StringUtils.isBlank(user.getUserId())) {
                return;
            }
            if (StringUtils.isBlank(user.getName())) {
                user.setName(user.getUserId());
            }
            if(Objects.isNull(user.getUserType())){
                user.setUserType(UserTypeEnum.INTERNAL_USER.getCode());
            }
            if(Objects.isNull(user.getUserStatus())){
                user.setUserStatus(UserStatusEnum.NORMAL.getCode());
            }
            result.add(user);
        });
        enterpriseUserMapper.batchInsertOrUpdate(result, eid);
    }

    public void batchInsertPlatformUsers(List<EnterpriseUserDO> users) {
        if(CollectionUtils.isEmpty(users)){
            return;
        }
        enterpriseUserMapper.batchInsertPlatformUsers(users);
    }

    /**
     * 根据用户id查询
     * @param enterpriseId 企业id
     * @param userId 用户id
     * @return EnterpriseUserDO
     */
    public EnterpriseUserDO selectByUserId(String enterpriseId, String userId){
        if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(userId)) {
            return null;
        }
        if(AIEnum.AI_USERID.getCode().equals(userId)){
            return EnterpriseUserDO.getAiUser();
        }
        return enterpriseUserMapper.selectByUserId(enterpriseId, userId);
    }

    public String selectNameByUserId(String enterpriseId, String userId){
        if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(userId)) {
            return null;
        }
        if(AIEnum.AI_USERID.getCode().equals(userId)){
            return AIEnum.AI_NAME.getCode();
        }
        return enterpriseUserMapper.selectActiveNameByUserId(enterpriseId, userId);
    }

    public String selectNameIgnoreActiveByUserId(String enterpriseId, String userId){
        if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(userId)) {
            return null;
        }
        return enterpriseUserMapper.selectNameIgnoreActiveByUserId(enterpriseId, userId);
    }

    public EnterpriseUserDO selectByUserIdIgnoreActive(String enterpriseId, String userId){
        if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(userId)) {
            return null;
        }
        if(AIEnum.AI_USERID.getCode().equals(userId)){
            return EnterpriseUserDO.getAiUser();
        }
        return enterpriseUserMapper.selectByUserIdIgnoreActive(enterpriseId, userId);
    }

    /**
     * 根据用户ids查询
     * @param enterpriseId
     * @param userIds
     * @return
     */
    public List<EnterpriseUserDO> selectByUserIds(String enterpriseId, List<String> userIds) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(userIds)) {
            return Lists.newArrayList();
        }
        return enterpriseUserMapper.selectUsersByUserIds(enterpriseId, userIds);
    }

    public EnterpriseUserDO selectByJobnumber(String enterpriseId, String jobnumber){
        if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(jobnumber)) {
            return null;
        }
        return enterpriseUserMapper.selectByJobnumber(enterpriseId, jobnumber);
    }

    public Map<String, String> getUserNameMap(String enterpriseId, List<String> userIds){
        if(CollectionUtils.isEmpty(userIds)){
            return Maps.newHashMap();
        }
        List<EnterpriseUserDO> userDOList = enterpriseUserMapper.selectUsersByUserIds(enterpriseId,userIds);
        Map<String,String> userIdNameMap = CollectionUtils.emptyIfNull(userDOList).stream()
                .filter(a -> a.getUserId() != null && a.getName() != null)
                .collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getName,(a, b)->a));
        userIdNameMap.putIfAbsent(Constants.SYSTEM_USER_ID, Constants.SYSTEM_USER_NAME);
        userIdNameMap.putIfAbsent(Constants.AI, Constants.AI);
        return userIdNameMap;
    }


    public Map<String, EnterpriseUserDO> getUserMap(String enterpriseId, List<String> userIds){
        if(CollectionUtils.isEmpty(userIds)){
            return Maps.newHashMap();
        }
        List<EnterpriseUserDO> userDOList = enterpriseUserMapper.selectUsersByUserIds(enterpriseId,userIds);
        return CollectionUtils.emptyIfNull(userDOList).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(EnterpriseUserDO::getUserId, Function.identity(),(a, b)->a));
    }

    public List<String> selectByUserIdsAndStatus(String eid, List<String> userIds, Integer userStatus){
        if(CollectionUtils.isEmpty(userIds)){
            return userIds;
        }
        return enterpriseUserMapper.selectByUserIdsAndStatus(eid, userIds, userStatus);
    }

    public List<EnterpriseUserDO> selectIgnoreDeletedUsersByUserIds(String enterpriseId, List<String> userIds) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(userIds)) {
            return Lists.newArrayList();
        }
        userIds = userIds.stream().distinct().collect(Collectors.toList());
        return enterpriseUserMapper.selectIgnoreDeletedUsersByUserIds(enterpriseId, userIds);
    }

    public EnterpriseUserDO selectIgnoreDeletedUserByUserId(String enterpriseId, String userId) {
        if(StringUtils.isAnyBlank(enterpriseId, userId)) {
            return null;
        }
        if(AIEnum.AI_USERID.getCode().equals(userId)){
            return EnterpriseUserDO.getAiUser();
        }
        List<EnterpriseUserDO> userDOList = enterpriseUserMapper.selectIgnoreDeletedUsersByUserIds(enterpriseId, Arrays.asList(userId));
        if(CollectionUtils.isEmpty(userDOList)){
            return null;
        }
        return userDOList.get(0);
    }

    public Integer getEnterpriseUserCount(String enterpriseId){
        if(StringUtils.isBlank(enterpriseId)){
            return Constants.ZERO;
        }
        return enterpriseUserMapper.countUserAll(enterpriseId);
    }

    public Integer getUserCountByUserIdOrRegionIds(String enterpriseId, List<String> userIds, List<String> regionIds){
        if(StringUtils.isBlank(enterpriseId)){
            return Constants.ZERO;
        }
        if(CollectionUtils.isEmpty(userIds) && CollectionUtils.isEmpty(regionIds)){
            return Constants.ZERO;
        }
        return enterpriseUserMapper.getUserCountByUserIdOrRegionIds(enterpriseId, userIds, regionIds);
    }


    public List<EnterpriseUserDO> getUsersByRoleIds(String enterpriseId,List<Long> roleIdsByComp) {
        if(StringUtils.isAnyBlank(enterpriseId)) {
            return null;
        }
        return enterpriseUserMapper.getUsersByRoleIds(enterpriseId,roleIdsByComp);
    }

    public void updateConfigEnterpriseUserList(List<EnterpriseUserDO> enterpriseUserList){
        if(CollectionUtils.isEmpty(enterpriseUserList)){
            return;
        }
        enterpriseUserMapper.updateConfigEnterpriseUserList(enterpriseUserList);
    }


    public void updateEnterpriseUser(String enterpriseId, EnterpriseUserDO enterpriseUser){
        enterpriseUserMapper.updateEnterpriseUser(enterpriseId, enterpriseUser);
    }

    public EnterpriseUserDO getUserByMobile(String enterpriseId, String mobile) {
        return enterpriseUserMapper.getUserByMobile(enterpriseId, mobile, null);
    }

    public EnterpriseUserDO selectByThirdOaUniqueFlag(String enterpriseId, String thirdOaUniqueFlag) {
        if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(thirdOaUniqueFlag)) {
            return null;
        }
        return enterpriseUserMapper.selectByThirdOaUniqueFlag(enterpriseId, thirdOaUniqueFlag);
    }

    public void updateConfigEnterpriseUserByUnionId(EnterpriseUserDO enterpriseUserDO){
        if(enterpriseUserDO == null || StringUtils.isBlank(enterpriseUserDO.getUnionid())){
            return;
        }
        enterpriseUserMapper.updateConfigEnterpriseUserByUnionId(enterpriseUserDO);
    }

    public List<EnterpriseUserDO> selectUsersByStatusAndUserIds(String enterpriseId, List<String> userIds, Integer userStatus, Boolean active){
        return enterpriseUserMapper.selectUsersByStatusAndUserIds(enterpriseId, userIds, userStatus, active);
    }

    public void batchUpdateDiffUserDiffRegionIds(String enterpriseId, List<EnterpriseUserDO> enterpriseUserDOList){
        if(CollectionUtils.isEmpty(enterpriseUserDOList)){
            return;
        }
        enterpriseUserMapper.batchUpdateDiffUserDiffRegionIds(enterpriseId, enterpriseUserDOList);
    }

    public void batchUpdateUserRegionIds(String eid, String oldFullRegionPath, String newFullRegionPath, String keyNode){
        enterpriseUserMapper.batchUpdateUserRegionIds(eid, oldFullRegionPath, newFullRegionPath, keyNode);
    }

    public List<String> selectAllUserIdsByActive(String eid, Boolean active){
        return enterpriseUserMapper.selectAllUserIdsByActive(eid, active);
    }

    public Integer getActiveUserCount(String eid){
        return enterpriseUserMapper.getActiveUserCount(eid);
    }

    public List<String> getUserIdsByRegionIdList(String eid, List<String> regionIdList){
        if(CollectionUtils.isEmpty(regionIdList)){
            return Lists.newArrayList();
        }
        return enterpriseUserMapper.getUserIdsByRegionIdList(eid, regionIdList);
    }

    public EnterpriseUserDO selectById(String enterpriseId, String id){
        return enterpriseUserMapper.selectById(enterpriseId, id);
    }

    public EnterpriseUserDTO getUserDetailByUnionId(@Param("eid") String enterpriseId, @Param("unionId") String unionId){
        return enterpriseUserMapper.getUserDetailByUnionId(enterpriseId, unionId);
    }

    public List<String> listUserIdByDepartmentIdList(String eid, List<String> departmentIdList){
        return enterpriseUserMapper.listUserIdByDepartmentIdList(eid, departmentIdList);
    }

    public EnterpriseUserDO selectConfigUserByUserIdIgnoreActive( String userId){
        return enterpriseUserMapper.selectConfigUserByUserIdIgnoreActive(userId);
    }

    public void updateConfigEnterpriseUser(EnterpriseUserDO enterpriseUserDO){
        enterpriseUserMapper.updateConfigEnterpriseUser(enterpriseUserDO);
    }

    public void batchDeleteUserIdsConfig(List<String> userIds){
        if(CollectionUtils.isEmpty(userIds)){
            return;
        }
        enterpriseUserMapper.batchDeleteUserIdsConfig(userIds);
    }

    public void batchUpdateUserMobile(String enterpriseId, List<EnterpriseUserDO> users){
        enterpriseUserMapper.batchUpdateUserMobile(enterpriseId, users);
    }

    public Integer batchUpdateImportUser(String enterpriseId, List<EnterpriseUserDO> users){
        return enterpriseUserMapper.batchUpdateImportUser(enterpriseId, users);
    }

    public void nonOverwriteUpdateImportUser(String enterpriseId, List<EnterpriseUserDO> users){
        enterpriseUserMapper.nonOverwriteUpdateImportUser(enterpriseId, users);
    }

    public EnterpriseUserDO getUserInfoByMobile(String enterpriseId, String mobile){
        return enterpriseUserMapper.getUserInfoByMobile(enterpriseId, mobile);
    }

    public List<EnterpriseUserDO> getMainAdmin(String eid){
        return enterpriseUserMapper.getMainAdmin(eid);
    }

    public List<String> selectUserIdsByUserIdOrJobNumber(String eid, String importType, String userId, String jobNumber){
        return enterpriseUserMapper.selectUserIdsByUserIdOrJobNumber(eid, importType, userId, jobNumber);
    }

    public List<String> selectUserNamesByUserIds(String eid, List<String> userIds){
        if(StringUtils.isBlank(eid) || CollectionUtils.isEmpty(userIds)){
            return null;
        }
        return enterpriseUserMapper.selectUserNamesByUserIds(eid, userIds);
    }

    public List<String> selectUserByDepartmentId(String eid,  String departmentId){
        return enterpriseUserMapper.selectUserByDepartmentId(eid, departmentId);
    }

    public List<String> selectUserByRegionId(String eid, String regionId){
        return enterpriseUserMapper.selectUserByRegionId(eid, regionId);
    }

    public List<String> selectUsersByDingUserIds(String eid, List<String> userIds){
        if(CollectionUtils.isEmpty(userIds)){
            return Lists.newArrayList();
        }
        return enterpriseUserMapper.selectUsersByDingUserIds(eid, userIds);
    }

    public List<EnterpriseUserSingleDTO> usersByUserIdList(String eid, List<String> userIds){
        if (CollectionUtils.isEmpty(userIds)){
            return Lists.newArrayList();
        }
        return enterpriseUserMapper.usersByUserIdList(eid, userIds);
    }

    public List<EnterpriseUserDO> selectActiveUsersByUserIds(String eid, List<String> userIds) {
        if(CollectionUtils.isEmpty(userIds)){
            return Lists.newArrayList();
        }
        return enterpriseUserMapper.selectActiveUsersByUserIds(eid, userIds);
    }

    public List<String> getUserIdsByUnionIds(String enterpriseId, List<String> unionids){
        if(CollectionUtils.isEmpty(unionids)){
            return Lists.newArrayList();
        }
        return enterpriseUserMapper.getUserIdsByUnionIds(enterpriseId, unionids);
    }


    public List<EnterpriseUserDO> selectUserByKeyword(String eid, String keyword, Integer userStatus, List<String> userIdList, Boolean active){
        return enterpriseUserMapper.selectUserByKeyword(eid, keyword, userStatus, userIdList, active);
    }

    public List<EnterpriseUserDO> listByUserIdIgnoreActive(String enterpriseId, List<String> userIds){
        if(CollectionUtils.isEmpty(userIds)){
            return Lists.newArrayList();
        }
        return enterpriseUserMapper.listByUserIdIgnoreActive(enterpriseId, userIds);
    }

    public List<EnterpriseUserDO> getPlatformUserByMobile(String mobile){
        if(StringUtils.isBlank(mobile)){
            return Lists.newArrayList();
        }
        return enterpriseUserMapper.getPlatformUserByMobile(mobile);
    }

}
