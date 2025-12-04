package com.coolcollege.intelligent.service.unifytask;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.store.dto.BasicsStoreDTO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.ParentTaskDTO;
import com.coolcollege.intelligent.model.unifytask.dto.SubTaskDTO;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;
import com.coolcollege.intelligent.model.unifytask.query.TbDisplayQuery;
import com.coolcollege.intelligent.model.unifytask.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/27 20:23
 */
public interface UnifyTaskDisplayService {

    /**
     * 我的父任务列表
     * @param enterpriseId
     * @param query
     * @param user
     * @return
     */
    ParentTaskDTO getDisplayParent(String enterpriseId, DisplayQuery query, CurrentUser user);


    /**
     * 父任务跳中间页
     * @param enterpriseId
     * @param query
     * @param user
     * @return
     */
    ParentTaskMiddlePageVO getParentMiddlePageData(String enterpriseId, TbDisplayQuery query, CurrentUser user);

    /**
     * 我的子任务列表
     * @param enterpriseId
     * @param query
     * @param user
     * @return
     */
    SubTaskDTO getDisplaySub(String enterpriseId, DisplayQuery query, CurrentUser user);

    /**
     * 父任务详情
     * @param enterpriseId
     * @param taskId
     * @return
     */
    TaskParentDetailVO getDisplayParentDetail(String enterpriseId, Long taskId);

    /**
     * 获取父任务任务范围
     * @param eid
     * @param taskId
     * @return
     */
    List<BasicsStoreDTO> getStoreScopeInput(String eid,Long taskId);
    /**
     * 子任务详情
     * @param enterpriseId
     * @param subTaskId
     * @return
     */
    TaskSubVO getDisplaySubDetail(String enterpriseId, Long subTaskId, String userId, Long taskStoreId);

    List<Object> getSubOperHistoryData(String enterpriseId, Long subTaskId);

    /**
     * 通过父任务id获取详情
     * @param enterpriseId
     * @param taskId
     * @param user
     * @return
     */
    TaskSubVO getDetailByParentId(String enterpriseId, Long taskId, CurrentUser user);
    /**
     * 子任务详情-批量
     * @param enterpriseId
     * @param subTaskIdList
     * @return
     */
    List<TaskSubVO> getDisplayBatchSubDetail(String enterpriseId, List<Long> subTaskIdList , String userId, List<Long> taskStoreIdList);

    /**
     * 父任务信息获取
     * @param enterpriseId
     * @param taskParentDOList
     * @return
     */
    List<TaskParentVO> getParentInfo(String enterpriseId, List<TaskParentDO> taskParentDOList);


    /**
     * 所有子任务都会显示
     * 分组统计显示最近的子任务结果
     * 支持多个父任务查询所有最新子任务
     *
     * @param oldList
     * @return
     */
    List<TaskSubVO> dealAllSubGroup(List<TaskSubVO> oldList);

    /**
     * 判断用户权限
     * @param m
     * @param user
     * @param userId
     * @param overTask
     */
    void dealButtonShow(TaskSubVO m, Map<String, List<PersonDTO>> user,
                        String userId,Boolean overTask, Boolean handlerOvertimeTaskContinue, Boolean approveOvertimeTaskContinue);

}
