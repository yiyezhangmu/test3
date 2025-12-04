package com.coolcollege.intelligent.facade.open.api.question;

import com.coolcollege.intelligent.facade.dto.openApi.QuestionOrderDTO;
import com.coolstore.base.response.rpc.OpenApiResponseVO;

/**
 * @Author suzhuhong
 * @Date 2022/7/11 15:38
 * @Version 1.0
 */
public interface YunDaApi {

    /**
     * 工单列表
     * @param questionOrderDTO
     * @return
     */
    OpenApiResponseVO<Boolean> sendQuestionOrder(QuestionOrderDTO questionOrderDTO);

}
