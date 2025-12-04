package com.coolcollege.intelligent.facade.passenger;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.BaseResultDTO;
import com.coolcollege.intelligent.facade.dto.passenger.PassengerSyncDTO;
import com.coolcollege.intelligent.facade.storework.StoreWorkFacade;
import com.coolcollege.intelligent.service.passengerflow.PassengerFlowService;
import com.coolstore.base.enums.AccountTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @Author suzhuhong
 * @Date 2023/2/20 17:01
 * @Version 1.0
 */
@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.PASSENGER_UNIQUE_ID ,interfaceType = PassengerFacade.class
        , bindings = {@SofaServiceBinding(bindingType = "bolt")})
@Component
public class PassengerFacadeImpl implements  PassengerFacade{

    @Resource
    PassengerFlowService passengerFlowService;

    @Override
    public BaseResultDTO passengerSync(PassengerSyncDTO passengerSyncDTO) {
        log.info("passengerSync:{}", JSONObject.toJSONString(passengerSyncDTO));
        passengerFlowService.syncHikPassengerFlow(passengerSyncDTO.getEnterpriseId(),passengerSyncDTO.getDataTime(), null);
        if (Constants.SYNC_PLATFORM_PASSENGER_EIDS.contains(passengerSyncDTO.getEnterpriseId())) {
            passengerFlowService.syncHikPassengerFlow(passengerSyncDTO.getEnterpriseId(),passengerSyncDTO.getDataTime(), AccountTypeEnum.PLATFORM);
            LocalDate now = LocalDate.now();
            LocalDate yesterdayDate = now.plusDays(-1);
            passengerFlowService.syncHikPassengerFlow(passengerSyncDTO.getEnterpriseId(), yesterdayDate.toString(), AccountTypeEnum.PLATFORM);
        }
        return BaseResultDTO.SuccessResult();
    }
}
