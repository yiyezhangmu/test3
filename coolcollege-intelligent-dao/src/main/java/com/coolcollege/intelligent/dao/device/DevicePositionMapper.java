package com.coolcollege.intelligent.dao.device;

import com.coolcollege.intelligent.model.device.DevicePositionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/28
 */
@Mapper
public interface DevicePositionMapper {

    void insertDevicePosition(@Param("eid") String eid,
                              @Param("devicePositionDO") DevicePositionDO devicePositionDO);


    void batchInsertDevicePosition(@Param("eid") String eid,
                              @Param("devicePositionDOList") List<DevicePositionDO> devicePositionDOList);

    void updateDevicePosition(@Param("eid") String eid,
                                      @Param("devicePositionDO") DevicePositionDO devicePositionDO);

    void deleteDevicePosition(@Param("eid") String eid,
                              @Param("id") Long id);

    DevicePositionDO selectDevicePositionById(@Param("eid") String eid,
                                              @Param("id") Long id);

    List<DevicePositionDO> listDevicePositionByDeviceAndChannel(@Param("eid") String eid,
                                                                @Param("deviceId") String deviceId,
                                                                @Param("channelNo") String channelNo);

    List<Integer> maxIndex(@Param("eid") String eid,
                     @Param("deviceId") String deviceId,
                     @Param("channelNo") String channelNo);


}
