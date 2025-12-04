package com.coolcollege.intelligent.service.syslog.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.util.SysLogHelper;
import com.coolstore.base.enums.YunTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.coolcollege.intelligent.common.constant.SysLogConstant.Template.*;
import static com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum.DEVICE_AUTHORIZATION;
import static com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum.DEVICE_SYNC;


/**
* describe: 设备集成操作内容处理
*
* @author wangff
* @date 2025-02-14
*/
@Service
@Slf4j
public class DeviceIntegrationResolve extends AbstractOpContentResolve {
    @Override
    protected void init() {
        super.init();
        funcMap.put(DEVICE_AUTHORIZATION, this::authorization);
        funcMap.put(DEVICE_SYNC, this::sync);
    }

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.SETTING_DEVICE_INTEGRATION;
    }


    /**
     * 授权
     */
    private String authorization(String enterpriseId, SysLogDO sysLogDO) {
        return SysLogHelper.buildContent(AUTHORIZATION_TEMPLATE, "萤石云");
    }

    /**
     * 同步
     */
    private String sync(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        String yunType = jsonObject.getString("yunType");
        YunTypeEnum yunTypeEnum = YunTypeEnum.valueOf(yunType);
        return SysLogHelper.buildContent(SYNC_DEVICE_TEMPLATE, yunTypeEnum.getMsg());
    }

}
