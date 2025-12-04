package com.coolcollege.intelligent.model.position;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName PositionUserMappingDO
 * @Description
 */
@Data
public class PositionUserMappingDO {
    private Long id;

    private String userId;

    private String positionId;

    private Long createTime;
}
