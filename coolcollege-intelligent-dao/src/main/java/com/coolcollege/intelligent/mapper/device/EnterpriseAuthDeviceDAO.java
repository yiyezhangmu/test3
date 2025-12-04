package com.coolcollege.intelligent.mapper.device;

import com.coolcollege.intelligent.dao.device.EnterpriseAuthDeviceMapper;
import com.coolcollege.intelligent.model.device.EnterpriseAuthDeviceDO;
import com.coolcollege.intelligent.model.device.dto.DeviceAuthPageDTO;
import com.coolcollege.intelligent.model.device.request.OpenDevicePageRequest;
import com.coolcollege.intelligent.model.device.vo.OpenDevicePageVO;
import com.coolcollege.intelligent.model.enterprise.dto.DeviceAuthReportPageDTO;
import com.coolcollege.intelligent.model.enterprise.vo.DeviceAuthReportVO;
import com.github.pagehelper.Page;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class EnterpriseAuthDeviceDAO {

    @Resource
    private EnterpriseAuthDeviceMapper enterpriseAuthDeviceMapper;

    public List<EnterpriseAuthDeviceDO> getAuthDeviceList(String enterpriseId, String deviceId, String channelNo, String appId) {
        return enterpriseAuthDeviceMapper.getAuthDeviceList(enterpriseId, deviceId, channelNo, appId);
    }

    public List<EnterpriseAuthDeviceDO> getAuthDeviceListByDeviceIds(String enterpriseId, List<String> deviceIds) {
        return enterpriseAuthDeviceMapper.getAuthDeviceListByDeviceIds(enterpriseId, deviceIds);
    }

    public void insert(EnterpriseAuthDeviceDO authDevice) {
        enterpriseAuthDeviceMapper.insert(authDevice);
    }

    public Integer cancelAuth(List<EnterpriseAuthDeviceDO> cancelList) {
        return enterpriseAuthDeviceMapper.cancelAuth(cancelList);
    }

    public List<EnterpriseAuthDeviceDO> getAuthDeviceByAppId(String deviceId, String channelNo, String appId) {
        return enterpriseAuthDeviceMapper.getAuthDeviceByAppId(deviceId, channelNo, appId);
    }

    public List<EnterpriseAuthDeviceDO> getAuthDeviceByThirdStoreId(String thirdStoreId, String appId) {
        return enterpriseAuthDeviceMapper.getAuthDeviceByThirdStoreId(thirdStoreId, appId);
    }

    public List<EnterpriseAuthDeviceDO> getAuthDeviceByEnterpriseId(String enterpriseId, String appId) {
        return enterpriseAuthDeviceMapper.getAuthDeviceByEnterpriseId(enterpriseId, appId);
    }

    public List<EnterpriseAuthDeviceDO> getDeviceAuthPage(String enterpriseId, DeviceAuthPageDTO param) {
        return enterpriseAuthDeviceMapper.getDeviceAuthPage(enterpriseId, param);
    }

    public Page<DeviceAuthReportVO> getDeviceAuthReport(DeviceAuthReportPageDTO param) {
        return enterpriseAuthDeviceMapper.getDeviceAuthReport(param);
    }

    public List<DeviceAuthReportVO> getEnterpriseCallNum(List<String>enterpriseIds, String queryDate){
        return enterpriseAuthDeviceMapper.getEnterpriseCallNum(enterpriseIds, queryDate);
    }

    public List<OpenDevicePageVO> getDevicePage(OpenDevicePageRequest request) {
        return enterpriseAuthDeviceMapper.getDevicePage(request);
    }
}
