package com.coolcollege.intelligent.model.question.request;

import com.coolcollege.intelligent.model.patrolstore.request.ExportBaseRequest;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/8/17 11:39
 * @Version 1.0
 */
@Data
public class ExportRegionQuestionReportRequest extends ExportBaseRequest {

    /**
     * 工单区域列表导出request
     */
    RegionQuestionReportRequest request;

    /**
     * 工单列表导出request（父任务）
     */
    QuestionParentRequest questionParentRequest;
}
