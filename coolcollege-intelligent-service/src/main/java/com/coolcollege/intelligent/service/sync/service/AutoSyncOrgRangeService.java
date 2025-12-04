package com.coolcollege.intelligent.service.sync.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.department.dto.DingDepartmentQueryDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2020/10/17 15:14
 */
@Service
public class AutoSyncOrgRangeService {

    @Resource
    private SysDepartmentMapper deptMapper;

    @Lazy
    @Autowired
    private StoreService storeService;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private EnterpriseConfigMapper configMapper;

    @Async("isvDingDingQwThreadPool")
    public void syncDeptRange(String eid) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        List<SysDepartmentDO> hideDeptList = deptMapper.selectHideDept(eid);
        List<String> dingIds = hideDeptList.stream().map(m -> m.getId().toString()).collect(Collectors.toList());
        if (CollUtil.isEmpty(dingIds)) {
            return;
        }
        List<String> storeIds = storeMapper.getEffectiveStoreByDingIdList(eid, dingIds);
//        Map<String, Object> map = new HashMap<>();
//        map.put("storeIds", storeIds);
        storeService.updateCache(eid, storeIds);
    }
}
