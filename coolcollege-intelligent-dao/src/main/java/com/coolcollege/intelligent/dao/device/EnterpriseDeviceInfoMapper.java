package com.coolcollege.intelligent.dao.device;


import com.coolcollege.intelligent.model.device.EnterpriseDeviceInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-04-28 07:20
 */
public interface EnterpriseDeviceInfoMapper {

    /**
     * 批量插入
     * @param recordList
     */
    void batchInsertOrUpdate(@Param("recordList") List<EnterpriseDeviceInfoDO> recordList);

    /**
     * 批量插入
     * @param recordList
     */
    void batchInsertOrUpdateV2(@Param("recordList") List<EnterpriseDeviceInfoDO> recordList);

    /**
     * 根据设备id获取企业id
     * @param deviceId
     * @return
     */
    List<String> getEnterpriseIdsByDeviceId(@Param("deviceId") String deviceId);


    List<EnterpriseDeviceInfoDO> getEnterpriseIdsByDeviceIdV2(@Param("enterpriseId")String enterpriseId, @Param("parentDeviceId")String parentDeviceId);

    List<EnterpriseDeviceInfoDO> getEnterpriseIdsByDeviceIdV3(@Param("deviceId")String deviceId);
}