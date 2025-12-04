package com.coolcollege.intelligent.service.aianalysis;

import com.coolcollege.intelligent.model.aianalysis.dto.AiAnalysisRuleDTO;
import com.coolcollege.intelligent.model.aianalysis.dto.AiAnalysisRuleQueryDTO;
import com.coolcollege.intelligent.model.aianalysis.vo.AiAnalysisModelVO;
import com.coolcollege.intelligent.model.aianalysis.vo.AiAnalysisRuleSimpleVO;
import com.coolcollege.intelligent.model.aianalysis.vo.AiAnalysisRuleVO;
import com.github.pagehelper.PageInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * AI分析规则 服务类
 * </p>
 *
 * @author wangff
 * @since 2025/6/30
 */
public interface AiAnalysisRuleService {

    /**
     * 新增
     * @param enterpriseId 企业id
     * @param dto AI分析规则新增编辑DTO
     * @return 是否成功
     */
    Boolean save(String enterpriseId, AiAnalysisRuleDTO dto);

    /**
     * 编辑
     * @param enterpriseId 企业id
     * @param dto AI分析规则新增编辑DTO
     * @return 是否成功
     */
    Boolean update(String enterpriseId, AiAnalysisRuleDTO dto);

    /**
     * 批量删除
     * @param enterpriseId 企业id
     * @param ids id列表
     * @return 是否删除
     */
    Boolean removeBatch(String enterpriseId, List<Long> ids);

    /**
     * 列表查询
     * @param enterpriseId 企业id
     * @param query AI分析规则查询DTO
     * @return AI分析规则简单信息VO列表
     */
    PageInfo<AiAnalysisRuleSimpleVO> getPage(String enterpriseId, AiAnalysisRuleQueryDTO query);

    /**
     * 详情
     * @param enterpriseId 企业id
     * @param id id
     * @return AI分析规则VO
     */
    AiAnalysisRuleVO getById(String enterpriseId, Long id);

    /**
     * 获取AI模型列表
     * @return AI模型VO列表
     */
    List<AiAnalysisModelVO> getModelList();

    /**
     * 进行AI分析并生成店报
     * @param enterpriseId 企业id
     * @param date 日期
     * @param retryRuleIds 规则id列表
     */
    void aiAnalysis(String enterpriseId, LocalDate date, List<Long> retryRuleIds);

    /**
     * 删除并重新生成AI店报
     * @param enterpriseId 企业id
     * @param date 日期
     * @param retryRuleIds 规则id列表
     */
    void deleteAndAiAnalysis(String enterpriseId, LocalDate date, List<Long> retryRuleIds);

    /**
     * 提交抓图任务，抓图报告生成日期的前一天
     * @param enterpriseId 企业id
     * @param date 报告生成日期
     * @param retryRuleIds 重试规则id列表
     */
    void submitCaptureTask(String enterpriseId, LocalDate date, List<Long> retryRuleIds);

    /**
     * 删除今日抓图任务并重新生成
     * @param enterpriseId 企业id
     * @param date 报告生成日期
     * @param retryRuleIds 重试规则id列表
     */
    void deleteAndSubmitCaptureTask(String enterpriseId, LocalDate date, List<Long> retryRuleIds);

    /**
     * 报告推送
     * @param enterpriseId 企业id
     * @param pushTime 推送时间
     */
    void reportPush(String enterpriseId, LocalDateTime pushTime);

    /**
     * 根据当前推送人员重新设置店报的人员映射关系
     * @param enterpriseId 企业id
     * @param date 日期
     * @param ruleIds 规则id列表
     */
    void reportUserMappingReset(String enterpriseId, LocalDate date, List<Long> ruleIds);
}
