package com.coolcollege.intelligent.service.unifytask;

import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskPersonDO;
import com.coolcollege.intelligent.model.unifytask.request.GetMiddlePageDataByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.vo.GetMiddlePageDataByPersonVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskPersonPatrolStatisticsVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author zhangnan
 * @date 2022-04-15 11:41
 */
public interface UnifyTaskPersonService {
    /**
     * 创建按人任务
     * @param enterpriseId
     * @param taskSubId
     * @param taskParentDO
     * @return
     */
    UnifyTaskPersonDO insertTaskPerson(String enterpriseId, String userId, Long taskSubId, TaskParentDO taskParentDO);

    /**
     * 根据子任务id获取按人任务
     * @param enterpriseId
     * @param subTaskId
     * @return
     */
    UnifyTaskPersonDO getTaskPersonBySubTaskId(String enterpriseId, Long subTaskId);

    TaskPersonPatrolStatisticsVO statisticsTaskPersonPatrol(String enterpriseId, Long subTaskId);

    /**
     * 查询中间页
     * @param enterpriseId
     * @param request
     * @return
     */
    PageInfo<GetMiddlePageDataByPersonVO> getMiddlePageDataByPerson(String enterpriseId, GetMiddlePageDataByPersonRequest request);

    /**
     * 根据父任务id删除
     * @param enterpriseId
     * @param unifyTaskId
     */
    void deleteByUnifyTaskId(String enterpriseId, Long unifyTaskId);

    /**
     * 根据子任务id查询详情
     * @param enterpriseId
     * @param subTaskId
     * @return
     */
    GetMiddlePageDataByPersonVO getTaskPersonDetail(String enterpriseId, Long subTaskId);


    void updateTaskPersonWhenCompletePotral(String enterpriseId, TbPatrolStoreRecordDO tbPatrolStoreRecordDO, String dingCorpId, String appType);

    List<UnifyTaskPersonDO> listBySubTaskIdList(String enterpriseId, List<Long> subTaskIdList);

    /**
     * 根据父任务id统计今天创建的数量
     * @param enterpriseId
     * @param taskId
     * @return
     */
    Integer countTodayByTaskId(String enterpriseId, Long taskId);
}
