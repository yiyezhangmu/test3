package com.coolcollege.intelligent.model.activity.dto;

import com.coolcollege.intelligent.model.patrolstore.request.ExportBaseRequest;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2023/7/6 17:18
 * @Version 1.0
 */
@Data
public class ActivityUserDTO extends ExportBaseRequest {

    private String enterpriseId;

    private Long activityId;

}
