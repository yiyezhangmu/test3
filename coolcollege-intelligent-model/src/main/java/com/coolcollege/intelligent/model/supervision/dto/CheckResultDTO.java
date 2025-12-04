package com.coolcollege.intelligent.model.supervision.dto;

import lombok.Data;

/**
 * @Author wxp
 * @Date 2023/2/2 20:09
 */
@Data
public class CheckResultDTO {
    /**
     * 督导任务ID
     */
    private int result;
    /**
     * 检验code
     */
    private String failReason;
}
