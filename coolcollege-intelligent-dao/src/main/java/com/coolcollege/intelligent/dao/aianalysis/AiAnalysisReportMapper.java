package com.coolcollege.intelligent.dao.aianalysis;

import com.coolcollege.intelligent.model.aianalysis.AiAnalysisReportDO;
import com.coolcollege.intelligent.model.aianalysis.vo.AiAnalysisReportSimpleVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-06-30 05:02
 */
public interface AiAnalysisReportMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-06-30 05:02
     */
    int insertSelective(@Param("record") AiAnalysisReportDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-06-30 05:02
     */
    int updateByPrimaryKeySelective(@Param("record") AiAnalysisReportDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 简单信息列表查询
     * @param enterpriseId 企业id
     * @param ids 报告id列表
     * @return AI分析报告列表
     */
    List<AiAnalysisReportSimpleVO> getSimpleListByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);

    /**
     * 根据id查询
     * @param enterpriseId 企业id
     * @param id 主键id
     * @return AI分析报告
     */
    AiAnalysisReportDO getById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    /**
     * 查询已经发过报告的规则
     * @param enterpriseId 企业id
     * @param ruleIds 规则id列表
     * @param date 报告分析时间
     * @return 规则id集合
     */
    List<Long> getExistRule(@Param("enterpriseId") String enterpriseId, @Param("ruleIds") List<Long> ruleIds, @Param("date") LocalDate date);

    /**
     * 根据推送时间查询
     * @param enterpriseId 企业id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 报告列表
     */
    List<AiAnalysisReportDO> getListByPushTime(@Param("enterpriseId") String enterpriseId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 根据规则id查询报告id
     * @param enterpriseId 企业id
     * @param ruleIds 规则id列表
     * @param reportDate 报告分析日期
     * @return 报告id列表
     */
    List<AiAnalysisReportDO> getReportByRuleIds(@Param("enterpriseId") String enterpriseId, @Param("ruleIds") List<Long> ruleIds, @Param("reportDate") LocalDate reportDate);

    /**
     * 根据规则id及报告日期删除
     * @param enterpriseId 企业id
     * @param ruleIds 规则id列表
     * @param reportDate 报告分析日期
     */
    int deleteByRuleIds(@Param("enterpriseId") String enterpriseId, @Param("ruleIds") List<Long> ruleIds, @Param("reportDate") LocalDate reportDate);
}