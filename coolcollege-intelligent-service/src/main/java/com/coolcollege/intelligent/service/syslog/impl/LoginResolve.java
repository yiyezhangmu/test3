package com.coolcollege.intelligent.service.syslog.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ResponseCodeEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum.LOGIN;

/**
 * <p>
 * 登录操作内容处理
 * </p>
 *
 * @author wangff
 * @since 2025/5/27
 */
@Service
@Slf4j
public class LoginResolve extends AbstractOpContentResolve {
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.LOGIN;
    }

    @Override
    protected void init() {
        super.init();
        funcMap.put(LOGIN, this::login);
    }

    private String login(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject resp = JSONObject.parseObject(sysLogDO.getRespParams());
        int code = resp.getInteger("code");
        // 登录失败的不记录
        if (ResponseCodeEnum.SUCCESS.getCode() != code) {
            sysLogDO.setDelete(true);
        }
        // 相应结果不入库
        sysLogDO.setRespParams(null);
        return null;
    }
}
