package com.coolcollege.intelligent.model.patrolstore.dto;

import lombok.Data;

/**
 * @author chenyupeng
 * @since 2022/4/22
 */
@Data
public class PatrolAiSignOutDTO {

    String eid;

    Long businessId;

    String userId;

    String userName;

    String appType;

    Long unifyTaskId;
}
