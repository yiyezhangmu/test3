package com.coolcollege.intelligent.service.achievement.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.enums.achievement.AchievementFormworkTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.util.CoolListUtils;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.RedisOperator;
import com.coolcollege.intelligent.dao.achievement.AchievementDetailMapper;
import com.coolcollege.intelligent.dao.achievement.AchievementFormWorkMapper;
import com.coolcollege.intelligent.dao.achievement.AchievementFormworkMappingMapper;
import com.coolcollege.intelligent.dao.achievement.AchievementTargetDetailMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.achievement.dto.AchievementFormworkMappingDTO;
import com.coolcollege.intelligent.model.achievement.dto.AchievementTotalAmountDTO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementDetailDO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementFormworkDO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTargetDetailDO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTypeDO;
import com.coolcollege.intelligent.model.achievement.request.*;
import com.coolcollege.intelligent.model.achievement.vo.*;
import com.coolcollege.intelligent.model.enums.AchievementErrorEnum;
import com.coolcollege.intelligent.model.enums.AchievementKeyPrefixEnum;
import com.coolcollege.intelligent.model.enums.StoreIsDeleteEnum;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.achievement.AchievementStatisticsService;
import com.coolcollege.intelligent.service.achievement.AchievementTypeService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.*;

/**
 * @Description: 业绩统计报表服务
 * @Author: mao
 * @CreateDate: 2021/5/25 13:27
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AchievementStatisticsServiceImpl implements AchievementStatisticsService {
    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor generalThreadPool;

    private final AchievementDetailMapper achievementDetailMapper;

    private final AchievementTargetDetailMapper achievementTargetDetailMapper;

    private final RegionMapper regionMapper;

    private final StoreMapper storeMapper;

    private final AchievementTypeService achievementTypeService;

    private final AchievementFormWorkMapper achievementFormWorkMapper;

    private final AchievementFormworkMappingMapper achievementFormworkMappingMapper;
    private final BigDecimal ZERO=new BigDecimal(0);

    @Override
    public AchievementStatisticsRegionListVO getRegionStatisticsTable(String enterpriseId,
                                                                      AchievementStatisticsReqVO req, CurrentUser user) {
        checkRegion(req);
        AchievementStatisticsRegionListVO result = new AchievementStatisticsRegionListVO();
        List<Future<List<AchievementStatisticsRegionTableVO>>> futures = new ArrayList<>();
        List<RegionDO> regions = regionMapper.getRegionPathByIds(enterpriseId, req.getRegionIds());
        List<List<RegionDO>> task = ListUtils.partition(regions, 5);
        List<AchievementStatisticsRegionTableVO> tableList = new ArrayList<>();
        for (int i = task.size() - 1; i >= 0; i--) {
            if (i == 0) {
                tableList.addAll(dealRegion(enterpriseId, task.get(i), req.getBeginDate(), req.getEndDate(), user, req.getShowCurrent()));
            } else {
                List<RegionDO> taskRegion = task.get(i);
                Future<List<AchievementStatisticsRegionTableVO>> future = generalThreadPool
                        .submit(() -> (dealRegion(enterpriseId, taskRegion, req.getBeginDate(), req.getEndDate(), user, req.getShowCurrent())));
                futures.add(future);
            }
        }
        for (Future<List<AchievementStatisticsRegionTableVO>> t : futures) {
            try {
                tableList.addAll(t.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error(AchievementErrorEnum.STATISTICS_QUERY_FAIL.message + e.getMessage());
                Thread.currentThread().interrupt();
                throw new ServiceException(AchievementErrorEnum.STATISTICS_QUERY_FAIL.code,
                        AchievementErrorEnum.STATISTICS_QUERY_FAIL.message);
            }
        }
        result.setList(tableList);
        return result;
    }

    @Override
    public AchievementStatisticsRegionSeriesVO getRegionStatisticsChart(String enterpriseId,
                                                                        AchievementStatisticsReqVO req, CurrentUser user) {
        checkRegion(req);
        AchievementStatisticsRegionSeriesVO result = new AchievementStatisticsRegionSeriesVO();
        List<Future<List<AchievementStatisticsRegionChartVO>>> futures = new ArrayList<>();
        List<RegionDO> regions = regionMapper.getRegionPathByIds(enterpriseId, req.getRegionIds());
        List<List<RegionDO>> task = ListUtils.partition(regions, 5);
        List<AchievementStatisticsRegionChartVO> series = new ArrayList<>();
        for (int i = task.size() - 1; i >= 0; i--) {
            if (i == 0) {
                series.addAll(dealRegionChart(enterpriseId, task.get(i), req.getBeginDate(), req.getEndDate(), user, req.getShowCurrent(),
                        req.getAchievementFormworkId(), req.getAchievementTypeId()));
            } else {
                List<RegionDO> taskRegion = task.get(i);
                Future<List<AchievementStatisticsRegionChartVO>> future = generalThreadPool.submit(
                        () -> dealRegionChart(enterpriseId, taskRegion, req.getBeginDate(), req.getEndDate(), user, req.getShowCurrent(),
                                req.getAchievementFormworkId(), req.getAchievementTypeId()));
                futures.add(future);
            }
        }
        for (Future<List<AchievementStatisticsRegionChartVO>> t : futures) {
            try {
                series.addAll(t.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error(AchievementErrorEnum.STATISTICS_QUERY_FAIL.message + e.getMessage());
                Thread.currentThread().interrupt();
                throw new ServiceException(AchievementErrorEnum.STATISTICS_QUERY_FAIL.code,
                        AchievementErrorEnum.STATISTICS_QUERY_FAIL.message);
            }
        }
        result.setSeries(series);
        return result;
    }

    @Override
    public AchievementStatisticsStoreTableVO getStoreStatistics(String enterpriseId, String storeId, Date beginDate) {
        StoreDO storeDO = getStoreDO(enterpriseId, storeId);
        String monthBegin = DateUtil.format(DateUtil.getFirstOfDayMonth(beginDate));
        String monthEnd = DateUtil.format(DateUtil.getLastOfDayMonth(beginDate));
        String beginTime = DateUtil.format(DateUtil.getBeginOfDay(beginDate));
        AchievementStatisticsStoreTableVO result = new AchievementStatisticsStoreTableVO();
        AchievementDetailDO storeDate = achievementDetailMapper.getStoreAmount(enterpriseId, storeId, monthBegin, monthEnd);
        AchievementDetailDO storeDay = achievementDetailMapper.getStoreAmount(enterpriseId, storeId, beginTime, beginTime);
        result.setStoreName(storeDO.getStoreName());
        AchievementTargetDetailDO param = new AchievementTargetDetailDO();
        param.setStoreId(storeId);
        param.setBeginDate(DateUtil.getFirstOfDayMonth(beginDate));
        param.setTimeType(AchievementKeyPrefixEnum.ACHIEVEMENT_TARGET_MONTH.type);
        AchievementTargetDetailDO storeTarget = achievementTargetDetailMapper.getTargetByStore(enterpriseId, param);
        result
                .setAchievementTarget(Objects.nonNull(storeTarget) ? storeTarget.getAchievementTarget() : BigDecimal.ZERO);
        result.setCompletionTarget(Objects.isNull(storeDate) ? BigDecimal.ZERO : storeDate.getAchievementAmount());
        result.setCompletionDayTarget(Objects.isNull(storeDay) ? BigDecimal.ZERO : storeDay.getAchievementAmount());
        double rate = 0;
        if (Objects.nonNull(result.getAchievementTarget())
                && result.getAchievementTarget().compareTo(BigDecimal.ZERO) > 0) {
            rate = result.getCompletionTarget().multiply(new BigDecimal(100))
                    .divide(result.getAchievementTarget(), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        result.setCompletionRate(rate);
        return result;
    }

    private void dealNoAchievementStoreDetailVO(List<String> nullDateStoreIdList, Map<String, StoreDO> storeNameMap, Map<Long, String> regionNameMap, List<AchievementStoreDetailVO> voList) {
        nullDateStoreIdList.forEach((storeId) -> {
            AchievementStoreDetailVO vo = new AchievementStoreDetailVO();
            vo.setStoreId(storeId);
            StoreDO storeDO = storeNameMap.get(storeId);
            vo.setStoreName(storeDO.getStoreName());
            vo.setStoreNum(storeDO.getStoreNum());
            if (MapUtils.isNotEmpty(regionNameMap)) {
                vo.setRegionName(regionNameMap.get(storeDO.getRegionId()));
            }
            vo.setProduceUserNum(0);
            vo.setCompletionTarget(ZERO);
            voList.add(vo);
        });
    }

    @Override
    public List<AchievementDetailVO> detailStatistics(String eid, AchievementDetailRequest request) {

        //处理入参
        dealAchievementDetailRequest(request);
        List<String> storeIdList = StrUtil.splitTrim(request.getStoreIdStr(), ",");
        //处理逗号分隔的参数
        List<Long> typeLongIdList = getAchievementTypeLongIdList(request.getAchievementTypeIdStr());
        List<String> produceUserIdList = StrUtil.splitTrim(request.getProduceUserIdStr(), ",");

        String regionPath = null;
        if(request.getRegionId() != null){
            RegionDO regionDO = regionMapper.getByRegionId(eid, request.getRegionId());
            storeIdList = null;
            if(regionDO != null){
                regionPath = regionDO.getFullRegionPath();
            }
        }

        if (StringUtils.isBlank(regionPath) && StringUtils.isBlank(request.getStoreIdStr())) {
            return Collections.emptyList();
        }

        PageHelper.startPage(request.getPageNo(),request.getPageSize());
        List<AchievementDetailVO> achievementDetailVOList = achievementDetailMapper.pageAchievementDetail(eid, new Date(request.getBeginDate()), new Date(request.getEndDate()),
                storeIdList, request.getAchievementFormworkId(), typeLongIdList, produceUserIdList, request.getIsNullProduceUser(), null,
                request.getStoreName(),request.getShowCurrent(),regionPath,request.getRegionId());
        AchievementFormworkDO achievementFormworkDO = achievementFormWorkMapper.get(eid, request.getAchievementFormworkId());
        List<AchievementTypeDO> achievementTypeDOList = achievementTypeService.listAllTypes(eid);
        if (CollectionUtils.isEmpty(achievementTypeDOList)) {
            return Collections.emptyList();
        }
        Map<Long, String> achievementTypeNameMap = ListUtils.emptyIfNull(achievementTypeDOList)
                .stream()
                .collect(Collectors.toMap(AchievementTypeDO::getId, AchievementTypeDO::getName, (a, b) -> a));
        ListUtils.emptyIfNull(achievementDetailVOList)
                .forEach(data -> mapAchievementDetailVO(achievementFormworkDO, achievementTypeNameMap, data));
        return achievementDetailVOList;
    }

    private List<Long> getAchievementTypeLongIdList(String achievementTypeIdStr) {
        List<String> achievementTypeIdList = StrUtil.splitTrim(achievementTypeIdStr, ",");
        return ListUtils.emptyIfNull(achievementTypeIdList)
                .stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public PageVO<AchievementDetailVO> achievementTypeStatistics(String eid, AchievementTypeStatisticsRequest request) {
        /**
         * 整体逻辑，
         * 1.先将条件中的门店进行分页
         * 2.将门店Id传入明细表查询
         * 3.组装数据
         */
        //处理入参
        dealAchievementTypeStatisticsRequest(request);
        //处理逗号分隔的参数

        List<Long> typeLongIdList = getAchievementTypeLongIdList(request.getAchievementTypeIdStr());
        List<StoreDO> storeDOList = getStoreList(eid, request.getStoreIdStr(), request.getRegionId(), request.getStoreName(), request.getShowCurrent(), request.getPageNo(), request.getPageSize());
        PageVO<AchievementDetailVO> pageVO = new PageVO<>();

        if (CollectionUtils.isEmpty(storeDOList)) {
            return pageVO;
        }
        PageInfo<StoreDO> storePageInfo = dealPage(storeDOList, pageVO);
        List<StoreDO> list = storePageInfo.getList();
        Map<String, StoreDO> storeNameMap = list.stream()
                .collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));
        List<String> queryStoreIdList = list.stream().map(StoreDO::getStoreId).collect(Collectors.toList());
        Map<Long, String> regionNameMap = regionNameMap(eid, list);

        List<AchievementDetailVO> achievementDetailVOList = achievementDetailMapper.achievementDetailGroupByAchievementType(eid,
                new Date(request.getBeginDate()), new Date(request.getEndDate()),
                queryStoreIdList, request.getAchievementFormworkId(), typeLongIdList, null, null, null);
        AchievementFormworkDO achievementFormworkDO = achievementFormWorkMapper.get(eid, request.getAchievementFormworkId());

        List<AchievementFormworkMappingDTO> achievementFormworkMappingDTOList = achievementFormworkMappingMapper.getListByFormWorkIdAndTypeId(eid, request.getAchievementFormworkId(), typeLongIdList);

        if (CollectionUtils.isEmpty(achievementFormworkMappingDTOList) || achievementFormworkDO == null) {
            return pageVO;
        }
        Map<Long, String> achievementTypeNameMap = ListUtils.emptyIfNull(achievementFormworkMappingDTOList)
                .stream()
                .collect(Collectors.toMap(AchievementFormworkMappingDTO::getTypeId, AchievementFormworkMappingDTO::getTypeName, (a, b) -> a));

        List<AchievementDetailVO> voList = new ArrayList<>();
        //填充组装数据

        Map<String, AchievementDetailVO> storeIdTypeIdMap = ListUtils.emptyIfNull(achievementDetailVOList).stream()
                .collect(Collectors.toMap(data -> data.getStoreId() + "_" + data.getAchievementTypeId(), data -> data, (a, b) -> a));
        List<String> allStoreIdList = new ArrayList<>(storeNameMap.keySet());

        for (String storeId : allStoreIdList) {
            achievementTypeNameMap.forEach((typeId, typeName) -> {
                AchievementDetailVO vo = new AchievementDetailVO();
                StoreDO storeDO = storeNameMap.get(storeId);
                vo.setStoreId(storeId);
                vo.setStoreName(storeDO.getStoreName());
                vo.setStoreNum(storeDO.getStoreNum());
                vo.setEndTime(new Date(request.getEndDate()));
                vo.setBeginTime(new Date(request.getBeginDate()));
                vo.setFormworkName(achievementFormworkDO.getName());
                vo.setAchievementTypeName(typeName);
                vo.setAchievementTypeId(typeId);
                if (MapUtils.isNotEmpty(regionNameMap)) {
                    vo.setRegionName(regionNameMap.get(storeDO.getRegionId()));
                }
                if (storeIdTypeIdMap.get(storeId + "_" + typeId) != null) {
                    AchievementDetailVO achievementDetailVO = storeIdTypeIdMap.get(storeId + "_" + typeId);
                    vo.setAchievementAmount(achievementDetailVO.getAchievementAmount());
                }else {
                    vo.setAchievementAmount(ZERO);
                }
                voList.add(vo);
            });
        }
        //没有查到数据的StoreID组装返回数据
        pageVO.setList(voList);

        return pageVO;
    }

    private PageInfo<StoreDO> dealPage(List<StoreDO> storeDOList, PageVO<AchievementDetailVO> pageVO) {
        PageInfo<StoreDO> storePageInfo = new PageInfo<>(storeDOList);
        long total = storePageInfo.getTotal();
        pageVO.setTotal(total);
        return storePageInfo;
    }

    @Override
    public PageVO<AchievementStoreDetailVO> storeDetailStatistics(String eid, AchievementStoreStatisticsRequest request) {
        //处理入参
        if (request.getBeginDate() == null) {
            throw new ServiceException(ACH_DATE_BEGIN_NOT_NULL);
        }
        if (request.getEndDate() == null) {
            throw new ServiceException(ACH_DATE_END_NOT_NULL);

        }
        dealAchievementStoreStatisticsRequest(request);
        PageVO<AchievementStoreDetailVO> pageVO = new PageVO<>();
        //处理逗号分隔的参数
        List<StoreDO> storeDOList = getStoreList(eid, request.getStoreIdStr(), request.getRegionId(), request.getStoreName(), request.getShowCurrent(), request.getPageNo(), request.getPageSize());
        if (CollectionUtils.isEmpty(storeDOList)) {
            return pageVO;
        }
        PageInfo<StoreDO> storePageInfo = new PageInfo<>(storeDOList);
        long total = storePageInfo.getTotal();
        pageVO.setTotal(total);
        List<StoreDO> list = storePageInfo.getList();
        Map<Long, String> regionNameMap = regionNameMap(eid, list);
        Map<String, StoreDO> storeNameMap = list.stream().collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));
        List<String> queryStoreIdList = list.stream().map(StoreDO::getStoreId).collect(Collectors.toList());
        List<AchievementDetailDO> achievementDetailDOList = achievementDetailMapper.achievementDetailList(eid, new Date(request.getBeginDate()), new Date(request.getEndDate()),
                queryStoreIdList, null, null, null, null, null, AchievementFormworkTypeEnum.NORMAL.getCode());
        List<AchievementFormworkDO> achievementFormworkDOList = achievementFormWorkMapper.listAll(eid, null);

        List<AchievementTypeDO> achievementTypeDOList = achievementTypeService.listAllTypes(eid);
        if (CollectionUtils.isEmpty(achievementTypeDOList) || CollectionUtils.isEmpty(achievementFormworkDOList)) {
            return pageVO;
        }
        Map<Long, String> achievementFormworkMap = ListUtils.emptyIfNull(achievementFormworkDOList)
                .stream()
                .collect(Collectors.toMap(AchievementFormworkDO::getId, AchievementFormworkDO::getName, (a, b) -> a));
        Map<Long, String> achievementTypeNameMap = ListUtils.emptyIfNull(achievementTypeDOList)
                .stream()
                .collect(Collectors.toMap(AchievementTypeDO::getId, AchievementTypeDO::getName, (a, b) -> a));
        if (CollectionUtils.isEmpty(achievementDetailDOList)) {
            //没有查到明细数据组装门店基本数据返回
            List<AchievementStoreDetailVO> voList = new ArrayList<>();
            dealNoAchievementStoreDetailVO(queryStoreIdList, storeNameMap, regionNameMap, voList);
            pageVO.setList(voList);
        } else {
            List<AchievementStoreDetailVO> voList = new ArrayList<>();
            Map<String, List<AchievementDetailDO>> storeIdGroupAchievemnentDetail = achievementDetailDOList.stream()
                    .collect(Collectors.groupingBy(AchievementDetailDO::getStoreId));
            List<String> detailStoreIdList = new ArrayList<>(storeIdGroupAchievemnentDetail.keySet());
            for (String storeId : detailStoreIdList) {
                AchievementStoreDetailVO vo = new AchievementStoreDetailVO();
                List<AchievementDetailDO> achievementDetailList = storeIdGroupAchievemnentDetail.get(storeId);
                BigDecimal reduce = achievementDetailList.stream()
                        .map(AchievementDetailDO::getAchievementAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                vo.setCompletionTarget(reduce);
                //组装基本数据
                vo.setStoreId(storeId);
                vo.setStoreName(storeNameMap.get(storeId).getStoreName());
                vo.setStoreNum(storeNameMap.get(storeId).getStoreNum());
                vo.setRegionId(storeNameMap.get(storeId).getRegionId());
                vo.setRegionName(regionNameMap.get(storeNameMap.get(storeId).getRegionId()));
                //分组聚合模板，类型
                List<AchievementFormworkDetailVO> formDetailVOList = getFormworkDetailVOList(achievementFormworkMap, achievementTypeNameMap, achievementDetailList);
                //分组聚合产生人 1.先将没有产生人的Id过滤 再分组 最后计算占比
                groupProduceUser(achievementDetailList, vo);
                vo.setFormworkDetailVOList(formDetailVOList);
                voList.add(vo);
            }
            List<String> allStoreIdList = new ArrayList<>(storeNameMap.keySet());
            List<String> reduceaListThanbList = CoolListUtils.getAddaListThanbList(detailStoreIdList, allStoreIdList);
            if (CollectionUtils.isNotEmpty(reduceaListThanbList)) {
                //没有查到数据的StoreID组装返回数据
                dealNoAchievementStoreDetailVO(reduceaListThanbList, storeNameMap, regionNameMap, voList);
            }
            pageVO.setList(voList);

        }
        return pageVO;
    }

    private List<AchievementFormworkDetailVO> getFormworkDetailVOList(Map<Long, String> achievementFormworkMap, Map<Long, String> achievementTypeNameMap, List<AchievementDetailDO> achievementDetailList) {
        Map<Long, List<AchievementDetailDO>> formworkGroup = ListUtils.emptyIfNull(achievementDetailList)
                .stream()
                .collect(Collectors.groupingBy(AchievementDetailDO::getAchievementFormworkId));
        List<AchievementFormworkDetailVO> formDetailVOList = new ArrayList<>();
        formworkGroup.forEach((formworkId, formList) -> {
            groupFormwork(achievementFormworkMap, achievementTypeNameMap, formDetailVOList, formworkId, formList);
        });
        return formDetailVOList;
    }

    private void groupProduceUser(List<AchievementDetailDO> achievementDetailDOList, AchievementStoreDetailVO vo) {
        //分组聚合产生人 1.先将没有产生人的Id过滤 再分组 最后计算占比
        List<AchievementProduceUserVO> achievementProduceUserVOList = new ArrayList<>();
        List<AchievementDetailDO> nullProduceUserList = achievementDetailDOList.stream()
                .filter(data -> StringUtils.isBlank(data.getProduceUserId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(nullProduceUserList)) {
            AchievementProduceUserVO achievementProduceUserVO = new AchievementProduceUserVO();
            BigDecimal reduce = nullProduceUserList.stream()
                    .map(AchievementDetailDO::getAchievementAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            achievementProduceUserVO.setUserAchievementAmount(reduce);
            achievementProduceUserVO.setUserName("未选择");
            achievementProduceUserVOList.add(achievementProduceUserVO);
        }
        Map<String, List<AchievementDetailDO>> produceUserGroup = achievementDetailDOList.stream()
                .filter(data -> StringUtils.isNotBlank(data.getProduceUserId()))
                .collect(Collectors.groupingBy(AchievementDetailDO::getProduceUserId));
        if(MapUtils.isNotEmpty(produceUserGroup)){
            vo.setProduceUserNum(produceUserGroup.size());
        }else {
            vo.setProduceUserNum(0);
        }
        MapUtils.emptyIfNull(produceUserGroup)
                .forEach((produceUser, produceUserList) -> {
                    AchievementProduceUserVO achievementProduceUserVO = new AchievementProduceUserVO();
                    achievementProduceUserVO.setUserName(produceUserList.get(0).getProduceUserName());
                    BigDecimal reduce = produceUserList.stream()
                            .map(AchievementDetailDO::getAchievementAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    achievementProduceUserVO.setUserAchievementAmount(reduce);
                    achievementProduceUserVOList.add(achievementProduceUserVO);
                });
        //计算产生人之间的占比   1.先求门店总金额  2计算占比
        BigDecimal storeAllAmount = achievementDetailDOList.stream()
                .map(AchievementDetailDO::getAchievementAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (storeAllAmount.compareTo(BigDecimal.ZERO) != 0) {
            achievementProduceUserVOList.forEach(data -> {
                BigDecimal divide = data.getUserAchievementAmount().multiply(new BigDecimal(100)).divide(storeAllAmount, 2, BigDecimal.ROUND_HALF_UP);
                data.setAmountPercent(divide.divide(new BigDecimal(100),4, BigDecimal.ROUND_HALF_UP));
                data.setAmountPercentStr(divide+ "%");
            });
        }
        vo.setProduceUserVOList(achievementProduceUserVOList);
    }

    private void groupFormwork(Map<Long, String> achievementFormworkMap, Map<Long, String> achievementTypeNameMap, List<AchievementFormworkDetailVO> formDetailVOList, Long formworkId, List<AchievementDetailDO> formList) {
        AchievementFormworkDetailVO formworkDetailVO = new AchievementFormworkDetailVO();
        formworkDetailVO.setId(formworkId);
        formworkDetailVO.setFormworkName(achievementFormworkMap.get(formworkId));
        Map<Long, List<AchievementDetailDO>> typeGroup = formList.stream().collect(Collectors.groupingBy(AchievementDetailDO::getAchievementTypeId));
        List<AchievementTypeDetailVO> achievementTypeDetailVOList = new ArrayList<>();
        typeGroup.forEach((typeId, typeList) -> {
            groupType(achievementTypeNameMap, achievementTypeDetailVOList, typeId, typeList);
        });
        formworkDetailVO.setTypeDetailVOList(achievementTypeDetailVOList);
        formDetailVOList.add(formworkDetailVO);
    }

    /**
     * 聚合分组
     *
     * @param achievementTypeNameMap
     * @param achievementTypeDetailVOList
     * @param typeId
     * @param typeList
     */
    private void groupType(Map<Long, String> achievementTypeNameMap, List<AchievementTypeDetailVO> achievementTypeDetailVOList, Long typeId, List<AchievementDetailDO> typeList) {
        AchievementTypeDetailVO typeDetailVO = new AchievementTypeDetailVO();
        typeDetailVO.setName(achievementTypeNameMap.get(typeId));
        BigDecimal reduce = typeList.stream().map(AchievementDetailDO::getAchievementAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        typeDetailVO.setTypeAmount(reduce);
        achievementTypeDetailVOList.add(typeDetailVO);
    }

    @Override
    public List<StoreDO> getStoreList(String eid, String storeIdStr, Long regionId, String storeName, Boolean showCurrent, Integer pageNo, Integer pageSize) {
        List<String> storeIdList = StrUtil.splitTrim(storeIdStr, ",");
        if (CollectionUtils.isEmpty(storeIdList)) {
            if (regionId == null) {
                return null;
            }
        }
        String fullRegionPath = null;
        if (regionId != null) {
            RegionDO regionDO = regionMapper.getByRegionId(eid, regionId);
            if (regionDO == null) {
                return null;
            }
            fullRegionPath = regionDO.getFullRegionPath();
        }
        if (StringUtils.isNotBlank(fullRegionPath)) {
            storeIdList = null;
        }
        PageHelper.startPage(pageNo, pageSize);
        return storeMapper.listStoreAndShowCurrent(eid, storeName,
                regionId, fullRegionPath, storeIdList, showCurrent, StoreIsDeleteEnum.EFFECTIVE.getValue());
    }

    @Override
    public Integer countStoreList(String eid, List<String> storeIdList, Long regionId, String storeName, Boolean showCurrent) {
        if (CollectionUtils.isEmpty(storeIdList)) {
            if (regionId == null) {
                return null;
            }
        }
        String fullRegionPath = null;
        if (Objects.nonNull(regionId)) {
            RegionDO regionDO = regionMapper.getByRegionId(eid, regionId);
            if (regionDO == null) {
                return null;
            }
            fullRegionPath = regionDO.getFullRegionPath();
            storeIdList = new ArrayList<>();
        }
        return storeMapper.countStoreAndShowCurrent(eid, storeName,
                regionId, fullRegionPath, storeIdList, showCurrent, null);
    }

    @Override
    public PageVO<AchievementMonthDetailVO> storeMonthStatistics(String eid, AchievementStoreStatisticsRequest request) {
        //处理入参
        if (request.getBeginDate() == null) {
            throw new ServiceException(ACH_DATE_BEGIN_NOT_NULL);
        }
        if (request.getEndDate() == null) {
            request.setEndDate(DateUtil.getLastOfDayMonth(new Date(request.getBeginDate())).getTime());
        }
        dealAchievementStoreStatisticsRequest(request);
        PageVO<AchievementMonthDetailVO> pageVO = new PageVO<>();
        //处理逗号分隔的参数
        List<StoreDO> storeDOList = getStoreList(eid, request.getStoreIdStr(), request.getRegionId(), request.getStoreName(), request.getShowCurrent(), request.getPageNo(), request.getPageSize());
        if (CollectionUtils.isEmpty(storeDOList)) {
            return pageVO;
        }
        long total = new PageInfo<>(storeDOList).getTotal();
        pageVO.setTotal(total);
        List<StoreDO> list = new PageInfo<>(storeDOList).getList();
        Map<String, StoreDO> storeNameMap = list.stream()
                .collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));
        List<String> queryStoreIdList = list.stream().map(StoreDO::getStoreId).collect(Collectors.toList());
        Map<Long, String> regionNameMap = regionNameMap(eid, list);

        List<AchievementDetailDO> achievementDetailDOList = achievementDetailMapper.achievementDetailListGroupByStore(eid, new Date(request.getBeginDate()), new Date(request.getEndDate()),
                queryStoreIdList, null, null, null, null, null, AchievementFormworkTypeEnum.NORMAL.getCode());

        List<AchievementMonthDetailVO> voList = new ArrayList<>();
        Map<String, AchievementDetailDO> detailDOMap = ListUtils.emptyIfNull(achievementDetailDOList)
                .stream()
                .collect(Collectors.toMap(AchievementDetailDO::getStoreId, data -> data, (a, b) -> a));
        storeNameMap.forEach((storeId, storeDO) -> {
            AchievementMonthDetailVO vo = new AchievementMonthDetailVO();
            AchievementDetailDO achievementDetailDO = detailDOMap.get(storeId);
            vo.setStoreId(storeId);
            vo.setStoreName(storeDO.getStoreName());
            vo.setRegionId(storeDO.getRegionId());
            vo.setRegionName(regionNameMap.get(storeDO.getRegionId()));
            vo.setStoreNum(storeDO.getStoreNum());
            if (MapUtils.isNotEmpty(detailDOMap) && achievementDetailDO != null) {
                vo.setCompletionTarget(achievementDetailDO.getAchievementAmount());
            }else {
                vo.setCompletionTarget(ZERO);
            }
            voList.add(vo);
        });

        //组装完成目标率和业绩目标
        Date beginDate = DateUtil.getFirstOfDayMonth(new Date(request.getBeginDate()));
        AchievementTargetRequest achievementTargetRequest = new AchievementTargetRequest();
        achievementTargetRequest.setStoreIds(queryStoreIdList);
        achievementTargetRequest.setTimeType(AchievementKeyPrefixEnum.ACHIEVEMENT_TARGET_MONTH.type);
        achievementTargetRequest.setBeginDate(beginDate);
        List<AchievementTargetDetailDO> targetDetailDOList = achievementTargetDetailMapper.getTargetByStoreAndDate(eid, achievementTargetRequest);
        if (CollectionUtils.isNotEmpty(targetDetailDOList)) {
            Map<String, AchievementTargetDetailDO> storeTargetMap = targetDetailDOList.stream()
                    .collect(Collectors.toMap(AchievementTargetDetailDO::getStoreId, data -> data, (a, b) -> a));
            voList.forEach(data -> {
                AchievementTargetDetailDO achievementTargetDetailDO = storeTargetMap.get(data.getStoreId());
                if (achievementTargetDetailDO != null) {
                    data.setAchievementTarget(achievementTargetDetailDO.getAchievementTarget());
                    if (data.getCompletionTarget() != null && achievementTargetDetailDO.getAchievementTarget().compareTo(BigDecimal.ZERO) != 0) {
                        BigDecimal completionRate = data.getCompletionTarget()
                                .divide(achievementTargetDetailDO.getAchievementTarget(), 2, BigDecimal.ROUND_HALF_UP);
                        data.setCompletionRate(completionRate.doubleValue());
                        data.setCompletionRateStr(completionRate.multiply(new BigDecimal(100)).intValue() + "%");
                    }
                }else {
                    data.setAchievementTarget(ZERO);
                }
            });
        }
        pageVO.setList(voList);
        return pageVO;
    }

    @Override
    public AchievementTotalAmountDTO totalAmountStatistics(String eid, AchievementTotalStatisticsRequest request) {
        String regionPath = null;
        RegionDO regionDO = regionMapper.getByRegionId(eid, request.getRegionId());
        if(regionDO != null){
            regionPath = regionDO.getFullRegionPath();
        }
        List<String> idList = null;
        if(StringUtils.isNotBlank(request.getStoreIdStr())){
            idList = Arrays.asList(request.getStoreIdStr().split(","));
        }
        AchievementTotalAmountDTO achievementTotalAmount = achievementDetailMapper.getAchievementTotalAmount(eid, idList, regionPath,
                request.getBeginDate(), request.getEndDate());
        return achievementTotalAmount;
    }

    private Map<Long, String> regionNameMap(String eid, List<StoreDO> list) {
        List<Long> regionIdList = list.stream()
                .map(StoreDO::getRegionId)
                .distinct()
                .collect(Collectors.toList());
        List<RegionDO> regionDOList = regionMapper.getByIds(eid, regionIdList);
        return ListUtils.emptyIfNull(regionDOList)
                .stream()
                .collect(Collectors.toMap(RegionDO::getId, RegionDO::getName, (a, b) -> a));
    }

    private void mapAchievementDetailVO(AchievementFormworkDO achievementFormworkDO, Map<Long, String> achievementTypeNameMap, AchievementDetailVO data) {

        data.setFormworkName(achievementFormworkDO.getName());
        data.setAchievementTypeName(achievementTypeNameMap.get(data.getAchievementTypeId()));
    }

    private void dealAchievementDetailRequest(AchievementDetailRequest achievementDetailRequest) {
        //时间处理
        if (achievementDetailRequest.getBeginDate() == null) {
            throw new ServiceException(ACH_DATE_BEGIN_NOT_NULL);
        }
        if (achievementDetailRequest.getEndDate() == null) {
            achievementDetailRequest.setEndDate(DateUtil.getLastOfDayMonth(new Date(achievementDetailRequest.getBeginDate())).getTime());
        }
        //参数校验
        DateUtils.checkDayInterval(achievementDetailRequest.getBeginDate(), achievementDetailRequest.getEndDate(), 31L);
        if (achievementDetailRequest.getAchievementFormworkId() == null) {
            throw new ServiceException(ACH_FORMWORK_ID_NOT_NULL);
        }
        if (achievementDetailRequest.getShowCurrent() == null) {
            achievementDetailRequest.setShowCurrent(true);
        }
        //处理时间参数
        //处理分页参数
        if (achievementDetailRequest.getPageNo() == null) {
            achievementDetailRequest.setPageNo(1);
        }
        if (achievementDetailRequest.getPageSize() == null) {
            achievementDetailRequest.setPageSize(10);
        }

    }

    private void dealAchievementStoreStatisticsRequest(AchievementStoreStatisticsRequest request) {

        //参数校验
        DateUtils.checkDayInterval(request.getBeginDate(), request.getEndDate(), 31L);

        //处理时间参数
        //处理分页参数
        if (request.getPageNo() == null) {
            request.setPageNo(1);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }
        if (request.getShowCurrent() == null) {
            request.setShowCurrent(true);
        }
    }

    private void dealAchievementTypeStatisticsRequest(AchievementTypeStatisticsRequest achievementTypeStatisticsRequest) {
        //时间处理
        if (achievementTypeStatisticsRequest.getBeginDate() == null) {
            throw new ServiceException(ACH_DATE_BEGIN_NOT_NULL);
        }
        if (achievementTypeStatisticsRequest.getEndDate() == null) {
            achievementTypeStatisticsRequest.setEndDate(DateUtil.getLastOfDayMonth(new Date(achievementTypeStatisticsRequest.getBeginDate())).getTime());
        }
        //参数校验
        DateUtils.checkDayInterval(achievementTypeStatisticsRequest.getBeginDate(), achievementTypeStatisticsRequest.getEndDate(), 31L);
        if (achievementTypeStatisticsRequest.getAchievementFormworkId() == null) {
            throw new ServiceException(ACH_FORMWORK_ID_NOT_NULL);
        }

        if (achievementTypeStatisticsRequest.getPageSize() == null) {
            achievementTypeStatisticsRequest.setPageSize(10);
        }
        //处理时间参数
        //处理分页参数
        if (achievementTypeStatisticsRequest.getPageNo() == null) {
            achievementTypeStatisticsRequest.setPageNo(1);
        }
        if (achievementTypeStatisticsRequest.getShowCurrent() == null) {
            achievementTypeStatisticsRequest.setShowCurrent(true);
        }
    }

    /**
     * 统计表按多个区域查询返回
     *
     * @param enterpriseId
     * @param regionList
     * @param beginDate
     * @param user
     * @return List<AchievementStatisticsRegionTableVO>
     * @author mao
     * @date 2021/6/1 14:50
     */
    public List<AchievementStatisticsRegionTableVO> dealRegion(String enterpriseId, List<RegionDO> regionList,
                                                               Date beginDate, Date endDate, CurrentUser user, Boolean showCurrent) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        List<AchievementStatisticsRegionTableVO> list = new ArrayList<>();
        Map<Long, String> typeMap = achievementTypeService.getMapType(enterpriseId);
        List<AchievementFormworkDO> achievementFormworkDOList = achievementFormWorkMapper.listAll(enterpriseId, null);
        Map<Long, String> formworkNameMap = ListUtils.emptyIfNull(achievementFormworkDOList).stream()
                .collect(Collectors.toMap(AchievementFormworkDO::getId, AchievementFormworkDO::getName, (a, b) -> a));
        for (RegionDO region : regionList) {
            StringBuilder sb = new StringBuilder();
            String startTime = DateUtil.format(beginDate);
            String endTime;
            sb.append(AchievementKeyPrefixEnum.ACHIEVEMENT_REGION_TABLE.type).append(region.getId()).append("_")
                    .append(enterpriseId).append(":").append(startTime).append("_!").append(showCurrent);
            if (Objects.nonNull(endDate)) {
                endTime = DateUtil.format(endDate);
                sb.append("_").append(endTime);
            } else {
                endTime = DateUtil.format(DateUtil.getLastOfDayMonth(beginDate));
            }
            String redisKey = sb.toString();
            String redisObject = RedisOperator.get(redisKey);
            AchievementStatisticsRegionTableVO result;
            if (StringUtils.isEmpty(redisObject)) {
                result = new AchievementStatisticsRegionTableVO();
                result.setRegionName(region.getName());
                List<AchievementDetailDO> details = ListUtils.emptyIfNull(achievementDetailMapper
                        .getRegionAmount(enterpriseId, region.getFullRegionPath(),region.getId(), startTime, endTime, showCurrent));
                List<AchievementFormworkDetailVO> formDetailVOList = getFormworkDetailVOList(formworkNameMap, typeMap, details);
                result.setTypeData(formDetailVOList);
                BigDecimal completionTarget = ListUtils.emptyIfNull(details)
                        .stream()
                        .map(AchievementDetailDO::getAchievementAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                result.setCompletionTarget(completionTarget);
                if (Objects.isNull(endDate)) {
                    BigDecimal acTarget =
                            achievementTargetDetailMapper.getRegionTargetAmount(enterpriseId, region.getFullRegionPath(),
                                    AchievementKeyPrefixEnum.ACHIEVEMENT_TARGET_MONTH.type, startTime, endTime);
                    if (Objects.isNull(acTarget)) {
                        acTarget = BigDecimal.ZERO;
                    }
                    result.setAchievementTarget(acTarget);
                    double rate = 0;
                    if (Objects.nonNull(acTarget) && acTarget.compareTo(BigDecimal.ZERO) > 0) {
                        rate = result.getCompletionTarget().multiply(new BigDecimal(100))
                                .divide(result.getAchievementTarget(), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    }
                    result.setCompletionRate(rate);
                }
                RedisOperator.setex(redisKey, JSON.toJSONString(result),
                        AchievementErrorEnum.REGION_STATISTICS_MAX.code.longValue());
            } else {
                result = JSON.parseObject(redisObject, AchievementStatisticsRegionTableVO.class);
            }
            list.add(result);
        }
        return list;
    }

    /**
     * 折线图多区域统计
     *
     * @param enterpriseId
     * @param regionList
     * @param beginDate
     * @param user
     * @return List<AchievementStatisticsRegionChartVO>
     * @author mao
     * @date 2021/6/1 14:59
     */
    private List<AchievementStatisticsRegionChartVO> dealRegionChart(String enterpriseId, List<RegionDO> regionList,
                                                                     Date beginDate, Date endDate, CurrentUser user, Boolean showCurrent, Long achievementFormworkId, Long achievementTypeId) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        List<AchievementStatisticsRegionChartVO> series = new ArrayList<>();
        if (Objects.isNull(endDate)) {
            endDate = DateUtil.getLastOfDayMonth(beginDate);
        }
        for (RegionDO region : regionList) {
            StringBuilder sb = new StringBuilder();
            String startTime = DateUtil.format(beginDate);
            String endTime = DateUtil.format(endDate);
            sb.append(AchievementKeyPrefixEnum.ACHIEVEMENT_REGION_CHART.type).append(region.getId()).append("_")
                    .append(enterpriseId).append(":").append(startTime).append("_!").append(endTime).append(showCurrent)
                    .append("!:").append(achievementFormworkId).append("#!").append(achievementTypeId);
            String redisKey = sb.toString();
            String redisObject = RedisOperator.get(redisKey);
            AchievementStatisticsRegionChartVO result;
            if (StringUtils.isEmpty(redisObject)) {
                result = new AchievementStatisticsRegionChartVO();
                result.setRegionName(region.getName());
                double[] completionTarget = new double[DateUtil.getBetweenDays(endDate.getTime(), beginDate.getTime())];
                List<AchievementDetailDO> details = achievementDetailMapper.getRegionDayAmount(enterpriseId,
                        region.getFullRegionPath(),region.getId(), achievementTypeId, startTime, endTime, showCurrent, achievementFormworkId);
                ListUtils.emptyIfNull(details).forEach(t -> {
                    int i = DateUtil.getBetweenDays(t.getProduceTime().getTime(), beginDate.getTime()) - 1;
                    completionTarget[i] = t.getAchievementAmount().doubleValue();
                });
                result.setCompletionTarget(completionTarget);
                RedisOperator.setex(redisKey, JSON.toJSONString(result),
                        AchievementErrorEnum.REGION_STATISTICS_MAX.code.longValue());
            } else {
                result = JSON.parseObject(redisObject, AchievementStatisticsRegionChartVO.class);
            }
            series.add(result);
        }
        return series;
    }

    public StoreDO getStoreDO(String enterpriseId, String id) {
        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, id);
        if (Objects.isNull(storeDO)) {
            throw new ServiceException(AchievementErrorEnum.TARGET_STORE_NULL.code,
                    AchievementErrorEnum.TARGET_STORE_NULL.message);
        }
        return storeDO;
    }

    private void checkRegion(AchievementStatisticsReqVO req) {
        if (Objects.isNull(req.getRegionIds()) || req.getRegionIds().isEmpty()) {
            throw new ServiceException(AchievementErrorEnum.STATISTICS_REGION_MIN.code,
                    AchievementErrorEnum.STATISTICS_REGION_MIN.message);
        }
        if (req.getRegionIds().size() > AchievementErrorEnum.REGION_MAX.code) {
            throw new ServiceException(AchievementErrorEnum.STATISTICS_REGION_MAX.code,
                    AchievementErrorEnum.STATISTICS_REGION_MAX.message);
        }

        if (Objects.isNull(req.getEndDate())) {
            req.setBeginDate(DateUtil.getFirstOfDayMonth(req.getBeginDate()));
        }
        if (Objects.nonNull(req.getBeginDate()) && Objects.nonNull(req.getEndDate())) {
            if (req.getEndDate().getTime() < req.getBeginDate().getTime()) {
                throw new ServiceException(AchievementErrorEnum.STATISTICS_TIME_ERROR.code,
                        AchievementErrorEnum.STATISTICS_TIME_ERROR.message);
            }
        }
    }

}

