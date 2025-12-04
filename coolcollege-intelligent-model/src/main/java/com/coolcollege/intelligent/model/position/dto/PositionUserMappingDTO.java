package com.coolcollege.intelligent.model.position.dto;

import lombok.Data;

import java.util.List;

/**
 * @ClassName PositionUserMappingDTO
 * @Description 用一句话描述什么
 */
@Data
public class PositionUserMappingDTO {
    private Long id;

    private String userId;

    private String positionId;

    private Long  createTime;

    private List<String> userIds;

    private List<String> positionIds;
}
