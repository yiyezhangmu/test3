package com.coolcollege.intelligent.model.impoetexcel.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * @author 邵凌志
 * @date 2020/12/14 10:30
 */
@Data
public class StoreImportDTO {

    @Excel(name = "描述", width = 30)
    private String dec;

    @Excel(name = "门店名称", orderNum = "1", width = 10)
    private String storeName;

    @Excel(name = "门店编号", orderNum = "2", width = 10)
    private String storeNum;

    @Excel(name = "门店分组", orderNum = "3", width = 10)
    private String storeGroup;

    @Excel(name = "区域名称", orderNum = "4", width = 10)
    private String regionName;

//    @Excel(name = "门店人员", orderNum = "5", width = 10)
//    private String users;

    @Excel(name = "门店地址", orderNum = "5", width = 10)
    private String storeAddress;

    @Excel(name = "门店电话", orderNum = "6", width = 10)
    private String telephone;

    @Excel(name = "开始营业时间", orderNum = "7", width = 15)
    private String startTime;

    @Excel(name = "结束营业时间", orderNum = "8", width = 15)
    private String endTime;

    @Excel(name = "门店面积（平米）", orderNum = "9", width = 20)
    private String storeAcreage;

    @Excel(name = "门店带宽（Mbps）", orderNum = "10", width = 20)
    private String storeBandwidth;

    @Excel(name = "门店开店时间", orderNum = "11", width = 20)
    private Date openDate;

    @Excel(name = "备注", orderNum = "12", width = 50)
    private String remark;
}
