package com.coolcollege.intelligent.service.sync.qywxEvent;

import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.service.sync.event.BaseEvent;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 企业微信删除部门事件消费
 */
public class DeletePartyEvent extends BaseChatDeptEvent {


    public DeletePartyEvent(String corpId, String deptId, String appType) {
        this.corpId = corpId;
        this.deptId = deptId;
        this.appType = appType;
    }

    @Override
    public String getEventType() {
        return BaseEvent.DELETE_PARTY;
    }

    @Override
    public void doEvent() {

        String accessToken = getDkfOrQwAccessToken();
        if (StrUtil.isBlank(accessToken)) {
            return;
        }

        List<String> deptIds = Arrays.stream(deptId.replaceAll("\\[", "").replaceAll("\\]", "").split(",")).map(String::valueOf).collect(Collectors.toList());
        DataSourceHelper.changeToSpecificDataSource(String.valueOf(getDbName()));
        // 检查部门下是否存在用户，存在用户的部门不可删除

        logger.info("DeletePartyEvent doEvent, corpId:{}, eventType:{}, deleteDeptIds:{}", corpId, getEventType(), deptIds);
        if (CollectionUtils.isEmpty(deptIds)) {
            return;
        }
        try {
            SpringContextUtil.getBean("sysDepartmentService", SysDepartmentService.class).deleteByIds(deptIds, getEid());
        } catch (Exception e) {
            logger.error("del dept error, corpId={}, deptId={}", corpId, deptId, e);
        }

        SysDepartmentService sysDepartmentService = SpringContextUtil.getBean("sysDepartmentService", SysDepartmentService.class);
        List<String> allDeptIds = sysDepartmentService.selectIdList(eid);
        if (CollectionUtils.isNotEmpty(allDeptIds)) {
            sysDepartmentService.deleteByIds(deptIds, eid);
        }
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DeletePartyEvent{");
        sb.append("deptId='").append(deptId).append('\'');
        sb.append(", corpId='").append(corpId).append('\'');
        sb.append(", eid=").append(eid);
        sb.append('}');
        return sb.toString();
    }
}

