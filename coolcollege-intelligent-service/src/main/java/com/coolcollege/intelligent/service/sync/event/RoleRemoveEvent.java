package com.coolcollege.intelligent.service.sync.event;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.model.department.dto.DingDepartmentQueryDTO;
import com.coolcollege.intelligent.model.department.dto.MonitorDeptTypeDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/10/22 15:14
 */
@Slf4j
public class RoleRemoveEvent extends BaseRoleEvent {

    public RoleRemoveEvent(String corpId, String roleInfo, String appType) {
        this.corpId = corpId;
        this.roleInfo = roleInfo;
        this.appType = appType;
    }

    @Override
    public String getEventType() {
        return BaseEvent.ROLE_REMOVE;
    }

    @Override
    public void doEvent() {

        SysRoleDO roleInfo = getRoleInfo();
        Long id = roleInfo.getId();
        SysRoleService roleService = SpringContextUtil.getBean("sysRoleService", SysRoleService.class);
        DataSourceHelper.changeToSpecificDataSource(getDbName());
        roleService.deleteRoles(getEid(), id);
        if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(appType)) {
            return;
        }

        RedisUtilPool redisUtil = SpringContextUtil.getBean("redisUtilPool", RedisUtilPool.class);
        String departmentDTO = redisUtil.getString("departmentDTO" + eid);
        if (StringUtils.isEmpty(departmentDTO)) {
            return;
        }
        DingDepartmentQueryDTO departmentQuery = JSONObject.parseObject(departmentDTO, DingDepartmentQueryDTO.class);
        MonitorDeptTypeDTO clerk = departmentQuery.getClerk();
        List<Long> clerkIds = clerk.getIds();
        if (CollUtil.isNotEmpty(clerkIds)) {
            clerkIds.remove(id);
        }

        MonitorDeptTypeDTO operator = departmentQuery.getOperator();
        List<Long> operatorIds = operator.getIds();
        if (CollUtil.isNotEmpty(operatorIds)) {
            operatorIds.remove(id);
        }

        MonitorDeptTypeDTO shopowner = departmentQuery.getShopowner();
        List<Long> shopownerIds = shopowner.getIds();
        if (CollUtil.isNotEmpty(shopownerIds)) {
            shopownerIds.remove(id);
        }
        log.info("更新缓存为：{}", departmentQuery);
        redisUtil.setString("departmentDTO" + eid, JSON.toJSONString(departmentQuery));

    }
}
