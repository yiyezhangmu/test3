package com.coolcollege.intelligent.dao.device;

import com.coolcollege.intelligent.model.device.DeviceLicenseInfoDO;
import com.coolcollege.intelligent.model.device.vo.DeviceLicenseVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-08-11 04:54
 */
public interface DeviceLicenseInfoMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-08-11 04:54
     */
    int insertSelective(@Param("record") DeviceLicenseInfoDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-08-11 04:54
     */
    int updateByPrimaryKeySelective(@Param("record") DeviceLicenseInfoDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量新增
     *
     * @param licenseInfos
     * @author twc
     * @date 2024/09/13
     */
    void batchInsert(@Param("eid") String enterpriseId, @Param("list") List<DeviceLicenseInfoDO> licenseInfos);

    List<DeviceLicenseVO> selectList(@Param("eid") String enterpriseId,
                                     @Param("type") Integer type,
                                     @Param("deviceSerial") String deviceSerial,
                                     @Param("name") String name,
                                     @Param("status") Integer status,
                                     @Param("useByNew") Integer useByNew);

    DeviceLicenseInfoDO selectOne(@Param("eid") String enterpriseId, @Param("id") String id);

    void update(@Param("eid") String enterpriseId, @Param("name") String name, @Param("remark") String remark, @Param("id") String id);

    void delete(@Param("eid") String enterpriseId, @Param("id") String id);

    void updateStatus(@Param("eid") String enterpriseId, @Param("id") String id,@Param("ys")String ysDeviceSerial);

    List<DeviceLicenseInfoDO> selectListByIds(@Param("eid") String enterpriseId, @Param("licenseIds") List<String> licenseIds);

    void updateChannelIds(@Param("eid") String enterpriseId, @Param("id") Long id, @Param("channelIds") String channelIds);

    void updateStatusNoNew(@Param("eid") String enterpriseId,@Param("ids") List<String> id);
}