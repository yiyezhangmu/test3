package com.coolcollege.intelligent.service.storework.Impl;

import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkRangeDao;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkRangeDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.service.storework.StoreWorkRangeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author wxp
 * @Date 2022/9/8 15:22
 * @Version 1.0
 */
@Service
public class StoreWorkRangeServiceImpl implements StoreWorkRangeService {

    @Resource
    SwStoreWorkRangeDao swStoreWorkRangeDao;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private StoreGroupMapper storeGroupMapper;
    @Resource
    private StoreMapper storeMapper;

    // 店务id，门店范围
    @Override
    public Map<Long, List<StoreWorkCommonDTO>> listStoreRange(String enterpriseId, List<Long> storeWorkIdList){
        Map<Long, List<StoreWorkCommonDTO>> result = Maps.newHashMap();
        List<SwStoreWorkRangeDO> storeWorkRangeList = swStoreWorkRangeDao.listBystoreWorkIds(enterpriseId, storeWorkIdList);
        Map<Long, List<SwStoreWorkRangeDO>> storeWorkRangeMap = storeWorkRangeList.stream()
                .collect(Collectors.groupingBy(SwStoreWorkRangeDO::getStoreWorkId));
        List<String> storeList = Lists.newArrayList();
        List<String> regionList = Lists.newArrayList();
        List<String> groupList = Lists.newArrayList();
        for (SwStoreWorkRangeDO item : storeWorkRangeList) {
            switch (item.getType()) {
                case UnifyTaskConstant.StoreType.STORE:
                    storeList.add(item.getMappingId());
                    break;
                case UnifyTaskConstant.StoreType.REGION:
                    regionList.add(item.getMappingId());
                    break;
                case UnifyTaskConstant.StoreType.GROUP:
                    groupList.add(item.getMappingId());
                    break;
                default:
                    break;
            }
        }
        //区域
        Map<String, String> regionDOMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(regionList)) {
            List<RegionDO> regionDOList = regionMapper.getRegionByRegionIds(enterpriseId, regionList);
            regionDOMap = regionDOList.stream()
                    .filter(a -> a.getRegionId() != null && a.getName() != null)
                    .collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getName, (a, b) -> a));
        }
        //分组
        Map<String, String> groupDOMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(groupList)) {
            List<StoreGroupDO> storeGroupDOS = storeGroupMapper.getListByIds(enterpriseId, groupList);
            groupDOMap = storeGroupDOS.stream()
                    .filter(a -> a.getGroupId() != null && a.getGroupName() != null)
                    .collect(Collectors.toMap(StoreGroupDO::getGroupId, StoreGroupDO::getGroupName, (a, b) -> a));
        }
        Map<String, String> storeMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(storeList)) {
            List<StoreDO> storeDOList = storeMapper.getStoresByStoreIds(enterpriseId, storeList);
            storeMap = ListUtils.emptyIfNull(storeDOList)
                    .stream().filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                    .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));
        }

        Map<String, String> finalStoreMap = storeMap;
        Map<String, String> finalRegionDOMap = regionDOMap;
        Map<String, String> finalGroupDOMap = groupDOMap;
        storeWorkRangeMap.forEach((storeWorkId, storeWorkRangeListTmp) -> {
            List<StoreWorkCommonDTO> storeRangeList = Lists.newArrayList();
            storeWorkRangeListTmp.forEach(storeRangeTmp -> {
                switch (storeRangeTmp.getType()) {
                    case UnifyTaskConstant.StoreType.STORE:
                        storeRangeList.add(new StoreWorkCommonDTO(UnifyTaskConstant.StoreType.STORE, storeRangeTmp.getMappingId(), finalStoreMap.get(storeRangeTmp.getMappingId())));
                        break;
                    case UnifyTaskConstant.StoreType.REGION:
                        storeRangeList.add(new StoreWorkCommonDTO(UnifyTaskConstant.StoreType.REGION, storeRangeTmp.getMappingId(), finalRegionDOMap.get(storeRangeTmp.getMappingId())));
                        break;
                    case UnifyTaskConstant.StoreType.GROUP:
                        storeRangeList.add(new StoreWorkCommonDTO(UnifyTaskConstant.StoreType.GROUP, storeRangeTmp.getMappingId(), finalGroupDOMap.get(storeRangeTmp.getMappingId())));
                        break;
                    default:
                        break;
                }
            });
            result.put(storeWorkId, storeRangeList);
        });
        return result;
    }

}
