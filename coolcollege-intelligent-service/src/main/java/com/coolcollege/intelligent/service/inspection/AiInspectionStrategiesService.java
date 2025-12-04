package com.coolcollege.intelligent.service.inspection;

import com.coolcollege.intelligent.model.ai.vo.AiModelSceneVO;
import com.coolcollege.intelligent.model.inspection.AiInspectionStrategiesDTO;
import com.coolcollege.intelligent.model.inspection.request.AiInspectionReportDetailRequest;
import com.coolcollege.intelligent.model.inspection.request.AiInspectionReportRequest;
import com.coolcollege.intelligent.model.inspection.request.AiInspectionStrategiesRequest;
import com.coolcollege.intelligent.model.inspection.vo.*;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * <p>
 * AI巡检策略表服务类
 * </p>
 *
 * @author zhangchenbiao
 * @since 2025/9/25
 */
public interface AiInspectionStrategiesService {

    PageInfo<AiInspectionStrategiesVO> selectList(String enterpriseId, AiInspectionStrategiesDTO query);

    /**
     * 添加AI巡检策略
     * @param enterpriseId 企业ID
     * @param strategiesRequest 策略信息
     * @return 添加的策略ID
     */
    Long add(String enterpriseId, AiInspectionStrategiesRequest strategiesRequest, String userId, List<AiModelSceneVO> aiModelSceneList);
    
    /**
     * 更新AI巡检策略
     * @param enterpriseId 企业ID
     * @param strategiesRequest 策略信息
     * @return 是否更新成功
     */
    boolean update(String enterpriseId, AiInspectionStrategiesRequest strategiesRequest, String userId);
    
    /**
     * 启用AI巡检策略
     * @param enterpriseId 企业ID
     * @param id 策略ID
     * @param updateUserId 更新用户ID
     * @return 是否启用成功
     */
    boolean enable(String enterpriseId, Long id, String updateUserId);
    
    /**
     * 禁用AI巡检策略
     * @param enterpriseId 企业ID
     * @param id 策略ID
     * @param updateUserId 更新用户ID
     * @return 是否禁用成功
     */
    boolean disable(String enterpriseId, Long id, String updateUserId);
    
    /**
     * 获取AI巡检策略详情
     * @param enterpriseId 企业ID
     * @param id 策略ID
     * @return 策略详情
     */
    AiInspectionStrategiesVO getDetail(String enterpriseId, Long id);
    
    /**
     * 删除AI巡检策略
     * @param enterpriseId 企业ID
     * @param id 策略ID
     * @param updateUserId 更新用户ID
     * @return 是否删除成功
     */
    boolean delete(String enterpriseId, Long id, String updateUserId);


    PageInfo<AiInspectionStatisticsVO> dailyReportList(String enterpriseId, AiInspectionReportRequest query, String userId);

    AiInspectionStatisticsTotalVO dailyReportCount(String enterpriseId, AiInspectionReportRequest query, String userId);

    AiInspectionStatisticsReportDetailVO dailyReportDetail(String enterpriseId, AiInspectionReportDetailRequest query);

    PageInfo<AiInspectionStatisticsPicListVO> imageList(String enterpriseId, AiInspectionReportRequest query, String userId);
}