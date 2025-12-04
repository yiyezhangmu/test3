package com.coolcollege.intelligent.facade.open.api.organization;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiAddRoleDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiDeleteRolesDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: hu hu
 * @Date: 2025/1/8 11:23
 * @Description:
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = RoleApi.class, bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class RoleApiImpl implements RoleApi {

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;
    @Resource
    private EnterpriseSettingService enterpriseSettingService;
    @Resource
    private SysRoleService sysRoleService;

    @Override
    @ShenyuSofaClient(path = "/role/insertOrUpdateRole")
    public OpenApiResponseVO insertOrUpdateRole(OpenApiAddRoleDTO param) {
        log.info("insertOrUpdateRole:{}", JSONObject.toJSONString(param));
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(sysRoleService.insertOrUpdateSysRole(enterpriseId, param));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#role/insertOrUpdateRole,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/role/deleteRoles")
    public OpenApiResponseVO deleteRoles(OpenApiDeleteRolesDTO param) {
        log.info("deleteRoles:{}", JSONObject.toJSONString(param));
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            DataSourceHelper.reset();
            Boolean enableDingSync = Objects.equals(enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId).getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN) ||
                    Objects.equals(enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId).getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD);
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            if(!param.check()){
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
            }
            List<SysRoleDO> roleList = sysRoleService.getRoleIdByThirdUniqueIds(enterpriseId, param.getThirdUniqueIds());
            List<Long> ids = roleList.stream().map(SysRoleDO::getId).collect(Collectors.toList());
            return OpenApiResponseVO.success(sysRoleService.batchDeleteRoles(enterpriseId, "syncUser", ids, enableDingSync));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#role/deleteRoles,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }
}
