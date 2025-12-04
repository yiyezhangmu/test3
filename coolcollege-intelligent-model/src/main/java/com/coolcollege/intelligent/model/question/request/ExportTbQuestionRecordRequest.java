package com.coolcollege.intelligent.model.question.request;

import com.coolcollege.intelligent.model.patrolstore.request.ExportBaseRequest;
import lombok.Data;

/**
 * 导出问题工单请求
 * @author zhangnan
 * @date 2021-12-28 16:16
 */
@Data
public class ExportTbQuestionRecordRequest extends ExportBaseRequest{

    private TbQuestionRecordSearchRequest request;
}
