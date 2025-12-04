package com.coolcollege.intelligent.service.sync.qywxEvent;

import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.service.sync.event.BaseEvent;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class UpdatePartyEvent extends BaseChatDeptEvent {


    public UpdatePartyEvent(String corpId, String deptId, String appType) {
        this.corpId = corpId;
        this.deptId = deptId;
        this.appType = appType;
    }

    @Override
    public String getEventType() {
        return BaseEvent.UPDATE_PARTY;
    }

    @Override
    public void doEvent() {

        long dpId = Long.parseLong(deptId);
        RedisUtilPool redisUtilPool = SpringContextUtil.getBean("redisUtilPool", RedisUtilPool.class);
        String corpSecret =  redisUtilPool.getString(corpId);
        String accessToken = getPyAccessToken(corpSecret);
        if (StrUtil.isBlank(accessToken)) {
            return;
        }
        SysDepartmentDO deptDetail = null;
        try {
            deptDetail = getDepartment(dpId, accessToken);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (deptDetail == null) {
            return;
        }

        DataSourceHelper.changeToSpecificDataSource(String.valueOf(getDbName()));
        SysDepartmentService sysDepartmentService = SpringContextUtil.getBean("sysDepartmentService", SysDepartmentService.class);
        try {
            List<SysDepartmentDO> sysDepartments = Lists.newArrayList();
            sysDepartments.add(deptDetail);
            sysDepartmentService.batchInsertOrUpdate(sysDepartments, getEid());
        } catch (Exception e) {
            log.error("update dept error, corpId={}, deptId={}", corpId, deptId, e);
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UpdatePartyEvent{");
        sb.append("deptId='").append(deptId).append('\'');
        sb.append(", corpId='").append(corpId).append('\'');
        sb.append(", eid=").append(eid);
        sb.append('}');
        return sb.toString();
    }
}
