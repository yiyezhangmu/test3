package com.coolcollege.intelligent.service.syslog.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.rpc.common.json.JSON;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.dao.store.StoreGroupMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMappingMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.store.dto.StoreGroupDTO;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.util.SysLogHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.constant.SysLogConstant.Template.*;

/**
 * describe: 门店分组操作内容处理
 *
 * @author wangff
 * @date 2025-01-24
 */
@Service
@Slf4j
public class StoreGroupResolve extends AbstractOpContentResolve {
    @Resource
    private StoreGroupMappingMapper storeGroupMappingMapper;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private StoreGroupMapper storeGroupMapper;

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.STORE_GROUP;
    }

    @Override
    protected String insert(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        StoreGroupDTO request = jsonObject.getObject("storeGroupDTO", StoreGroupDTO.class);
        return SysLogHelper.buildContent(INSERT_TEMPLATE, "门店分组", request.getStoreGroup().getGroupName());
    }

    @Override
    protected String edit(String enterpriseId, SysLogDO sysLogDO) {
        String preprocessResult = SysLogHelper.getPreprocessResultByExtendInfoStr(sysLogDO.getExtendInfo());
        JSONObject jsonObject = JSONObject.parseObject(preprocessResult);
        if (Objects.isNull(jsonObject)) return null;
        List<String> addStoreIds = jsonObject.getJSONArray("addStoreIds").toJavaList(String.class);
        List<String> removeStoreIds = jsonObject.getJSONArray("removeStoreIds").toJavaList(String.class);
        List<String> updateStoreIds = Lists.newArrayList();
        updateStoreIds.addAll(addStoreIds);
        updateStoreIds.addAll(removeStoreIds);

        String result = SysLogHelper.buildContent(UPDATE_TEMPLATE, "门店分组", jsonObject.getString("groupName"));
        if (CollectionUtil.isNotEmpty(updateStoreIds)) {
            List<StoreDO> storeList = storeMapper.selectByStoreIds(enterpriseId, updateStoreIds);
            Map<String, StoreDO> storeMap = CollStreamUtil.toMap(storeList, StoreDO::getStoreId, v -> v);
            Function<String, String> nameFunc = v -> Optional.ofNullable(storeMap.get(v)).map(StoreDO::getStoreName).get();
            Function<String, Object> idFunc = v -> Optional.ofNullable(storeMap.get(v)).map(StoreDO::getStoreId).get();
            if (CollectionUtil.isNotEmpty(addStoreIds)) {
                String items = SysLogHelper.buildBatchContentItem(addStoreIds, nameFunc, idFunc);
                result = result + "，新增门店：" + items;
            }
            if (CollectionUtil.isNotEmpty(removeStoreIds)) {
                String items = SysLogHelper.buildBatchContentItem(removeStoreIds, nameFunc, idFunc);
                result = result + "，移除门店：" + items;
            }
        }
        return result;
    }

    @Override
    protected String delete(String enterpriseId, SysLogDO sysLogDO) {
        return SysLogHelper.getPreprocessResultByExtendInfoStr(sysLogDO.getExtendInfo());
    }

    @Override
    public String preprocess(String enterpriseId, Map<String, Object> reqParams, OpTypeEnum typeEnum) {
        switch (typeEnum) {
            case EDIT:
                return editPreprocess(enterpriseId, reqParams);
            case DELETE:
                return deletePreprocess(enterpriseId, reqParams);
        }
        return null;
    }

    /**
     * EDIT前置操作逻辑
     */
    private String editPreprocess(String enterpriseId, Map<String, Object> reqParams) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(reqParams));
        StoreGroupDTO request = jsonObject.getObject("storeGroupDTO", StoreGroupDTO.class);
        List<String> storeIds = CollStreamUtil.toList(request.getStoreList(), StoreDTO::getStoreId);
        // 查旧的门店
        List<String> oldStoreIds = storeGroupMappingMapper.selectStoreByGroupId(enterpriseId, request.getGroupId());
        Set<String> oldStoreSet = new HashSet<>(oldStoreIds);
        Set<String> newStoreSet = new HashSet<>(storeIds);
        // 新增和删除的门店
        List<String> addStoreIds = storeIds.stream().filter(v -> !oldStoreSet.contains(v)).collect(Collectors.toList());
        List<String> removeStoreIds = oldStoreIds.stream().filter(v -> !newStoreSet.contains(v)).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("addStoreIds", addStoreIds);
        map.put("removeStoreIds", removeStoreIds);
        map.put("groupName", request.getStoreGroup().getGroupName());
        return JSON.toJSONString(map);
    }

    /**
     * DELETE前置操作逻辑
     */
    private String deletePreprocess(String enterpriseId, Map<String, Object> reqParams) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(reqParams));
        StoreGroupDTO request = jsonObject.getObject("storeGroupDTO", StoreGroupDTO.class);
        if (CollectionUtil.isEmpty(request.getGroupIdList())) return null;
        List<StoreGroupDO> groupList = storeGroupMapper.getListByIds(enterpriseId, request.getGroupIdList());
        if (CollectionUtil.isEmpty(groupList)) return null;
        String result = SysLogHelper.buildBatchContentItem(groupList, StoreGroupDO::getGroupName);
        return SysLogHelper.buildContent(DELETE_TEMPLATE2, "门店分组", result);
    }

}
