package com.coolcollege.intelligent.service.unifytask;

import com.coolcollege.intelligent.model.question.request.BuildQuestionRequest;
import com.coolcollege.intelligent.model.supervision.request.AddSupervisionTaskParentRequest;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentItemDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.request.BuildByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.request.GetTaskByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.vo.GetTaskByPersonVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.Date;
import java.util.List;

/**
 * 父任务相关业务
 * @author zhangnan
 * @date 2022-04-14 16:23
 */
public interface UnifyTaskParentService {

    /**
     * 新增父任务-按人任务
     * 1创建父任务
     * 2根据选择的人员/职位（拆分人员）发送mq创建子任务和按人任务
     * @param enterpriseId 企业id
     * @param user CurrentUser
     * @param request BuildForPersonRequest
     * @return TaskParentDO
     */
    TaskParentDO insertByPerson(String enterpriseId, CurrentUser user, BuildByPersonRequest request);

    void splitTaskForPerson(String enterpriseId, String dbName, List<TaskProcessDTO> processList, TaskParentDO taskParentDO);

    /**
     * 查询父任务-按人任务（分页）
     * @param enterpriseId
     * @param request
     * @return
     */
    PageInfo<GetTaskByPersonVO> getTaskParentByPerson(String enterpriseId, GetTaskByPersonRequest request);

    /**
     * 根据父任务id查询
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    GetTaskByPersonVO getTaskParentById(String enterpriseId, Long unifyTaskId);

    void updateParentStatusByTaskId(String enterpriseId, String parentStatus, Long taskId);

    String setSchedulerForOnce(String enterpriseId, Long taskId, Date beginTime, Integer offset,int isOperateOverdue);

    /**
     * 父任务id
     * @param enterpriseId
     * @param taskId
     * @return
     */
    TaskParentDO getTaskParentDOById(String enterpriseId, Long taskId);

    TaskParentDO insertQuestionOrder(String enterpriseId, BuildQuestionRequest buildQuestionRequest, String userId,
                                     List<UnifyTaskParentItemDO> unifyTaskParentItemDOList);


    /**
     * 督导任务添加父任务
     * @param enterpriseId
     * @param user
     * @param request
     * @return
     */
    TaskParentDO addSupervisionTaskParent(String enterpriseId, CurrentUser user, AddSupervisionTaskParentRequest request);

    Boolean updateSuperVisionParent(String enterpriseId, CurrentUser user, AddSupervisionTaskParentRequest request);


}
