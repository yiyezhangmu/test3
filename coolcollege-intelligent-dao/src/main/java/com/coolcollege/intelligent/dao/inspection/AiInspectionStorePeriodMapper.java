package com.coolcollege.intelligent.dao.inspection;

import com.coolcollege.intelligent.model.inspection.AiInspectionStatisticsDTO;
import com.coolcollege.intelligent.model.inspection.entity.AiInspectionStorePeriodDO;
import com.coolcollege.intelligent.model.inspection.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-10-14 03:41
 */
public interface AiInspectionStorePeriodMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-10-14 03:41
     */
    int insertSelective(@Param("record") AiInspectionStorePeriodDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-10-14 03:41
     */
    int updateByPrimaryKeySelective(@Param("record") AiInspectionStorePeriodDO record, @Param("enterpriseId") String enterpriseId);

    List<AiInspectionStatisticsVO> selectStatisticsByDateRange(
            @Param("enterpriseId") String enterpriseId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("storeIdList") List<String> storeIdList,
            @Param("sceneId") Long sceneId,
            @Param("inspectionResult") String inspectionResult,
            @Param("reportType") String reportType,
            @Param("regionPathList") List<String> regionPathList);

    AiInspectionStatisticsTotalVO dailyReportCount(
            @Param("enterpriseId") String enterpriseId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("storeIdList") List<String> storeIdList,
            @Param("sceneId") Long sceneId,
            @Param("inspectionResult") String inspectionResult,
            @Param("regionPathList") List<String> regionPathList);

    AiInspectionStatisticsTotalVO problemTop(
            @Param("enterpriseId") String enterpriseId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("storeIdList") List<String> storeIdList,
            @Param("sceneId") Long sceneId,
            @Param("inspectionResult") String inspectionResult,
            @Param("regionPathList") List<String> regionPathList);

    List<AiInspectionStatisticsSceneVO> sceneCountList(
            @Param("enterpriseId") String enterpriseId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("storeId") String storeId,
            @Param("sceneId") Long sceneId,
            @Param("inspectionResult") String inspectionResult);

    List<AiInspectionStatisticsPicListVO> selectStatisticsImageByDateRange(
            @Param("enterpriseId") String enterpriseId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("storeIdList") List<String> storeIdList,
            @Param("sceneId") Long sceneId,
            @Param("inspectionResult") String inspectionResult,
            @Param("regionPathList") List<String> regionPathList);

    AiInspectionStorePeriodDO selectByPrimaryKey(@Param("enterpriseId") String enterpriseId,@Param("id") Long id);

    AiInspectionReportVO inspectionOverview(@Param("enterpriseId") String enterpriseId,
                                            @Param("startDate") String startDate,
                                            @Param("endDate") String endDate,
                                            @Param("storeIdList") List<String> storeIdList,
                                            @Param("sceneIdList") List<Long> sceneIdList,
                                            @Param("regionPathList") List<String> regionPathList);

    List<AiInspectionTendReportVO> inspectionTrend(@Param("enterpriseId") String enterpriseId,
                                            @Param("startDate") String startDate,
                                            @Param("endDate") String endDate,
                                            @Param("storeIdList") List<String> storeIdList,
                                            @Param("sceneIdList") List<Long> sceneIdList,
                                            @Param("regionPathList") List<String> regionPathList);

    List<AiInspectionProblemReportVO> sceneProblemRate(@Param("enterpriseId") String enterpriseId,
                                                   @Param("startDate") String startDate,
                                                   @Param("endDate") String endDate,
                                                   @Param("storeIdList") List<String> storeIdList,
                                                   @Param("sceneIdList") List<Long> sceneIdList,
                                                   @Param("regionPathList") List<String> regionPathList,
                                                   @Param("reportType") String reportType,
                                                   @Param("totalFailNum") Long totalFailNum);

    Long sceneTotalProblemNum(@Param("enterpriseId") String enterpriseId,
                                                       @Param("startDate") String startDate,
                                                       @Param("endDate") String endDate,
                                                       @Param("storeIdList") List<String> storeIdList,
                                                       @Param("sceneIdList") List<Long> sceneIdList,
                                                       @Param("regionPathList") List<String> regionPathList,
                                                       @Param("reportType") String reportType) ;


    List<AiInspectionProblemStoreReportVO> problemStoreTop(@Param("enterpriseId") String enterpriseId,
                                                       @Param("startDate") String startDate,
                                                       @Param("endDate") String endDate,
                                                       @Param("storeIdList") List<String> storeIdList,
                                                       @Param("sceneIdList") List<Long> sceneIdList,
                                                       @Param("regionPathList") List<String> regionPathList,
                                                       @Param("reportType") String reportType) ;

    List<AiInspectionProblemTimeReportVO> sceneProblemTimeList(@Param("enterpriseId") String enterpriseId,
                                                           @Param("startDate") String startDate,
                                                           @Param("endDate") String endDate,
                                                           @Param("storeIdList") List<String> storeIdList,
                                                           @Param("sceneIdList") List<Long> sceneIdList,
                                                           @Param("regionPathList") List<String> regionPathList) ;


    int countInspectionStorePictureByTime(@Param("enterpriseId") String enterpriseId, @Param("startTime") String startTime,
                                          @Param("endTime") String endTime,@Param("storeId")  String storeId,@Param("inspectionId")  Long inspectionId);

    int countInspectionStorePictureByDay(@Param("enterpriseId") String enterpriseId, @Param("captureDate") String captureDate,@Param("storeId")   String storeId,
                                         @Param("inspectionId") Long inspectionId);
}