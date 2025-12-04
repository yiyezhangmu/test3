package com.coolcollege.intelligent.facade.open.api.organization;

import com.alibaba.fastjson.JSON;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiStoreLicenseRequest;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.license.LicenseDetailVO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.rpc.license.LicenseApiService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/7/25 9:55
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = LicenseApi.class,bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class LicenseApiImpl implements LicenseApi{

    @Resource
    private LicenseApiService licenseApiService;

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;

    @Resource
    private StoreService storeService;

    //添加证照
    @Override
    @ShenyuSofaClient(path = "/license/addLicense")
    public OpenApiResponseVO addLicense(OpenApiStoreLicenseRequest request) {
        log.info("openApi#/license/addLicense, request:{}", JSON.toJSON(request));
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(licenseApiService.batchSaveStoreLicenseInstance(request.getRequestDTOS(),enterpriseConfig.getDbName(),enterpriseId));
        }catch (ServiceException e){
            log.info("openApi#/license/addLicense,ServiceException", e);
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#/license/addLicense,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    //获取证照类型详情
    @Override
    @ShenyuSofaClient(path = "/license/getLicense")
    public OpenApiResponseVO getLicense() {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            List<LicenseDetailVO> storeLicenseDetail = storeService.getStoreLicenseDetail(enterpriseId);
            return OpenApiResponseVO.success(storeLicenseDetail);
        }catch (ServiceException e){
            log.info("openApi#/license/getLicense,ServiceException", e);
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#/license/getLicense,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }


}
