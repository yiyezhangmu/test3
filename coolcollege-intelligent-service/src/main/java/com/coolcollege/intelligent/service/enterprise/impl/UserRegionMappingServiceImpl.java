package com.coolcollege.intelligent.service.enterprise.impl;

import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.enterprise.UserRegionMappingDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.UserRegionMappingService;
import com.coolcollege.intelligent.service.region.RegionService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: UserRegionMappingServiceImpl
 * @Description:
 * @date 2022-03-03 10:10
 */
@Service
public class UserRegionMappingServiceImpl implements UserRegionMappingService {

    @Autowired
    private UserRegionMappingDAO userRegionMappingDAO;
    @Autowired
    private RegionService regionService;

    @Override
    public void dealUserRegionMapping(String enterpriseId, String userId, List<Long> regionIds) {
        userRegionMappingDAO.deletedByUserIds(enterpriseId, Arrays.asList(userId));
        if(CollectionUtils.isEmpty(regionIds)){
            //移到未分组
            RegionDO unclassified = regionService.getUnclassifiedRegionDO(enterpriseId);
            if(Objects.isNull(unclassified)){
                return;
            }
            //移到未分组
            userRegionMappingDAO.addUserRegionMapping(enterpriseId, buildUserRegionMapping(userId, unclassified.getId()));
            return;
        }
        List<UserRegionMappingDO> userRegionMappingList = buildUserRegionMapping(userId, regionIds);
        userRegionMappingDAO.batchInsertRegionMapping(enterpriseId, userRegionMappingList);
    }

    @Override
    public void dealUserRegionMappingBySynDingDeptId(String enterpriseId, String userId, List<Long> synDingDeptIds) {
        userRegionMappingDAO.deletedByUserIds(enterpriseId, Arrays.asList(userId));
        List<Long> regionIds = null;
        if(CollectionUtils.isNotEmpty(synDingDeptIds)){
            regionIds = regionService.getRegionIdsBySynDingDeptIds(enterpriseId, synDingDeptIds.stream().map(Objects::toString).collect(Collectors.toList()));
        }
        if(CollectionUtils.isEmpty(synDingDeptIds) || CollectionUtils.isEmpty(regionIds)){
            //移到未分组
            RegionDO unclassified = regionService.getUnclassifiedRegionDO(enterpriseId);
            if(Objects.isNull(unclassified)){
                return;
            }
            userRegionMappingDAO.addUserRegionMapping(enterpriseId, buildUserRegionMapping(userId, unclassified.getId()));
            return;
        }
        List<UserRegionMappingDO> userRegionMappingList = buildUserRegionMapping(userId, regionIds);
        userRegionMappingDAO.batchInsertRegionMapping(enterpriseId, userRegionMappingList);
    }

    @Override
    public void deletedUserRegionMappingByUserIds(String enterpriseId, String userId) {
        userRegionMappingDAO.deletedByUserIds(enterpriseId, Arrays.asList(userId));
    }

    /**
     * 处理映射关系
     * @param userId
     * @param regionIds
     * @return
     */
    private List<UserRegionMappingDO> buildUserRegionMapping(String userId, List<Long> regionIds){
        if(CollectionUtils.isEmpty(regionIds)){
            return null;
        }
        List<UserRegionMappingDO> resultList = new ArrayList<>();
        for (Long regionId : regionIds) {
            resultList.add(buildUserRegionMapping(userId, regionId));
        }
        return resultList;
    }

    private UserRegionMappingDO buildUserRegionMapping(String userId, Long regionId){
        CurrentUser user = UserHolder.getUser();
        String createUser = Optional.ofNullable(user).map(CurrentUser::getUserId).orElse("system");
        UserRegionMappingDO regionMapping = new UserRegionMappingDO();
        regionMapping.setRegionId(regionId.toString());
        regionMapping.setUserId(userId);
        regionMapping.setCreateId(createUser);
        regionMapping.setCreateTime(System.currentTimeMillis());
        return regionMapping;
    }
}
