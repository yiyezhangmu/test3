package com.coolcollege.intelligent.facade.video.impl;

import com.alipay.sofa.runtime.api.annotation.SofaMethod;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.ResultDTO;
import com.coolcollege.intelligent.facade.video.VideoDeviceFacade;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.passengerflow.JieFengApiService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * describe: 视频监控设备
 *
 * @author wangff
 * @date 2024/11/29
 */
@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.VIDEO_DEVICE_CHECK_DOWNLOAD_STATUS_UNIQUE_ID,
        interfaceType = VideoDeviceFacade.class,
        bindings = {@SofaServiceBinding(bindingType = IntelligentFacadeConstants.SOFA_BINDING_TYPE)})
@Component
@RequiredArgsConstructor
public class VideoDeviceFacadeImpl implements VideoDeviceFacade {

    private final EnterpriseConfigService enterpriseConfigService;

    private final DeviceService deviceService;

    private final JieFengApiService jieFengApiService;


    @Override
    public ResultDTO checkVideoDownloadStatus(String enterpriseId) {
        DataSourceHelper.reset();
        try {
            EnterpriseConfigDO configDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
            if (Objects.isNull(configDO)) {
                log.info("enterpriseId：{}，企业信息不存在, enterpriseConfigDO", enterpriseId);
                throw new ServiceException(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
            }
            DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
            deviceService.checkAndUpdateYingShiVideoDownloadTaskStatus(enterpriseId);
        } catch (Exception e) {
            log.error("视频监控设备下载中心下载状态更新失败", e);
        }
        return ResultDTO.SuccessResult();
    }

    @Override
    public ResultDTO addAllStoreNode(String enterpriseId) {
        DataSourceHelper.reset();
        try {
            EnterpriseConfigDO configDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
            if (Objects.isNull(configDO)) {
                log.info("enterpriseId：{}，企业信息不存在, enterpriseConfigDO", enterpriseId);
                throw new ServiceException(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
            }
            DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
            jieFengApiService.addAllStoreNode(enterpriseId);
        } catch (Exception e) {
            log.error("addAllStoreNode#error", e);
        }
        return ResultDTO.SuccessResult();
    }

    @Override
    public ResultDTO getAllPassengerFlow(String enterpriseId, String beginTime, String endTime) {
        DataSourceHelper.reset();
        try {
            EnterpriseConfigDO configDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
            if (Objects.isNull(configDO)) {
                log.info("enterpriseId：{}，企业信息不存在, enterpriseConfigDO", enterpriseId);
                throw new ServiceException(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
            }
            DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
            jieFengApiService.getAllPassengerFlow(enterpriseId, beginTime, endTime);
        } catch (Exception e) {
            log.error("getAllPassengerFlow#error", e);
        }
        return ResultDTO.SuccessResult();
    }
}
