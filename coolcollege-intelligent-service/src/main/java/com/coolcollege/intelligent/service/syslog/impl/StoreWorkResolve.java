package com.coolcollege.intelligent.service.syslog.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.SysLogConstant;
import com.coolcollege.intelligent.common.enums.storework.StoreWorkCycleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.storework.SwStoreWorkDataTableMapper;
import com.coolcollege.intelligent.dao.storework.SwStoreWorkMapper;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableDO;
import com.coolcollege.intelligent.model.storework.request.BuildStoreWorkRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.storework.request.TransferHandlerCommentRequest;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.util.SysLogHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.coolcollege.intelligent.common.constant.SysLogConstant.*;
import static com.coolcollege.intelligent.common.constant.SysLogConstant.Template.*;

/**
 * describe: 店务日/周/月清操作内容处理
 *
 * @author wangff
 * @date 2025/1/21
 */
@Service
@Slf4j
public class StoreWorkResolve extends AbstractOpContentResolve {
    @Resource
    private SwStoreWorkMapper storeWorkMapper;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private SwStoreWorkDataTableMapper swStoreWorkDataTableMapper;

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.STORE_WORK;
    }

    @Override
    protected String insert(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        BuildStoreWorkRequest request = jsonObject.getObject("request", BuildStoreWorkRequest.class);
        sysLogDO.setModule(StoreWorkCycleEnum.getByCode(request.getWorkCycle()));
        return SysLogHelper.buildContent(INSERT_TEMPLATE, StoreWorkCycleEnum.getByCode(request.getWorkCycle()), request.getWorkName());
    }

    @Override
    protected String edit(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        BuildStoreWorkRequest request = jsonObject.getObject("request", BuildStoreWorkRequest.class);
        sysLogDO.setModule(StoreWorkCycleEnum.getByCode(request.getWorkCycle()));
        return SysLogHelper.buildContent(UPDATE_TEMPLATE, StoreWorkCycleEnum.getByCode(request.getWorkCycle()), request.getWorkName());
    }

    @Override
    protected String delete(String enterpriseId, SysLogDO sysLogDO) {
        String result = SysLogHelper.getPreprocessResultByExtendInfoStr(sysLogDO.getExtendInfo());
        JSONObject jsonObject = JSONObject.parseObject(result);
        sysLogDO.setModule(jsonObject.getString("workCycle"));
        return jsonObject.getString("result");
    }

    @Override
    protected String stop(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        Long storeWorkId = jsonObject.getLong("storeWorkId");
        SwStoreWorkDO swStoreWorkDO = storeWorkMapper.selectByPrimaryKey(enterpriseId, storeWorkId);
        if (Objects.isNull(swStoreWorkDO)) {
            log.info("stop#店务为空");
            return null;
        }
        sysLogDO.setModule(StoreWorkCycleEnum.getByCode(swStoreWorkDO.getWorkCycle()));
        return SysLogHelper.buildContent(STOP_TEMPLATE, StoreWorkCycleEnum.getByCode(swStoreWorkDO.getWorkCycle()), swStoreWorkDO.getWorkName());
    }

    @Override
    protected String remind(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        StoreWorkDataListRequest storeWorkDataListRequest = jsonObject.getObject("storeWorkDataListRequest", StoreWorkDataListRequest.class);
        if (Objects.isNull(storeWorkDataListRequest)) {
            log.info("preprocessResultByRemind#参数不存在");
            return null;
        }
        List<String> storeIds = storeWorkDataListRequest.getStoreIds();
        SwStoreWorkDO swStoreWorkDO = storeWorkMapper.selectByPrimaryKey(enterpriseId, storeWorkDataListRequest.getStoreWorkId());
        if (Objects.isNull(swStoreWorkDO)) {
            log.info("preprocessResultByRemind#店务不存在");
            return null;
        }
        StoreDO storeDO = null;
        if (CollectionUtil.isNotEmpty(storeIds)) {
            String storeId = CollectionUtil.get(storeIds, 0);
            storeDO = storeMapper.getByStoreId(enterpriseId, storeId);
        }
        sysLogDO.setModule(StoreWorkCycleEnum.getByCode(swStoreWorkDO.getWorkCycle()));
        return getContent(REMIND_TEMPLATE, SysLogConstant.REMIND, swStoreWorkDO.getWorkCycle(), swStoreWorkDO.getWorkName(), storeWorkDataListRequest.getStoreWorkDate(), storeDO);
    }

    @Override
    protected String reallocate(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        JSONArray requestList = jsonObject.getJSONArray("requestList");
        if (Objects.isNull(requestList) || requestList.isEmpty()) {
            log.info("preprocessResultByReallocate#参数不存在");
            return null;
        }
        TransferHandlerCommentRequest storeWorkDataListRequest = requestList.toJavaList(TransferHandlerCommentRequest.class).get(0);
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = swStoreWorkDataTableMapper.selectByIds(storeWorkDataListRequest.getStoreWorkDataTableIds(), enterpriseId);
        if (CollectionUtil.isEmpty(swStoreWorkDataTableDOS)) {
            log.info("preprocessResultByReallocate#店务数据为空");
            return null;
        }
        SwStoreWorkDataTableDO swStoreWorkDataTableDO = swStoreWorkDataTableDOS.get(0);
        SwStoreWorkDO swStoreWorkDO = storeWorkMapper.selectByPrimaryKey(enterpriseId, swStoreWorkDataTableDO.getStoreWorkId());
        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, swStoreWorkDataTableDO.getStoreId());
        String workCycle = swStoreWorkDataTableDO.getWorkCycle();
        String workName = swStoreWorkDO.getWorkName();
        String storeWorkDate = DateUtil.format(swStoreWorkDataTableDO.getStoreWorkDate(), "yyyy-MM-dd");
        sysLogDO.setModule(StoreWorkCycleEnum.getByCode(workCycle));
        return getContent(null, REALLOCATE, workCycle, workName, storeWorkDate, storeDO);
    }

    @Override
    public String preprocess(String enterpriseId, Map<String, Object> reqParams, OpTypeEnum typeEnum) {
        if (typeEnum == OpTypeEnum.DELETE) {
            Long storeWorkId = MapUtils.getLong(reqParams, "storeWorkId");
            SwStoreWorkDO swStoreWorkDO = storeWorkMapper.selectByPrimaryKey(enterpriseId, storeWorkId);
            if (Objects.nonNull(swStoreWorkDO)) {
                String result = SysLogHelper.buildContent(DELETE_TEMPLATE, StoreWorkCycleEnum.getByCode(swStoreWorkDO.getWorkCycle()), swStoreWorkDO.getWorkName());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("result", result);
                jsonObject.put("workCycle", StoreWorkCycleEnum.getByCode(swStoreWorkDO.getWorkCycle()));
                return jsonObject.toJSONString();
            }
        }
        return null;
    }

    /**
     * 拼接操作内容
     * @param template 模板
     * @param prefix 前缀
     * @param workCycle 店务周期
     * @param workName 店务名称
     * @param storeWorkDate 任务日期
     * @param storeDO 门店
     * @return 操作内容
     */
    private String getContent(String template, String prefix, String workCycle, String workName, String storeWorkDate, StoreDO storeDO) {
        if (Objects.nonNull(storeDO)) {
            String storeName = storeDO.getStoreName();
            String storeNum = storeDO.getStoreNum();
            return SysLogHelper.buildContent(STORE_WORK_SPECIAL_TEMPLATE, prefix, storeName, storeNum, storeWorkDate, StoreWorkCycleEnum.getByCode(workCycle), workName);
        } else {
            return SysLogHelper.buildContent(template, StoreWorkCycleEnum.getByCode(workCycle), workName);
        }
    }
}
