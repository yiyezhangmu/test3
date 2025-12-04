package com.coolcollege.intelligent.service.inspection;

import com.coolcollege.intelligent.model.inspection.AiInspectionStatisticsDTO;
import com.coolcollege.intelligent.model.inspection.vo.*;

import java.util.List;
import java.util.Map;

/**
 *
 * @author byd
 * @date 2025-11-13 16:30
 */
public interface AiInspectionStatisticsService {

    AiInspectionReportVO inspectionOverview(String enterpriseId, String userId, AiInspectionStatisticsDTO statisticsDTO);

    List<AiInspectionTendReportVO> inspectionTrend(String enterpriseId, String userId, AiInspectionStatisticsDTO statisticsDTO);

    List<AiInspectionProblemReportVO> sceneProblemRate(String enterpriseId, String userId, AiInspectionStatisticsDTO statisticsDTO);

    List<AiInspectionProblemStoreReportVO> problemStoreTop(String enterpriseId, String userId, AiInspectionStatisticsDTO statisticsDTO);

    List<AiInspectionProblemTimeReportListVO> sceneProblemTimeList(String enterpriseId, String userId, AiInspectionStatisticsDTO statisticsDTO);
}
