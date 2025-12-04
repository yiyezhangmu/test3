package com.coolcollege.intelligent.mapper.user;

import com.coolcollege.intelligent.dao.enterprise.UserRegionMappingMapper;
import com.coolcollege.intelligent.model.enterprise.UserRegionMappingDO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: UserRegionMappingDAO
 * @Description:
 * @date 2022-03-03 9:50
 */
@Service
public class UserRegionMappingDAO {

    @Autowired
    private UserRegionMappingMapper userRegionMappingMapper;

    /**
     * 根据主键id查询数据
     * @param id
     * @return
     */
    public UserRegionMappingDO selectById(String enterpriseId, Integer id){
        return userRegionMappingMapper.selectById(enterpriseId, id);
    }

    /**
     * 根据人员id和部门映射id删除映射关系
     * @param enterpriseId
     * @param userId
     * @param regionIds
     */
    public void deletedByUserIdAndRegionId(String enterpriseId, String userId, List<String> regionIds){
        userRegionMappingMapper.deletedByUserIdAndRegionId(enterpriseId, userId, regionIds);
    }

    /**
     * 编辑数据
     * @param entity
     * @return
     */
    public Boolean updateById(String enterpriseId, UserRegionMappingDO entity){
        return userRegionMappingMapper.updateById(enterpriseId, entity);
    }

    /**
     * 新增人员部门映射 返回主键id
     * @param entity
     * @return
     */
    public Integer addUserRegionMapping(String enterpriseId, UserRegionMappingDO entity){
        return userRegionMappingMapper.addUserRegionMapping(enterpriseId, entity);
    }

    /**
     * 根据用户id删除用户和区域的映射关系
     * @param enterpriseId
     * @param userIds
     */
    public void deletedByUserIds(String enterpriseId, List<String> userIds){
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        List<String> distinctData = userIds.stream()
                .distinct()
                .collect(Collectors.toList());
        userRegionMappingMapper.deletedByUserIds(enterpriseId, distinctData);
    }

    /**
     * 根据用户id删除除用户自建外的映射关系
     * @param enterpriseId 企业id
     * @param userIds 用户id列表
     */
    public void deletedExcludeCreateByUserIds(String enterpriseId, List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        List<String> distinctData = userIds.stream()
                .distinct()
                .collect(Collectors.toList());
        userRegionMappingMapper.deletedExcludeCreateByUserIds(enterpriseId, distinctData);
    }

    /**
     * 根据ids批量删除
     * @param enterpriseId
     * @param ids
     */
    public void deletedByIds(String enterpriseId, List<Integer> ids){
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        List<Integer> distinctData = ids.stream()
                .distinct()
                .collect(Collectors.toList());
        userRegionMappingMapper.deletedByIds(enterpriseId, distinctData);
    }

    /**
     * 批量插入用户和区域映射关系
     * @param enterpriseId
     * @param userRegionMappingDOS
     */
    public void batchInsertRegionMapping(String enterpriseId, List<UserRegionMappingDO> userRegionMappingDOS){
        if (CollectionUtils.isEmpty(userRegionMappingDOS)) {
            return;
        }
        List<UserRegionMappingDO> distinctData = userRegionMappingDOS.stream()
                .distinct()
                .collect(Collectors.toList());
        userRegionMappingMapper.batchInsertRegionMapping(enterpriseId, distinctData);
    }
    /**
     * 批量新增人员部门映射关系
     * @param enterpriseId
     * @param list
     */
    public void batchInsert(String enterpriseId, List<UserRegionMappingDO> list){
        userRegionMappingMapper.batchInsert(enterpriseId, list);
    }

    public void batchAdd(String enterpriseId, String userId, List<String> regionIds){
        List<String> dbRegionIds = getRegionIdsByUserId(enterpriseId, userId);
        boolean isExist = CollectionUtils.isNotEmpty(dbRegionIds);
        List<UserRegionMappingDO> userRegionList = new ArrayList<>();
        for (String regionId : regionIds) {
            if(isExist && dbRegionIds.contains(regionId)){
                continue;
            }
            UserRegionMappingDO userRegion = UserRegionMappingDO.convertDO(userId, regionId, null);
            userRegionList.add(userRegion);
        }
        if(isExist){
            List<String> deleteIds = dbRegionIds.stream().filter(regionId -> !regionIds.contains(regionId)).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(deleteIds)){
                deletedByUserIdAndRegionId(enterpriseId, userId, deleteIds);
            }
        }
        if(CollectionUtils.isEmpty(userRegionList)){
            return;
        }
        batchInsert(enterpriseId, userRegionList);
    }

    /**
     * 根据人员id列表查询人员所属部门
     * @param enterpriseId
     * @param userIds
     * @return
     */
    public List<UserRegionMappingDO> listUserRegionMappingByUserId(String enterpriseId, List<String> userIds){
        return userRegionMappingMapper.listUserRegionMappingByUserId(enterpriseId, userIds);
    }


    /**
     * 查询部门下的人员
     * @param enterpriseId
     * @param regionIds
     * @return
     */
    public List<UserRegionMappingDO> selectUserListByRegionIds(String enterpriseId, List<String> regionIds){
       return userRegionMappingMapper.selectUserListByRegionIds(enterpriseId,regionIds);
    }

    /**
     * 获取区域的直连人员数量
     * @param enterpriseId
     * @param regionIds
     * @return
     */
    public List<HashMap<String,Long>> getRegionUserCount(String enterpriseId,List<String> regionIds){
        return userRegionMappingMapper.getRegionUserCount(enterpriseId,regionIds);
    }

    /**
     * 根据Userids和regionids查询数据
     * @param enterpriseId
     * @param userIds
     * @param regionIds
     * @return
     */
    public List<UserRegionMappingDO> listByUserIdsAndRegionIds(String enterpriseId,List<String> userIds,List<String> regionIds){
        return userRegionMappingMapper.listByUserIdsAndRegionIds(enterpriseId,userIds,regionIds);
    }


    /**
     * 批量删除人员部门映射关系
     * @param enterpriseId
     * @param userIds
     * @param regionIds
     */
    public void batchDeletedByUserIdsAndRegionIds(String enterpriseId,List<String> userIds,List<String> regionIds){
        userRegionMappingMapper.batchDeletedByUserIdsAndRegionIds(enterpriseId,userIds,regionIds);
    }

    /**
     * 获取用户所在的部门
     * @param enterpriseId
     * @param userIds
     * @return
     */
    public List<UserRegionMappingDO> getRegionIdsByUserIds(String enterpriseId, List<String> userIds){
        if(CollectionUtils.isEmpty(userIds)){
            return Lists.newArrayList();
        }
        return userRegionMappingMapper.getRegionIdsByUserIds(enterpriseId, userIds);
    }

    public List<String> getRegionIdsByUserId(String enterpriseId, String userId){
        if(StringUtils.isAnyBlank(enterpriseId, userId)){
            return Lists.newArrayList();
        }
        List<UserRegionMappingDO> regionMappings = getRegionIdsByUserIds(enterpriseId, Arrays.asList(userId));
        return ListUtils.emptyIfNull(regionMappings).stream().map(UserRegionMappingDO::getRegionId).distinct().collect(Collectors.toList());
    }

    /**
     * 获取部门有哪些用户
     * @param enterpriseId
     * @param regionIds
     * @return
     */
    public List<String> getUserIdsByRegionIds(String enterpriseId, List<String> regionIds){
        return userRegionMappingMapper.getUserIdsByRegionIds(enterpriseId, regionIds);
    }
}
