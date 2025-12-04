package com.coolcollege.intelligent.dao.store;

import com.coolcollege.intelligent.model.store.StoreDeviceMappingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shoul
 */
@Mapper
public interface StoreDeviceMappingMapper {

    /**
     * 批量删除门店关联设备映射数据
     * @Title batchDeleteDevicesMappingByStoreIds
     * @Description 批量删除门店关联设备映射数据
     * @param enterpriseId 企业id
     * @param storeIds 门店id
     * @return
     */
    Integer batchDeleteDevicesMappingByStoreIds(@Param("enterpriseId") String enterpriseId,
                                                @Param("storeIds") List<String> storeIds);


    /**
     * 根据门店id获取关联设备映射数据
     * @Title batchGetDeviceByStoreIds
     * @Description 根据门店id获取关联设备映射数据
     * @param enterpriseId 企业id
     * @param storeIds 门店ids
     * @return
     */
    List<StoreDeviceMappingDO> batchGetDeviceByStoreIds(@Param("enterpriseId") String enterpriseId,
                                                        @Param("storeIds") List<String> storeIds);


    /**
     * 根据设备id获取关联设备映射数据
     * @Title getDevicesMappingByDeviceId
     * @Description 根据设备id获取关联设备映射数据
     * @param enterpriseId 企业id
     * @param deviceId 设备id
     * @return
     */
    StoreDeviceMappingDO getDevicesMappingByDeviceId(@Param("enterpriseId") String enterpriseId,
                                                     @Param("deviceId") String deviceId);

    /**
     * 新增门店关联设备映射
     * @Title addStoreDeviceMapping
     * @Description 新增门店关联设备映射
     * @param enterpriseId 企业id
     * @param storeDeviceMappingDO 门店关联设备映射
     * @return
     */
    void addStoreDeviceMapping(@Param("enterpriseId") String enterpriseId,
                               @Param("storeDeviceMappingDO") StoreDeviceMappingDO storeDeviceMappingDO);

    /**
     * 更新门店关联设备映射
     * @Title updateDevicesMappingByStoreId
     * @Description 更新门店关联设备映射
     * @param enterpriseId 企业id
     * @param storeId 门店id
     * @param deviceId 设备id
     * @return
     */
    Integer updateDevicesMappingByStoreId(@Param("enterpriseId") String enterpriseId,
                                          @Param("storeId") String storeId,
                                          @Param("deviceId") String deviceId);

    /**
     * 批量新增门店关联设备映射数据
     * @Title batchInsertDeviceMapping
     * @Description 批量新增门店关联设备映射数据
     * @param enterpriseId 企业id
     * @param storeDeviceMappings 门店关联设备映射
     * @return
     */
    Integer batchInsertDeviceMapping(@Param("enterpriseId") String enterpriseId,
                                     @Param("storeDeviceMappings") List<StoreDeviceMappingDO> storeDeviceMappings);

    /**
     * 查询门店打卡组
     * @param enterpriseId
     * @param storeId
     * @return
     */
    StoreDeviceMappingDO getDeviceMapping(@Param("enterpriseId") String enterpriseId,
                                          @Param("storeId") String storeId);

    /**
     * 查询门店打卡组
     * @param enterpriseId
     * @param storeIds
     * @return
     */
    List<StoreDeviceMappingDO> getSyncDeviceMapping(@Param("enterpriseId") String enterpriseId,
                                                    @Param("storeIds") List<String> storeIds,
                                                    @Param("dingIds") List<String> dingIds);

    /**
     * 更新锁定状态
     * @param enterpriseId
     * @param storeId
     * @param newDeviceId
     * @param lockFlag
     * @return
     */
    int updateLockFlag(@Param("enterpriseId") String enterpriseId, @Param("storeId") String storeId, @Param("newDeviceId") String newDeviceId, @Param("lockFlag") Integer lockFlag);
}