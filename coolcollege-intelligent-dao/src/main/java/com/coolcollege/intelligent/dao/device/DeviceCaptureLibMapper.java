package com.coolcollege.intelligent.dao.device;

import com.coolcollege.intelligent.model.device.DeviceCaptureLibDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2022-12-16 11:11
 */
public interface DeviceCaptureLibMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-12-16 11:11
     */
    int insertSelective(@Param("record") DeviceCaptureLibDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-12-16 11:11
     */
    DeviceCaptureLibDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-12-16 11:11
     */
    int updateByPrimaryKeySelective(@Param("record") DeviceCaptureLibDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-12-16 11:11
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量删除
     * @param ids
     * @param enterpriseId
     * @return
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("enterpriseId") String enterpriseId);

    List<DeviceCaptureLibDO> listByStoreId(@Param("storeId") String storeId,
                                           @Param("enterpriseId") String enterpriseId,
                                           @Param("beginTime") String beginTime,
                                           @Param("endTime") String endTime);

}