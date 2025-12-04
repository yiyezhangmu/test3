package com.coolcollege.intelligent.service.aianalysis;

import com.coolcollege.intelligent.model.aianalysis.dto.AiAnalysisReportQueryDTO;
import com.coolcollege.intelligent.model.aianalysis.vo.AiAnalysisReportSimpleVO;
import com.coolcollege.intelligent.model.aianalysis.vo.AiAnalysisReportVO;
import com.github.pagehelper.PageInfo;

/**
 * <p>
 * AI分析报告 服务类
 * </p>
 *
 * @author wangff
 * @since 2025/7/3
 */
public interface AiAnalysisReportService {

    /**
     * 分页查询
     * @param enterpriseId 企业id
     * @param queryDTO 查询DTO
     * @return AI分析报告简单信息分页
     */
    PageInfo<AiAnalysisReportSimpleVO> getPage(String enterpriseId, AiAnalysisReportQueryDTO queryDTO);

    /**
     * 详情查询
     * @param enterpriseId 企业id
     * @param id id
     * @return AI分析报告VO
     */
    AiAnalysisReportVO getById(String enterpriseId, Long id);
}
