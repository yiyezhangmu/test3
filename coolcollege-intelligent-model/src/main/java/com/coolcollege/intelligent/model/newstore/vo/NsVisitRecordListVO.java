package com.coolcollege.intelligent.model.newstore.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 拜访记录列表VO
 * @author zhangnan
 * @date 2022-03-08 9:32
 */
@Data
public class NsVisitRecordListVO {

    @ApiModelProperty("拜访记录id")
    private Long id;

    @ApiModelProperty("拜访表id")
    private Long metaTableId;

    @Excel(name = "拜访表名称", orderNum = "5")
    @ApiModelProperty("拜访表名称")
    private String metaTableName;

    @ApiModelProperty("拜访状态：ongoing进行中，completed完成")
    private String status;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人姓名")
    private String createUserName;

    @Excel(name = "拜访定位时间", orderNum = "9", format = "yyyy.MM.dd HH:mm")
    @ApiModelProperty("签到时间")
    private Date signInTime;

    @Excel(name = "拜访定位", orderNum = "8")
    @ApiModelProperty("签到地址")
    private String signInAddress;

    @ApiModelProperty("签到经纬度")
    private String signInLongitudeLatitude;

    @Excel(name = "成交进度", orderNum = "6", suffix = "%")
    @ApiModelProperty("成交进度")
    private Long progress;

    @Excel(name = "拜访提交时间", orderNum = "10", format = "yyyy.MM.dd HH:mm")
    @ApiModelProperty("完成时间/提交时间")
    private Date completedTime;

    @ApiModelProperty("区域id")
    private Long regionId;

    @Excel(name = "所属区域", orderNum = "2")
    @ApiModelProperty("所属区域")
    private String regionName;

    @ApiModelProperty("门店id")
    private Long newStoreId;

    @Excel(name = "门店名称", orderNum = "1")
    @ApiModelProperty("门店名称")
    private String newStoreName;

    @Excel(name = "门店类型", orderNum = "3")
    @ApiModelProperty("新店类型")
    private String newStoreType;

    @Excel(name = "门店地址", orderNum = "4")
    @ApiModelProperty("新店定位")
    private String newStoreLocationAddress;

    @ApiModelProperty("新店经纬度")
    private String newStoreLongitudeLatitude;

    @ApiModelProperty("更新人id")
    private String updateUserId;

    @Excel(name = "拜访人", orderNum = "7")
    @ApiModelProperty("更新人姓名")
    private String updateUserName;
}
