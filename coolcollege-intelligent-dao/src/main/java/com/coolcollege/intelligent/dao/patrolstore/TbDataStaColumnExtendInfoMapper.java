package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.entity.TbDataStaColumnExtendInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-07-10 02:37
 */
@Mapper
public interface TbDataStaColumnExtendInfoMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-07-10 02:37
     */
    int insertSelective(@Param("record") TbDataStaColumnExtendInfoDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-07-10 02:37
     */
    int updateByPrimaryKeySelective(@Param("record") TbDataStaColumnExtendInfoDO record, @Param("enterpriseId") String enterpriseId);

    int batchInsertOrUpdateDataColumnExtendInfo(@Param("enterpriseId") String enterpriseId, @Param("insertList") List<TbDataStaColumnExtendInfoDO> insertList);

    /**
     * 查询是否存在分析中的AI项
     *
     * @param enterpriseId 企业ID
     * @param businessId
     */
    boolean checkHasProcessingColumn(@Param("enterpriseId") String enterpriseId,
                                @Param("businessId") Long businessId);

    TbDataStaColumnExtendInfoDO selectById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    List<TbDataStaColumnExtendInfoDO> selectTimeoutProcessingRecords(@Param("enterpriseId") String enterpriseId, @Param("timeoutMinutes") int timeoutMinutes);

    void updateAiStatusByJob(@Param("enterpriseId") String enterpriseId,
                                        @Param("idList") List<Long> idList,
                                        @Param("failReason") String failReason);

    void resetAiStatus(@Param("enterpriseId") String enterpriseId,
                       @Param("businessId") Long businessId);


}