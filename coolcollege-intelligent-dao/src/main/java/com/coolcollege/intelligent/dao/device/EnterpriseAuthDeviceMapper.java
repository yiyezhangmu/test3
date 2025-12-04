package com.coolcollege.intelligent.dao.device;

import com.coolcollege.intelligent.model.device.EnterpriseAuthDeviceDO;
import com.coolcollege.intelligent.model.device.dto.DeviceAuthPageDTO;
import com.coolcollege.intelligent.model.device.request.OpenDevicePageRequest;
import com.coolcollege.intelligent.model.device.vo.OpenDevicePageVO;
import com.coolcollege.intelligent.model.enterprise.dto.DeviceAuthReportPageDTO;
import com.coolcollege.intelligent.model.enterprise.vo.DeviceAuthReportVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-07-01 03:43
 */
public interface EnterpriseAuthDeviceMapper {


    /**
     * 获取设备授权信息
     * @param enterpriseId
     * @param deviceId
     * @param channelNo
     * @return
     */
    List<EnterpriseAuthDeviceDO> getAuthDeviceList(@Param("enterpriseId") String enterpriseId, @Param("deviceId") String deviceId, @Param("channelNo") String channelNo, @Param("appId")String appId);

    /**
     * 插入设备授权信息
     * @param authDevice
     * @return
     */
    Integer insert(@Param("authDevice") EnterpriseAuthDeviceDO authDevice);

    /**
     * 取消设备授权
     * @param cancelList
     */
    Integer cancelAuth(@Param("cancelList") List<EnterpriseAuthDeviceDO> cancelList);

    /**
     * 根据appId获取设备授权信息
     * @param deviceId
     * @param channelNo
     * @param appId
     * @return
     */
    List<EnterpriseAuthDeviceDO> getAuthDeviceByAppId(@Param("deviceId") String deviceId, @Param("channelNo") String channelNo, @Param("appId") String appId);

    /**
     * 根据第三方门店Id获取设备授权信息
     * @param thirdStoreId
     * @param appId
     * @return
     */
    List<EnterpriseAuthDeviceDO> getAuthDeviceByThirdStoreId(@Param("thirdStoreId") String thirdStoreId, @Param("appId") String appId);

    /**
     * 根据企业Id获取设备授权信息
     * @param enterpriseId
     * @param appId
     * @return
     */
    List<EnterpriseAuthDeviceDO> getAuthDeviceByEnterpriseId(@Param("enterpriseId") String enterpriseId, @Param("appId") String appId);

    /**
     * 获取设备授权信息
     * @param enterpriseId
     * @param param
     * @return
     */
    List<EnterpriseAuthDeviceDO> getDeviceAuthPage(@Param("enterpriseId") String enterpriseId, @Param("param") DeviceAuthPageDTO param);

    /**
     * 根据设备Id获取设备授权信息
     * @param enterpriseId
     * @param deviceIds
     * @return
     */
    List<EnterpriseAuthDeviceDO> getAuthDeviceListByDeviceIds(@Param("enterpriseId") String enterpriseId, @Param("deviceIds") List<String> deviceIds);

    /**
     *
     * @param param
     * @return
     */
    Page<DeviceAuthReportVO> getDeviceAuthReport(@Param("param") DeviceAuthReportPageDTO param);

    /**
     * 获取企业调用次数
     * @param enterpriseIds
     * @param queryDate
     * @return
     */
    List<DeviceAuthReportVO> getEnterpriseCallNum(@Param("enterpriseIds")List<String>enterpriseIds, @Param("queryDate") String queryDate);

    /**
     * 获取设备授权信息
     * @param request
     * @return
     */
    List<OpenDevicePageVO> getDevicePage(@Param("param") OpenDevicePageRequest request);
}