package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @author byd
 * @date 2022-07-01 14:43
 */
@Data
public class TenRegionExportDTO {
    @Excel(name = "一级区域「根节点」", orderNum = "140")
    private String firstRegionName;

    @Excel(name = "二级区域", orderNum = "141")
    private String secondRegionName;

    @Excel(name = "三级区域", orderNum = "142")
    private String thirdRegionName;

    @Excel(name = "四级区域", orderNum = "143")
    private String fourRegionName;

    @Excel(name = "五级区域", orderNum = "144")
    private String fiveRegionName;

    @Excel(name = "六级区域", orderNum = "145")
    private String sixRegionName;

    @Excel(name = "七级区域", orderNum = "146")
    private String sevenRegionName;

    @Excel(name = "八级区域", orderNum = "147")
    private String eightRegionName;

    @Excel(name = "九级区域", orderNum = "148")
    private String nineRegionName;

    @Excel(name = "十级区域", orderNum = "149")
    private String tenRegionName;

}
