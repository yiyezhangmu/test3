package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

/**
 * @Author wxp
 * @Date 2023/2/7 17:09
 * @Version 1.0
 */
@Data
public class OpenApiUpdateSupervisionTaskDTO {

    /**
     * 督导任务ID集合，逗号分隔
     */
    private String supervisionTaskIds;
}
