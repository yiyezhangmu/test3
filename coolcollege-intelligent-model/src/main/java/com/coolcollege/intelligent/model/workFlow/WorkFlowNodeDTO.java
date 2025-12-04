package com.coolcollege.intelligent.model.workFlow;

import lombok.Data;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: WorkFlowNodeDTO
 * @Description:
 * @date 2024-01-26 15:46
 */
@Data
public class WorkFlowNodeDTO {

    private String nodeNo;
    private String nodeType;
    private String nodeName;
    private String callbackType;
    private List<WorkFlowCaseDTO> flowCases;
    private Integer executeCount;
    private Boolean startNode;
    private Boolean endNode;

}
