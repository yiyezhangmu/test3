package com.coolcollege.intelligent.service.sync.qywxEvent;

import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.sync.event.BaseEvent;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseChatDeptEvent extends BaseEvent {


    protected String deptId;

    protected SysDepartmentDO getDepartment(long deptId, String accessToken) {

        SysDepartmentDO deptDetail;
        ChatService chatService = SpringContextUtil.getBean("chatService", ChatService.class);
        try {
            deptDetail = chatService.getDeptDetail(accessToken, deptId);
        } catch (ApiException e) {
            log.error("getDeptDetail error, corpId={}, deptId={}", corpId, deptId, e);
            return null;
        }
        DataSourceHelper.changeToSpecificDataSource(String.valueOf(getDbName()));
        Set<String> deptIdSet = SpringContextUtil.getBean("sysDepartmentService", SysDepartmentService.class).selectAllDepts(getEid()).stream().map(d -> d.getId()).collect(Collectors.toSet());

        String parentId = deptDetail.getParentId();
        if (parentId != null && !deptIdSet.contains(parentId)) {
            deptDetail.setParentId(SyncConfig.ROOT_DEPT_ID_STR);
        }
        return deptDetail;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }
}
