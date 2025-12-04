package com.coolcollege.intelligent.service.sync.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.system.SysRoleQueryDTO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wxp
 * @date 2023/4/1 15:14
 */
@Slf4j
public class OpRoleAddRemoveUserEvent extends BaseRoleEvent {

    public static final String ADD_USER = "add_user";
    public static final String REMOVE_USER = "remove_user";

    public OpRoleAddRemoveUserEvent(String corpId, String roleInfo, String appType) {
        this.corpId = corpId;
        this.roleInfo = roleInfo;
        this.appType = appType;
    }

    @Override
    public String getEventType() {
        return BaseEvent.OP_ROLE_ADD_REMOVE_USER;
    }

    @Override
    public void doEvent() {
        log.info("OpRoleAddRemoveUserEvent=====roleInfo===={}", roleInfo);
        JSONObject jsonObject = JSON.parseObject(roleInfo);
        Long roleId = jsonObject.getLong("role_id");
        String type = jsonObject.getString("type");
        JSONArray jsonArray = jsonObject.getJSONArray("user_id_list");
        List<String> userIdList = ListUtils.emptyIfNull(jsonArray).stream()
                .map(String.class::cast)
                .collect(Collectors.toList());
        DataSourceHelper.reset();
        EnterpriseConfigService enterpriseConfigService = SpringContextUtil.getBean("enterpriseConfigService", EnterpriseConfigService.class);
        EnterpriseConfigDO config = enterpriseConfigService.selectByCorpId(corpId, appType);
        if(config == null){
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        SysRoleService roleService = SpringContextUtil.getBean("sysRoleService", SysRoleService.class);
        if (roleId != null && CollectionUtils.isNotEmpty(userIdList)) {
            if(ADD_USER.equals(type)){
                SysRoleQueryDTO sysRoleQueryDTO = SysRoleQueryDTO.builder().roleId(roleId.toString()).build();
                sysRoleQueryDTO.setUserIds(userIdList);
                roleService.addPersonToUser(config.getEnterpriseId(), sysRoleQueryDTO, false);
            }else if(REMOVE_USER.equals(type)){
                roleService.deletePersonToUser(config.getEnterpriseId(), roleId, userIdList);
            }
        }
    }
}
