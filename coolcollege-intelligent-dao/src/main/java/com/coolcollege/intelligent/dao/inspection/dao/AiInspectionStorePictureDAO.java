package com.coolcollege.intelligent.dao.inspection.dao;

import com.coolcollege.intelligent.dao.inspection.AiInspectionStorePictureMapper;
import com.coolcollege.intelligent.model.inspection.entity.AiInspectionStorePictureDO;
import com.coolcollege.intelligent.model.inspection.vo.AiInspectionPhotoVO;
import com.coolcollege.intelligent.model.inspection.vo.AiInspectionStatisticsPicDetailVO;
import com.coolcollege.intelligent.model.inspection.vo.AiInspectionStatisticsPicListVO;
import com.coolcollege.intelligent.model.inspection.vo.AiInspectionStatisticsProblemPicVO;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * AI巡检门店设备抓拍图片分析结果表
 * </p>
 *
 * @author zhangchenbiao
 * @since 2025/9/25
 */
@RequiredArgsConstructor
@Repository
public class AiInspectionStorePictureDAO {
    private final AiInspectionStorePictureMapper aiInspectionStorePictureMapper;

    /**
     * 插入图片分析结果记录
     *
     * @param record       图片分析结果实体
     * @param enterpriseId 企业ID
     * @return 影响行数
     */
    public int insertSelective(AiInspectionStorePictureDO record, String enterpriseId) {
        return aiInspectionStorePictureMapper.insertSelective(record, enterpriseId);
    }

    /**
     * 批量插入图片分析结果记录
     *
     * @param records      图片分析结果实体列表
     * @param enterpriseId 企业ID
     * @return 影响行数
     */
    public int batchInsert(List<AiInspectionStorePictureDO> records, String enterpriseId) {
        return aiInspectionStorePictureMapper.batchInsert(records, enterpriseId);
    }

    /**
     * 根据主键更新图片分析结果记录
     *
     * @param record       图片分析结果实体
     * @param enterpriseId 企业ID
     * @return 影响行数
     */
    public int updateByPrimaryKeySelective(AiInspectionStorePictureDO record, String enterpriseId) {
        return aiInspectionStorePictureMapper.updateByPrimaryKeySelective(record, enterpriseId);
    }


    public List<AiInspectionStatisticsProblemPicVO> failPicCountList(
            String enterpriseId,
            Long sceneId,
            String storeId,
            String beginTime,
            String endTime) {
        return aiInspectionStorePictureMapper.failPicCountList(
                enterpriseId,
                beginTime,
                endTime,
                storeId,
                sceneId);
    }
    public List<AiInspectionStatisticsPicListVO> selectStatisticsImageByDateRange(
            String enterpriseId,
            Long sceneId,
            List<String> storeIdList,
            String beginTime,
            String endTime,
            String inspectionResult,
            List<String> regionPathList) {
        return aiInspectionStorePictureMapper.selectStatisticsImageByDateRange(
                enterpriseId,
                beginTime,
                endTime,
                storeIdList,
                sceneId,
                inspectionResult,
                regionPathList);
    }


    public List<AiInspectionStatisticsPicDetailVO> imageList(
            String enterpriseId,
            List<Long> sceneIdList,
            List<String> storeIdList,
            List<String> captureDateList,
            String inspectionResult) {
        return aiInspectionStorePictureMapper.imageList(
                enterpriseId,
                sceneIdList,
                storeIdList,
                captureDateList,
                inspectionResult);
    }

    public List<AiInspectionPhotoVO> selectFailImageByDateRange(
            String enterpriseId,
            List<Long> sceneIdList,
            List<String> storeIdList,
            List<String> captureDateList,
            String reportType) {
        return aiInspectionStorePictureMapper.selectFailImageByDateRange(
                enterpriseId,
                sceneIdList,
                storeIdList,
                captureDateList,
                reportType);
    }

    public List<AiInspectionStorePictureDO> selectInspectionPeriodIdAiStatus(String enterpriseId, String date, Long inspectionPeriodId) {
        return aiInspectionStorePictureMapper.selectInspectionPeriodIdAiStatus(enterpriseId, date, inspectionPeriodId);
    }

    public List<AiInspectionStorePictureDO> selectListByPeriodId(String enterpriseId, Long inspectionPeriodId) {
        return aiInspectionStorePictureMapper.selectListByPeriodId(enterpriseId, inspectionPeriodId);
    }

    public int updateResultByPeriodId(String enterpriseId, String aiPeriodResult, Long inspectionPeriodId) {
        return aiInspectionStorePictureMapper.updateResultByPeriodId(enterpriseId, aiPeriodResult, inspectionPeriodId);
    }

    public AiInspectionStorePictureDO selectById( String enterpriseId,
                                           Long id){
        return aiInspectionStorePictureMapper.selectById(enterpriseId, id);
    }

    public int batchUpdate(String enterpriseId, List<AiInspectionStorePictureDO> records){
        return aiInspectionStorePictureMapper.batchUpdate(records, enterpriseId);
    }
}