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
public class PatrolStorePictureMsgDTO {
    private String eid;
    private Long businessId;
    private Long storeSceneId;
    private String patrolType;
}
