package com.coolcollege.intelligent.service.authentication.impl;

import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enums.DataSourceEnum;
import com.coolcollege.intelligent.model.enums.UserAuthMappingTypeEnum;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.service.authentication.UserAuthMappingService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/11/11
 */
@Service
@Slf4j
public class UserAuthMappingServiceImpl implements UserAuthMappingService {
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;

    @Resource
    private RegionMapper regionMapper;


    @Override
    public Boolean deleteUserAuthMapping(String eid, String userId) {
        userAuthMappingMapper.deleteAuthMappingByUserIds(eid, Collections.singletonList(userId));
        return true;
    }

    @Override
    public List<UserAuthMappingDO> listUserAuthMappingByUserId(String eid, String userId) {
        if(StringUtils.isAnyBlank(eid, userId)){
            return Lists.newArrayList();
        }
        return userAuthMappingMapper.listUserAuthMappingByUserId(eid, userId);
    }

    @Override
    public void deleteAuthMappingByUserIdAndMappingIds(String eid, String userId, List<String> mappingIds) {
        if(CollectionUtils.isEmpty(mappingIds)){
            return;
        }
        userAuthMappingMapper.deleteAuthMappingByUserIdAndTypeAndMappingIds(eid, userId, Constants.REGION, mappingIds);
    }

    @Override
    public void addUserRegionAuth(String eid, String userId, List<String> mappingIds) {
        List<UserAuthMappingDO> list = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userId);
        List<String> authRegionList = ListUtils.emptyIfNull(list).stream().map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
        List<UserAuthMappingDO> addAuthRegionList = new ArrayList<>();
        mappingIds.forEach(regionId -> {
            if(!authRegionList.contains(regionId)){
                UserAuthMappingDO userAuthMappingDO = new UserAuthMappingDO();
                userAuthMappingDO.setUserId(userId);
                userAuthMappingDO.setMappingId(regionId);
                userAuthMappingDO.setType(Constants.REGION);
                userAuthMappingDO.setSource(DataSourceEnum.SYNC.getCode());
                userAuthMappingDO.setCreateId("system");
                userAuthMappingDO.setCreateTime(System.currentTimeMillis());
                addAuthRegionList.add(userAuthMappingDO);
            }
        });

        if(CollectionUtils.isNotEmpty(addAuthRegionList)){
            userAuthMappingMapper.batchInsertUserAuthMapping(eid, addAuthRegionList);
        }
    }

    @Override
    public void changeUserRegionAuth(String eid, String userId, List<String> mappingIds) {
        List<UserAuthMappingDO> list = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userId);
        List<String> authRegionList = ListUtils.emptyIfNull(list).stream().map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
        List<UserAuthMappingDO> addAuthRegionList = new ArrayList<>();
        mappingIds.forEach(regionId -> {
            if(!authRegionList.contains(regionId)){
                UserAuthMappingDO userAuthMappingDO = new UserAuthMappingDO();
                userAuthMappingDO.setUserId(userId);
                userAuthMappingDO.setMappingId(regionId);
                userAuthMappingDO.setType(Constants.REGION);
                userAuthMappingDO.setSource(DataSourceEnum.SYNC.getCode());
                userAuthMappingDO.setCreateId("system");
                userAuthMappingDO.setCreateTime(System.currentTimeMillis());
                addAuthRegionList.add(userAuthMappingDO);
            }
        });
        List<String> deleteAuth = authRegionList.stream().filter(id -> !mappingIds.contains(id)).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(addAuthRegionList)){
            userAuthMappingMapper.batchInsertUserAuthMapping(eid, addAuthRegionList);
        }
        if(CollectionUtils.isNotEmpty(deleteAuth)){
            userAuthMappingMapper.deleteAuthMappingByUserIdAndTypeAndMappingIds(eid, userId, Constants.REGION, deleteAuth);
        }
    }

}
