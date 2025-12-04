package com.coolcollege.intelligent.facade;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.LxzEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.myj.MyjEnterpriseEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.convert.ConvertFactory;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dto.AuthScopeDTO;
import com.coolcollege.intelligent.dto.SysDepartmentDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.enums.StoreStatusEnum;
import com.coolcollege.intelligent.model.openApi.request.SyncStoreRequest;
import com.coolcollege.intelligent.model.openApi.vo.SyncStoreVO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.AsyncDingRequestDTO;
import com.coolcollege.intelligent.model.region.dto.RegionNode;
import com.coolcollege.intelligent.model.region.dto.RegionStoreNumRecursionMsgDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.baili.ThirdOaDeptSyncService;
import com.coolcollege.intelligent.service.dingSync.DingDeptSyncService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.enterprise.FsService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 同步组织/区域/门店/部门
 *
 * @author byd
 */
@Service
@Slf4j
public class SyncDeptFacade {

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DingDeptSyncService dingDeptSyncService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private RegionMapper regionMapper;

    @Autowired
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Autowired
    private StoreService storeService;

    @Autowired
    private EnterpriseSettingService enterpriseSettingService;
    @Resource
    FsService fsService;

    @Autowired
    private RedisConstantUtil redisConstantUtil;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ThirdOaDeptSyncService thirdOaDeptSyncService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;

    @Resource
    private ConvertFactory convertFactory;
    @Resource
    private SysDepartmentMapper sysDepartmentMapper;

    public void sync(String eid, String userName, String userId) throws ApiException {


        if (!redisUtilPool.setNxExpire(redisConstantUtil.getSyncLockKey(eid), eid, 5)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "同步中，请稍后再试");
        }

        String eidLockKey = redisConstantUtil.getSyncEidEffectiveKey(eid);
        if (StringUtils.isNotBlank(redisUtilPool.getString(eidLockKey))) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "一天内只能同步一次");
        }

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        if(Objects.isNull(enterpriseConfigDO) || Objects.isNull(enterpriseSettingVO)){
            return;
        }
        if (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_NOT_OPEN)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "未开启同步");
        }
        AsyncDingRequestDTO asyncDingRequestDTO = new AsyncDingRequestDTO();
        asyncDingRequestDTO.setDingCorpId(enterpriseConfigDO.getDingCorpId());
        asyncDingRequestDTO.setEid(eid);
        asyncDingRequestDTO.setDbName(enterpriseConfigDO.getDbName());
        asyncDingRequestDTO.setUserName(userName);
        asyncDingRequestDTO.setUserId(userId);
        asyncDingRequestDTO.setEnterpriseSettingVO(enterpriseSettingVO);
        asyncDingRequestDTO.setAppType(enterpriseConfigDO.getAppType());
        if(Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD) || MyjEnterpriseEnum.myjCompany(eid)){
            //加入锁
            //todo 时间设置为1小时
            redisUtilPool.setString(eidLockKey, eid,  60*60);
            simpleMessageService.send(JSONObject.toJSONString(asyncDingRequestDTO),RocketMqTagEnum.DING_SYNC_ALL_DATA_OA_QUEUE);
        }else {
            simpleMessageService.send(JSONObject.toJSONString(asyncDingRequestDTO),RocketMqTagEnum.DING_SYNC_ALL_DATA_QUEUE);
        }
    }


    public void syncDingOnePartyDept(String eid, String corpId) throws ApiException {
        //同步部门
        dingDeptSyncService.syncDingDepartmentAll(eid, corpId);
    }

    public void syncRegionAndStore(String enterpriseId, RegionDO region, EnterpriseSettingVO enterpriseSetting, Long syncLogId){
        log.info("eid:{},regionId：{}，enterpriseSettingVO：{}",enterpriseId, region.getId(), JSONObject.toJSONString(enterpriseSetting));
        //查询指定区域的区域ID 如果specifiedDeptId=null 查询全部
        List<RegionDO> regionList = regionMapper.getAllRegionInfo(enterpriseId);
        Map<String, RegionDO> syncDingRegionMap = regionList.stream().filter(o->Objects.nonNull(o) && StringUtils.isNotBlank(o.getSynDingDeptId()))
                .collect(Collectors.toMap(RegionDO::getSynDingDeptId, Function.identity(), (k1, k2)-> k1));
        //获取指定部门下的所有部门 包含自己
        List<SysDepartmentDO> departmentList = sysDepartmentMapper.getDeptListBySyncId(enterpriseId, syncLogId);
        dealDept(departmentList, enterpriseSetting, region.getSynDingDeptId());
        ListUtils.partition(departmentList, 1000).forEach(deptList->sysDepartmentMapper.batchUpdateDept(enterpriseId, deptList));
        List<String> syncRegionDeptIds = departmentList.stream().filter(SysDepartmentDO::getIsSyncRegion).map(SysDepartmentDO::getId).collect(Collectors.toList());
        for (SysDepartmentDO sysDepartment : departmentList) {
            if(!syncRegionDeptIds.contains(sysDepartment.getParentId()) && !Constants.ROOT_DEPT_ID_STR.equals(sysDepartment.getId())){
                sysDepartment.setParentId(Constants.ROOT_DEPT_ID_STR);
            }
        }
        String extendField = enterpriseSetting.getExtendField();
        StoreStatusEnum storeStatus = StoreStatusEnum.OPEN;
        if(StringUtils.isNotBlank(extendField)){
            JSONObject jsonObject = JSONObject.parseObject(extendField);
            storeStatus = StoreStatusEnum.parse(jsonObject.getString("newShopStatus"));
            if(Objects.isNull(storeStatus)){
                storeStatus = StoreStatusEnum.OPEN;
            }
        }
        Map<String, List<SysDepartmentDO>> subDeptMap = departmentList.stream().filter(o->Objects.nonNull(o.getParentId())).filter(SysDepartmentDO::getIsSyncRegion).collect(Collectors.groupingBy(SysDepartmentDO::getParentId,
                Collectors.collectingAndThen(Collectors.toList(), list->{list.sort(Comparator.comparing(SysDepartmentDO::getDepartOrder, Comparator.nullsLast(Comparator.naturalOrder()))); return list;})));
        String containRegionId = Constants.SLASH + region.getId() + Constants.SLASH;
        List<String> syncStoreDeptIds = departmentList.stream().filter(SysDepartmentDO::getIsStore).map(SysDepartmentDO::getId).collect(Collectors.toList());
        List<String> deleteStoreIds = new ArrayList<>();
        List<Long> deleteRegionIds = new ArrayList<>();

        List<String> removeSyncIdList = new ArrayList<>();

        if(LxzEnterpriseEnum.lxzCompany(enterpriseId)){
            SysDepartmentDO centerDO = sysDepartmentMapper.getDepartmentByName(enterpriseId, Constants.CHE_ZHENG_CENTER);
            if(centerDO != null){
                List<SysDepartmentDO> centerDeptList = sysDepartmentMapper.getAllSubDeptList(enterpriseId, centerDO.getId());
                if(CollectionUtils.isNotEmpty(centerDeptList)){
                    removeSyncIdList = centerDeptList.stream().map(SysDepartmentDO::getId).collect(Collectors.toList());
                }
                removeSyncIdList.add(centerDO.getId());
            }
        }

        for (RegionDO regionDO : regionList) {
            if(Constants.ROOT_DEPT_ID.equals(regionDO.getId())){
                //跟节点不处理
                continue;
            }
            if(Objects.nonNull(regionDO.getIsExternalNode()) && regionDO.getIsExternalNode()){
                continue;
            }
            if(StringUtils.isNotBlank(regionDO.getRegionPath()) && !regionDO.getRegionPath().contains(containRegionId)){
                //不是当天同步节点的子节点 不做处理
                continue;
            }
            if(!syncRegionDeptIds.contains(regionDO.getSynDingDeptId()) && StringUtils.isNotBlank(regionDO.getRegionPath()) && regionDO.getRegionPath().contains(containRegionId)){
                if(RegionTypeEnum.STORE.getType().equals(regionDO.getRegionType())){
                    deleteStoreIds.add(regionDO.getStoreId());
                }
                deleteRegionIds.add(regionDO.getId());
            }
            //兰湘子组织架构同步处理
            if(LxzEnterpriseEnum.lxzCompany(enterpriseId) && removeSyncIdList.contains(regionDO.getSynDingDeptId())){
                deleteRegionIds.add(regionDO.getId());
            }
            //表里现有的是门店 然后需要同步成区域的 则需要把老的门店删除
            if(RegionTypeEnum.STORE.getType().equals(regionDO.getRegionType()) && CollectionUtils.isNotEmpty(syncStoreDeptIds) && !syncStoreDeptIds.contains(regionDO.getSynDingDeptId())){
                deleteStoreIds.add(regionDO.getStoreId());
            }
        }

        List<SysDepartmentDO> syncDepartmentList = subDeptMap.get(region.getSynDingDeptId());
        //兰湘子组织架构同步处理
        if (LxzEnterpriseEnum.lxzCompany(enterpriseId)) {
            syncDepartmentList = processLxzEnterpriseSpecialLogic(enterpriseId, departmentList, subDeptMap, syncDepartmentList);
        }

        List<RegionDO> addOrUpdateList = new ArrayList<>();
        String regionPath = region.getFullRegionPath();
        for (SysDepartmentDO dept : ListUtils.emptyIfNull(syncDepartmentList)) {
            String syncDeptId = dept.getId();
            if(dept.getIsSyncRegion()){
                convertRegion(enterpriseId, dept, regionPath, addOrUpdateList, syncDingRegionMap, storeStatus);
            }
            dealSubDeptConvertRegion(enterpriseId, syncDeptId, subDeptMap, addOrUpdateList, syncDingRegionMap, storeStatus);
        }
        if(CollectionUtils.isNotEmpty(addOrUpdateList)){
            regionMapper.batchInsertOrUpdateRegion(enterpriseId, addOrUpdateList);
            List<RegionDO> storeRegion = addOrUpdateList.stream().filter(o -> RegionTypeEnum.STORE.getType().equals(o.getRegionType())).collect(Collectors.toList());
            for (RegionDO aDo : storeRegion) {
                aDo.setStoreRange(Boolean.TRUE);
                regionService.saveSyncRegionAndStore(enterpriseId, aDo, null);
            }
            addOrUpdateList.clear();
        }
        regionService.removeRegions(enterpriseId, deleteRegionIds);
        storeService.deleteSyncStoreByStoreIds(enterpriseId, deleteStoreIds, Constants.SYSTEM_USER_ID);
        // 删除存在门店但不存在区域的门店
        regionList = regionMapper.getAllRegionInfo(enterpriseId);
        Set<String> regionStoreIds = regionList.stream().map(RegionDO::getStoreId).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        List<StoreDO> allStore = storeService.getALlStoreList(enterpriseId);
        List<String> noExistRegionStoreIds = allStore.stream().map(StoreDO::getStoreId).filter(v -> !regionStoreIds.contains(v)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(noExistRegionStoreIds)) {
            storeService.deleteSyncStoreByStoreIds(enterpriseId, noExistRegionStoreIds, Constants.SYSTEM_USER_ID);
        }

        simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumRecursionMsgDTO(enterpriseId, Long.valueOf(SyncConfig.ROOT_DEPT_ID))), RocketMqTagEnum.CAL_REGION_STORE_NUM);
    }

    void dealSubDeptConvertRegion(String enterpriseId, String syncDeptId, Map<String, List<SysDepartmentDO>> subDeptMap, List<RegionDO> addOrUpdateList, Map<String, RegionDO> syncDingRegionMap, StoreStatusEnum storeStatus){
        List<SysDepartmentDO> deptList = subDeptMap.get(syncDeptId);
        if(CollectionUtils.isEmpty(deptList)){
            return;
        }
        RegionDO region = syncDingRegionMap.get(syncDeptId);
        String regionPath = Optional.ofNullable(region).map(RegionDO::getFullRegionPath).orElse(null);
        for (SysDepartmentDO dept : deptList) {
            String subDeptId = dept.getId();
            if(dept.getIsSyncRegion()){
                convertRegion(enterpriseId, dept, regionPath, addOrUpdateList, syncDingRegionMap, storeStatus);
            }
            if(dept.getIsLeaf() || dept.getIsStore()){
                continue;
            }
            dealSubDeptConvertRegion(enterpriseId, subDeptId, subDeptMap, addOrUpdateList, syncDingRegionMap, storeStatus);
        }
    }

    public void convertRegion(String enterpriseId, SysDepartmentDO dept, String regionPath, List<RegionDO> addOrUpdateList, Map<String, RegionDO> syncDingRegionMap, StoreStatusEnum storeStatus){
        String syncDeptId = dept.getId();
        RegionDO regionDO = syncDingRegionMap.get(syncDeptId);
        RegionDO parentRegion = syncDingRegionMap.get(dept.getParentId());
        String regionType = dept.getIsStore() ? RegionTypeEnum.STORE.getType() : RegionTypeEnum.PATH.getType();
        RegionDO updateRegion = new RegionDO();
        updateRegion.setName(dept.getName());
        updateRegion.setParentId(String.valueOf(parentRegion.getId()));
        updateRegion.setSynDingDeptId(syncDeptId);
        updateRegion.setDeleted(false);
        updateRegion.setRegionPath(regionPath);
        updateRegion.setCreateTime(System.currentTimeMillis());
        updateRegion.setUpdateTime(System.currentTimeMillis());
        updateRegion.setRegionType(regionType);
        updateRegion.setOrderNum(dept.getDepartOrder());
        updateRegion.setStoreStatus(storeStatus.getValue());
        if(Objects.nonNull(regionDO)){
            updateRegion.setId(regionDO.getId());
            addOrUpdateList.add(updateRegion);
        }else{
            regionMapper.batchInsertOrUpdateRegion(enterpriseId, Arrays.asList(updateRegion));
            if(dept.getIsStore()){
                updateRegion.setStoreRange(Boolean.TRUE);
                regionService.saveSyncRegionAndStore(enterpriseId, updateRegion, null);
            }
        }
        syncDingRegionMap.put(syncDeptId, updateRegion);
        if(CollectionUtils.isNotEmpty(addOrUpdateList) && addOrUpdateList.size() > Constants.MAX_QUERY_SIZE){
            regionMapper.batchInsertOrUpdateRegion(enterpriseId, addOrUpdateList);
            List<RegionDO> storeRegion = addOrUpdateList.stream().filter(o -> RegionTypeEnum.STORE.getType().equals(o.getRegionType())).collect(Collectors.toList());
            for (RegionDO aDo : storeRegion) {
                aDo.setStoreRange(Boolean.TRUE);
                regionService.saveSyncRegionAndStore(enterpriseId, aDo, null);
            }
            addOrUpdateList.clear();
        }
    }

    public void dealDept(List<SysDepartmentDO> deptList, EnterpriseSettingVO enterpriseSetting, String syncDeptId){
        Map<String, List<SysDepartmentDO>> parentDepMap = deptList.stream().filter(o->Objects.nonNull(o.getParentId())).collect(Collectors.groupingBy(SysDepartmentDO::getParentId));
        List<EnterpriseSettingVO.DingSyncOrgScope> regionSyncList = enterpriseSetting.getDingSyncOrgScopeList();
        //区域为空 默认根节点
        if (CollectionUtils.isEmpty(regionSyncList)) {
            regionSyncList = regionSyncList == null ? new ArrayList<>() : regionSyncList;
            EnterpriseSettingVO.DingSyncOrgScope syncOrgScope = new EnterpriseSettingVO.DingSyncOrgScope();
            syncOrgScope.setDingDeptId(SyncConfig.ROOT_DEPT_ID);
            regionSyncList.add(syncOrgScope);
        }
        List<String> syncDeptIds = regionSyncList.stream().map(EnterpriseSettingVO.DingSyncOrgScope::getDingDeptId).collect(Collectors.toList());
        Map<String, SysDepartmentDO> departmentMap = deptList.stream().collect(Collectors.toMap(SysDepartmentDO::getId, Function.identity(), (k1, k2)->k1));
        SysDepartmentDO syncRootDept = departmentMap.get(syncDeptId);
        if(Objects.isNull(syncRootDept)){
            log.info("没有获取到部门 {}", syncDeptId);
            throw new ServiceException(ErrorCodeEnum.ERROR, "没有获取到部门 {}", syncDeptId);
        }
        List<String> parentIds = null;
        if(StringUtils.isNotBlank(syncRootDept.getParentIds())){
            parentIds = Arrays.stream(syncRootDept.getParentIds().split(Constants.SLASH)).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(parentIds)){
            parentIds = Collections.singletonList(Constants.ROOT_DEPT_ID_STR);
        }
        //将父节点都设置成区域同步为true
        for (String parentId : parentIds) {
            SysDepartmentDO parentDept = departmentMap.get(parentId);
            if(Objects.nonNull(parentDept)){
                parentDept.setIsSyncRegion(Boolean.TRUE);
                parentDept.setIsStore(Boolean.FALSE);
                parentDept.setIsLeaf(Boolean.FALSE);
            }
        }
        List<SysDepartmentDO> rootSubDeptList = parentDepMap.get(syncDeptId);
        syncRootDept.setIsLeaf(CollectionUtils.isEmpty(rootSubDeptList));
        setDeptSyncField(syncRootDept, departmentMap, syncDeptIds, enterpriseSetting);
        if(CollectionUtils.isEmpty(rootSubDeptList)){
            log.info("没有获取到任何的子节点 {}", syncDeptId);
            return;
        }
        for (SysDepartmentDO sysDepartment : rootSubDeptList) {
            List<SysDepartmentDO> subDeptList = parentDepMap.get(sysDepartment.getId());
            sysDepartment.setIsLeaf(CollectionUtils.isEmpty(subDeptList));
            setDeptSyncField(sysDepartment, departmentMap, syncDeptIds, enterpriseSetting);
            dealSyncDept(subDeptList, departmentMap, parentDepMap, syncDeptIds, enterpriseSetting);
            if(!sysDepartment.getIsSyncRegion() && !sysDepartment.getIsLeaf()){
                //如果当前节点不在同步范围内，然后又是非叶子节点，叶子节点的所有节点都同步成门店区域，则当前节点也会同步成门店区域
                List<SysDepartmentDO> notSyncDeptList = subDeptList.stream().filter(o -> !o.getIsSyncRegion()).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(subDeptList) && CollectionUtils.isEmpty(notSyncDeptList)){
                    sysDepartment.setIsSyncRegion(Boolean.TRUE);
                }
            }
        }
    }

    private void setDeptSyncField(SysDepartmentDO sysDepartment, Map<String, SysDepartmentDO> departmentMap, List<String> syncDeptIds, EnterpriseSettingVO enterpriseSetting){
        String parentId = sysDepartment.getParentId();
        List<String> parentIds = new ArrayList<>();
        if(Objects.nonNull(parentId)){
            parentIds.add(parentId);
        }
        SysDepartmentDO parentDept = departmentMap.get(parentId);
        while (Objects.nonNull(parentDept)){
            String pId = parentDept.getParentId();
            parentDept = departmentMap.get(parentDept.getParentId());
            if(Objects.nonNull(pId)){
                parentIds.add(pId);
            }
        }
        Collections.reverse(parentIds);
        if(CollectionUtils.isNotEmpty(parentIds)){
            sysDepartment.setParentIds(Constants.SLASH + String.join(Constants.SLASH, parentIds) + Constants.SLASH);
        }
        boolean isSyncRegion = parentIds.stream().anyMatch(syncDeptIds::contains);
        sysDepartment.setIsSyncRegion(isSyncRegion || syncDeptIds.contains(sysDepartment.getId()));
        sysDepartment.setIsStore(storeRuleJudge(sysDepartment, enterpriseSetting));
    }

    private void dealSyncDept(List<SysDepartmentDO> deptList, Map<String, SysDepartmentDO> departmentMap, Map<String, List<SysDepartmentDO>> parentDepMap, List<String> syncDeptIds, EnterpriseSettingVO enterpriseSetting){
        if(CollectionUtils.isEmpty(deptList)){
            return;
        }
        for (SysDepartmentDO sysDepartment : deptList) {
            List<SysDepartmentDO> subDeptList = parentDepMap.get(sysDepartment.getId());
            sysDepartment.setIsLeaf(CollectionUtils.isEmpty(subDeptList));
            setDeptSyncField(sysDepartment, departmentMap, syncDeptIds, enterpriseSetting);
            dealSyncDept(subDeptList, departmentMap, parentDepMap, syncDeptIds, enterpriseSetting);
            if(!sysDepartment.getIsSyncRegion() && !sysDepartment.getIsLeaf()){
                //如果当前节点不在同步范围内，然后又是非叶子节点，叶子节点的所有节点都同步成门店区域，则当前节点也会同步成门店区域
                List<SysDepartmentDO> notSyncDeptList = subDeptList.stream().filter(o -> !o.getIsSyncRegion()).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(subDeptList) && CollectionUtils.isEmpty(notSyncDeptList)){
                    sysDepartment.setIsSyncRegion(Boolean.TRUE);
                }
            }
        }
    }

    public Boolean storeRuleJudge(SysDepartmentDO dept, EnterpriseSettingVO enterpriseSetting) {
        if(dept.getId().equals(SyncConfig.ROOT_DEPT_ID)){
            return false;
        }
        if(Objects.isNull(dept.getName()) || Objects.isNull(dept.getId())){
            return false;
        }
        String storeRuleCode = enterpriseSetting.getStoreRuleCode();
        //门店区域的规则(自定义下使用)
        String storeRuleValue = enterpriseSetting.getStoreRuleValue();
        if (SyncConfig.DING_SYNC_STORE_RULE_ALLLEAF.equals(storeRuleCode)) {
            //叶子节点为门店
            return dept.getIsLeaf();
        } else if (SyncConfig.DING_SYNC_STORE_RULE_CUSTOMREGULAR.equals(storeRuleCode)) {
            //自定义：正则
            return dept.getName().matches(storeRuleValue);
        } else if (SyncConfig.DING_SYNC_STORE_RULE_STORELEAF.equals(storeRuleCode)) {
            //手动选择的叶子节点，当规则为手动选择叶子节点时，该集合不为空
            Map<String, String> storeLeaf = enterpriseSetting.getDingSyncStoreScopeMap();
            if(Objects.isNull(storeLeaf)){
                return false;
            }
            List<String> targetDeptIds = new ArrayList<>(storeLeaf.keySet());
            List<String> parentIds = new ArrayList<>();
            if(StringUtils.isNotBlank(dept.getParentIds())){
                parentIds = Arrays.stream(dept.getParentIds().split(Constants.SLASH)).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            }
            boolean isSubDepartment = Collections.disjoint(targetDeptIds, parentIds);
            return Objects.nonNull(dept.getIsLeaf()) && dept.getIsLeaf() && (!isSubDepartment || targetDeptIds.contains(dept.getId()));
        } else if (SyncConfig.DING_SYNC_STORE_RULE_STORE_ENDSTRING.equals(storeRuleCode)) {
            //叶子节点为门店
            return dept.getName().endsWith(SyncConfig.DING_SYNC_STORE_RULE_ENDSTRING_VALUE) && dept.getIsLeaf();
        }
        //默认是以店为结尾的部门
        return dept.getName().endsWith(SyncConfig.DING_SYNC_STORE_RULE_ENDSTRING_VALUE);
    }


    /**
     *
     * @param enterpriseConfig
     * @param enterprise
     * @param syncDeptId
     * @param syncLogId
     */
    public void syncDept(EnterpriseConfigDO enterpriseConfig, EnterpriseDO enterprise, String syncDeptId, Long syncLogId){
        String corpId = enterpriseConfig.getDingCorpId(), enterpriseId = enterpriseConfig.getEnterpriseId();
        String appType = enterpriseConfig.getAppType();
        List<String> errorList = new ArrayList<>();
        List<String> allDeptIds = new ArrayList<>();
        allDeptIds.add(Constants.ROOT_DEPT_ID_STR);
        SysDepartmentDO rootSysDepartment = getRootSysDepartment(enterprise.getName(), syncLogId);
        List<String> dbDeptIds = sysDepartmentMapper.getAllSubDeptIdList(enterpriseConfig.getEnterpriseId(), syncDeptId);
        AuthScopeDTO authScope = null;
        int tryCount = 0;
        while (tryCount < Constants.INDEX_TEN && Objects.isNull(authScope)){
            try {
                authScope = enterpriseInitConfigApiService.getAuthScope(corpId, appType);
                break;
            } catch (Exception e) {
                try {
                    tryCount ++;
                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception apiException) {

                }
            }
        }
        if(Objects.isNull(authScope)){
            throw new ServiceException(ErrorCodeEnum.GET_AUTH_SCOPE_ERROR);
        }
        List<SysDepartmentDO> parentDeptList = Collections.singletonList(rootSysDepartment);
        List<String> deptIdList = authScope.getDeptIdList();
        if(!Constants.ROOT_DEPT_ID_STR.equals(syncDeptId) && Objects.nonNull(syncDeptId)){
            //指定节点同步 获取指定节点的父级部门 同时将父节点的同步id都纳入当前同步范围
            deptIdList = Collections.singletonList(syncDeptId);
            SysDepartmentDO sysDepartment = sysDepartmentMapper.selectById(enterpriseId, syncDeptId);
            if(Objects.isNull(sysDepartment)){
                throw new ServiceException(ErrorCodeEnum.SYNC_DEPT_ERROR, "部门不存在");
            }
            List<String> parentIds = Arrays.stream(sysDepartment.getParentIds().split(Constants.SLASH)).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(parentIds)){
                parentDeptList = sysDepartmentMapper.getDepartmentList(enterpriseId, parentIds);
                for (SysDepartmentDO sysDepartmentDO : parentDeptList) {
                    sysDepartmentDO.setSyncId(syncLogId);
                }
                allDeptIds.addAll(parentIds);
            }
        }
        sysDepartmentMapper.syncBatchInsertOrUpdate(parentDeptList, enterprise.getId());
        syncDept(enterpriseConfig, deptIdList, errorList, allDeptIds, syncLogId);
        tryCount = 0;
        //争对失败的数量做重试
        while (CollectionUtils.isNotEmpty(errorList) && tryCount < Constants.INDEX_TEN){
            log.info("重试次数：{}", tryCount);
            deptIdList = new ArrayList<>(errorList);
            errorList.clear();
            tryCount ++;
            syncDept(enterpriseConfig, deptIdList, errorList, allDeptIds, syncLogId);
        }
        if(CollectionUtils.isNotEmpty(errorList)){
            throw new ServiceException(ErrorCodeEnum.SYNC_DEPT_ERROR, errorList);
        }
        if (CollectionUtils.isNotEmpty(allDeptIds) && CollectionUtils.isNotEmpty(dbDeptIds)) {
            List<String> deleteDeptIds = dbDeptIds.stream().filter(dbDeptId -> !allDeptIds.contains(dbDeptId)).distinct().collect(Collectors.toList());
            deleteDept(enterpriseConfig, deleteDeptIds, allDeptIds, syncLogId);
        }
    }

    /**
     * 删除部门
     * @param enterpriseConfig
     * @param deleteDeptIds
     * @param allDeptIds
     */
    private void deleteDept(EnterpriseConfigDO enterpriseConfig, List<String> deleteDeptIds, List<String> allDeptIds, Long syncLogId){
        if(CollectionUtils.isEmpty(deleteDeptIds)){
            log.info("没有需要删除的部门");
            return;
        }
        log.info("处理删除节点：{}", deleteDeptIds);
        String corpId = enterpriseConfig.getDingCorpId();
        String appType = enterpriseConfig.getAppType();
        List<String> deleteIds = new ArrayList<>();
        List<SysDepartmentDO> addOrUpdateList = new ArrayList<>();
        for (String deleteDeptId : deleteDeptIds) {
            try {
                SysDepartmentDTO departmentDetail = enterpriseInitConfigApiService.getDepartmentDetail(corpId, deleteDeptId, appType);
                SysDepartmentDO sysDepartment = convertFactory.convertSysDepartmentDTO2SysDepartmentDO(departmentDetail, appType);
                if(Objects.isNull(sysDepartment)){
                    deleteIds.add(deleteDeptId);
                    continue;
                }
                if(!allDeptIds.contains(sysDepartment.getParentId())){
                    sysDepartment.setParentId(SyncConfig.ROOT_DEPT_ID_STR);
                }
                sysDepartment.setSyncId(syncLogId);
                addOrUpdateList.add(sysDepartment);
            } catch (ApiException e) {
                deleteIds.add(deleteDeptId);
            }
        }
        if(CollectionUtils.isNotEmpty(addOrUpdateList)){
            sysDepartmentMapper.syncBatchInsertOrUpdate(addOrUpdateList, enterpriseConfig.getEnterpriseId());
        }
        //删除不存在的数据
        if(CollectionUtils.isNotEmpty(deleteIds)){
            sysDepartmentMapper.deleteByIds(deleteIds, enterpriseConfig.getEnterpriseId());
        }
        log.info("删除部门处理完毕");
    }

    private SysDepartmentDO getRootSysDepartment(String corpName, Long syncLogId) {
        SysDepartmentDO rootDepartment = new SysDepartmentDO();
        rootDepartment.setId(SyncConfig.ROOT_DEPT_ID_STR);
        rootDepartment.setName(corpName);
        rootDepartment.setSyncId(syncLogId);
        rootDepartment.setIsSyncRegion(Boolean.TRUE);
        rootDepartment.setIsStore(Boolean.FALSE);
        rootDepartment.setIsLeaf(Boolean.FALSE);
        return rootDepartment;
    }



    /**
     * 同步部门
     * @param enterpriseConfig
     * @param deptIdList
     * @param errorList
     * @param allDeptIds
     * @param syncLogId
     */
    public void syncDept(EnterpriseConfigDO enterpriseConfig, List<String> deptIdList, List<String> errorList, List<String> allDeptIds, Long syncLogId){
        String enterpriseId = enterpriseConfig.getEnterpriseId();
        String corpId = enterpriseConfig.getDingCorpId();
        String appType = enterpriseConfig.getAppType();
        List<SysDepartmentDO> addOrUpdateList = new ArrayList<>();
        for (String deptId : deptIdList) {
            allDeptIds.add(deptId);
            if(SyncConfig.ROOT_DEPT_ID_STR.equals(deptId)){
                continue;
            }
            //非根节点的时候才处理
            try {
                SysDepartmentDTO departmentDetail = enterpriseInitConfigApiService.getDepartmentDetail(corpId, deptId, appType);
                SysDepartmentDO sysDepartment = convertFactory.convertSysDepartmentDTO2SysDepartmentDO(departmentDetail, appType);
                if(Objects.isNull(departmentDetail)){
                    continue;
                }
                sysDepartment.setSyncId(syncLogId);
                if(!allDeptIds.contains(sysDepartment.getParentId())){
                    sysDepartment.setParentId(SyncConfig.ROOT_DEPT_ID_STR);
                }
                addOrUpdateList.add(sysDepartment);
            } catch (ApiException e) {
                log.info("获取部门信息错误：{}", deptId);
                errorList.add(deptId);
            }
        }
        for (String deptId : deptIdList){
            syncSubDept(enterpriseConfig, deptId, errorList, allDeptIds, syncLogId, addOrUpdateList);
        }
        if(CollectionUtils.isNotEmpty(addOrUpdateList)){
            sysDepartmentMapper.syncBatchInsertOrUpdate(addOrUpdateList, enterpriseId);
            addOrUpdateList.clear();
        }
    }

    /**
     * 处理子部门
     * @param enterpriseConfig
     * @param deptId
     * @param errorList
     * @param allDeptIds
     * @param syncLogId
     * @param addOrUpdateList
     */
    public void syncSubDept(EnterpriseConfigDO enterpriseConfig, String deptId, List<String> errorList, List<String> allDeptIds, Long syncLogId, List<SysDepartmentDO> addOrUpdateList){
        String enterpriseId = enterpriseConfig.getEnterpriseId();
        String corpId = enterpriseConfig.getDingCorpId();
        String appType = enterpriseConfig.getAppType();
        List<SysDepartmentDTO> subDepartments = null;
        try {
            subDepartments = enterpriseInitConfigApiService.getSubDepartments(corpId, appType, deptId, false);
            if(CollectionUtils.isNotEmpty(subDepartments)){
                List<SysDepartmentDO> subDepartmentList = convertFactory.convertDeptList(subDepartments, appType, syncLogId);
                if(CollectionUtils.isNotEmpty(subDepartmentList)){
                    allDeptIds.addAll(subDepartmentList.stream().map(SysDepartmentDO::getId).distinct().collect(Collectors.toList()));
                    for (SysDepartmentDO sysDepartment : subDepartmentList) {
                        if(!allDeptIds.contains(sysDepartment.getParentId())){
                            sysDepartment.setParentId(Constants.ROOT_DEPT_ID_STR);
                        }
                    }
                    addOrUpdateList.addAll(subDepartmentList);
                }
            }
            if(CollectionUtils.isNotEmpty(addOrUpdateList) && addOrUpdateList.size() >= Constants.MAX_QUERY_SIZE){
                sysDepartmentMapper.syncBatchInsertOrUpdate(addOrUpdateList, enterpriseId);
                addOrUpdateList.clear();
            }
        } catch (ApiException e) {
            errorList.add(deptId);
        }
        //企业
        if(CollectionUtils.isNotEmpty(subDepartments) && !AppTypeEnum.isWxSelfAndPrivateType(appType) && !AppTypeEnum.isQwType(appType)){
            for (SysDepartmentDTO dept : subDepartments) {
                syncSubDept(enterpriseConfig, dept.getId(), errorList, allDeptIds, syncLogId, addOrUpdateList);
            }
        }
    }

    /**
     * 钉钉/企微 同步 支持按节点同步
     * @param eid
     * @param userName
     * @param userId
     */
    public void newSync(String eid, String userName, String userId,Long regionId)  {

        //全量同步的时候，一天只能同步一次
        // TODO: 2022/8/1 存在一个问题，就是节点同步的时候，节点下子节点较多，这块要不要限制同步次数
        String eidLockKey = redisConstantUtil.getSyncEidEffectiveKey(eid);

        if (regionId==null){
            if (StringUtils.isNotBlank(redisUtilPool.getString(eidLockKey))) {
                throw new ServiceException(ErrorCodeEnum.DING_TALK_LIMIT);
            }
        }

        //一个企业 同时只能有一个节点在同步 四个小时之后自动释放锁，防止死锁
        if (!redisUtilPool.setNxExpire(redisConstantUtil.getSyncLockKey(eid), eid, 60*60*4)) {
            throw new ServiceException(ErrorCodeEnum.DING_TALK_SYNCING);
        }

        DataSourceHelper.reset();
        //是否开启钉钉同步
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);

        AsyncDingRequestDTO asyncDingRequestDTO = new AsyncDingRequestDTO();
        asyncDingRequestDTO.setDingCorpId(enterpriseConfigDO.getDingCorpId());
        asyncDingRequestDTO.setEid(eid);
        asyncDingRequestDTO.setDbName(enterpriseConfigDO.getDbName());
        asyncDingRequestDTO.setUserName(userName);
        asyncDingRequestDTO.setUserId(userId);
        asyncDingRequestDTO.setEnterpriseSettingVO(enterpriseSettingVO);
        asyncDingRequestDTO.setAppType(enterpriseConfigDO.getAppType());
        asyncDingRequestDTO.setRegionId(regionId);
        log.info("sync_param:{}",JSONObject.toJSONString(asyncDingRequestDTO));
        if(Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD) || MyjEnterpriseEnum.myjCompany(eid)){
            //加入锁
            //todo 时间设置为1小时
            redisUtilPool.setString(eidLockKey, eid,  60*60);
            simpleMessageService.send(JSONObject.toJSONString(asyncDingRequestDTO),RocketMqTagEnum.DING_SYNC_ALL_DATA_OA_QUEUE);
        }else {
            simpleMessageService.send(JSONObject.toJSONString(asyncDingRequestDTO),RocketMqTagEnum.DING_SYNC_ALL_DATA_QUEUE);
        }
    }

    /**
     * 清除同步限制
     * @param eid
     */
    public void clearSyncLimit(String eid) {
        String eidLockKey = redisConstantUtil.getSyncEidEffectiveKey(eid);
        redisUtilPool.delKey(eidLockKey);
        String lockKey = "hd_" + RedisConstant.EID_SYNC_EFFECTIVE + eid;;
        redisUtilPool.delKey(lockKey);
    }

    @Transactional(rollbackFor = Exception.class)
    public SyncStoreVO open2SyncSingleStore(EnterpriseConfigDO enterpriseConfig, SyncStoreRequest request) {
        log.info("开始同步门店信息: {}", JSONObject.toJSONString(request));
        String enterpriseId = request.getEnterpriseId();
        String shopName = request.getShopName();
        // 分布式锁防止并发
        String lockKey = MessageFormat.format("enterpriseOpenSyncStore:{0}:{1}", enterpriseId, request.getShopCode());
        if (!redisUtilPool.setNxExpire(lockKey, "locked", Constants.NORMAL_LOCK_TIMES)) {
            throw new ServiceException(ErrorCodeEnum.STORE_SYNCING);
        }
        try {
            RegionNode parentRegion = regionService.getRegionById(enterpriseId, request.getRegionId());
            if (parentRegion == null) {
                throw new ServiceException(ErrorCodeEnum.REGION_NOT_EXIST);
            }
            // 检查是否已有相同名称的门店存在
            List<RegionDO> subRegion = regionService.getSubRegion(enterpriseId, parentRegion.getId());
            RegionDO existingRegion = ListUtils.emptyIfNull(subRegion).stream().filter(a -> a.getName().equals(shopName)).findFirst().orElse(null);
            if (existingRegion != null) {
                existingRegion.setStoreCode(request.getShopCode());
                storeService.handleExistingStore(enterpriseId, existingRegion);
                return new SyncStoreVO(enterpriseConfig.getAppType(), true);
            }
            // 获取第三方子部门信息
            List<SysDepartmentDTO> subDepartments = fetchSubDepartmentsWithRetry(enterpriseConfig, parentRegion.getSynDingDeptId(), shopName);
            if (CollectionUtils.isEmpty(subDepartments)) {
                throw new ServiceException(ErrorCodeEnum.SUB_REGION_NULL);
            }
            // 查找匹配的部门
            SysDepartmentDTO matchedDept = subDepartments.stream().filter(dept -> dept.getName().equals(shopName)).findFirst().orElse(null);;
            if (matchedDept == null) {
                throw new ServiceException(ErrorCodeEnum.SAME_NAME_REGION_NULL, shopName);
            }
            // 创建新的区域及门店记录
            createNewRegionAndStore(enterpriseId, request, matchedDept, request.getRegionId());
            return new SyncStoreVO(enterpriseConfig.getAppType(), true);
        } catch (Exception e) {
            log.error("同步门店失败，请求参数: {}, 错误信息: {}", JSONObject.toJSONString(request), e.getMessage(), e);
            throw e;
        } finally {
            // 释放分布式锁
            redisUtilPool.delKey(lockKey);
        }
    }

    private List<SysDepartmentDTO> fetchSubDepartmentsWithRetry(EnterpriseConfigDO config, String parentDeptId, String shopName) {
        int tryCount = 0;
        List<SysDepartmentDTO> subDepartments = null;
        while (tryCount < Constants.INDEX_THREE) {
            try {
                subDepartments = enterpriseInitConfigApiService.getSubDepartments(config.getDingCorpId(), config.getAppType(), parentDeptId, false);
                break;
            } catch (Exception e) {
                log.warn("获取子部门失败，第{}次重试，门店名: {}", ++tryCount, shopName, e);
            }
        }
        return subDepartments;
    }

    private void createNewRegionAndStore(String enterpriseId, SyncStoreRequest request, SysDepartmentDTO dept, String regionId) {
        RegionNode parentRegion = regionService.getRegionById(enterpriseId, regionId);
        RegionDO updateRegion = new RegionDO();
        updateRegion.setName(dept.getName());
        updateRegion.setParentId(String.valueOf(parentRegion.getId()));
        updateRegion.setSynDingDeptId(dept.getId());
        updateRegion.setDeleted(false);
        updateRegion.setRegionPath(parentRegion.getFullRegionPath());
        updateRegion.setCreateTime(System.currentTimeMillis());
        updateRegion.setUpdateTime(System.currentTimeMillis());
        updateRegion.setRegionType(RegionTypeEnum.STORE.getType());
        updateRegion.setOrderNum(dept.getDepartOrder());
        updateRegion.setStoreCode(request.getShopCode());
        updateRegion.setStoreRange(Boolean.TRUE);
        regionService.saveSyncRegionAndStore(enterpriseId, updateRegion, null);
    }

    /**
     * 处理LXZ企业的特殊逻辑：部门名称修改和厨政中心过滤
     * 返回处理后的syncDepartmentList
     */
    private List<SysDepartmentDO> processLxzEnterpriseSpecialLogic(String enterpriseId,
                                                                   List<SysDepartmentDO> departmentList,
                                                                   Map<String, List<SysDepartmentDO>> subDeptMap,
                                                                   List<SysDepartmentDO> syncDepartmentList) {

        String kitchenCenterDeptId = null;

        // 处理部门名称...
        for (SysDepartmentDO dept : departmentList) {
            if (dept.getName() != null) {
                if ("运营中心".equals(dept.getName())) {
                    dept.setName(dept.getName().replace("运营中心", "门店中心"));
                }
                if (Constants.CHE_ZHENG_CENTER.equals(dept.getName())) {
                    kitchenCenterDeptId = dept.getId();
                }
                if (dept.getName().contains("（前厅）")) {
                    dept.setName(dept.getName().replace("（前厅）", ""));
                }
            }
        }

        // 过滤掉厨政中心
        if (StringUtils.isNotBlank(kitchenCenterDeptId)) {
            subDeptMap.remove(kitchenCenterDeptId);
            String finalKitchenCenterDeptId = kitchenCenterDeptId;
            return syncDepartmentList.stream()
                    .filter(o -> !finalKitchenCenterDeptId.equals(o.getId()))
                    .collect(Collectors.toList());
        }

        return syncDepartmentList;
    }
}
