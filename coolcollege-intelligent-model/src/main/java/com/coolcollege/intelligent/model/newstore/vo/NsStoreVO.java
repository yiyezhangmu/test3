package com.coolcollege.intelligent.model.newstore.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 新店列表VO
 * @author wxp
 * @date 2022-03-08 13:41
 */
@Data
public class NsStoreVO {

    @ApiModelProperty("新店id")
    private Long id;

    @Excel(name = "门店名称" ,orderNum = "1")
    @ApiModelProperty("门店名称")
    private String name;

    @ApiModelProperty("区域id")
    private Long regionId;

    @Excel(name = "所属区域" ,orderNum = "2")
    @ApiModelProperty("所属区域")
    private String regionName;

    @Excel(name = "门店类型" ,orderNum = "3")
    @ApiModelProperty("新店类型")
    private String type;

    @Excel(name = "GPS定位" ,orderNum = "4")
    @ApiModelProperty("定位地址")
    private String locationAddress;

    @Excel(name = "联系人" ,orderNum = "5")
    @ApiModelProperty("联系人姓名")
    private String contactName;

    @Excel(name = "联系电话" ,orderNum = "6")
    @ApiModelProperty("联系人电话")
    private String contactPhone;

    @Excel(name = "门头照" ,orderNum = "7")
    @ApiModelProperty("门头照")
    private String avatar;

    @Excel(name = "状态" ,orderNum = "8", replace = {"进行中_ongoing","完成_completed","失败_failed"})
    @ApiModelProperty("新店状态：ongoing(进行中),completed(完成),failed(失败)")
    private String status;

    @Excel(name = "成交进度（最近一次拜访）" ,orderNum = "9", suffix = "%")
    @ApiModelProperty("成交进度")
    private Long progress;

    @Excel(name = "拜访时间（最近一次拜访）" ,orderNum = "10", format = "yyyy.MM.dd HH:mm")
    @ApiModelProperty("拜访时间")
    private Date visitTime;

    @Excel(name = "拜访次数" ,orderNum = "11")
    @ApiModelProperty("拜访次数")
    private Integer visitNum;

    @ApiModelProperty("创建日期")
    private Date createDate;

    @Excel(name = "创建时间" ,orderNum = "13", format = "yyyy.MM.dd HH:mm")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @Excel(name = "创建人" ,orderNum = "12")
    @ApiModelProperty("创建人姓名")
    private String createUserName;

    @ApiModelProperty("负责人")
    private String directUserId;

    @Excel(name = "负责人" ,orderNum = "14")
    @ApiModelProperty("负责人姓名")
    private String directUserName;

    @ApiModelProperty("经度")
    private String longitude;
    @ApiModelProperty("纬度")
    private String latitude;

}
