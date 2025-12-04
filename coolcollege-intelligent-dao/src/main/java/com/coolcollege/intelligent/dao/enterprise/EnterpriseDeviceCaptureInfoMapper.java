package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.entity.EnterpriseDeviceCaptureInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-11-06 04:41
 */
public interface EnterpriseDeviceCaptureInfoMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-11-06 04:41
     */
    int insertSelective(EnterpriseDeviceCaptureInfoDO record);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-11-06 04:41
     */
    int updateByPrimaryKeySelective(EnterpriseDeviceCaptureInfoDO record);
    /**
     *
     * 批量插入
     * dateTime:2025-11-06 04:41
     */
    int insertBatch(@Param("records")List<EnterpriseDeviceCaptureInfoDO> records);

    List<EnterpriseDeviceCaptureInfoDO> selectList(@Param("enterpriseId") String enterpriseId, @Param("lastId") Long lastId,
                                                   @Param("createTime") String createTime);

    EnterpriseDeviceCaptureInfoDO selectByTaskId(@Param("captureTaskId") String captureTaskId);

}