package com.coolcollege.intelligent.facade.open.api;


import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseDetailUserVO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhangchenbiao
 * @FileName: DemoApiImpl
 * @Description:
 * @date 2022-07-06 14:00
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = DemoApi.class,bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class DemoApiImpl implements DemoApi{

    @Resource
    private EnterpriseUserService enterpriseUserService;
    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;

    /**
     * {
     *     "bizContent": {
     *         "enterprise": "45f92210375346858b6b6694967f44de",
     *         "userId": "123836131931284423"
     *     }
     * }
     * 参数列表的形式接收参数要求入参顺序和参数列表一致
     * @param enterpriseId
     * @param userId
     * @return
     */
    @Override
    @ShenyuSofaClient(path = "/getUserInfo")
    public OpenApiResponseVO test01(String enterpriseId, String userId) {

        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            EnterpriseDetailUserVO fullDetail = enterpriseUserService.getFullDetail(enterpriseId, userId);
            return OpenApiResponseVO.success(fullDetail);
        } catch (Exception e) {
            log.error("DemoApiImpl -> test01 has exception", e);
        }
        return OpenApiResponseVO.fail();
    }

    /**
     * {
     *     "bizContent": {
     *         "enterpriseId": "45f92210375346858b6b6694967f44de",
     *         "userId":"123836131931284423"
     *     }
     * }
     * @param param
     * @return
     */
    @Override
    @ShenyuSofaClient(path = "/getUserInfo01")
    public OpenApiResponseVO test02(EnterpriseLoginDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("enterpriseId:{}", enterpriseId);
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(param.getEnterpriseId());
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            EnterpriseDetailUserVO fullDetail = enterpriseUserService.getFullDetail(param.getEnterpriseId(), param.getUserId());
            return OpenApiResponseVO.success(fullDetail);
        } catch (ServiceException e) {
            log.error("DemoApiImpl -> test01 has exception", e);
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        }
    }

    /**
     * {
     *     "bizContent": {
     *         "enterpriseId": "45f92210375346858b6b6694967f44de",
     *         "param": {
     *             "userId":"123836131931284423"
     *         }
     *     }
     * }
     * @param enterpriseId
     * @param param
     * @return
     */
    @Override
    @ShenyuSofaClient(path = "/getUserInfo02")
    public OpenApiResponseVO test03(String enterpriseId, EnterpriseLoginDTO param) {
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            EnterpriseDetailUserVO fullDetail = enterpriseUserService.getFullDetail(enterpriseId, param.getUserId());
            return OpenApiResponseVO.success(fullDetail);
        } catch (Exception e) {
            log.error("DemoApiImpl -> test01 has exception", e);
        }
        return OpenApiResponseVO.fail();
    }
}
