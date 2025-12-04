package com.coolcollege.intelligent.dao.aianalysis;

import com.coolcollege.intelligent.model.aianalysis.AiAnalysisReportUserMappingDO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-06-30 05:01
 */
public interface AiAnalysisReportUserMappingMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-06-30 05:01
     */
    int insertSelective(@Param("record") AiAnalysisReportUserMappingDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-06-30 05:01
     */
    int updateByPrimaryKeySelective(@Param("record") AiAnalysisReportUserMappingDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量新增
     */
    int insertBatch(@Param("enterpriseId") String enterpriseId, @Param("list") List<AiAnalysisReportUserMappingDO> list);

    /**
     * 查询用户可见的报告
     * @param enterpriseId 企业id
     * @param userId 用户id
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param storeIds 门店id列表
     * @return 报告id列表
     */
    List<Long> getReportIdByUser(@Param("enterpriseId") String enterpriseId,
                                 @Param("userId") String userId,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate,
                                 @Param("storeIds") List<String> storeIds);

    /**
     * 根据报告id查询用户id列表
     * @param enterpriseId 企业id
     * @param reportId 报告id
     * @return userId列表
     */
    List<String> getUserIdsByReportId(@Param("enterpriseId") String enterpriseId, @Param("reportId") Long reportId);

    /**
     * 根据报告id查询用户id列表
     * @param enterpriseId 企业id
     * @param reportIds 报告id列表
     * @return userId列表
     */
    List<AiAnalysisReportUserMappingDO> getUserIdsByReportIds(@Param("enterpriseId") String enterpriseId, @Param("reportIds") List<Long> reportIds);

    /**
     * 根据报告id删除人员映射
     * @param enterpriseId 企业id
     * @param reportIds 报告id列表
     * @return java.lang.Boolean
     */
    int deleteByReportIds(@Param("enterpriseId") String enterpriseId, @Param("reportIds") List<Long> reportIds);
}