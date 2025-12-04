package com.coolcollege.intelligent.service.question;

import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.question.request.BuildQuestionRequest;
import com.coolcollege.intelligent.model.question.request.QuestionParentRequest;
import com.coolcollege.intelligent.model.question.request.TbQuestionRecordSearchRequest;
import com.coolcollege.intelligent.model.question.vo.TbQuestionParentInfoDetailVO;
import com.coolcollege.intelligent.model.question.vo.TbQuestionParentInfoVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskParentQuestionVO;
import com.coolcollege.intelligent.model.unifytask.vo.UnifyTaskParentItemVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @author byd
 * @date 2022-08-04 14:32
 */
public interface QuestionParentInfoService {

    /**
     * 工单列表
     *
     * @param eid
     * @param questionParentRequest
     * @return
     */
    PageInfo<TbQuestionParentInfoVO> questionList(String eid, QuestionParentRequest questionParentRequest);

    /**
     * 工单详情
     *
     * @param eid
     * @param questionParentInfoId
     * @param currentUserId
     * @return
     */
    TaskParentQuestionVO questionDetail(String eid, Long questionParentInfoId, String currentUserId);

    /**
     * 工单详情
     *
     * @param eid
     * @param questionParentInfoId
     * @return
     */
    void deleteQuestion(String eid, Long questionParentInfoId, String appType, String dingCorpId);

    /**
     * 父工单详情
     *
     * @param eid
     * @param questionParentInfoId
     * @return
     */
    TbQuestionParentInfoDetailVO questionParentInfoDetail(String eid, Long questionParentInfoId);

    /**
     * 父工单详情
     *
     * @param eid
     * @param buildQuestionRequest
     * @return
     */
    Long buildQuestion(String eid, BuildQuestionRequest buildQuestionRequest, String userId, boolean isAuto,Boolean isFilterUserAuth);


    /**
     * 工单列表导出
     * @param enterpriseId
     * @param questionParentRequest
     * @param user
     * @return
     */
    ImportTaskDO questionListExport(String enterpriseId, QuestionParentRequest questionParentRequest, CurrentUser user);

    Map<String,String> workOrderCompletionStatus(String enterpriseId, Long businessId);
}
