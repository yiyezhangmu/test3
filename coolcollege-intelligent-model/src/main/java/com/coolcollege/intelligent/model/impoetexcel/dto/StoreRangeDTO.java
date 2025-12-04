package com.coolcollege.intelligent.model.impoetexcel.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2023/2/3 17:47
 * @Version 1.0
 */
@Data
public class StoreRangeDTO {

    @Excel(name = "门店名称", orderNum = "1", width = 10)
    private String storeName;

    @Excel(name = "门店ID", orderNum = "2", width = 10)
    private String storeId;

    private Integer index;

    private String failMsg;
}
