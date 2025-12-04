package com.coolcollege.intelligent.service.question;

import com.coolcollege.intelligent.model.patrolstore.dto.StaColumnDTO;
import com.coolcollege.intelligent.model.question.dto.QuestionReportDTO;
import com.coolcollege.intelligent.model.question.vo.QuestionDetailVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/16 19:59
 */
public interface QuestionOrderTaskService {

    /**
     * 获取问题单报表相关数据
     * @param enterpriseId
     * @param taskIdList
     * @return
     */
    List<QuestionReportDTO> getQuestionReportData(String enterpriseId, List<Long> taskIdList);
    /**
     * 父任务id获取问题单详情-只适用于父任务发起是单门店的工单
     * @param enterpriseId
     * @param taskQuestionId
     * @return
     */
    QuestionDetailVO getQuestionDetailByTaskId(String enterpriseId, Long taskQuestionId);
}
