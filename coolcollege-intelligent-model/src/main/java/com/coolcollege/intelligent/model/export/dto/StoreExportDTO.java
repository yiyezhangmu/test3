package com.coolcollege.intelligent.model.export.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.patrolstore.statistics.TenRegionExportDTO;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author shuchang.wei
 * @date 2021/6/16 11:06
 */
@Data
@ToString
public class StoreExportDTO extends TenRegionExportDTO {

    @Excel(name = "门店Id", orderNum = "1", width = 10)
    private String storeId;

    @Excel(name = "门店名称", orderNum = "2", width = 30)
    private String storeName;

    @Excel(name = "门店编号", orderNum = "3", width = 10)
    private String storeNum;

    @Excel(name = "门店分组", orderNum = "4", width = 30)
    private String groupName;

    @Excel(name = "所属区域", orderNum = "5", width = 30)
    private String regionName;

    @Excel(name = "门店人员", orderNum = "6", width = 30)
    private String userName;

    @Excel(name = "门店地址", orderNum = "7", width = 30)
    private String storeAddress;

    @Excel(name = "门店电话", orderNum = "8", width = 15)
    private String telephone;

    private String businessHours;

    @Excel(name = "营业开始时间", orderNum = "9", width = 10)
    private String businessStartTime;

    @Excel(name = "营业结束时间", orderNum = "10", width = 10)
    private String businessEndTime;


    @Excel(name = "门店面积（平米）", orderNum = "11", width = 10)
    private String storeAcreage;

    @Excel(name = "门店带宽（Mbps）", orderNum = "12", width = 10)
    private String storeBandwidth;

    @Excel(name = "备注", orderNum = "13", width = 30)
    private String remark;

    @Excel(name = "门店状态", orderNum = "14", width = 10)
    private String storeStatus;

    @Excel(name = "门店开店时间", orderNum = "15", width = 26)
    private String openDateStr;

    private Date openDate;

    @Excel(name = "创建人", orderNum = "16", width = 11)
    private String createName;

    @Excel(name = "创建时间", orderNum = "17", width = 26)
    private String createTimeStr;

    @Excel(name = "更新时间", orderNum = "18", width = 26)
    private String updateTimeStr;

    private Long createTime;

    private Long updateTime;

    private String extendField;

    private Long regionId;

//    @Excel(name = "一级区域[根节点]", orderNum = "15", width = 20)
//    private String regionOne;
//    @Excel(name = "二级区域", orderNum = "15", width = 20)
//    private String regionTwo;
//    @Excel(name = "三级区域", orderNum = "16", width = 20)
//    private String regionThree;
//    @Excel(name = "四级区域", orderNum = "17", width = 20)
//    private String regionFour;
//    @Excel(name = "五级区域", orderNum = "18", width = 20)
//    private String regionFive;
//    @Excel(name = "六级区域", orderNum = "19", width = 20)
//    private String regionSix;
//    @Excel(name = "七级区域", orderNum = "20", width = 20)
//    private String regionSeven;
//    @Excel(name = "八级区域", orderNum = "21", width = 20)
//    private String regionEight;
//    @Excel(name = "九级区域", orderNum = "22", width = 20)
//    private String regionNine;
//    @Excel(name = "十级区域", orderNum = "23", width = 20)
//    private String regionTe;

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
