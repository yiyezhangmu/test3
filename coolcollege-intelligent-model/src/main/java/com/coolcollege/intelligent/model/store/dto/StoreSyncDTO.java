package com.coolcollege.intelligent.model.store.dto;

import lombok.Data;

import java.util.List;

/**
 * byd
 * @author byd
 */
@Data
public class StoreSyncDTO {

    /**
     * 门店id
     */
    private Long id;

    /**
     * 区域钉钉id
     */
    private String synDingDeptId;
}
