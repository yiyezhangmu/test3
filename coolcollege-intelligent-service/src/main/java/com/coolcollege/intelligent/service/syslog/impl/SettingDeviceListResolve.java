package com.coolcollege.intelligent.service.syslog.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.request.BaseDeviceListRequest;
import com.coolcollege.intelligent.model.device.request.DeviceAddRequest;
import com.coolcollege.intelligent.model.device.request.DeviceUpdateRequest;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.util.SysLogHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.coolcollege.intelligent.common.constant.SysLogConstant.Template.*;


/**
* describe: 设备管理设备列表操作内容处理
*
* @author wangff
* @date 2025-01-23
*/
@Service
@Slf4j
public class SettingDeviceListResolve extends AbstractOpContentResolve {

    @Resource
    private DeviceMapper deviceMapper;

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.SETTING_DEVICE_LIST;
    }

    @Override
    protected String insert(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        DeviceAddRequest request = jsonObject.getObject("request", DeviceAddRequest.class);
        return SysLogHelper.buildContent(INSERT_TEMPLATE2, "设备", request.getDeviceName(), request.getDeviceId());
    }

    @Override
    protected String edit(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        DeviceUpdateRequest request = jsonObject.getObject("request", DeviceUpdateRequest.class);
        return SysLogHelper.buildContent(UPDATE_TEMPLATE2, "设备", request.getName(), request.getDeviceId());
    }

    @Override
    protected String delete(String enterpriseId, SysLogDO sysLogDO) {
        return SysLogHelper.getPreprocessResultByExtendInfoStr(sysLogDO.getExtendInfo());
    }

    @Override
    public String preprocess(String enterpriseId, Map<String, Object> reqParams, OpTypeEnum typeEnum) {
        if (OpTypeEnum.DELETE.equals(typeEnum)) {
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(reqParams));
            BaseDeviceListRequest request = jsonObject.getObject("request", BaseDeviceListRequest.class);
            if (CollectionUtil.isNotEmpty(request.getDeviceIdList())) {
                List<DeviceDO> deviceList = deviceMapper.getDeviceByDeviceIdList(enterpriseId, request.getDeviceIdList());
                String result = SysLogHelper.buildBatchContentItem(deviceList, DeviceDO::getDeviceName, DeviceDO::getDeviceId);
                return SysLogHelper.buildContent(DELETE_TEMPLATE2, "设备", result);
            }
        }
        return null;
    }
}
