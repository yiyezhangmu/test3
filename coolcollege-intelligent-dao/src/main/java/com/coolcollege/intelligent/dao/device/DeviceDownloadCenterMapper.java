package com.coolcollege.intelligent.dao.device;

import com.coolcollege.intelligent.model.device.DeviceDownloadCenterDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2022-12-16 11:50
 */
public interface DeviceDownloadCenterMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-12-16 11:50
     */
    int insertSelective(@Param("record") DeviceDownloadCenterDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-12-16 11:50
     */
    DeviceDownloadCenterDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-12-16 11:50
     */
    int updateByPrimaryKeySelective(@Param("record") DeviceDownloadCenterDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-12-16 11:50
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    List<DeviceDownloadCenterDO> listByUserId(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId);

    /**
     * 查询所有进行中的下载记录
     * @param enterpriseId 企业id
     * @return 实体对象列表
     */
    List<DeviceDownloadCenterDO> selectOngoingRecords(@Param("enterpriseId") String enterpriseId);
}