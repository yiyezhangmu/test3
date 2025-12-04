package com.coolcollege.intelligent.model.achievement.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @author chenyupeng
 * @since 2021/12/6
 */
@Data
public class AchievementTargetSongXiaExportVO {

    @Excel(name = "门店ID", width = 20, orderNum = "1")
    private String storeId;

    @Excel(name = "门店名称", width = 20, orderNum = "2")
    private String storeName;

    @Excel(name = "门店编号", width = 20, orderNum = "3")
    private String storeNum;

    @Excel(name = "年份", width = 20, orderNum = "4")
    private Integer year;

    @Excel(name = "1月", width = 10, orderNum = "5",type = 10)
    private String january;

    @Excel(name = "2月", width = 10, orderNum = "6",type = 10)
    private String february;

    @Excel(name = "3月", width = 10, orderNum = "7",type = 10)
    private String march;

    @Excel(name = "4月", width = 10, orderNum = "8",type = 10)
    private String april;

    @Excel(name = "5月", width = 10, orderNum = "9",type = 10)
    private String may;

    @Excel(name = "6月", width = 10, orderNum = "10",type = 10)
    private String june;

    @Excel(name = "7月", width = 10, orderNum = "11",type = 10)
    private String july;

    @Excel(name = "8月", width = 10, orderNum = "12",type = 10)
    private String august;

    @Excel(name = "9月", width = 10, orderNum = "13",type = 10)
    private String september;

    @Excel(name = "10月", width = 10, orderNum = "14",type = 10)
    private String october;

    @Excel(name = "11月", width = 10, orderNum = "15",type = 10)
    private String november;

    @Excel(name = "12月", width = 10, orderNum = "16",type = 10)
    private String december;

    @Excel(name = "门店状态", width = 10, orderNum = "17",replace = {"营业_open","闭店_closed","未开业_not_open"})
    private String status;
}
