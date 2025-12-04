package com.coolcollege.intelligent.model.region.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/9/2 17:50
 * @Version 1.0
 */
@Data
public class LimitDTO {

    private Long startTime;

    private Integer count;

    public LimitDTO(Long startTime, Integer count) {
        this.startTime = startTime;
        this.count = count;
    }
}
