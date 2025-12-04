package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author byd
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbMetaColumnDetailVO extends TenRegionExportDTO implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * 自定义名称
     */
    @Excel(name = "检查项名称", orderNum = "6")
    private String columnName;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    @Excel(name = "门店名称", orderNum = "1")
    private String storeName;

    @Excel(name = "门店编号", orderNum = "1")
    private String storeNum;

    /**
     * 任务名称
     */
    @Excel(name = "任务名称", orderNum = "2")
    private String taskName;

    /**
     * 任务描述
     */
    @Excel(name = "任务说明", orderNum = "3")
    private String taskDesc;

    /**
     * 区域ID
     */
    private Long regionId;

    private String regionName;

    @Excel(name = "所属区域", orderNum = "0")
    private String fullRegionName;


    /**
     * 检查表id
     */
    private Long metaTableId;

    /**
     * 检查表名称
     */
    @Excel(name = "任务内容", orderNum = "5")
    private String metaTableName;

    @Excel(name = "任务有效期", orderNum = "4")
    private String validTime;

}