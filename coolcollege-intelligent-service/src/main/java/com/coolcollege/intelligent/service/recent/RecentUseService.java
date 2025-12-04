package com.coolcollege.intelligent.service.recent;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.DeviceMappingDO;
import com.coolcollege.intelligent.model.enterprise.dto.SelectUserDTO;
import com.coolcollege.intelligent.model.region.dto.AuthVisualDTO;
import com.coolcollege.intelligent.model.store.dto.RecentViewStoreDTO;
import com.coolcollege.intelligent.model.store.dto.SelectStoreDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.SubordinateMappingService;
import com.coolcollege.intelligent.service.selectcomponent.SelectionComponentService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2021/1/12 16:44
 */
@Service
public class RecentUseService {

    @Resource
    private StoreMapper storeMapper;


    @Resource
    private EnterpriseUserMapper userMapper;

    @Resource
    private DeviceMapper deviceMapper;

    @Autowired
    private RedisUtilPool redis;
    @Resource
    private AuthVisualService authVisualService;
    @Autowired
    private SelectionComponentService selectionComponentService;
    @Autowired
    private SubordinateMappingService subordinateMappingService;

    /**
     * 常用人员列表
     * @param eid
     * @return
     */
    public List<SelectUserDTO> recentUseUserList(String eid) {
        String userId = UserHolder.getUser().getUserId();
        String key = LRUService.getKey(eid, userId, LRUService.RECENT_USE_USER);
        Set<String> recentUserSet = redis.zrange(key, 0, -1);
        List<SelectUserDTO> results = new ArrayList<>();
        if (CollUtil.isNotEmpty(recentUserSet)) {
            results = userMapper.selectRecentUserList(eid, new ArrayList<>(recentUserSet));
        }
        //填充人员岗位，门店，区域信息
        results = selectionComponentService.supplementRecentUserQueryResult(eid, results);
        // 填充选取权限
        Boolean haveAllSubordinateUser = subordinateMappingService.checkHaveAllSubordinateUser(eid, userId);
        List<String> userSubordinateList = Lists.newArrayList();
        if(!haveAllSubordinateUser){
            userSubordinateList = subordinateMappingService.getSubordinateUserIdList(eid, userId,Boolean.TRUE);
        }
        List<String> finalUserSubordinateList = userSubordinateList;
        results.forEach(f -> {
            if(haveAllSubordinateUser){
                f.setSelectFlag(true);
            }else {
                
                f.setSelectFlag(finalUserSubordinateList.contains(f.getUserId()));
            }
        });
        return results;
    }

    /**
     * 常用门店列表
     * @param eid
     * @return
     */
    public List<RecentViewStoreDTO> recentUseStoreList(String eid, CurrentUser user) {

        String key = LRUService.getKey(eid, user.getUserId(), LRUService.RECENT_USE_STORE);
        Set<String> recentStoreColl = redis.zrange(key, 0, -1);
        //所有数据权限
        if(!AuthRoleEnum.ALL.equals(user.getRoleAuth())){
            //查询权限门店
            AuthVisualDTO authVisualDTO = authVisualService.authRegionStoreByRole(eid,user.getUserId());
            List<String> storeIds = authVisualDTO.getStoreIdList();
            if(CollectionUtils.isEmpty(storeIds)){
                return new ArrayList<>();
            }
            //过滤无权限门店
            Set<String> set = storeIds.stream().collect(Collectors.toSet());
            recentStoreColl = recentStoreColl.stream().filter(data -> set.contains(data)).collect(Collectors.toSet());
        }
        if (CollUtil.isNotEmpty(recentStoreColl)) {
            List<SelectStoreDTO> stores = storeMapper.selectRecentStoreList(eid, new ArrayList<>(recentStoreColl));
            List<String> storeIds = stores.stream().map(SelectStoreDTO::getStoreId).collect(Collectors.toList());
            // 获取门店设备列表
            List<DeviceDO> deviceByStoreIdList = deviceMapper.getDeviceByStoreIdList(eid, storeIds,null, null, null);
            Map<String, List<String>> storeDeviceIdMap = deviceByStoreIdList.stream()
                    .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
                    .collect(Collectors.groupingBy(DeviceDO::getBindStoreId,
                    Collectors.mapping(DeviceDO::getDeviceId, Collectors.toList())));
            List<String> deviceIds = deviceByStoreIdList.stream()
                    .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
                    .map(DeviceDO::getDeviceId).collect(Collectors.toList());
            List<DeviceDO> deviceList = deviceMapper.getDeviceByDeviceIdList(eid, deviceIds);
            // 填充门店与设备的关联数据
            List<RecentViewStoreDTO> result = stores.stream().map(m -> {
                RecentViewStoreDTO recentViewStore = new RecentViewStoreDTO();
                BeanUtil.copyProperties(m, recentViewStore);
                List<String> devices = storeDeviceIdMap.get(m.getStoreId());
                if (CollUtil.isEmpty(devices)) {
                    recentViewStore.setDeviceNum(0);
                    recentViewStore.setDeviceList(new ArrayList<>());
                } else {
                    recentViewStore.setDeviceList(deviceList.stream().filter(d -> devices.contains(d.getDeviceId())).collect(Collectors.toList()));
                    recentViewStore.setDeviceNum(recentViewStore.getDeviceList().size());
                }
                return recentViewStore;
            }).collect(Collectors.toList());
            return result;
        }
        return new ArrayList<>();
    }
}
