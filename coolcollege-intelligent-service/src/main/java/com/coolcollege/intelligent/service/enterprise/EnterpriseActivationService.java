package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.model.enterprise.dto.DeviceAuthReportPageDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseActivationPageDTO;
import com.coolcollege.intelligent.model.enterprise.vo.DeviceAuthReportVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseActivationVO;
import com.github.pagehelper.PageInfo;

public interface EnterpriseActivationService {

    /**
     * 获取分页
     * @param param
     * @return
     */
    PageInfo<EnterpriseActivationVO> getEnterpriseActivationPage(EnterpriseActivationPageDTO param);

    /**
     * 获取设备授权报表分页
     * @param param
     * @return
     */
    PageInfo<DeviceAuthReportVO> getDeviceAuthReport(DeviceAuthReportPageDTO param);
}
