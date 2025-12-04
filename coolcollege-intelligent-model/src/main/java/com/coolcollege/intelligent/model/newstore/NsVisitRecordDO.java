package com.coolcollege.intelligent.model.newstore;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 新店拜访记录
 * @author   zhangnan
 * @date   2022-03-04 04:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NsVisitRecordDO implements Serializable {
    @ApiModelProperty("")
    private Long id;

    @ApiModelProperty("")
    private Boolean deleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建日期")
    private Date createDate;

    @ApiModelProperty("创建人")
    private String createUserId;

    @ApiModelProperty("创建人姓名")
    private String createUserName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("更新人姓名")
    private String updateUserName;

    @ApiModelProperty("新店创建日期")
    private Date newStoreCreateDate;

    @ApiModelProperty("区域id")
    private Long regionId;

    @ApiModelProperty("区域路径")
    private String regionPath;

    @ApiModelProperty("拜访状态：ongoing进行中，completed完成")
    private String status;

    @ApiModelProperty("拜访表提交状态，0未提交，1已提交，默认0")
    private Integer dataTableStatus;

    @ApiModelProperty("新店id")
    private Long newStoreId;

    @ApiModelProperty("新店类型")
    private String newStoreType;

    @ApiModelProperty("新店状态：ongoing(进行中),completed(完成),failed(失败)")
    private String newStoreStatus;

    @ApiModelProperty("新店名称")
    private String newStoreName;

    @ApiModelProperty("新店定位")
    private String newStoreLocationAddress;

    @ApiModelProperty("新店经纬度")
    private String newStoreLongitudeLatitude;

    @ApiModelProperty("签到时间")
    private Date signInTime;

    @ApiModelProperty("签到状态：1正常，2异常")
    private Integer signInStatus;

    @ApiModelProperty("签到地址")
    private String signInAddress;

    @ApiModelProperty("签到经纬度")
    private String signInLongitudeLatitude;

    @ApiModelProperty("完成时间")
    private Date completedTime;

    @ApiModelProperty("检查表id")
    private Long metaTableId;

    @ApiModelProperty("成交进度")
    private Long progress;
}