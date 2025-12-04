package com.coolcollege.intelligent.controller.question;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.service.question.QuestionOrderTaskService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/31 13:58
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/questionorder")
@BaseResponse
@Slf4j
public class QuestionOrderController {

    @Autowired
    private QuestionOrderTaskService questionOrderTaskService;

    /**
     * 父任务id获取问题单详情
     * @param enterpriseId
     * @param taskQuestionId
     * @return
     */
    @GetMapping(path = "/question/detail")
    public ResponseResult getDisplaySubDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                              @RequestParam(value = "taskQuestionId") Long taskQuestionId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(questionOrderTaskService.getQuestionDetailByTaskId(enterpriseId, taskQuestionId));
    }
}
