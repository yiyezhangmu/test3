package com.coolcollege.intelligent.facade.open.api.organization;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.facade.dto.openApi.*;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/7/19 10:45
 * @Version 1.0
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = StoreApi.class,bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class StoreApiImpl implements StoreApi{

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;
    @Resource
    StoreService storeService;
    @Resource
    RegionService regionService;

    @Override
    @ShenyuSofaClient(path = "/store/list")
    public OpenApiResponseVO storeList(OpenApiStoreDTO openApiStoreDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(storeService.getStoreList(enterpriseId,openApiStoreDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#/store/list,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/store/listIncreaseStore")
    public OpenApiResponseVO listIncreaseStore(OpenApiStoreDTO openApiStoreDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            if(openApiStoreDTO.getBeginTime() == null){
                Long beginTime = DateUtils.addHours(new Date(), -48).getTime();
                openApiStoreDTO.setBeginTime(beginTime);
            }
            return OpenApiResponseVO.success(storeService.getStoreList(enterpriseId,openApiStoreDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#/store/list,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/store/detail")
    public OpenApiResponseVO storeDetail(OpenApiStoreDTO openApiStoreDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(storeService.getStoreDetail(enterpriseId,openApiStoreDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#/store/detail,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/store/addStore")
    public OpenApiResponseVO addStore(OpenApiAddStoreDTO openApiAddStoreDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(storeService.addStore(enterpriseId,openApiAddStoreDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#user/addStore,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/store/insertOrUpdateStore")
    public OpenApiResponseVO insertOrUpdateStore(OpenApiInsertOrUpdateStoreDTO openApiAddStoreDTO) {
        log.info("insertOrUpdateStore:{}", JSONObject.toJSONString(openApiAddStoreDTO));
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(storeService.insertOrUpdateStore(enterpriseId, openApiAddStoreDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#user/addStore,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/store/updateStore")
    public OpenApiResponseVO updateStore(OpenApiAddStoreDTO openApiAddStoreDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(storeService.editStore(enterpriseId,openApiAddStoreDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#user/updateStore,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/store/updateStoreInfo")
    public OpenApiResponseVO updateStoreInfo(OpenApiUpdateStoreDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("enterpriseId：{}, updateStoreInfo:{}", enterpriseId, JSONObject.toJSONString(param));
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(storeService.updateStoreInfo(enterpriseId, param));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#user/updateStore,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/store/remove")
    public OpenApiResponseVO removeStore(OpenApiAddStoreDTO openApiAddStoreDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            regionService.deleteRegionByStoreId(enterpriseId, openApiAddStoreDTO.getStoreId(), "system");
            return OpenApiResponseVO.success(null);
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        }catch (Exception e) {
            log.error("openApi#/store/remove,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }


    /**
     * 添加门店分组
     * @param openApiStoreGroupDTO
     * @return
     */
    @Override
    @ShenyuSofaClient(path = "/store/addStoreGroup")
    public OpenApiResponseVO addStoreGroup(OpenApiStoreGroupDTO openApiStoreGroupDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(storeService.addOpenApiStoreGroup(enterpriseId,openApiStoreGroupDTO).getData());
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        }catch (Exception e) {
            log.error("openApi#/store/addStoreGroup,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    /**
     * 删除门店分组
     * @param openApiStoreGroupDTO
     * @return
     */
    @Override
    @ShenyuSofaClient(path = "/store/removeStoreGroup")
    public OpenApiResponseVO removeStoreGroup(OpenApiStoreGroupDTO openApiStoreGroupDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            StoreGroupDO storeGroupDO = new StoreGroupDO();
            storeGroupDO.setGroupId(openApiStoreGroupDTO.getGroupId());
            return OpenApiResponseVO.success(storeService.deleteStoreGroupForOpenApi(enterpriseId, storeGroupDO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        }catch (Exception e) {
            log.error("openApi#/store/removeStoreGroup,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    /**
     * 更新门店分组
     * @param openApiStoreGroupDTO
     * @return
     */
    @Override
    @ShenyuSofaClient(path = "/store/updateStoreGroup")
    public OpenApiResponseVO updateStoreGroup(OpenApiStoreGroupDTO openApiStoreGroupDTO){
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            List<String> strings = storeService.updateOpenApiStoreGroup(enterpriseId, openApiStoreGroupDTO);
            log.info("cccc{}",strings);
            return OpenApiResponseVO.success(strings);
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#/store/updateStoreGroup,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    /**
     * 查询门店分组列表
     * @param openApiStoreGroupDTO
     * @return
     */
    @Override
    @ShenyuSofaClient(path = "/store/storeGroupList")
    public OpenApiResponseVO storeGroupList(OpenApiStoreGroupDTO openApiStoreGroupDTO){
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(storeService.getOpenApiStoreGroupList(enterpriseId,openApiStoreGroupDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#/store/storeGroupList,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }
    /**
     * 查询门店分组详情
     * @param openApiStoreGroupDTO
     * @return
     */
    @Override
    @ShenyuSofaClient(path = "/store/getStoreGroupInfo")
    public OpenApiResponseVO getGroupInfo(OpenApiStoreGroupDTO openApiStoreGroupDTO){
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(storeService.getOpenApiGroupInfo(enterpriseId,openApiStoreGroupDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#/store/getStoreGroupInfo,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/store/addXfsgStore")
    public OpenApiResponseVO addXfsgStore(XfsgAddStoreDTO xfsgAddStoreDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(storeService.addXfsgStore(enterpriseId, xfsgAddStoreDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#store/addXfsgStore,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/store/transferXfsgStore")
    public OpenApiResponseVO transferXfsgStore(XfsgTransferStoreDTO xfsgTransferStoreDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(storeService.transferXfsgStore(enterpriseId, xfsgTransferStoreDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#store/transferXfsgStore,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

}
