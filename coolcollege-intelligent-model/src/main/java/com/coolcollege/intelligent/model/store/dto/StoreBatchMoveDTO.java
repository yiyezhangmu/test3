package com.coolcollege.intelligent.model.store.dto;

import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/7/9 15:30
 */
@Data
public class StoreBatchMoveDTO {

    /**
     * 区域id
     */
    private String areaId;

    /**
     * 门店id列表
     */
    private List<String> storeIds;
}
