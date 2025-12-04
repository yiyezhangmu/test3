package com.coolcollege.intelligent.facade.open.api.video;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.OpenApiParamCheckUtils;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiDeviceStoreDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiDeviceStoreQueryDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiVideoDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.DeviceLiveVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.DeviceVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.model.video.vo.LiveVideoVO;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.video.openapi.VideoServiceApi;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Author: huhu
 * @Date: 2025/1/8 17:43
 * @Description:
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = DeviceVideoApi.class, bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class DeviceVideoApiImpl implements DeviceVideoApi{

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;
    @Resource
    private StoreService storeService;
    @Resource
    private VideoServiceApi videoServiceApi;

    @Override
    @ShenyuSofaClient(path = "/device/video/getDeviceStore")
    public OpenApiResponseVO getDeviceStore(OpenApiDeviceStoreDTO param) {
        log.info("getDeviceStore:{}", JSONObject.toJSONString(param));
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(storeService.getDeviceStorePage(enterpriseId, param));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#device/video/getDeviceStore,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/device/video/getVideoInfo")
    public OpenApiResponseVO getVideoInfo(OpenApiVideoDTO param) {
        log.info("getVideoInfo:{}", JSONObject.toJSONString(param));
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(videoServiceApi.getVideoInfo(enterpriseId, param));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#device/video/getVideoInfo,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/device/video/getPastVideoInfo")
    public OpenApiResponseVO getPastVideoInfo(OpenApiVideoDTO param) {
        log.info("getPastVideoInfo:{}", JSONObject.toJSONString(param));
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(videoServiceApi.getPastVideoInfo(enterpriseId, param));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#device/video/getPastVideoInfo,Exception", e);
            return OpenApiResponseVO.fail();
        }

    }

    @Override
    @ShenyuSofaClient(path = "/device/video/deviceInfos")
    public OpenApiResponseVO getDeviceByStoreThirdDeptId(OpenApiDeviceStoreQueryDTO param) {
        log.info("getDeviceByStoreThirdDeptId:{}", JSONObject.toJSONString(param));
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            OpenApiParamCheckUtils.checkNecessaryParam(param.getPageNum(), param.getPageSize(), param.getThirdDeptId());
            OpenApiParamCheckUtils.checkParamLimit(param.getPageSize(), 0, 100);

            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());

            PageDTO<DeviceVO> result = storeService.getDeviceByStoreThirdDeptId(enterpriseId, param);
            return OpenApiResponseVO.success(result);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#device/video/getDeviceByStoreThirdDeptId,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/device/video/live")
    public OpenApiResponseVO getVideoLive(OpenApiVideoDTO param) {
        log.info("getVideoLive:{}", JSONObject.toJSONString(param));
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            if (StringUtils.isBlank(param.getProtocol())) {
                param.setProtocol("ezopen");
            }
            if (Objects.isNull(param.getQuality())) {
                param.setQuality(1);
            }
            LiveVideoVO result = videoServiceApi.getVideoInfo(enterpriseId, param);
            return OpenApiResponseVO.success(new DeviceLiveVO(result.getUrl()));
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#device/video/getVideoLive,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }
}
