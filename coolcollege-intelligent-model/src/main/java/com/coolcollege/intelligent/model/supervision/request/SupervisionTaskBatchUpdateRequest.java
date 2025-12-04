package com.coolcollege.intelligent.model.supervision.request;

import lombok.Data;

/**
 * @Author wxp
 * @Date 2023/2/1 19:15
 * @Version 1.0
 */
@Data
public class SupervisionTaskBatchUpdateRequest {

    /**
     * 督导任务ID集合，逗号分隔
     */
    private String supervisionTaskIds;

    private String sign;

}
