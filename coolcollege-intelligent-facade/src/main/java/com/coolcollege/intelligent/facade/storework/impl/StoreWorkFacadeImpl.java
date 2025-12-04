package com.coolcollege.intelligent.facade.storework.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.storework.StoreWorkCycleEnum;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.BaseResultDTO;
import com.coolcollege.intelligent.facade.dto.storework.StoreWorkResolveDTO;
import com.coolcollege.intelligent.facade.storework.StoreWorkFacade;
import com.coolcollege.intelligent.model.storework.request.StoreTaskResolveRequest;
import com.coolcollege.intelligent.service.storework.StoreWorkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/9/8 15:42
 * @Version 1.0
 */
@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.STORE_WORK_UNIQUE_ID ,interfaceType = StoreWorkFacade.class
        , bindings = {@SofaServiceBinding(bindingType = "bolt")})
@Component
public class StoreWorkFacadeImpl implements StoreWorkFacade {
    @Resource
    StoreWorkService storeWorkService;

    @Override
    public BaseResultDTO taskResolve(StoreWorkResolveDTO storeWorkResolveDTO) {
        log.info("店务分解开始 param:{}", JSONObject.toJSONString(storeWorkResolveDTO));
        if (storeWorkResolveDTO.getCurrentDate()==null || storeWorkResolveDTO.getEnterpriseId()==null){
            return BaseResultDTO.FailResult("必填参数不能不空");
        }
        //日清每天都发
        log.info("店务分解日清开始");
        this.dayClearTask(storeWorkResolveDTO);
        //是否是周一
        Date currentDate = storeWorkResolveDTO.getCurrentDate();
        Integer weekOfDate = DateUtils.getWeekOfDate(currentDate);
        if (Constants.INDEX_ONE.equals(weekOfDate)){
            log.info("店务分解周清开始");
            weekClearTask(storeWorkResolveDTO);
        }
        //是否是当月1号
        Integer monthOfDate = DateUtils.getMonthOfDate(currentDate);
        if (Constants.INDEX_ONE.equals(monthOfDate)){
            log.info("店务分解月清开始");
            monthClearTask(storeWorkResolveDTO);
        }
        log.info("店务分解完成 {}",JSONObject.toJSONString(BaseResultDTO.SuccessResult()));
        return BaseResultDTO.SuccessResult();
    }

    @Override
    public BaseResultDTO dayClearTask(StoreWorkResolveDTO  storeWorkResolveDTO) {
        //设置为日清
        storeWorkResolveDTO.setWorkCycle(StoreWorkCycleEnum.DAY.getCode());
        StoreTaskResolveRequest storeTaskResolveRequest = getStoreTaskResolveRequest(storeWorkResolveDTO);
        storeWorkService.dayClearTaskResolve(storeTaskResolveRequest);
        return BaseResultDTO.SuccessResult();
    }

    @Override
    public BaseResultDTO weekClearTask(StoreWorkResolveDTO storeWorkResolveDTO) {
        //设置为日清
        storeWorkResolveDTO.setWorkCycle(StoreWorkCycleEnum.WEEK.getCode());
        StoreTaskResolveRequest storeTaskResolveRequest = getStoreTaskResolveRequest(storeWorkResolveDTO);
        storeWorkService.dayClearTaskResolve(storeTaskResolveRequest);
        return BaseResultDTO.SuccessResult();
    }

    @Override
    public BaseResultDTO monthClearTask(StoreWorkResolveDTO storeWorkResolveDTO) {
        //设置为日清
        storeWorkResolveDTO.setWorkCycle(StoreWorkCycleEnum.MONTH.getCode());
        StoreTaskResolveRequest storeTaskResolveRequest = getStoreTaskResolveRequest(storeWorkResolveDTO);
        storeWorkService.dayClearTaskResolve(storeTaskResolveRequest);
        return BaseResultDTO.SuccessResult();
    }

    /**
     * 封装数据
     * @param storeWorkResolveDTO
     * @return
     */
    public StoreTaskResolveRequest getStoreTaskResolveRequest(StoreWorkResolveDTO storeWorkResolveDTO){
        StoreTaskResolveRequest storeTaskResolveRequest = new StoreTaskResolveRequest();
        storeTaskResolveRequest.setCurrentDate(storeWorkResolveDTO.getCurrentDate());
        storeTaskResolveRequest.setWorkCycle(storeWorkResolveDTO.getWorkCycle());
        storeTaskResolveRequest.setEnterpriseId(storeWorkResolveDTO.getEnterpriseId());
        storeTaskResolveRequest.setPushFlag(storeWorkResolveDTO.getPushFlag());
        storeTaskResolveRequest.setReissueFlag(storeWorkResolveDTO.getReissueFlag());
        storeTaskResolveRequest.setStoreWorkId(storeWorkResolveDTO.getStoreWorkId());
        return storeTaskResolveRequest;
    }

}
