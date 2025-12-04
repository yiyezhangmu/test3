package com.coolcollege.intelligent.service.dingSync.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.QyNameReplaceEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataOperation;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataType;
import com.coolcollege.intelligent.common.enums.store.OnePartyStoreStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRequest;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.convert.ConvertFactory;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.store.dao.StoreGroupDao;
import com.coolcollege.intelligent.dao.store.dao.StoreGroupMappingDao;
import com.coolcollege.intelligent.dto.*;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseOperateLogDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionNode;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleCallBackRequest;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleFixedRequest;
import com.coolcollege.intelligent.model.scheduler.request.SchedulerAddRequest;
import com.coolcollege.intelligent.model.scheduler.request.SchedulerCalendarInfoRequest;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.StoreGroupMappingDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.dingSync.DingDeptSyncService;
import com.coolcollege.intelligent.service.dingSync.DingTalkClientService;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseOperateLogService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.sync.SyncContext;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtil;
import com.coolcollege.intelligent.util.ScheduleCallBackUtil;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.taobao.api.ApiException;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2021-03-22 16:13
 */
@Slf4j
@Service
public class DingDeptSyncServiceImpl implements DingDeptSyncService {

    @Autowired
    private RegionService regionService;

    @Lazy
    @Autowired
    private StoreService storeService;

    @Resource
    private SysDepartmentMapper sysDepartmentMapper;

    @Resource
    private StoreMapper storeMapper;

    @Autowired
    private DingTalkClientService dingTalkClientService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private EnterpriseOperateLogService enterpriseOperateLogService;

    @Autowired
    private DingService dingService;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Autowired
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;

    @Value("${scheduler.callback.task.url}")
    private String schedulerCallbackTaskUrl;

    @Value("${scheduler.api.url}")
    private String schedulerApiUrl;

    @Autowired
    ConvertFactory convertFactory;
    @Resource
    private StoreDao storeDao;
    @Resource
    private RedisConstantUtil redisConstantUtil;
    @Resource
    private StoreGroupDao storeGroupDao;
    @Resource
    private StoreGroupMappingDao storeGroupMappingDao;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;
    @Autowired
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;

    /**
     * 准备根节点和删除接单
     *
     * @param eid
     * @param userId
     * @return
     */
    private RegionDO prepareRegionRoot(String eid, String userId, String accessToken, EnterpriseConfigDO config) throws ApiException {
        RegionDO regionDelete = regionService.getRegionByIdIgnoreDelete(eid, SyncConfig.DELETE_DEPT_ID);

        if (regionDelete == null) {
            regionService.insertRoot(eid, RegionDO.builder().name("删除区域")
                    .createName(userId)
                    .createTime(System.currentTimeMillis())
                    .deleted(Boolean.TRUE)
                    .id(Long.valueOf(SyncConfig.DELETE_DEPT_ID))
                    .build());
        }

        RegionNode regionRoot = regionService.getRootRegion(eid);
        RegionDO rootDO = RegionDO.builder().name("根区域").
                synDingDeptId(SyncConfig.ROOT_DEPT_ID_STR)
                .updateTime(System.currentTimeMillis())
                .deleted(false)
                .id(Long.valueOf(SyncConfig.ROOT_DEPT_ID))
                .regionType(RegionTypeEnum.ROOT.getType())
                .build();
        String deptName = null;
        // 门店通应用根区域节点取通讯录根节点
        if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(config.getAppType())) {
            OpContactInfoDTO root = enterpriseInitConfigApiService.getContactInfo(config.getDingCorpId(), config.getAppType());
            if(Objects.nonNull(root)) {
                rootDO.setSynDingDeptId(root.getRootDeptId().toString());
                rootDO.setName(root.getName());
                // 门店通通讯录code
                rootDO.setContactCode(root.getCode());
                // 门店通oa部门和门店不一定对应，防止系统创建默认分组，设置跟节点为未分组，默认会将没有分组的人员落到根节点上
                rootDO.setUnclassifiedFlag(Constants.INDEX_ONE);
                if(root.getDingDeptId() != null){
                    rootDO.setThirdDeptId(String.valueOf(root.getDingDeptId()));
                }
                deptName = root.getName();
            }
        }else {
            OapiV2DepartmentListsubResponse.DeptBaseResponse root = null;
            try {
                 root = dingTalkClientService.getDeptDetail(SyncConfig.ROOT_DEPT_ID_STR, accessToken);
            }catch (ApiException e){
                //首次获取失败 重试一次
                log.info("first_get_root_node_fail_{}",accessToken);
                accessToken = dingService.getAccessToken(config.getDingCorpId(), config.getAppType());
                root = dingTalkClientService.getDeptDetail(SyncConfig.ROOT_DEPT_ID_STR, accessToken);
            }

            if (root != null && root.getDeptId() != null) {
                rootDO.setName(root.getName());
                rootDO.setSynDingDeptId(Constants.ROOT_REGION_ID);
                deptName = root.getName();
            }
        }
        if (regionRoot == null) {
            regionService.insertOrUpdate(rootDO, eid);
        } else {
            regionService.updateRootDeptId(eid, deptName, rootDO.getSynDingDeptId(), rootDO.getThirdDeptId());
        }
        return rootDO;
    }


    @Override
    public void setDingSyncScheduler(String enterpriseId, String userId, String userName) {
        DataSourceHelper.reset();
        EnterpriseOperateLogDO enterpriseOperateLog = enterpriseOperateLogService.getLatestLogByEnterpriseIdAndOptType(enterpriseId, SyncConfig.ENTERPRISE_OPERATE_LOG_SCHEDULER);
        if (enterpriseOperateLog != null && StringUtils.isNotBlank(enterpriseOperateLog.getOperateDesc())
                && (enterpriseOperateLog.getOperateEndTime() != null && enterpriseOperateLog.getOperateEndTime().after(new Date()))) {
            log.info("定时任务已启动");
            return;
        }

        //设置任务开始时间，第二天2點到7點之間启动
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Random random = new Random();
        int minute = random.nextInt(5);
        int second = random.nextInt(59);
        int hour = RandomUtil.randomInt(2, 7);
        LocalDateTime localDateTime = tomorrow.atTime(hour, minute, second);
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_DAY);
        DateTimeFormatter dfTime = DateTimeFormatter.ofPattern(DateUtils.TIME_FORMAT_SEC2);
        SchedulerAddRequest request = new SchedulerAddRequest();
        String startTime = df.format(localDateTime);
        request.setStartTime(startTime);
        LocalDate endTime = LocalDate.now().plusYears(1L);
        request.setEndTime(Date.from(endTime.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        request.setStatus(SchedulerStatusEnum.ON.getCode());
        request.setType(SchedulerTypeEnum.CALENDAR.getCode());
        request.setCalendarInfo(buildSchedulerCalendarInfoRequest(dfTime.format(localDateTime)));
        // 验签使用
        String token = MD5Util.md5(enterpriseId);

        List<ScheduleCallBackRequest> jobs = Lists.newArrayList();
        jobs.add(ScheduleCallBackUtil.getCallBack(schedulerCallbackTaskUrl + "/v2/" + enterpriseId +
                "/communication/dingSyncAllTaskScheduler/" + token, ScheduleCallBackEnum.api.getValue()));
        request.setJobs(jobs);
        log.info("定时同步区域门店用户，开始调用定时器enterpriseId={},开始调用参数={}", enterpriseId, JSON.toJSONString(request));
        String schedule = HttpRequest.sendPost(schedulerApiUrl + "/v2/" + enterpriseId + "/schedulers", JSON.toJSONString(request), ScheduleCallBackUtil.buildHeaderMap());
        JSONObject jsonObjectSchedule = JSONObject.parseObject(schedule);
        log.info("定时同步区域门店用户，结束调用定时器enterpriseId={},返回结果={}", enterpriseId, jsonObjectSchedule);
        String scheduleId = null;
        if (ObjectUtil.isNotEmpty(jsonObjectSchedule)) {
            scheduleId = jsonObjectSchedule.getString("scheduler_id");
        }
        EnterpriseOperateLogDO logDO = EnterpriseOperateLogDO.builder().enterpriseId(enterpriseId).operateDesc(scheduleId).operateEndTime(request.getEndTime())
                .operateType(SyncConfig.ENTERPRISE_OPERATE_LOG_SCHEDULER).operateStartTime(new Date()).userName(userName).createTime(new Date())
                .status(SyncConfig.SYNC_STATUS_SUCCESS).userId(userId).build();
        enterpriseOperateLogService.insert(logDO);
    }

    @Override
    public void syncDingDepartmentAll(String eid, String corpId) throws ApiException {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        SyncContext syncContext = new SyncContext(corpId, config.getAppType());
        //同步部门
        Set<String> deptIds = syncDept(syncContext);
        if (CollectionUtils.isNotEmpty(deptIds)) {
            //删除不存在的数据
            sysDepartmentMapper.deleteByNotInIds(new ArrayList<>(deptIds), eid);
        }
    }

    private SchedulerCalendarInfoRequest buildSchedulerCalendarInfoRequest(String startTme) {
        SchedulerCalendarInfoRequest calendarInfoRequest = new SchedulerCalendarInfoRequest();
        calendarInfoRequest.setCalendarTime(startTme);
        calendarInfoRequest.setCalendarType(TaskCycleEnum.WEEK.getCode().toLowerCase());
        //随机周一到周日一天
        String calendarValue = String.valueOf(RandomUtil.randomInt(1, 7));
        calendarInfoRequest.setCalendarValue(calendarValue);
        return calendarInfoRequest;
    }


    /**
     * 同步部门信息
     *
     * @param syncContext
     */
    @Override
    public Set<String> syncDept(SyncContext syncContext) throws ApiException {
        log.info("{} start syncDept", syncContext.getCorpId());
        List<SysDepartmentDO> sysDepartments;
        if (AppTypeEnum.WX_APP.getValue().equals(syncContext.getAppType()) ||
                AppTypeEnum.WX_APP2.getValue().equals(syncContext.getAppType())) {
            sysDepartments = syncContext.getPySysDepartmentsV2();
            log.info("企业微信的部门:{}", sysDepartments.toString());
        }if (AppTypeEnum.isWxSelfAndPrivateType(syncContext.getAppType())) {
            List<SysDepartmentDTO> sysDepartmentDTOList = enterpriseInitConfigApiService.getDepartments(syncContext.getCorpId(), syncContext.getAppType(), null);
            sysDepartments = new ArrayList<>();
            for (SysDepartmentDTO department : sysDepartmentDTOList) {
                SysDepartmentDO sysDepartment = convertFactory.convertSysDepartmentDTO2SysDepartmentDO(department, syncContext.getAppType());
                if(Objects.nonNull(sysDepartment)){
                    sysDepartments.add(sysDepartment);
                }
            }
            log.info("企业微信的部门:{}", sysDepartments.size());
        } else if (AppTypeEnum.APP.getValue().equals(syncContext.getAppType())) {
            log.info("app端注册企业，只初始化根部门");
            SysDepartmentDO rootDepartment = new SysDepartmentDO();
            rootDepartment.setId("1");
            rootDepartment.setName(syncContext.getEnterpriseName());
            sysDepartments = new ArrayList<>();
            sysDepartments.add(rootDepartment);
        } else if (AppTypeEnum.FEI_SHU.getValue().equals(syncContext.getAppType())) {
            sysDepartments = syncContext.getFsDepartmentsV1();
            log.info("飞书的部门:{}", JSONObject.toJSONString(sysDepartments));
        }else {
            sysDepartments = syncContext.getSysDepartments();
        }

        Set<String> idSet = new HashSet<>();
        if (syncContext.getErrCtx() == null) {
            log.info("{} get depts size={}", syncContext.getCorpId(), sysDepartments.size());
            idSet = sysDepartments.stream().map(SysDepartmentDO::getId).collect(Collectors.toSet());
            String eid = syncContext.getEid();
            String dbName = syncContext.getDbName();
            DataSourceHelper.changeToSpecificDataSource(dbName);
            //分开批量插入
            if (CollectionUtils.isNotEmpty(sysDepartments)) {
                Lists.partition(sysDepartments, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
                    sysDepartmentMapper.batchInsertOrUpdate(p, eid);
                });
            }
        }
        return idSet;
    }



    @Override
    public void syncDingOnePartyRegionAndStore(String eid, String corpId, String userId, String userName, EnterpriseSettingVO enterpriseSetting) throws ApiException {
        String regionKey = redisConstantUtil.getSyncRegionKey(eid);
        String storeKey = redisConstantUtil.getSyncStoreKey(eid);
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        String accessToken = dingService.getAccessToken(corpId, config.getAppType());
        // 1.准备根节点和删除节点
        RegionDO regionRoot = prepareRegionRoot(eid, userName, accessToken, config);
        // 2.广度遍历同步门店和区域，从根节点开始
        this.updateOnePartyRegionStore(eid, corpId, regionKey, storeKey, regionRoot, userName, getDefaultStoreStatus(enterpriseSetting));
        // 3.获取redis未处理的区域列表：即钉钉端已删除的区域
        Map<String, Object> leftMap = redisUtil.entries(regionKey);
        List<Long> removeRegionIdList = new ArrayList<>();
        if (!leftMap.isEmpty()) {
            for (Map.Entry<String, Object> entry : leftMap.entrySet()) {
                // 根节点和删除节点判断，不处理
                if (regionRoot.getSynDingDeptId().equals(entry.getKey()) || SyncConfig.DELETE_DEPT_ID.equals(entry.getKey())) {
                    continue;
                }
                removeRegionIdList.add(Long.valueOf(entry.getValue().toString()));
            }
        }
        //移除钉钉段删除的区域
        regionService.removeRegions(eid, removeRegionIdList);
        // 4.获取redis未处理的门店列表：即钉钉端已删除的门店
        Map<String, Object> leftStoreMap = redisUtil.entries(storeKey);
        List<String> removeStoreList = Lists.newArrayList();
        if (!leftStoreMap.isEmpty()) {
            for (Map.Entry<String, Object> entry : leftStoreMap.entrySet()) {
                removeStoreList.add(entry.getValue().toString());
            }
        }
        //移除钉钉端删除的门店
        if (CollectionUtils.isNotEmpty(removeStoreList)) {
            List<String> storeIds = storeMapper.getStoreIdByIdList(eid, removeStoreList);
            if (CollectionUtils.isNotEmpty(storeIds)) {
                storeService.deleteByStoreIds(eid, storeIds, userId);
            }
        }
    }

    public StoreStatusEnum getDefaultStoreStatus(EnterpriseSettingVO enterpriseSetting){
        String extendField = enterpriseSetting.getExtendField();
        StoreStatusEnum storeStatus = StoreStatusEnum.OPEN;
        if(StringUtils.isNotBlank(extendField)){
            JSONObject jsonObject = JSONObject.parseObject(extendField);
            storeStatus = StoreStatusEnum.parse(jsonObject.getString("newShopStatus"));
            if(Objects.isNull(storeStatus)){
                storeStatus = StoreStatusEnum.OPEN;
            }
        }
        return storeStatus;
    }

    @Override
    public void syncDingOnePartyStoreGroup(String eid, String dingCorpId, String userId, String userName) throws ApiException {
        // 查询钉钉门店分组
        List<OpGroupDTO> dingGroups = enterpriseInitConfigApiService.getGroups(dingCorpId, AppTypeEnum.ONE_PARTY_APP.getValue());
        // 查询cool门店分组
        List<StoreGroupDO> coolGroups = storeGroupDao.selectGroupBySource(eid, DataSourceEnum.SYNC.getCode());
        // cool门店分组转map，用作匹配钉钉门店分组
        Map<String, StoreGroupDO> coolGroupMap = coolGroups.stream().collect(Collectors.toMap(StoreGroupDO::getGroupId, Function.identity()));
        List<StoreGroupDO> updateGroups = Lists.newArrayList();
        List<StoreGroupDO> insertGroups = Lists.newArrayList();
        List<StoreGroupMappingDO> mappingDOList = Lists.newArrayList();
        for (OpGroupDTO dingGroup : dingGroups) {
            // 构建门店分组映射放入到列表中，统一批量插入
            mappingDOList.addAll(this.buildStoreGroupMappingDO(dingGroup, userName));
            StoreGroupDO coolGroup = coolGroupMap.get(dingGroup.getGroupId().toString());
            if(Objects.isNull(coolGroup)){
                coolGroup = new StoreGroupDO();
                coolGroup.setGroupId(dingGroup.getGroupId().toString());
                coolGroup.setGroupName(dingGroup.getGroupName());
                coolGroup.setSource(DataSourceEnum.SYNC.getCode());
                coolGroup.setCreateTime(System.currentTimeMillis());
                coolGroup.setCreateUser(userName);
                insertGroups.add(coolGroup);
                continue;
            }
            coolGroups.remove(coolGroup);
            // 更新分组
            if(!coolGroup.getGroupName().equals(dingGroup.getGroupName())) {
                coolGroup.setGroupName(dingGroup.getGroupName());
                coolGroup.setUpdateTime(System.currentTimeMillis());
                coolGroup.setUpdateUser(userName);
                updateGroups.add(coolGroup);
            }
        }
        // 同步删除分组
        storeGroupDao.deleteByIds(eid, coolGroups.stream().map(StoreGroupDO::getGroupId).collect(Collectors.toList()));
        // 同步新增分组
        storeGroupDao.batchInsertGroup(eid, insertGroups);
        // 同步更新分组
        storeGroupDao.batchUpdateGroup(eid, updateGroups);
        // 删除分组映射
        storeGroupMappingDao.deleteByGroupIds(eid, coolGroupMap.keySet().stream().collect(Collectors.toList()));
        // 批量新增分组映射
        storeGroupMappingDao.batchInsertGroupMapping(eid, mappingDOList);
    }

    private List<StoreGroupMappingDO> buildStoreGroupMappingDO(OpGroupDTO dingGroup, String userName) {
        List<StoreGroupMappingDO> result = Lists.newArrayList();
        for (Long storeId : ListUtils.emptyIfNull(dingGroup.getStoreIdList())) {
            StoreGroupMappingDO mappingDO = new StoreGroupMappingDO();
            mappingDO.setStoreId(storeId.toString());
            mappingDO.setGroupId(dingGroup.getGroupId().toString());
            mappingDO.setCreateTime(System.currentTimeMillis());
            mappingDO.setCreateUser(userName);
            result.add(mappingDO);
        }
        return result;
    }

    /**
     * 同步门店通区域和门店数据
     * @param eid
     * @param corpId
     * @param regionKey
     * @param storeKey
     * @param regionFirst
     * @param userName
     * @throws ApiException
     */
    private void updateOnePartyRegionStore(String eid, String corpId, String regionKey,
                                           String storeKey, RegionDO regionFirst, String userName, StoreStatusEnum defaultStoreStatus) throws ApiException {
        // 获取门店通通讯录code，在准备根节点时放入到RegionDO里面，所以可以直接获取到
        String contactCode = regionFirst.getContactCode();
        // 根区域放入队列，开始遍历
        Queue<RegionDO> queue = new LinkedList<>();
        queue.add(regionFirst);
        Integer limitStoreCount = storeService.getLimitStoreCount(eid);
        while (!queue.isEmpty()) {
            // 遍历队列里面的区域，同步其子区域数据
            for (int i = 0; i < queue.size(); i++) {
                RegionDO parentRegion = queue.poll();
                // 查询下一级节点
                if(parentRegion.getSynDingDeptId().equals("841318217")){
                    continue;
                }
                List<OpStoreAndRegionDTO> storeAndRegions = enterpriseInitConfigApiService.getSubStoreAndRegion(corpId, AppTypeEnum.ONE_PARTY_APP.getValue(),
                        contactCode, parentRegion.getSynDingDeptId());
                List<RegionDO> regionDOList = Lists.newArrayList();
                List<RegionDO> regionInsertList = Lists.newArrayList();
                List<RegionDO> regionUpdateList = Lists.newArrayList();
                List<StoreDO> storeInsertList = Lists.newArrayList();
                List<StoreDO> storeUpdateList = Lists.newArrayList();
                // 区域门店map，用来临时存储区域门店关系。区域数据入库生成id后，给门店赋值区域路径
                Map<RegionDO, StoreDO> regionStoreMapping = Maps.newHashMap();
                for (OpStoreAndRegionDTO storeAndRegion : storeAndRegions) {
                    // 节点id：门店/区域id
                    String nodeId = String.valueOf(storeAndRegion.getId());
                    // 构建区域数据
                    RegionDO regionDO = this.buildRegionDOForOneParty(parentRegion, storeAndRegion);
                    // redis中获取本地的区域id
                    String id = redisUtil.hashGetString(regionKey, nodeId);
                    // 获取到本地的区域id做更新操作，否则做新增操作
                    if (StringUtils.isNotBlank(id)) {
                        regionDO.setId(Long.valueOf(id));
                        regionUpdateList.add(regionDO);
                        //删除对应的已有redis key缓存
                        redisUtil.delete(regionKey, nodeId);
                    } else {
                        regionInsertList.add(regionDO);
                    }
                    // 门店类型，保存门店数据
                    if(RegionTypeEnum.STORE.getType().equals(storeAndRegion.getType())) {
                        regionDO.setStoreId(nodeId);
                        // redis中获取本地门店id
                        String storeId = redisUtil.hashGetString(storeKey, nodeId);
                        // 构建门店
                        StoreDO storeDO = this.buildStoreDOForOneParty(storeAndRegion, userName);
                        if(StringUtils.isNotBlank(storeId)) {
                            storeDO.setId(Long.parseLong(storeId));
                            storeUpdateList.add(storeDO);
                            redisUtil.delete(storeKey, nodeId);
                        }else{
                            storeDO.setStoreStatus(defaultStoreStatus.getValue());
                            storeInsertList.add(storeDO);
                        }
                        // 区域门店关系
                        regionStoreMapping.put(regionDO, storeDO);
                    }else{
                        regionDOList.add(regionDO);
                    }
                }
                //批量更新区域数据
                regionService.batchUpdateIgnoreRegionType(regionUpdateList, eid);
                //批量插入区域数据
                try {
                    regionService.batchInsertRegionsNotExistDuplicate(eid, regionInsertList);
                } catch(DuplicateKeyException e) {
                    log.error("新增区域异常", e);
                    throw new ServiceException(parentRegion.getName() + "[" + parentRegion.getId() + "] 下级部门和其他同步部门有上下级关系");
                }catch (Exception e) {
                    log.error("保存部门异常细节", e);
                    throw new ServiceException(parentRegion.getName() + "[" + parentRegion.getId() + "] 保存异常");
                }
                // 将下级区域放入到队列，待遍下次遍历
                queue.addAll(CollectionUtils.emptyIfNull(regionDOList));
                // 填充门店所属区域id和区域路径
                regionStoreMapping.forEach((region, store) -> {
                    store.setRegionId(Long.parseLong(region.getParentId()));
                    store.setRegionPath(region.getFullRegionPath());
                });
                // 门店批量更新
                storeDao.batchUpdateStore(eid, storeUpdateList, userName, limitStoreCount);
                // 门店批量新增
                storeDao.batchInsertStore(eid, storeInsertList, limitStoreCount);
            }
        }
    }

    /**
     * 门店通-构建门店DO
     * @param storeAndRegion
     * @param userName
     * @return
     */
    private StoreDO buildStoreDOForOneParty(OpStoreAndRegionDTO storeAndRegion, String userName) {
        StoreDO storeDO = new StoreDO();
        storeDO.setCreateName(userName);
        storeDO.setStoreName(storeAndRegion.getName());
        storeDO.setStoreNum(storeAndRegion.getStoreCode());
        storeDO.setStoreAddress(storeAndRegion.getAddress());
        storeDO.setLocationAddress(storeAndRegion.getLocationAddress());
        storeDO.setIsLock(StoreIsLockEnum.NOT_LOCKED.getValue());
        if(StringUtils.isNotBlank(storeAndRegion.getLongitude()) || StringUtils.isNotBlank(storeAndRegion.getLatitude())) {
            storeDO.setLongitudeLatitude(storeAndRegion.getLongitude() + Constants.COMMA + storeAndRegion.getLatitude());
            storeDO.setAddressPoint("POINT("+storeAndRegion.getLongitude()+" "+storeAndRegion.getLatitude()+")");
        }
        storeDO.setLongitude(storeAndRegion.getLongitude());
        storeDO.setLatitude(storeAndRegion.getLatitude());
        storeDO.setIsDelete(StoreIsDeleteEnum.EFFECTIVE.getValue());
        storeDO.setTelephone(storeAndRegion.getTelephone());
        storeDO.setBusinessHours(storeAndRegion.getBusinessHours());
        if(StringUtils.isNotBlank(storeAndRegion.getBusinessHours()) && storeAndRegion.getBusinessHours().contains(Constants.COLON)){
            List<String> list = Arrays.asList(storeAndRegion.getBusinessHours().split(Constants.COMMA));
            long startTimeLong = DateUtils.getDateTime(list.get(0));
            long endTimeLong = DateUtils.getDateTime(list.get(1));
            storeDO.setBusinessHours(startTimeLong + "," + endTimeLong);
        }
        storeDO.setStoreAcreage(storeAndRegion.getStoreAcreage());
        storeDO.setStoreBandwidth(storeAndRegion.getStoreBandwidth());
        storeDO.setCreateTime(System.currentTimeMillis());
        storeDO.setSource(DataSourceEnum.SYNC.getCode());
        storeDO.setDingId(storeAndRegion.getId().toString());
        storeDO.setSynDingDeptId(storeAndRegion.getId().toString());
        storeDO.setStoreStatus(OnePartyStoreStatusEnum.getEnumByCode(storeAndRegion.getStatus()));
        storeDO.setStoreId(storeAndRegion.getId().toString());
        if(storeAndRegion.getDingDeptId() != null){
            storeDO.setThirdDeptId(String.valueOf(storeAndRegion.getDingDeptId()));
        }
        return storeDO;
    }

    /**
     * 门店通-构建区域DO
     * @param parentRegion
     * @param storeAndRegion
     * @return
     */
    private RegionDO buildRegionDOForOneParty(RegionDO parentRegion, OpStoreAndRegionDTO storeAndRegion) {
        RegionDO region = new RegionDO();
        region.setParentId(String.valueOf(parentRegion.getId()));
        region.setName(storeAndRegion.getName());
        region.setCreateTime(System.currentTimeMillis());
        region.setUpdateTime(System.currentTimeMillis());
        region.setSynDingDeptId(String.valueOf(storeAndRegion.getId()));
        region.setRegionType(storeAndRegion.getType());
        region.setStoreRange(RegionTypeEnum.STORE.getType().equals(storeAndRegion.getType()));
        String regionPath = null;
        if (StringUtils.isBlank(parentRegion.getRegionPath())) {
            regionPath = "/1/";
        } else {
            regionPath = parentRegion.getRegionPath().endsWith("/") ? parentRegion.getRegionPath() + parentRegion.getId() + "/" :
                    parentRegion.getRegionPath() + "/" + parentRegion.getId() + "/";
        }
        if (!regionPath.startsWith("/")) {
            regionPath = "/" + regionPath;
        }
        regionPath = regionPath.replaceAll("//", "/");
        region.setRegionPath(regionPath);
        region.setDeleted(false);
        if(storeAndRegion.getDingDeptId() != null){
            region.setThirdDeptId(String.valueOf(storeAndRegion.getDingDeptId()));
        }
        return region;
    }

    @Override
    public void syncSingleOnePartyStoreAndRegion(String eid, OpStoreAndRegionDTO storeAndRegion) throws ApiException {
        // 1.查询上一级区域
        RegionDO parentRegion = regionService.getRegionBySynDingDeptId(eid, storeAndRegion.getParentId());
        if(Objects.isNull(parentRegion)) {
            // 如果没有查到上级，可能是批量操作，上级还没有入库。 放到redis里面，待上级节点入库后再处理
            String subNodeCacheKey = MessageFormat.format(SyncConfig.SYNC_NODE_CONCURRENT_CACHE_KEY, eid, storeAndRegion.getParentId());
            redisUtil.lPush(subNodeCacheKey, JSONObject.toJSONString(storeAndRegion));
            return;
        }
        Integer limitStoreCount = storeService.getLimitStoreCount(eid);
        // 2.查询当前节点区域
        RegionDO regionDO = regionService.getRegionBySynDingDeptId(eid, storeAndRegion.getId());
        List<RegionDO> regionInsertList = Lists.newArrayList();
        List<RegionDO> regionUpdateList = Lists.newArrayList();
        List<StoreDO> storeInsertList = Lists.newArrayList();
        List<StoreDO> storeUpdateList = Lists.newArrayList();
        // 3.构建区域数据
        RegionDO syncRegionDO = this.buildRegionDOForOneParty(parentRegion, storeAndRegion);
        if(Objects.isNull(regionDO)) {
            regionInsertList.add(syncRegionDO);
        }else{
            syncRegionDO.setId(regionDO.getId());
            regionUpdateList.add(syncRegionDO);
        }
        // 4.如果是门店类型，构建门店数据
        StoreDO storeDO = null;
        if(RegionTypeEnum.STORE.getType().equals(storeAndRegion.getType())) {
            syncRegionDO.setStoreId(storeAndRegion.getId().toString());
            // 构建门店
            storeDO = this.buildStoreDOForOneParty(storeAndRegion, Constants.SYSTEM_USER_NAME);
            StoreDTO existStore = storeService.getStoreByStoreId(eid, storeAndRegion.getId().toString());
            if(Objects.isNull(existStore)) {
                storeInsertList.add(storeDO);
            }else {
                storeUpdateList.add(storeDO);
            }
        }
        // 保存区域数据
        regionService.batchInsertRegionsNotExistDuplicate(eid, regionInsertList);
        regionService.batchUpdateIgnoreRegionType(regionUpdateList, eid);
        // 保存门店数据
        if(Objects.nonNull(storeDO)) {
            storeDO.setRegionId(Long.parseLong(syncRegionDO.getParentId()));
            storeDO.setRegionPath(syncRegionDO.getFullRegionPath());
            storeDao.batchUpdateStore(eid, storeUpdateList, Constants.SYSTEM_USER_NAME, limitStoreCount);
            storeDao.batchInsertStore(eid, storeInsertList, limitStoreCount);
        }
        // 5.获取并发同步的子节点，完成子节点同步
        String subNodesCacheKey = MessageFormat.format(SyncConfig.SYNC_NODE_CONCURRENT_CACHE_KEY, eid, storeAndRegion.getId());
        while(redisUtil.listExists(subNodesCacheKey)) {
            String subNodeStr = redisUtil.rPop(subNodesCacheKey);
            OpStoreAndRegionDTO subNode = JSONObject.parseObject(subNodeStr, OpStoreAndRegionDTO.class);
            this.syncSingleOnePartyStoreAndRegion(eid, subNode);
        }
        if(!RegionTypeEnum.STORE.getType().equals(storeAndRegion.getType()) && Objects.nonNull(regionDO)
                && !syncRegionDO.getRegionPath().equals(regionDO.getRegionPath())
                && regionService.getSubRegionNumBySynDeptId(eid, storeAndRegion.getId()) >= Constants.ZERO) {
            // 非叶子节点变更
            regionService.updateRegionPathTraversalDown(eid, regionDO.getFullRegionPath(), syncRegionDO.getRegionPath(), syncRegionDO.getId().toString());
        }

        if (CollectionUtils.isNotEmpty(regionInsertList)) {
            //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
            List<String> addRegionIds = regionInsertList.stream()
                    .map(m -> String.valueOf(m.getId()))
                    .collect(Collectors.toList());
            coolCollegeIntegrationApiService.sendDataChangeMsg(eid, addRegionIds, ChangeDataOperation.ADD.getCode(), ChangeDataType.REGION.getCode());
        }
        if (CollectionUtils.isNotEmpty(regionUpdateList)) {
            //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
            List<String> updateRegionIds = regionUpdateList.stream()
                    .map(m -> String.valueOf(m.getId()))
                    .collect(Collectors.toList());
            coolCollegeIntegrationApiService.sendDataChangeMsg(eid, updateRegionIds, ChangeDataOperation.UPDATE.getCode(), ChangeDataType.REGION.getCode());
        }

    }
}
