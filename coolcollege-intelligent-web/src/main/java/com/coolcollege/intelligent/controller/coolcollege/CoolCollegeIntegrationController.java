package com.coolcollege.intelligent.controller.coolcollege;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.coolcollege.CoolCollegeMsgDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolstore.base.enums.AppTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author: xuanfeng
 * @date: 2022-04-21 15:14
 */
@RestController
@RequestMapping({"/v3/enterprises/{enterprise-id}/college/integration", "/v3/enterprises/college/integration"})
@BaseResponse
@Slf4j
public class CoolCollegeIntegrationController {
    @Autowired
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;
    @Autowired
    private EnterpriseConfigService enterpriseConfigService;
    @Resource
    private EnterpriseSettingService enterpriseSettingService;

    @GetMapping("/getLoginCoolCollegeTicket")
    public ResponseResult getLoginCoolCollegeTicket(@PathVariable("enterprise-id") String enterpriseId, @RequestParam("userId") String userId){
        return ResponseResult.success(coolCollegeIntegrationApiService.getLoginCoolCollegeTicket(userId, enterpriseId));
    }

    @PostMapping("/sendMsg")
    public ResponseResult sendCoolCollegeMsg(String corp_id, @RequestBody CoolCollegeMsgDTO dto) {
        coolCollegeIntegrationApiService.sendCoolCollegeMsg(dto, corp_id);
        return ResponseResult.success(Boolean.TRUE);
    }

    @GetMapping("/getCoolCollegeTodoList")
    public ResponseResult getCoolCollegeTodoList(@PathVariable("enterprise-id") String enterpriseId,
                                                 @RequestParam("userId") String userId,
                                                 @RequestParam("pageNum") Integer pageNum,
                                                 @RequestParam("pageSize") Integer pageSize,
                                                 @RequestParam(value = "type", required = false) String type){
        return ResponseResult.success(coolCollegeIntegrationApiService.getCoolCollegeTodoList(enterpriseId, userId, pageSize, pageNum, type));
    }

    @GetMapping("/getEnterpriseIncludeTrainingModule")
    public ResponseResult getEnterpriseIncludeTrainingModule(@PathVariable("enterprise-id") String enterpriseId){
        return ResponseResult.success(coolCollegeIntegrationApiService.getEnterpriseIncludeTrainingModule(enterpriseId));
    }

    @GetMapping("/getLoginCoolCollegeTicketForOneParty")
    public ResponseResult getLoginCoolCollegeTicketForOneParty(@RequestParam("corpId") String corpId,
                                                    @RequestParam("userId") String userId){
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByCorpId(corpId, AppTypeEnum.ONE_PARTY_APP.getValue());
        if (Objects.isNull(enterpriseConfig)) {
            log.error(ErrorCodeEnum.OP_9000000.getMessage());
            return ResponseResult.success(null);
        }
        EnterpriseSettingDO setting = enterpriseSettingService.selectByEnterpriseId(enterpriseConfig.getEnterpriseId());
        if (Objects.isNull(setting) || !setting.getAccessCoolCollege() || StringUtils.isBlank(enterpriseConfig.getCoolCollegeEnterpriseId()) || StringUtils.isBlank(enterpriseConfig.getCoolCollegeSecret())) {
            log.error(ErrorCodeEnum.COOL_STORE_OPEN_TRAINING.getMessage());
            return ResponseResult.success(null);
        }
        return ResponseResult.success(coolCollegeIntegrationApiService.getLoginCoolCollegeTicket(userId, enterpriseConfig.getEnterpriseId()));
    }
}
