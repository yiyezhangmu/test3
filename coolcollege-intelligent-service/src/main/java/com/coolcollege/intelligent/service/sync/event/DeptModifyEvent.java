package com.coolcollege.intelligent.service.sync.event;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.department.dto.DingDepartmentQueryDTO;
import com.coolcollege.intelligent.model.department.dto.MonitorDeptDTO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enums.StoreIsDeleteEnum;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.sync.service.AutoSyncService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class DeptModifyEvent extends BaseDeptEvent {


    public DeptModifyEvent(String corpId, String deptId) {
        this.corpId = corpId;
        this.deptId = deptId;
    }


    @Override
    public String getEventType() {
        return BaseEvent.ORG_DEPT_MODIFY;
    }

    @Override
    public void doEvent() {

        log.info("修改的部门信息为{}", deptId);
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

        String eid = getEid();
        String dbName = getDbName();

        SysDepartmentService sysDepartmentService = SpringContextUtil.getBean("sysDepartmentService", SysDepartmentService.class);
        StoreService storeService = SpringContextUtil.getBean("storeService", StoreService.class);

        try {
            //删除部门需要清理缓存
            RedisUtilPool redisUtil = SpringContextUtil.getBean("redisUtilPool", RedisUtilPool.class);
            log.info("企业id为{}", eid);

            List<SysDepartmentDO> sysDepartmentDOList = Collections.singletonList(deptDetail);
            DataSourceHelper.changeToSpecificDataSource(dbName);
            SysDepartmentDO oldDept = sysDepartmentService.selectById(eid, dpId);
            sysDepartmentService.batchInsertOrUpdate(sysDepartmentDOList, eid);
            boolean deptNoChange = deptDetail.getParentId().equals(oldDept.getParentId());
            // 如果开启同步  则修改门店名称
            List<String> storeList = null;
            if (getEnableDingSync()) {
                DataSourceHelper.changeToSpecificDataSource(dbName);
                storeList = getStoreId(deptDetail);
            }
            // 如果部门的父节点没有变动  说明只是修改基础信息 在更新部门信息后结束
            if (deptNoChange) {
                return;
            }
            //有其他同步逻辑获取锁时候，不允许添加
            if (StringUtils.isNotEmpty(redisUtil.getString("syncDingStore" + eid))) {
                Long second = redisUtil.getExpire("syncDingStore" + eid);
                log.info("线程处于同步中，在等待。。。。");
                Thread.sleep(second * 1000L);
            }
            DataSourceHelper.changeToSpecificDataSource(dbName);

            String departmentDTO = redisUtil.getString("departmentDTO" + eid);
            DingDepartmentQueryDTO departmentQueryDTO = JSONObject.parseObject(departmentDTO, DingDepartmentQueryDTO.class);
            //部门id
            String deptId = deptDetail.getId().toString();
            List<MonitorDeptDTO> departments = departmentQueryDTO.getDepartments();
            if (CollUtil.isEmpty(departments)) {
                return;
            }
            List<String> monitorDeptIds = departments.stream().map(m -> m.getDepartmentId().toString()).collect(Collectors.toList());
            // 该部门此次同步到监控的子节点下
            boolean syncToParent = false;

            // 需要同步的部门id
            // 需要移除的父节点id
            for (MonitorDeptDTO dept: departments) {
                String departmentId = String.valueOf(dept.getDepartmentId());
                List<String> storeIds = dept.getStoreIds();
                List<String> deleteIds = dept.getDeleteIds();
                // 如果原本在监控节点 现在移出了  则删除redis缓存
                if (Objects.equals(departmentId, oldDept.getParentId().toString())) {
                    if (CollUtil.isNotEmpty(storeIds)) {
                        storeIds.remove(deptDetail.getId().toString());
                    }
                    if (CollUtil.isNotEmpty(deleteIds)) {
                        deleteIds.remove(deptDetail.getId().toString());
                    }
                }
                if (Objects.equals(departmentId, deptDetail.getParentId().toString())) {
                    syncToParent = true;
                    if (!storeIds.contains(deptId)) {
                        storeIds.add(deptId);
                    }
                }
                // recursiveGetDeptId(syncDeptIds, deptId, storeIds, deptDetail.getParentId());
            }
            // 如果原来的部门在监控中现在移除，则删除
            List<String> deptIds = Collections.singletonList(deptDetail.getId());

            if (getEnableDingSync()) {
                DataSourceHelper.changeToSpecificDataSource(dbName);
                if (!syncToParent) {
                    delStore(storeList, deptIds);
                } else {
                    StoreDao storeDao = SpringContextUtil.getBean("storeDao", StoreDao.class);
                    storeDao.updateStoreStatus(eid, storeList, StoreIsDeleteEnum.EFFECTIVE.getValue(), storeService.getLimitStoreCount(eid));
                    syncDept(storeList, deptDetail, departmentQueryDTO, false, monitorDeptIds, accessToken);
                }
            }
            // 过滤掉移出父节点的部门及子部门
            log.info("更新缓存为{}", departmentQueryDTO);
            redisUtil.setString("departmentDTO" + eid, JSONObject.toJSONString(departmentQueryDTO));
        } catch (Exception e) {
            log.error("update dept error, corpId={}, deptId={}", corpId, deptId, e);
        }
    }

    private void delStore(List<String> storeList, List<String> deptIds) {

        if(CollectionUtils.isNotEmpty(storeList)) {
            StoreMapper storeMapper = SpringContextUtil.getBean("storeMapper", StoreMapper.class);
            List<StoreDO> removeStoreDOList = storeMapper.getByStoreIdList(eid, storeList);
            if (CollectionUtils.isNotEmpty(removeStoreDOList)) {
                Map<Long, StoreDO> removeStoreDOMap = ListUtils.emptyIfNull(removeStoreDOList).stream()
                        .collect(Collectors.toMap(StoreDO::getId, data -> data, (a, b) -> a));
                StoreDO defaultStore = removeStoreDOMap.get(Constants.DEFAULT_STORE_ID);
                if (defaultStore != null && Constants.SENYU_ENTERPRISE_ID.equals(eid)) {
                    storeList.remove(defaultStore.getStoreId());
                }
            }
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("store_ids", storeList);
        map.put("corpId", corpId);
        map.put("syncDelete", true);
        map.put("dingId", deptIds);
        StoreService storeService = SpringContextUtil.getBean("storeService", StoreService.class);
        storeService.deleteStoreByStoreIds(eid, map,Boolean.FALSE);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DeptModifyEvent{");
        sb.append("deptId='").append(deptId).append('\'');
        sb.append(", corpId='").append(corpId).append('\'');
        sb.append(", eid=").append(eid);
        sb.append('}');
        return sb.toString();
    }

//    private void recursiveGetDeptId(List<String> syncDeptIds, String deptId, List<String> childIds, Long compareDeptId) {
//        if (compareDeptId == null) {
//            return;
//        }
//        if (syncDeptIds.contains(compareDeptId.toString())) {
//            return;
//        }
//        if (childIds.contains(compareDeptId.toString())) {
//            syncDeptIds.add(compareDeptId.toString());
//            syncDeptIds.add(deptId);
//        }
//        Long pid = idForPid.get(compareDeptId);
//        recursiveGetDeptId(syncDeptIds, deptId, childIds, pid);
//    }


    protected List<String> getStoreId(SysDepartmentDO deptDetail) {
        //如果此时父节点不在监控范围内，需要把此门店删除掉
        StoreService storeService = SpringContextUtil.getBean("storeService", StoreService.class);
        log.info("此次修改的门店为{}", deptDetail);
        List<String> storeList = storeService.getAllStoreList(getEid(), Collections.singletonList(deptDetail.getId().toString()), null)
                .stream()
                .map(StoreDTO::getStoreId).collect(Collectors.toList());
        // 如果修改到监控父节点下则还原（新建）门店
        if (CollUtil.isNotEmpty(storeList)) {
            StoreDO storeDO = new StoreDO();
            storeDO.setStoreName(deptDetail.getName());
            storeDO.setStoreId(storeList.get(0));
            storeService.updateSingleStore(eid, storeDO);
            return storeList;
        } else {
            log.info("门店不存在，需要重新同步");
            String storeId = UUIDUtils.get32UUID();
            storeService.addStores(eid, deptDetail, storeId);
            return Collections.singletonList(storeId);
        }
    }

    private void syncDept(List<String> storeIds, SysDepartmentDO deptDetail, DingDepartmentQueryDTO dingDepartmentQueryDTO, boolean syncToChild, List<String> monitorDeptIds, String accessToken) {
        AutoSyncService autoSyncService = SpringContextUtil.getBean("autoSyncService", AutoSyncService.class);
        autoSyncService.autoSyncDept(eid, storeIds, deptDetail, dingDepartmentQueryDTO, corpId, syncToChild, monitorDeptIds, accessToken);
    }
}
