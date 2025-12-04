package com.coolcollege.intelligent.facade.storework;

import com.coolcollege.intelligent.facade.dto.BaseResultDTO;
import com.coolcollege.intelligent.facade.dto.storework.StoreWorkResolveDTO;

/**
 * @Author suzhuhong
 * @Date 2022/9/8 15:34
 * @Version 1.0
 */
public interface StoreWorkFacade {

    BaseResultDTO taskResolve(StoreWorkResolveDTO storeWorkResolveDTO);

    /**
     * 日清任务分解
     * @return
     */
    BaseResultDTO dayClearTask(StoreWorkResolveDTO storeWorkResolveDTO);

    /**
     * 周清任务分解
     * @return
     */
    BaseResultDTO weekClearTask(StoreWorkResolveDTO storeWorkResolveDTO);

    /**
     * 月清任务分解
     * @return
     */
    BaseResultDTO monthClearTask(StoreWorkResolveDTO storeWorkResolveDTO);
}
