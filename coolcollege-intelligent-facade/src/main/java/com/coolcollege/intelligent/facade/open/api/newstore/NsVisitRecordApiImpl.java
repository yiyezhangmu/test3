package com.coolcollege.intelligent.facade.open.api.newstore;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.facade.dto.openApi.NsVisitRecordDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.service.newstore.NsVisitRecordService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: hu hu
 * @Date: 2025/1/15 10:08
 * @Description:
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = NsVisitRecordApi.class, bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class NsVisitRecordApiImpl implements NsVisitRecordApi {

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;
    @Resource
    private NsVisitRecordService nsVisitRecordService;

    @Override
    @ShenyuSofaClient(path = "/nsVisitRecord/getVisitRecordList")
    public OpenApiResponseVO getVisitRecordList(NsVisitRecordDTO param) {
        log.info("getVisitRecordList:{}", JSONObject.toJSONString(param));
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(nsVisitRecordService.getVisitRecordList(enterpriseId, param));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#nsVisitRecord/getVisitRecordList,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }
}
