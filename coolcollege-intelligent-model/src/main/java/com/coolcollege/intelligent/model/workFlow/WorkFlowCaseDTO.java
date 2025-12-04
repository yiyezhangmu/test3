package com.coolcollege.intelligent.model.workFlow;

import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: WorkFlowCaseDTO
 * @Description:
 * @date 2024-01-26 15:48
 */
@Data
public class WorkFlowCaseDTO {

    private String defaultGoNode;
    private String caseWhen;
    private String goNode;
    private String checkType;

}
