package com.coolcollege.intelligent.service.sync.event;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.model.department.dto.DingDepartmentQueryDTO;
import com.coolcollege.intelligent.model.department.dto.MonitorDeptDTO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enums.StoreIsDeleteEnum;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.sync.service.AutoSyncService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class DeptCreateEvent extends BaseDeptEvent {


    public DeptCreateEvent(String corpId, String deptId) {
        this.corpId = corpId;
        this.deptId = deptId;
    }

    @Override
    public String getEventType() {
        return BaseEvent.ORG_DEPT_CREATE;
    }

    @Override
    public void doEvent() {

        log.info("SysDepartmentDO deptId:{}", deptId);
        JSONArray jsonArray = JSONArray.parseArray(deptId);

        String dpId = jsonArray.getString(0);

        String accessToken = getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return;
        }
        SysDepartmentDO deptDetail = null;
        try {
            deptDetail = getDepartment(dpId, accessToken, corpId, AppTypeEnum.DING_DING.getValue());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (deptDetail == null) {
            return;
        }

        log.info("SysDepartmentDO deptDetail:{}", deptDetail.getName());
        String dbName = getDbName();
        log.info("dbName++++++++++++" + dbName);
        DeptReleatedInfo deptReleatedInfo = new DeptReleatedInfo(deptDetail);

        try {
            log.info("企业id为{}", eid);

            DataSourceHelper.changeToSpecificDataSource(dbName);
            addDeptReleatedInfo(deptReleatedInfo);
            if (getEnableDingSync()) {
                //删除部门需要清理缓存
                RedisUtilPool redisUtil = SpringContextUtil.getBean("redisUtilPool", RedisUtilPool.class);
                if (StringUtils.isNotEmpty(redisUtil.getString("syncDingStore" + eid))) {
                    Long second = redisUtil.getExpire("syncDingStore" + eid);
                    log.info("线程处于同步中，在等待。。。。");
                    Thread.sleep(second * 1000L);
                }
                DataSourceHelper.changeToSpecificDataSource(dbName);
                this.addStoreAndPosition(eid, deptDetail, redisUtil, accessToken);
            }
        } catch (Exception e) {
            log.error("add deptReleated info error, corpId={}, deptId={}", corpId, deptId, e);
        }
    }

    public void addDeptReleatedInfo(DeptReleatedInfo deptReleatedInfo) {

        String eid = getEid();
        String dbName = getDbName();

        log.info("addDeptReleatedInfo into db_name:{},eid:{}", getDbName(), eid);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        if (deptReleatedInfo.getDepartment() != null) {
            List<SysDepartmentDO> sysDepartmentDOList = Lists.newArrayList();
            sysDepartmentDOList.add(deptReleatedInfo.getDepartment());
            SpringContextUtil.getBean("sysDepartmentService", SysDepartmentService.class).batchInsertOrUpdate(sysDepartmentDOList, eid);
        }

        /*if (CollectionUtils.isNotEmpty(deptReleatedInfo.getCourseVisibleMappings())) {
            SpringContextUtil.getBean("deptCourseVisibleMappingService", DeptCourseVisibleMappingService.class).batchInsertOrUpdate(deptReleatedInfo.getCourseVisibleMappings());
        }*/
    }


    class DeptReleatedInfo {

        private SysDepartmentDO department;
        //private List<DeptCourseVisibleMapping> courseVisibleMappings;

        public DeptReleatedInfo(SysDepartmentDO department) {
            this.department = department;
        }

        public SysDepartmentDO getDepartment() {
            return department;
        }

        public void setDepartment(SysDepartmentDO department) {
            this.department = department;
        }

        /*public List<DeptCourseVisibleMapping> getCourseVisibleMappings() {
            return courseVisibleMappings;
        }*/

        /*public void setCourseVisibleMappings(List<DeptCourseVisibleMapping> courseVisibleMappings) {
            this.courseVisibleMappings = courseVisibleMappings;
        }*/
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

    private Boolean addStoreAndPosition(String eid, SysDepartmentDO deptDtail, RedisUtilPool redisUtil, String accessToken) {

        log.info("eid>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + eid);
        try {
            // 1. 如果加入到监控的父节点，则需要初始化一个门店
            // 2.否则是加入到子节点，此时不需要同步为门店，只需要按照配置添加人员就行 同步人员时该部门storeId不会是新增部门对应的门店id，所以传一个不存在的值
            DeptModifyEvent modifyEvent = new DeptModifyEvent(corpId, deptDtail.getId().toString());
            DataSourceHelper.changeToSpecificDataSource(getDbName());
            List<String> storeIds = modifyEvent.getStoreId(deptDtail);
            String departmentDTO = redisUtil.getString("departmentDTO" + eid);
            log.info("departmentDTO" + departmentDTO);
            StoreDao storeDao = SpringContextUtil.getBean("storeDao", StoreDao.class);
            StoreService storeService = SpringContextUtil.getBean("storeService", StoreService.class);
            if (StringUtils.isNotEmpty(departmentDTO)) {
                log.info("开始同步添加门店下信息");
                DingDepartmentQueryDTO departmentQueryDTO = JSONObject.parseObject(departmentDTO, DingDepartmentQueryDTO.class);
                List<MonitorDeptDTO> departments = departmentQueryDTO.getDepartments();
                // 获取监控中的所有节点（子节和父节点）
                List<String> monitorDeptIds = departments.stream().map(m -> m.getDepartmentId().toString()).collect(Collectors.toList());
                // 该部门此次同步到监控的子节点下
                boolean addToMonitor = false;
                for (MonitorDeptDTO dept: departments) {
                    String departmentId = String.valueOf(dept.getDepartmentId());
                    List<String> nodeIds = dept.getStoreIds();
                    monitorDeptIds.addAll(nodeIds);
                    if (Objects.equals(departmentId, deptDtail.getParentId().toString())) {
                        addToMonitor = true;
                        if (!nodeIds.contains(deptDtail.getId().toString())) {
                            nodeIds.add(deptDtail.getId().toString());
                            continue;
                        }
                    }
                }
                if (!addToMonitor) {
                    if (CollUtil.isNotEmpty(storeIds)) {
                        storeDao.updateStoreStatus(eid, storeIds, StoreIsDeleteEnum.UN_SYNC.getValue(), storeService.getLimitStoreCount(eid));
                    }
                    return false;
                } else {
                    AutoSyncService autoSyncService = SpringContextUtil.getBean("autoSyncService", AutoSyncService.class);
                    autoSyncService.autoSyncDept(eid, storeIds, deptDtail, departmentQueryDTO, corpId, false, monitorDeptIds, accessToken);
                }

                log.info("更新缓存为{}", departmentQueryDTO);
                redisUtil.setString("departmentDTO" + eid, JSONObject.toJSONString(departmentQueryDTO));
                log.info("清理后的缓存信息为{}", departmentQueryDTO);
            } else {
                storeDao.updateStoreStatus(eid, storeIds, StoreIsDeleteEnum.UN_SYNC.getValue(), storeService.getLimitStoreCount(eid));
            }

        } catch (Exception e) {
            log.error("同步创建门店失败", e);
        }
        return Boolean.TRUE;
    }


}

