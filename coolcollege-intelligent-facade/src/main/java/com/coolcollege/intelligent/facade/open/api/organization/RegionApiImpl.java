package com.coolcollege.intelligent.facade.open.api.organization;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.OpenApiParamCheckUtils;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiAddRegionDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiRegionAuthDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiRegionDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiRemoveRegionAuthDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.UserAuthMappingVO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.service.authentication.UserAuthMappingService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/7/18 10:55
 * @Version 1.0
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = RegionApi.class,bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class RegionApiImpl implements RegionApi{

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;
    @Resource
    RegionService regionService;
    @Resource
    UserAuthMappingService userAuthMappingService;

    @Override
    @ShenyuSofaClient(path = "/region/list")
    public OpenApiResponseVO regionList(OpenApiRegionDTO openApiRegionDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(regionService.regionList(enterpriseId,openApiRegionDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#region/list,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/region/detail")
    public OpenApiResponseVO regionDetail(OpenApiRegionDTO openApiRegionDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(regionService.regionDetail(enterpriseId,openApiRegionDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#region/detail,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/region/addRegion")
    public OpenApiResponseVO addRegion(OpenApiRegionDTO openApiRegionDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            return OpenApiResponseVO.success(regionService.insertRegion(enterpriseId,openApiRegionDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#region/addRegion,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/region/insertOrUpdateRegion")
    public OpenApiResponseVO insertOrUpdateRegion(OpenApiAddRegionDTO param) {
        log.info("insertOrUpdateRegion:{}", JSONObject.toJSONString(param));
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            return OpenApiResponseVO.success(regionService.insertOrUpdateRegion(enterpriseId, param));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#region/addRegion,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/region/updateRegion")
    public OpenApiResponseVO updateRegion(OpenApiRegionDTO openApiRegionDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            return OpenApiResponseVO.success(regionService.editRegion(enterpriseId,openApiRegionDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#region/updateRegion,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/region/deleteRegion")
    public OpenApiResponseVO deleteRegion(OpenApiRegionDTO openApiRegionDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        OpenApiParamCheckUtils.checkNecessaryParam(openApiRegionDTO.getRegionId());
        try {
            return OpenApiResponseVO.success(regionService.deleteRegion(enterpriseId,String.valueOf(openApiRegionDTO.getRegionId())));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#region/deleteRegion,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/region/getRegionAuth")
    public OpenApiResponseVO getRegionAuth(OpenApiRegionAuthDTO openApiRegionAuthDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        OpenApiParamCheckUtils.checkNecessaryParam(openApiRegionAuthDTO.getUserId());
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            List<UserAuthMappingDO> userAuthMappingDOList = userAuthMappingService.listUserAuthMappingByUserId(enterpriseId, openApiRegionAuthDTO.getUserId());
            List<UserAuthMappingVO> userAuthMappingVOList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(userAuthMappingDOList)) {
                for (UserAuthMappingDO userAuthMappingDO : userAuthMappingDOList) {
                    UserAuthMappingVO userAuthMappingVO = new UserAuthMappingVO();
                    userAuthMappingVO.setUserId(userAuthMappingDO.getUserId());
                    userAuthMappingVO.setMappingId(userAuthMappingDO.getMappingId());
                    userAuthMappingVOList.add(userAuthMappingVO);
                }
            }
            return OpenApiResponseVO.success(userAuthMappingVOList);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#region/getRegionAuth,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/region/removeUserRegionAuth")
    public OpenApiResponseVO removeUserRegionAuth(OpenApiRemoveRegionAuthDTO openApiRemoveRegionAuthDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("removeUserRegionAuth:{}, request:{}", enterpriseId, JSONObject.toJSONString(openApiRemoveRegionAuthDTO));
        OpenApiParamCheckUtils.checkNecessaryParam(openApiRemoveRegionAuthDTO.getUserId(), openApiRemoveRegionAuthDTO.getMappingIdList());
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            userAuthMappingService.deleteAuthMappingByUserIdAndMappingIds(enterpriseId, openApiRemoveRegionAuthDTO.getUserId(), openApiRemoveRegionAuthDTO.getMappingIdList());
            return OpenApiResponseVO.success(true);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#region/removeUserRegionAuth,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/region/addUserRegionAuth")
    public OpenApiResponseVO addUserRegionAuth(OpenApiRemoveRegionAuthDTO openApiRemoveRegionAuthDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("addUserRegionAuth:{}, request:{}", enterpriseId, JSONObject.toJSONString(openApiRemoveRegionAuthDTO));
        OpenApiParamCheckUtils.checkNecessaryParam(openApiRemoveRegionAuthDTO.getUserId(), openApiRemoveRegionAuthDTO.getMappingIdList());
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            userAuthMappingService.addUserRegionAuth(enterpriseId, openApiRemoveRegionAuthDTO.getUserId(), openApiRemoveRegionAuthDTO.getMappingIdList());
            return OpenApiResponseVO.success(true);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#region/addUserRegionAuth,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }
}
