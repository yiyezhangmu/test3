package com.coolcollege.intelligent.service.task;

import com.coolcollege.intelligent.model.task.param.DealParam;
import com.coolcollege.intelligent.model.task.param.DealTaskParam;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/14 15:27
 */
public interface TaskDealService {

    /**
     * 批量任务处理-有审批流无关联检查表的任务
     *  @param enterpriseId
     * @param paramList
     * @param user
     */
    void batchDeal(String enterpriseId, List<DealParam> paramList, CurrentUser user);

    /**
     * 批量任务处理-有审批流无关联检查表的任务
     *  @param enterpriseId
     * @param dealTaskParam
     * @param user
     */
    void dealTask(String enterpriseId, DealTaskParam dealTaskParam, CurrentUser user);

    /**
     * 批量整改/审批
     * @param enterpriseId
     * @param paramList
     * @param user
     */
    void batchDealQuestion(String enterpriseId, List<DealTaskParam> paramList, CurrentUser user);

}
