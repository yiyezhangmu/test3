package com.coolcollege.intelligent.facade.open.api.songxia;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.SongXiaDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.SongXiaSalesInfoVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.SongXiaSampleInfoVO;
import com.coolcollege.intelligent.facade.request.PageRequest;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.service.songxia.SongXiaService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = SongXiaApi.class,bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class SongXiaApiImpl implements SongXiaApi {
    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;

    @Resource
    private SongXiaService songXiaService;

    @Override
    @ShenyuSofaClient(path = "/songxia/getSalesInfo")
    public OpenApiResponseVO getSalesInfo(SongXiaDTO songXiaDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            PageDTO<SongXiaSalesInfoVO> data=songXiaService.getSalesInfo(songXiaDTO);
            return OpenApiResponseVO.success(data);
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        }catch (Exception e) {
            log.error("openApi#/songxia/getSalesInfo,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    /**
     * 松下开放api
     * @param request
     * @return
     */
    @Override
    @ShenyuSofaClient(path = "/songxia/getSampleInfo")
    public OpenApiResponseVO getSampleInfo(PageRequest request) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            PageDTO<SongXiaSampleInfoVO> data=songXiaService.getSampleInfo(request);
            return OpenApiResponseVO.success(data);
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        }catch (Exception e) {
            log.error("openApi#/songxia/getSampleInfo,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/songxia/getStockInfo")
    public OpenApiResponseVO getStockInfo(SongXiaDTO songXiaDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            PageDTO<SongXiaSampleInfoVO> data=songXiaService.getStockInfo(songXiaDTO);
            return OpenApiResponseVO.success(data);
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        }catch (Exception e) {
            log.error("openApi#/songxia/getStockInfo,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

}
