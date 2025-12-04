package com.coolcollege.intelligent.service.sync.event;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.department.dto.DingDepartmentQueryDTO;
import com.coolcollege.intelligent.model.department.dto.MonitorDeptDTO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enums.StoreIsDeleteEnum;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DeptRemoveEvent extends BaseDeptEvent {


    public DeptRemoveEvent(String corpId, String deptId) {
        this.corpId = corpId;
        this.deptId = deptId;
    }

    @Override
    public String getEventType() {
        return BaseEvent.ORG_DEPT_REMOVE;
    }


    @Override
    public void doEvent() {

        String accessToken = getAccessToken();
        log.info("当前token是：" + accessToken);
        if (StringUtils.isBlank(accessToken)) {
            return;
        }

        String eid = getEid();
        String dbName = getDbName();
        log.info("数据库" + dbName);
        List<String> deptIds = Arrays.stream(deptId.replaceAll("\\[", "").replaceAll("\\]", "").split(",")).map(String::valueOf).collect(Collectors.toList());
        log.info("要删除的部门ids为{}", deptIds);
        try {
            log.info("企业id为{}", eid);
            SysDepartmentService sysDepartmentService = SpringContextUtil.getBean("sysDepartmentService", SysDepartmentService.class);
            DataSourceHelper.changeToSpecificDataSource(dbName);
            // 获取该部门下的子部门
            List<String> deleteDeptIds = sysDepartmentService.getChildDeptIdList(eid, deptIds.get(0), true);
            log.info("删除掉的部门为{}", deleteDeptIds);sysDepartmentService.deleteByIds(deleteDeptIds, eid);
            if (getEnableDingSync()) {
                RedisUtilPool redisUtil = SpringContextUtil.getBean("redisUtilPool", RedisUtilPool.class);
                //有其他同步逻辑获取锁时候，不允许添加
                if (StringUtils.isNotEmpty(redisUtil.getString("syncDingStore" + eid))) {
                    Long second = redisUtil.getExpire("syncDingStore" + eid);
                    log.info("线程处于同步中，在等待。。。。");
                    Thread.sleep(second * 1000L);
                }
                DataSourceHelper.changeToSpecificDataSource(dbName);
                deleteDeptDepend(deleteDeptIds, eid, redisUtil);
            }
        } catch (Exception e) {
            log.error("del dept error, corpId={}, deptId={}", corpId, deptId, e);
        }
    }

    /**
     * 删除与该部门有关的依赖
     * @param deleteDeptIds
     * @param eid
     */
    public void deleteDeptDepend(List<String> deleteDeptIds, String eid, RedisUtilPool redisUtil) {
        log.info("开始清理缓存中删除掉的部门{}+++++++++++++++++++", eid);
        //删除部门需要清理缓存
        String departmentDTO = redisUtil.getString("departmentDTO" + eid);
        log.info("departmentDto" + departmentDTO);
        List<String> collect = new ArrayList<>();
        //要删除的部门IDs
        if (StringUtils.isNotEmpty(departmentDTO)) {
            collect = deleteDeptIds.stream().map(String::valueOf).collect(Collectors.toList());
            DingDepartmentQueryDTO departmentQueryDTO = JSONObject.parseObject(departmentDTO, DingDepartmentQueryDTO.class);
            List<MonitorDeptDTO> departments = departmentQueryDTO.getDepartments();
            // 新的监控父节点
            List<MonitorDeptDTO> newDepartments = new ArrayList<>();
            for (MonitorDeptDTO dept: departments) {
                // 如果当前删除的节点在监控的父节下
                if (!(deleteDeptIds.contains(dept.getDepartmentId()))) {
                    // 如果删除的部门是监控的门店，则直接remove
                    if (CollUtil.isNotEmpty(dept.getStoreIds())) {
                        dept.getStoreIds().remove(this.deptId);
                    }
                    if (CollUtil.isNotEmpty(dept.getDeleteIds())) {
                        dept.getDeleteIds().remove(this.deptId);
                    }
                    newDepartments.add(dept);
                }
            }
            departmentQueryDTO.setDepartments(newDepartments);
            redisUtil.setString("departmentDTO" + eid, JSONObject.toJSONString(departmentQueryDTO));
            log.info("清理后的缓存信息为{}", departmentQueryDTO);
        }
        //后台删除节点，需要把门店删除
        StoreService storeService = SpringContextUtil.getBean("storeService", StoreService.class);
        log.info("删除的门店dingID为{}", collect);
        List<StoreDTO> delStoreList = storeService.getAllStoreList(eid, collect, StoreIsDeleteEnum.EFFECTIVE.getValue());
        if (CollUtil.isEmpty(delStoreList)) {
            return;
        }
        List<String> storeIdList = delStoreList.stream().map(StoreDTO::getStoreId).collect(Collectors.toList());
        log.info("删除的门店为{}", storeIdList);

        if(CollectionUtils.isNotEmpty(storeIdList)) {
            StoreMapper storeMapper = SpringContextUtil.getBean("storeMapper", StoreMapper.class);
            List<StoreDO> removeStoreDOList = storeMapper.getByStoreIdList(eid, storeIdList);
            if (CollectionUtils.isNotEmpty(removeStoreDOList)) {
                Map<Long, StoreDO> removeStoreDOMap = ListUtils.emptyIfNull(removeStoreDOList).stream()
                        .collect(Collectors.toMap(StoreDO::getId, data -> data, (a, b) -> a));
                StoreDO defaultStore = removeStoreDOMap.get(Constants.DEFAULT_STORE_ID);
                if (defaultStore != null && Constants.SENYU_ENTERPRISE_ID.equals(eid)) {
                    storeIdList.remove(defaultStore.getStoreId());
                }
            }
        }

        Map<String, Object> map = Maps.newHashMap();
        map.put("store_ids", storeIdList);
        map.put("corpId", corpId);
        map.put("syncDelete", true);
        map.put("dingId", deleteDeptIds);
        storeService.deleteStoreByStoreIds(eid, map,Boolean.FALSE);
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DeptRemoveEvent{");
        sb.append("deptId='").append(deptId).append('\'');
        sb.append(", corpId='").append(corpId).append('\'');
        sb.append(", eid=").append(eid);
        sb.append('}');
        return sb.toString();
    }


}

