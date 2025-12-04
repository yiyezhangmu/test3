package com.coolcollege.intelligent.facade.open.api.question;

import com.coolcollege.intelligent.facade.dto.openApi.CreateQuestionOrderDTO;
import com.coolcollege.intelligent.facade.dto.openApi.QuestionDTO;
import com.coolstore.base.response.rpc.OpenApiResponseVO;

/**
 * @Author suzhuhong
 * @Date 2022/7/11 15:38
 * @Version 1.0
 */
public interface QuestionApi {

    /**
     * 工单列表
     * @param questionDTO
     * @return
     */
    OpenApiResponseVO QuestionList(QuestionDTO questionDTO);


    /**
     * 工单详情
     * @param questionDTO
     * @return
     */
    OpenApiResponseVO QuestionDetail(QuestionDTO questionDTO);

    /**
     * 创建工单
     * @param param
     * @return
     */
    OpenApiResponseVO createQuestionOrder(CreateQuestionOrderDTO param);


}
