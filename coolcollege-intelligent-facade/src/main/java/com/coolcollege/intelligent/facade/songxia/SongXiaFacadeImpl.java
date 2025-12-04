package com.coolcollege.intelligent.facade.songxia;


import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.ResultDTO;
import com.coolcollege.intelligent.service.achievement.AchievementTaskRecordService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.SONGXIA_FACADE_UNIQUE_ID, interfaceType = SongXiaFacade.class, bindings = {@SofaServiceBinding(bindingType = "bolt")})
@Component
public class SongXiaFacadeImpl implements SongXiaFacade{

    @Resource
    private AchievementTaskRecordService taskRecordService;

    @Override
    public ResultDTO<Integer> sendRemindMsg(String enterpriseId) throws ApiException {
        try {
            log.info("发送撤样提醒：eid：{}", enterpriseId);
            boolean b = taskRecordService.sendRemindMsg(enterpriseId);
        }catch (Exception e){
            throw new ApiException(e);
        }
        return ResultDTO.SuccessResult();
    }
}
