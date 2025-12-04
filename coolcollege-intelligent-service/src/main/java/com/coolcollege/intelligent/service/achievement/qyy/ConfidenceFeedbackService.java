package com.coolcollege.intelligent.service.achievement.qyy;

import com.coolcollege.intelligent.model.achievement.qyy.dto.ConfidenceFeedbackPageDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.SubmitConfidenceFeedbackDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ConfidenceFeedbackDetailVO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: ConfidenceFeedbackService
 * @Description: 信心反馈
 * @date 2023-04-12 19:32
 */
public interface ConfidenceFeedbackService {


    /**
     * 提交信心反馈
     * @param enterpriseId
     * @param userId
     * @param username
     * @param param
     * @return
     */
    Boolean submitConfidenceFeedback(String enterpriseId, String userId, String username, SubmitConfidenceFeedbackDTO param);


    /**
     * 获取信心反馈详情
     * @param enterpriseId
     * @param id
     * @return
     */
    ConfidenceFeedbackDetailVO getConfidenceFeedback(String enterpriseId, Long id);

    /**
     * 分页获取信心反馈
     * @param enterpriseId
     * @param param
     * @return
     */
    PageInfo<ConfidenceFeedbackDetailVO> getConfidenceFeedbackPage(String enterpriseId, ConfidenceFeedbackPageDTO param);


    /**
     * 导出
     * @param enterpriseId
     * @param param
     * @return
     */
    List<ConfidenceFeedbackDetailVO> exportConfidenceFeedbackPage(String enterpriseId, ConfidenceFeedbackPageDTO param);

    /**
     * 导出
     * @param enterpriseId
     * @param param
     * @param user
     * @return
     */
    ImportTaskDO exportConfidenceFeedbackPage(String enterpriseId, ConfidenceFeedbackPageDTO param, CurrentUser user);


    /**
     * 删除信心反馈
     * @param enterpriseId
     * @param id
     * @return
     */
    Boolean deleteConfidenceFeedback(String enterpriseId, Long id);


}
