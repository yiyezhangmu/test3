package com.coolcollege.intelligent.model.region.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author byd
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatrolStoreScoreMsgDTO {
    private String eid;
    private Long businessId;

    private Long subTaskId;

    private String supervisorId;
    private String name;
    /**
     * 是否需要再次发起工单
     */
    private Boolean needAgainSendProblem;

}
