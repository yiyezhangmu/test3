package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;

import java.util.List;

@Data
public class StorePersonDto {
    /**
     * 门店id
     */
    private String storeId;
    /**
     * 人员Id集合
     */
    private List<String> userIdList;

    private Long loopCount;
}
