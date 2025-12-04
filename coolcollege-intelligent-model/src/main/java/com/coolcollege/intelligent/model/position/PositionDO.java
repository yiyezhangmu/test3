package com.coolcollege.intelligent.model.position;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName PositionDO
 * @Description 用一句话描述什么
 */
@Data
public class PositionDO {
    private Long id;
    private String positionId;
    private String name;
    private String parentId;
    private String source;
    private String type;
    private Long createTime;
    private Long updateTime;
    private String mark;
}
