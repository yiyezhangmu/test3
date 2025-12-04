package com.coolcollege.intelligent.model.position.dto;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName PositionDTO
 * @Description 用一句话描述什么
 */
@Data
public class PositionDTO {
    private Long id;
    private String positionId;
    private String name;
    private String parentId;
    private String source;
    private String type;
    private Long createTime;
    private Long updateTime;
    private Long userCount;
    private String key;
    private String mark;
}
