package com.coolcollege.intelligent.dao.aianalysis;

import com.coolcollege.intelligent.model.aianalysis.AiAnalysisRuleDO;
import com.coolcollege.intelligent.model.aianalysis.dto.AiAnalysisRuleQueryDTO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-06-30 04:55
 */
public interface AiAnalysisRuleMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-06-30 04:55
     */
    int insertSelective(@Param("record") AiAnalysisRuleDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-06-30 04:55
     */
    int updateByPrimaryKeySelective(@Param("record") AiAnalysisRuleDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量删除
     */
    int deleteBatch(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);

    /**
     * 列表查询
     */
    List<AiAnalysisRuleDO> getList(@Param("enterpriseId") String enterpriseId, @Param("query") AiAnalysisRuleQueryDTO query);

    /**
     * 根据id查询
     */
    AiAnalysisRuleDO getById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    /**
     * 查询日期在有效期内的AI规则
     */
    List<AiAnalysisRuleDO> getListByPeriod(@Param("enterpriseId") String enterpriseId, @Param("time") LocalDate time, @Param("ruleIds") List<Long> ruleIds);
}