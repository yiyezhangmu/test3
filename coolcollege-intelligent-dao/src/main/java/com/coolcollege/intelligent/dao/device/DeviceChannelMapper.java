package com.coolcollege.intelligent.dao.device;

import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.dto.DeviceSummaryDataDTO;
import com.coolcollege.intelligent.model.device.request.DeleteChannelRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shoul
 */
@Mapper
public interface DeviceChannelMapper {

    void batchInsertOrUpdateDeviceChannel(@Param("eid")String eid,
                                          @Param("list") List<DeviceChannelDO> deviceChannelDOList);

    List<DeviceChannelDO> listDeviceChannelByDeviceId(@Param("eid")String eid,
                                                      @Param("parentDeviceIdList")List<String> parentDeviceIdList,
                                                      @Param("storeSceneId") Long storeSceneId);

    List<DeviceChannelDO> listDeviceChannelByIdList(@Param("eid")String eid,
                                                    @Param("idList") List<Long> idList);

    void batchDeleteDeviceChannelById(@Param("eid")String eid,
                                      @Param("idList") List<Long> idList);

    DeviceChannelDO selectDeviceChannelById(@Param("eid")String eid,
                                            @Param("id") Long id);
    void updateDeviceChannelById(@Param("eid")String eid,
                                 @Param("id") Long id,
                                 @Param("channelName")String channelName,
                                 @Param("hasPtz") Boolean hasPtz,
                                 @Param("storeSceneId") Long storeSceneId,
                                 @Param("remark")String remark,
                                 @Param("deviceStatus")String deviceStatus);

    void batchDeleteDeviceChannelByDeviceId(@Param("eid")String eid,
                                      @Param("parentDeviceIdList") List<String> parentDeviceIdList);

    void batchDeleteChannelByChannelNo(@Param("eid")String eid,
                                 @Param("parentDeviceId") String parentDeviceId,
                                 @Param("channelNoList") List<String> channelNoList);

    void updateDeviceChannalByDeviceId(@Param("eid")String eid,
                                       @Param("deviceIdList")List<String> deviceIdList);

    void updatePassengerDeviceChannalByDeviceId(@Param("eid")String eid,
                                                @Param("enablePassenger") Boolean enablePassenger,
                                                @Param("deviceIdList")List<String> deviceIdList);

    DeviceChannelDO selectDeviceChannelByParentId(@Param("eid")String eid,
                                                  @Param("parentDeviceId") String parentDeviceId,
                                                  @Param("channelNo") String channelNo);

    void updateDeviceChannelStatus(@Param("eid")String eid,
                                   @Param("list") List<DeviceChannelDO> deviceChannelDOList);

    List<DeviceChannelDO> getDeviceChannelByParentId(@Param("eid")String eid, @Param("parentDeviceId") String parentDeviceId);

    /**
     * @param eid
     * @param parentDeviceIdList
     * @return
     */
    DeviceSummaryDataDTO getDeviceChannelSummaryData(@Param("eid") String eid,
                                                     @Param("parentDeviceIdList") List<String> parentDeviceIdList);

    Integer getDeviceChannelCount(@Param("eid")String eid);

    List<DeviceChannelDO> getByParentDeviceIds(@Param("eid") String eid, @Param("deviceIds") List<String> deviceIds, @Param("storeSceneIds") List<Long> storeSceneIds);

    int deleteChannel(@Param("enterpriseId")String enterpriseId, @Param("channelList") List<DeleteChannelRequest.ChannelDelete> channelList);
}
