package com.coolcollege.intelligent.model.export.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * describe:导出门店基本信息
 *
 * @author zhouyiping
 * @date 2021/07/06
 */
@Data
public class StoreBaseInfoExportDTO {
    @Excel(name = "门店Id",orderNum = "1", width = 10)
    private String storeId;

    @Excel(name = "门店名称",orderNum = "2",width = 30)
    private String storeName;

    @Excel(name = "门店编号",orderNum = "3",width = 10)
    private String storeNum;

    @Excel(name = "门店分组",orderNum = "4",width = 30)
    private String groupName;

//    @Excel(name = "所属区域",orderNum = "5",width = 30)
//    private String regionName;

//    @Excel(name = "门店人员",orderNum = "6",width = 30)
//    private String userName;

    @Excel(name = "门店地址",orderNum = "5",width = 30)
    private String storeAddress;

    @Excel(name = "门店电话",orderNum = "6",width = 15)
    private String telephone;

    private String businessHours;

    @Excel(name = "营业开始时间",orderNum = "7",width = 10)
    private String businessStartTime;

    @Excel(name = "营业结束时间",orderNum = "8",width = 10)
    private String businessEndTime;


    @Excel(name = "门店面积（平米）",orderNum = "9",width = 10)
    private String storeAcreage;

    @Excel(name = "门店带宽（Mbps）",orderNum = "10",width = 10)
    private String storeBandwidth;

    @Excel(name = "备注",orderNum = "11",width = 30)
    private String remark;

    @Excel(name = "门店状态",orderNum = "12",width = 10)
    private String storeStatus;

    private String extendField;

    private Long regionId;
}
