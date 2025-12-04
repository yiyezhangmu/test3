package com.coolcollege.intelligent.model.store;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @ClassName StoreDO
 * @Description 用一句话描述什么
 */
@Data
public class StoreGroupExportDTO {

    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 门店名称
     */
    @Excel(name = "门店名称")
    private String storeName;

    /**
     * 门店名称
     */
    @Excel(name = "门店分组")
    private String storeGroupName;

    /**
     * 门店编号
     */
    @Excel(name = "门店编号")
    private String storeNum;

    /**
     * 门店编号
     */
    @Excel(name = "门店区域")
    private String regionName;

}
