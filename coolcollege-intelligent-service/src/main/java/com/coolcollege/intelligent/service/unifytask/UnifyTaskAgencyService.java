package com.coolcollege.intelligent.service.unifytask;

import com.coolcollege.intelligent.model.unifytask.dto.CommissionTotalDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskReminderDTO;
import com.coolcollege.intelligent.model.unifytask.query.QuestionQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskAgencyQuery;
import com.coolcollege.intelligent.model.unifytask.vo.PatrolPlanVO;
import com.coolcollege.intelligent.model.unifytask.vo.QuestionToDoVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/10 21:12
 */
public interface UnifyTaskAgencyService {

    /**
     * 查询代办列表
     * @param enterpriseId
     * @param query
     * @param user
     * @param isNewTodoTask
     * @return
     */
    PageInfo getTaskAgencyList(String enterpriseId, TaskAgencyQuery query, CurrentUser user, Boolean isNewTodoTask);

    /**
     * 查询催办列表
     * @param enterpriseId
     * @param query
     * @author: xugangkun
     * @return com.github.pagehelper.PageInfo
     * @date: 2021/11/9 11:27
     */
    PageInfo getReminderList(String enterpriseId, TaskReminderDTO query);

    /**
     * 工单待办列表
     * @param enterpriseId
     * @param query
     * @param user
     * @return
     */
    PageInfo<QuestionToDoVO> questionToDoList(String enterpriseId, QuestionQuery query, CurrentUser user);

    /**
    * @Description:  查询待办条数
    * @Param: [enterpriseId, user]
    * @Author: tangziqi
    * @Date: 2023/6/7~20:15
    */
    CommissionTotalDTO getTotal(String enterpriseId, TaskAgencyQuery query);


    /**
     * 当前登录人所有的巡店计划 包括完成未完成
     * @param enterpriseId
     * @param user
     * @return
     */
    PageInfo<PatrolPlanVO> getPatrolPlanList(String enterpriseId, CurrentUser user,Integer completeFlag,Integer pageSize,Integer pageNum);

}
