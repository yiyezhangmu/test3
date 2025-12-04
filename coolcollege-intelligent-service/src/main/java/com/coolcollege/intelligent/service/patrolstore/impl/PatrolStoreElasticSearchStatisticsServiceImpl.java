package com.coolcollege.intelligent.service.patrolstore.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.PageUtil;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.elasticSearch.request.MetaTableStatisticsRequest;
import com.coolcollege.intelligent.model.elasticSearch.request.RegionPatrolStatisticsRequest;
import com.coolcollege.intelligent.model.elasticSearch.response.*;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsRegionQuery;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsRegionDTO;
import com.coolcollege.intelligent.model.patrolstore.vo.*;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionChildDTO;
import com.coolcollege.intelligent.model.region.dto.RegionNode;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.BasicsStoreDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.elasticsearch.ElasticSearchService;
import com.coolcollege.intelligent.service.patrolstore.AsynElasticSearch;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreElasticSearchStatisticsService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2021/10/27 13:51
 * @Version 1.0
 */
@Service
@Slf4j
public class PatrolStoreElasticSearchStatisticsServiceImpl implements PatrolStoreElasticSearchStatisticsService {
    //店外首页缓存标识
    private static final String HOMEPAGEPREFIX = "homePage_{0}_{1}";
    //大企业缓存标识
    private static final String BIGENTERPRISEKEY= "bigEnterpriseList";
    //默认分页数
    private static final Integer DEFAULTPAGESIZE = 50;

    @Resource
    private StoreMapper storeMapper;
    @Resource
    private RegionService regionService;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    ElasticSearchService elasticSearchService;
    @Resource
    ThreadPoolTaskExecutor elasticSearchExecutorService;
    @Resource
    EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    UserAuthMappingMapper userAuthMappingMapper;
    @Resource
    TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Autowired
    private RedisUtilPool redis;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Autowired
    AsynElasticSearch asynElasticSearch;

    @Override
    public PageInfo<PatrolStoreStatisticsRegionVO> statisticsRegionSummary(String eid, PatrolStoreStatisticsRegionQuery query) {
        long start = System.currentTimeMillis();
        PageInfo<PatrolStoreStatisticsRegionVO> pageInfo = new PageInfo();
        List<String> regionIds = query.getRegionIds();
        Date beginDate = query.getBeginDate();
        Date endDate = query.getEndDate();
        //校验原始的regionIds
        if (CollUtil.isEmpty(regionIds) || Objects.isNull(beginDate) || Objects.isNull(endDate)) {
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        long total = regionIds.size();
        //是否是查询子节点数据
        Integer pagesize = query.getPageSize();
        if (query.isGetChild()) {
            //分页处理 一次查询50个区域
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            List<RegionChildDTO> regionByParentId = regionMapper.getRegionByParentId(eid, regionIds, Boolean.TRUE);
            PageInfo<RegionChildDTO> regionChildDTOPageInfo = new PageInfo<>(regionByParentId);
            regionIds = regionByParentId.stream().map(RegionChildDTO::getId).collect(Collectors.toList());
            pagesize = regionIds.size();
            total = regionChildDTOPageInfo.getTotal();
        }
        //校验重新赋值后的regionIds
        if (CollUtil.isEmpty(regionIds)) {
            return pageInfo;
        }
        List<RegionChildDTO> containSubAreaList = regionMapper.getRegionByParentId(eid, regionIds, Boolean.TRUE);
        Map<String, RegionChildDTO> containSubAreaMap = containSubAreaList.stream()
                .collect(Collectors.toMap(RegionChildDTO::getPid, Function.identity(), (a, b) -> a));
        List<Long> regionIdList = regionIds.stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
        // 获取区域的路径
        List<RegionPathDTO> regionPathList = regionService.getRegionPathByList(eid, regionIds);
        List<PatrolStoreStatisticsRegionVO> result = new ArrayList<>();

        //区域直连门店数量统计 getGetDirectStore=true 只查询直连门店
        if (query.getGetDirectStore()) {
            List<StoreDO> stores = storeMapper.directlyStoreCountByRegion(eid, regionIdList);
            //如果查询直连门店数量的时候 regionIds 只有一个值
            regionPathList.get(Constants.INDEX_ZERO).setStoreNum(stores.isEmpty() ? Constants.INDEX_ZERO : stores.size());
        }
        RegionPatrolStatisticsRequest rpsr = RegionPatrolStatisticsRequest.builder().regionIds(regionIdList).beginDate(beginDate)
                .endDate(endDate).isGetDirectStore(query.getGetDirectStore()).enterpriseId(eid).build();
        result = asynElasticSearch.asynStatisticsRegionSummary(rpsr, regionPathList);
        Map<String, PatrolStoreStatisticsRegionVO> patrolStoreStatisticsRegionVOMap = result.stream()
                .collect(Collectors.toMap(PatrolStoreStatisticsRegionVO::getRegionId, Function.identity(), (a, b) -> a));
        for (RegionPathDTO region : regionPathList) {
            String regionId = region.getRegionId();
            RegionChildDTO regionChildDTO = containSubAreaMap.get(regionId);
            PatrolStoreStatisticsRegionVO psspv = patrolStoreStatisticsRegionVOMap.get(regionId);
            psspv.setContainSubArea(regionChildDTO != null ? Boolean.TRUE : Boolean.FALSE);
        }
        pageInfo.setPageNum(query.getPageNum());
        pageInfo.setPageSize(pagesize);
        pageInfo.setTotal(total);
        pageInfo.setList(result);
        log.info("统计时间消耗：{}毫秒", System.currentTimeMillis() - start);
        return pageInfo;
    }


    @Override
    public PageInfo<PatrolStoreStatisticsRegionDTO> statisticsRegion(String eid, PatrolStoreStatisticsRegionQuery query) {
        long start = System.currentTimeMillis();
        PageInfo<PatrolStoreStatisticsRegionDTO> pageInfo = new PageInfo();
        List<String> regionIds = query.getRegionIds();
        //校验原始的regionIds
        if (CollUtil.isEmpty(regionIds)) {
            throw new ServiceException(ErrorCodeEnum.VALIDATION_RULES_1060001);
        }
        //是否是查询子节点数据
        Integer pagesize = query.getPageSize();
        if (query.isGetChild()){
            List<RegionChildDTO> regionByParentId = regionMapper.getRegionByParentId(eid, regionIds, Boolean.FALSE);
            regionIds = regionByParentId.stream().map(RegionChildDTO::getId).collect(Collectors.toList());
            pagesize = regionIds.size();
        }
        if (query.getContainAllChild()) {
            // 查询所有下级
            List<RegionDO> regionByRegionIds = regionMapper.getRegionByRegionIds(eid, regionIds);
            List<String> regionPahtList = regionByRegionIds.stream().map(RegionDO::getFullRegionPath).collect(Collectors.toList());
            regionIds.addAll(regionMapper.getSubIdsByRegionIds(eid, regionPahtList));
            pagesize = regionIds.size();
        }
        //校验重新赋值后的regionIds
        if (CollUtil.isEmpty(regionIds)) {
            return pageInfo;
        }
        List<String> list = PageUtil.startPage(regionIds, query.getPageNum(), pagesize);
        List<Long> regionIdList = list.stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
        Date beginDate = query.getBeginDate();
        Date endDate = query.getEndDate();
        // 获取区域的路径
        List<RegionPathDTO> regionPathList = regionService.getRegionPathByList(eid, list);
        List<PatrolStoreStatisticsRegionDTO> result = new ArrayList<>();

        List<Long> ids = regionIds.stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
        Map<String, String> nameMap = regionService.getNoBaseNodeFullNameByRegionIds(eid, ids, Constants.SPRIT);

        RegionPatrolStatisticsRequest rpsr = RegionPatrolStatisticsRequest.builder().regionIds(regionIdList).beginDate(beginDate).endDate(endDate).enterpriseId(eid).build();
        //通过es获取指定区域的巡店数、巡店人数、巡店门店数、任务巡店数
        Future<List<PatrolStatisticsDataDTO>> patrolStatisticsDataFuture = elasticSearchExecutorService.submit(() ->
            elasticSearchService.patrolStoreStatisticsRegionRecord(rpsr));
        //通过es获取指定区域总问题数、待整改问题数、待复检问题数、已解决问题数
        Future<List<TaskStoreStatisticsQuestionDTO>> taskStoreStatisticsQuestionFuture = elasticSearchExecutorService.submit(() ->
                (elasticSearchService.patrolStoreStatisticsRegionColumn(rpsr)));
        Future<List<PatrolStatisticsGroupByPatrolTypeDTO>> patrolStatisticsGroupByPatrolTypeFututre = elasticSearchExecutorService.submit(() ->
                (elasticSearchService.getNumByPatrolType(rpsr)));
        Future<List<PatrolRuleTimeDTO>> patrolRuleTimeFuture = elasticSearchExecutorService.submit(() -> (elasticSearchService.getPatrolRuleTime(rpsr)));
        Future<List<PatrolTimeDTO>> patrolTimeFuture = elasticSearchExecutorService.submit(() -> (elasticSearchService.getPatrolTime(rpsr)));
        Map<String, PatrolStatisticsDataDTO> patrolStatisticsDataMap = new HashMap<>();
        Map<String, TaskStoreStatisticsQuestionDTO> taskStoreStatisticsQuestionMap = new HashMap<>();
        Map<String, Map<String, Integer>> countMap = new HashMap<>();
        Map<String, PatrolRuleTimeDTO> patrolRuleTimeMap = new HashMap<>();
        Map<String, PatrolTimeDTO> patrolTimeDTOMap = new HashMap<>();
        try {
            List<PatrolStatisticsDataDTO> patrolStatisticsDataDTOS = patrolStatisticsDataFuture.get();
            patrolStatisticsDataMap = patrolStatisticsDataDTOS.stream()
                    .collect(Collectors.toMap(PatrolStatisticsDataDTO::getRegionId, Function.identity(), (a, b) -> a));
            taskStoreStatisticsQuestionMap = taskStoreStatisticsQuestionFuture.get().stream()
                    .collect(Collectors.toMap(TaskStoreStatisticsQuestionDTO::getRegionId, Function.identity(), (a, b) -> a));
            countMap =  patrolStatisticsGroupByPatrolTypeFututre.get().stream().filter(a->a.getPatrolTypeList()!=null)
                    .collect(Collectors.toMap(PatrolStatisticsGroupByPatrolTypeDTO::getRegionId,
                            date -> date.getPatrolTypeList().stream().collect(Collectors.toMap(PatrolTypeCountDTO::getPatrolType, PatrolTypeCountDTO::getPatrolTypeNum)), (a, b) -> a));
            patrolRuleTimeMap = patrolRuleTimeFuture.get().stream()
                    .collect(Collectors.toMap(PatrolRuleTimeDTO::getRegionId, Function.identity(), (a, b) -> a));
            patrolTimeDTOMap = patrolTimeFuture.get().stream()
                    .collect(Collectors.toMap(PatrolTimeDTO::getRegionId, Function.identity(), (a, b) -> a));
        } catch (Exception e) {
            log.error("数据统计异常{}",e);
        }
        for (RegionPathDTO region : regionPathList) {
            String regionId = region.getRegionId();
            String parentId = region.getParentId() == null ? "" : region.getParentId();
            PatrolStatisticsDataDTO psd = patrolStatisticsDataMap.get(regionId);
            TaskStoreStatisticsQuestionDTO tsqd = taskStoreStatisticsQuestionMap.get(regionId);
            Map<String, Integer> stringIntegerMap = countMap.get(regionId);
            PatrolRuleTimeDTO patrolRuleTimeDTO = patrolRuleTimeMap.get(regionId);
            PatrolTimeDTO patrolTimeDTO = patrolTimeDTOMap.get(regionId);
            //统计 线上巡店数 线下巡店数  定时巡检数
            Integer offlineNum = Constants.INDEX_ZERO;
            Integer onlineNum = Constants.INDEX_ZERO;
            Integer selfCheckNum = Constants.INDEX_ZERO;
            Integer pictureInspectionNum = Constants.INDEX_ZERO;
            Integer formPatrolNum = Constants.INDEX_ZERO;
            if (stringIntegerMap!=null) {
                offlineNum = stringIntegerMap.getOrDefault(TaskTypeEnum.PATROL_STORE_OFFLINE.getCode(), Constants.INDEX_ZERO);
                onlineNum = stringIntegerMap.getOrDefault(TaskTypeEnum.PATROL_STORE_ONLINE.getCode(), Constants.INDEX_ZERO);
                selfCheckNum = stringIntegerMap.getOrDefault(TaskTypeEnum.STORE_SELF_CHECK.getCode(), Constants.INDEX_ZERO);
                formPatrolNum = stringIntegerMap.getOrDefault(TaskTypeEnum.PATROL_STORE_FORM.getCode(), Constants.INDEX_ZERO);
                pictureInspectionNum = stringIntegerMap.getOrDefault(TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode(), Constants.INDEX_ZERO);
            }
            PatrolStoreStatisticsRegionDTO pssr = PatrolStoreStatisticsRegionDTO.builder()
                    .patrolStoreNum(psd.getPatrolStoreNum()).patrolNum(psd.getPatrolNum()).parentId(parentId)
                    .patrolPersonNum(psd.getPatrolPersonNum()).taskPatrolNum(psd.getTaskPatrolNum()).selfCheckNum(selfCheckNum).regionId(regionId)
                    .name(region.getRegionName()).storeNum(region.getStoreNum()).totalQuestionNum(tsqd.getTotalQuestionNum())
                    .todoQuestionNum(tsqd.getTodoQuestionNum()).offlineNum(offlineNum).avgPatrolStoreNum(statisticsData(psd.getPatrolNum(),psd.getPatrolStoreNum()))
                    .onlineNum(onlineNum).pictureInspectionNum(pictureInspectionNum).formPatrolNum(formPatrolNum)
                    .unRecheckQuestionNum(tsqd.getUnRecheckQuestionNum()).finishQuestionNum(tsqd.getFinishQuestionNum())
                    .totalPatrolStoreDuration(DateUtils.formatBetween(patrolRuleTimeDTO.getTotalTourTime())).storeId(region.getStoreId()).regionType(region.getRegionType())
                    .avgPatrolStoreDuration(DateUtils.formatBetween( statisticsLongData(patrolRuleTimeDTO.getTotalTourTime(),psd.getPatrolNum())))
                    .actualAvgPatrolStoreDuration(DateUtils.formatBetween(statisticsLongData(patrolTimeDTO.getTotalPatrolTime(),psd.getPatrolNum())))
                    .actualTotalPatrolStoreDuration(DateUtils.formatBetween(patrolTimeDTO.getTotalPatrolTime()))
                    .pathName(nameMap.get(regionId)).build();
            result.add(pssr);
        }
        pageInfo.setPageNum(query.getPageNum());
        pageInfo.setPageSize(pagesize);
        pageInfo.setTotal(regionIds.size());
        pageInfo.setList(result);
        log.info("统计时间消耗：{}毫秒",System.currentTimeMillis()-start);
        return pageInfo;
    }

    @Override
    public List<PatrolStoreStatisticsRankVO> patrolStoreNumRank(String enterpriseId, PatrolStoreStatisticsRegionQuery request) {
        List<PatrolStoreStatisticsRankVO> result = new ArrayList<>();
        List<String> regionIds = request.getRegionIds();
        if (CollectionUtils.isEmpty(request.getRegionIds())) {
            throw new ServiceException(ErrorCodeEnum.VALIDATION_RULES_1060003);
        }
        List<Long> regionIdList = regionIds.stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
        Date startTime = request.getBeginDate();
        Date endTime = request.getEndDate();
        //封装请求参数
        RegionPatrolStatisticsRequest rpsr = RegionPatrolStatisticsRequest.builder().regionIds(regionIdList).beginDate(startTime).endDate(endTime)
                .enterpriseId(enterpriseId).isGetDirectStore(request.getGetDirectStore()).build();
        //通过es获取多个区域下巡店的巡店次数排行
        List<PatrolStoreRankDTO> patrolStoreRankDTOS = elasticSearchService.regionPatrolNumRank(rpsr);
        if (CollectionUtils.isEmpty(patrolStoreRankDTOS)){
            return buildEmptyStoreStatisticsRank(result, enterpriseId);
        }
        List<String> storeIds = patrolStoreRankDTOS.stream().map(data -> data.getStoreId()).collect(Collectors.toList());
        //通过门店id查询门店信息
        List<StoreDO> stores = storeMapper.getStoresByStoreIds(enterpriseId, storeIds);
        Map<String, String> storeIdNameMap = stores.stream()
                .filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                .collect(Collectors.toMap(data -> data.getStoreId(), data -> data.getStoreName(), (a, b) -> a));
        for (PatrolStoreRankDTO temp:patrolStoreRankDTOS) {
            storeIdNameMap.get(temp.getStoreId());
            PatrolStoreStatisticsRankVO pssr = PatrolStoreStatisticsRankVO.builder().storeId(temp.getStoreId()).count(temp.getCount())
                    .storeName(storeIdNameMap.get(temp.getStoreId())).build();
            result.add(pssr);
        }
        if (CollectionUtils.isEmpty(result)){
            return buildEmptyStoreStatisticsRank(result, enterpriseId);
        }
        return result;
    }

    /**
     * 巡店排行为空的时候，拿到10个门店填充列表
     * @param result
     * @param eid
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.patrolstore.vo.PatrolStoreStatisticsRankVO>
     * @date: 2021/12/30 21:29
     */
    private List<PatrolStoreStatisticsRankVO> buildEmptyStoreStatisticsRank(List<PatrolStoreStatisticsRankVO> result, String eid) {
        if (CollectionUtils.isNotEmpty(result)) {
            return result;
        }
        List<BasicsStoreDTO> basicsStoreDTOS = storeMapper.getBaseStoreList(eid, Constants.INDEX_TEN);
        basicsStoreDTOS.forEach(store -> {
            PatrolStoreStatisticsRankVO vo = new PatrolStoreStatisticsRankVO(store.getStoreId(), store.getStoreName(), Constants.INDEX_ZERO);
            result.add(vo);
        });
        return result;
    }

    @Override
    public List<PatrolStoreStatisticsProblemRankVO> statisticsStoreProblemRank(String enterpriseId, PatrolStoreStatisticsRegionQuery query, CurrentUser user) {
        List<PatrolStoreStatisticsProblemRankVO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(query.getRegionIds())) {
            throw new ServiceException(ErrorCodeEnum.VALIDATION_RULES_1060003);
        }
        List<String> regionIds = query.getRegionIds();
        List<Long> regionIdList = regionIds.stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
        Date startTime = query.getBeginDate();
        Date endTime = query.getEndDate();
        RegionPatrolStatisticsRequest rpsr = RegionPatrolStatisticsRequest.builder().regionIds(regionIdList).beginDate(startTime).endDate(endTime)
                .isGetDirectStore(query.getGetDirectStore()).enterpriseId(enterpriseId).build();
        List<StoreQuestionRankDTO> storeQuestionRankDTOS = elasticSearchService.regionQuestionNumRank(rpsr);
        if (CollectionUtils.isEmpty(storeQuestionRankDTOS)){
//            return buildEmptyStatisticsStoreProblemRank(result, enterpriseId);
            return buildMyStoreProblemRank(result,enterpriseId,user);
        }
        List<String> storeIds = storeQuestionRankDTOS.stream().map(StoreQuestionRankDTO::getStoreId).collect(Collectors.toList());
        //通过门店id查询门店信息
        List<StoreDO> stores = storeMapper.getStoresByStoreIds(enterpriseId, storeIds);
        Map<String, String> storeIdNameMap = stores.stream()
                .filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                .collect(Collectors.toMap(data -> data.getStoreId(), data -> data.getStoreName(), (a, b) -> a));
        for (StoreQuestionRankDTO temp:storeQuestionRankDTOS) {
            storeIdNameMap.get(temp.getStoreId());
            PatrolStoreStatisticsProblemRankVO pssr = PatrolStoreStatisticsProblemRankVO.builder().storeId(temp.getStoreId()).count(temp.getTotalNum())
                    .finishQuestionNum(temp.getFinishQuestionNum()).storeName(storeIdNameMap.get(temp.getStoreId())).build();
            result.add(pssr);
        }
        if (CollectionUtils.isEmpty(result)){
            return buildEmptyStatisticsStoreProblemRank(result, enterpriseId);
        }
        return result;
    }

    private List<PatrolStoreStatisticsProblemRankVO> buildMyStoreProblemRank(List<PatrolStoreStatisticsProblemRankVO> result,
                                                                             String enterpriseId,
                                                                             CurrentUser user) {
        if (CollectionUtils.isNotEmpty(result)) {
            return result;
        }
        List<String> regionIdByUserId = userAuthMappingMapper.getRegionIdByUserId(enterpriseId, user.getUserId());
        if(CollectionUtils.isEmpty(regionIdByUserId)){
            return result;
        }
        List<RegionDO> regionByRegionIds = regionMapper.getRegionByRegionIds(enterpriseId, regionIdByUserId);
        List<String> storeIds = new ArrayList<>();
        List<String> regionIds = new ArrayList<>();
        List<StoreDO> storeDOList = new ArrayList<>();
        for (RegionDO regionByRegionId : regionByRegionIds) {
            if (Constants.REGION_TYPE_PATH.equals(regionByRegionId.getRegionType())){
                regionIds.add(regionByRegionId.getRegionId());
            }else if (Constants.STORE.equals(regionByRegionId.getRegionType())){
                storeIds.add(regionByRegionId.getStoreId());
            }
        }
        if (CollectionUtils.isNotEmpty(storeIds)){
            List<StoreDO> storeByStoreIdList = storeMapper.getStoreByStoreIdList(enterpriseId, storeIds);
            storeDOList.addAll(storeByStoreIdList);
        }
        if (storeDOList.size()>Constants.TEN){
            storeDOList = storeDOList.stream().limit(10).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(storeDOList)){
            storeDOList.forEach(store -> {
                PatrolStoreStatisticsProblemRankVO vo = PatrolStoreStatisticsProblemRankVO.builder()
                        .storeId(store.getStoreId())
                        .storeName(store.getStoreName())
                        .finishQuestionNum(0)
                        .build();
                vo.setFinishPercent(vo.getFinishPercent());
                result.add(vo);
            });
        }
        return result;
    }

    /**
     * 工单排行为空的时候，拿到10个门店填充列表
     * @param result
     * @param eid
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.patrolstore.vo.PatrolStoreStatisticsRankVO>
     * @date: 2021/12/30 21:29
     */
    private List<PatrolStoreStatisticsProblemRankVO> buildEmptyStatisticsStoreProblemRank(List<PatrolStoreStatisticsProblemRankVO> result, String eid) {
        if (CollectionUtils.isNotEmpty(result)) {
            return result;
        }
        List<BasicsStoreDTO> basicsStoreDTOS = storeMapper.getBaseStoreList(eid, Constants.INDEX_TEN);
        basicsStoreDTOS.forEach(store -> {
            PatrolStoreStatisticsProblemRankVO vo = PatrolStoreStatisticsProblemRankVO.builder()
                    .storeId(store.getStoreId())
                    .storeName(store.getStoreName())
                    .finishQuestionNum(0)
                    .build();
            vo.setFinishPercent(vo.getFinishPercent());
            result.add(vo);
        });
        return result;
    }

    @Override
    public List<PatrolStoreStatisticsUserRankVO> userPatrolstoreStoreRank(String enterpriseId, PatrolStoreStatisticsRegionQuery query) {
        List<PatrolStoreStatisticsUserRankVO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(query.getRegionIds())) {
            throw new ServiceException(ErrorCodeEnum.VALIDATION_RULES_1060003);
        }
        List<String> regionIds = query.getRegionIds();
        List<Long> regionIdList = regionIds.stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
        Date startTime = query.getBeginDate();
        Date endTime = query.getEndDate();
        RegionPatrolStatisticsRequest rpsr = RegionPatrolStatisticsRequest.builder().regionIds(regionIdList).beginDate(startTime).endDate(endTime)
                .isGetDirectStore(query.getGetDirectStore()).enterpriseId(enterpriseId).build();
        List<PatrolNumRankDataDTO> patrolNumRankDataDTOS = elasticSearchService.patrolStoreNumberOfRank(rpsr);
        if (CollectionUtils.isEmpty(patrolNumRankDataDTOS)){
            return result;
        }
        List<String> userIds = patrolNumRankDataDTOS.stream().map(PatrolNumRankDataDTO::getUserId).collect(Collectors.toList());
        //通过userid查询人员名称信息
        List<EnterpriseUserDO> enterpriseUserDOS = enterpriseUserMapper.listByUserIdIgnoreActive(enterpriseId, userIds);
        Map<String, String> storeIdNameMap = enterpriseUserDOS.stream()
                .filter(a -> a.getUserId() != null && a.getName() != null)
                .collect(Collectors.toMap(data -> data.getUserId(), data -> data.getName(), (a, b) -> a));
        for (PatrolNumRankDataDTO temp:patrolNumRankDataDTOS) {
            PatrolStoreStatisticsUserRankVO pssur = PatrolStoreStatisticsUserRankVO.builder().patrolNum(temp.getPatrolNum()).userId(temp.getUserId()).
                    userName(storeIdNameMap.get(temp.getUserId())).build();
            result.add(pssur);
        }
        return result;
    }

    @Override
    public List<PatrolStoreTypeStatisticsVO> statisticsPatrolType(String enterpriseId, PatrolStoreStatisticsRegionQuery query) {
        List<PatrolStoreTypeStatisticsVO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(query.getRegionIds())) {
            throw new ServiceException(ErrorCodeEnum.VALIDATION_RULES_1060003);
        }
        List<String> regionIds = query.getRegionIds();
        List<Long> regionIdList = regionIds.stream().map(Long::valueOf).collect(Collectors.toList());
        Date startTime = query.getBeginDate();
        Date endTime = query.getEndDate();
        RegionPatrolStatisticsRequest rpsr = RegionPatrolStatisticsRequest.builder().regionIds(regionIdList).beginDate(startTime).endDate(endTime).enterpriseId(enterpriseId).build();
        List<PatrolTypeTaskCountDTO> patrolTypeTaskCountDTOS = elasticSearchService.patrolStoreStatisticsByPatrolType(rpsr);
        for (PatrolTypeTaskCountDTO temp:patrolTypeTaskCountDTOS) {
            PatrolStoreTypeStatisticsVO psts = PatrolStoreTypeStatisticsVO.builder().patrolType(temp.getPatrolType()).taskNum(temp.getTaskNum()).totalNum(temp.getTotalNum()).build();
            result.add(psts);
        }

        return result;
    }

    @Override
    public List<PatrolStoreTaskStatisticsVO> statisticsPatrolTask(String enterpriseId, PatrolStoreStatisticsRegionQuery query) {
        List<PatrolStoreTaskStatisticsVO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(query.getRegionIds())) {
            throw new ServiceException(ErrorCodeEnum.VALIDATION_RULES_1060003);
        }
        List<String> regionIds = query.getRegionIds();
        List<Long> regionIdList = regionIds.stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
        Date startTime = query.getBeginDate();
        Date endTime = query.getEndDate();
        RegionPatrolStatisticsRequest rpsr = RegionPatrolStatisticsRequest.builder().regionIds(regionIdList).beginDate(startTime).endDate(endTime).enterpriseId(enterpriseId).build();
        List<PatrolTypeTaskCountDTO> patrolTypeTaskCountDTOS = elasticSearchService.finishTaskStatistics(rpsr);
        for (PatrolTypeTaskCountDTO temp:patrolTypeTaskCountDTOS) {
            PatrolStoreTaskStatisticsVO psts = PatrolStoreTaskStatisticsVO.builder().patrolType(temp.getPatrolType()).totalNum(temp.getTotalNum()).onTimeNum(temp.getOnTimeNum()).build();
        result.add(psts);
        }
        return result;
    }

    @Override
    public List<PatrolStoreTaskStatisticsVO> statisticsUnfinishedPatrolTask(String enterpriseId, PatrolStoreStatisticsRegionQuery query) {
        List<PatrolStoreTaskStatisticsVO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(query.getRegionIds())) {
            throw new ServiceException(ErrorCodeEnum.VALIDATION_RULES_1060003);
        }
        List<String> regionIds = query.getRegionIds();
        List<Long> regionIdList = regionIds.stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
        Date startTime = query.getBeginDate();
        Date endTime = query.getEndDate();
        RegionPatrolStatisticsRequest rpsr = RegionPatrolStatisticsRequest.builder().regionIds(regionIdList).beginDate(startTime).endDate(endTime).enterpriseId(enterpriseId).build();
        List<PatrolTypeUnFinishTaskCountDTO> patrolTypeUnFinishTaskCountDTOS = elasticSearchService.unFinishTaskStatistics(rpsr);
        for (PatrolTypeUnFinishTaskCountDTO temp:patrolTypeUnFinishTaskCountDTOS) {
            PatrolStoreTaskStatisticsVO psts = PatrolStoreTaskStatisticsVO.builder().patrolType(temp.getPatrolType()).totalNum(temp.getUnExpireTaskNum()+temp.getExpireTaskNum())
                    .onTimeNum(temp.getExpireTaskNum()).build();
            result.add(psts);
        }
        return result;
    }

    @Override
    public HomePageVo statisticsHomePage(String enterpriseId, PatrolStoreStatisticsRegionQuery query) {
        HomePageVo homePageVo = new HomePageVo(Constants.INDEX_ZERO,Constants.INDEX_ZERO,Constants.INDEX_ZERO,Constants.INDEX_ZERO,Constants.INDEX_ZERO,Constants.INDEX_ZERO);
        //从redis缓存中获取 如果缓存中有值 直接返回 缓存有效期 10分钟
        String string = redis.getString(MessageFormat.format(HOMEPAGEPREFIX,enterpriseId,query.getUser().getUserId()));
        if (!StringUtils.isNullOrEmpty(string)){
            return  JSONObject.parseObject(string, HomePageVo.class);
        }
        //查询用户是否是管理员
        int storeNum = Constants.INDEX_ZERO ;
        Boolean isAdmin = false;
        List<SysRoleDO> sysRoleDOList = sysRoleMapper.listRoleByUserId(enterpriseId, query.getUser().getUserId());
        isAdmin = ListUtils.emptyIfNull(sysRoleDOList)
                .stream()
                .anyMatch(role-> org.apache.commons.lang3.StringUtils.equals(Role.MASTER.getRoleEnum(),role.getRoleEnum()));
        Date startTime = query.getBeginDate();
        Date endTime = query.getEndDate();
        List<Long> regionIds = new ArrayList<>();
        List<String> storeIdList = new ArrayList<>();
        if (isAdmin){
            //查询root节点
            RegionNode region = regionMapper.getRegionByRegionId(enterpriseId, Constants.ONE_VALUE_STRING);
            storeIdList = null;
            regionIds.add(Long.valueOf(region.getRegionId()));
            storeNum = region.getStoreCount().intValue();
        }else{
            PageHelper.startPage(query.getPageNum(),DEFAULTPAGESIZE);
            //非管理员查询用户管理的区域或者门店 50个
            List<UserAuthMappingDO> userAuthMappingDOS = userAuthMappingMapper.listUserAuthMappingByUserAndType(enterpriseId,
                    query.getUser().getUserId(),null);
            if (CollectionUtils.isEmpty(userAuthMappingDOS)){
                return homePageVo;
            }
            //只含有区域
            List<String> storeNumByRegionPathList = new ArrayList<>();
            List<UserAuthMappingDO> userAuthMappingList = userAuthMappingDOS.stream().filter(x -> Constants.REGION.equals(x.getType())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(userAuthMappingList)){
                List<String> regionIdList = userAuthMappingList.stream().map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
                List<RegionDO> regionByRegionIds = regionMapper.getRegionByRegionIds(enterpriseId, regionIdList);
                regionIds = regionIdList.stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
                List<String> regionPathList = regionByRegionIds.stream().map(RegionDO::getFullRegionPath).collect(Collectors.toList());
                //所有区域的门店数，上下级区域id都在regionPathList的时候，门店数已经去重
                storeNumByRegionPathList = storeMapper.getStoreNumByRegionPathList(enterpriseId, regionPathList);
                storeNum = storeNumByRegionPathList.size();
            }

            //如果选择的门店在区域下转范围内，需要去重
            List<UserAuthMappingDO>  userAuthMappingByStoreList= userAuthMappingDOS.stream().filter(x -> Constants.STORE.equals(x.getType())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(userAuthMappingByStoreList)){
                storeIdList = userAuthMappingByStoreList.stream().map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
                for (String storeId:storeIdList) {
                    if (!storeNumByRegionPathList.contains(storeId)){
                        storeNum++;
                    }
                }
            }
        }
        RegionPatrolStatisticsRequest rpsr = RegionPatrolStatisticsRequest.builder().regionIds(regionIds).beginDate(startTime).endDate(endTime)
                .storeIds(storeIdList).enterpriseId(enterpriseId).build();
        asynElasticSearch.asynStatisticsHomePage(rpsr,homePageVo);
        homePageVo.setStoreNum(storeNum);
        //每次查询将数据存入到redis缓存使用 有效期10分钟
        redis.setString(MessageFormat.format(HOMEPAGEPREFIX, enterpriseId,query.getUser().getUserId()), JSON.toJSONString(homePageVo),600);
        return homePageVo;
    }

    @Override
    public PageInfo<ColumnAnalyzeVO> columnAnalyze(String enterpriseId, PatrolStoreStatisticsRegionQuery query) {
        Date startTime = query.getBeginDate();
        Date endTime = query.getEndDate();
        if (Objects.isNull(startTime) || Objects.isNull(endTime)) {
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        List<ColumnAnalyzeVO> result = new ArrayList<>();
        PageInfo<ColumnAnalyzeVO> pageInfo = new PageInfo();
        List<TbMetaStaTableColumnDO> columnList = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, Collections.singletonList(query.getMetaTableId()), Boolean.FALSE);
        List<String> regionIds = query.getRegionIds();
        Long metaTableId = query.getMetaTableId();
        MetaTableStatisticsRequest build = new MetaTableStatisticsRequest();
        build.setMetaTableId(metaTableId);
        build.setBeginDate(startTime);
        build.setEndDate(endTime);
        build.setEnterpriseId(enterpriseId);
        build.setIsGetDirectStore(query.getGetDirectStore());
        List<RegionChildDTO> regionChildDTOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(query.getRegionIds())){
            if (query.isGetChild()){
                //每页50条数据
                PageHelper.startPage(query.getPageNum(),DEFAULTPAGESIZE);
                regionChildDTOS = regionMapper.getRegionByParentId(enterpriseId, regionIds, Boolean.TRUE);
                regionIds = regionChildDTOS.stream().map(RegionChildDTO::getId).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(regionChildDTOS)){
                    return pageInfo;
                }
            }
            //查询当前需要查询的区域是否有子区域
            Map<String, RegionChildDTO> containSubAreaMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(regionIds)){
                List<RegionChildDTO> containSubAreaList = regionMapper.getRegionByParentId(enterpriseId, regionIds, Boolean.TRUE);
                containSubAreaMap = containSubAreaList.stream()
                        .collect(Collectors.toMap(RegionChildDTO::getPid, Function.identity(), (a, b) -> a));
            }
            List<RegionPathDTO> regionidList = regionService.getRegionPathByList(enterpriseId, regionIds);
            List<Long> regionIdList = regionIds.stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
            //区域直连门店数量统计 getGetDirectStore=true 只查询直连门店
            if (query.getGetDirectStore()){
                List<StoreDO>  stores= storeMapper.directlyStoreCountByRegion(enterpriseId, regionIdList);
                //如果查询直连门店数量的时候 regionIds 只有一个值
                regionidList.get(Constants.INDEX_ZERO).setStoreNum(stores.isEmpty()?Constants.INDEX_ZERO:stores.size());
            }
            build.setRegionIds(regionIdList);
            List<CheckEntryStatisticsDTO> checkEntryStatistics = elasticSearchService.getCheckEntryStatistics(build);
            Map<String, CheckEntryStatisticsDTO> CheckEntryStatisticsMap = checkEntryStatistics.stream()
                    .collect(Collectors.toMap(CheckEntryStatisticsDTO::getRegionId, Function.identity(), (a, b) -> a));
            for (RegionPathDTO regionPathDTO:regionidList) {
                CheckEntryStatisticsDTO cesd = CheckEntryStatisticsMap.get(regionPathDTO.getRegionId());
                RegionChildDTO regionChildDTO = containSubAreaMap.get(regionPathDTO.getRegionId());
                ColumnAnalyzeVO columnAnalyzeVO = ColumnAnalyzeVO.builder().checkStoreNum(cesd.getCheckStoreNum()).checkNum(cesd.getCheckNum())
                        .questionNum(cesd.getQuestionNum()).qualifiedNum(cesd.getQualifiedNum()).regionName(regionPathDTO.getRegionName())
                        .storeId(cesd.getStoreId()).regionId(cesd.getRegionId()).columnNum(columnList.size())
                        .storeNum(regionPathDTO.getStoreNum()).containSubArea(regionChildDTO!=null?Boolean.TRUE:Boolean.FALSE).build();
                result.add(columnAnalyzeVO);
            }
        }else if (CollectionUtils.isNotEmpty(query.getStoreIds())) {
            build.setStoreIds(query.getStoreIds());
            List<CheckEntryStatisticsDTO> checkEntryStatistics = elasticSearchService.getCheckEntryStatistics(build);
            int checkStoreNum = Constants.INDEX_ZERO;
            int questionNum = Constants.INDEX_ZERO;
            int qualifiedNum = Constants.INDEX_ZERO;
            int checkNum = Constants.INDEX_ZERO;
            for (CheckEntryStatisticsDTO ch :checkEntryStatistics) {
                checkNum+=ch.getCheckNum();
                checkStoreNum+= ch.getCheckStoreNum();
                questionNum+= ch.getQuestionNum();
                qualifiedNum+=ch.getQualifiedNum();
            }
            ColumnAnalyzeVO columnAnalyzeVO = ColumnAnalyzeVO.builder().checkStoreNum(checkStoreNum).checkNum(checkNum)
                    .questionNum(questionNum).qualifiedNum(qualifiedNum).columnNum(columnList.size())
                    .storeNum(query.getStoreIds().size()).containSubArea(Boolean.FALSE).build();
            result.add(columnAnalyzeVO);
        }
        pageInfo.setList(result);
        pageInfo.setTotal(result.size());
        return pageInfo;
    }

    @Override
    public Boolean isBigEnterprise(String enterpriseId) {
        List<String> bigEnterpriseList = redis.listGetAll(BIGENTERPRISEKEY);
        return bigEnterpriseList.contains(enterpriseId);
    }

    @Override
    public void setBigEnterprise(String enterpriseId) {
        //将企业加入到redis中
        redis.listPushTail(BIGENTERPRISEKEY,enterpriseId);
    }

    /**
     * 统计数据
     * @param num1
     * @param num2
     * @return
     */
    public String statisticsData(Integer num1 ,Integer num2){
        if (Constants.INDEX_ZERO.equals(num1) ||Constants.INDEX_ZERO.equals(num2)){
            return "0";
        }
        DecimalFormat df = new DecimalFormat("0.00");
        String s = df.format((float)num1/num2);
        return s;
    }

    /**
     * 统计时间
     * @param num1
     * @param num2
     * @return
     */
    public Long statisticsLongData(Long num1 ,Integer num2){
        if (Constants.INDEX_ZERO.equals(num1.intValue())||Constants.INDEX_ZERO.equals(num2)){
            return 0L;
        }
        return num1/num2;
    }
}
