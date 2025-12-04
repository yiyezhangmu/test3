package com.coolcollege.intelligent.model.supervision.dto;

import lombok.Data;

/**
 * @Author wxp
 * @Date 2023/2/2 20:09
 */
@Data
public class SupervisionTaskDTO {
    /**
     * 督导任务ID
     */
    private Long taskId;
    /**
     * 检验code
     */
    private String ruleCode;
}
