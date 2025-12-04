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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 企业微信部门监听事件消费
 */
@Slf4j
public class CreatePartyEvent extends BaseChatDeptEvent {


    public CreatePartyEvent(String corpId, String deptId, String appType) {
        this.corpId = corpId;
        this.deptId = deptId;
        this.appType = appType;
    }

    @Override
    public String getEventType() {
        return BaseEvent.CREATE_PARTY;
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

        DeptReleatedInfo deptReleatedInfo = new DeptReleatedInfo(deptDetail);

        try {
            addDeptReleatedInfo(deptReleatedInfo);
        } catch (Exception e) {
            log.error("add deptReleated info error, corpId={}, deptId={}", corpId, deptId, e);
        }
    }



    @Transactional
    private void addDeptReleatedInfo(DeptReleatedInfo deptReleatedInfo) {

        DataSourceHelper.changeToSpecificDataSource(String.valueOf(getDbName()));
        if (deptReleatedInfo.getDepartment() != null) {
            List<SysDepartmentDO> sysDepartments = Lists.newArrayList();
            sysDepartments.add(deptReleatedInfo.getDepartment());
            SpringContextUtil.getBean("sysDepartmentService", SysDepartmentService.class).batchInsertOrUpdate(sysDepartments, getEid());
        }

    }


    class DeptReleatedInfo {

        private SysDepartmentDO department;

        public DeptReleatedInfo(SysDepartmentDO department) {
            this.department = department;
        }

        public SysDepartmentDO getDepartment() {
            return department;
        }

        public void setDepartment(SysDepartmentDO department) {
            this.department = department;
        }

    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DeptCreateEvent{");
        sb.append("deptId='").append(deptId).append('\'');
        sb.append(", corpId='").append(corpId).append('\'');
        sb.append(", eid=").append(eid);
        sb.append('}');
        return sb.toString();
    }
}

