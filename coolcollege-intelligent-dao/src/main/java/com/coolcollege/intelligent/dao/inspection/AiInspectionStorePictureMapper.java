package com.coolcollege.intelligent.dao.inspection;

import com.coolcollege.intelligent.model.inspection.entity.AiInspectionStorePictureDO;
import com.coolcollege.intelligent.model.inspection.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-10-11 04:33
 */
public interface AiInspectionStorePictureMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-10-11 04:33
     */
    int insertSelective(@Param("record") AiInspectionStorePictureDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-10-11 04:33
     */
    int updateByPrimaryKeySelective(@Param("record") AiInspectionStorePictureDO record, @Param("enterpriseId") String enterpriseId);


    /**
     * 批量插入图片分析结果记录
     *
     * @param records      图片分析结果实体列表
     * @param enterpriseId 企业ID
     * @return 影响行数
     */
    int batchInsert(@Param("records") List<AiInspectionStorePictureDO> records, @Param("enterpriseId") String enterpriseId);

    List<AiInspectionStatisticsProblemPicVO> failPicCountList(
            @Param("enterpriseId") String enterpriseId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("storeId") String storeId,
            @Param("sceneId") Long sceneId);

    List<AiInspectionStatisticsPicListVO> selectStatisticsImageByDateRange(
            @Param("enterpriseId") String enterpriseId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("storeIdList") List<String> storeIdList,
            @Param("sceneId") Long sceneId,
            @Param("inspectionResult") String inspectionResult,
            @Param("regionPathList") List<String> regionPathList);

    List<AiInspectionStatisticsPicDetailVO> imageList(
            @Param("enterpriseId") String enterpriseId,
            @Param("sceneIdList") List<Long> sceneIdList,
            @Param("storeIdList")   List<String> storeIdList,
            @Param("captureDateList") List<String> captureDateList,
            @Param("inspectionResult") String inspectionResult);

    List<AiInspectionPhotoVO> selectFailImageByDateRange(
            @Param("enterpriseId") String enterpriseId,
            @Param("sceneIdList") List<Long> sceneIdList,
            @Param("storeIdList")   List<String> storeIdList,
            @Param("captureDateList") List<String> captureDateList,
            @Param("reportType")String reportType);


    List<AiInspectionStorePictureDO> selectInspectionPeriodIdAiStatus(@Param("enterpriseId") String enterpriseId, @Param("date") String date,
                                                              @Param("inspectionPeriodId") Long inspectionPeriodId);

    List<AiInspectionStorePictureDO> selectListByPeriodId(@Param("enterpriseId") String enterpriseId,
                                                              @Param("inspectionPeriodId") Long inspectionPeriodId);

    int updateResultByPeriodId(@Param("enterpriseId") String enterpriseId, @Param("aiPeriodResult") String aiPeriodResult,
                                                              @Param("inspectionPeriodId") Long inspectionPeriodId);

    AiInspectionStorePictureDO selectById(@Param("enterpriseId") String enterpriseId,
                                          @Param("id") Long id);

    /**
     * 批量更新图片分析结果记录
     *
     * @param records      图片分析结果实体列表
     * @param enterpriseId 企业ID
     * @return 影响行数
     */
    int batchUpdate(@Param("records") List<AiInspectionStorePictureDO> records, @Param("enterpriseId") String enterpriseId);

}