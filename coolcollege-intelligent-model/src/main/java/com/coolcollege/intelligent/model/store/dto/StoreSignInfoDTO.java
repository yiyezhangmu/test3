package com.coolcollege.intelligent.model.store.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-05-18 02:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSignInfoDTO implements Serializable {

    @ApiModelProperty("自增id")
    private Long id;

    @ApiModelProperty("签到日期")
    private Date signDate;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("巡店人id")
    private String supervisorId;

    @ApiModelProperty("巡店人姓名")
    private String supervisorName;

    @ApiModelProperty("签到时间")
    private Date signStartTime;

    @ApiModelProperty("签退时间")
    private Date signEndTime;

    @ApiModelProperty("签到地址")
    private String signStartAddress;

    @ApiModelProperty("签退地址")
    private String signEndAddress;

    @ApiModelProperty("签到定位经纬度")
    private String startLongitudeLatitude;

    @ApiModelProperty("签退定位经纬度")
    private String endLongitudeLatitude;

    @ApiModelProperty("签到状态 1正常 2异常")
    private Integer signInStatus;

    @ApiModelProperty("签退状态 1正常 2异常")
    private Integer signOutStatus;

    @ApiModelProperty("签到备注信息")
    private String signStartRemark;

    @ApiModelProperty("签退备注信息")
    private String signEndRemark;


    @ApiModelProperty("签到图片")
    private String signInPicture;

    @ApiModelProperty("签退图片")
    private String signOutPicture;

    @ApiModelProperty("签到视频")
    private String signInVideo;

    @ApiModelProperty("签退视频")
    private String signOutVideo;

    @ApiModelProperty("门店编号")
    private String storeNum;
}