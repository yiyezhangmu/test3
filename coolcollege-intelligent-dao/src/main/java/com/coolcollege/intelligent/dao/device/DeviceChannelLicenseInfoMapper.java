package com.coolcollege.intelligent.dao.device;

import com.coolcollege.intelligent.model.device.DeviceChannelLicenseInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-08-11 04:54
 */
public interface DeviceChannelLicenseInfoMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-08-11 04:54
     */
    int insertSelective(@Param("record") DeviceChannelLicenseInfoDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-08-11 04:54
     */
    int updateByPrimaryKeySelective(@Param("record") DeviceChannelLicenseInfoDO record, @Param("enterpriseId") String enterpriseId);

    void delete(@Param("eid") String enterpriseId, @Param("ids") List<String> list);

    void update(@Param("eid") String enterpriseId, @Param("channelName") String channelName, @Param("id") String id);

    List<DeviceChannelLicenseInfoDO> selectList(@Param("eid") String enterpriseId, @Param("id") String id);

    DeviceChannelLicenseInfoDO selectById(@Param("eid") String enterpriseId,@Param("id") String id);

    List<DeviceChannelLicenseInfoDO> selectListByIds(@Param("eid") String enterpriseId,@Param("ids") List<String> ids);

    List<DeviceChannelLicenseInfoDO> selectByLicenseIds(@Param("eid") String enterpriseId,@Param("licenseIds") List<String> licenseIds);

    DeviceChannelLicenseInfoDO selectByChannelSerial(@Param("channelSerial") String channelSerial,@Param("eid") String enterpriseId);
}