package com.coolcollege.intelligent.service.inspection.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.inspection.dao.*;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.mapper.store.StoreSceneDAO;
import com.coolcollege.intelligent.model.ai.vo.AiModelSceneVO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.UnifyTaskLoopDateEnum;
import com.coolcollege.intelligent.model.inspection.AiInspectionStrategiesDTO;
import com.coolcollege.intelligent.model.inspection.AiInspectionStrategiesExtendInfo;
import com.coolcollege.intelligent.model.inspection.AiInspectionTimePeriodDTO;
import com.coolcollege.intelligent.model.inspection.entity.AiInspectionStoreMappingDO;
import com.coolcollege.intelligent.model.inspection.entity.AiInspectionStrategiesDO;
import com.coolcollege.intelligent.model.inspection.entity.AiInspectionTimePeriodDO;
import com.coolcollege.intelligent.model.inspection.request.AiInspectionReportDetailRequest;
import com.coolcollege.intelligent.model.inspection.request.AiInspectionReportRequest;
import com.coolcollege.intelligent.model.inspection.request.AiInspectionStoreMappingRequest;
import com.coolcollege.intelligent.model.inspection.request.AiInspectionStrategiesRequest;
import com.coolcollege.intelligent.model.inspection.vo.*;
import com.coolcollege.intelligent.model.pictureInspection.StoreSceneVO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.service.inspection.AiInspectionStrategiesService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * AI巡检策略表服务实现类
 * </p>
 *
 * @author zhangchenbiao
 * @since 2025/9/25
 */
@Service
public class AiInspectionStrategiesServiceImpl implements AiInspectionStrategiesService {

    @Resource
    private AiInspectionStrategiesDAO aiInspectionStrategiesDAO;

    @Resource
    private AiInspectionStoreMappingDAO aiInspectionStoreMappingDAO;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private StoreSceneDAO storeSceneDAO;

    @Resource
    private AiInspectionTimePeriodDAO aiInspectionTimePeriodDAO;

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Resource
    private AiInspectionStorePictureDAO aiInspectionStorePictureDAO;

    @Resource
    private AiInspectionStorePeriodDAO aiInspectionStorePeriodDAO;


    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;

    @Resource
    private SysRoleService sysRoleService;

    @Override
    public PageInfo<AiInspectionStrategiesVO> selectList(String enterpriseId, AiInspectionStrategiesDTO query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<AiInspectionStrategiesDO> list = aiInspectionStrategiesDAO.selectByQuery(query, enterpriseId);
        PageInfo pageInfo = new PageInfo(list);
        if (CollectionUtils.isEmpty(list)) {
            return pageInfo;
        }
        List<Long> inspectionIdList = list.stream().map(AiInspectionStrategiesDO::getId).collect(Collectors.toList());

        List<String> userIdList = list.stream()
                .flatMap(strategy -> Stream.of(
                        strategy.getCreateUserId(),
                        strategy.getUpdateUserId()
                ))
                .filter(Objects::nonNull)
                .distinct()  // 去重
                .collect(Collectors.toList());

        List<EnterpriseUserDO> enterpriseUserList = enterpriseUserMapper.selectUsersByUserIds(enterpriseId, userIdList);
        Map<String, String> userNameMap = enterpriseUserList.stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getName));


        List<AiInspectionStoreMappingDO> storeMappingList = aiInspectionStoreMappingDAO.selectByInspectionIdList(inspectionIdList, enterpriseId);

        List<String> regionIdList = storeMappingList.stream().filter(x -> UnifyTaskConstant.StoreType.REGION.equals(x.getType())).map(AiInspectionStoreMappingDO::getMappingId).collect(Collectors.toList());

        List<String> storeIdList = storeMappingList.stream()
                .filter(x -> UnifyTaskConstant.StoreType.STORE.equals(x.getType()))
                .map(AiInspectionStoreMappingDO::getMappingId)
                .collect(Collectors.toList());

        // 查询区域名称
        Map<String, String> regionNameMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            regionNameMap = regionMapper.getRegionByRegionIds(enterpriseId, regionIdList).stream()
                    .filter(a -> a.getRegionId() != null && a.getName() != null)
                    .collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getName, (a, b) -> a));
        }

        // 查询分组名称
        Map<String, String> storeIdNameMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(storeIdList)) {
            List<StoreDO> storeDOList = storeMapper.getByStoreIdList(enterpriseId, storeIdList);
            storeIdNameMap = storeDOList.stream()
                    .filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                    .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));
        }

        List<AiInspectionStoreMappingVO> storeMappingVOList = new ArrayList<>();
        Map<String, String> finalRegionNameMap = regionNameMap;
        Map<String, String> finalStoreIdNameMap = storeIdNameMap;
        storeMappingList.forEach(x -> {
            AiInspectionStoreMappingVO storeMappingVO = AiInspectionStoreMappingVO.builder()
                    .id(x.getId())
                    .inspectionId(x.getInspectionId())
                    .mappingId(x.getMappingId())
                    .type(x.getType())
                    .build();
            if (UnifyTaskConstant.StoreType.REGION.equals(x.getType())) {
                storeMappingVO.setMappingName(finalRegionNameMap.get(x.getMappingId()));
            }
            if (UnifyTaskConstant.StoreType.STORE.equals(x.getType())) {
                storeMappingVO.setMappingName(finalStoreIdNameMap.get(x.getMappingId()));
            }
            storeMappingVOList.add(storeMappingVO);
        });
        Map<Long, List<AiInspectionStoreMappingVO>> storeMappingMap = storeMappingVOList.stream().collect(Collectors.groupingBy(AiInspectionStoreMappingVO::getInspectionId));

        Map<Long, List<String>> storeSceneIdList = list.stream()
                .collect(Collectors.toMap(
                        AiInspectionStrategiesDO::getId,
                        item -> Arrays.asList(item.getTags().split(Constants.COMMA))
                ));

        Set<String> allUniqueTags = list.stream()
                .map(AiInspectionStrategiesDO::getTags)
                .filter(Objects::nonNull)
                .flatMap(tags -> Arrays.stream(tags.split(Constants.COMMA)))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toSet());

        Map<String, String> storeSceneNameMap = storeSceneDAO.getStoreSceneNameMap(enterpriseId, new ArrayList(allUniqueTags));
        storeSceneNameMap = storeSceneNameMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> String.valueOf(entry.getKey()),
                        Map.Entry::getValue
                ));

        Map<String, String> finalStoreSceneNameMap = storeSceneNameMap;
        Map<Long, List<StoreSceneVO>> storeSceneMap = storeSceneIdList.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(Objects::nonNull)  // 过滤null值
                                .map(String::trim)        // 去除前后空格
                                .filter(tag -> !tag.isEmpty())  // 过滤空字符串
                                .flatMap(tag -> {
                                    try {
                                        Long sceneId = Long.valueOf(tag);
                                        String sceneName = finalStoreSceneNameMap.get(tag);

                                        return Stream.of(StoreSceneVO.builder()
                                                .id(sceneId)
                                                .name(sceneName)
                                                .build());
                                    } catch (NumberFormatException e) {
                                        // 可以添加日志记录无效的tag
                                        return Stream.empty();
                                    }
                                })
                                .collect(Collectors.toList())
                ));

        List<AiInspectionTimePeriodDO> timePeriodList = aiInspectionTimePeriodDAO.selectByInspectionIdList(inspectionIdList, enterpriseId);


        Map<Long, List<AiInspectionTimePeriodVO>> timePeriodMap = timePeriodList.stream()
                .map(x -> AiInspectionTimePeriodVO.builder()
                        .id(x.getId())
                        .inspectionId(x.getInspectionId())
                        .beginTime(x.getBeginTime())
                        .endTime(x.getEndTime())
                        .period(x.getPeriod())
                        .build())
                .collect(Collectors.groupingBy(AiInspectionTimePeriodVO::getInspectionId));

        List<AiInspectionStrategiesVO> resultList = list.stream()
                .map(x -> AiInspectionStrategiesVO.builder()
                        .id(x.getId())
                        .sceneId(x.getSceneId())
                        .sceneName(x.getSceneName())
                        .description(x.getDescription())
                        .status(x.getStatus())
                        .runDate(x.getRunDate())
                        .tags(x.getTags())
                        .createTime(x.getCreateTime())
                        .createUserId(x.getCreateUserId())
                        .createUserName(userNameMap.get(x.getCreateUserId()))
                        .updateUserName(userNameMap.get(x.getUpdateUserId()))
                        .updateTime(x.getUpdateTime())
                        .updateUserId(x.getUpdateUserId())
                        .storeMappingList(storeMappingMap.get(x.getId()))
                        .timePeriodList(timePeriodMap.get(x.getId()))
                        .storeSceneList(storeSceneMap.get(x.getId()))
                        .build()
                ).collect(Collectors.toList());
        pageInfo.setList(resultList);
        return pageInfo;
    }

    /**
     * 添加AI巡检策略
     *
     * @param enterpriseId 企业ID
     * @param strategyVO   策略信息
     * @return 添加的策略ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long add(String enterpriseId, AiInspectionStrategiesRequest strategyVO, String userId, List<AiModelSceneVO> aiModelSceneList) {


        aiModelSceneList.forEach(sceneVO -> {
            // 保存主策略信息
            AiInspectionStrategiesDO strategyDO = AiInspectionStrategiesDO.builder()
                    .sceneId(sceneVO.getSceneId())
                    .sceneName(sceneVO.getSceneName())
                    .description(strategyVO.getDescription())
                    .status(1)
                    .runDate(strategyVO.getRunDate())
                    .tags(strategyVO.getTags())
                    .createUserId(userId)
                    .createTime(new Date())
                    .updateUserId(userId)
                    .updateTime(new Date())
                    .build();
            if(strategyVO.getExtendInfoConfig() != null){
                strategyDO.setExtendInfo(JSONObject.toJSONString(strategyVO.getExtendInfoConfig()));
            }
            aiInspectionStrategiesDAO.insertSelective(strategyDO, enterpriseId);
            Long strategyId = strategyDO.getId();

            // 保存门店映射信息
            if (strategyVO.getStoreMappingList() != null && !strategyVO.getStoreMappingList().isEmpty()) {
                for (AiInspectionStoreMappingRequest mappingVO : strategyVO.getStoreMappingList()) {
                    AiInspectionStoreMappingDO mappingDO = AiInspectionStoreMappingDO.builder()
                            .inspectionId(strategyId)
                            .mappingId(mappingVO.getMappingId())
                            .type(mappingVO.getType())
                            .build();
                    aiInspectionStoreMappingDAO.insertSelective(mappingDO, enterpriseId);
                }
            }

            // 保存时间周期信息
            if (strategyVO.getTimePeriodList() != null && !strategyVO.getTimePeriodList().isEmpty()) {
                for (AiInspectionTimePeriodDTO periodVO : strategyVO.getTimePeriodList()) {
                    AiInspectionTimePeriodDO periodDO = AiInspectionTimePeriodDO.builder()
                            .inspectionId(strategyId)
                            .beginTime(periodVO.getBeginTime())
                            .endTime(periodVO.getEndTime())
                            .period(periodVO.getPeriod())
                            .createTime(new Date())
                            .createUserId(userId)
                            .updateTime(new Date())
                            .updateUserId(userId)
                            .build();
                    aiInspectionTimePeriodDAO.insertSelective(periodDO, enterpriseId);
                }
            }
        });

        return null;
    }

    /**
     * 更新AI巡检策略
     *
     * @param enterpriseId 企业ID
     * @param strategyVO   策略信息
     * @return 是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(String enterpriseId, AiInspectionStrategiesRequest strategyVO, String userId) {
        // 更新主策略信息
        AiInspectionStrategiesDO strategyDO = AiInspectionStrategiesDO.builder()
                .id(strategyVO.getId())
                .sceneId(strategyVO.getSceneId())
                .sceneName(strategyVO.getSceneName())
                .description(strategyVO.getDescription())
                .runDate(strategyVO.getRunDate())
                .tags(strategyVO.getTags())
                .updateUserId(userId)
                .updateTime(new Date())
                .build();
        if(strategyVO.getExtendInfoConfig() != null){
            strategyDO.setExtendInfo(JSONObject.toJSONString(strategyVO.getExtendInfoConfig()));
        }
        int result = aiInspectionStrategiesDAO.updateByPrimaryKeySelective(strategyDO, enterpriseId);

        Long strategyId = strategyVO.getId();

        // 先删除原有的门店映射信息，再重新添加
        aiInspectionStoreMappingDAO.deleteByInspectionId(strategyId, enterpriseId);
        if (strategyVO.getStoreMappingList() != null && !strategyVO.getStoreMappingList().isEmpty()) {
            for (AiInspectionStoreMappingRequest mappingVO : strategyVO.getStoreMappingList()) {
                AiInspectionStoreMappingDO mappingDO = AiInspectionStoreMappingDO.builder()
                        .inspectionId(strategyId)
                        .mappingId(mappingVO.getMappingId())
                        .type(mappingVO.getType())
                        .build();
                aiInspectionStoreMappingDAO.insertSelective(mappingDO, enterpriseId);
            }
        }

        // 先删除原有的时间周期信息，再重新添加
        aiInspectionTimePeriodDAO.deleteByInspectionId(strategyId, enterpriseId);
        if (strategyVO.getTimePeriodList() != null && !strategyVO.getTimePeriodList().isEmpty()) {
            for (AiInspectionTimePeriodDTO periodVO : strategyVO.getTimePeriodList()) {
                AiInspectionTimePeriodDO periodDO = AiInspectionTimePeriodDO.builder()
                        .inspectionId(strategyId)
                        .beginTime(periodVO.getBeginTime())
                        .endTime(periodVO.getEndTime())
                        .period(periodVO.getPeriod())
                        .updateTime(new Date())
                        .updateUserId(userId)
                        .build();
                aiInspectionTimePeriodDAO.insertSelective(periodDO, enterpriseId);
            }
        }

        return result > 0;
    }

    /**
     * 启用AI巡检策略
     *
     * @param enterpriseId 企业ID
     * @param id           策略ID
     * @param updateUserId 更新用户ID
     * @return 是否启用成功
     */
    @Override
    public boolean enable(String enterpriseId, Long id, String updateUserId) {
        AiInspectionStrategiesDO strategyDO = AiInspectionStrategiesDO.builder()
                .id(id)
                .status(1)
                .updateUserId(updateUserId)
                .updateTime(new Date())
                .build();
        return aiInspectionStrategiesDAO.updateByPrimaryKeySelective(strategyDO, enterpriseId) > 0;
    }

    /**
     * 禁用AI巡检策略
     *
     * @param enterpriseId 企业ID
     * @param id           策略ID
     * @param updateUserId 更新用户ID
     * @return 是否禁用成功
     */
    @Override
    public boolean disable(String enterpriseId, Long id, String updateUserId) {
        AiInspectionStrategiesDO strategyDO = AiInspectionStrategiesDO.builder()
                .id(id)
                .status(0)
                .updateUserId(updateUserId)
                .updateTime(new Date())
                .build();
        return aiInspectionStrategiesDAO.updateByPrimaryKeySelective(strategyDO, enterpriseId) > 0;
    }

    /**
     * 删除AI巡检策略
     *
     * @param enterpriseId 企业ID
     * @param id           策略ID
     * @param updateUserId 更新用户ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(String enterpriseId, Long id, String updateUserId) {

        // 再删除关联的时间周期信息
        aiInspectionTimePeriodDAO.removeByInspectionId(id, enterpriseId);

        // 最后删除主策略信息
        AiInspectionStrategiesDO strategyDO = AiInspectionStrategiesDO.builder()
                .id(id)
                .updateUserId(updateUserId)
                .deleted(Boolean.TRUE)
                .updateTime(new Date())
                .build();
        return aiInspectionStrategiesDAO.updateByPrimaryKeySelective(strategyDO, enterpriseId) > 0;
    }

    @Override
    public PageInfo<AiInspectionStatisticsVO> dailyReportList(String enterpriseId, AiInspectionReportRequest query, String userId) {

        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        List<String> regionPathList = getAuthRegionList(enterpriseId, isAdmin, userId, query.getRegionIdList(), query.getStoreIdList());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathList) && CollectionUtils.isEmpty(query.getStoreIdList())){
            return new PageInfo<>();
        }

        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        if (StringUtils.isBlank(query.getReportType())) {
            query.setReportType(UnifyTaskLoopDateEnum.DAY.getCode());
        }
        List<AiInspectionStatisticsVO> statisticsList = aiInspectionStorePeriodDAO.selectStatisticsByDateRange(
                enterpriseId,
                query.getSceneId(),
                query.getStoreIdList(),
                DateUtils.convertTimeToString(query.getBeginTime(), DateUtils.DATE_FORMAT_DAY),
                DateUtils.convertTimeToString(query.getEndTime(), DateUtils.DATE_FORMAT_DAY),
                query.getInspectionResult(),
                query.getReportType(),
                regionPathList);

        List<String> captureDateList = statisticsList.stream().map(e -> DateUtil.format(e.getCaptureDate(), DateUtils.DATE_FORMAT_DAY)).collect(Collectors.toList());
        List<String> storeIdList = statisticsList.stream().map(AiInspectionStatisticsVO::getStoreId).collect(Collectors.toList());

        List<AiInspectionPhotoVO> imageList = aiInspectionStorePictureDAO.selectFailImageByDateRange(enterpriseId, null, storeIdList, captureDateList, query.getReportType());
        Map<String, String> imageMap = imageList.stream()
                .collect(Collectors.toMap(
                        e -> e.getStoreId() + "_" + (UnifyTaskLoopDateEnum.DAY.getCode().equals(query.getReportType()) ?
                                DateUtil.format(e.getCaptureDate(), DateUtils.DATE_FORMAT_DAY) : DateUtil.format(e.getWeekDay(), DateUtils.DATE_FORMAT_DAY)),
                        AiInspectionPhotoVO::getPicture,
                        (existing, replacement) -> existing
                ));
        statisticsList.forEach(e -> {
            e.setFailPic(imageMap.get(e.getStoreId() + "_" + (UnifyTaskLoopDateEnum.DAY.getCode().equals(query.getReportType()) ?
                    DateUtil.format(e.getCaptureDate(), DateUtils.DATE_FORMAT_DAY) : DateUtil.format(e.getWeekDay(), DateUtils.DATE_FORMAT_DAY))));
            e.setWeekEndDay(DateUtil.plusDays(e.getWeekDay(), 6));
            e.setReportType(query.getReportType());
            e.setSceneId(query.getSceneId());
            e.setTotalValidInspectionCount(e.getPassNum() + e.getFailNum());
            e.setPassRate(calculatePassRate(e.getPassNum(), e.getTotalValidInspectionCount()));
        });

        return new PageInfo<>(statisticsList);
    }

    public static BigDecimal calculatePassRate(Long passNum, Long totalCount) {
        if (totalCount == null || passNum == null || totalCount == 0 || passNum == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(passNum)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);
    }

    @Override
    public AiInspectionStatisticsTotalVO dailyReportCount(String enterpriseId, AiInspectionReportRequest query, String userId) {
        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        List<String> regionPathList = getAuthRegionList(enterpriseId, isAdmin, userId, query.getRegionIdList(), query.getStoreIdList());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathList) && CollectionUtils.isEmpty(query.getStoreIdList())){
            return new AiInspectionStatisticsTotalVO();
        }
        AiInspectionStatisticsTotalVO statisticsTotalVO = aiInspectionStorePeriodDAO.dailyReportCount(
                enterpriseId,
                query.getSceneId(),
                query.getStoreIdList(),
                DateUtils.convertTimeToString(query.getBeginTime(), DateUtils.DATE_FORMAT_DAY),
                DateUtils.convertTimeToString(query.getEndTime(), DateUtils.DATE_FORMAT_DAY),
                query.getInspectionResult(), regionPathList);

        AiInspectionStatisticsTotalVO problemTotalVO = aiInspectionStorePeriodDAO.problemTop(
                enterpriseId,
                query.getSceneId(),
                query.getStoreIdList(),
                DateUtils.convertTimeToString(query.getBeginTime(), DateUtils.DATE_FORMAT_DAY),
                DateUtils.convertTimeToString(query.getEndTime(), DateUtils.DATE_FORMAT_DAY),
                query.getInspectionResult(), regionPathList);
        if (problemTotalVO != null) {
            statisticsTotalVO.setProblemTop(problemTotalVO.getProblemTop());
            statisticsTotalVO.setProblemTotalNum(problemTotalVO.getProblemTotalNum());
        }
        return statisticsTotalVO;
    }

    @Override
    public AiInspectionStatisticsReportDetailVO dailyReportDetail(String enterpriseId, AiInspectionReportDetailRequest query) {

        Date reportEndTime = new Date(query.getReportDate());
        if (UnifyTaskLoopDateEnum.WEEK.getCode().equals(query.getReportType())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(query.getReportDate()));
            calendar.add(Calendar.DAY_OF_MONTH, 6);
            reportEndTime = calendar.getTime();
        }

        AiInspectionStatisticsTotalVO statisticsTotalVO = aiInspectionStorePeriodDAO.dailyReportCount(
                enterpriseId,
                query.getSceneId(),
                Collections.singletonList(query.getStoreId()),
                DateUtils.convertTimeToString(query.getReportDate(), DateUtils.DATE_FORMAT_DAY),
                DateUtils.convertTimeToString(reportEndTime.getTime(), DateUtils.DATE_FORMAT_DAY),
                null, null);

        AiInspectionStatisticsTotalVO problemTotalVO = aiInspectionStorePeriodDAO.problemTop(
                enterpriseId,
                query.getSceneId(),
                Collections.singletonList(query.getStoreId()),
                DateUtils.convertTimeToString(query.getReportDate(), DateUtils.DATE_FORMAT_DAY),
                DateUtils.convertTimeToString(reportEndTime.getTime(), DateUtils.DATE_FORMAT_DAY),
                null, null);

        AiInspectionStatisticsReportDetailVO reportDetailVO = new AiInspectionStatisticsReportDetailVO();
        reportDetailVO.setStoreId(query.getStoreId());
        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, query.getStoreId());
        reportDetailVO.setStoreName(storeDO.getStoreName());
        reportDetailVO.setReportTime(new Date(query.getReportDate()));
        reportDetailVO.setFailNum(statisticsTotalVO.getFailNum());
        reportDetailVO.setPassNum(statisticsTotalVO.getPassNum());
        reportDetailVO.setTotalValidInspectionCount(statisticsTotalVO.getPassNum() + statisticsTotalVO.getFailNum());
        reportDetailVO.setPatrolTotalNum(statisticsTotalVO.getPatrolTotalNum());
        // 安全地计算比率（小数形式）
        Long totalValidInspectionCount = reportDetailVO.getTotalValidInspectionCount();

        // 处理除零情况
        if (totalValidInspectionCount == 0) {
            reportDetailVO.setPassRate(BigDecimal.ZERO);
            reportDetailVO.setFailRate(BigDecimal.ZERO);
        } else {
            // 1. 计算合格率 (passNum / totalNum) * 100
            BigDecimal passRate = new BigDecimal(statisticsTotalVO.getPassNum())
                    .divide(new BigDecimal(totalValidInspectionCount), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
            reportDetailVO.setPassRate(passRate);
            // 2. 计算不合格率 (failNum / totalNum) * 100
            BigDecimal failRate = new BigDecimal(statisticsTotalVO.getFailNum())
                    .divide(new BigDecimal(totalValidInspectionCount), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
            reportDetailVO.setFailRate(failRate);
        }
        if (problemTotalVO != null) {
            reportDetailVO.setProblemTop(problemTotalVO.getProblemTop());
            reportDetailVO.setProblemTotalNum(problemTotalVO.getProblemTotalNum());
        }

        reportDetailVO.setReportBeginTime(new Date(query.getReportDate()));
        reportDetailVO.setReportEndTime(reportEndTime);
        reportDetailVO.setSceneList(aiInspectionStorePeriodDAO.sceneCountList(
                enterpriseId,
                query.getSceneId(),
                query.getStoreId(),
                DateUtils.convertTimeToString(query.getReportDate(), DateUtils.DATE_FORMAT_DAY),
                DateUtils.convertTimeToString(reportEndTime.getTime(), DateUtils.DATE_FORMAT_DAY),
                null));

        if(CollectionUtils.isNotEmpty(reportDetailVO.getSceneList())){
            reportDetailVO.getSceneList().forEach(scene -> {
                scene.setTotalValidInspectionCount(scene.getPassNum() + scene.getFailNum());
            });
        }

        reportDetailVO.setProblemList(aiInspectionStorePictureDAO.failPicCountList(
                enterpriseId,
                query.getSceneId(),
                query.getStoreId(),
                DateUtils.convertTimeToString(query.getReportDate(), DateUtils.DATE_FORMAT_DAY),
                DateUtils.convertTimeToString(reportEndTime.getTime(), DateUtils.DATE_FORMAT_DAY)));
        Set<Long> allUniqueTags = ListUtils.emptyIfNull(reportDetailVO.getProblemList()).stream().map(AiInspectionStatisticsProblemPicVO::getStoreSceneId).collect(Collectors.toSet());
        Map<Long, String> storeSceneNameMap = storeSceneDAO.getStoreSceneNameMap(enterpriseId, new ArrayList(allUniqueTags));
        ListUtils.emptyIfNull(reportDetailVO.getProblemList()).forEach(problem -> problem.setStoreSceneName(storeSceneNameMap.get(problem.getStoreSceneId())));
        return reportDetailVO;
    }

    @Override
    public PageInfo<AiInspectionStatisticsPicListVO> imageList(String enterpriseId, AiInspectionReportRequest query, String userId) {
        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        List<String> regionPathList = getAuthRegionList(enterpriseId, isAdmin, userId, query.getRegionIdList(), query.getStoreIdList());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathList) && CollectionUtils.isEmpty(query.getStoreIdList())){
            return new PageInfo<>();
        }
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<AiInspectionStatisticsPicListVO> statisticsList = aiInspectionStorePictureDAO.selectStatisticsImageByDateRange(
                enterpriseId,
                query.getSceneId(),
                query.getStoreIdList(),
                DateUtils.convertTimeToString(query.getBeginTime(), DateUtils.DATE_FORMAT_DAY),
                DateUtils.convertTimeToString(query.getEndTime(), DateUtils.DATE_FORMAT_DAY),
                query.getInspectionResult(),
                regionPathList);
        PageInfo<AiInspectionStatisticsPicListVO> pageInfo = new PageInfo<>(statisticsList);
        List<String> captureDateList = statisticsList.stream().map(e -> DateUtil.format(e.getCaptureDate(), DateUtils.DATE_FORMAT_DAY)).collect(Collectors.toList());
        List<String> storeIdList = statisticsList.stream().map(AiInspectionStatisticsPicListVO::getStoreId).collect(Collectors.toList());

        List<Long> sceneIdList = statisticsList.stream().map(AiInspectionStatisticsPicListVO::getSceneId).collect(Collectors.toList());
        List<AiInspectionStatisticsPicDetailVO> imageList = aiInspectionStorePictureDAO.imageList(enterpriseId, sceneIdList, storeIdList, captureDateList, query.getInspectionResult());
        Set<Long> allUniqueTags = imageList.stream().map(AiInspectionStatisticsPicDetailVO::getStoreSceneId).collect(Collectors.toSet());
        Map<Long, String> storeSceneNameMap = storeSceneDAO.getStoreSceneNameMap(enterpriseId, new ArrayList(allUniqueTags));
        imageList.forEach(e ->
                e.setStoreSceneName(storeSceneNameMap.get(e.getStoreSceneId()))
        );
        Map<String, List<AiInspectionStatisticsPicDetailVO>> imageListMap = imageList.stream().collect(Collectors.groupingBy(e -> DateUtil.format(e.getCaptureTime(), DateUtils.DATE_FORMAT_DAY) +
                "_" + e.getStoreId() + "_" +  e.getSceneId()));
        statisticsList.forEach(e ->
                e.setImageList(imageListMap.get(DateUtil.format(e.getCaptureDate(), DateUtils.DATE_FORMAT_DAY)+ "_"  + e.getStoreId() + "_"  + e.getSceneId()))
        );
        return pageInfo;
    }

    /**
     * 获取AI巡检策略详情
     *
     * @param enterpriseId 企业ID
     * @param id           策略ID
     * @return 策略详情
     */
    @Override
    public AiInspectionStrategiesVO getDetail(String enterpriseId, Long id) {
        // 查询主策略信息
        AiInspectionStrategiesDO strategyDO = aiInspectionStrategiesDAO.selectByPrimaryKey(id, enterpriseId);
        if (strategyDO == null) {
            return null;
        }

        // 查询用户信息
        List<String> userIdList = Arrays.asList(strategyDO.getCreateUserId(), strategyDO.getUpdateUserId());
        List<EnterpriseUserDO> enterpriseUserList = enterpriseUserMapper.selectUsersByUserIds(enterpriseId, userIdList);
        Map<String, String> userNameMap = enterpriseUserList.stream()
                .collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getName));

        // 查询门店映射信息
        List<AiInspectionStoreMappingDO> storeMappingList = aiInspectionStoreMappingDAO.selectByInspectionIdList(Arrays.asList(id), enterpriseId);

        // 分别获取区域和分组ID列表
        List<String> regionIdList = storeMappingList.stream()
                .filter(x -> UnifyTaskConstant.StoreType.REGION.equals(x.getType()))
                .map(AiInspectionStoreMappingDO::getMappingId)
                .collect(Collectors.toList());

        List<String> storeIdList = storeMappingList.stream()
                .filter(x -> UnifyTaskConstant.StoreType.STORE.equals(x.getType()))
                .map(AiInspectionStoreMappingDO::getMappingId)
                .collect(Collectors.toList());

        // 查询区域名称
        Map<String, String> regionNameMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            regionNameMap = regionMapper.getRegionByRegionIds(enterpriseId, regionIdList).stream()
                    .filter(a -> a.getRegionId() != null && a.getName() != null)
                    .collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getName, (a, b) -> a));
        }

        // 查询分组名称
        Map<String, String> storeIdNameMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(storeIdList)) {
            List<StoreDO> storeDOList = storeMapper.getByStoreIdList(enterpriseId, storeIdList);
            storeIdNameMap = storeDOList.stream()
                    .filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                    .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));
        }

        // 构建门店映射VO列表
        List<AiInspectionStoreMappingVO> storeMappingVOList = new ArrayList<>();
        Map<String, String> finalRegionNameMap = regionNameMap;
        Map<String, String> finalStoreNameMap = storeIdNameMap;
        storeMappingList.forEach(x -> {
            AiInspectionStoreMappingVO storeMappingVO = AiInspectionStoreMappingVO.builder()
                    .id(x.getId())
                    .inspectionId(x.getInspectionId())
                    .mappingId(x.getMappingId())
                    .type(x.getType())
                    .build();
            if (UnifyTaskConstant.StoreType.REGION.equals(x.getType())) {
                storeMappingVO.setMappingName(finalRegionNameMap.get(x.getMappingId()));
            }
            if (UnifyTaskConstant.StoreType.STORE.equals(x.getType())) {
                storeMappingVO.setMappingName(finalStoreNameMap.get(x.getMappingId()));
            }
            storeMappingVOList.add(storeMappingVO);
        });

        // 处理场景标签
        Set<String> storeSceneIdList = new HashSet<>();
        if (StringUtils.isNotBlank(strategyDO.getTags())) {
            storeSceneIdList = new HashSet<>(Arrays.asList(strategyDO.getTags().split(Constants.COMMA)));
        }

        Map<String, String> storeSceneNameMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(storeSceneIdList)) {
            storeSceneNameMap = storeSceneDAO.getStoreSceneNameMap(enterpriseId, new ArrayList(storeSceneIdList));

            storeSceneNameMap = storeSceneNameMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> String.valueOf(entry.getKey()),
                            Map.Entry::getValue
                    ));
        }

        Map<String, String> finalStoreSceneNameMap = storeSceneNameMap;
        List<StoreSceneVO> storeSceneList = storeSceneIdList.stream()
                .map(tag -> {
                    String sceneName = finalStoreSceneNameMap.get(tag);
                    Long sceneId = Long.parseLong(tag);
                    return StoreSceneVO.builder()
                            .id(sceneId)
                            .name(sceneName)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 查询时间周期信息
        List<AiInspectionTimePeriodDO> timePeriodList = aiInspectionTimePeriodDAO.selectByInspectionIdList(Collections.singletonList(id), enterpriseId);
        List<AiInspectionTimePeriodVO> timePeriodVOList = timePeriodList.stream()
                .map(x -> AiInspectionTimePeriodVO.builder()
                        .id(x.getId())
                        .inspectionId(x.getInspectionId())
                        .beginTime(x.getBeginTime())
                        .endTime(x.getEndTime())
                        .period(x.getPeriod())
                        .build())
                .collect(Collectors.toList());
        AiInspectionStrategiesExtendInfo extendInfoConfig = null;
        if (StringUtils.isNotBlank(strategyDO.getExtendInfo())) {
            extendInfoConfig = JSONObject.parseObject(strategyDO.getExtendInfo(), AiInspectionStrategiesExtendInfo.class);
        }else {
            extendInfoConfig = new AiInspectionStrategiesExtendInfo();
        }
        // 构建返回VO
        return AiInspectionStrategiesVO.builder()
                .id(strategyDO.getId())
                .sceneId(strategyDO.getSceneId())
                .sceneName(strategyDO.getSceneName())
                .description(strategyDO.getDescription())
                .status(strategyDO.getStatus())
                .runDate(strategyDO.getRunDate())
                .tags(strategyDO.getTags())
                .createTime(strategyDO.getCreateTime())
                .createUserId(strategyDO.getCreateUserId())
                .createUserName(userNameMap.get(strategyDO.getCreateUserId()))
                .updateUserName(userNameMap.get(strategyDO.getUpdateUserId()))
                .updateTime(strategyDO.getUpdateTime())
                .updateUserId(strategyDO.getUpdateUserId())
                .storeMappingList(storeMappingVOList)
                .timePeriodList(timePeriodVOList)
                .storeSceneList(storeSceneList)
                .extendInfoConfig(extendInfoConfig)
                .build();
    }


    private List<String> getAuthRegionList(String enterpriseId, Boolean isAdmin, String userId, List<String> regionIdList, List<String> storeIdList){
        if (!isAdmin && CollectionUtils.isEmpty(regionIdList) && CollectionUtils.isEmpty(storeIdList)) {
            List<UserAuthMappingDO> userAuthMappingList = userAuthMappingMapper.listUserAuthMappingByUserId(enterpriseId, userId);
            if (CollectionUtils.isNotEmpty(userAuthMappingList)) {
                regionIdList = userAuthMappingList.stream().map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
            }
        }
        List<String> regionPathList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(regionIdList)){
            regionPathList = regionMapper.getFullPathByIds(enterpriseId, regionIdList);
        }
        return regionPathList;
    }
}