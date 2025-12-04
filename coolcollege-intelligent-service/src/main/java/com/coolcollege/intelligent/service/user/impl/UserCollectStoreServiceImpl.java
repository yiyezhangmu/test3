package com.coolcollege.intelligent.service.user.impl;

import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.mapper.user.UserCollectStoreDAO;
import com.coolcollege.intelligent.model.device.vo.LastPatrolStoreVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.user.UserCollectStoreDO;
import com.coolcollege.intelligent.service.user.UserCollectStoreService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: UserCollectStoreServiceImpl
 * @Description:
 * @date 2022-12-20 14:47
 */
@Service
public class UserCollectStoreServiceImpl implements UserCollectStoreService {

    @Resource
    private UserCollectStoreDAO userCollectStoreDAO;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private DeviceMapper deviceMapper;

    @Override
    public boolean addUserCollectStore(String enterpriseId, String userId, String storeId) {
        userCollectStoreDAO.addUserCollectStore(enterpriseId, userId, storeId);
        return true;
    }

    @Override
    public boolean deleteUserCollectStore(String enterpriseId, String userId, String storeId) {
        userCollectStoreDAO.deleteUserCollectStore(enterpriseId, userId, storeId);
        return false;
    }

    @Override
    public List<LastPatrolStoreVO> getUserCollectStore(String enterpriseId, String userId) {
        List<LastPatrolStoreVO> resultList = new ArrayList<>();
        List<UserCollectStoreDO> userCollectStoreList = userCollectStoreDAO.getUserCollectStore(enterpriseId, userId);
        if(CollectionUtils.isEmpty(userCollectStoreList)){
            return Lists.newArrayList();
        }
        List<String> storeIds = userCollectStoreList.stream().map(UserCollectStoreDO::getStoreId).distinct().collect(Collectors.toList());
        List<StoreDO> storeNames = storeMapper.getStoreNameByIds(enterpriseId, storeIds);
        Map<String, StoreDO> storeMap = storeNames.stream().collect(Collectors.toMap(k -> k.getStoreId(), Function.identity()));
        Map<String, String> storeIdMap = new HashMap<>();
        List<String> deviceStoreIds = deviceMapper.getDeviceByStoreIds(enterpriseId, storeIds);
        for (UserCollectStoreDO userCollectStore : ListUtils.emptyIfNull(userCollectStoreList)) {
            String storeId = userCollectStore.getStoreId();
            String store = storeIdMap.get(storeId);
            StoreDO storeDO = storeMap.get(storeId);
            if(StringUtils.isBlank(store) && Objects.nonNull(storeDO)){
                LastPatrolStoreVO lastPatrolStore = new LastPatrolStoreVO(storeId, storeDO.getStoreName(), storeDO.getStoreNum(), deviceStoreIds.contains(storeId));
                resultList.add(lastPatrolStore);
                storeIdMap.put(storeId, storeId);
            }
        }
        return resultList;
    }
}
