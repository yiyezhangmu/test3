package com.coolcollege.intelligent.dao.device;

import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.DeviceDTO;
import com.coolcollege.intelligent.model.device.dto.DeviceMappingDTO;
import com.coolcollege.intelligent.model.device.dto.DeviceSummaryDataDTO;
import com.coolcollege.intelligent.model.device.dto.DeviceSummaryListDTO;
import com.coolcollege.intelligent.model.device.request.DeviceReportSearchRequest;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shoul
 */
@Mapper
public interface DeviceMapper {



     DeviceDO getDeviceByDeviceId(@Param("enterpriseId") String enterpriseId,
                                   @Param("deviceId") String deviceId);


    DeviceDO getDeviceById(@Param("enterpriseId") String enterpriseId,
                                 @Param("id") Long id);


    /**
     *
     * @param enterpriseId 必传不能为空
     * @return
     */
     List<String> getDeviceId(@Param("enterpriseId") String enterpriseId);


    List<DeviceDO> getDeviceByDeviceIdList(@Param("enterpriseId") String enterpriseId,
                                  @Param("deviceIdList") List<String> deviceIdList);

    List<DeviceDO> getDeviceByIdList(@Param("enterpriseId") String enterpriseId,
                                           @Param("idList") List<Long> idList);

    List<DeviceDTO> getDeviceDTOByDeviceIdList(@Param("enterpriseId") String enterpriseId,
                                  @Param("deviceIdList") List<String> deviceIdList);

    List<DeviceDO> getDeviceByStoreIdList(@Param("enterpriseId") String enterpriseId,
                                           @Param("storeIdList") List<String> storeIdList,
                                          @Param("deviceType")String deviceType,
                                          @Param("resourceList") List<String> resourceList,
                                            @Param("supportCapture") Boolean supportCapture);

    /**
     * 批量插入或更新
     * @param enterpriseId
     * @param list
     * @param deviceType
     */
    void batchInsertOrUpdateDevices(@Param("enterpriseId") String enterpriseId, @Param("list") List<DeviceDO> list, @Param("deviceType") String deviceType);


    void batchUpdateDevices(@Param("enterpriseId") String enterpriseId, @Param("deviceList") List<DeviceDO> deviceList);

    /**
     * 批量删除设备
     * @param enterpriseId
     * @param list
     * @param deviceType
     */
    void batchDeleteDevices(@Param("enterpriseId") String enterpriseId, @Param("list") List<String> list, @Param("deviceType") String deviceType);

    /**
     * 根据设备id获取设备列表(以设备为主表)
     * @param enterpriseId
     * @param deviceIds
     * @param deviceType
     * @return
     */
    List<DeviceDTO> getDevicesByDeviceIds(@Param("enterpriseId") String enterpriseId, @Param("deviceIds") List<String> deviceIds, @Param("deviceType") String deviceType);

    int updateDevice(@Param("enterpriseId") String enterpriseId,
                     @Param("deviceDO") DeviceDO deviceDO);

    /**
     * 条件查询绑定的设备List
     * @param enterpriseId
     * @param storeIdList
     * @param deviceType
     * @param keywords
     * @param bindStatus
     * @return
     */
    List<DeviceMappingDTO> getDeviceListForStore(@Param("enterpriseId") String enterpriseId,
                                                 @Param("storeIdList") List<String> storeIdList,
                                                 @Param("deviceType") String deviceType,
                                                 @Param("keywords") String keywords,
                                                 @Param("bindStatus") Integer bindStatus,
                                                 @Param("deviceStatus") String deviceStatus);

    /**
     * 查询设备以设备为主体
     * @param enterpriseId
     * @param deviceType
     * @param device
     * @param bindStatus
     * @return
     */
    List<DeviceMappingDTO> getDeviceListForNotStore(@Param("enterpriseId") String enterpriseId,
                                                    @Param("deviceType") String deviceType,
                                                    @Param("device") String device,
                                                    @Param("bindStatus") Integer bindStatus,
                                                    @Param("deviceStatus") String deviceStatus,
                                                    @Param("isAdmin") Boolean isAdmin,
                                                    @Param("authStoreIdList") List<String> authStoreIdList,
                                                    @Param("authFullRegionPathList") List<String> authFullRegionPathList);

    List<DeviceMappingDTO> getDeviceListForNotBind(@Param("enterpriseId") String enterpriseId,
                                                    @Param("deviceType") String deviceType,
                                                    @Param("device") String device,
                                                    @Param("bindStatus") Integer bindStatus,
                                                    @Param("deviceStatus") String deviceStatus);

    /**
     * 查询设备  设备名称模糊 设备ID列表
     * @param enterpriseId
     * @param deviceName
     * @param deviceIdList
     * @param deviceType
     * @param beginTime
     * @param endTime
     * @return
     */
    List<DeviceDTO> getDeviceListForDevice(@Param("enterpriseId") String enterpriseId,
                                           @Param("deviceName") String deviceName,
                                           @Param("deviceIdList") List<String> deviceIdList,
                                           @Param("deviceType") String deviceType,
                                           @Param("beginTime") Long beginTime,
                                           @Param("endTime") Long endTime);

    /**
     * 更新设备名称
     * @param enterpriseId
     * @param deviceId
     * @param deviceName
     * @param userName
     * @return
     */
    int updateDeviceNameById(@Param("enterpriseId") String enterpriseId,
                             @Param("deviceId") String deviceId,
                             @Param("deviceName") String deviceName,
                             @Param("userName") String userName);


    /**
     *更新设备绑定状态
     * @param enterpriseId
     * @param deviceIdList
     * @param bindStatus
     * @return
     */
    int updateDeviceBindStatus(@Param("enterpriseId") String enterpriseId,
                               @Param("deviceIdList") List<String> deviceIdList,
                               @Param("bindStatus")Boolean bindStatus);

    /**
     * 修改设备绑定id
     * @Author chenyupeng
     * @Date 2021/7/1
     * @param enterpriseId
     * @param deviceId
     * @param storeDTO
     * @return: int
     */
    int updateDeviceBindStoreId(@Param("enterpriseId") String enterpriseId,
                               @Param("deviceId") String deviceId,
                               @Param("storeDTO") StoreDTO storeDTO);

    /**
     * 批量修改设备绑定id
     * @Author chenyupeng
     * @Date 2021/7/6
     * @param enterpriseId
     * @param deviceIds
     * @param storeDTO
     * @return: int
     */
    int bathUpdateDeviceBindStoreId(@Param("enterpriseId") String enterpriseId,
                                @Param("deviceIdList") List<String> deviceIds,
                                @Param("bindTime") Long bindTime,
                                @Param("storeDTO") StoreDTO storeDTO,
                                @Param("bindStatus")Boolean bindStatus);


    /**
     * bathUpdateDeviceBindStoreIds
     * @param enterpriseId
     * @param deviceIds
     * @param bindTime
     * @param storeDTO
     * @param bindStatus
     * @param bindStoreIds
     * @return
     */
    int bathUpdateDeviceBindStoreIds(@Param("enterpriseId") String enterpriseId,
                                    @Param("deviceIdList") List<String> deviceIds,
                                    @Param("bindTime") Long bindTime,
                                    @Param("storeDTO") StoreDTO storeDTO,
                                    @Param("bindStatus")Boolean bindStatus,
                                    @Param("bindStoreIds") String bindStoreIds);

    /**
     * 批量修改设备绑定id
     * @Author chenyupeng
     * @Date 2021/7/6
     * @param enterpriseId
     * @param storeIds
     * @param storeDTO
     * @return: int
     */
    int bathUpdateDeviceBindStoreIdByStoreIds(@Param("enterpriseId") String enterpriseId,
                                    @Param("storeIdList") List<String> storeIds,
                                    @Param("bindTime") Long bindTime,
                                    @Param("storeDTO") StoreDTO storeDTO,
                                    @Param("bindStatus")Boolean bindStatus);

    int countDevice(@Param("eid") String eid,
                    @Param("type") String type);

    int countDeviceByStoreId(@Param("eid") String eid,
                    @Param("type") String type,
                    @Param("storeId") String storeId);

    List<DeviceDO> selectAllDevice(@Param("eid") String eid,@Param("type")String type);

    List<String> selectDeviceIdByLikeName(@Param("eid") String eid,@Param("deviceName") String deviceName);

    void  updateDeviceByDeviceId(@Param("eid") String eid,@Param("deviceDoList") List<DeviceDO> deviceDoList);

    void  updateEnablePassengerByDeviceId(@Param("eid") String eid,
                                          @Param("enable")Boolean enable,
                                          @Param("deviceIdList") List<String> deviceIdList);


    List<DeviceDO> selectBindDevice(@Param("eid") String eid,
                                    @Param("type") String type,
                                    @Param("resource") String resource);

    Integer count(@Param("eid") String enterpriseId);

    void updateDeviceStatus(@Param("eid") String eid, @Param("deviceDO") DeviceDO deviceDO);


    /**
     * @param eid
     * @param keyword 门店名称与编号
     * @param authFullRegionPathList
     * @return
     */
    DeviceSummaryDataDTO getDeviceSummaryData(@Param("eid") String eid,
                                              @Param("request") DeviceReportSearchRequest request,
                                              @Param("authFullRegionPathList") List<String> authFullRegionPathList,
                                              @Param("filter") Boolean filter);


    /**
     * 分组获取每个门店的设备在线数 离线数 总数
     * @param eid
     * @param request
     * @param authFullRegionPathList
     * @return
     */
    List<DeviceSummaryListDTO> getDeviceSummaryGroupStoreId(@Param("eid") String eid,
                                                            @Param("request") DeviceReportSearchRequest request,
                                                            @Param("authFullRegionPathList") List<String> authFullRegionPathList);


    List<DeviceSummaryListDTO> getDeviceStoreIds(@Param("eid") String eid,
                                                            @Param("request") DeviceReportSearchRequest request,
                                                            @Param("authFullRegionPathList") List<String> authFullRegionPathList);

    List<String> getDeviceIdByStoreIds(@Param("eid") String eid, @Param("storeIds") List<String> storeIds);


    /**
     * 导出设备数量
     * @param eid
     * @param keyword
     * @param authFullRegionPathList
     * @return
     */
    Integer deviceSummaryGroupStoreIdNum(@Param("eid") String eid,
                                         @Param("request") DeviceReportSearchRequest request,
                                         @Param("authFullRegionPathList") List<String> authFullRegionPathList);

    /**
     *
     * @param eid
     * @param storeIds
     * @return
     */
    List<String> getDeviceByStoreIds(@Param("eid") String eid, @Param("bindStoreIds")List<String> bindStoreIds);


    List<DeviceDO> selectListByBndStatus(@Param("eid") String eid);

    List<DeviceDO> selectShanDongList(@Param("eid") String eid);

    List<DeviceDO> getDeviceIdByStoreId(@Param("enterpriseId") String enterpriseId, @Param("storeId") String storeId);

    /**
     * 根据门店id查询
     * @param enterpriseId 企业id
     * @param storeId 门店id
     * @return 设备列表
     */
    List<DeviceDO> getByStoreId(@Param("enterpriseId") String enterpriseId, @Param("storeId") String storeId);

    /**
     * 根据门店id查询
     * @param enterpriseId 企业id
     * @param storeIds 门店id列表
     * @param storeSceneIds 场景id列表
     * @return 设备列表
     */
    List<DeviceDO> getByStoreIds(@Param("enterpriseId") String enterpriseId,
                                 @Param("storeIds") List<String> storeIds,
                                 @Param("storeSceneIds") List<Long> storeSceneIds,
                                 @Param("resourceList") List<String> resourceList);
}
