package com.coolcollege.intelligent.service.newMode.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.remoting.util.StringUtils;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.StringUtil;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.newMode.EnterpriseTypeRequest;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.newMode.NewModeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
@Slf4j
public class NewModeServiceImpl implements NewModeService {

    @Resource
    EnterpriseConfigService enterpriseConfigService;
    @Resource
    private EnterpriseService enterpriseService;

    @Override
    public String getAppType(EnterpriseTypeRequest request) {
        log.info("getAppType request:{}", JSONObject.toJSONString(request));
        if (StringUtils.isBlank(request.getDingCorpId())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByDingCorpId(request.getDingCorpId());
        if (Objects.isNull(enterpriseConfigDO)){
            throw new ServiceException(ErrorCodeEnum.NOT_FOUND_ENTERPRISE);
        }
        EnterpriseDO enterprise = enterpriseService.selectById(enterpriseConfigDO.getEnterpriseId());
        if (EnterpriseStatusEnum.FREEZE.getCode() == enterprise.getStatus()){
            throw new ServiceException(ErrorCodeEnum.ENTERPRISE_FROZEN);
        }
        return enterpriseConfigDO.getAppType();
    }
}
