package com.coolcollege.intelligent.service.sync.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.position.PositionSourceEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.dto.OpRoleDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.dingSync.DingRoleSyncService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/10/22 15:06
 */
@Slf4j
public abstract class BaseRoleEvent extends BaseEvent {

    protected String roleInfo;

    @Override
    public void doEvent() {
        if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(appType)) {
            this.syncOnePartyRole();
            return;
        }
        SysRoleDO roleInfo = getRoleInfo();
        SysRoleService roleService = SpringContextUtil.getBean("sysRoleService", SysRoleService.class);
        DataSourceHelper.changeToSpecificDataSource(getDbName());
        roleService.insertOrUpdateRole(getEid(), roleInfo);
    }

    /**
     * 门店通：同步角色
     */
    private void syncOnePartyRole() {
        // 1.config服务获取角色列表
        EnterpriseInitConfigApiService enterpriseInitConfigApiService = SpringContextUtil.getBean("enterpriseInitConfigApiService", EnterpriseInitConfigApiService.class);
        List<OpRoleDTO> openRoleList = null;
        try {
            openRoleList = enterpriseInitConfigApiService.getRoles(corpId, appType);
        } catch (ApiException e) {
            log.error("ding sync role insertOrUpdate error corpId:{}", corpId);
            throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
        }
        // 2.切库
        DataSourceHelper.reset();
        EnterpriseConfigService enterpriseConfigService = SpringContextUtil.getBean("enterpriseConfigService", EnterpriseConfigService.class);
        EnterpriseConfigDO config = enterpriseConfigService.selectByCorpId(corpId, appType);
        if(config == null){
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        // 3.开始同步角色
        DingRoleSyncService dingRoleSyncService = SpringContextUtil.getBean("dingRoleSyncServiceImpl", DingRoleSyncService.class);
        JSONObject jsonObject = JSON.parseObject(roleInfo);
        dingRoleSyncService.syncDingOnePartyRoles(config.getEnterpriseId(), openRoleList, jsonObject.getLong("role_id"));
    }

    protected SysRoleDO getRoleInfo() {
        JSONObject jsonObject = JSON.parseObject(roleInfo);
        SysRoleDO role = new SysRoleDO();
        role.setId(jsonObject.getLong("role_id"));
        role.setRoleName(jsonObject.getString("role_name"));
        role.setIsInternal(0);
        role.setSource(PositionSourceEnum.SYNC.getValue());
        return role;
    }
}
