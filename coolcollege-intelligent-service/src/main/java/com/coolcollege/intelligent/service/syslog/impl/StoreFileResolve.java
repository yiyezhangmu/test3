package com.coolcollege.intelligent.service.syslog.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.SysLogConstant;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.util.LogUtil;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.model.region.dto.RegionNode;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreBatchMoveDTO;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.service.requestBody.store.StoreRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum.*;

/**
 * describe: 门店档案操作内容处理
 *
 * @author wangff
 * @date 2025/1/20
 */
@Service
@Slf4j
public class StoreFileResolve extends AbstractOpContentResolve {

    @Resource
    private RegionDao regionDao;
    @Resource
    private StoreDao storeDao;

    @PostConstruct
    @Override
    protected void init() {
        super.init();
        funcMap.put(BATCH_MOVE, this::batchMove);
        funcMap.put(BATCH_DELETE, this::batchDelete);
    }

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.STORE_FILE;
    }

    @Override
    protected String insert(String enterpriseId, SysLogDO sysLogDO) {
        // 操作内容
        String storeId = (String) JSONObject.parse(sysLogDO.getRespParams());
        String storeName = "";
        StoreRequestBody storeRequestBody = LogUtil.paresString(sysLogDO.getReqParams(), "storeRequestBody", StoreRequestBody.class);
        if (Objects.nonNull(storeRequestBody)) {
            storeName = storeRequestBody.getStore_name();
        }
        String content = "新增了门店「%s（%s）」";
        content = String.format(content, storeName, storeId);
        return content;
    }

    @Override
    protected String edit(String enterpriseId, SysLogDO sysLogDO) {
        // TODO
        String extendInfo = sysLogDO.getExtendInfo();
        String reqParams = sysLogDO.getReqParams();
        String content = "修改了门店「%s（%s）」%s";
        if (StringUtils.isNotBlank(extendInfo) && StringUtils.isNotBlank(reqParams)) {
            StoreDO oldStore = LogUtil.paresString(extendInfo, SysLogConstant.PREPROCESS_RESULT, StoreDO.class);
            JSONObject reqParamsJson = JSONObject.parseObject(reqParams);
            String storeId = reqParamsJson.getString("storeId");
            StoreDO newStore = storeDao.getByStoreId(enterpriseId, storeId);
            if (Objects.nonNull(oldStore) && Objects.nonNull(newStore)) {
                String updateContent = LogUtil.compareTwoObj(oldStore, newStore);
                content = String.format(content, oldStore.getStoreName(), oldStore.getStoreId(), updateContent);
            }
        }
        return content;
    }

    @Override
    protected String delete(String enterpriseId, SysLogDO sysLogDO) {
        // TODO
        return "删除了门店";
    }

    @Override
    public String preprocess(String enterpriseId, Map<String, Object> reqParams, OpTypeEnum typeEnum) {
        String storeId = (String) reqParams.get("storeId");
        StoreDO store = storeDao.getByStoreId(enterpriseId, storeId);
        if (Objects.nonNull(store)) {
            return JSONObject.toJSONString(store);
        }
        return "";
    }

    protected String batchDelete(String enterpriseId, SysLogDO sysLogDO) {
        String content = "删除了门店%s";
        JSONObject paramJson = JSONObject.parseObject(sysLogDO.getReqParams());
        String storeNames = "";
        if (Objects.nonNull(paramJson)) {
            JSONObject map = paramJson.getJSONObject("map");
            if (Objects.nonNull(map) && Objects.nonNull(map.getJSONArray("store_ids"))) {
                JSONArray storeIds = map.getJSONArray("store_ids");
                List<String> storeIdList = new ArrayList<>();
                storeIds.forEach(storeId -> storeIdList.add(storeId.toString()));
                storeNames = getStoreNames(enterpriseId, storeIdList);
            }
        }
        content = String.format(content, storeNames);
        return content;
    }

    protected String batchMove(String enterpriseId, SysLogDO sysLogDO) {
        String content = "批量移动门店%s至区域：「%s」";
        StoreBatchMoveDTO storeBatchMoveDTO = LogUtil.paresString(sysLogDO.getReqParams(), "moveDTO", StoreBatchMoveDTO.class);
        String areaName = "";
        String storeNames = "";
        if (Objects.nonNull(storeBatchMoveDTO)) {
            List<String> storeIds = storeBatchMoveDTO.getStoreIds();
            String areaId = storeBatchMoveDTO.getAreaId();
            RegionNode regionByRegionId = regionDao.getRegionByRegionId(enterpriseId, areaId);
            if (Objects.nonNull(regionByRegionId)) {
                areaName = regionByRegionId.getName();
            }
            storeNames = getStoreNames(enterpriseId, storeIds);
        }
        content = String.format(content, storeNames, areaName);
        return content;
    }

    private String getStoreNames(String enterpriseId, List<String> storeIds) {
        List<StoreDO> storeDOList = storeDao.getByStoreIdList(enterpriseId, storeIds);
        String nameContent = "「%s（%s）」";
        StringJoiner nameJoiner = new StringJoiner("、", "","");
        for (StoreDO storeDO : storeDOList) {
            nameJoiner.add(String.format(nameContent, storeDO.getStoreName(), storeDO.getStoreId()));
        }
        return nameJoiner.toString();
    }
}
