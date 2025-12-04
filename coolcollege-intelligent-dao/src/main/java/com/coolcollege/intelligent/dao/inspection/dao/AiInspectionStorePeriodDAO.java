package com.coolcollege.intelligent.dao.inspection.dao;

import com.coolcollege.intelligent.dao.inspection.AiInspectionStorePeriodMapper;
import com.coolcollege.intelligent.model.inspection.entity.AiInspectionStorePeriodDO;
import com.coolcollege.intelligent.model.inspection.vo.AiInspectionStatisticsPicListVO;
import com.coolcollege.intelligent.model.inspection.vo.AiInspectionStatisticsSceneVO;
import com.coolcollege.intelligent.model.inspection.vo.AiInspectionStatisticsTotalVO;
import com.coolcollege.intelligent.model.inspection.vo.AiInspectionStatisticsVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author byd
 * @date 2025-10-14 15:42
 */
@Repository
public class AiInspectionStorePeriodDAO {

    @Resource
    private AiInspectionStorePeriodMapper aiInspectionStorePeriodMapper;

    public int insertSelective(AiInspectionStorePeriodDO record, String enterpriseId) {
        return aiInspectionStorePeriodMapper.insertSelective(record, enterpriseId);
    }

    public int updateByPrimaryKeySelective(AiInspectionStorePeriodDO record, String enterpriseId) {
        return aiInspectionStorePeriodMapper.updateByPrimaryKeySelective(record, enterpriseId);
    }

    public List<AiInspectionStatisticsVO> selectStatisticsByDateRange(
            String enterpriseId,
            Long sceneId,
            List<String> storeIdList,
            String beginTime,
            String endTime,
            String inspectionResult,
            String reportType,
            List<String> regionPathList) {
        return aiInspectionStorePeriodMapper.selectStatisticsByDateRange(
                enterpriseId,
                beginTime,
                endTime,
                storeIdList,
                sceneId,
                inspectionResult,
                reportType,
                regionPathList);
    }

    public AiInspectionStatisticsTotalVO dailyReportCount(
            String enterpriseId,
            Long sceneId,
            List<String> storeIdList,
            String beginTime,
            String endTime,
            String inspectionResult,
            List<String> regionPathList) {
        return aiInspectionStorePeriodMapper.dailyReportCount(
                enterpriseId,
                beginTime,
                endTime,
                storeIdList,
                sceneId,
                inspectionResult,
                regionPathList);
    }

    public AiInspectionStatisticsTotalVO problemTop(
            String enterpriseId,
            Long sceneId,
            List<String> storeIdList,
            String beginTime,
            String endTime,
            String inspectionResult,
            List<String> regionPathList) {
        return aiInspectionStorePeriodMapper.problemTop(
                enterpriseId,
                beginTime,
                endTime,
                storeIdList,
                sceneId,
                inspectionResult,
                regionPathList);
    }
    public List<AiInspectionStatisticsSceneVO> sceneCountList(
            String enterpriseId,
            Long sceneId,
            String storeId,
            String beginTime,
            String endTime,
            String inspectionResult) {
        return aiInspectionStorePeriodMapper.sceneCountList(
                enterpriseId,
                beginTime,
                endTime,
                storeId,
                sceneId,
                inspectionResult);
    }

    public List<AiInspectionStatisticsPicListVO> selectStatisticsImageByDateRange(
            String enterpriseId,
            Long sceneId,
            List<String> storeIdList,
            String beginTime,
            String endTime,
            String inspectionResult,
            List<String> regionPathList) {
        return aiInspectionStorePeriodMapper.selectStatisticsImageByDateRange(
                enterpriseId,
                beginTime,
                endTime,
                storeIdList,
                sceneId,
                inspectionResult,
                regionPathList);
    }
    public AiInspectionStorePeriodDO selectByPrimaryKey(String enterpriseId, Long id){
        return aiInspectionStorePeriodMapper.selectByPrimaryKey(enterpriseId, id);
    }

    public int countInspectionStorePictureByTime(String enterpriseId, String startTime, String endTime, String storeId, Long inspectionId){
        return aiInspectionStorePeriodMapper.countInspectionStorePictureByTime(enterpriseId, startTime, endTime, storeId, inspectionId);
    }

    public int countInspectionStorePictureByDay(String enterpriseId, String captureDate,  String storeId, Long inspectionId){
        return aiInspectionStorePeriodMapper.countInspectionStorePictureByDay(enterpriseId, captureDate, storeId, inspectionId);
    }
}
