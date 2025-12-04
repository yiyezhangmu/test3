package com.coolcollege.intelligent.service.question;


import com.coolcollege.intelligent.model.question.vo.TbQuestionHistoryVO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;

import java.util.List;

/**
 * 工单处理记录
 * @author byd
 */
public interface QuestionHistoryService {


    /**
     * 查询处理记录列表
     * @param enterpriseId
     * @param unifyTaskId
     * @param storeId
     * @return
     */
    List<TbQuestionHistoryVO> selectHistoryList(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount);


    /**
     * 转交记录
     * @param enterpriseId
     * @param oldTaskSubDo
     * @param newTaskSubDo
     */
    void turnQuestionTask(String enterpriseId, TaskSubDO oldTaskSubDo, TaskSubDO newTaskSubDo);

    /**
     * 重新分配记录
     * @param enterpriseId
     * @param taskStoreDO
     * @param operUserId
     */
    void reallocateQuestionTask(String enterpriseId, TaskStoreDO taskStoreDO, String operUserId, List<String> userIdList, String typeName);

}
