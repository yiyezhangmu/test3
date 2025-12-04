package com.coolcollege.intelligent.service.inspection.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.inspection.AiInspectionStorePeriodMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.inspection.AiInspectionStatisticsDTO;
import com.coolcollege.intelligent.model.inspection.vo.*;
import com.coolcollege.intelligent.service.inspection.AiInspectionStatisticsService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author byd
 * @date 2025-11-13 16:31
 */
@Service
public class AiInspectionStatisticsServiceImpl implements AiInspectionStatisticsService {

    @Resource
    private AiInspectionStorePeriodMapper aiInspectionStorePeriodMapper;

    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;


    @Resource
    private RegionMapper regionMapper;

    @Override
    public AiInspectionReportVO inspectionOverview(String enterpriseId, String userId, AiInspectionStatisticsDTO statisticsDTO) {
        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        List<String> regionPathList = getAuthRegionList(enterpriseId, isAdmin, userId, statisticsDTO.getRegionIdList(), statisticsDTO.getStoreIdList());
        String beginDate = DateUtils.convertTimeToString(statisticsDTO.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(statisticsDTO.getEndTime(), DateUtils.DATE_FORMAT_DAY);
        return aiInspectionStorePeriodMapper.inspectionOverview(enterpriseId,
                beginDate, endDate, statisticsDTO.getStoreIdList(), statisticsDTO.getSceneIdList(), regionPathList);
    }

    @Override
    public List<AiInspectionTendReportVO> inspectionTrend(String enterpriseId, String userId, AiInspectionStatisticsDTO statisticsDTO) {
        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        List<String> regionPathList = getAuthRegionList(enterpriseId, isAdmin, userId, statisticsDTO.getRegionIdList(), statisticsDTO.getStoreIdList());
        String beginDate = DateUtils.convertTimeToString(statisticsDTO.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(statisticsDTO.getEndTime(), DateUtils.DATE_FORMAT_DAY);
        return aiInspectionStorePeriodMapper.inspectionTrend(enterpriseId,
                beginDate, endDate, statisticsDTO.getStoreIdList(), statisticsDTO.getSceneIdList(), regionPathList);
    }

    @Override
    public List<AiInspectionProblemReportVO> sceneProblemRate(String enterpriseId, String userId, AiInspectionStatisticsDTO statisticsDTO) {
        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        List<String> regionPathList = getAuthRegionList(enterpriseId, isAdmin, userId, statisticsDTO.getRegionIdList(), statisticsDTO.getStoreIdList());
        String beginDate = DateUtils.convertTimeToString(statisticsDTO.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(statisticsDTO.getEndTime(), DateUtils.DATE_FORMAT_DAY);
        Long totalFailNum = aiInspectionStorePeriodMapper.sceneTotalProblemNum(enterpriseId,
                beginDate, endDate, statisticsDTO.getStoreIdList(), statisticsDTO.getSceneIdList(), regionPathList, statisticsDTO.getReportType());
        List<AiInspectionProblemReportVO> resultList = aiInspectionStorePeriodMapper.sceneProblemRate(enterpriseId,
                beginDate, endDate, statisticsDTO.getStoreIdList(), statisticsDTO.getSceneIdList(), regionPathList, statisticsDTO.getReportType(), totalFailNum);
        if(CollectionUtils.isNotEmpty(resultList) && resultList.size() == Constants.TEN){
            //等于10条，第十条则为前九条数据之前的占比
            AiInspectionProblemReportVO lastItem = resultList.get(resultList.size() - 1);
            //前九条数据之和
            BigDecimal totalFailRate = resultList.subList(0, resultList.size() - 2).stream()
                    .filter(Objects::nonNull)
                    .map(AiInspectionProblemReportVO::getFailRate)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            lastItem.setFailRate(new BigDecimal(Constants.ONE_HUNDRED).subtract(totalFailRate));
            lastItem.setSceneId(0L);
            lastItem.setSceneName("其他");
            lastItem.setFailNum(0L);
        }
        return resultList;
    }

    @Override
    public List<AiInspectionProblemStoreReportVO> problemStoreTop(String enterpriseId, String userId, AiInspectionStatisticsDTO statisticsDTO) {
        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        List<String> regionPathList = getAuthRegionList(enterpriseId, isAdmin, userId, statisticsDTO.getRegionIdList(), statisticsDTO.getStoreIdList());
        String beginDate = DateUtils.convertTimeToString(statisticsDTO.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(statisticsDTO.getEndTime(), DateUtils.DATE_FORMAT_DAY);
        return aiInspectionStorePeriodMapper.problemStoreTop(enterpriseId,
                beginDate, endDate, statisticsDTO.getStoreIdList(), statisticsDTO.getSceneIdList(), regionPathList, statisticsDTO.getReportType());
    }

    @Override
    public List<AiInspectionProblemTimeReportListVO> sceneProblemTimeList(String enterpriseId, String userId, AiInspectionStatisticsDTO statisticsDTO) {
        // 检查管理员权限
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);

        // 获取权限区域列表
        List<String> regionPathList = getAuthRegionList(enterpriseId, isAdmin, userId,
                statisticsDTO.getRegionIdList(), statisticsDTO.getStoreIdList());

        // 格式化时间
        String beginDate = DateUtils.convertTimeToString(statisticsDTO.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(statisticsDTO.getEndTime(), DateUtils.DATE_FORMAT_DAY);

        // 查询数据
        List<AiInspectionProblemTimeReportVO> resultList = aiInspectionStorePeriodMapper.sceneProblemTimeList(
                enterpriseId, beginDate, endDate,
                statisticsDTO.getStoreIdList(), statisticsDTO.getSceneIdList(), regionPathList);
        //不传场景，合并统计数据
        if (CollectionUtils.isEmpty(statisticsDTO.getSceneIdList()) && CollectionUtils.isNotEmpty(resultList)) {
            resultList.forEach(item -> {
                item.setSceneName("全部场景");
                item.setSceneId(0L);
            });
        }

        // 获取24个小时时间点（带:00格式）
        List<String> hourTimeList = DateUtils.getHourTimeList().stream()
                .map(hour -> hour + ":00")
                .collect(Collectors.toList());

        // 如果查询结果为空，直接返回24小时空数据
        if (CollectionUtils.isEmpty(resultList)) {
            return createEmpty24HourData(hourTimeList);
        }

        // 获取前5个场景ID（按失败数降序）
        Set<Long> sceneIdList = resultList.stream()
                .sorted(Comparator.comparing(AiInspectionProblemTimeReportVO::getFailNum).reversed())
                .map(AiInspectionProblemTimeReportVO::getSceneId)
                .distinct()
                .limit(5)
                .collect(Collectors.toSet());

        // 创建场景ID到名称的映射
        Map<Long, String> sceneIdNameMap = resultList.stream()
                .collect(Collectors.toMap(
                        AiInspectionProblemTimeReportVO::getSceneId,
                        AiInspectionProblemTimeReportVO::getSceneName,
                        (existing, replacement) -> existing
                ));

        // 只返回5个场景的数据
        resultList = resultList.stream()
                .filter(item -> sceneIdList.contains(item.getSceneId()))
                .collect(Collectors.toList());

        // 处理hourTime格式
        resultList.forEach(e -> e.setHourTime(e.getHourTime() + ":00"));

        // 按小时分组
        Map<String, List<AiInspectionProblemTimeReportVO>> hourGroupMap = resultList.stream()
                .collect(Collectors.groupingBy(AiInspectionProblemTimeReportVO::getHourTime));

        // 补全24小时数据
        return hourTimeList.stream()
                .map(hourTime -> {
                    AiInspectionProblemTimeReportListVO listVO = new AiInspectionProblemTimeReportListVO();
                    listVO.setHourTime(hourTime);

                    // 如果该小时有数据，使用实际数据；否则创建空数据
                    if (hourGroupMap.containsKey(hourTime)) {
                        listVO.setSceneTimeList(hourGroupMap.get(hourTime));
                    } else {
                        listVO.setSceneTimeList(createEmptySceneList(hourTime, sceneIdList, sceneIdNameMap));
                    }
                    return listVO;
                })
                .sorted(Comparator.comparing(AiInspectionProblemTimeReportListVO::getHourTime))
                .collect(Collectors.toList());
    }

    /**
     * 创建24小时空数据
     */
    private List<AiInspectionProblemTimeReportListVO> createEmpty24HourData(List<String> hourTimeList) {
        return hourTimeList.stream()
                .map(hourTime -> {
                    AiInspectionProblemTimeReportListVO listVO = new AiInspectionProblemTimeReportListVO();
                    listVO.setHourTime(hourTime);
                    listVO.setSceneTimeList(new ArrayList<>());  // 空列表
                    return listVO;
                })
                .sorted(Comparator.comparing(AiInspectionProblemTimeReportListVO::getHourTime))
                .collect(Collectors.toList());
    }

    /**
     * 创建空场景数据列表
     */
    private List<AiInspectionProblemTimeReportVO> createEmptySceneList(String hourTime, Set<Long> sceneIdList, Map<Long, String> sceneIdNameMap) {
        return sceneIdList.stream()
                .map(sceneId -> {
                    AiInspectionProblemTimeReportVO vo = new AiInspectionProblemTimeReportVO();
                    vo.setSceneId(sceneId);
                    vo.setSceneName(sceneIdNameMap.get(sceneId));
                    vo.setHourTime(hourTime);
                    vo.setFailNum(0L);
                    return vo;
                })
                .collect(Collectors.toList());
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
