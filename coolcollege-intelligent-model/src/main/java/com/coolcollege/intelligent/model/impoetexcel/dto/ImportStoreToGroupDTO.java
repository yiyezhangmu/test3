package com.coolcollege.intelligent.model.impoetexcel.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @author 邵凌志
 * @date 2021/2/1 14:52
 */
@Data
public class ImportStoreToGroupDTO {

    @Excel(name = "描述", width = 30)
    private String dec;

    @Excel(name = "门店名称", orderNum = "1", width = 10)
    private String storeName;

    @Excel(name = "门店编号", orderNum = "2", width = 10)
    private String storeNum;

    @Excel(name = "门店区域", orderNum = "3", width = 10)
    private String regionName;
}
