package com.coolcollege.intelligent.model.patrolstore.records;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class PatrolStoreRecordsBaseDTO {
    public PatrolStoreRecordsBaseDTO() {
        super();
    }
    /**
     * 区域id
     */
    private Long areaId;
    /**
     * 区域
     */
    @Excel(name = "门店区域")
    private String areaName;

    /**
     * 门店名称
     */
    @Excel(name = "门店名称")
    private String storeName;

    /**
     * 门店id
     */
    private String storeId;

}
