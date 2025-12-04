package com.coolcollege.intelligent.service.baili.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.baili.BailiCustomizeFieldEnum;
import com.coolcollege.intelligent.common.enums.baili.BailiEnterpriseEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreSettingMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.facade.dto.RegionDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionNode;
import com.coolcollege.intelligent.model.region.dto.RegionStoreNumRecursionMsgDTO;
import com.coolcollege.intelligent.model.region.dto.RegionSyncDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.ExtendFieldDataDTO;
import com.coolcollege.intelligent.model.store.dto.ExtendFieldInfoDTO;
import com.coolcollege.intelligent.model.store.dto.StoreSyncDTO;
import com.coolcollege.intelligent.model.store.vo.ExtendFieldInfoVO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.baili.ThirdOaDeptSyncService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtil;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2021-08-11 16:05
 */
@Slf4j
@Service
public class ThirdOaDeptSyncServiceImpl implements ThirdOaDeptSyncService {


    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RegionService regionService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private RedisConstantUtil redisConstantUtil;


    @Resource
    private StoreMapper storeMapper;


    @Resource
    private RegionMapper regionMapper;
    @Resource
    private EnterpriseConfigService enterpriseConfigService;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    private EnterpriseStoreSettingMapper enterpriseStoreSettingMapper;

    static final String PREFIX ="extend_field_";


    @Override
    public void syncOrgAll(String eid, String unitId, List<RegionDTO> resultList) {
        if(CollectionUtils.isEmpty(resultList)){
            return;
        }
        //先去重
        resultList = resultList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                new TreeSet<>(Comparator.comparing(RegionDTO::getSynDingDeptId))), ArrayList::new));
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(eid);
        EnterpriseStoreSettingDO enterpriseStoreSetting = enterpriseStoreSettingMapper.getEnterpriseStoreSetting(eid);
        //初始化门店自定义字段，判断是否有zoneName 大区名称  brand 主品牌  mangerCity 管理分区    bizCity 经营城市 没有这几个字段，新增
        String extendFieldInfo = enterpriseStoreSetting.getExtendFieldInfo();
        //百丽类型企业同步的时候保证大区名称 主品牌 管理分区 经营城市 这四个门店扩展自定义类型字段
        List<ExtendFieldInfoDTO> extendFieldInfoDTOS = new ArrayList<>();
        if (BailiEnterpriseEnum.bailiAffiliatedCompany(eid)){
            extendFieldInfoDTOS = initStoreExtendFieldInfo(eid, extendFieldInfo);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());

        String userId = "";
        String regionKey = redisConstantUtil.getSyncRegionKey(eid);
        String storeKey =  redisConstantUtil.getSyncStoreKey(eid);

        Map<String, RegionDTO> mapResult = resultList.stream().collect(Collectors.toMap(RegionDTO::getSynDingDeptId, org -> org));
        RegionDTO root = mapResult.get(unitId);
        if(root == null){
            throw new ServiceException("需要同步的节点不存在，同步失败 unitId:" + unitId);
        }

        //上下级一致修改为根节点
        resultList.forEach(e ->{
            if(StringUtils.isNotBlank(e.getSynDingDeptId()) && e.getSynDingDeptId().equals(e.getParentId())){
                e.setParentId(root.getSynDingDeptId());
            }
        });

        //根据父节点分组
        Map<String, List<RegionDTO>> parentGroup = ListUtils.emptyIfNull(resultList)
                .stream()
                .collect(Collectors.groupingBy(RegionDTO::getParentId));

        //构建根节点
        root.setSynDingDeptId(unitId);
        root.setStoreRange(false);
        //构建数据
        RegionDO rootRegion = prepareRegionRoot(eid, userId, root);
        if(rootRegion == null){
            return;
        }
        Queue<RegionDO> queue = new LinkedList<>();
        queue.add(rootRegion);

        List<RegionSyncDTO> regionList = regionService.getAllRegionIdAndDeptId(eid);
        List<Long> regionSelfList = new ArrayList<>();
        Map<String, Object> regionMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(regionList)) {
            regionSelfList = regionList.stream()
                    .filter((RegionSyncDTO s) -> StringUtils.isBlank(s.getSynDingDeptId())).map(RegionSyncDTO::getId)
                    .collect(Collectors.toList());

            regionMap = regionList.stream().filter((RegionSyncDTO s) -> StringUtils.isNotBlank(s.getSynDingDeptId())&&s.getId()!=null).collect(Collectors.toMap(RegionSyncDTO::getSynDingDeptId, RegionSyncDTO::getId));
        }
        List<StoreSyncDTO> storeDOList = storeService.getAllStoreIdsAndDeptId(eid);
        List<String> removeStoreList = new ArrayList<>();
        Map<String, Object> storeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(storeDOList)) {
            removeStoreList = storeDOList.stream()
                    .filter((StoreSyncDTO s) -> StringUtils.isBlank(s.getSynDingDeptId()))
                    .map(e -> String.valueOf(e.getId())).collect(Collectors.toList());
            storeMap = storeDOList.stream().filter((StoreSyncDTO s) ->
                    StringUtils.isNotBlank(s.getSynDingDeptId())&&s.getId()!=null).collect(Collectors.toMap(StoreSyncDTO::getSynDingDeptId, StoreSyncDTO::getId));
        }
        //有效期一天 完成任务删除
        redisUtil.putAll(storeKey, storeMap, 1L, TimeUnit.DAYS);
        //有效期一天
        redisUtil.putAll(regionKey, regionMap, 1L, TimeUnit.DAYS);

        while (!queue.isEmpty()) {
            int size = queue.size();
            //门店区域
            List<RegionDO> storeRegionList = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                RegionDO deptInfo = queue.poll();

                Long parentId = deptInfo.getId();
                if(deptInfo.getStoreRange() == null){
                    deptInfo.setStoreRange(false);
                }
                //是门店则结束探下级
                if(deptInfo.getStoreRange()){
                    storeRegionList.add(deptInfo);
                    continue;
                }

                //查询下一级节点
                List<RegionDTO> deptList = parentGroup.get(deptInfo.getSynDingDeptId());

                log.info(" deptList synDingDeptId : {} deptList.size:{}", deptInfo.getSynDingDeptId(), deptList == null ? 0 : deptList.size());
                //叶子节点
                if (CollectionUtils.isEmpty(deptList)) {
                    log.info("叶子节点：deptId:{}, synDingDeptId:{}", deptInfo.getId(), deptInfo.getSynDingDeptId());
                    continue;
                }


                List<RegionDO> regionInsertList = new ArrayList<>();
                List<RegionDO> regionUpdateList = new ArrayList<>();
                List<ExtendFieldInfoDTO> finalExtendFieldInfoDTOS = extendFieldInfoDTOS;
                deptList.forEach(e -> {
                    RegionDO region = new RegionDO();
                    region.setParentId(String.valueOf(parentId));
                    region.setName(e.getName());
                    region.setCreateTime(System.currentTimeMillis());
                    region.setUpdateTime(System.currentTimeMillis());
                    region.setSynDingDeptId(String.valueOf(e.getSynDingDeptId()));
                    region.setRegionType(RegionTypeEnum.PATH.getType());
                    region.setStoreRange(e.getStoreRange());
                    region.setAddress(e.getAddress());
                    region.setStoreCode(e.getStoreCode());
                    region.setLongitude(e.getLongitude());
                    region.setLatitude(e.getLatitude());
                    region.setThirdRegionType(e.getThirdRegionType());
                    if (e.getStoreStatus() != null){
                        region.setStoreStatus(e.getStoreStatus());
                    }
                    if(region.getStoreRange()){
                        log.info("门店");
                        if (BailiEnterpriseEnum.bailiAffiliatedCompany(eid)){
                            Map<String, String> collect = finalExtendFieldInfoDTOS.stream().collect(Collectors.toMap(ExtendFieldInfoDTO::getExtendFieldName, ExtendFieldInfoDTO::getExtendFieldKey));
                            List<String> allNames = BailiCustomizeFieldEnum.getAllNames();
                            List<Object> extendFiledDataList = new ArrayList<>();
                            for (String name:allNames) {
                                ExtendFieldDataDTO extendFieldDataDTO = new ExtendFieldDataDTO();
                                extendFieldDataDTO.setExtendFieldKey(collect.get(name));
                                if (name.equals(BailiCustomizeFieldEnum.BIZCITY.getName())){
                                    extendFieldDataDTO.setExtendFieldValue(e.getBizCity());
                                }else if (name.equals(BailiCustomizeFieldEnum.MANGERCITY.getName())){
                                    extendFieldDataDTO.setExtendFieldValue(e.getMangerCity());
                                }else if (name.equals(BailiCustomizeFieldEnum.BRAND.getName())){
                                    extendFieldDataDTO.setExtendFieldValue(e.getBrand());
                                }else if (name.equals(BailiCustomizeFieldEnum.ZONENAME.getName())){
                                    extendFieldDataDTO.setExtendFieldValue(e.getZoneName());
                                }else if (name.equals(BailiCustomizeFieldEnum.PROVINCENAME.getName())){
                                    extendFieldDataDTO.setExtendFieldValue(e.getProvinceName());
                                }
                                extendFiledDataList.add(extendFieldDataDTO);
                            }
                            region.setExtendField(JSONObject.toJSONString(extendFiledDataList));
                        }
                    }
                    String regionPath;
                    if(StringUtils.isBlank(deptInfo.getRegionPath())){
                        regionPath = "/1/";
                    }else {
                        regionPath = deptInfo.getRegionPath().endsWith("/") ? deptInfo.getRegionPath() + deptInfo.getId()  + "/" :
                                deptInfo.getRegionPath() + "/" + deptInfo.getId()  + "/" ;
                    }
                    if(!regionPath.startsWith("/")){
                        regionPath =  "/" + regionPath;
                    }
                    regionPath = regionPath.replaceAll("//", "/");
                    region.setRegionPath(regionPath);
                    region.setDeleted(false);
                    String deptId = e.getSynDingDeptId();
                    String id = redisUtil.hashGetString(regionKey, deptId);
                    if (StringUtils.isNotBlank(id)) {
                        region.setId(Long.valueOf(id));
                        //删除对应的已有redis key缓存
                        redisUtil.delete(regionKey, deptId);
                        regionUpdateList.add(region);
                    } else {
                        regionInsertList.add(region);
                    }
                });
                log.info("regionUpdateList：size:{}", regionUpdateList.size());
                if (CollectionUtils.isNotEmpty(regionUpdateList)) {
                    log.info("regionUpdateList：int");
                    for(RegionDO regionDO : regionUpdateList){
                        //批量插入或更新
                        regionService.saveRegionAndStore(eid, regionDO, userId);
                    }
                    queue.addAll(regionUpdateList);
                }
                log.info("regionInsertList：size:{}", regionInsertList.size());
                if (CollectionUtils.isNotEmpty(regionInsertList)) {
                    log.info("regionInsertList：int");
                    try {
                        for(RegionDO regionDO : regionInsertList){
                            //批量插入或更新
                            regionService.saveRegionAndStore(eid, regionDO, userId);
                        }
                    } catch (Exception e) {
                        log.error("保存部门异常 eid :{} deptId :{} deptName :{}", eid, deptInfo.getId(), deptInfo.getName());
                        log.error("保存部门异常细节",e);
                        if (e instanceof DuplicateKeyException) {
                            throw new ServiceException(deptInfo.getName() + "[" + deptInfo.getId() + "] 下级部门和其他同步部门有上下级关系");
                        }
                        throw new ServiceException(deptInfo.getName() + "[" + deptInfo.getId() + "] 保存异常");
                    }
                    queue.addAll(regionInsertList);
                }
            }
        }


        Map<String, Object> leftMap = redisUtil.entries(regionKey);
        List<Long> regionIdList = new ArrayList<>();
        if (!leftMap.isEmpty()) {
            for (Map.Entry<String, Object> entry : leftMap.entrySet()) {
                if (SyncConfig.ROOT_DEPT_ID_STR.equals(entry.getKey()) || SyncConfig.DELETE_DEPT_ID.equals(entry.getKey())) {
                    continue;
                }
                regionIdList.add(Long.valueOf(entry.getValue().toString()));
            }
        }
        //移除删除的钉钉区域
        if (CollectionUtils.isNotEmpty(regionIdList) && !Constants.SENYU_ENTERPRISE_ID.equals(eid) ) {
            regionService.removeRegions(eid, regionIdList);
        }
        //移除自有的区域
        if (CollectionUtils.isNotEmpty(regionSelfList) && !Constants.SENYU_ENTERPRISE_ID.equals(eid)) {
            Lists.partition(regionSelfList, Constants.BATCH_INSERT_COUNT).forEach(idList -> regionService.removeRegions(eid, idList));
        }
        Map<String, Object> leftStoreMap = redisUtil.entries(storeKey);
        if (!leftStoreMap.isEmpty()) {
            for (Map.Entry<String, Object> entry : leftStoreMap.entrySet()) {
                removeStoreList.add(entry.getValue().toString());
            }
        }
        //移除无用门店
        if (CollectionUtils.isNotEmpty(removeStoreList) && !Constants.SENYU_ENTERPRISE_ID.equals(eid)) {
            List<String> storeIds = storeMapper.getStoreIdByIdList(eid, removeStoreList);
            if(CollectionUtils.isNotEmpty(storeIds)){
                List<StoreDO> removeStoreDOList = storeMapper.getByStoreIdList(eid, storeIds);
                if(CollectionUtils.isNotEmpty(removeStoreDOList)){
                    Map<Long, StoreDO> removeStoreDOMap = ListUtils.emptyIfNull(removeStoreDOList).stream()
                            .collect(Collectors.toMap(StoreDO::getId, data -> data, (a, b) -> a));
                    StoreDO defaultStore = removeStoreDOMap.get(Constants.DEFAULT_STORE_ID);
                    if(defaultStore != null && Constants.SENYU_ENTERPRISE_ID.equals(eid)){
                        storeIds.remove(defaultStore.getStoreId());
                    }
                }
                if(CollectionUtils.isNotEmpty(storeIds)){
                    Lists.partition(storeIds, Constants.BATCH_INSERT_COUNT).forEach(idList -> storeService.deleteByStoreIds(eid, idList, userId));
                }
            }
        }
        if(Constants.SENYU_ENTERPRISE_ID.equals(eid)){
            StoreDO defaultStore = storeService.getById(eid, Constants.DEFAULT_STORE_ID);
            if(defaultStore != null){
                RegionDO storeRegion = regionService.getByStoreId(eid, defaultStore.getStoreId());
                if(storeRegion != null){
                    regionService.updateTestRegion(eid, defaultStore.getRegionId(), storeRegion.getId());
                }
            }

        }
        //发消息计算门店数量
        simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumRecursionMsgDTO(eid, Long.valueOf(SyncConfig.ROOT_DEPT_ID))), RocketMqTagEnum.CAL_REGION_STORE_NUM);
    }


    /**
     * 对于百丽类型企业，同步时 初始化门店扩展信息
     * @param enterpriseId
     * @param extendFieldInfo
     */
    public List<ExtendFieldInfoDTO> initStoreExtendFieldInfo(String enterpriseId,String extendFieldInfo){
        List<String> extendFieldNames = new ArrayList<>();
        List<ExtendFieldInfoDTO> extendFieldInfoDTOList = new ArrayList<>();
        if (StringUtils.isEmpty(extendFieldInfo)){
            //所有的名称,插入到
            extendFieldNames = BailiCustomizeFieldEnum.getAllNames();
        }else {
            try {
                extendFieldInfoDTOList = JSONObject.parseArray(extendFieldInfo, ExtendFieldInfoDTO.class);
            } catch (Exception e) {
                log.error("扩展字段信息json转换异常！{}",e.getMessage(),e);
            }
            List<String> oldNames = extendFieldInfoDTOList.stream().map(ExtendFieldInfoDTO::getExtendFieldName).collect(Collectors.toList());
            extendFieldNames = BailiCustomizeFieldEnum.getExcludeNames(oldNames);
        }
        //只需要保证企业库中有百丽的4个自定义字段即可
        for (String name:extendFieldNames) {
            ExtendFieldInfoDTO extendFieldInfoDTO = new ExtendFieldInfoDTO();
            extendFieldInfoDTO.setExtendFieldType("1");
            extendFieldInfoDTO.setExtendFieldName(name);
            extendFieldInfoDTO.setExtendFieldKey(PREFIX+System.currentTimeMillis() + new Random().nextInt(1000));
            extendFieldInfoDTOList.add(extendFieldInfoDTO);
        }
        if (CollectionUtils.isEmpty(extendFieldInfoDTOList)){
            return new ArrayList<>();
        }
        extendFieldInfo = JSONObject.toJSONString(extendFieldInfoDTOList);
        enterpriseStoreSettingMapper.updateExtendField(enterpriseId,extendFieldInfo);
        return extendFieldInfoDTOList;
    }

    @Override
    public void newSyncOrgAll(String eid,Long regionId ,String unitId, List<RegionDTO> resultList) {
        if(CollectionUtils.isEmpty(resultList)){
            return;
        }
        //先去重
        resultList = resultList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                new TreeSet<>(Comparator.comparing(RegionDTO::getSynDingDeptId))), ArrayList::new));
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());

        String userId = "";
        String regionKey = redisConstantUtil.getSyncRegionKey(eid);
        String storeKey =  redisConstantUtil.getSyncStoreKey(eid);

        Map<String, RegionDTO> mapResult = resultList.stream().collect(Collectors.toMap(RegionDTO::getSynDingDeptId, org -> org));
        //取的是跟节点或者指定节点
        // TODO: 2022/8/2
        RegionDTO root = mapResult.get(unitId);
        if(root == null){
            throw new ServiceException("需要同步的节点不存在，同步失败 unitId:" + unitId);
        }

        //上下级一致修改为根节点
        resultList.forEach(e ->{
            if(StringUtils.isNotBlank(e.getSynDingDeptId()) && e.getSynDingDeptId().equals(e.getParentId())){
                e.setParentId(root.getSynDingDeptId());
            }
        });

        //根据父节点分组
        Map<String, List<RegionDTO>> parentGroup = ListUtils.emptyIfNull(resultList)
                .stream()
                .collect(Collectors.groupingBy(RegionDTO::getParentId));

        //构建根节点
        root.setSynDingDeptId(unitId);

        //如果是节点同步，regionId就是该次同步的头节点，如果不设置为根节点，数据将会有问题
        if (regionId!=null){
            root.setId(regionId);
        }
        //构建数据
        RegionDO rootRegion = prepareRegionRoot(eid, userId, root);
        if(rootRegion == null){
            return;
        }
        Queue<RegionDO> queue = new LinkedList<>();
        queue.add(rootRegion);

        //节点同步的时候，需要将该节点个regionPath拿到，之后的数据都是基于该regionPath
        if (regionId!=null){
            RegionDO regionDO = regionMapper.getByRegionId(eid, regionId);
            rootRegion.setRegionPath(regionDO.getRegionPath());
            rootRegion.setParentId(regionDO.getParentId());
        }
        List<RegionSyncDTO> regionList = regionService.getSpecifiedRegionIdAndDeptId(eid,regionId);
        List<Long> regionSelfList = new ArrayList<>();
        Map<String, Object> regionMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(regionList)) {
            regionSelfList = regionList.stream()
                    .filter((RegionSyncDTO s) -> StringUtils.isBlank(s.getSynDingDeptId())).map(RegionSyncDTO::getId)
                    .collect(Collectors.toList());

            regionMap = regionList.stream().filter((RegionSyncDTO s) -> StringUtils.isNotBlank(s.getSynDingDeptId())&&s.getId()!=null).collect(Collectors.toMap(RegionSyncDTO::getSynDingDeptId, RegionSyncDTO::getId));
        }
        List<StoreSyncDTO> storeDOList = storeService.getSpecifiedStoreIdsAndDeptId(eid,regionId);
        List<String> removeStoreList = new ArrayList<>();
        Map<String, Object> storeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(storeDOList)) {
            removeStoreList = storeDOList.stream()
                    .filter((StoreSyncDTO s) -> StringUtils.isBlank(s.getSynDingDeptId()))
                    .map(e -> String.valueOf(e.getId())).collect(Collectors.toList());
            storeMap = storeDOList.stream().filter((StoreSyncDTO s) ->
                    StringUtils.isNotBlank(s.getSynDingDeptId())&&s.getId()!=null).collect(Collectors.toMap(StoreSyncDTO::getSynDingDeptId, StoreSyncDTO::getId));
        }
        //有效期一天 完成任务删除
        redisUtil.putAll(storeKey, storeMap, 1L, TimeUnit.DAYS);
        //有效期一天
        redisUtil.putAll(regionKey, regionMap, 1L, TimeUnit.DAYS);


        while (!queue.isEmpty()) {
            int size = queue.size();
            //门店区域
            List<RegionDO> storeRegionList = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                RegionDO deptInfo = queue.poll();

                Long parentId = deptInfo.getId();
                if(deptInfo.getStoreRange() == null){
                    deptInfo.setStoreRange(false);
                }
                //是门店则结束探下级
                if(deptInfo.getStoreRange()){
                    storeRegionList.add(deptInfo);
                    continue;
                }

                //查询下一级节点
                List<RegionDTO> deptList = parentGroup.get(deptInfo.getSynDingDeptId());

                log.info(" deptList deptId : {} deptList.size:{}", deptInfo.getSynDingDeptId(), deptList == null ? 0 : deptList.size());
                //叶子节点
                if (CollectionUtils.isEmpty(deptList)) {
                    log.info("叶子节点：deptId:{}", deptInfo.getId());
                    continue;
                }


                List<RegionDO> regionInsertList = new ArrayList<>();
                List<RegionDO> regionUpdateList = new ArrayList<>();
                deptList.forEach(e -> {
                    RegionDO region = new RegionDO();
                    region.setParentId(String.valueOf(parentId));
                    region.setName(e.getName());
                    region.setCreateTime(System.currentTimeMillis());
                    region.setUpdateTime(System.currentTimeMillis());
                    region.setSynDingDeptId(String.valueOf(e.getSynDingDeptId()));
                    region.setRegionType(RegionTypeEnum.PATH.getType());
                    region.setStoreRange(e.getStoreRange());
                    region.setAddress(e.getAddress());
                    region.setStoreCode(e.getStoreCode());
                    region.setLongitude(e.getLongitude());
                    region.setLatitude(e.getLatitude());
                    region.setOpenDate(e.getOpenDate());
                    if(region.getStoreRange()){
                        log.info("门店");
                        //转换经纬度
//                        changeGaode(region);
                    }
                    String regionPath;
                    if(StringUtils.isBlank(deptInfo.getRegionPath())){
                        regionPath = "/1/";
                    }else {
                        regionPath = deptInfo.getRegionPath().endsWith("/") ? deptInfo.getRegionPath() + deptInfo.getId()  + "/" :
                                deptInfo.getRegionPath() + "/" + deptInfo.getId()  + "/" ;
                    }
                    if(!regionPath.startsWith("/")){
                        regionPath =  "/" + regionPath;
                    }
                    regionPath = regionPath.replaceAll("//", "/");
                    region.setRegionPath(regionPath);
                    region.setDeleted(false);
                    String deptId = e.getSynDingDeptId();
                    String id = redisUtil.hashGetString(regionKey, deptId);
                    if (StringUtils.isNotBlank(id)) {
                        region.setId(Long.valueOf(id));
                        //删除对应的已有redis key缓存
                        redisUtil.delete(regionKey, deptId);
                        regionUpdateList.add(region);
                    } else {
                        regionInsertList.add(region);
                    }
                });
                log.info("regionUpdateList：size:{}", regionUpdateList.size());
                if (CollectionUtils.isNotEmpty(regionUpdateList)) {
                    log.info("regionUpdateList：int");
                    for(RegionDO regionDO : regionUpdateList){
                        //批量插入或更新
                        regionService.saveRegionAndStore(eid, regionDO, userId);
                    }
                    queue.addAll(regionUpdateList);
                }
                log.info("regionInsertList：size:{}", regionInsertList.size());
                if (CollectionUtils.isNotEmpty(regionInsertList)) {
                    log.info("regionInsertList：int");
                    try {
                        for(RegionDO regionDO : regionInsertList){
                            //批量插入或更新
                            regionService.saveRegionAndStore(eid, regionDO, userId);
                        }
                    } catch (Exception e) {
                        log.error("保存部门异常 eid :{} deptId :{} deptName :{}", eid, deptInfo.getId(), deptInfo.getName());
                        log.error("保存部门异常细节",e);
                        if (e instanceof DuplicateKeyException) {
                            throw new ServiceException(deptInfo.getName() + "[" + deptInfo.getId() + "] 下级部门和其他同步部门有上下级关系");
                        }
                        throw new ServiceException(deptInfo.getName() + "[" + deptInfo.getId() + "] 保存异常");
                    }
                    queue.addAll(regionInsertList);
                }
            }
        }


        Map<String, Object> leftMap = redisUtil.entries(regionKey);
        List<Long> regionIdList = new ArrayList<>();
        if (!leftMap.isEmpty()) {
            for (Map.Entry<String, Object> entry : leftMap.entrySet()) {
                if (SyncConfig.ROOT_DEPT_ID_STR.equals(entry.getKey()) || SyncConfig.DELETE_DEPT_ID.equals(entry.getKey())) {
                    continue;
                }
                if (SyncConfig.ROOT_DEPT_ID_STR.equals(entry.getValue().toString())) {
                    continue;
                }
                regionIdList.add(Long.valueOf(entry.getValue().toString()));
            }
        }
        //移除删除的钉钉区域
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            regionService.removeRegions(eid, regionIdList);
        }
        //移除自有的区域
        if (CollectionUtils.isNotEmpty(regionSelfList)) {
            Lists.partition(regionSelfList, Constants.BATCH_INSERT_COUNT).forEach(idList -> regionService.removeRegions(eid, idList));
        }
        Map<String, Object> leftStoreMap = redisUtil.entries(storeKey);
        if (!leftStoreMap.isEmpty()) {
            for (Map.Entry<String, Object> entry : leftStoreMap.entrySet()) {
                removeStoreList.add(entry.getValue().toString());
            }
        }
        //移除无用门店
        if (CollectionUtils.isNotEmpty(removeStoreList)) {
            List<String> storeIds = storeMapper.getStoreIdByIdList(eid, removeStoreList);
            if(CollectionUtils.isNotEmpty(storeIds)){
                List<StoreDO> removeStoreDOList = storeMapper.getByStoreIdList(eid, storeIds);
                if(CollectionUtils.isNotEmpty(removeStoreDOList)){
                    Map<Long, StoreDO> removeStoreDOMap = ListUtils.emptyIfNull(removeStoreDOList).stream()
                            .collect(Collectors.toMap(StoreDO::getId, data -> data, (a, b) -> a));
                    StoreDO defaultStore = removeStoreDOMap.get(Constants.DEFAULT_STORE_ID);
                    if(defaultStore != null && Constants.SENYU_ENTERPRISE_ID.equals(eid)){
                        storeIds.remove(defaultStore.getStoreId());
                    }
                }
                if(CollectionUtils.isNotEmpty(storeIds)){
                    Lists.partition(storeIds, Constants.BATCH_INSERT_COUNT).forEach(idList -> storeService.deleteByStoreIds(eid, idList, userId));
                }
            }
        }
        if(Constants.SENYU_ENTERPRISE_ID.equals(eid)){
            StoreDO defaultStore = storeService.getById(eid, Constants.DEFAULT_STORE_ID);
            if(defaultStore != null){
                RegionDO storeRegion = regionService.getByStoreId(eid, defaultStore.getStoreId());
                if(storeRegion != null){
                    regionService.updateTestRegion(eid, defaultStore.getRegionId(), storeRegion.getId());
                }
            }

        }
        //发消息计算门店数量
        simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumRecursionMsgDTO(eid, Long.valueOf(SyncConfig.ROOT_DEPT_ID))), RocketMqTagEnum.CAL_REGION_STORE_NUM);
    }



    private RegionDO prepareRegionRoot(String eid, String userId, RegionDTO deptRoot) {

        RegionDO regionDelete = regionService.getRegionByIdIgnoreDelete(eid, SyncConfig.DELETE_DEPT_ID);

        if (regionDelete == null) {
            regionService.insertRoot(eid, RegionDO.builder().name("删除区域")
                    .createName(userId)
                    .createTime(System.currentTimeMillis())
                    .deleted(Boolean.TRUE)
                    .id(Long.valueOf(SyncConfig.DELETE_DEPT_ID))
                    .build());
        }

        String regionType = "";
        if (deptRoot.getSynDingDeptId().equals(SyncConfig.ROOT_DEPT_ID.toString())){
            regionType = RegionTypeEnum.ROOT.getType();
        }else {
            regionType = deptRoot.getStoreRange()? RegionTypeEnum.STORE.getType():RegionTypeEnum.PATH.getType();
        }
        RegionDO rootDO = RegionDO.builder().name(deptRoot.getName()).
                synDingDeptId(deptRoot.getSynDingDeptId())
                .updateTime(System.currentTimeMillis())
                .deleted(false)
                .parentId(deptRoot.getParentId())
                .id(deptRoot.getId())
                .regionType(regionType)
                .build();
        if(deptRoot.getId() == null){
            rootDO.setId(Long.valueOf(SyncConfig.ROOT_DEPT_ID));
            rootDO.setParentId("0");
        }
        regionService.insertOrUpdate(rootDO, eid);
        return rootDO;
    }
}
