package com.coolcollege.intelligent.service.syslog.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.SysLogConstant;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.region.dto.RegionNode;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.service.requestBody.region.RegionAddPersonalRequest;
import com.coolcollege.intelligent.service.requestBody.region.RegionRequestBody;
import com.coolcollege.intelligent.util.SysLogHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.coolcollege.intelligent.common.constant.SysLogConstant.Template.*;
import static com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum.*;

/**
* describe: 区域门店操作内容处理
*
* @author wangff
* @date 2025-02-14
*/
@Service
@Slf4j
public class RegionStoreResolve extends AbstractOpContentResolve {
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Override
    protected void init() {
        super.init();
        funcMap.put(INSERT_PERSON, this::insertPerson);
    }

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.SETTING_REGION_STORE;
    }

    @Override
    protected String insert(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        RegionRequestBody request = jsonObject.getObject("regionRequestBody", RegionRequestBody.class);
        return SysLogHelper.buildContent(INSERT_TEMPLATE, "区域", request.getName());
    }

    @Override
    protected String edit(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        RegionRequestBody request = jsonObject.getObject("regionRequestBody", RegionRequestBody.class);
        return SysLogHelper.buildContent(UPDATE_TEMPLATE, "区域", request.getName());
    }

    @Override
    protected String delete(String enterpriseId, SysLogDO sysLogDO) {
        return SysLogHelper.getPreprocessResultByExtendInfoStr(sysLogDO.getExtendInfo());
    }

    /**
     * 新增人员
     */
    private String insertPerson(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        RegionAddPersonalRequest request = jsonObject.getObject("request", RegionAddPersonalRequest.class);
        RegionNode regionNode = regionMapper.getRegionByRegionId(enterpriseId, request.getRegionId());
        if (Objects.isNull(regionNode)) {
            log.info("insertPerson#区域不存在");
            return null;
        }
        List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, request.getUserIds());
        if (CollectionUtil.isEmpty(userList)) {
            log.info("insertPerson#新增人员不存在");
            return null;
        }
        String items = SysLogHelper.buildBatchContentItem(userList, EnterpriseUserDO::getName, EnterpriseUserDO::getMobile);
        // 新增用户和重新分配为同一个接口，通过判断用户是否存在所属部门判断是新增还是重新分配
        String preprocessResult = SysLogHelper.getPreprocessResultByExtendInfoStr(sysLogDO.getExtendInfo());
        if ("reallocate".equals(preprocessResult)) {
            sysLogDO.setFunc("重新分配");
            return SysLogHelper.buildContent(REGION_STORE_TEMPLATE, SysLogConstant.REALLOCATE, items, "组织架构", regionNode.getName());
        } else {
            return SysLogHelper.buildContent(REGION_STORE_TEMPLATE, SysLogConstant.INSERT, items, "区域", regionNode.getName());
        }
    }

    @Override
    public String preprocess(String enterpriseId, Map<String, Object> reqParams, OpTypeEnum typeEnum) {
        switch (typeEnum) {
            case DELETE:
                return deletePreprocess(enterpriseId, reqParams);
            case INSERT_PERSON:
                return insertPreprocess(enterpriseId, reqParams);
        }
        return null;
    }

    private String deletePreprocess(String enterpriseId, Map<String, Object> reqParams) {
        String regionId = MapUtils.getString(reqParams, "regionId");
        RegionNode regionNode = regionMapper.getRegionByRegionId(enterpriseId, regionId);
        if (Objects.isNull(regionNode)) {
            log.info("preprocess#区域不存在");
            return null;
        }
        return SysLogHelper.buildContent(DELETE_TEMPLATE, "区域", regionNode.getName());
    }

    private String insertPreprocess(String enterpriseId, Map<String, Object> reqParams) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(reqParams));
        RegionAddPersonalRequest request = jsonObject.getObject("request", RegionAddPersonalRequest.class);
        if (CollectionUtil.isEmpty(request.getUserIds())) {
            log.info("insertPreprocess#用户id为空");
            return null;
        }
        // 取一个用户，根据是否有所属部门判断是新增人员还是重新分配
        String userId = CollectionUtil.getFirst(request.getUserIds());
        EnterpriseUserDO user = enterpriseUserDao.selectByUserId(enterpriseId, userId);
        if (user.getUserRegionIds().endsWith("/-2/]")) {
            return "reallocate";
        } else {
            return "insertPerson";
        }
    }

}
