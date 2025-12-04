package com.coolcollege.intelligent.service.sync.event;

import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseDeptEvent extends BaseEvent {


    protected String deptId;

    protected SysDepartmentDO getDepartment(String deptId, String accessToken, String corpId, String appType) {

        SysDepartmentDO deptDetail;
        try {
            deptDetail = SpringContextUtil.getBean("dingService", DingService.class).getDeptDetail(accessToken, deptId, corpId, appType);
        } catch (ApiException e) {
            log.error("getDeptDetail error, corpId={}, deptId={}", corpId, deptId);
            return null;
        }


        String eid = getEid();
        String dbName = getDbName();
        log.info("getDepartment getDbName:{},getEid:{}", dbName, eid);
        DataSourceHelper.changeToSpecificDataSource(dbName);

        String parentId = deptDetail.getParentId();
        if (parentId != null ) {
            SysDepartmentDO dept = SpringContextUtil.getBean("sysDepartmentService", SysDepartmentService.class).selectById(eid, parentId);
            if (dept == null) {
                deptDetail.setParentId(SyncConfig.ROOT_DEPT_ID_STR);
            }
        }
        return deptDetail;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }
}
