package com.coolcollege.intelligent.model.workFlow;

import lombok.Data;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: AddWorkflowTemplateDTO
 * @Description:
 * @date 2024-01-26 15:45
 */
@Data
public class AddWorkflowTemplateDTO {
    private List<WorkFlowNodeDTO> nodes;
    private String createUserId;
    private String updateUserId;

}
