package com.coolcollege.intelligent.facade;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.yunda.YundaEnterpriseEnum;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.region.dto.RegionStoreNumRecursionMsgDTO;
import com.coolcollege.intelligent.model.region.dto.RegionSyncDTO;
import com.coolcollege.intelligent.model.store.dto.StoreSyncDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.dingSync.DingDeptSyncService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 同步门店
 * @author zhangnan
 * @date 2022-05-06 9:25
 */
@Slf4j
@Service
public class SyncStoreFacade {

    @Resource
    private RegionService regionService;
    @Resource
    private StoreService storeService;
    @Resource
    private RedisConstantUtil redisConstantUtil;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private DingDeptSyncService dingDeptSyncService;

    public void syncDingOnePartyStoreAndRegion(String eid,String dingCorpId, String userId, String userName, EnterpriseSettingVO enterpriseSettingVO) throws ApiException {
        // 根据企业id查询全部区域
        List<RegionSyncDTO> regionList = regionService.getAllRegionIdAndDeptId(eid);
        Map<String, Object> regionMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(regionList)) {
            regionMap = regionList.stream().filter((RegionSyncDTO s) -> StringUtils.isNotBlank(s.getSynDingDeptId())&&s.getId()!=null).collect(Collectors.toMap(RegionSyncDTO::getSynDingDeptId, RegionSyncDTO::getId));
        }
        // 根据企业id查询全部门店
        List<StoreSyncDTO> storeDOList = storeService.getAllStoreIdsAndDeptId(eid);
        Map<String, Object> storeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(storeDOList)) {
            storeMap = storeDOList.stream().filter((StoreSyncDTO s) ->
                    StringUtils.isNotBlank(s.getSynDingDeptId())&&s.getId()!=null).collect(Collectors.toMap(StoreSyncDTO::getSynDingDeptId, StoreSyncDTO::getId));
        }
        // 将数据库所有门店放到redis中缓存，同步过程中使用
        redisUtil.putAll(redisConstantUtil.getSyncStoreKey(eid), storeMap, 1L, TimeUnit.DAYS);
        // 将数据库所有区域放到redis中缓存，同步过程中使用
        redisUtil.putAll(redisConstantUtil.getSyncRegionKey(eid), regionMap, 1L, TimeUnit.DAYS);
        // 广度优先遍历 -- 区域和门店
        dingDeptSyncService.syncDingOnePartyRegionAndStore(eid, dingCorpId, userId, userName, enterpriseSettingVO);
        // 根据门店&区域数量
        if(!YundaEnterpriseEnum.yundaAffiliatedCompany(eid)){
            simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumRecursionMsgDTO(eid, Long.valueOf(SyncConfig.ROOT_DEPT_ID))), RocketMqTagEnum.CAL_REGION_STORE_NUM);
        }
    }

    /**
     * 同步门店部门
     * @param eid
     * @param dingCorpId
     * @param userId
     * @param userName
     */
    public void syncDingOnePartyStoreGroup(String eid, String dingCorpId, String userId, String userName){
        try {
            dingDeptSyncService.syncDingOnePartyStoreGroup(eid, dingCorpId, userId, userName);
        } catch (ApiException e) {
            log.error("syncDingOnePartyStoreGroup error eid:{}, corpId:{}", eid, dingCorpId, e);
        }
    }
}
