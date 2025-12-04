package com.coolcollege.intelligent.facade.qyy;

import com.coolcollege.intelligent.facade.dto.ResultDTO;
import com.taobao.api.ApiException;

/**
 * @author zhangchenbiao
 * @FileName: QyyCardTimerService
 * @Description: 群应用卡片定时
 * @date 2023-04-27 10:12
 */
public interface QyyCardTimerFacade {

    /**
     * 批量发送主推款卡片
     * @param enterpriseId
     * @return
     */
    ResultDTO<Integer> batchSendRecommendStyle(String enterpriseId) throws ApiException;


    /**
     * 发送个人目标
     * @param enterpriseId
     * @return
     */
    ResultDTO<Integer> sendTodayUserGoal(String enterpriseId)  throws ApiException;

    /**
     * 卓诗尼-推送用户目标
     */
    ResultDTO<Integer> sendUserGoal(String enterpriseId) throws ApiException;


    ResultDTO<Integer> sendWeeklyNewsPaperCount(String enterpriseId) throws ApiException;

    ResultDTO<Integer> sendWeeklyNewsPaperDing(String enterpriseId) throws ApiException;

}
